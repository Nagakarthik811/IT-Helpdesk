package com.helpdesk.service;

import com.helpdesk.dto.CommentRequest;
import com.helpdesk.dto.StatusUpdateRequest;
import com.helpdesk.dto.TicketRequest;
import com.helpdesk.dto.TicketResponse;
import com.helpdesk.entity.*;
import com.helpdesk.exception.BadRequestException;
import com.helpdesk.exception.ResourceNotFoundException;
import com.helpdesk.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketCommentRepository ticketCommentRepository;
    private final AttachmentRepository attachmentRepository;
    private final TicketHistoryRepository ticketHistoryRepository;

    public Page<TicketResponse> getTicketsForCurrentUser(Pageable pageable) {
        User currentUser = getCurrentUser();
        return ticketRepository.findByCreatedBy(currentUser, pageable).map(this::toResponse);
    }

    public Page<TicketResponse> getAllTickets(Pageable pageable) {
        return ticketRepository.findAll(pageable).map(this::toResponse);
    }

    public TicketResponse getTicketById(Long id) {
        return toResponse(findTicketById(id));
    }

    public TicketResponse createTicket(TicketRequest request) {
        User currentUser = getCurrentUser();
        String ticketNumber = generateTicketNumber();

        Ticket ticket = Ticket.builder()
                .ticketNumber(ticketNumber)
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .priority(request.getPriority())
                .status(TicketStatus.OPEN)
                .createdBy(currentUser)
                .build();

        Ticket savedTicket = ticketRepository.save(ticket);
        createHistory(savedTicket, currentUser, null, TicketStatus.OPEN, "Ticket created");
        return toResponse(savedTicket);
    }

    public TicketResponse assignTicket(Long ticketId, Long supportUserId) {
        Ticket ticket = findTicketById(ticketId);
        User supportUser = userRepository.findById(supportUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Support user not found"));
        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new BadRequestException("Closed tickets cannot be reassigned");
        }
        ticket.setAssignedTo(supportUser);
        if (ticket.getStatus() == TicketStatus.OPEN) {
            ticket.setStatus(TicketStatus.ASSIGNED);
            createHistory(ticket, supportUser, TicketStatus.OPEN, TicketStatus.ASSIGNED, "Assigned to support user");
        } else {
            createHistory(ticket, supportUser, ticket.getStatus(), ticket.getStatus(), "Ticket reassigned");
        }
        return toResponse(ticketRepository.save(ticket));
    }

    public TicketResponse updateStatus(Long ticketId, StatusUpdateRequest request) {
        Ticket ticket = findTicketById(ticketId);
        User currentUser = getCurrentUser();
        TicketStatus oldStatus = ticket.getStatus();
        TicketStatus newStatus = request.getStatus();

        validateStatusTransition(oldStatus, newStatus, ticket);
        ticket.setStatus(newStatus);

        if (newStatus == TicketStatus.RESOLVED) {
            if (request.getComment() == null || request.getComment().isBlank()) {
                throw new BadRequestException("Resolution details are required before resolving a ticket");
            }
            ticket.setResolution(request.getComment());
            ticket.setResolvedAt(LocalDateTime.now());
        }
        if (newStatus == TicketStatus.CLOSED) {
            ticket.setClosedAt(LocalDateTime.now());
        }

        createHistory(ticket, currentUser, oldStatus, newStatus, request.getComment() == null ? "Status changed" : request.getComment());
        return toResponse(ticketRepository.save(ticket));
    }

    public TicketResponse addComment(Long ticketId, CommentRequest request) {
        Ticket ticket = findTicketById(ticketId);
        User currentUser = getCurrentUser();
        TicketComment comment = TicketComment.builder()
                .ticket(ticket)
                .user(currentUser)
                .comment(request.getComment())
                .build();
        ticketCommentRepository.save(comment);
        createHistory(ticket, currentUser, ticket.getStatus(), ticket.getStatus(), "Comment added: " + request.getComment());
        return toResponse(ticket);
    }

    public List<TicketComment> getComments(Long ticketId) {
        Ticket ticket = findTicketById(ticketId);
        return ticketCommentRepository.findByTicketOrderByCreatedAtAsc(ticket);
    }

    public Page<TicketResponse> searchAndFilter(String ticketNumber, String title, TicketStatus status, TicketPriority priority,
                                           TicketCategory category, Long assignedUserId, Pageable pageable) {
        Specification<Ticket> specification = Specification.where(null);

        if (ticketNumber != null && !ticketNumber.isBlank()) {
            specification = specification.and((root, query, cb) -> cb.like(root.get("ticketNumber"), "%" + ticketNumber + "%"));
        }
        if (title != null && !title.isBlank()) {
            specification = specification.and((root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        }
        if (status != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (priority != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("priority"), priority));
        }
        if (category != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("category"), category));
        }
        if (assignedUserId != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("assignedTo").get("id"), assignedUserId));
        }

        return ticketRepository.findAll(specification, pageable).map(this::toResponse);
    }

    public List<TicketResponse> getRecentTickets() {
        return ticketRepository.findTop10ByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
    }

    public List<TicketResponse> getHighPriorityTickets() {
        List<Ticket> tickets = ticketRepository.findAll().stream()
                .filter(t -> t.getPriority() == TicketPriority.HIGH || t.getPriority() == TicketPriority.CRITICAL)
                .toList();
        return tickets.stream().map(this::toResponse).toList();
    }

    public List<TicketResponse> getUnassignedTickets() {
        return ticketRepository.findUnassignedTickets().stream().map(this::toResponse).toList();
    }

    public List<TicketResponse> getAssignedTicketsForCurrentUser() {
        User current = getCurrentUser();
        return ticketRepository.findByAssignedTo(current, Pageable.unpaged()).stream().map(this::toResponse).toList();
    }

    public List<TicketResponse> getMyTickets() {
        User current = getCurrentUser();
        return ticketRepository.findByCreatedBy(current, Pageable.unpaged()).stream().map(this::toResponse).toList();
    }

    public void validateStatusTransition(TicketStatus oldStatus, TicketStatus newStatus, Ticket ticket) {
        if (oldStatus == newStatus) {
            return;
        }
        if (oldStatus == TicketStatus.OPEN && newStatus == TicketStatus.CLOSED) {
            throw new BadRequestException("A ticket cannot move directly from OPEN to CLOSED");
        }
        if (oldStatus == TicketStatus.OPEN && newStatus == TicketStatus.RESOLVED) {
            throw new BadRequestException("Ticket must be assigned and in progress before resolution");
        }
        if (oldStatus == TicketStatus.ASSIGNED && newStatus == TicketStatus.CLOSED) {
            throw new BadRequestException("A ticket must be resolved before closure");
        }
        if (oldStatus == TicketStatus.CLOSED && newStatus != TicketStatus.CLOSED) {
            throw new BadRequestException("Closed tickets are not editable");
        }
    }

    public String generateTicketNumber() {
        long count = ticketRepository.count();
        return String.format("TKT-%d-%05d", LocalDateTime.now().getYear(), count + 1);
    }

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            username = principal.toString();
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    public Ticket findTicketById(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));
    }

    private void createHistory(Ticket ticket, User actor, TicketStatus oldStatus, TicketStatus newStatus, String comment) {
        TicketHistory ticketHistory = TicketHistory.builder()
                .ticket(ticket)
                .changedBy(actor)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .comment(comment)
                .build();
        ticketHistoryRepository.save(ticketHistory);
    }

    private TicketResponse toResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .category(ticket.getCategory())
                .priority(ticket.getPriority())
                .status(ticket.getStatus())
                .createdByUsername(ticket.getCreatedBy() != null ? ticket.getCreatedBy().getUsername() : null)
                .assignedToUsername(ticket.getAssignedTo() != null ? ticket.getAssignedTo().getUsername() : null)
                .resolution(ticket.getResolution())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .resolvedAt(ticket.getResolvedAt())
                .closedAt(ticket.getClosedAt())
                .build();
    }
}

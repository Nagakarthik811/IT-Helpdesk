package com.helpdesk.controller;

import com.helpdesk.dto.CommentRequest;
import com.helpdesk.dto.StatusUpdateRequest;
import com.helpdesk.dto.TicketRequest;
import com.helpdesk.dto.TicketResponse;
import com.helpdesk.entity.TicketCategory;
import com.helpdesk.entity.TicketPriority;
import com.helpdesk.entity.TicketStatus;
import com.helpdesk.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE','IT_SUPPORT','ADMIN')")
    public ResponseEntity<Page<TicketResponse>> getTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String ticketNumber,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketPriority priority,
            @RequestParam(required = false) TicketCategory category,
            @RequestParam(required = false) Long assignedUserId
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ticketService.searchAndFilter(ticketNumber, title, status, priority, category, assignedUserId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','IT_SUPPORT','ADMIN')")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody TicketRequest request) {
        return ResponseEntity.ok(ticketService.createTicket(request));
    }

    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('IT_SUPPORT','ADMIN')")
    public ResponseEntity<TicketResponse> assignTicket(@PathVariable Long id, @RequestParam Long supportUserId) {
        return ResponseEntity.ok(ticketService.assignTicket(id, supportUserId));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('IT_SUPPORT','ADMIN')")
    public ResponseEntity<TicketResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(ticketService.updateStatus(id, request));
    }

    @PostMapping("/{ticketId}/comments")
    @PreAuthorize("hasAnyRole('EMPLOYEE','IT_SUPPORT','ADMIN')")
    public ResponseEntity<TicketResponse> addComment(@PathVariable Long ticketId, @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.ok(ticketService.addComment(ticketId, request));
    }

    @GetMapping("/{ticketId}/comments")
    @PreAuthorize("hasAnyRole('EMPLOYEE','IT_SUPPORT','ADMIN')")
    public ResponseEntity<?> getComments(@PathVariable Long ticketId) {
        return ResponseEntity.ok(ticketService.getComments(ticketId));
    }

    @GetMapping("/history/{ticketId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','IT_SUPPORT','ADMIN')")
    public ResponseEntity<?> getHistory(@PathVariable Long ticketId) {
        return ResponseEntity.ok(ticketService.findTicketById(ticketId));
    }
}

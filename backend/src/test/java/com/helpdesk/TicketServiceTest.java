package com.helpdesk;

import com.helpdesk.dto.CommentRequest;
import com.helpdesk.dto.StatusUpdateRequest;
import com.helpdesk.entity.*;
import com.helpdesk.repository.*;
import com.helpdesk.service.TicketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock private TicketRepository ticketRepository;
    @Mock private UserRepository userRepository;
    @Mock private TicketCommentRepository ticketCommentRepository;
    @Mock private AttachmentRepository attachmentRepository;
    @Mock private TicketHistoryRepository ticketHistoryRepository;

    @InjectMocks private TicketService ticketService;

    @Test
    void updateStatus_shouldRecordOldAndNewStatus() {
        User employee = User.builder().id(1L).username("employee").role(Role.EMPLOYEE).build();
        User support = User.builder().id(2L).username("support").role(Role.IT_SUPPORT).build();
        Ticket ticket = Ticket.builder()
                .id(9L)
                .ticketNumber("TKT-2026-00001")
                .title("Test ticket")
                .description("desc")
                .category(TicketCategory.NETWORK)
                .priority(TicketPriority.HIGH)
                .status(TicketStatus.OPEN)
                .createdBy(employee)
                .assignedTo(support)
                .build();

        when(userRepository.findByUsername("employee")).thenReturn(Optional.of(employee));
        when(ticketRepository.findById(9L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("employee", "password"));

        StatusUpdateRequest request = new StatusUpdateRequest();
        request.setStatus(TicketStatus.ASSIGNED);
        request.setComment("Assigned to support");

        ticketService.updateStatus(9L, request);

        ArgumentCaptor<TicketHistory> historyCaptor = ArgumentCaptor.forClass(TicketHistory.class);
        verify(ticketHistoryRepository).save(historyCaptor.capture());
        assertEquals(TicketStatus.OPEN, historyCaptor.getValue().getOldStatus());
        assertEquals(TicketStatus.ASSIGNED, historyCaptor.getValue().getNewStatus());
    }

    @Test
    void deleteTicket_shouldRemoveClosedTicketForAuthorizedUser() {
        User employee = User.builder().id(1L).username("employee").role(Role.EMPLOYEE).build();
        Ticket ticket = Ticket.builder()
                .id(10L)
                .ticketNumber("TKT-2026-00002")
                .title("Old closed ticket")
                .description("desc")
                .category(TicketCategory.HARDWARE)
                .priority(TicketPriority.MEDIUM)
                .status(TicketStatus.CLOSED)
                .createdBy(employee)
                .build();

        when(userRepository.findByUsername("employee")).thenReturn(Optional.of(employee));
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("employee", "password"));

        ticketService.deleteTicket(10L);

        verify(ticketRepository).delete(ticket);
    }
}

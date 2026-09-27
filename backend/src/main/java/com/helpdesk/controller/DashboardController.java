package com.helpdesk.controller;

import com.helpdesk.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final TicketService ticketService;

    @GetMapping("/employee")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> employeeDashboard() {
        return ResponseEntity.ok(Map.of(
                "myTickets", ticketService.getMyTickets(),
                "recentTickets", ticketService.getRecentTickets()
        ));
    }

    @GetMapping("/support")
    @PreAuthorize("hasRole('IT_SUPPORT')")
    public ResponseEntity<Map<String, Object>> supportDashboard() {
        return ResponseEntity.ok(Map.of(
                "totalTickets", ticketService.getAllTickets(org.springframework.data.domain.Pageable.unpaged()).getContent(),
                "recentTickets", ticketService.getRecentTickets(),
                "highPriorityTickets", ticketService.getHighPriorityTickets(),
                "unassignedTickets", ticketService.getUnassignedTickets(),
                "myAssignedTickets", ticketService.getAssignedTicketsForCurrentUser()
        ));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> adminDashboard() {
        return ResponseEntity.ok(Map.of(
                "totalTickets", ticketService.getAllTickets(org.springframework.data.domain.Pageable.unpaged()).getContent(),
                "recentTickets", ticketService.getRecentTickets()
        ));
    }
}

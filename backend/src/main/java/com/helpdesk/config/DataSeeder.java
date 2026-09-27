package com.helpdesk.config;

import com.helpdesk.entity.*;
import com.helpdesk.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final TicketCommentRepository ticketCommentRepository;
    private final TicketHistoryRepository ticketHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User admin = userRepository.save(User.builder()
                .firstName("Admin")
                .lastName("User")
                .email("admin@helpdesk.com")
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .employeeId("ADM-001")
                .department("IT")
                .role(Role.ADMIN)
                .active(true)
                .build());

        User support = userRepository.save(User.builder()
                .firstName("Support")
                .lastName("User")
                .email("support@helpdesk.com")
                .username("support")
                .password(passwordEncoder.encode("support123"))
                .employeeId("SUP-001")
                .department("IT Support")
                .role(Role.IT_SUPPORT)
                .active(true)
                .build());

        User employee = userRepository.save(User.builder()
                .firstName("Employee")
                .lastName("User")
                .email("employee@helpdesk.com")
                .username("employee")
                .password(passwordEncoder.encode("employee123"))
                .employeeId("EMP-001")
                .department("Operations")
                .role(Role.EMPLOYEE)
                .active(true)
                .build());

        Ticket t1 = ticketRepository.save(Ticket.builder()
                .ticketNumber("TKT-2026-00001")
                .title("Laptop cannot connect to WiFi")
                .description("Employee laptop fails to connect to office WiFi after login.")
                .category(TicketCategory.NETWORK)
                .priority(TicketPriority.HIGH)
                .status(TicketStatus.OPEN)
                .createdBy(employee)
                .build());

        Ticket t2 = ticketRepository.save(Ticket.builder()
                .ticketNumber("TKT-2026-00002")
                .title("Access request for ERP system")
                .description("Need access to finance ERP modules for the new role.")
                .category(TicketCategory.ACCESS)
                .priority(TicketPriority.MEDIUM)
                .status(TicketStatus.ASSIGNED)
                .createdBy(employee)
                .assignedTo(support)
                .build());

        Ticket t3 = ticketRepository.save(Ticket.builder()
                .ticketNumber("TKT-2026-00003")
                .title("Printer not responding")
                .description("HR office printer is stuck and not accepting print jobs.")
                .category(TicketCategory.PRINTER)
                .priority(TicketPriority.CRITICAL)
                .status(TicketStatus.IN_PROGRESS)
                .createdBy(employee)
                .assignedTo(support)
                .build());

        ticketHistoryRepository.save(TicketHistory.builder().ticket(t1).changedBy(employee).oldStatus(null).newStatus(TicketStatus.OPEN).comment("Ticket created").changedAt(LocalDateTime.now()).build());
        ticketHistoryRepository.save(TicketHistory.builder().ticket(t2).changedBy(support).oldStatus(TicketStatus.OPEN).newStatus(TicketStatus.ASSIGNED).comment("Assigned to support").changedAt(LocalDateTime.now()).build());
        ticketHistoryRepository.save(TicketHistory.builder().ticket(t3).changedBy(support).oldStatus(TicketStatus.ASSIGNED).newStatus(TicketStatus.IN_PROGRESS).comment("Support started work").changedAt(LocalDateTime.now()).build());

        ticketCommentRepository.save(TicketComment.builder().ticket(t1).user(employee).comment("My laptop cannot find the correct network.").build());
        ticketCommentRepository.save(TicketComment.builder().ticket(t1).user(support).comment("Please reboot the device and try again.").build());
    }
}

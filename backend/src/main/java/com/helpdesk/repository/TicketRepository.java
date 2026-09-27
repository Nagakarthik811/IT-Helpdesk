package com.helpdesk.repository;

import com.helpdesk.entity.Ticket;
import com.helpdesk.entity.TicketPriority;
import com.helpdesk.entity.TicketStatus;
import com.helpdesk.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {
    Page<Ticket> findByCreatedBy(User user, Pageable pageable);
    Page<Ticket> findByAssignedTo(User user, Pageable pageable);
    List<Ticket> findTop10ByOrderByCreatedAtDesc();
    long countByStatus(TicketStatus status);
    long countByPriority(TicketPriority priority);
    long countByAssignedTo(User user);
    @Query("SELECT t FROM Ticket t WHERE t.assignedTo IS NULL")
    List<Ticket> findUnassignedTickets();
    boolean existsByTicketNumber(String ticketNumber);
}

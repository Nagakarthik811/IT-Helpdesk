package com.helpdesk.dto;

import com.helpdesk.entity.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusUpdateRequest {
    @NotNull
    private TicketStatus status;
    private String comment;
}

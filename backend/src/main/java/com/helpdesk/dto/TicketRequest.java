package com.helpdesk.dto;

import com.helpdesk.entity.TicketCategory;
import com.helpdesk.entity.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private TicketCategory category;

    @NotNull
    private TicketPriority priority;
}

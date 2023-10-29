package com.edstem.contract.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskUpdateRequest {
    private String title;
    private String description;
    private Long assigneeId;
    private String status;
}

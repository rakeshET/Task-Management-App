package com.edstem.contract.request;

import lombok.Data;

@Data
public class TaskRequest {
    private String title;
    private String description;
    private Long assigneeId;
    private String status;
}

package com.edstem.contract.response;

import lombok.Getter;

@Getter
public class TaskUpdateResponse {
    private Long id;
    private String title;
    private String description;
    private Long assigneeId;
    private String status;
}

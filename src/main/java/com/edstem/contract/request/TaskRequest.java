package com.edstem.contract.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskRequest {
    private String title;
    private String description;
    private Long assigneeId;
    private String status;
}

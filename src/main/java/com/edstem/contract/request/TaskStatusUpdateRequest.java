package com.edstem.contract.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskStatusUpdateRequest {
    private String status;
}

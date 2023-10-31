package com.edstem.contract.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskStatusUpdateRequest {
    private String status;
}

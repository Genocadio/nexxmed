package com.nexxserve.medadmin.dto.sync;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncResponse<T> {
    private UUID syncSessionId;
    private String status;
    private List<T> data;
    private Long totalItems;
    private Boolean isComplete;
}
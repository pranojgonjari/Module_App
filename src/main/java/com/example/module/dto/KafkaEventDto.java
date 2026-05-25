package com.example.module.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaEventDto {

    private String eventType;
    private String moduleName;
    private String entityId;
    private Object data;
    private LocalDateTime timestamp;

}

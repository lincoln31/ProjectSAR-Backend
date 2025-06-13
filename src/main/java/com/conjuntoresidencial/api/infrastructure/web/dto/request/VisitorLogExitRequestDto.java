package com.conjuntoresidencial.api.infrastructure.web.dto.request;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VisitorLogExitRequestDto {
    @NotNull(message = "Exit timestamp cannot be null")
    private LocalDateTime exitTimestamp;
}
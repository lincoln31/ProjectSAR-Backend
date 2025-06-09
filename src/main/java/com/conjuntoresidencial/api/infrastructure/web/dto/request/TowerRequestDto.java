package com.conjuntoresidencial.api.infrastructure.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TowerRequestDto {
    @NotBlank(message = "Tower name cannot be blank")
    @Size(max = 100, message = "Tower name must be less than 100 characters")
    private String name;
}
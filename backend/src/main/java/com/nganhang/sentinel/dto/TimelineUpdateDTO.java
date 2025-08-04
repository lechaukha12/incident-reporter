package com.nganhang.sentinel.dto;

import com.nganhang.sentinel.model.IncidentUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimelineUpdateDTO {

    @NotBlank(message = "Nội dung cập nhật không được để trống")
    private String message;
    
    @NotNull(message = "Loại cập nhật không được để trống")
    private IncidentUpdate.UpdateType updateType;
    
    @NotBlank(message = "Người cập nhật không được để trống")
    private String createdBy;
}

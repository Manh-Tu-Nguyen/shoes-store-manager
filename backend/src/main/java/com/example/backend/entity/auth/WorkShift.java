package com.example.backend.entity.auth;

import com.example.backend.entity.baseEntity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "work_shift")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkShift extends BaseEntity {

    @NotBlank(message = "Tên ca làm việc không được để trống")
    @Size(max = 100, message = "Tên ca làm việc không vượt quá 100 ký tự")
    @Column(name = "name", nullable = false, columnDefinition = "NVARCHAR(100)")
    private String name;

    @NotNull(message = "Giờ bắt đầu không được để trống")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull(message = "Giờ kết thúc không được để trống")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
}
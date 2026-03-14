package com.revworkforce.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "default_quota", nullable = false)
    @Builder.Default
    private Integer defaultQuota = 0;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    @Builder.Default
    private Boolean isActive = true;
}

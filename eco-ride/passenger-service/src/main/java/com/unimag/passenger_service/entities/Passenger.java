package com.unimag.passenger_service.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("passengers")
public class Passenger {

    @Id
    private String id;

    @Column("keycloak_sub")
    private String keycloakSub;

    private String name;

    private String email;

    @Column("rating_avg")
    private Double ratingAvg;

    @Column("created_at")
    private LocalDateTime createdAt;
}
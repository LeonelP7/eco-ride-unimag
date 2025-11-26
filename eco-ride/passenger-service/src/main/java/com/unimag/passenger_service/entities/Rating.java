package com.unimag.passenger_service.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("ratings")
public class Rating {

    @Id
    private String id;

    @Column("trip_id")
    private String tripId;

    @Column("from_id")
    private String fromId;  // ← ID del que califica (Passenger)

    @Column("to_id")
    private String toId;    // ← ID del calificado (Passenger)

    private Integer score;

    private String comment;
}
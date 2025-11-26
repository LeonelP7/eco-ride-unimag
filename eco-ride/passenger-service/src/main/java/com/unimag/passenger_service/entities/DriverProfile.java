package com.unimag.passenger_service.entities;

import com.unimag.passenger_service.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("driver_profiles")
public class DriverProfile {

    @Id
    private String id;

    @Column("passenger_id")
    private String passengerId;

    @Column("license_no")
    private String licenseNo;

    @Column("car_plate")
    private String carPlate;

    @Column("seats_offered")
    private Integer seatsOffered;

    @Column("verification_status")
    private VerificationStatus verificationStatus;
}
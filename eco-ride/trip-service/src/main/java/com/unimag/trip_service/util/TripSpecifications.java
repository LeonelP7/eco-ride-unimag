package com.unimag.trip_service.util;

import com.unimag.trip_service.entities.Trip;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class TripSpecifications {

    public static Specification<Trip> hasOrigin(String origin) {
        return (root, query, cb) -> {
            if (origin == null || origin.isBlank()) {
                return cb.conjunction(); // siempre true
            }
            return cb.equal(root.get("origin"), origin);
        };
    }

    public static Specification<Trip> hasDestination(String destination) {
        return (root, query, cb) -> {
            if (destination == null || destination.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("destination"), destination);
        };
    }

    public static Specification<Trip> startsInRange(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            if (from == null && to == null) {
                return cb.conjunction();
            }

            if (from != null && to == null) {
                return cb.greaterThanOrEqualTo(root.get("startTime"), from);
            }

            if (from == null && to != null) {
                return cb.lessThanOrEqualTo(root.get("startTime"), to);
            }

            return cb.between(root.get("startTime"), from, to);
        };
    }
}

package com.unimag.apigateway.predicates;

import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalTime;
import java.util.function.Predicate;

@Component
public class BusinessHoursRoutePredicateFactory extends AbstractRoutePredicateFactory<BusinessHoursRoutePredicateFactory.Config> {


    public BusinessHoursRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return exchange -> {
            LocalTime now = LocalTime.now();
            LocalTime start = LocalTime.parse(config.getStartTime());
            LocalTime end = LocalTime.parse(config.getEndTime());

            return !now.isBefore(start) && !now.isAfter(end);
        };
    }

    public static class Config {
        private String startTime = "06:00";
        private String endTime = "23:00";

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
    }
}

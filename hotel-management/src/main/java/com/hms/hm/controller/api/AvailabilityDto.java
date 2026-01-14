package com.hms.hm.controller.api;

import java.time.LocalDate;
import java.util.UUID;

public record AvailabilityDto(
        UUID rqUid,
        LocalDate startDate,
        LocalDate endDate
) {
}

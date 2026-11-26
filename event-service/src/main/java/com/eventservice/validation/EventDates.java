package com.eventservice.validation;

import java.time.LocalDateTime;

public interface EventDates {
    LocalDateTime startsAt();

    LocalDateTime endsAt();
}

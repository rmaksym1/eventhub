package com.eventservice.validation;

import com.eventservice.dto.event.CreateEventRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EventDatesValidator implements ConstraintValidator<ValidEventDates, CreateEventRequest> {

    @Override
    public boolean isValid(
            CreateEventRequest request,
            ConstraintValidatorContext context
    ) {
        if (request.startsAt() == null || request.endsAt() == null) {
            return true;
        }

        return request.endsAt().isAfter(request.startsAt());
    }
}
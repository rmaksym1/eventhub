package com.eventservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EventDatesValidator implements ConstraintValidator<ValidEventDates, EventDates> {

    @Override
    public boolean isValid(
            EventDates request,
            ConstraintValidatorContext context
    ) {
        if (request.startsAt() == null || request.endsAt() == null) {
            return true;
        }

        return request.endsAt().isAfter(request.startsAt());
    }
}
package com.eventservice.mapper;

import com.eventservice.config.MapperConfig;
import com.eventservice.dto.event.CreateEventRequest;
import com.eventservice.dto.event.EventResponse;
import com.eventservice.dto.event.UpdateEventRequest;
import com.eventservice.entity.event.Event;
import org.mapstruct.*;

@Mapper(config = MapperConfig.class)
public interface EventMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "venue", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Event toEntity(CreateEventRequest request);

    @Mapping(target = "venueId", source = "venue.id")
    @Mapping(target = "venueName", source = "venue.name")
    EventResponse toDto(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "venue", ignore = true)
    @Mapping(target = "organizerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEvent(UpdateEventRequest updateRequest, @MappingTarget Event event);
}

package com.eventservice.mapper;

import com.eventservice.config.MapperConfig;
import com.eventservice.dto.venue.VenueRequest;
import com.eventservice.dto.venue.VenueResponse;
import com.eventservice.entity.Venue;
import org.mapstruct.*;

@Mapper(config = MapperConfig.class)
public interface VenueMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sections", ignore = true)
    Venue toEntity(VenueRequest request);

    VenueResponse toDto(Venue venue);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sections", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateVenue(VenueRequest updateRequest, @MappingTarget Venue venue);
}

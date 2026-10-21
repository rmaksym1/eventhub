package com.eventservice.mapper;

import com.eventservice.config.MapperConfig;
import com.eventservice.dto.section.SectionRequest;
import com.eventservice.dto.section.SectionResponse;
import com.eventservice.entity.Section;
import org.mapstruct.*;

@Mapper(config = MapperConfig.class)
public interface SectionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "venue", ignore = true)
    @Mapping(target = "seats", ignore = true)
    Section toEntity(SectionRequest request);

    @Mapping(target = "venueId", source = "venue.id")
    @Mapping(target = "venueName", source = "venue.name")
    SectionResponse toDto(Section section);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "venue", ignore = true)
    @Mapping(target = "seats", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSection(SectionRequest updateRequest, @MappingTarget Section section);
}

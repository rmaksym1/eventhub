package com.eventservice.mapper;

import com.eventservice.config.MapperConfig;
import com.eventservice.dto.seat.SeatRequest;
import com.eventservice.dto.seat.SeatResponse;
import com.eventservice.entity.Seat;
import org.mapstruct.*;

@Mapper(config = MapperConfig.class)
public interface SeatMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "section", ignore = true)
    Seat toEntity(SeatRequest request);

    @Mapping(target = "sectionId", source = "section.id")
    @Mapping(target = "sectionName", source = "section.name")
    SeatResponse toDto(Seat seat);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "section", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSeat(SeatRequest updateRequest, @MappingTarget Seat seat);
}

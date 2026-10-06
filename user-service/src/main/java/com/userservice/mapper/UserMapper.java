package com.userservice.mapper;

import com.userservice.config.MapperConfig;
import com.userservice.dto.user.CreateUserRequest;
import com.userservice.dto.user.PublicUserResponse;
import com.userservice.dto.user.UserResponse;
import com.userservice.dto.user.UserUpdateRequest;
import com.userservice.entity.user.User;
import org.mapstruct.*;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User toEntity(CreateUserRequest request);

    UserResponse toDto(User user);

    PublicUserResponse toPublicResponse(User user);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(UserUpdateRequest updateRequest, @MappingTarget User user);
}

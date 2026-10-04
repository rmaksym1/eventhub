package com.userservice.mapper;

import com.userservice.config.MapperConfig;
import com.userservice.dto.user.CreateUserRequest;
import com.userservice.dto.user.UserResponse;
import com.userservice.entity.user.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User toEntity(CreateUserRequest request);

    UserResponse toDto(User user);
}

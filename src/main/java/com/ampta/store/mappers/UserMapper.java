package com.ampta.store.mappers;

import com.ampta.store.dtos.RegisterUserRequest;
import com.ampta.store.dtos.UpdateUserRequest;
import com.ampta.store.dtos.UserDto;
import com.ampta.store.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(RegisterUserRequest request);
    void update(UpdateUserRequest request, @MappingTarget User user);
}

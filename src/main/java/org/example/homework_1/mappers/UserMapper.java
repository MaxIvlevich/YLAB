package org.example.homework_1.mappers;

import org.example.homework_1.dto.UserDTO;
import org.example.homework_1.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    User toEntity(UserDTO dto);
    UserDTO toDTO(User user);
}


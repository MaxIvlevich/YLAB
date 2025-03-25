package org.example.homework_1.mappers;

import org.example.homework_1.dto.GoalDTO;
import org.example.homework_1.models.Goal;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GoalMapper {
    GoalMapper INSTANCE = Mappers.getMapper(GoalMapper.class);
    Goal toEntity(GoalDTO dto);
    GoalDTO toDTO(Goal goal);
}

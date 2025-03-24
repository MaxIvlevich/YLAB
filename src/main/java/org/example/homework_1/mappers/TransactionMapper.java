package org.example.homework_1.mappers;

import org.example.homework_1.dto.TransactionDTO;
import org.example.homework_1.models.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);
    Transaction toEntity(TransactionDTO dto);
    TransactionDTO toDTO(Transaction transaction);
}

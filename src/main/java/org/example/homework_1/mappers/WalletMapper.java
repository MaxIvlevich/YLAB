package org.example.homework_1.mappers;

import org.example.homework_1.dto.WalletDTO;
import org.example.homework_1.models.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
@Mapper
public interface WalletMapper {
    WalletMapper INSTANCE = Mappers.getMapper(WalletMapper.class);
    Wallet toEntity(WalletDTO dto);
    WalletDTO toDTO(Wallet wallet);
}

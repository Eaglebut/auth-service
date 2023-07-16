package ru.eaglebut.authservice.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import ru.eaglebut.authservice.dto.UserDto;
import ru.eaglebut.authservice.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
	UserDto map(UserEntity userEntity);

	@InheritInverseConfiguration
	UserEntity map(UserDto userDto);
}

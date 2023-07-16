package ru.eaglebut.authservice.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.LowerCaseStrategy.class)
public class AuthRequestDto {
	private String username;
	private String password;
}

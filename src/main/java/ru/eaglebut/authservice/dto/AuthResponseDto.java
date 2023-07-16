package ru.eaglebut.authservice.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@JsonNaming(PropertyNamingStrategies.LowerCaseStrategy.class)
public class AuthResponseDto {
	private UUID userId;
	private String token;
	private Date issuedAt;
	private Date expiresAt;
}

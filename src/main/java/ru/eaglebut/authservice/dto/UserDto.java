package ru.eaglebut.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import ru.eaglebut.authservice.entity.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonNaming(PropertyNamingStrategies.LowerCaseStrategy.class)
public class UserDto {
	private UUID id;
	private String username;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	private UserRole role;
	private String firstName;
	private String lastName;
	private boolean enabled;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

}

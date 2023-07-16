package ru.eaglebut.authservice.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.eaglebut.authservice.dto.AuthRequestDto;
import ru.eaglebut.authservice.dto.AuthResponseDto;
import ru.eaglebut.authservice.dto.UserDto;
import ru.eaglebut.authservice.entity.UserEntity;
import ru.eaglebut.authservice.mapper.UserMapper;
import ru.eaglebut.authservice.security.CustomPrincipal;
import ru.eaglebut.authservice.security.SecurityService;
import ru.eaglebut.authservice.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthRestController {

	private final SecurityService securityService;
	private final UserService userService;
	private final UserMapper userMapper;

	@PostMapping("/register")
	public Mono<UserDto> register(@RequestBody UserDto dto) {
		UserEntity entity = userMapper.map(dto);
		return userService.registerUser(entity).map(userMapper::map);
	}


	@PostMapping("/login")
	public Mono<AuthResponseDto> login(@RequestBody AuthRequestDto dto) {
		return securityService.authenticate(dto.getUsername(), dto.getPassword())
			.flatMap(tokenDetails -> Mono.just(
				AuthResponseDto.builder()
					.userId(tokenDetails.getUserID())
					.token(tokenDetails.getToken())
					.issuedAt(tokenDetails.getIssuedAt())
					.expiresAt(tokenDetails.getExpiresAt())
					.build()));
	}

	@GetMapping("/info")
	public Mono<UserDto> getUserInfo(Authentication authentication) {
		CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
		return userService.getUserById(principal.getId()).map(userMapper::map);
	}

}

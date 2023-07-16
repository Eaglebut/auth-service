package ru.eaglebut.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.eaglebut.authservice.entity.UserEntity;
import ru.eaglebut.authservice.entity.UserRole;
import ru.eaglebut.authservice.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public Mono<UserEntity> registerUser(UserEntity user) {
		return userRepository.save(
				user.toBuilder()
					.password(passwordEncoder.encode(user.getPassword()))
					.role(UserRole.USER)
					.enabled(true)
					.createdAt(LocalDateTime.now())
					.updatedAt(LocalDateTime.now())
					.build())
			.doOnSuccess(userEntity ->
				log.info("Registered user - user {}", userEntity)
			);
	}

	public Mono<UserEntity> getUserById(UUID id) {
		return userRepository.findById(id);
	}

	public Mono<UserEntity> getUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}

}

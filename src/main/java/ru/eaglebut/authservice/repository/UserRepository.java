package ru.eaglebut.authservice.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;
import ru.eaglebut.authservice.entity.UserEntity;

import java.util.UUID;

public interface UserRepository extends R2dbcRepository<UserEntity, UUID> {
	Mono<UserEntity> findByUsername(String username);
}

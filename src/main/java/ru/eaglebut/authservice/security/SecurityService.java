package ru.eaglebut.authservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.eaglebut.authservice.entity.UserEntity;
import ru.eaglebut.authservice.exception.AuthException;
import ru.eaglebut.authservice.service.UserService;

import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityService {

	private final UserService userService;
	private final PasswordEncoder passwordEncoder;

	@Value("${jwt.secret}")
	private String secret;
	@Value("${jwt.expiration}")
	private int expirationInSeconds;
	@Value("${jwt.issuer}")
	private String issuer;

	private TokenDetails generateToken(UserEntity user) {
		Map<String, Object> claims = Map.of(
			"role", user.getRole(),
			"username", user.getUsername()
		);
		return generateToken(claims, user.getId().toString());
	}

	private TokenDetails generateToken(Map<String, Object> claims, String subject) {
		long expirationTimeInMillis = expirationInSeconds * 1000L;
		Date expirationDate = new Date(new Date().getTime() + expirationTimeInMillis);
		return generateToken(expirationDate, claims, subject);
	}

	private TokenDetails generateToken(Date expirationDate, Map<String, Object> claims, String subject) {
		Date createdDate = new Date();
		String token = Jwts.builder()
			.setClaims(claims)
			.setIssuer(issuer)
			.setSubject(subject)
			.setIssuedAt(createdDate)
			.setId(UUID.randomUUID().toString())
			.setExpiration(expirationDate)
			.signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(secret.getBytes()))
			.compact();
		return TokenDetails.builder()
			.token(token)
			.issuedAt(createdDate)
			.expiresAt(expirationDate)
			.build();
	}

	public Mono<TokenDetails> authenticate(String username, String password) {
		return userService.getUserByUsername(username)
			.flatMap(user -> {
				if (!user.isEnabled()) {
					return Mono.error(new AuthException("Account disabled", "AUTH_SERVICE_ACCOUNT_DISABLED"));
				}
				if (!passwordEncoder.matches(password, user.getPassword())) {
					return Mono.error(new AuthException("Invalid password", "AUTH_SERVICE_INVALID_PASSWORD"));
				}
				return Mono.just(generateToken(user).toBuilder().userID(user.getId()).build());
			})
			.switchIfEmpty(Mono.error(new AuthException("Invalid username", "AUTH_SERVICE_INVALID_USERNAME")));
	}
}

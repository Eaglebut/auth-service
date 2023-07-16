package ru.eaglebut.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import ru.eaglebut.authservice.exception.AuthException;
import ru.eaglebut.authservice.exception.UnauthorizedException;

import java.util.Base64;
import java.util.Date;

@RequiredArgsConstructor
public class JwtHandler {
	private final String secret;

	public Mono<VerificationResult> check(String accessToken) {
		return Mono.just(verify(accessToken)).onErrorResume(e -> Mono.error(new UnauthorizedException(e.getMessage())));
	}

	private VerificationResult verify(String token) {
		Claims claims = getClaimsFromToken(token);
		final Date expirationDate = claims.getExpiration();
		if (expirationDate.before(new Date())) {
			throw new AuthException("Token expired", "AUTH_SERVICE_TOKEN_EXPIRED");
		}
		return new VerificationResult(claims, token);
	}

	private Claims getClaimsFromToken(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(Base64.getEncoder().encodeToString(secret.getBytes()))
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	public record VerificationResult(Claims claims, String token) {
	}
}

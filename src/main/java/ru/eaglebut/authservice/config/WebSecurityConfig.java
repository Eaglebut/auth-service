package ru.eaglebut.authservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;
import ru.eaglebut.authservice.security.AuthenticationManager;
import ru.eaglebut.authservice.security.BearerTokenServerAuthenticationConverter;
import ru.eaglebut.authservice.security.JwtHandler;

@Configuration
@EnableReactiveMethodSecurity
@Slf4j
public class WebSecurityConfig {

	private final String[] publicRoutes = {"/api/v1/auth/register", "/api/v1/auth/login", "/swagger-ui/**",
		"/v3/api-docs/**", "/swagger-ui.html", "/webjars/swagger-ui/**", "/actuator/**"};
	@Value("${jwt.secret}")
	private String secret;

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
														 AuthenticationManager authenticationManager) {
		return http
			.csrf(ServerHttpSecurity.CsrfSpec::disable)
			.authorizeExchange(authorizeExchangeSpec ->
				authorizeExchangeSpec.pathMatchers(HttpMethod.OPTIONS)
					.permitAll()
					.pathMatchers(publicRoutes)
					.permitAll()
					.anyExchange()
					.authenticated())
			.exceptionHandling(exceptionHandlingSpec ->
				exceptionHandlingSpec
					.authenticationEntryPoint((exchange, ex) -> {
						log.error("IN securityWebFilterChain - unauthorized error: {}", ex.getMessage());
						return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
					})
					.accessDeniedHandler((exchange, ex) -> {
						log.error("IN securityWebFilterChain - access denied: {}", ex.getMessage());
						return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
					}))
			.addFilterAt(bearerAuthenticationFilter(authenticationManager), SecurityWebFiltersOrder.AUTHENTICATION)
			.build();
	}

	private AuthenticationWebFilter bearerAuthenticationFilter(AuthenticationManager authenticationManager) {
		AuthenticationWebFilter bearerAuthenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
		bearerAuthenticationWebFilter.setServerAuthenticationConverter(
			new BearerTokenServerAuthenticationConverter(new JwtHandler(secret))
		);
		bearerAuthenticationWebFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));
		return bearerAuthenticationWebFilter;
	}

}

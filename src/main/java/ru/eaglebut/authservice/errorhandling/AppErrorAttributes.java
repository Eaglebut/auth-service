package ru.eaglebut.authservice.errorhandling;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.NoArgsConstructor;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import ru.eaglebut.authservice.exception.ApiException;
import ru.eaglebut.authservice.exception.AuthException;
import ru.eaglebut.authservice.exception.UnauthorizedException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@NoArgsConstructor
public class AppErrorAttributes extends DefaultErrorAttributes {

	@Override
	public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
		var errorAttributes = super.getErrorAttributes(request, ErrorAttributeOptions.defaults());
		var error = getError(request);
		if (isUnauthorizedError(error)) {
			var errorCode = error instanceof ApiException apiError ? apiError.getErrorCode() : "";
			return handleError(errorAttributes, HttpStatus.UNAUTHORIZED, error.getMessage(), errorCode);
		}
		if (error instanceof ApiException apiError) {
			return handleError(errorAttributes, HttpStatus.BAD_REQUEST, error.getMessage(), apiError.getErrorCode());
		}
		String message = Optional.ofNullable(error.getMessage()).orElseGet(() -> error.getClass().getName());
		return handleError(errorAttributes, HttpStatus.INTERNAL_SERVER_ERROR, message, "INTERNAL_ERROR");
	}

	private Map<String, Object> handleError(Map<String, Object> errorAttributes,
											HttpStatus status,
											String message,
											String errorCode) {
		var errorMap = new LinkedHashMap<String, Object>();
		errorMap.put("message", message);
		errorMap.put("code", errorCode);
		errorAttributes.put("errors", Map.of("errors", List.of(errorMap)));
		errorAttributes.put("status", status.value());
		return errorAttributes;
	}

	private boolean isUnauthorizedError(Throwable error) {
		return error instanceof AuthException
			|| error instanceof UnauthorizedException
			|| error instanceof ExpiredJwtException
			|| error instanceof SignatureException
			|| error instanceof MalformedJwtException;
	}
}
package ru.eaglebut.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends ApiException {
	public UnauthorizedException(String message) {
		super(message, "USER_SERVICE_UNAUTHORISED");
	}

}

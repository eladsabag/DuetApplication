package iob.logic.customExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class UnauthorizedRequestException extends RuntimeException {

	private static final long serialVersionUID = -4175511148518938575L;

	public UnauthorizedRequestException() {
	}

	public UnauthorizedRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnauthorizedRequestException(String message) {
		super(message);
	}

	public UnauthorizedRequestException(Throwable cause) {
		super(cause);
	}
}
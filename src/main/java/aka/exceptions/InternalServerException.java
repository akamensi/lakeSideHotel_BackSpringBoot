package aka.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InternalServerException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
    public InternalServerException(String message) {
        super(message);
    }

}
package app.foot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class NotAllowedException extends ApiException{

    public NotAllowedException(String message){
        super(HttpStatus.METHOD_NOT_ALLOWED.value(),  message);
    }

}

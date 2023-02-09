package app.foot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class NotFoundException extends ApiException{

   public NotFoundException(String message){
       super(HttpStatus.NOT_FOUND.value(),  message);
   }

}

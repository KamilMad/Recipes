package recipes.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import recipes.payload.ApiError;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class RecipesErrorHandler {

    @ExceptionHandler(RecipeNotFoundException.class)
    public ResponseEntity<RuntimeException> handleContentNotAllowedException(RecipeNotFoundException ex) {

        return new ResponseEntity<>(new RuntimeException(ex.getMessage()), HttpStatus.NOT_FOUND);
        //return new ResponseEntity<>(new ApiError(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RuntimeException> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        return new ResponseEntity<>(new RuntimeException(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<RuntimeException> UsernameNotFoundException(UsernameNotFoundException ex) {
        return new ResponseEntity<>(new RuntimeException(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RuntimeException> handleAccessDeniedException(AccessDeniedException ex) {
        return new ResponseEntity<>(new RuntimeException(ex.getMessage()), HttpStatus.FORBIDDEN);
    }

}

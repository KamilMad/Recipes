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
    public ResponseEntity<ApiError> handleContentNotAllowedException(RecipeNotFoundException ex) {

        return new ResponseEntity<>(new ApiError(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        return new ResponseEntity<>(new ApiError("Validacja jebła"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> UsernameNotFoundException(UsernameNotFoundException ex) {
        return new ResponseEntity<>(new ApiError(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex) {
        // return the error response with a 403 Forbidden status code
        return new ResponseEntity<>(new ApiError("PreAuthorize error"), HttpStatus.FORBIDDEN);
    }

}

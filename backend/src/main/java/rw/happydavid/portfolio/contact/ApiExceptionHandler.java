package rw.happydavid.portfolio.contact;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> invalid(MethodArgumentNotValidException ignored) {
        return ResponseEntity.badRequest().body(new ApiResponse("Please check the name, email, and message fields."));
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiResponse> unreadable(HttpMessageNotReadableException ignored) {
        return ResponseEntity.badRequest().body(new ApiResponse("The request could not be read."));
    }
    @ExceptionHandler(MailException.class)
    ResponseEntity<ApiResponse> mailFailure(MailException error) {
        log.warn("Contact email could not be accepted by the configured mail server ({})", error.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ApiResponse("The mail service is temporarily unavailable. Please try again later."));
    }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse> unexpected(Exception error) {
        log.error("Unexpected error while processing a contact request ({})", error.getClass().getSimpleName());
        return ResponseEntity.internalServerError().body(new ApiResponse("Something went wrong. Please try again later."));
    }
}

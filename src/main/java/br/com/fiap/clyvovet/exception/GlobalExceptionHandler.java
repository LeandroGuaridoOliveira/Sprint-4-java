package br.com.fiap.clyvovet.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private boolean isBrowserHtmlRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri != null && uri.startsWith("/api/")) {
            return false;
        }
        String accept = request.getHeader("Accept");
        return accept != null && accept.contains("text/html");
    }

    private Object buildResponse(HttpServletRequest request, HttpServletResponse response, HttpStatus status, String error, String message, Map<String, ?> extra) {
        if (isBrowserHtmlRequest(request)) {
            response.setStatus(status.value());
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("status", status.value());
            mav.addObject("error", error);
            mav.addObject("message", message);
            if (extra != null) {
                mav.addAllObjects(extra);
            }
            return mav;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        if (extra != null) {
            body.putAll(extra);
        }
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleNotFound(ResourceNotFoundException ex, HttpServletRequest request, HttpServletResponse response) {
        return buildResponse(request, response, HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), null);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public Object handleRegraDeNegocio(RegraDeNegocioException ex, HttpServletRequest request, HttpServletResponse response) {
        return buildResponse(request, response, HttpStatus.BAD_REQUEST, "Regra de negócio violada", ex.getMessage(), null);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public Object handlePropertyReference(PropertyReferenceException ex, HttpServletRequest request, HttpServletResponse response) {
        return buildResponse(request, response, HttpStatus.BAD_REQUEST, "Requisição inválida",
                "Propriedade de ordenação inválida: '" + ex.getPropertyName() + "'", null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> fieldErrors.put(e.getField(), e.getDefaultMessage()));
        return buildResponse(request, response, HttpStatus.BAD_REQUEST, "Erro de validação", "Dados inválidos", Map.of("campos", fieldErrors));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Object handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request, HttpServletResponse response) {
        return buildResponse(request, response, HttpStatus.METHOD_NOT_ALLOWED, "Método não permitido", ex.getMessage(), null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request, HttpServletResponse response) {
        return buildResponse(request, response, HttpStatus.BAD_REQUEST, "Parâmetro inválido", ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public Object handleGeneral(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        return buildResponse(request, response, HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor", ex.getMessage(), null);
    }
}

package com.ferreteria.clientes.exception;

import lombok.Getter;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private Map<String, String> details;
}

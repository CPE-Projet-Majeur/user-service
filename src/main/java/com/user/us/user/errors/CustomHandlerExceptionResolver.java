package com.user.us.user.errors;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class CustomHandlerExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Vérifie si l'exception est une InvalidFormatException
        if (ex instanceof InvalidFormatException) {
            InvalidFormatException invalidFormatException = (InvalidFormatException) ex;

            if (invalidFormatException.getTargetType().isEnum()) {
                // Traite les erreurs de type Enum
                String fieldName = invalidFormatException.getPath().get(0).getFieldName();
                String invalidValue = invalidFormatException.getValue().toString();
                String expectedValues = Arrays.stream(invalidFormatException.getTargetType().getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));

                String message = String.format(
                        "Valeur invalide pour le champ '%s': '%s'. Les valeurs acceptées sont : %s.",
                        fieldName, invalidValue, expectedValues);

                // Renvoyer une réponse JSON personnalisée
                writeErrorResponse(response, HttpStatus.BAD_REQUEST, "Invalid Enum Value", message);
            }
        }

        // Pour les autres exceptions, laissez-les non traitées
        return null;
    }

    private void writeErrorResponse(HttpServletResponse response, HttpStatus status, String error, String message) {
        try {
            response.setStatus(status.value());
            response.setContentType("application/json");
            response.getWriter().write(String.format("{\"error\": \"%s\", \"message\": \"%s\"}", error, message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


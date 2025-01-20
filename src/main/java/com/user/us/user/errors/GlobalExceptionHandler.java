package com.user.us.user.errors;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.user.us.user.common.tools.ReflectionUtils;
import com.user.us.user.model.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.util.ClassUtil.getRootCause;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoleNotFoundException(RoleNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) // Ou un autre statut approprié
                .body(new ErrorResponse("Role Not Found", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalStateException(IllegalStateException ex) {
        String detailMessage = ex.getMessage();

        return ResponseEntity.status(HttpStatus.CONFLICT) // 409 Conflict
                .body(new ErrorResponse("Conflict", detailMessage));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String detailMessage = ex.getMessage();
        return ResponseEntity.status(HttpStatus.CONFLICT) // 409 Conflict
                .body(new ErrorResponse("Conflict", "Une contrainte d'intégrité a été violée : " + detailMessage));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleInvalidFormatException(HttpMessageNotReadableException ex) {
        Throwable rootCause = getRootCause(ex);

        if (rootCause instanceof InvalidFormatException) {
            InvalidFormatException invalidFormatException = (InvalidFormatException) rootCause;

            // Accéder aux champs privés (cause -> _value et _targetType) de InvalidFormatException
            Object targetType = ReflectionUtils.getPrivateFieldRecursively(invalidFormatException, "_targetType");
            Object value = ReflectionUtils.getPrivateFieldRecursively(invalidFormatException, "_value");

            if (targetType instanceof Class && ((Class<?>) targetType).isEnum()) {
                String fieldName = invalidFormatException.getPath().get(0).getFieldName();
                String invalidValue = value != null ? value.toString() : "null";
                String expectedValues = Arrays.stream(((Class<?>) targetType).getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));

                String message = String.format(
                        "Valeur invalide pour le champ '%s': '%s'. Les valeurs acceptées sont : %s.",
                        fieldName, invalidValue, expectedValues);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Invalid Enum Value", message));
            }
        }
        // Réponse générique
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("JSON Parse Error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidTokenException(InvalidTokenException ex) {
        // Créer une réponse personnalisée
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Unauthorized");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.UNAUTHORIZED.value());

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // Gestion des exceptions d'accès refusé
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Access Denied");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.FORBIDDEN.value());

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

//    // Gestion de l'exception InvalidFormatException
//    // Ne fonctionne pas l'exception par jackson va directement dans internal error 500
//    @ExceptionHandler(InvalidFormatException.class)
//    public ResponseEntity<ErrorResponse> handleInvalidFormatException(InvalidFormatException ex) {
//        if (ex.getTargetType().isEnum()) { // Vérifie si l'exception concerne un Enum
//            String fieldName = ex.getPath().get(0).getFieldName();
//            String invalidValue = ex.getValue().toString();
//            String expectedValues = Arrays.stream(ex.getTargetType().getEnumConstants())
//                    .map(Object::toString) // Convertit chaque valeur en String
//                    .collect(Collectors.joining(", "));
//
//            String message = String.format(
//                    "Valeur invalide pour le champ '%s': '%s'. Les valeurs acceptées sont : %s.",
//                    fieldName, invalidValue, expectedValues);
//
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(new ErrorResponse("Invalid Enum Value", message));
//        }
//
//        // Si ce n'est pas un Enum, renvoyer une erreur générique
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(new ErrorResponse("JSON Parse Error", ex.getMessage()));
//    }

    // Default message for internal error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        System.out.println(ex.getMessage());

        System.out.println(ex.getCause());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500 Internal Server Error
                .body(new ErrorResponse("Internal Server Error", ex.getMessage()));
    }

    // Méthode pour extraire le détail pertinent qui ne fonctionne pas
//    private String extractDetailMessage(String errorMessage) {
//        // Expression régulière pour trouver la partie "Détail : ..."
//        Pattern pattern = Pattern.compile("Détail\\s*:\\s*(.*)");
//        Matcher matcher = pattern.matcher(errorMessage);
//        if (matcher.find()) {
//            return matcher.group(1).trim(); // Retourne la partie correspondante
//        }
//        System.out.println(errorMessage);
//        return errorMessage; // Aucun détail trouvé
//    }

    // detailed message with stack
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Object> handleGeneralException(Exception ex) {
//        StringWriter sw = new StringWriter();
//        PrintWriter pw = new PrintWriter(sw);
//        ex.printStackTrace(pw); // Écrit la pile d'appels dans un StringWriter
//
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(new ErrorResponse("Internal Server Error", sw.toString())); // Inclut la pile d'appels
//    }

}


package com.user.us.user.errors;

import com.user.us.user.model.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // Default message for internal error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
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


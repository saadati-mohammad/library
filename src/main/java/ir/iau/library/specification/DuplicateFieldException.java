package ir.iau.library.specification;

import lombok.Getter;

@Getter
public class DuplicateFieldException extends RuntimeException {
    // Getters
    private String fieldName;
    private String fieldValue;

    public DuplicateFieldException(String fieldName, String fieldValue) {
        super("Duplicate entry for field: " + fieldName + " with value: " + fieldValue);
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

}
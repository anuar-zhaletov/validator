package com.creditsuisse.validator.responses;

import com.creditsuisse.validator.model.ErrorReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Response of Trades Validation Result
 */
public class ValidationResponse {
    private boolean valid;
    private List<ErrorReport> validationDetails = new ArrayList<>();

    public ValidationResponse(boolean valid) {
        this.valid = valid;
    }

    public ValidationResponse(boolean valid, List<ErrorReport> validationDetails) {
        this.valid = valid;
        this.validationDetails = validationDetails;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<ErrorReport> getValidationDetails() {
        return validationDetails;
    }

    public void setValidationDetails(List<ErrorReport> validationDetails) {
        this.validationDetails = validationDetails;
    }
}

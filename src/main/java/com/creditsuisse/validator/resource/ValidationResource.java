package com.creditsuisse.validator.resource;

import com.creditsuisse.validator.model.Trade;
import com.creditsuisse.validator.responses.ValidationResponse;
import com.creditsuisse.validator.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Resource, where user can validate his data. User will send List<Trade> which he want, that it will be validated.
 * As Output he receives trade
 */
@RestController
public class ValidationResource {

    @Autowired
    private ValidationService validationService;

    /**
     * Validate Trade data
     * @param trades data, which user want to be validated
     * @return response, with details of validation
     */
    @RequestMapping(value = "/", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    public ValidationResponse validate(@RequestBody List<Trade> trades) {
        return validationService.validate(trades);
    }
}

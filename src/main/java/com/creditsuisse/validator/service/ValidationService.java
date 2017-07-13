package com.creditsuisse.validator.service;

import com.creditsuisse.validator.model.Trade;
import com.creditsuisse.validator.responses.ValidationResponse;

import java.util.List;

/**
 * Service, which contains main logic of Trades Validation
 */
public interface ValidationService {

    /**
     * Validation of trades.
     * Note: every trade validated in parallelStream. It will work faster, because
     * on each validation it's calling 3rd-party application fixerFacade.
     * @param trades input data trades, which should be validated
     * @return response, with details of validation
     */
    ValidationResponse validate(List<Trade> trades);
}

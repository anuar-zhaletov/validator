package com.creditsuisse.validator.integration;

import java.time.LocalDate;

/**
 * Facade to calling 3rd party API
 */
public interface FixerFacade {

    /**
     * Identify, if input date is working day
     * @param date input date, which we should identify
     * @return true, if input date is working day
     */
    boolean isWorkingDay(LocalDate date);
}

package com.creditsuisse.validator.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * Facade to calling 3rd party API
 * Note: I'm not found normal API... also I can't find it in "http://fixer.io"
 *      so, I manually put all public holidays in property file.
 * I'm calling this method in parallel stream (but of course,
 *      we will have performance improvement only if we call 3rd party API)
 */
@Service
public class FixerFacadeImpl implements FixerFacade {

    @Value("#{'${holidays.usa}'.split(',')}")
    private List<String> holidays;

    /**
     * Identify, if input date is working day
     * @param date input date, which we should identify
     * @return true, if input date is working day
     */
    @Override
    public boolean isWorkingDay(LocalDate date) {
        return date.getDayOfWeek() != DayOfWeek.SATURDAY
                && date.getDayOfWeek() != DayOfWeek.SUNDAY
                && !isHoliday(date);
    }

    private boolean isHoliday(LocalDate date) {
        return holidays.contains(String.format("2017-%02d-%02d", date.getMonth().getValue(), date.getDayOfMonth()));
    }
}

package com.creditsuisse.validator.service;

import com.creditsuisse.validator.enums.MessageTextEnum;
import com.creditsuisse.validator.model.ErrorReport;
import com.creditsuisse.validator.model.Trade;
import com.creditsuisse.validator.responses.ValidationResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.creditsuisse.validator.enums.MessageTextEnum.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ValidationServiceTest {

    @Autowired
    private ValidationService validationService;

    @Test
    public void positiveValidationTest() {
        ValidationResponse validationResponse = validationService.validate(getValidTrades());
        assertTrue(validationResponse.isValid());
    }

    @Test
    public void negativeValidationValueDateAfterTradeDateTest() {
        Trade trade = getSpotValidTrade();
        trade.setValueDate(LocalDate.of(2016, 1, 1));
        trade.setTradeDate(LocalDate.of(2016, 1, 2));
        negativeTradeTest(trade, VALUE_DATE_BEFORE_TRADE_DATE);
    }

    @Test
    public void negativeValidationValueDateIsWorkingDayTest() {
        Trade trade = getSpotValidTrade();
        trade.setValueDate(LocalDate.of(2017, 1, 1));
        negativeTradeTest(trade, VALUE_DATE_IS_NOT_WORKING_DAY);
    }

    @Test
    public void negativeValidationCustomerIsSupportedTest() {
        Trade trade = getSpotValidTrade();
        trade.setCustomer("PLUTO3");
        negativeTradeTest(trade, CUSTOMER_IS_NOT_SUPPORTED);
    }

    @Test
    public void negativeValidationCcyPairCurrencyTest() {
        Trade trade = getSpotValidTrade();
        trade.setCcyPair("USD");
        negativeTradeTest(trade, CCYPAIR_IS_NOT_VALID);
        trade.setCcyPair("AAAAAA");
        negativeTradeTest(trade, CCYPAIR_IS_NOT_VALID);
    }

    @Test
    public void negativeValidationPayCcyCurrencyTest() {
        Trade trade = getSpotValidTrade();
        trade.setPayCcy("AAA");
        negativeTradeTest(trade, PAYCCY_IS_NOT_VALID);
    }

    @Test
    public void negativeValidationPremiumCcyCurrencyTest() {
        Trade trade = getSpotValidTrade();
        trade.setPayCcy("AAA");
        negativeTradeTest(trade, PREMIUMCCY_IS_NOT_VALID);
    }

    @Test
    public void negativeValidationValueDateTest() {
        Trade trade = getSpotValidTrade();
        trade.setValueDate(null);
        negativeTradeTest(trade, VALUE_DATE_IS_EMPTY);
    }

    @Test
    public void negativeValidationOptionNotValidStyleTest() {
        Trade trade = getOptionValidTrade();
        trade.setStyle("NOT_RECOGNIZED_STYLE");
        negativeTradeTest(trade, STYLE_IS_NOT_VALID_FOR_OPTION);
    }

    @Test
    public void negativeValidationAmericanExcerciesStartDateEmptyTest() {
        Trade trade = getOptionValidTrade();
        trade.setStyle("AMERICAN");
        trade.setExcerciseStartDate(null);
        negativeTradeTest(trade, EXCERCISE_START_DATE_IS_EMPTY);
    }

    @Test
    public void negativeValidationAmericanExcerciesStartDateBeforeTradeDateTest() {
        Trade trade = getOptionValidTrade();
        trade.setStyle("AMERICAN");
        trade.setExcerciseStartDate(LocalDate.of(2016, 1, 1));
        trade.setTradeDate(LocalDate.of(2016, 1, 2));
        negativeTradeTest(trade, EXCERCISE_START_DATE_IS_BEFORE_TRADE_DATE);
    }

    @Test
    public void negativeValidationAmericanExcerciesStartDateAfterExpiryDateTest() {
        Trade trade = getOptionValidTrade();
        trade.setStyle("AMERICAN");
        trade.setExcerciseStartDate(LocalDate.of(2016, 1, 2));
        trade.setTradeDate(LocalDate.of(2016, 1, 1));
        trade.setExpiryDate(LocalDate.of(2016, 1, 1));
        negativeTradeTest(trade, EXCERCISE_START_DATE_IS_AFTER_EXPIRY_DATE);
    }

    private void negativeTradeTest(Trade trade, MessageTextEnum message) {
        ValidationResponse validationResponse = validationService.validate(Collections.singletonList(trade));
        assertEquals(validationResponse.isValid(), false);
        List<ErrorReport> errorReports = validationResponse.getValidationDetails();
        assertEquals(errorReports.size(), 1);
        assertEquals(errorReports.get(0).getMessages().size(), 1);
        assertEquals(errorReports.get(0).getMessages().get(0), message);
    }

    private List<Trade> getValidTrades() {
        return Arrays.asList(getSpotValidTrade(), getOptionValidTrade());
    }
    
    private Trade getSpotValidTrade() {
        Trade trade = new Trade();
        trade.setCustomer("PLUTO1");
        trade.setCcyPair("EURUSD");
        trade.setType("Spot");
        trade.setDirection("BUY");
        trade.setTradeDate(LocalDate.of(2016, 8, 11));
        trade.setAmount1(BigDecimal.valueOf(1000000.00));
        trade.setAmount2(BigDecimal.valueOf(1120000.00));
        trade.setRate(1.12);
        trade.setValueDate(LocalDate.of(2016, 8, 15));
        trade.setLegalEntity("CS Zurich");
        trade.setTrader("Johann Baumfiddler");
        
        return trade;
    }

    private Trade getOptionValidTrade() {
        Trade trade = new Trade();
        trade.setCustomer("PLUTO1");
        trade.setCcyPair("EURUSD");
        trade.setType("VanillaOption");
        trade.setStyle("AMERICAN");
        trade.setDirection("BUY");
        trade.setStrategy("CALL");
        trade.setTradeDate(LocalDate.of(2016, 8, 11));
        trade.setAmount1(BigDecimal.valueOf(1000000.00));
        trade.setAmount2(BigDecimal.valueOf(1120000.00));
        trade.setRate(1.12);
        trade.setDeliveryDate(LocalDate.of(2016, 8, 22));
        trade.setExpiryDate(LocalDate.of(2016, 8, 19));
        trade.setExcerciseStartDate(LocalDate.of(2016, 8, 12));
        trade.setPayCcy("USD");
        trade.setPremium(0.20);
        trade.setPremiumCcy("USD");
        trade.setPremiumType("%USD");
        trade.setPremiumDate(LocalDate.of(2016, 8, 12));
        trade.setLegalEntity("CS Zurich");
        trade.setTrader("Johann Baumfiddler");

        return trade;
    }
}
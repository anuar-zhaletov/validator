package com.creditsuisse.validator.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * NOTE: it's better to configure internalization and i18n... and store messages in property files.
 *      But for current demo task.... I will put in enum.
 * Message Text, with explanation, why Trade data is not valid.
 */
public enum MessageTextEnum {

    VALUE_DATE_BEFORE_TRADE_DATE("Value date should be after Trade Date"),
    VALUE_DATE_IS_NOT_WORKING_DAY("Value date should be as working day"),
    CUSTOMER_IS_NOT_SUPPORTED("Customer is not supported. Should be some of 'PLUTO1' or 'PLUTO2'"),
    CCYPAIR_IS_NOT_VALID("CcyPair is not valid"),
    PAYCCY_IS_NOT_VALID("PayCcy is not valid according WIKI list of correct currencies"),
    PREMIUMCCY_IS_NOT_VALID("PremiumCcy is not valid according WIKI list of correct currencies"),
    VALUE_DATE_IS_EMPTY("Value Date should not be empty for Product Type = SPOT or FORWARD"),
    STYLE_IS_NOT_VALID_FOR_OPTION("Style is not valid. Should be AMERICAN or EUROPEAN"),
    EXCERCISE_START_DATE_IS_EMPTY("ExcerciseStartDate should not be empty, if style is American"),
    EXCERCISE_START_DATE_IS_BEFORE_TRADE_DATE("ExcerciseStartDate should be after trade date"),
    EXCERCISE_START_DATE_IS_AFTER_EXPIRY_DATE("ExcerciseStartDate should be before expiry date"),
    EXPIRY_DATE_IS_AFTER_DELIVERY_DATE("Expiry date should be before Delivery date, if Product Type contains 'OPTION'"),
    PREMIUM_DATE_IS_AFTER_DELIVERY_DATE("Premium date should be before Delivery date, if Product Type contains 'OPTION'"),
    LEGAL_ENTITY_IS_NOT_VALID("Legal Entity is not valid");


    private String text;

    MessageTextEnum(String text) {
        this.text = text;
    }

    @JsonValue
    public String getText() {
        return text;
    }
}

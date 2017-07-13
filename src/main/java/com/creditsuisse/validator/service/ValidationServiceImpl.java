package com.creditsuisse.validator.service;

import com.creditsuisse.validator.enums.MessageTextEnum;
import com.creditsuisse.validator.integration.FixerFacade;
import com.creditsuisse.validator.model.ErrorReport;
import com.creditsuisse.validator.model.Trade;
import com.creditsuisse.validator.responses.ValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.creditsuisse.validator.enums.MessageTextEnum.*;

/**
 * Service, which contains main logic of Trades Validation
 */
@Service
public class ValidationServiceImpl implements ValidationService {

    /**
     * Better to put this to enums. But I need more information about fields.
     */
    private static final String PRODUCT_TYPE_SPOT = "SPOT";
    private static final String PRODUCT_TYPE_FORWARD = "FORWARD";
    private static final String PRODUCT_TYPE_OPTION = "OPTION";
    private static final String STYLE_AMERICAN = "AMERICAN";
    private static final String STYLE_EUROPEAN = "EUROPEAN";


    @Value("#{'${valid.customers}'.split(',')}")
    private List<String> validCustomers;

    @Value("#{'${valid.currencies}'.split(',')}")
    private List<String> validCurrencies;

    @Value("#{'${valid.legalEntities}'.split(',')}")
    private List<String> legalEntites;

    @Autowired
    private FixerFacade fixerFacade;

    /**
     * Validation of trades.
     * Note: every trade validated in parallelStream. It will work faster, because
     * on each validation it's calling 3rd-party application fixerFacade.
     * @param trades input data trades, which should be validated
     * @return response, with details of validation
     */
    @Override
    public ValidationResponse validate(List<Trade> trades) {
        List<ErrorReport> validationDetails = trades.stream()
            .parallel()
            .map(this::validate)
            .filter(report -> null != report && report.getMessages().size() != 0)
            .collect(Collectors.toList());

        if(validationDetails.size() == 0) {
            return new ValidationResponse(true, new ArrayList<>());
        } else {
            return new ValidationResponse(false, validationDetails);
        }
    }

    /**
     * Validation of single trade.
     * @param trade data, which should be validated.
     * @return report, with explanation why this trade are not valid or empty report if it's valid
     */
    private ErrorReport validate(Trade trade) {
        List<MessageTextEnum> messages = new ArrayList<>();
        messages.add(validateValueDateAfterTradeDate(trade));
        messages.add(validateValueDateIsWorkingDay(trade));
        messages.add(validateCustomerIsSupported(trade));
        messages.add(validateCcyPairCurrency(trade));
        messages.add(validatePayCcyCurrency(trade));
        messages.add(validatePremiumCcyCurrency(trade));
        messages.add(validateLegalEntity(trade));


        if(null != trade.getType()) {
            if(trade.getType().equalsIgnoreCase(PRODUCT_TYPE_SPOT) || trade.getType().equalsIgnoreCase(PRODUCT_TYPE_FORWARD)) {
                messages.add(validateValueDate(trade));
            } else if (trade.getType().toUpperCase().contains(PRODUCT_TYPE_OPTION)) {
                messages.add(validateStyleAndExcerciseStartDate(trade));
                messages.add(validateExpiryPremiumDeliveryDate(trade));
            }
        }

        messages = messages.stream()
                .filter(message -> null != message)
                .collect(Collectors.toList());

        return new ErrorReport(trade, messages);
    }

    private MessageTextEnum validateValueDateAfterTradeDate(Trade trade) {
        if(null != trade.getValueDate() && null != trade.getTradeDate()
                && trade.getValueDate().isBefore(trade.getTradeDate())) {
            return VALUE_DATE_BEFORE_TRADE_DATE;
        }

        return null;
    }

    private MessageTextEnum validateValueDateIsWorkingDay(Trade trade) {
        if(null != trade.getValueDate() && !fixerFacade.isWorkingDay(trade.getValueDate())) {
            return VALUE_DATE_IS_NOT_WORKING_DAY;
        }

        return null;
    }

    private MessageTextEnum validateCustomerIsSupported(Trade trade) {
        if(null != trade.getCustomer() && !validCustomers.contains(trade.getCustomer())) {
            return CUSTOMER_IS_NOT_SUPPORTED;
        }

        return null;
    }

    private MessageTextEnum validateCcyPairCurrency(Trade trade) {
        if(null != trade.getCcyPair() && !(trade.getCcyPair().length() == 6
                && validCurrencies.contains(trade.getCcyPair().substring(0, 3))
                && validCurrencies.contains(trade.getCcyPair().substring(3, 6)))) {
            return CCYPAIR_IS_NOT_VALID;
        }

        return null;
    }

    private MessageTextEnum validatePayCcyCurrency(Trade trade) {
        if(null != trade.getPayCcy() && !validCurrencies.contains(trade.getPayCcy())) {
            return PAYCCY_IS_NOT_VALID;
        }

        return null;
    }

    private MessageTextEnum validatePremiumCcyCurrency(Trade trade) {
        if(null != trade.getPremiumCcy() && !validCurrencies.contains(trade.getPremiumCcy())) {
            return PREMIUMCCY_IS_NOT_VALID;
        }

        return null;
    }

    /**
     * Validate if valueDate is not empty.... in case of type is "SPOT" or "FORWARD"
     * @param trade data, which should be validated.
     * @return message, if trade is not valid
     */
    private MessageTextEnum validateValueDate(Trade trade) {
        if(null == trade.getValueDate()) {
            return VALUE_DATE_IS_EMPTY;
        }

        return null;
    }

    /**
     * Validate Style in case of type is "OPTION".
     *      If value = "AMERICAN" or "EUROPEAN", then valid.
     * Validate ExcerciseStartDate, in case of value = "AMERICAN"
     *      It should not be empty, and should be after tradeDate, but before ExpiryDate
     * @param trade data, which should be validated.
     * @return message, if trade is not valid
     */
    private MessageTextEnum validateStyleAndExcerciseStartDate(Trade trade) {
        if(null == trade.getStyle() || (!trade.getStyle().equalsIgnoreCase(STYLE_AMERICAN)
                && !trade.getStyle().equalsIgnoreCase(STYLE_EUROPEAN))) {
            return STYLE_IS_NOT_VALID_FOR_OPTION;
        }

        if(trade.getStyle().equalsIgnoreCase(STYLE_AMERICAN)) {
            if(null == trade.getExcerciseStartDate()) {
                return EXCERCISE_START_DATE_IS_EMPTY;
            }

            if(null != trade.getTradeDate() && null != trade.getExpiryDate()
                    && trade.getExcerciseStartDate().isBefore(trade.getTradeDate())) {
                return EXCERCISE_START_DATE_IS_BEFORE_TRADE_DATE;
            }

            if(null != trade.getTradeDate() && null != trade.getExpiryDate()
                    && trade.getExcerciseStartDate().isAfter(trade.getExpiryDate())) {
                return EXCERCISE_START_DATE_IS_AFTER_EXPIRY_DATE;
            }
        }

        return null;
    }

    private MessageTextEnum validateExpiryPremiumDeliveryDate(Trade trade) {
        if(null != trade.getDeliveryDate()) {
            if(null != trade.getExpiryDate() && trade.getExpiryDate().isAfter(trade.getDeliveryDate())) {
                return EXPIRY_DATE_IS_AFTER_DELIVERY_DATE;
            }

            if(null != trade.getPremiumDate() && trade.getPremiumDate().isAfter(trade.getDeliveryDate())) {
                return PREMIUM_DATE_IS_AFTER_DELIVERY_DATE;
            }
        }

        return null;
    }

    private MessageTextEnum validateLegalEntity(Trade trade) {
        if(null == trade.getLegalEntity() || !legalEntites.contains(trade.getLegalEntity())) {
            return LEGAL_ENTITY_IS_NOT_VALID;
        }

        return null;
    }
}

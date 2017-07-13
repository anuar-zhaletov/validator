package com.creditsuisse.validator.model;

import com.creditsuisse.validator.enums.MessageTextEnum;

import java.util.List;

/**
 * Report, which contains error messages with information why is Input Trade is not valid
 */
public class ErrorReport {
    private Trade trade;
    private List<MessageTextEnum> messages;

    public ErrorReport(Trade trade, List<MessageTextEnum> messages) {
        this.trade = trade;
        this.messages = messages;
    }

    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    public List<MessageTextEnum> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageTextEnum> messages) {
        this.messages = messages;
    }
}

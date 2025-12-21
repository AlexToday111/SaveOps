package com.saveops.common.event;

public final class EventConstants {
    public static final String EXCHANGE = "saveops.events";
    public static final String ACCOUNT_OPENED = "account.opened";
    public static final String ACCOUNT_CLOSED = "account.closed";
    public static final String MONEY_TRANSFERRED = "money.transferred";
    public static final String PURCHASE_ROUNDED_UP = "purchase.rounded-up";
    public static final String INTEREST_ACCRUED = "interest.accrued";
    public static final String NOTIFICATION_FAILED = "notification.failed";

    private EventConstants() {
    }
}


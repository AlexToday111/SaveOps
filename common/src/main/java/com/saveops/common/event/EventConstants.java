package com.saveops.common.event;

public final class EventConstants {
    public static final String EXCHANGE = "saveops.events";
    public static final String ACCOUNT_OPENED = "account.opened";
    public static final String ACCOUNT_CLOSED = "account.closed";
    public static final String MONEY_TRANSFERRED = "money.transferred";
    public static final String PURCHASE_ROUNDED_UP = "purchase.rounded-up";
    public static final String INTEREST_ACCRUED = "interest.accrued";
    public static final String NOTIFICATION_FAILED = "notification.failed";
    public static final String NOTIFICATION_QUEUE = "saveops.notification.events";
    public static final String NOTIFICATION_RETRY_QUEUE = "saveops.notification.retry";
    public static final String NOTIFICATION_DLQ = "saveops.notification.dlq";
    public static final String AUDIT_QUEUE = "saveops.audit.events";
    public static final String ACCOUNT_ROUND_UP_QUEUE = "saveops.account.round-up";
    public static final String RETRY_EXCHANGE = "saveops.notification.retry";
    public static final String DLX_EXCHANGE = "saveops.notification.dlx";

    private EventConstants() {
    }
}

package com.amigoscode.testing.payment;

public class SMSSent {
    private final boolean isSent;

    public SMSSent(boolean isSent) {
        this.isSent = isSent;
    }

    public boolean isSent() {
        return isSent;
    }

    @Override
    public String toString() {
        return "SMSSent{" +
                "isSent=" + isSent +
                '}';
    }
}

package com.example.testbluetooh;

public interface ConnectResult {
}

final class ConnectionSuccess implements ConnectResult {
    public static final ConnectionSuccess INSTANCE = new ConnectionSuccess();

    private ConnectionSuccess() {
    }
}

final class ConnectionError implements ConnectResult {
    private final String message;

    public ConnectionError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
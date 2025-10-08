package api.requests.skelethon;

public enum ErrorMessages {
    UNAUTHORIZED_ACCESS("Unauthorized access to account"),
    INVALID_ACCOUNT_OR_AMOUNT("Invalid account or amount");

    private final String message;

    ErrorMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

package UI.pages;

import lombok.Getter;

@Getter
public enum BankAlert {
    USER_CREATED_SUCCESSFULLY("✅ User created successfully!"),
    USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS("Username must be between 3 and 15 characters"),
    NEW_ACCOUNT_CREATES("✅ New Account Created! Account Number: "),
    DEPOSIT_LESS_OR_EQUAL("Please deposit less or equal to 5000$."),
    SELECT_ACCOUNT("Please select an account."),
    ENTER_VALID_AMOUNT("Please enter a valid amount."),
    INVALID_TRANSFER("Invalid transfer: insufficient funds or invalid accounts"),
    FILL_ALL_FIELDS("Please fill all fields and confirm."),
    NAME_UPDATED_SUCCESSFULLY("Name updated successfully!"),
    NAME_MUST_CONTAIN("Name must contain two words with letters only");

    private final String message;

    BankAlert(String message) {
        this.message = message;
    }
}

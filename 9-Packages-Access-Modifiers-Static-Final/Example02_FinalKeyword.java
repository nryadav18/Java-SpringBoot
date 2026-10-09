/*
 * Example02_FinalKeyword.java
 *
 * What this file demonstrates - the 3 places "final" can go:
 *   1. final VARIABLE  -> can be assigned only ONCE (local variable, parameter, field)
 *      - "blank final" field: assigned later, but exactly once, in the constructor
 *      - static final  -> a constant (UPPER_SNAKE_CASE)
 *      - a final REFERENCE cannot point to another object, BUT the object itself can still change!
 *   2. final METHOD    -> child classes can NOT override it
 *   3. final CLASS     -> nobody can extend it (String is a final class!)
 *
 * Domain: a bank account.
 *
 * How to run:
 *   java Example02_FinalKeyword.java
 *
 * Output:
 *   Bank: Java Bank, min balance: 1000.0
 *   Account SB-101 created for Kiran
 *   Interest for 5000.0 = 200.0
 *   Notes before: [Opened online]
 *   Notes after : [Opened online] [KYC done]
 *   Savings rule: Withdrawals must leave at least 1000.0
 *   Receipt: Paid 250.0 to Electricity Board
 */
public class Example02_FinalKeyword {

    // static final = constant: one copy, never changes. Use UPPER_SNAKE_CASE.
    static final String BANK_NAME = "Java Bank";
    static final double MIN_BALANCE = 1000.0;

    public static void main(String[] args) {
        System.out.println("Bank: " + BANK_NAME + ", min balance: " + MIN_BALANCE);
        // BANK_NAME = "Other Bank";   // compile error: cannot assign a value to final variable BANK_NAME

        // 1a. final local variable
        final double interestRate = 0.04;
        // interestRate = 0.05;        // compile error: cannot assign a value to final variable

        SavingsAccount acc = new SavingsAccount("SB-101", "Kiran");
        System.out.println("Account " + acc.getAccountNumber() + " created for " + acc.getOwner());
        System.out.println("Interest for 5000.0 = " + calculateInterest(5000.0, interestRate));

        // 1b. final REFERENCE: the variable cannot point to a NEW object...
        final StringBuilder notes = new StringBuilder("[Opened online]");
        System.out.println("Notes before: " + notes);
        // notes = new StringBuilder();  // compile error: cannot re-assign a final variable
        // ...but the object it points to CAN be changed:
        notes.append(" [KYC done]");
        System.out.println("Notes after : " + notes);

        // 2. final method (rule cannot be overridden)
        System.out.println("Savings rule: " + acc.withdrawalRule());

        // 3. final class
        Receipt r = new Receipt(250.0, "Electricity Board");
        System.out.println("Receipt: " + r.describe());
    }

    // 1c. final PARAMETERS: the method cannot accidentally change them.
    static double calculateInterest(final double amount, final double rate) {
        // amount = 0;   // compile error
        return amount * rate;
    }
}

class BankAccount {

    // "blank final" fields: not given a value here, but MUST be set exactly once in every constructor.
    private final String accountNumber;
    private final String owner;

    BankAccount(String accountNumber, String owner) {
        this.accountNumber = accountNumber;
        this.owner = owner;
    }

    String getAccountNumber() { return accountNumber; }
    String getOwner()         { return owner; }

    // 2. final method: the bank's rule must be the same for ALL account types.
    final String withdrawalRule() {
        return "Withdrawals must leave at least " + Example02_FinalKeyword.MIN_BALANCE;
    }
}

class SavingsAccount extends BankAccount {

    SavingsAccount(String accountNumber, String owner) {
        super(accountNumber, owner);
    }

    // Trying to override the final method would NOT compile:
    // @Override
    // String withdrawalRule() { return "No rules!"; }
    //  -> error: withdrawalRule() in SavingsAccount cannot override withdrawalRule() in BankAccount
    //            overridden method is final
}

// 3. final class: nobody can write "class FakeReceipt extends Receipt".
//    Useful for security/immutability. java.lang.String, Integer, etc. are final too.
final class Receipt {
    private final double amount;
    private final String paidTo;

    Receipt(double amount, String paidTo) {
        this.amount = amount;
        this.paidTo = paidTo;
    }

    String describe() {
        return "Paid " + amount + " to " + paidTo;
    }
}

// class FakeReceipt extends Receipt { }   // compile error: cannot inherit from final Receipt

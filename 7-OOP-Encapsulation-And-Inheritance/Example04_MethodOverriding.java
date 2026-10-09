/*
 * Example04_MethodOverriding.java
 * ------------------------------------------------------------
 * What this file shows (domain: Bank accounts):
 *   1. METHOD OVERRIDING: a child class gives its OWN version of a parent method
 *      (same name, same parameters, same return type).
 *   2. @Override: the compiler checks you really are overriding (catches typos).
 *   3. super.method(): re-use the parent's logic and add to it.
 *   4. The version that runs depends on the REAL object, even when the variable
 *      type is the parent (a sneak peek of runtime polymorphism, Topic 8).
 *   5. Overriding vs overloading.
 *
 * How to run (from this folder):
 *   java Example04_MethodOverriding.java
 */
public class Example04_MethodOverriding {

    public static void main(String[] args) {
        Account basic = new Account("A-1", 10_000);
        SavingsAccount savings = new SavingsAccount("S-1", 10_000);
        CurrentAccount current = new CurrentAccount("C-1", 10_000, 5_000);

        System.out.println("--- Interest for one year ---");
        System.out.println("Account        : " + basic.calculateInterest());
        System.out.println("SavingsAccount : " + savings.calculateInterest());
        System.out.println("CurrentAccount : " + current.calculateInterest());

        System.out.println();
        System.out.println("--- Withdraw 12,000 from each ---");
        basic.withdraw(12_000);
        savings.withdraw(12_000);
        current.withdraw(12_000);      // allowed thanks to the overdraft limit

        System.out.println();
        System.out.println("--- Parent-type variable, child object ---");
        Account someAccount = new SavingsAccount("S-2", 2_000);
        // The variable type is Account, but the OBJECT is a SavingsAccount,
        // so SavingsAccount's version runs:
        System.out.println(someAccount.describe());

        System.out.println();
        System.out.println("--- Overloading (same name, different parameters) ---");
        savings.deposit(500);
        savings.deposit(500, "Salary credit");
    }
}

class Account {
    protected String accountNumber;
    protected double balance;

    Account(String accountNumber, double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    double calculateInterest() {
        return 0;                                     // a plain account pays no interest
    }

    void withdraw(double amount) {
        if (amount > balance) {
            System.out.println(accountNumber + ": REFUSED, balance is only " + balance);
            return;
        }
        balance -= amount;
        System.out.println(accountNumber + ": withdrew " + amount + ", balance " + balance);
    }

    String describe() {
        return "Account " + accountNumber + " with balance " + balance;
    }

    // OVERLOADED methods: same name, different parameter lists (decided at compile time)
    void deposit(double amount) {
        balance += amount;
        System.out.println(accountNumber + ": deposited " + amount);
    }

    void deposit(double amount, String note) {
        balance += amount;
        System.out.println(accountNumber + ": deposited " + amount + " (" + note + ")");
    }
}

class SavingsAccount extends Account {
    private static final double INTEREST_RATE = 0.04;   // 4% per year

    SavingsAccount(String accountNumber, double balance) {
        super(accountNumber, balance);
    }

    @Override
    double calculateInterest() {
        return balance * INTEREST_RATE;
    }

    @Override
    String describe() {
        // re-use the parent's text and add more
        return super.describe() + " (Savings, 4% interest)";
    }
}

class CurrentAccount extends Account {
    private final double overdraftLimit;

    CurrentAccount(String accountNumber, double balance, double overdraftLimit) {
        super(accountNumber, balance);
        this.overdraftLimit = overdraftLimit;
    }

    // calculateInterest() is NOT overridden -> the parent's version (0) is used.

    @Override
    void withdraw(double amount) {
        // Different rule: the balance may go negative, down to -overdraftLimit.
        if (amount > balance + overdraftLimit) {
            System.out.println(accountNumber + ": REFUSED, overdraft limit reached");
            return;
        }
        balance -= amount;
        System.out.println(accountNumber + ": withdrew " + amount + ", balance " + balance + " (overdraft allowed)");
    }

    // @Override
    // void withdraw(int amount) { }   // would NOT compile with @Override: parameters differ,
    //                                 // so this is an overload, not an override.
}

/*
Output:
--- Interest for one year ---
Account        : 0.0
SavingsAccount : 400.0
CurrentAccount : 0.0

--- Withdraw 12,000 from each ---
A-1: REFUSED, balance is only 10000.0
S-1: REFUSED, balance is only 10000.0
C-1: withdrew 12000.0, balance -2000.0 (overdraft allowed)

--- Parent-type variable, child object ---
Account S-2 with balance 2000.0 (Savings, 4% interest)

--- Overloading (same name, different parameters) ---
S-1: deposited 500.0
S-1: deposited 500.0 (Salary credit)
*/

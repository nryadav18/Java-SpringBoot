/*
 * Example01_Encapsulation.java
 * ------------------------------------------------------------
 * What this file shows (domain: a Bank):
 *   1. ENCAPSULATION = keep the data PRIVATE inside the class and allow access
 *      only through methods that protect the rules.
 *   2. Getters (read) and setters (change) - with VALIDATION.
 *   3. Read-only fields: a getter but NO setter (accountNumber can never change).
 *   4. "Behaviour methods" (deposit/withdraw) are often better than plain setters.
 *
 * How to run (from this folder):
 *   java Example01_Encapsulation.java
 */
public class Example01_Encapsulation {

    public static void main(String[] args) {
        BankAccount account = new BankAccount("ACC-501", "Neha", 1000);

        // account.balance = 1_000_000;   // COMPILE ERROR: balance has private access in BankAccount
        // The only way to change the balance is through the methods below, which check the rules.

        account.deposit(500);
        account.deposit(-200);           // rejected by validation
        account.withdraw(5000);          // rejected: not enough money
        account.withdraw(300);

        account.setHolderName("Neha Verma");   // allowed: valid new name
        account.setHolderName("   ");          // rejected: blank name

        // Reading is done with getters
        System.out.println();
        System.out.println("Account : " + account.getAccountNumber());
        System.out.println("Holder  : " + account.getHolderName());
        System.out.println("Balance : " + account.getBalance());
        System.out.println("Transactions done: " + account.getTransactionCount());
    }
}

class BankAccount {

    // 'private' = visible ONLY inside this class. Nobody outside can touch these directly.
    private final String accountNumber;   // final + no setter -> read-only after creation
    private String holderName;
    private double balance;
    private int transactionCount;

    BankAccount(String accountNumber, String holderName, double openingBalance) {
        if (openingBalance < 0) {
            throw new IllegalArgumentException("Opening balance cannot be negative");  // Topic 10
        }
        this.accountNumber = accountNumber;
        setHolderName(holderName);        // re-use the validation in the setter
        this.balance = openingBalance;
    }

    // ---------- getters: "get" + field name, return the value ----------
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public double getBalance() {
        return balance;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    // ---------- setter with validation ----------
    public void setHolderName(String holderName) {
        if (holderName == null || holderName.isBlank()) {
            System.out.println("Rejected: holder name cannot be blank");
            return;
        }
        this.holderName = holderName.strip();
        System.out.println("Holder name set to: " + this.holderName);
    }

    // NO setBalance(...) on purpose! Money changes only through deposit/withdraw,
    // so the rules can never be skipped.

    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Rejected: deposit amount must be positive (" + amount + ")");
            return;
        }
        balance += amount;
        transactionCount++;
        System.out.println("Deposited " + amount + ", balance = " + balance);
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Rejected: withdrawal amount must be positive");
            return;
        }
        if (amount > balance) {
            System.out.println("Rejected: insufficient balance for " + amount + " (balance " + balance + ")");
            return;
        }
        balance -= amount;
        transactionCount++;
        System.out.println("Withdrew " + amount + ", balance = " + balance);
    }
}

/*
Output:
Holder name set to: Neha
Deposited 500.0, balance = 1500.0
Rejected: deposit amount must be positive (-200.0)
Rejected: insufficient balance for 5000.0 (balance 1500.0)
Withdrew 300.0, balance = 1200.0
Holder name set to: Neha Verma
Rejected: holder name cannot be blank

Account : ACC-501
Holder  : Neha Verma
Balance : 1200.0
Transactions done: 2
*/

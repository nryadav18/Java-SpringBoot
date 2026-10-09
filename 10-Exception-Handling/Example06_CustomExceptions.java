/*
 * Example06_CustomExceptions.java
 *
 * What this file demonstrates:
 *   - Writing your OWN exception classes with meaningful names:
 *       InsufficientBalanceException  (CHECKED   - extends Exception)
 *       AccountNotFoundException      (UNCHECKED - extends RuntimeException)
 *   - Adding extra fields to an exception (balance, requested amount, shortfall)
 *   - Two constructors: (message) and (message, cause)
 *   - Using them in a small Bank service.
 *
 * Which to choose?  Modern Java & Spring code mostly uses UNCHECKED custom exceptions
 * (e.g. ResourceNotFoundException in Topic 35). Checked ones are shown so you know both.
 *
 * How to run:
 *   java Example06_CustomExceptions.java
 *
 * Output:
 *   Withdrew Rs.2000.0 from ACC-1. New balance: Rs.3000.0
 *   Withdrawal failed: Insufficient balance in ACC-1: balance Rs.3000.0, requested Rs.4500.0
 *     -> You need Rs.1500.0 more
 *   Lookup failed: Account not found: ACC-99
 *   Transfer of Rs.1000.0 from ACC-1 to ACC-2 done
 *   Balances: ACC-1 = Rs.2000.0, ACC-2 = Rs.1500.0
 */
public class Example06_CustomExceptions {

    public static void main(String[] args) {
        Bank bank = new Bank();

        try {
            bank.withdraw("ACC-1", 2000);
            bank.withdraw("ACC-1", 4500);                 // not enough money -> exception
            System.out.println("This line is skipped");
        } catch (InsufficientBalanceException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
            System.out.println("  -> You need Rs." + e.getShortfall() + " more");
        }

        try {
            bank.findAccount("ACC-99");
        } catch (AccountNotFoundException e) {            // unchecked: catching is optional
            System.out.println("Lookup failed: " + e.getMessage());
        }

        try {
            bank.transfer("ACC-1", "ACC-2", 1000);
        } catch (InsufficientBalanceException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
        System.out.println("Balances: ACC-1 = Rs." + bank.findAccount("ACC-1").getBalance()
                + ", ACC-2 = Rs." + bank.findAccount("ACC-2").getBalance());
    }
}

// ---------- 1. A CHECKED custom exception ----------
// Name ends with "Exception" (convention). Extends Exception -> callers MUST handle it.
class InsufficientBalanceException extends Exception {

    private final double balance;
    private final double requested;

    InsufficientBalanceException(String accountNo, double balance, double requested) {
        // super(message) stores the message so getMessage() returns it
        super("Insufficient balance in " + accountNo + ": balance Rs." + balance + ", requested Rs." + requested);
        this.balance = balance;
        this.requested = requested;
    }

    double getShortfall() {
        return requested - balance;
    }
}

// ---------- 2. An UNCHECKED custom exception ----------
class AccountNotFoundException extends RuntimeException {

    AccountNotFoundException(String accountNo) {
        super("Account not found: " + accountNo);
    }

    // Constructor that also keeps the ORIGINAL cause (see exception chaining, Example07)
    AccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

class Account {
    private final String number;
    private double balance;

    Account(String number, double balance) {
        this.number = number;
        this.balance = balance;
    }

    String getNumber()  { return number; }
    double getBalance() { return balance; }

    void withdraw(double amount) throws InsufficientBalanceException {
        if (amount > balance) {
            throw new InsufficientBalanceException(number, balance, amount);
        }
        balance -= amount;
    }

    void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit must be positive");
        }
        balance += amount;
    }
}

class Bank {
    private final Account[] accounts = {
            new Account("ACC-1", 5000),
            new Account("ACC-2", 500)
    };

    Account findAccount(String number) {
        for (Account a : accounts) {
            if (a.getNumber().equals(number)) {
                return a;
            }
        }
        throw new AccountNotFoundException(number);   // never return null - throw a clear exception
    }

    void withdraw(String number, double amount) throws InsufficientBalanceException {
        Account acc = findAccount(number);
        acc.withdraw(amount);
        System.out.println("Withdrew Rs." + amount + " from " + number + ". New balance: Rs." + acc.getBalance());
    }

    void transfer(String from, String to, double amount) throws InsufficientBalanceException {
        Account source = findAccount(from);
        Account target = findAccount(to);   // look up BOTH first, so we fail before moving any money
        source.withdraw(amount);
        target.deposit(amount);
        System.out.println("Transfer of Rs." + amount + " from " + from + " to " + to + " done");
    }
}

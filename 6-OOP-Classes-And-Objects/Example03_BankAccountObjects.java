/*
 * Example03_BankAccountObjects.java
 * ------------------------------------------------------------
 * What this file shows (domain: a Bank):
 *   1. A realistic class that mixes fields, a constructor and methods.
 *   2. Methods that CHANGE state (deposit, withdraw) and methods that RETURN values (getBalance).
 *   3. Methods that work with ANOTHER object of the same class (transferTo).
 *   4. Objects stored in an array and processed in a loop.
 *
 * (We keep fields simple here. In Topic 7 you will learn to make them 'private'
 *  so nobody can do  account.balance = 1000000;  from outside. That is called encapsulation.)
 *
 * How to run (from this folder):
 *   java Example03_BankAccountObjects.java
 */
public class Example03_BankAccountObjects {

    public static void main(String[] args) {
        BankAccount ravi = new BankAccount("ACC-101", "Ravi", 5000);
        BankAccount sita = new BankAccount("ACC-102", "Sita", 2000);

        ravi.deposit(1500);
        ravi.withdraw(10000);            // not enough money -> refused
        ravi.transferTo(sita, 2500);

        System.out.println();
        System.out.println("Ravi's balance: " + ravi.getBalance());
        System.out.println("Sita's balance: " + sita.getBalance());

        // An array can hold objects too (it holds REFERENCES to them).
        BankAccount[] accounts = {
                ravi,
                sita,
                new BankAccount("ACC-103", "John", 12000)
        };

        double totalMoney = 0;
        for (BankAccount acc : accounts) {
            acc.printStatement();
            totalMoney += acc.getBalance();
        }
        System.out.println("Total money in the bank: " + totalMoney);
    }
}

class BankAccount {
    String accountNumber;
    String holderName;
    double balance;

    BankAccount(String accountNumber, String holderName, double openingBalance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = openingBalance;
    }

    void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Deposit must be positive");
            return;
        }
        balance += amount;
        System.out.println(holderName + " deposited " + amount + " -> balance " + balance);
    }

    // returns true/false so the CALLER knows if it worked
    boolean withdraw(double amount) {
        if (amount > balance) {
            System.out.println(holderName + " tried to withdraw " + amount + " -> REFUSED (balance " + balance + ")");
            return false;
        }
        balance -= amount;
        System.out.println(holderName + " withdrew " + amount + " -> balance " + balance);
        return true;
    }

    // One object talking to another object of the same class.
    void transferTo(BankAccount target, double amount) {
        if (this.withdraw(amount)) {          // 'this' = the account sending money
            target.deposit(amount);           // 'target' = the account receiving money
            System.out.println("Transferred " + amount + " from " + holderName + " to " + target.holderName);
        }
    }

    double getBalance() {
        return balance;
    }

    void printStatement() {
        System.out.printf("%s | %-5s | balance %10.2f%n", accountNumber, holderName, balance);
    }
}

/*
Output:
Ravi deposited 1500.0 -> balance 6500.0
Ravi tried to withdraw 10000.0 -> REFUSED (balance 6500.0)
Ravi withdrew 2500.0 -> balance 4000.0
Sita deposited 2500.0 -> balance 4500.0
Transferred 2500.0 from Ravi to Sita

Ravi's balance: 4000.0
Sita's balance: 4500.0
ACC-101 | Ravi  | balance    4000.00
ACC-102 | Sita  | balance    4500.00
ACC-103 | John  | balance   12000.00
Total money in the bank: 20500.0
*/


package banking;

import java.util.ArrayList;
import java.util.Scanner;

public class BankingSystemMain {

    private static final ArrayList<BankAccount> accounts =
            new ArrayList<>();

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        // Polymorphism: BankAccount references hold subclass objects.
        accounts.add(new SavingsAccount("S1010", "Khathatso", 10000));
        accounts.add(new SavingsAccount("S1020", "Nathalia", 20000));
        accounts.add(new CurrentAccount("C1030", "Nomhlolo", 150000));

        boolean running = true;

        while (running) {
            System.out.println("\n===== CONSOLE BANKING SYSTEM =====");
            System.out.println("1. Display all accounts");
            System.out.println("2. Deposit money");
            System.out.println("3. Withdraw money");
            System.out.println("4. Transfer money");
            System.out.println("5. Calculate interest");
            System.out.println("6. Request bank loan");
            System.out.println("7. Display account details");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");

            String input = scanner.nextLine().trim();
            int choice;

            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException exception) {
                System.out.println("Enter a valid whole number.");
                TransactionLogger.log(
                        "SYSTEM", "INVALID MENU", null, null,
                        "Non-numeric menu input: " + input);
                continue;
            }

            switch (choice) {
                case 1 -> displayAllAccounts();
                case 2 -> depositMoney();
                case 3 -> withdrawMoney();
                case 4 -> transferMoney();
                case 5 -> calculateInterest();
                case 6 -> requestLoan();
                case 7 -> displayAccountDetails();

                case 8 -> {
                    TransactionLogger.log(
                            "SYSTEM", "EXIT", null, null,
                            "Program closed");
                    running = false;
                    System.out.println(
                            "Thank you for using the system.");
                }

                default -> {
                    System.out.println(
                            "Choose a number from 1 to 8.");
                    TransactionLogger.log(
                            "SYSTEM", "INVALID MENU", null, null,
                            "Invalid choice: " + choice);
                }
            }
        }

        scanner.close();
    }

    private static BankAccount findAccount(String number) {
        for (BankAccount account : accounts) {
            if (account.getAccountNumber()
                    .equalsIgnoreCase(number)) {
                return account;
            }
        }
        return null;
    }

    private static BankAccount askForAccount() {
        System.out.print("Enter account number: ");
        String number = scanner.nextLine().trim();

        BankAccount account = findAccount(number);

        if (account == null) {
            TransactionLogger.log(
                    number, "ACCOUNT LOOKUP", null, null,
                    "Rejected: account not found");

            throw new IllegalArgumentException(
                    "Account not found.");
        }

        return account;
    }

    // Asks the user for a specific transaction amount.
    private static double askForAmount(String action) {
        System.out.print(
                "Enter the amount you want to " + action + ": M");

        try {
            double amount =
                    Double.parseDouble(scanner.nextLine().trim());

            if (!Double.isFinite(amount) || amount <= 0) {
                throw new IllegalArgumentException(
                        "Amount must be greater than zero.");
            }

            return amount;

        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Please enter a valid number.");
        }
    }

    private static void displayAllAccounts() {
        for (BankAccount account : accounts) {
            account.displayAccountInfo();

            System.out.println(
                    "Type: " + account.getClass().getSimpleName());
            System.out.println("-------------------------");
        }

        TransactionLogger.log(
                "SYSTEM", "DISPLAY ALL", null, null,
                "Displayed all accounts");
    }

    // Deposit money
    private static void depositMoney() {
        BankAccount account = null;
        Double amount = null;

        try {
            System.out.println("\n--- DEPOSIT MONEY ---");

            account = askForAccount();
            amount = askForAmount("deposit");

            account.deposit(amount);

            System.out.printf(
                    "Amount deposited: M%.2f%n", amount);
            System.out.printf(
                    "New balance: M%.2f%n",
                    account.getBalance());

            TransactionLogger.log(
                    account.getAccountNumber(),
                    "DEPOSIT", amount, account.getBalance(),
                    "Successful");

        } catch (IllegalArgumentException
                 | IllegalStateException exception) {

            logRejected(
                    account, "DEPOSIT", amount,
                    exception.getMessage());

            System.out.println(
                    "Deposit rejected: " + exception.getMessage());
        }
    }

    // Withdraw money
    private static void withdrawMoney() {
        BankAccount account = null;
        Double amount = null;

        try {
            System.out.println("\n--- WITHDRAW MONEY ---");

            account = askForAccount();
            amount = askForAmount("withdraw");

            account.withdraw(amount);

            System.out.printf(
                    "Amount withdrawn: M%.2f%n", amount);
            System.out.printf(
                    "Remaining balance: M%.2f%n",
                    account.getBalance());

            TransactionLogger.log(
                    account.getAccountNumber(),
                    "WITHDRAWAL", amount, account.getBalance(),
                    "Successful");

        } catch (IllegalArgumentException
                 | IllegalStateException exception) {

            logRejected(
                    account, "WITHDRAWAL", amount,
                    exception.getMessage());

            System.out.println(
                    "Withdrawal rejected: "
                            + exception.getMessage());
        }
    }

    // Transfer money between accounts
    private static void transferMoney() {
        BankAccount source = null;
        BankAccount destination = null;
        Double amount = null;

        try {
            System.out.println("\n--- TRANSFER MONEY ---");
            System.out.println("SOURCE ACCOUNT");
            source = askForAccount();

            System.out.println("DESTINATION ACCOUNT");
            destination = askForAccount();

            if (source == destination) {
                throw new IllegalArgumentException(
                        "Choose two different accounts.");
            }

            amount = askForAmount("transfer");

            // Transfers cannot use an overdraft.
            if (source.getBalance() - amount < 0) {
                throw new IllegalStateException(
                        "Insufficient funds for transfer.");
            }

            source.withdraw(amount);

            try {
                destination.deposit(amount);

            } catch (IllegalArgumentException
                     | IllegalStateException exception) {

                // Return the money if the destination deposit fails.
                source.deposit(amount);
                throw exception;
            }

            TransactionLogger.log(
                    source.getAccountNumber(),
                    "TRANSFER OUT", amount, source.getBalance(),
                    "Sent to " + destination.getAccountNumber());

            TransactionLogger.log(
                    destination.getAccountNumber(),
                    "TRANSFER IN", amount,
                    destination.getBalance(),
                    "Received from " + source.getAccountNumber());

            System.out.printf(
                    "Amount transferred: M%.2f%n", amount);
            System.out.printf(
                    "Source balance: M%.2f%n",
                    source.getBalance());
            System.out.printf(
                    "Destination balance: M%.2f%n",
                    destination.getBalance());

            System.out.println("Transfer successful.");

        } catch (IllegalArgumentException
                 | IllegalStateException exception) {

            logRejected(
                    source, "TRANSFER", amount,
                    exception.getMessage());

            System.out.println(
                    "Transfer rejected: "
                            + exception.getMessage());
        }
    }

    // Calculate interest
    private static void calculateInterest() {
        BankAccount account = null;

        try {
            System.out.println("\n--- CALCULATE INTEREST ---");

            account = askForAccount();
            double interest = account.calculateInterest();

            System.out.printf(
                    "Interest earned: M%.2f%n", interest);
            System.out.printf(
                    "Current balance: M%.2f%n",
                    account.getBalance());

            TransactionLogger.log(
                    account.getAccountNumber(),
                    "INTEREST", interest, account.getBalance(),
                    "Successful");

        } catch (IllegalArgumentException
                 | IllegalStateException exception) {

            logRejected(
                    account, "INTEREST", null,
                    exception.getMessage());

            System.out.println(
                    "Interest failed: " + exception.getMessage());
        }
    }

    // Request a bank loan
    private static void requestLoan() {
        BankAccount account = null;
        Double amount = null;

        try {
            System.out.println("\n--- REQUEST BANK LOAN ---");

            account = askForAccount();
            amount = askForAmount("borrow");

            if (!(account instanceof Loanable)) {
                throw new IllegalStateException(
                        "This account cannot request a loan.");
            }

            Loanable loanable = (Loanable) account;
            boolean approved = loanable.requestLoan(amount);

            if (!approved) {
                throw new IllegalStateException(
                        "The loan request was not approved.");
            }

            System.out.printf(
                    "Loan approved: M%.2f%n", amount);

            TransactionLogger.log(
                    account.getAccountNumber(),
                    "LOAN", amount, account.getBalance(),
                    "Approved");

        } catch (IllegalArgumentException
                 | IllegalStateException exception) {

            logRejected(
                    account, "LOAN", amount,
                    exception.getMessage());

            System.out.println(
                    "Loan rejected: " + exception.getMessage());
        }
    }

    // Display account details
    private static void displayAccountDetails() {
        BankAccount account = null;

        try {
            System.out.println("\n--- ACCOUNT DETAILS ---");

            account = askForAccount();
            account.displayAccountInfo();

            System.out.println(
                    "Account type: "
                            + account.getClass().getSimpleName());

            TransactionLogger.log(
                    account.getAccountNumber(),
                    "ACCOUNT DETAILS", null,
                    account.getBalance(),
                    "Displayed successfully");

        } catch (IllegalArgumentException
                 | IllegalStateException exception) {

            logRejected(
                    account, "ACCOUNT DETAILS", null,
                    exception.getMessage());

            System.out.println(exception.getMessage());
        }
    }

    // Record rejected transactions
    private static void logRejected(
            BankAccount account,
            String operation,
            Double amount,
            String message) {

        String number = account == null
                ? "UNKNOWN"
                : account.getAccountNumber();

        Double balance = account == null
                ? null
                : account.getBalance();

        TransactionLogger.log(
                number, operation, amount, balance,
                "Rejected: " + message);
    }
}


package banking;

public class SavingsAccount extends BankAccount implements Loanable {
    private static final double MINIMUM_BALANCE = 100.00;
    private static final double INTEREST_RATE = 0.05;
    private static final double LOAN_LIMIT = 50000.00;

    public SavingsAccount(String accountNumber, String accountHolder,
                          double openingBalance) {
        super(accountNumber, accountHolder, openingBalance);
    }

    private void validateAmount(double amount, String type) {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException(
                type + " amount must be greater than zero.");
        }
    }

    @Override
    public void deposit(double amount) {
        validateAmount(amount, "Deposit");
        updateBalance(amount);
    }

    @Override
    public void withdraw(double amount) {
        validateAmount(amount, "Withdrawal");

        if (getBalance() - amount < MINIMUM_BALANCE) {
            throw new IllegalStateException(
                "Savings balance cannot go below M100.00.");
        }

        updateBalance(-amount);
    }

    @Override
    public double calculateInterest() {
        double interest = getBalance() * INTEREST_RATE;
        updateBalance(interest);
        return interest;
    }

    @Override
    public boolean requestLoan(double amount) {
        validateAmount(amount, "Loan");

        if (amount > LOAN_LIMIT) {
            throw new IllegalStateException(
                "Savings loan limit is M50000.00.");
        }

        updateBalance(amount);
        return true;
    }
}

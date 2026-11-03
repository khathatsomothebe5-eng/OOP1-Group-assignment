
package banking;

public class CurrentAccount extends BankAccount implements Loanable {
    private static final double OVERDRAFT_LIMIT = 2000.00;
    private static final double LOAN_LIMIT = 100000.00;

    public CurrentAccount(String accountNumber, String accountHolder,
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

        if (getBalance() - amount < -OVERDRAFT_LIMIT) {
            throw new IllegalStateException(
                "Withdrawal exceeds the M2000.00 overdraft limit.");
        }

        updateBalance(-amount);
    }

    @Override
    public double calculateInterest() {
        return 0.00;
    }

    @Override
    public boolean requestLoan(double amount) {
        validateAmount(amount, "Loan");

        if (amount > LOAN_LIMIT) {
            throw new IllegalStateException(
                "Current account loan limit is M100000.00.");
        }

        updateBalance(amount);
        return true;
    }
}

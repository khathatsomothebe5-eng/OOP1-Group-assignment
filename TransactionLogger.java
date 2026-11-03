
package banking;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Final class: this class is not intended to be extended.
public final class TransactionLogger {
    private static final Path LOG_FILE = Path.of("transactions.txt");

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private TransactionLogger() {
    }

    public static synchronized void log(
            String accountNumber,
            String transactionType,
            Double amount,
            Double balance,
            String resultMessage) {

        String amountText = amount == null
            ? "N/A" : String.format("M%.2f", amount);

        String balanceText = balance == null
            ? "N/A" : String.format("M%.2f", balance);

        String timestamp = LocalDateTime.now().format(FORMATTER);

        String entry = String.format(
            "%s | Account: %s | Type: %s | Amount: %s | Balance: %s | Result: %s%n",
            timestamp, accountNumber, transactionType,
            amountText, balanceText, resultMessage);

        try {
            Files.writeString(
                LOG_FILE,
                entry,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
        } catch (IOException exception) {
            System.out.println(
                "Could not write to transactions.txt: "
                + exception.getMessage());
        }
    }
}

package base.exception;

public class VendingMachineInsufficientBalanceException extends RuntimeException {

    /**
     * Coins left to be provided for the specified item
     */
    private int leftAmount;

    public VendingMachineInsufficientBalanceException(String message, int leftAmount) {
        super(message);
        this.leftAmount = leftAmount;
    }

    public int getLeftAmount() {
        return this.leftAmount;
    }

    @Override
    public String toString() {
        return "Coins left to insert: " + leftAmount;
    }
}

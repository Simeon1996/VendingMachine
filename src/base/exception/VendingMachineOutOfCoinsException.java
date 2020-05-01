package base.exception;

public class VendingMachineOutOfCoinsException extends RuntimeException {
    public VendingMachineOutOfCoinsException(String message) {
        super(message);
    }
}
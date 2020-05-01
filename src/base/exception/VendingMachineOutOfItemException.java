package base.exception;

public class VendingMachineOutOfItemException extends RuntimeException {
    public VendingMachineOutOfItemException(String message) {
        super(message);
    }
}

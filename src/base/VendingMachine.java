package base;

import base.exception.VendingMachineInsufficientBalanceException;
import base.exception.VendingMachineOutOfItemException;
import base.model.Coin;
import base.model.Item;

import java.util.List;
import java.util.Map;

/**
 * Default actions for each kind of Vending Machine implementations
 */
public interface VendingMachine {

    /**
     * Default amount of items from each type. E.g. Coke, Pepsi.. etc.
     */
    int DEFAULT_ITEMS_FROM_EACH_TYPE_AMOUNT = 5;

    /**
     * Default amount of coins from each type. E.g. Nickel, Dime.. etc.
     */
    int DEFAULT_COINS_FROM_EACH_TYPE_AMOUNT = 5;

    /**
     * Resets the machine fully to an initial state.
     */
    void reset();

    /**
     * Inserts a coin within the machine
     *
     * @param coin Coin enum to represent the available coins that the machine can work with
     */
    void insert(Coin coin);

    /**
     * Gets and item on selection, if the required coins from the client are provided.
     *
     * @param item Item enum to represent the available items within the machine.
     *
     * @return Map with the item that the client wants to get along with a List<Coin> as a change
     *
     * @throws VendingMachineOutOfItemException to express the failure due to machine running out of certain items.
     * @throws VendingMachineInsufficientBalanceException to express the failure when one tries to get a certain item without having provided
     * the required amount of coins.
     */
    Map<Item, List<Coin>> getOnSelect(Item item) throws VendingMachineOutOfItemException, VendingMachineInsufficientBalanceException;

    /**
     * Canceling the request and getting back the coins that were provided by the client
     *
     * @return List of coins
     */
    List<Coin> cancel();

    /**
     * Get the current balance within the machine
     *
     * @return int balance
     */
    int getCurrentBalance();
}

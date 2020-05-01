package base.machines;

import base.VendingMachine;
import base.exception.VendingMachineInsufficientBalanceException;
import base.exception.VendingMachineOutOfCoinsException;
import base.exception.VendingMachineOutOfItemException;
import base.model.Coin;
import base.model.Item;

import java.util.*;

/**
 * Standard implementation of a vending machine
 */
public class StandardVendingMachine implements VendingMachine {

    /**
     * Storage for all coins in machine inventory based on instance
     */
    private final List<Coin> coinsInventory = new ArrayList<>();

    /**
     * Storage for all items in machine inventory based on instance
     */
    private final List<Item> itemsInventory = new ArrayList<>();

    /**
     * Initial machine balance
     */
    private int currentBalance = 0;

    public StandardVendingMachine()
    {
        initialize();
    }

    /**
     * Initialize vending machine instance with default number of entities in the inventories
     */
    private void initialize() {
        List<Item> items = Arrays.asList(Item.values());
        List<Coin> coins = Arrays.asList(Coin.values());

        addUnitsInInventory(DEFAULT_ITEMS_FROM_EACH_TYPE_AMOUNT, items, itemsInventory);
        addUnitsInInventory(DEFAULT_COINS_FROM_EACH_TYPE_AMOUNT, coins, coinsInventory);
    }

    @Override
    public synchronized void reset() {
        itemsInventory.clear();
        coinsInventory.clear();
        setCurrentBalance(0);
    }

    @Override
    public synchronized void insert(Coin coin) {
        coinsInventory.add(coin);
        setCurrentBalance(getCurrentBalance() + coin.getValue());
    }

    @Override
    public int getCurrentBalance()
    {
        return this.currentBalance;
    }

    /**
     * Current balance setter
     *
     * @param balance int value
     */
    private void setCurrentBalance(int balance) {
        this.currentBalance = balance;
    }

    @Override
    public synchronized Map<Item, List<Coin>> getOnSelect(Item item) throws VendingMachineOutOfItemException, VendingMachineInsufficientBalanceException {
        final Map<Item, List<Coin>> itemAndChange = new HashMap<>(1);
        int itemPrice = item.getPrice();

        if (!itemsInventory.contains(item)) {
            throw new VendingMachineOutOfItemException("Item " + item.toString() + " is not available.");
        } else if (getCurrentBalance() < itemPrice) {
            throw new VendingMachineInsufficientBalanceException("Not enough to buy the item.", itemPrice - getCurrentBalance());
        }

        setCurrentBalance(getCurrentBalance() - item.getPrice());

        List<Coin> change = null;

        try {
             change = getBalanceAsCoins(getCurrentBalance());
             setCurrentBalance(0);
        } catch (VendingMachineOutOfCoinsException ex) {
            // In case there are not enough coins from a certain value and the change cannot be fully paid
            // a revert to the initial balance is made and refunded back to the user.
            setCurrentBalance(getCurrentBalance() + item.getPrice());

            try {
                Map<Item, List<Coin>> coinsWithoutItem = Collections.singletonMap(null, getBalanceAsCoins(getCurrentBalance()));
                setCurrentBalance(0);

                return coinsWithoutItem;
            } catch (VendingMachineOutOfCoinsException ignored) {
                // Can't access this block since that's the scenario when
                // we refund everything that the client has provided.
            }
        }

        itemsInventory.remove(item);

        itemAndChange.put(item, change);

        return itemAndChange;
    }

    /**
     * Get the int value of balance as coins from the machine coins inventory
     *
     * @param balance int value
     *
     * @return List of coins that have the same value as the balance
     *
     * @throws VendingMachineOutOfCoinsException is thrown whenever there are not enough coins of a certain type to pay the change
     */
    private List<Coin> getBalanceAsCoins(int balance) throws VendingMachineOutOfCoinsException {
        List<Coin> changeInCoins = new ArrayList<>();

        if (balance > 0) {

            while (coinsInventory.contains(Coin.QUARTER) && balance >= Coin.QUARTER.getValue()) {
                changeInCoins.add(Coin.QUARTER);
                balance -= Coin.QUARTER.getValue();
            }

            while (coinsInventory.contains(Coin.DIME) && balance >= Coin.DIME.getValue()) {
                changeInCoins.add(Coin.DIME);
                balance -= Coin.DIME.getValue();
            }

            while (coinsInventory.contains(Coin.NICKEL) && balance >= Coin.NICKEL.getValue()) {
                changeInCoins.add(Coin.NICKEL);
                balance -= Coin.NICKEL.getValue();
            }

            while (coinsInventory.contains(Coin.PENNY) && balance >= Coin.PENNY.getValue()) {
                changeInCoins.add(Coin.PENNY);
                balance -= Coin.PENNY.getValue();
            }

            if (balance > 0) {
                throw new VendingMachineOutOfCoinsException("Not enough coins from certain type to pay the change.");
            }
        }

        return changeInCoins;
    }

    @Override
    public synchronized List<Coin> cancel() {
        List<Coin> coins = null;

        try {
            coins = getBalanceAsCoins(getCurrentBalance());
        } catch (VendingMachineOutOfCoinsException ignored) {
            // Can't access this block since that's the scenario when
            // we refund everything that the client has provided.
        }

        return coins;
    }

    /**
     * Generic method to populate the different inventories with specified amount of units
     *
     * @param amount int value of how much entities will be added
     * @param units List of Items or Coins to be added
     * @param inventory The inventory to add the specified entities
     * @param <T> Coin || Item
     */
    private <T> void addUnitsInInventory(int amount, final List<T> units, final List<T> inventory) {
        for (int i = 0; i < amount; i++) {
            inventory.addAll(units);
        }
    }
}

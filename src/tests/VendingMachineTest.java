package tests;

import base.VendingMachine;
import base.VendingMachineFactory;
import base.model.VendingMachineType;
import base.exception.VendingMachineInsufficientBalanceException;
import base.exception.VendingMachineOutOfItemException;
import base.model.Coin;
import base.model.Item;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class VendingMachineTest {

    @Test
    public void testBalanceOnMachineInitialize()
    {
        VendingMachine machine = VendingMachineFactory.create(VendingMachineType.STANDARD);

        Assert.assertEquals(0, machine.getCurrentBalance());
    }

    @Test
    public void testBalanceAfterNickelInsertion()
    {
        VendingMachine machine = VendingMachineFactory.create(VendingMachineType.STANDARD);
        int balance = 0;

        // 5
        machine.insert(Coin.NICKEL);
        balance =+ Coin.NICKEL.getValue();

        Assert.assertEquals(balance, machine.getCurrentBalance());

        // 1
        machine.insert(Coin.PENNY);
        balance += Coin.PENNY.getValue();

        Assert.assertEquals(balance, machine.getCurrentBalance());

        // 10
        machine.insert(Coin.DIME);
        balance += Coin.DIME.getValue();

        Assert.assertEquals(balance, machine.getCurrentBalance());

        // 20
        machine.insert(Coin.QUARTER);
        balance += Coin.QUARTER.getValue();

        Assert.assertEquals(balance, machine.getCurrentBalance());
    }

    @Test(expected = VendingMachineInsufficientBalanceException.class)
    public void testBuyCokeWithNotEnoughCoins() throws VendingMachineInsufficientBalanceException, VendingMachineOutOfItemException
    {
        VendingMachine machine = VendingMachineFactory.create(VendingMachineType.STANDARD);

        machine.getOnSelect(Item.COKE);
    }

    @Test
    public void testBuyCokeWithNotEnoughCoinsAndGetRemainingCoins() throws VendingMachineOutOfItemException
    {
        VendingMachine machine = VendingMachineFactory.create(VendingMachineType.STANDARD);

        try {
            machine.getOnSelect(Item.COKE);
        } catch (VendingMachineInsufficientBalanceException ex) {
            Assert.assertEquals(Item.COKE.getPrice(), ex.getLeftAmount());
        }
    }

    @Test
    public void testBuyCoke() throws VendingMachineInsufficientBalanceException, VendingMachineOutOfItemException
    {
        VendingMachine machine = VendingMachineFactory.create(VendingMachineType.STANDARD);

        machine.insert(Coin.QUARTER);
        machine.insert(Coin.DIME);
        machine.insert(Coin.NICKEL);

        Assert.assertEquals(35, machine.getCurrentBalance());

        Map<Item, List<Coin>> itemAndChange = machine.getOnSelect(Item.COKE);

        Assert.assertTrue(itemAndChange.containsKey(Item.COKE));
        Assert.assertEquals(0, machine.getCurrentBalance());
        Assert.assertEquals(Collections.singletonList(Coin.DIME), itemAndChange.get(Item.COKE));
    }

    @Test(expected = VendingMachineOutOfItemException.class)
    public void testMoreCokesThanAvailable() throws VendingMachineInsufficientBalanceException, VendingMachineOutOfItemException
    {
        VendingMachine machine = VendingMachineFactory.create(VendingMachineType.STANDARD);

        // Buy all products from the same type. In this cake "Coke"
        for (int i = 0; i < VendingMachine.DEFAULT_ITEMS_FROM_EACH_TYPE_AMOUNT; i ++) {
            machine.insert(Coin.QUARTER);
            machine.insert(Coin.DIME);

            Assert.assertEquals((Coin.QUARTER.getValue() + Coin.DIME.getValue()), machine.getCurrentBalance());

            Map<Item, List<Coin>> itemAndChange = machine.getOnSelect(Item.COKE);

            Assert.assertTrue(itemAndChange.containsKey(Item.COKE));
            Assert.assertEquals(0, machine.getCurrentBalance());
            Assert.assertEquals(Collections.singletonList(Coin.NICKEL), itemAndChange.get(Item.COKE));
        }

        // All products are sold and there are no more left.. exception will be thrown
        machine.insert(Coin.QUARTER);
        machine.insert(Coin.DIME);

        machine.getOnSelect(Item.COKE);
    }

    @Test
    public void testCancelOperation()
    {
        VendingMachine vendingMachine = VendingMachineFactory.create(VendingMachineType.STANDARD);

        vendingMachine.insert(Coin.QUARTER);
        vendingMachine.insert(Coin.DIME);

        Assert.assertEquals(Coin.QUARTER.getValue() + Coin.DIME.getValue(), vendingMachine.getCurrentBalance());

        List<Coin> returnedCoins = vendingMachine.cancel();

        Assert.assertEquals(Arrays.asList(Coin.QUARTER, Coin.DIME), returnedCoins);
    }

    @Test
    public void testResetOperationForBalance()
    {
        VendingMachine vendingMachine = VendingMachineFactory.create(VendingMachineType.STANDARD);

        vendingMachine.insert(Coin.QUARTER);
        vendingMachine.insert(Coin.DIME);

        Assert.assertEquals(vendingMachine.getCurrentBalance(), Coin.QUARTER.getValue() + Coin.DIME.getValue());

        vendingMachine.reset();

        Assert.assertEquals(0, vendingMachine.getCurrentBalance());
    }

    @Test(expected = VendingMachineOutOfItemException.class)
    public void testResetOperationForInventoryItems() throws VendingMachineOutOfItemException, VendingMachineInsufficientBalanceException {
        VendingMachine vendingMachine = VendingMachineFactory.create(VendingMachineType.STANDARD);

        vendingMachine.insert(Coin.QUARTER);
        vendingMachine.insert(Coin.DIME);

        Assert.assertEquals(vendingMachine.getCurrentBalance(), Coin.QUARTER.getValue() + Coin.DIME.getValue());

        vendingMachine.reset();

        // No items should be found in the inventory since we cleared them all
        vendingMachine.getOnSelect(Item.COKE);
    }

    @Test
    public void testMachineNotBeingAbleToReturnChange() throws VendingMachineOutOfItemException, VendingMachineInsufficientBalanceException {
        VendingMachine vendingMachine = VendingMachineFactory.create(VendingMachineType.STANDARD);

        // All items and coins are pulled out
        vendingMachine.reset();

        try {
            Field itemsInventoryField = vendingMachine.getClass().getDeclaredField("itemsInventory");

            itemsInventoryField.setAccessible(true);
            itemsInventoryField.set(vendingMachine, Collections.singletonList(Item.PEPSI));

            Assert.assertEquals(vendingMachine.getCurrentBalance(), 0);

            vendingMachine.insert(Coin.QUARTER);
            vendingMachine.insert(Coin.QUARTER);

            Assert.assertEquals(40, (Coin.QUARTER.getValue() + Coin.QUARTER.getValue()));

            // since the coins inventory has only two coins with value of 20 and nothing more
            // once we try to buy the Pepsi that costs 35 there won't be change to return,
            // so we should return no item and all of the balance as coins as the user provided.
            Map<Item, List<Coin>> itemAndChange = vendingMachine.getOnSelect(Item.PEPSI);

            // Pepsi won't be returned as item
            Assert.assertFalse(itemAndChange.containsKey(Item.PEPSI));
            Assert.assertTrue(itemAndChange.containsKey(null));

            // All of the coins will be returned
            Assert.assertEquals(Arrays.asList(Coin.QUARTER, Coin.QUARTER), itemAndChange.get(null));

            // Balance should be zero
            Assert.assertEquals(0, vendingMachine.getCurrentBalance());

            // Pepsi should still be in the inventory
            Assert.assertEquals(Collections.singletonList(Item.PEPSI), itemsInventoryField.get(vendingMachine));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}

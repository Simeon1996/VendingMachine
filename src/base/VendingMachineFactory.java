package base;

import base.machines.StandardVendingMachine;
import base.model.VendingMachineType;

/**
 * Factory to produce all kinds of vending machines
 */
public class VendingMachineFactory {

    /**
     * Create vending machine from a specified type
     *
     * @param type Enum
     *
     * @return VendingMachine
     */
    public static VendingMachine create(VendingMachineType type)
    {
        VendingMachine machine = null;

        if (type == VendingMachineType.STANDARD) {
            machine = new StandardVendingMachine();
        }

        return machine;
    }
}

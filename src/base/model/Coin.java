package base.model;

/**
 * Different representations of a coin
 */
public enum Coin {
    PENNY(1),
    NICKEL(5),
    DIME(10),
    QUARTER(20);

    private int value;

    Coin(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

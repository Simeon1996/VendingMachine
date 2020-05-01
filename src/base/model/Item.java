package base.model;

/**
 * Items that the vending machine will support along with their price
 */
public enum Item {
    COKE(25),
    PEPSI(35),
    SODA(45);

    private int price;

    Item(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }
}

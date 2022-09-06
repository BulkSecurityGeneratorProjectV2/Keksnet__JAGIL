package de.neo.jagil.util;

public class InventoryPositionUtil {

    public static int toSlot(int x, int y) {
        return y + (x * 9);
    }

}

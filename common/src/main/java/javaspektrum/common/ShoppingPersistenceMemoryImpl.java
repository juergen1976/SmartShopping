package javaspektrum.common;

import java.util.ArrayList;
import java.util.List;

public class ShoppingPersistenceMemoryImpl implements ShoppingPersistence {

    public int maxID = 0;
    private final List<ShoppingListItem> arrayOfShoppingItems = new ArrayList<>();

    @Override
    public List<ShoppingListItem> getShoppingListItems() {
        return arrayOfShoppingItems;
    }

    @Override
    public long createShoppingItem(ShoppingListItem newItem) {
        arrayOfShoppingItems.add(newItem);
        return maxID;
    }

    @Override
    public int updateShoppingItem(ShoppingListItem shopItem) {
        int index = arrayOfShoppingItems.indexOf(shopItem);
        arrayOfShoppingItems.set(index, shopItem);
        return 1;
    }

    @Override
    public void deleteShoppingItem(ShoppingListItem shopItem) {
        arrayOfShoppingItems.remove(shopItem);
    }

    @Override
    public int getNextId() {
        this.maxID++;
        return maxID;
    }
}
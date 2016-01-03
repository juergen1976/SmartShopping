package javaspektrum.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juergen on 01.01.16.
 */
public interface ShoppingPersistence {
    List<ShoppingListItem> getShoppingListItems();

    long createShoppingItem(ShoppingListItem newItem);

    int updateShoppingItem(ShoppingListItem shopItem);

    void deleteShoppingItem(ShoppingListItem shopItem);

    int getNextId();
}
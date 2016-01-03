package javaspektrum.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juergen on 01.01.16.
 */
public class ShoppingDatabase extends SQLiteOpenHelper implements ShoppingPersistence {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "smartshopping";

    // Shopping table name
    private static final String TABLE_SHOPPING = "shopping";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_BOUGHT = "bought";

    public ShoppingDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SHOPPING_TABLE = "CREATE TABLE " + TABLE_SHOPPING + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_BOUGHT + " INTEGER" + ")";
        db.execSQL(CREATE_SHOPPING_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPPING);

        // Create tables again
        onCreate(db);
    }

    public int getNextId() {
        return 0;
    }

    public long createShoppingItem(ShoppingListItem shopItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, shopItem.getName());
        values.put(KEY_BOUGHT, shopItem.isBought());

        // Inserting Row
        long newResult = db.insert(TABLE_SHOPPING, null, values);
        db.close(); // Closing database connection
        return newResult;
    }


    public ShoppingListItem getShoppingListItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SHOPPING, new String[] { KEY_ID,
                        KEY_NAME, KEY_BOUGHT }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ShoppingListItem shoppingListItem = new ShoppingListItem(Integer.parseInt(cursor
                .getString(0)),
                cursor.getString(1), cursor.getInt(2) == 1 ? true: false);
        return shoppingListItem;
    }


    public List<ShoppingListItem> getShoppingListItems() {
        List<ShoppingListItem> shoppingList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_SHOPPING;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ShoppingListItem shoppingListItem = new ShoppingListItem();
                shoppingListItem.setId(Integer.parseInt(cursor.getString(0)));
                shoppingListItem.setName(cursor.getString(1));
                shoppingListItem.setBought(cursor.getInt(2) == 1 ? true: false);

                shoppingList.add(shoppingListItem);
            } while (cursor.moveToNext());
        }

        return shoppingList;
    }


    public int updateShoppingItem(ShoppingListItem shoppingItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, shoppingItem.getName());
        values.put(KEY_BOUGHT, shoppingItem.isBought());

        // updating row
        return db.update(TABLE_SHOPPING, values, KEY_ID + " = ?",
                new String[] { String.valueOf(shoppingItem.getId()) });
    }

    public void deleteShoppingItem(ShoppingListItem shoppingItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SHOPPING, KEY_ID + " = ?",
                new String[] { String.valueOf(shoppingItem.getId()) });
        db.close();
    }

    public int getShoppingListCount() {
        String countQuery = "SELECT  * FROM " + TABLE_SHOPPING;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}
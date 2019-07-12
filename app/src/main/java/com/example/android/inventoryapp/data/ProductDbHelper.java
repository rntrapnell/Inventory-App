package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.inventoryapp.data.ProductsContract.ProductEntry;

/**
 * Created by trappe on 12/10/16.
 */
public class ProductDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventory.db";
    String SQL_CREATE_PRODUCTS_TABLE =  "CREATE TABLE " +  ProductEntry.TABLE_NAME + " ("
            +  ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            +  ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
            +  ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, "
            +  ProductEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
            +  ProductEntry.COLUMN_PRODUCT_SUPPLIER + " TEXT NOT NULL,"
            +  ProductEntry.COLUMN_SUPPLIER_CONTACT + " TEXT NOT NULL,"
            +  ProductEntry.COLUMN_PRODUCT_IMAGE + " INTEGER); ";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

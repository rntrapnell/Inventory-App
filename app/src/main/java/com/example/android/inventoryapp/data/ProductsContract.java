package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by trappe on 12/10/16.
 */
public class ProductsContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    public static final String PATH_PRODUCTS = "products";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static abstract class ProductEntry implements BaseColumns {


        //Table constants
        public static final String TABLE_NAME = "products";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_SUPPLIER = "supplier";
        public static final String COLUMN_SUPPLIER_CONTACT = "email";
        public static final String COLUMN_PRODUCT_IMAGE = "image";



        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);


        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;


        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;


    }
}

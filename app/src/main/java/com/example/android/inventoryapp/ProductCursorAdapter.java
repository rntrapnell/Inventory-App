package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.ProductsContract;

/**
 * Created by trappe on 12/11/16.
 */
public class ProductCursorAdapter extends CursorAdapter {



    /**
     * Constructs a new {@link ProductCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // TODO: Fill out this method and return the list item view (instead of null)
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView nameView = (TextView) view.findViewById(R.id.name);
        TextView priceView = (TextView) view.findViewById(R.id.summary);

        final TextView quantityView = (TextView) view.findViewById(R.id.in_stock_number);
        Button saleButton = (Button) view.findViewById(R.id.sale_button);
        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String price = "$" + String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("price")));
        final String quantity = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("quantity")));
        final int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        if (TextUtils.isEmpty(price)) {
            price = "Unknown";
        }
        // Populate fields with extracted properties
        nameView.setText(name);
        priceView.setText(price);
        quantityView.setText(quantity);


        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if ((Integer.parseInt(quantityView.getText().toString()) > 0)) {
                    quantityView.setText(String.valueOf(Integer.parseInt(quantityView.getText().toString()) - 1));
                    Uri itemUri = Uri.withAppendedPath(ProductsContract.ProductEntry.CONTENT_URI, String.valueOf(id));
                    ContentValues values = new ContentValues();
                    values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, Integer.parseInt(quantityView.getText().toString()));
                    context.getContentResolver().update(itemUri, values, null, null);
                }

            }
        });
    }


}
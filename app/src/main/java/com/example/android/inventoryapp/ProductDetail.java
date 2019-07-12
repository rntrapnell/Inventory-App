package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.android.inventoryapp.data.ProductsContract;


public class ProductDetail extends MainActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    String mNameText;
    int mPriceText;
    int mQuantityText;
    String mSupplierText;
    String mSupplierEmail;
    Uri mUri;

    private static final int URL_LOADER = 0;

    TextView mNameTextView;
    TextView mQuantityView;
    TextView mSupplierTextView;
    TextView mPriceTextView;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_detail);

        FloatingActionButton fabDelete = (FloatingActionButton) findViewById(R.id.fab_delete);
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog();
            }
        });


        mNameTextView = (TextView) findViewById(R.id.product_name_detail);
        mQuantityView = (TextView) findViewById(R.id.product_quantity_detail);
        mSupplierTextView = (TextView) findViewById(R.id.supplier_and_email_detail);
        mPriceTextView = (TextView) findViewById(R.id.product_price_detail);
        mImageView = (ImageView) findViewById(R.id.image_view_detail);

        getLoaderManager().initLoader(URL_LOADER, null, this);

        TextView decrement = (TextView) findViewById(R.id.inventory_decrement);
        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog saleDialog = new Dialog(ProductDetail.this);
                final NumberPicker np = new NumberPicker(ProductDetail.this);
                np.setMaxValue((Integer.parseInt(mQuantityView.getText().toString())));
                np.setMinValue(0);
                np.setWrapSelectorWheel(true);
                np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {

                    }
                });
                final android.app.AlertDialog.Builder saleBuilder = new android.app.AlertDialog.Builder(ProductDetail.this);

                saleBuilder.setView(np);
                saleBuilder.setTitle("Record a Sale");
                saleBuilder.setPositiveButton("Sale", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mQuantityView.setText(String.valueOf(Integer.parseInt(mQuantityView.getText().toString()) - np.getValue()));
                        updateQuantityInDb();
                        saleDialog.dismiss();
                    }
                });
                saleBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        saleDialog.dismiss();
                    }
                });
                android.app.AlertDialog alertDialog = saleBuilder.create();
                alertDialog.show();
            }
        });

        TextView increment = (TextView) findViewById(R.id.inventory_increment);
        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog newShipmentDialog = new Dialog(ProductDetail.this);
                final NumberPicker np2 = new NumberPicker(ProductDetail.this);
                np2.setMaxValue(100);
                np2.setMinValue(0);
                np2.setWrapSelectorWheel(false);
                np2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                np2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {

                    }
                });
                final android.app.AlertDialog.Builder addInventoryBuilder = new android.app.AlertDialog.Builder(ProductDetail.this);

                addInventoryBuilder.setView(np2);
                addInventoryBuilder.setTitle("Add new inventory");
                addInventoryBuilder.setPositiveButton("Add Inventory", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mQuantityView.setText(String.valueOf(Integer.parseInt(mQuantityView.getText().toString()) + np2.getValue()));
                        updateQuantityInDb();
                        newShipmentDialog.dismiss();
                    }
                });
                addInventoryBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        newShipmentDialog.dismiss();
                    }
                });
                android.app.AlertDialog alertDialog = addInventoryBuilder.create();
                alertDialog.show();
            }
        });

        Button orderButton = (Button) findViewById(R.id.order_button);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", mSupplierEmail, null));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        //Takes action based on the ID of the Loader that's being created

        mUri = (Uri) getIntent().getExtras().get("URI");
        //Uri.withAppendedPath(ProductsContract.ProductEntry.CONTENT_URI,"1")
        switch (i) {
            case URL_LOADER:

                String[] mProjection = {
                        ProductsContract.ProductEntry._ID,
                        ProductsContract.ProductEntry.COLUMN_PRODUCT_NAME,
                        ProductsContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER,
                        ProductsContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                        ProductsContract.ProductEntry.COLUMN_SUPPLIER_CONTACT,
                        ProductsContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                        ProductsContract.ProductEntry.COLUMN_PRODUCT_IMAGE};

                // Returns a new CursorLoader
                return new CursorLoader(
                        ProductDetail.this,   // Parent activity context
                        mUri,        // Table to query
                        mProjection,     // Projection to return
                        null,            // No selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );

            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Figure out the index of each column
        int idColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        int supplierColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER);
        int contactColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_SUPPLIER_CONTACT);
        int imgSrcColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_IMAGE);

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Use that index to extract the String or Int value of the word
            // at the current row the cursor is on.
            int currentID = cursor.getInt(idColumnIndex);

            // Get the info sent in the intent from the main activity and assign them to variables.
            mNameText = cursor.getString(nameColumnIndex);
            mQuantityText = cursor.getInt(quantityColumnIndex);
            mPriceText = cursor.getInt(priceColumnIndex);
            mSupplierText = cursor.getString(supplierColumnIndex);
            mSupplierEmail = cursor.getString(contactColumnIndex);

            Uri imageSrc = Uri.parse(cursor.getString(imgSrcColumnIndex));
            mImageView.setImageURI(imageSrc);

            mNameTextView.setText(getString(R.string.name) + getString(R.string.colon_and_space) + mNameText);
            mQuantityView.setText(String.valueOf(mQuantityText));
            mPriceTextView.setText("Price: $" + mPriceText);
            mSupplierTextView.setText("Supplier: " + mSupplierText + "(" + mSupplierEmail + ")");
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
    }

    private void deleteDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this entry?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteProduct();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        getContentResolver().delete(mUri, null, null);
        NavUtils.navigateUpFromSameTask(ProductDetail.this);
    }

    private void updateQuantityInDb() {

        ContentValues values = new ContentValues();
        values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, Integer.parseInt(mQuantityView.getText().toString()));
        getContentResolver().update(mUri, values, null, null);

    }
}



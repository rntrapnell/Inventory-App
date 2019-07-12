package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductsContract.ProductEntry;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    ProductCursorAdapter mAdapter;
    String nameString;
    String quantityString;
    String priceString;
    String supplierString;
    String emailString;

    String mCurrentPhotoPath;


    // Identifies a particular Loader being used in this component
    private static final int URL_LOADER = 0;
    static final int REQUEST_TAKE_PHOTO = 1;

    Uri mPhotoUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener addButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                saveProduct();
                            }
                        };

                mPhotoUri = null;
                editorDialog(addButtonClickListener);
            }

        });
        // Find ListView to populate
        ListView lvItems = (ListView) findViewById(R.id.list_view);

        mAdapter = new ProductCursorAdapter(this, null);
        lvItems.setAdapter(mAdapter);
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ProductDetail.class);
                Uri itemUri = Uri.withAppendedPath(ProductEntry.CONTENT_URI, String.valueOf(id));
                intent.putExtra("URI", itemUri);
                startActivity(intent);

            }
        });

        getLoaderManager().initLoader(URL_LOADER, null, this);

        View emptyView = findViewById(R.id.empty_view);
        lvItems.setEmptyView(emptyView);
    }

    public void editorDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        final View bodyView = getLayoutInflater().inflate(R.layout.edit_alert, (ViewGroup) findViewById(R.id.test)); // Find all relevant views that we will need to read user input from
        final EditText nameEditText = (EditText) bodyView.findViewById(R.id.enter_product_name);
        final EditText quantityEditText = (EditText) bodyView.findViewById(R.id.enter_product_quantity);
        final EditText priceEditText = (EditText) bodyView.findViewById(R.id.enter_product_price);
        final EditText supplierEditText = (EditText) bodyView.findViewById(R.id.enter_product_supplier);
        final EditText emailEditText = (EditText) bodyView.findViewById(R.id.enter_supplier_email);
        final Button takePicButton = (Button) bodyView.findViewById(R.id.take_picture);
        takePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(bodyView);
        builder.setTitle("Add a Product");

        builder.setPositiveButton("Save", null);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
            }
        });

        android.app.AlertDialog alertDialog = builder.create();

        //Code to keep dialog on screen while showing warning toast. http://stackoverflow.com/a/7636468
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {
                        nameString = nameEditText.getText().toString().trim();
                        quantityString = quantityEditText.getText().toString().trim();
                        priceString = priceEditText.getText().toString().trim();
                        supplierString = supplierEditText.getText().toString().trim();
                        emailString = emailEditText.getText().toString().trim();
                        if (TextUtils.isEmpty(nameString)) {
                            Toast.makeText(MainActivity.this, "Please insert product name", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(quantityString) || (Integer.parseInt(quantityString)) == 0) {
                            Toast.makeText(MainActivity.this, "Please insert product quantity", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(priceString) || (Integer.parseInt(priceString)) == 0) {
                            Toast.makeText(MainActivity.this, "Please insert product price", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(supplierString)) {
                            Toast.makeText(MainActivity.this, "Please insert product supplier", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(emailString)) {
                            Toast.makeText(MainActivity.this, "Please insert supplier email", Toast.LENGTH_SHORT).show();
                        } else if (mPhotoUri == null) {
                            Toast.makeText(MainActivity.this, "Please insert a photo", Toast.LENGTH_SHORT).show();
                        } else {
                            saveProduct();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        alertDialog.show();
    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        switch (i) {
            case URL_LOADER:

                return new CursorLoader(
                        this,
                        ProductEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);

    }


    private void saveProduct() {

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, supplierString);
        values.put(ProductEntry.COLUMN_SUPPLIER_CONTACT, emailString);
        values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, mPhotoUri.toString());

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                mPhotoUri = photoURI;


            }
        }

    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}

package javaspektrum.smartshopping;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import javaspektrum.common.DataStorageConstants;
import javaspektrum.common.ShoppingDatabase;
import javaspektrum.common.ShoppingListItem;
import javaspektrum.common.ShoppingPersistence;
import javaspektrum.common.ShoppingPersistenceMemoryImpl;

public class CreateActivity extends AppCompatActivity implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private TextView txtName;
    private TextView txtDescription;
    private String name;
    private String description;
    private Button btnCreate;
    private Button btnBack;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    private ShoppingPersistence shoppingPersistence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        txtName = (TextView) findViewById(R.id.txtName);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        shoppingPersistence = new ShoppingDatabase(this);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        btnCreate = (Button) findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                name = txtName.getText().toString();
                description = txtDescription.getText().toString();

                if (name.equals("") && description.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter name and description",
                            Toast.LENGTH_LONG).show();

                } else {
                    // Create object in database
                    ShoppingListItem shoppingItem = new ShoppingListItem();
                    shoppingItem.setBought(false);
                    shoppingItem.setName(name);
                    long newID = shoppingPersistence.createShoppingItem(shoppingItem);

                    // Put the object into the DataAPI
                    PutDataMapRequest putDataMapRequest = PutDataMapRequest.create
                            (DataStorageConstants.SHOPPING_PATH + "/"  + newID);
                    putDataMapRequest.getDataMap().putLong(DataStorageConstants.SHOPPING_ITEM_ID, newID);
                    putDataMapRequest.getDataMap().putString(DataStorageConstants.SHOPPING_ITEM_NAME, name);
                    putDataMapRequest.getDataMap().putBoolean(DataStorageConstants
                            .SHOPPING_ITEM_BOUGHT, false);

                    PutDataRequest request = putDataMapRequest.asPutDataRequest();
                    if (!mGoogleApiClient.isConnected()) {
                        return;
                    }
                    Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                            .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                                @Override
                                public void onResult(DataApi.DataItemResult dataItemResult) {
                                    if (!dataItemResult.getStatus().isSuccess()) {
                                        Log.e("CreateActivity", "ERROR: failed to putDataItem, " +
                                                "status code: "
                                                + dataItemResult.getStatus().getStatusCode());
                                    }
                                }
                            });
                }

                    Intent listActivity = new Intent(getApplicationContext(), ListActivity.class);
                    startActivity(listActivity);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if (!mResolvingError) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mResolvingError = false;
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    public void onConnectionSuspended(int cause) {
        // Empty placeholder
    }

    // Called when there was an error connecting the client to the service.
    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

    }
}

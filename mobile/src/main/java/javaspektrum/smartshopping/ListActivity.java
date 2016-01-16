package javaspektrum.smartshopping;


import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

import javaspektrum.common.ShoppingDatabase;
import javaspektrum.common.ShoppingListItem;
import javaspektrum.common.ShoppingPersistence;
import javaspektrum.common.ShoppingPersistenceMemoryImpl;

public class ListActivity extends AppCompatActivity implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Button btnCreate;
    private ShoppingListItemAdapter adapter;
    private List<ShoppingListItem> shoppingListItems;
    private ShoppingPersistence shoppingPersistence;
    private static final String COUNT_KEY = "com.example.key.count";

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Setup UI
        btnCreate = (Button) findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent createActivity = new Intent(getApplicationContext(), CreateActivity.class);
                startActivity(createActivity);
            }
        });

        // Setup NodeAPi and Listeners
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Load initial data from Persistence layer
        shoppingPersistence = new ShoppingDatabase(this);
        shoppingListItems = shoppingPersistence.getShoppingListItems();

        // Initialize UI with data
        adapter = new ShoppingListItemAdapter(this, shoppingListItems);
        ListView listView = (ListView) findViewById(R.id.shoppingItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView lv = (ListView) view;
                TextView row = (TextView)lv.getItemAtPosition(position);
                row.setPaintFlags(row.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                // TODO: update over DATA API to inform wearable
            }
        });
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        // TODO: update list item => new message from wearable

    }

    @Override
    public void onConnectionSuspended(int cause) {
    }

    // Called when there was an error connecting the client to the service.
    @Override
    public void onConnectionFailed(ConnectionResult result) {
    }

    @Override
    protected void onResume() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }
}

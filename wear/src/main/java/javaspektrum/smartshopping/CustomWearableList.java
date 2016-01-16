package javaspektrum.smartshopping;

import android.app.Activity;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WearableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

import javaspektrum.common.DataStorageConstants;
import javaspektrum.common.ShoppingListItem;
import javaspektrum.common.ShoppingPersistence;
import javaspektrum.common.ShoppingPersistenceMemoryImpl;

public class CustomWearableList extends Activity implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private TextView mHeader;
    private WearableAdapter shoppingAdpater;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    private ShoppingPersistence shoppingPersistence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // This is our list header
        mHeader = (TextView) findViewById(R.id.header);

        WearableListView wearableListView = (WearableListView) findViewById(R.id.wearable_List);

        shoppingPersistence = new ShoppingPersistenceMemoryImpl();

        this.shoppingAdpater = new WearableAdapter(this, shoppingPersistence.getShoppingListItems());
        wearableListView.setAdapter(this.shoppingAdpater);
        wearableListView.setClickListener(mClickListener);
        wearableListView.addOnScrollListener(mOnScrollListener);

        mGoogleApiClient.connect();
    }

    // Handle our Wearable List's click events
    private WearableListView.ClickListener mClickListener =
            new WearableListView.ClickListener() {
                @Override
                public void onClick(WearableListView.ViewHolder viewHolder) {
                    int position = viewHolder.getLayoutPosition();

                    // Update UI
                    TextView txtView = (TextView) viewHolder.itemView.findViewById(R.id.name);
                    txtView.setPaintFlags(txtView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    CircledImageView circledView = (CircledImageView) viewHolder.itemView.findViewById(R.id.circle);
                    circledView.setImageResource(R.drawable.ic_action_done);

                    // Update DataAPI
                    ShoppingListItem shoppingItem = shoppingPersistence.getShoppingListItems().get(position);
                    shoppingItem.setBought(true);
                    PutDataMapRequest putDataMapRequest = PutDataMapRequest.create
                            (DataStorageConstants.SHOPPING_PATH + "/" + shoppingItem.getId());
                    putDataMapRequest.getDataMap().putLong(DataStorageConstants.SHOPPING_ITEM_ID,
                            shoppingItem.getId());
                    putDataMapRequest.getDataMap().putBoolean(DataStorageConstants
                            .SHOPPING_ITEM_BOUGHT, true);
                    PutDataRequest request = putDataMapRequest.asPutDataRequest();
                    if (!mGoogleApiClient.isConnected()) {
                        return;
                    }
                    Wearable.DataApi.putDataItem(mGoogleApiClient, request);
                }

                @Override
                public void onTopEmptyRegionClick() {
                    Toast.makeText(CustomWearableList.this, "Top empty area tapped", Toast.LENGTH_SHORT).show();
                }
            };

    // The following code ensures that the title scrolls as the user scrolls up
    // or down the list
    private WearableListView.OnScrollListener mOnScrollListener =
            new WearableListView.OnScrollListener() {
                @Override
                public void onAbsoluteScrollChange(int i) {
                    // Only scroll the title up from its original base position
                    // and not down.
                    if (i > 0) {
                        mHeader.setY(-i);
                    }
                }

                @Override
                public void onScroll(int i) {
                    // Placeholder
                }

                @Override
                public void onScrollStateChanged(int i) {
                    // Placeholder
                }

                @Override
                public void onCentralPositionChanged(int i) {
                    // Placeholder
                }
            };


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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadAllShoppingItemsFromDataAPI();
            }
        });
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
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();

        for (DataEvent event : events) {
            final long id = DataMapItem.fromDataItem(event.getDataItem())
                    .getDataMap().getLong(DataStorageConstants.SHOPPING_ITEM_ID);
            final String name = DataMapItem.fromDataItem(event.getDataItem())
                    .getDataMap().getString(DataStorageConstants.SHOPPING_ITEM_NAME);
            final boolean bought = DataMapItem.fromDataItem(event.getDataItem())
                    .getDataMap().getBoolean(DataStorageConstants.SHOPPING_ITEM_BOUGHT);


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Update UI views here..
                    ShoppingListItem shoppingListItem = new ShoppingListItem(id, name, bought);
                    shoppingPersistence.getShoppingListItems().add(shoppingListItem);
                    shoppingAdpater.notifyDataSetChanged();
                }
            });
        }
    }

    private void loadAllShoppingItemsFromDataAPI() {
        shoppingPersistence.getShoppingListItems().clear();
        Uri uri = new Uri.Builder().scheme(PutDataRequest.WEAR_URI_SCHEME).path
                (DataStorageConstants.SHOPPING_PATH).build();

        Wearable.DataApi.getDataItems(mGoogleApiClient, uri, DataApi.FILTER_PREFIX)
                .setResultCallback(new ResultCallback<DataItemBuffer>() {
                    @Override
                    public void onResult(DataItemBuffer result) {
                        if (result.getStatus().isSuccess()) {
                            for (DataItem item : result) {
                                DataMap data = DataMapItem.fromDataItem(item).getDataMap();

                                ShoppingListItem shopItem = new ShoppingListItem();
                                shopItem.setId(data.getLong(DataStorageConstants.SHOPPING_ITEM_ID));
                                shopItem.setName(data.getString(DataStorageConstants.SHOPPING_ITEM_NAME));
                                shopItem.setBought(data.getBoolean(DataStorageConstants.SHOPPING_ITEM_BOUGHT));
                                shoppingPersistence.getShoppingListItems().add(shopItem);
                            }
                            result.release();
                            shoppingAdpater.notifyDataSetChanged();
                        }
                    }
                });
    }
}
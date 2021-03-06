package javaspektrum.smartshopping;


import android.content.Context;
import android.graphics.Paint;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javaspektrum.common.ShoppingListItem;

public class WearableAdapter extends WearableListView.Adapter {
    private List<ShoppingListItem> mItems;
    private final LayoutInflater mInflater;

    public WearableAdapter(Context context, List<ShoppingListItem> items) {
        mInflater = LayoutInflater.from(context);
        mItems = items;
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(
            ViewGroup viewGroup, int i) {
        return new ItemViewHolder(mInflater.inflate(javaspektrum.smartshopping.R.layout.list_item, null));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int position) {
        ShoppingListItem shopItem = mItems.get(position);

        // Create UI according actual Shopping Item state
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        TextView textView = itemViewHolder.mItemTextView;
        textView.setText(shopItem.getName());
        CircledImageView circledView = itemViewHolder.mCircledImageView;

        // Set item according bought state
        if (shopItem.isBought()) {
            circledView.setImageResource(R.drawable.ic_action_done);
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            circledView.setImageResource(R.drawable.shop);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private static class ItemViewHolder extends WearableListView.ViewHolder {
        private CircledImageView mCircledImageView;
        private TextView mItemTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mCircledImageView = (CircledImageView)
                    itemView.findViewById(R.id.circle);
            mItemTextView = (TextView) itemView.findViewById(R.id.name);
        }
    }
}
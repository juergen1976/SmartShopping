package javaspektrum.smartshopping;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javaspektrum.common.ShoppingListItem;

public class ShoppingListItemAdapter extends ArrayAdapter<ShoppingListItem> {

    public ShoppingListItemAdapter(Context context, List<ShoppingListItem> shoppingListItems) {
        super(context, 0, shoppingListItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ShoppingListItem listItem = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.shopping_item,
                    parent, false);
        }
        TextView txtName = (TextView) convertView.findViewById(R.id.txtName);
        txtName.setText(listItem.getName());
        return convertView;
    }
}
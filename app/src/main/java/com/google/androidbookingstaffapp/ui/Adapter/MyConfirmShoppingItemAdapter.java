package com.google.androidbookingstaffapp.ui.Adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.androidbookingstaffapp.R;
import com.google.androidbookingstaffapp.model.entities.CartItem;
import com.google.androidbookingstaffapp.model.entities.ShoppingItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class MyConfirmShoppingItemAdapter extends RecyclerView.Adapter<MyConfirmShoppingItemAdapter.viewHolder> {

    private List<CartItem> shoppingItemList;
    private Context context;


    public MyConfirmShoppingItemAdapter(List<CartItem> shoppingItemList, Context context) {
        this.shoppingItemList = shoppingItemList;
        this.context = context;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_confirm_shopping , parent , false);
        return new  MyConfirmShoppingItemAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        CartItem item = shoppingItemList.get(position);

        Picasso.get().load(item.getProductImage()).into(holder.itm_image);

        holder.itm_txt.setText(new StringBuilder(shoppingItemList.get(position).getProductName()).append(" x")
        .append(shoppingItemList.get(position).getProductQuantity()));
    }


    @Override
    public int getItemCount() {
        return shoppingItemList.size();
    }


    class viewHolder extends RecyclerView.ViewHolder{

        private ImageView itm_image;
        private TextView itm_txt;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            itm_image = itemView.findViewById(R.id.item_img);
            itm_txt = itemView.findViewById(R.id.txt_item);

        }
    }
}

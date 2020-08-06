package com.google.androidbookingstaffapp.ui.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.androidbookingstaffapp.R;
import com.google.androidbookingstaffapp.model.Common.Common;
import com.google.androidbookingstaffapp.model.Interface.IrecyclerItemSelectedListner;
import com.google.androidbookingstaffapp.model.Interface.IOnShoppingItemSelected;
import com.google.androidbookingstaffapp.model.entities.ShoppingItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AllShoppingItems extends RecyclerView.Adapter<AllShoppingItems.viewHolder> {

    private List<ShoppingItem> shoppingItemsList;
    private Context context;
    private IOnShoppingItemSelected OnShoppingItemSelected;

    public AllShoppingItems() {

    }

    public AllShoppingItems(List<ShoppingItem> shoppingItemsList, Context context , IOnShoppingItemSelected IOnShoppingItemSelected) {
        this.shoppingItemsList = shoppingItemsList;
        this.context = context;
        this.OnShoppingItemSelected = IOnShoppingItemSelected;


        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_shopping_item, parent, false);

        return new AllShoppingItems.viewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        final ShoppingItem items = shoppingItemsList.get(position);

        holder.name_shopping_item.setText(Common.formatShoppingItemName(items.getName()));

        Picasso.get().load(items.getImage()).into(holder.image_shopping_item);

        holder.price_shooping_item.setText(new StringBuilder(" $ ").append(items.getPrice()));

       holder.setIrecyclerItemSelectedListner(new IrecyclerItemSelectedListner() {
           @Override
           public void onItemSelectedListner(View view, int position) {
               OnShoppingItemSelected.onShoppingItemSelected(items);
           }
       });

    }


    @Override
    public int getItemCount() {
        return shoppingItemsList.size();
    }

    class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name_shopping_item;
        private ImageView image_shopping_item;
        private TextView price_shooping_item;

        private IrecyclerItemSelectedListner irecyclerItemSelectedListner;


        public viewHolder setIrecyclerItemSelectedListner(IrecyclerItemSelectedListner irecyclerItemSelectedListner) {
            this.irecyclerItemSelectedListner = irecyclerItemSelectedListner;
            return this;
        }


        public viewHolder(@NonNull View itemView) {

            super(itemView);

            itemView.setOnClickListener(this);

            name_shopping_item = itemView.findViewById(R.id.name_shopping_item);
            image_shopping_item = itemView.findViewById(R.id.img_shopping_item);
            price_shooping_item = itemView.findViewById(R.id.price_shopping_item);

        }



        @Override
        public void onClick(View v) {
         irecyclerItemSelectedListner.onItemSelectedListner(v , getAdapterPosition());
        }
    }
}




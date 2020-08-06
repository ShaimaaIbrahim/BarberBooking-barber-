package com.google.androidbookingstaffapp.ui.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.androidbookingstaffapp.model.Common.Common;
import com.google.androidbookingstaffapp.model.Interface.IrecyclerStateListner;
import com.google.androidbookingstaffapp.model.entities.City;
import com.google.androidbookingstaffapp.R;
import com.google.androidbookingstaffapp.ui.activities.SalonListActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyStateAdapter extends RecyclerView.Adapter<MyStateAdapter.viewHolder> {

    private List<City> cityList;
    private Context context;
    private int lastPosition= -1;


    public MyStateAdapter(List<City> cityList, Context context) {
        this.cityList = cityList;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_state , parent , false);

        return new MyStateAdapter.viewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        final City city = cityList.get(position);
        holder.txt_name.setText(city.getName());

        setAnimation(holder.itemView , position);

        holder.setIrecyclerStateListner(new IrecyclerStateListner() {
            @Override
            public void onItemSelected(View view, int position) {
                Common.state_name = city.getName();
                context.startActivity(new Intent(context , SalonListActivity.class));
            }
        });

    }

    private void setAnimation(View itemView , int position) {
      if (position > lastPosition){
          Animation animation = AnimationUtils.loadAnimation(context ,
                 android.R.anim.slide_in_left);

          itemView.setAnimation(animation);
          lastPosition= position;
      }
    }


    @Override
    public int getItemCount() {
        return cityList.size();
    }

    class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView txt_name;
        private IrecyclerStateListner irecyclerStateListner;

        public viewHolder setIrecyclerStateListner(IrecyclerStateListner irecyclerStateListner) {
            this.irecyclerStateListner = irecyclerStateListner;
            return this;
        }

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.txt_state_name);

            itemView.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            irecyclerStateListner.onItemSelected(v , getAdapterPosition());
        }
    }
}

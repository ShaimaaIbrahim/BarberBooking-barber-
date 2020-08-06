package com.google.androidbookingstaffapp.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.androidbookingstaffapp.R;
import com.google.androidbookingstaffapp.databinding.FragmentShoppingBinding;
import com.google.androidbookingstaffapp.model.Common.SpaceItemDecoration;
import com.google.androidbookingstaffapp.model.Interface.IsShoppingDataLoadListner;
import com.google.androidbookingstaffapp.model.Interface.IOnShoppingItemSelected;
import com.google.androidbookingstaffapp.model.entities.ShoppingItem;
import com.google.androidbookingstaffapp.ui.Adapter.AllShoppingItems;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

@SuppressLint("ValidFragment")
public class ShoppingItemFragmentI extends BottomSheetDialogFragment implements IsShoppingDataLoadListner, IOnShoppingItemSelected {

private IOnShoppingItemSelected callbackToActivity;
private IsShoppingDataLoadListner isShoppingDataLoadListner;

FragmentShoppingBinding binding;
private CollectionReference shoppingIremRef;
public static ShoppingItemFragmentI instance;



    public ShoppingItemFragmentI() {
    }

    public ShoppingItemFragmentI(IOnShoppingItemSelected IOnShoppingItemSelected) {
        this.callbackToActivity= IOnShoppingItemSelected;
    }

    public static ShoppingItemFragmentI getInstance(IOnShoppingItemSelected IOnShoppingItemSelected) {
        return instance==null? new ShoppingItemFragmentI(IOnShoppingItemSelected):instance ;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_shopping , container , false);


        isShoppingDataLoadListner=this;


        loadShoppingItems("Wax");

        binding.chipWax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedChip(binding.chipWax);
                loadShoppingItems("Wax");
            }
        });

        binding.bodyCare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedChip(binding.bodyCare);
                loadShoppingItems("BodyCare");
            }
        });

        binding.hairCare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedChip(binding.hairCare);
                loadShoppingItems("HairCare");
            }
        });

        binding.chipSpray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedChip(binding.chipSpray);
                loadShoppingItems("Spray");
            }
        });
        return binding.getRoot();

    }



    private void setSelectedChip(Chip chip) {
        for (int i = 0 ; i < binding.chipGroup.getChildCount() ; i++){

            Chip chipItem = (Chip) binding.chipGroup.getChildAt(i);

            if (chipItem.getId() != chip.getId()){

                chipItem.setChipBackgroundColorResource(android.R.color.darker_gray);
                chipItem.setTextColor(getResources().getColor(android.R.color.white));

            }else {

                chipItem.setChipBackgroundColorResource(android.R.color.holo_orange_dark);
                chipItem.setTextColor(getResources().getColor(android.R.color.black));

            }
        }
    }

    private void loadShoppingItems(String itemMenu) {
        shoppingIremRef = FirebaseFirestore.getInstance().collection("Shopping")
                .document(itemMenu).collection("items");


        shoppingIremRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){

                    List<ShoppingItem> shoppingItemsList = new ArrayList<>();


                    for (DocumentSnapshot snapshot : task.getResult()){

                        ShoppingItem shoppingItems = snapshot.toObject(ShoppingItem.class);

                        shoppingItems.setId(snapshot.getId());
                        shoppingItemsList.add(shoppingItems);
                    }
                    isShoppingDataLoadListner.ShoppingDataLoadSuccess(shoppingItemsList);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isShoppingDataLoadListner.ShoppingDataLoadFailed(e.getMessage());
            }
        });
    }


    @Override
    public void ShoppingDataLoadSuccess(List<ShoppingItem> shoppingItemList) {
        binding.recyclerItems.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity() , 2);
        binding.recyclerItems.setLayoutManager(layoutManager);
        binding.recyclerItems.addItemDecoration(new SpaceItemDecoration(8));
        binding.recyclerItems.setAdapter(new AllShoppingItems(shoppingItemList , getActivity() , this));
    }

    @Override
    public void ShoppingDataLoadFailed(String message) {
        Toast.makeText(getActivity() , message , Toast.LENGTH_LONG).show();

    }


    @Override
    public void onShoppingItemSelected(ShoppingItem shoppingItem) {

        callbackToActivity.onShoppingItemSelected(shoppingItem);

        Log.e("Sho" , "");
    }
}

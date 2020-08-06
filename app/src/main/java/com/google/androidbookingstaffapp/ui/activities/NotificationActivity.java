package com.google.androidbookingstaffapp.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.androidbookingstaffapp.R;
import com.google.androidbookingstaffapp.databinding.ActivityNotificationBinding;
import com.google.androidbookingstaffapp.model.Common.Common;
import com.google.androidbookingstaffapp.model.Common.SpaceItemDecoration;
import com.google.androidbookingstaffapp.model.Interface.INotificationLoadListner;
import com.google.androidbookingstaffapp.model.entities.MyNotification;
import com.google.androidbookingstaffapp.ui.Adapter.MyNotificationAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity implements INotificationLoadListner{

    private INotificationLoadListner iNotificationLoadListner;
    private CollectionReference collectionReference;
    private ActivityNotificationBinding binding;
    private int total_item =0 , last_visible_item;
    private boolean isLoading, isMaxData;
    private DocumentSnapshot finalDoc;
    private MyNotificationAdapter adapter;
    private List<MyNotification> firstList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = DataBindingUtil.setContentView(this , R.layout.activity_notification);

       iNotificationLoadListner=this;

       initView();
       loadNotification(null);

    }

    private void initView() {

        binding.recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this ,linearLayoutManager.getOrientation() ));
        binding.recyclerView.setLayoutManager(linearLayoutManager);

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                total_item = linearLayoutManager.getItemCount();
                last_visible_item= linearLayoutManager.getInitialPrefetchItemCount();

                if (!isLoading &&  total_item <= (last_visible_item + Common.MAX_NOTIFICTION_PER_LOAD)){
                     loadNotification(finalDoc);
                     isLoading=true;
                }

            }
        });
    }

    private void loadNotification(DocumentSnapshot lastDoc) {
       collectionReference = FirebaseFirestore.getInstance().collection("AllSalon").document(Common.state_name)
                .collection("Branch").document(Common.selectedSalon.getSalonId())
                .collection("Barber").document(Common.currentBarber.getBarberId())
                .collection("Notification");

        if (lastDoc==null){
            collectionReference.orderBy("serverTimestamp" , Query.Direction.DESCENDING)
                    .limit(Common.MAX_NOTIFICTION_PER_LOAD)
                    .get().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
            iNotificationLoadListner.OnNotificationLoadFailed(e.getMessage());
                }
            }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful()){

                        List<MyNotification> myNotifications =new ArrayList<>();

                        MyNotification myNotification = new MyNotification();
                        DocumentSnapshot finalDoc =null;

                        for (DocumentSnapshot notiSnapshot : task.getResult()){

                            myNotification = notiSnapshot.toObject(MyNotification.class);

                            myNotifications.add(myNotification);

                            finalDoc = notiSnapshot;
                        }
                        iNotificationLoadListner.OnNotificationLoadSuccess(myNotifications , finalDoc);
                    }

                }});
        }else {

            if (!isMaxData){
                collectionReference.orderBy("serverTimestamp" , Query.Direction.DESCENDING)
                        .startAfter(lastDoc)
                        .limit(Common.MAX_NOTIFICTION_PER_LOAD)
                        .get().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iNotificationLoadListner.OnNotificationLoadFailed(e.getMessage());
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){

                            List<MyNotification> myNotifications =new ArrayList<>();

                            MyNotification myNotification = new MyNotification();
                            DocumentSnapshot finalDoc =null;

                            for (DocumentSnapshot notiSnapshot : task.getResult()){

                                myNotification = notiSnapshot.toObject(MyNotification.class);

                                myNotifications.add(myNotification);

                                finalDoc = notiSnapshot;
                            }
                            iNotificationLoadListner.OnNotificationLoadSuccess(myNotifications , finalDoc);
                        }

                    }});
            }
        }
    }

    @Override
    public void OnNotificationLoadSuccess(List<MyNotification> myNotifications, DocumentSnapshot lastDecument) {

        if (lastDecument!=null){

            if (lastDecument.equals(finalDoc)){

                isMaxData=true;
            }else {
                finalDoc = lastDecument;

                isMaxData=false;
            }
        if (adapter ==null &&  firstList.size()==0){
            adapter= new MyNotificationAdapter(this , myNotifications);
            firstList=myNotifications;
        }else {

            if (!myNotifications.equals(firstList)){

                assert adapter != null;
                adapter.updateList(myNotifications);
            }
        }
            binding.recyclerView.setAdapter(adapter);
        } }

    @Override
    public void OnNotificationLoadFailed(String message) {

        Toast.makeText(NotificationActivity.this , message , Toast.LENGTH_LONG).show();
    }

}

package com.google.androidbookingstaffapp.ui.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.net.VpnService;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.androidbookingstaffapp.R;
import com.google.androidbookingstaffapp.databinding.TotalPriceFragmentBinding;
import com.google.androidbookingstaffapp.model.Common.Common;
import com.google.androidbookingstaffapp.model.Interface.IBottomSheetDialogDismissListner;
import com.google.androidbookingstaffapp.model.entities.BarberServices;
import com.google.androidbookingstaffapp.model.entities.CartItem;
import com.google.androidbookingstaffapp.model.entities.Invoice;
import com.google.androidbookingstaffapp.model.entities.ShoppingItem;
import com.google.androidbookingstaffapp.model.fcm.FcmResponce;
import com.google.androidbookingstaffapp.model.fcm.FcmSendData;
import com.google.androidbookingstaffapp.model.fcm.MyToken;
import com.google.androidbookingstaffapp.repository.fcmRetrofit.IFCMApi;
import com.google.androidbookingstaffapp.repository.fcmRetrofit.RetrofitClient;
import com.google.androidbookingstaffapp.ui.Adapter.MyConfirmShoppingItemAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TotalPriceFragment extends BottomSheetDialogFragment {

   private TotalPriceFragmentBinding binding;
   private HashSet<BarberServices> serviceAdded;
//   private List<ShoppingItem> shoppingItemList;
   private IFCMApi ifcmApi;
   private AlertDialog alertDialog;
   public static TotalPriceFragment instance;
   private IBottomSheetDialogDismissListner iBottomSheetDialogDismissListner;

   private String image_url;


    public TotalPriceFragment() {
    }

    @SuppressLint("ValidFragment")
    public TotalPriceFragment(IBottomSheetDialogDismissListner iBottomSheetDialogDismissListner) {
        this.iBottomSheetDialogDismissListner=iBottomSheetDialogDismissListner;
    }

    public static TotalPriceFragment getInstance(IBottomSheetDialogDismissListner iBottomSheetDialogDismissListner) {
        return instance==null ? new TotalPriceFragment(iBottomSheetDialogDismissListner):instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       binding = TotalPriceFragmentBinding.bind(inflater.inflate(R.layout.total_price_fragment , container , false));
        ifcmApi= RetrofitClient.getInstance().create(IFCMApi.class);

            init();
                 initView();

                 getBundle(getArguments());

                 setInformation();

       return binding.getRoot();
    }

    private void setInformation() {
        binding.txtBarberName.setText(Common.currentBarber.getName());
        binding.txtCustomerName.setText(Common.currentBookingInformation.getCustomerName());
        binding.txtPhone.setText(Common.currentBookingInformation.getCustomerPhone());
        binding.txtSalonName.setText(Common.currentBookingInformation.getSalonName());
        binding.txtTime.setText(Common.ConvertTimeSlotsToString(Common.currentBookingInformation.getSlot()));

        int i =0;
        if (serviceAdded.size() > 0){

            for (final BarberServices services : serviceAdded) {

                Chip chip = (Chip) getLayoutInflater().inflate(R.layout.chip_item, null);
                chip.setText(services.getName());
                chip.setTag(i);

                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        serviceAdded.remove((int) v.getTag());
                        binding.chipGroupService.removeView(v);


                    }
                });
                binding.chipGroupService.addView(chip);
                i++;
            }}

        if (Common.currentBookingInformation.getCartItemList()!=null){

            if (Common.currentBookingInformation.getCartItemList().size() > 0){

                MyConfirmShoppingItemAdapter adapter = new MyConfirmShoppingItemAdapter(Common.currentBookingInformation.getCartItemList() , getContext());
                binding.recyclerViewShopping.setAdapter(adapter);
            }

            calculatePrice();
        }
        }


    private double calculatePrice() {
        double price = Common.DEFAULT_PRICE;

        for (BarberServices barberServices : serviceAdded){
            price +=barberServices.getPrice();
        }
        if (Common.currentBookingInformation.getCartItemList()!=null){
            for (CartItem cartItem : Common.currentBookingInformation.getCartItemList()){
                price += (cartItem.getProductPrice()* cartItem.getProductQuantity());
            }
        }

 binding.txtPrice.setText(new StringBuilder(Common.MONEY_SIGN).append(price));

 return price;
    }

    private void getBundle(Bundle arguments) {

        this.serviceAdded = new Gson().fromJson(arguments.getString(Common.SERVICE_ADDED), new TypeToken<HashSet<BarberServices>>(){}.getType());

      //  this.shoppingItemList = new Gson().fromJson(arguments.getString(Common.SHOPPING_ITEMS) , new TypeToken<List<ShoppingItem>>(){}.getType());

       image_url = arguments.getString(Common.DOWNLOAD_IMAGE);
    }

    private void initView() {

        binding.recyclerViewShopping.setHasFixedSize(true);
        binding.recyclerViewShopping.setLayoutManager(new LinearLayoutManager(getContext() , LinearLayout.HORIZONTAL , false));


        //todo create invoice
        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.show();

                if (Common.state_name!=null &&  Common.currentBarber!=null && Common.selectedSalon!=null &&
                Common.simpleDateFormat!=null && Common.currentBookingInformation!=null && Common.bookingDate!=null ) {

                    final DocumentReference bookingSet = FirebaseFirestore.getInstance().collection("AllSalon").
                            document(Common.state_name).collection("Branch").
                            document(Common.selectedSalon.getSalonId()).collection("Barber").
                            document(Common.currentBarber.getBarberId()).
                            collection(Common.simpleDateFormat.format(Common.bookingDate.getTime()))
                            .document(Common.currentBookingInformation.getBookingId());

                    bookingSet.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {

                                if (task.getResult().exists()) {
                                    // update
                                    Map<String, Object> dataUpdate = new HashMap<>();
                                    dataUpdate.put("done", true);

                                    bookingSet.update(dataUpdate).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            alertDialog.dismiss();
                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            //todo
                                            //          if done create invoice
                                            createInvoice();
                                        }
                                    });
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            alertDialog.dismiss();
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

                }});

    }

    private void createInvoice() {


        if (Common.selectedSalon!=null && Common.state_name!=null && Common.currentBookingInformation!=null && Common.currentBarber!=null){
            CollectionReference invoiceRef =  FirebaseFirestore.getInstance().collection("AllSalon")
                    .document(Common.state_name).collection("Branch")
                    .document(Common.selectedSalon.getSalonId()).collection("Invoices");

            Invoice invoice = new Invoice();

            invoice.setBarberId(Common.currentBarber.getBarberId());
            invoice.setBarberName(Common.currentBarber.getName());

            invoice.setCustomerName(Common.currentBookingInformation.getCustomerName());
            invoice.setCustomerPhone(Common.currentBookingInformation.getCustomerPhone());

            invoice.setSalonId(Common.selectedSalon.getSalonId());
            invoice.setSalonName(Common.selectedSalon.getName());
            invoice.setSalonAddress(Common.selectedSalon.getAddress());

            invoice.setImagUrl(image_url);

            invoice.setBarberServices(new ArrayList<BarberServices>(serviceAdded));
            invoice.setShoppingItemList(Common.currentBookingInformation.getCartItemList());

            invoice.setFinalPrice(calculatePrice());

            invoiceRef.document().set(invoice)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext() , e.getMessage() , Toast.LENGTH_LONG).show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()){
                        sendNotificationUpdateToUser(Common.currentBookingInformation.getCustomerPhone());

                    }
                }
            });

        }


    }

    private void sendNotificationUpdateToUser(final String customerPhone) {

        alertDialog.show();

        FirebaseFirestore.getInstance().collection("Tokens")
                .whereEqualTo("userPhone" , customerPhone)
                .limit(1)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful() && task.getResult().size() > 0){

                    MyToken myToken = new MyToken();
                    for (DocumentSnapshot snapshot : task.getResult()){
                        myToken= snapshot.toObject(MyToken.class);
                    }
                    FcmSendData fcmSendData = new FcmSendData();


                    Map<String , String > dataSend = new HashMap<>();
                    dataSend.put("update_done" , "true" );

                    if (Common.selectedSalon!=null && Common.state_name!=null && Common.currentBookingInformation!=null && Common.currentBarber!=null) {

                        //todo show rating ========
                       dataSend.put(Common.RATING_STATE_KEY , Common.state_name);
                       dataSend.put(Common.RATING_SALON_ID , Common.selectedSalon.getSalonId());

                        dataSend.put(Common.RATING_SALON_NAME , Common.selectedSalon.getName());
                        dataSend.put(Common.RATING_BARBER_ID , Common.currentBarber.getBarberId());
                    }

                    fcmSendData.setTo(myToken.getToken());
                    fcmSendData.setData(dataSend);

                    ifcmApi.sendNotification(fcmSendData).subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.newThread()).subscribe(new Consumer<FcmResponce>() {
                        @Override
                        public void accept(FcmResponce fcmResponce) throws Exception {

                            alertDialog.dismiss();
                            dismiss();
                            iBottomSheetDialogDismissListner.onDismissBottomSheetDialog(true);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getContext() , throwable.getMessage() , Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void init() {

        alertDialog= new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        ifcmApi = RetrofitClient.getInstance().create(IFCMApi.class);

    }
}

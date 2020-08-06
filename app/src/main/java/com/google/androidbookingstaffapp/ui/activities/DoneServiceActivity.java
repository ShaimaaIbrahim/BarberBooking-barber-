package com.google.androidbookingstaffapp.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.androidbookingstaffapp.model.Common.Common;
import com.google.android.gms.tasks.Continuation;
import com.google.android.material.chip.Chip;
import com.google.androidbookingstaffapp.model.Interface.IBottomSheetDialogDismissListner;
import com.google.androidbookingstaffapp.model.Interface.IServiceLoadListner;
import com.google.androidbookingstaffapp.model.Interface.IOnShoppingItemSelected;
import com.google.androidbookingstaffapp.model.entities.BarberServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.androidbookingstaffapp.R;
import com.google.androidbookingstaffapp.databinding.ActivityDoneServiceBinding;

import com.google.androidbookingstaffapp.model.entities.CartItem;
import com.google.androidbookingstaffapp.model.entities.ShoppingItem;
import com.google.androidbookingstaffapp.ui.fragments.ShoppingItemFragmentI;
import com.google.androidbookingstaffapp.ui.fragments.TotalPriceFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class DoneServiceActivity extends AppCompatActivity implements IServiceLoadListner, IOnShoppingItemSelected , IBottomSheetDialogDismissListner {

    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private ActivityDoneServiceBinding binding;
    private IServiceLoadListner iServiceLoadListner;
    private AlertDialog alertDialog;


    private HashSet <BarberServices> servicesAdded= new HashSet<>();
    private LayoutInflater inflater;

    private Uri fileUri;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = DataBindingUtil.setContentView(this , R.layout.activity_done_service);


       iServiceLoadListner=this;
       inflater = LayoutInflater.from(this);

       setCustomerInformation();
       // todo add items shopping
       initView();

       alertDialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();


       loadBarberServices();

    }

    private void initView() {

        getSupportActionBar().setTitle("Checkout");

        binding.rdWithPic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    binding.imgCustomerHair.setVisibility(View.VISIBLE);
                    binding.btnFinish.setEnabled(false);
                }
            }
        });

        binding.rdWithoutPic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    binding.imgCustomerHair.setVisibility(View.GONE);
                    binding.btnFinish.setEnabled(true);
                }
            }
        });

        // todo adding fragment dialoge to an activity

        binding.addShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShoppingItemFragmentI shoppingFragment = ShoppingItemFragmentI.getInstance(DoneServiceActivity.this);
                shoppingFragment.show(getSupportFragmentManager() , "Shopping");
            }
        });

        //todo capture image from device and get its uri
        binding.imgCustomerHair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                fileUri = getOutPutMediaFileUri();
                intent.putExtra(MediaStore.EXTRA_OUTPUT , fileUri);

                startActivityForResult(intent , MY_CAMERA_REQUEST_CODE);
            }
        });

        // todo upload picture file to firestore
        binding.btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.rdWithoutPic.isChecked()){
                    alertDialog.dismiss();

                    TotalPriceFragment totalPriceFragment = TotalPriceFragment.getInstance(DoneServiceActivity.this);

                    //todo pass data to fragments using bundle
                    // todo open fragment buttomsheet dialog
                    Bundle bundle = new Bundle();
                    bundle.putString(Common.SERVICE_ADDED , new Gson().toJson(servicesAdded));

                    totalPriceFragment.setArguments(bundle);
                    totalPriceFragment.show(getSupportFragmentManager() , "Shopping Price");
                }else {

                    uploadPicture(fileUri);
                }
            }
        });
    }

    private void uploadPicture(Uri fileUri) {

        if (fileUri!=null){
            alertDialog.show();

            String fileName = Common.getFileName(getContentResolver() , fileUri);
            String path = new StringBuilder("Customer_Pictures").append(fileName).toString();

             storageReference = FirebaseStorage.getInstance().getReference("path");

            UploadTask uploadTask = storageReference.putFile(fileUri);
            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()){

                        Toast.makeText(DoneServiceActivity.this , "Failed to Upload file " , Toast.LENGTH_LONG).show();

                    }
                        return storageReference.getDownloadUrl();
                    }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()){

                        String url = task.getResult().toString().substring(0 , task.getResult().toString().indexOf("&token"));
                        Log.e("DOWNLOADABLE_LINK" , url);

                        Paper.init(DoneServiceActivity.this);
                        Paper.book().write(Common.IMAGE_URL , url);
                        // generate final billing here

                        alertDialog.dismiss();

                        TotalPriceFragment totalPriceFragment = TotalPriceFragment.getInstance(DoneServiceActivity.this);

                        //todo pass data to fragments using bundle
                        // todo open fragment buttomsheet dialog
                          Bundle bundle = new Bundle();
                          bundle.putString(Common.SERVICE_ADDED , new Gson().toJson(servicesAdded));
                       //   bundle.putString(Common.SHOPPING_ITEMS , new Gson().toJson(shoppingItemList));
                          bundle.putString(Common.DOWNLOAD_IMAGE , url);
                          totalPriceFragment.setArguments(bundle);
                          totalPriceFragment.show(getSupportFragmentManager() , "Shopping Price");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DoneServiceActivity.this , e.getMessage() , Toast.LENGTH_LONG).show();
                    alertDialog.dismiss();

                }
            });

        }else {
        Toast.makeText(this , "Empty File" , Toast.LENGTH_LONG).show();
        }
    }

    private Uri getOutPutMediaFileUri() {
         return Uri.fromFile(getOutPutMediaFile());
    }

    private File getOutPutMediaFile() {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) , "MyCameraApp");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdir())

                return null;
        }

        String time_stamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + time_stamp
        + "_" + new Random().nextInt() + ".jpg");

        return mediaFile;
    }


    private void loadBarberServices() {

        alertDialog.show();

        FirebaseFirestore.getInstance().collection("AllSalon")
                .document(Common.state_name).collection("Branch").document(Common.selectedSalon.getSalonId())
                .collection("Services").get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                 iServiceLoadListner.OnLoadServiceFailed(e.getMessage());

            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<BarberServices> barberServices = new ArrayList<>();

                    for (DocumentSnapshot snapshot : task.getResult()) {

                       BarberServices service = snapshot.toObject(BarberServices.class);
                       barberServices.add(service);
                    }
                iServiceLoadListner.OnLoadServiceSuccess(barberServices);
                }

            }
        });

    }

    private void setCustomerInformation() {

        if (Common.currentBookingInformation!=null){
            binding.txtCustomerName.setText(Common.currentBookingInformation.getCustomerName());
            binding.txtCustomerPhone.setText(Common.currentBookingInformation.getCustomerPhone());
        }


    }


    @Override
    public void OnLoadServiceSuccess(final List<BarberServices> barberServicesList) {


        final List<String >nameSrevices = new ArrayList<>();

       // do sort alpha_beta
       Collections.sort(barberServicesList, new Comparator<BarberServices>() {
            @Override
            public int compare(BarberServices o1, BarberServices o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        for (BarberServices service : barberServicesList){

            nameSrevices.add(service.getName());
        }
        // todo working for autocomplete text and set it in chips

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this
                , android.R.layout.select_dialog_item, nameSrevices);

        binding.editService.setThreshold(1); // start working from first character
        binding.editService.setAdapter(arrayAdapter);

       binding.editService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int index = nameSrevices.indexOf(binding.editService.getText().toString().trim());

                if (!servicesAdded.contains(barberServicesList.get(index))){

                    servicesAdded.add(barberServicesList.get(index));

                    final Chip item = (Chip) inflater.inflate(R.layout.chip_item , null);
                    item.setText(binding.editService.getText().toString());

                    item.setTag(index);

                    binding.editService.setText("");

                    item.setOnCloseIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            binding.chipGroupService.removeView(v);
                            servicesAdded.remove((int) item.getTag());
                        }
                    });
                    binding.chipGroupService.addView(item);
                }else {

                    binding.editService.setText("");
                }
            }
        });
       loadExtraItems();
    }

    @Override
    public void OnLoadServiceFailed(String message) {
        if (alertDialog.isShowing())   alertDialog.dismiss();

        Toast.makeText(DoneServiceActivity.this , message , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onShoppingItemSelected(final ShoppingItem shoppingItem) {

      /*  // todo adding this item to chipGroup
         shoppingItemList.add(shoppingItem);
         Log.e("ShoppingItems" , shoppingItemList.size() + " ");*/

        CartItem cartItem = new CartItem();
        cartItem.setProductId(shoppingItem.getId());
        cartItem.setProductImage(shoppingItem.getImage());
        cartItem.setProductName(shoppingItem.getName());
        cartItem.setProductPrice(shoppingItem.getPrice());
        cartItem.setProductQuantity(1);
        cartItem.setUserPhone(Common.currentBookingInformation.getCustomerPhone());

if (Common.currentBookingInformation.getCartItemList()==null){
    Common.currentBookingInformation.setCartItemList(new ArrayList<CartItem>());
}

boolean flag = false;

for (int i =0 ; i< Common.currentBookingInformation.getCartItemList().size() ; i++){

    if (Common.currentBookingInformation.getCartItemList().get(i).getProductName().equals(shoppingItem.getName())){
        flag = true;
        CartItem itemUpdate = Common.currentBookingInformation.getCartItemList().get(i)  ;
        itemUpdate.setProductQuantity(itemUpdate.getProductQuantity()+1);
        Common.currentBookingInformation.getCartItemList().set(i , itemUpdate); // update item in list

    }
}

if (!flag){
    Common.currentBookingInformation.getCartItemList().add(cartItem) ;
    final Chip item = (Chip) inflater.inflate(R.layout.chip_item , null);
    item.setText(cartItem.getProductName());

    item.setTag(Common.currentBookingInformation.getCartItemList().indexOf(cartItem));

    item.setOnCloseIconClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            binding.chipGroupShopping.removeView(v);
            Common.currentBookingInformation.getCartItemList().remove( (int) item.getTag());

        }
    });
    binding.chipGroupShopping.addView(item);
}else {
    binding.chipGroupShopping.removeAllViews();
    loadExtraItems();
}

     


    }

    private void loadExtraItems() {

        if (Common.currentBookingInformation.getCartItemList()!=null){
            for (CartItem cartItem : Common.currentBookingInformation.getCartItemList()){

                final Chip item = (Chip) inflater.inflate(R.layout.chip_item , null);

                item.setText(new StringBuilder(cartItem.getProductName()).append(" x")
                .append(cartItem.getProductQuantity()));


                item.setTag(Common.currentBookingInformation.getCartItemList().indexOf(cartItem));

                item.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        binding.chipGroupShopping.removeView(v);
                        Common.currentBookingInformation.getCartItemList().remove( (int) item.getTag());

                    }
                });
                binding.chipGroupShopping.addView(item);
            }
            }
        alertDialog.dismiss();
        }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode , resultCode , data);

        if (requestCode==MY_CAMERA_REQUEST_CODE){

            if (resultCode==RESULT_OK){

                Bitmap bitmap = null;
                ExifInterface ef= null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver() , fileUri);


                        ef = new ExifInterface(getContentResolver().openInputStream(fileUri));

                        int orientation = ef.getAttributeInt(ExifInterface.TAG_ORIENTATION , ExifInterface.ORIENTATION_UNDEFINED);

                        Bitmap rotateBitmape = null ;

                        switch (orientation){

                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotateBitmape = rotateImage(bitmap , 90);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotateBitmape = rotateImage(bitmap , 180);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotateBitmape = rotateImage(bitmap , 270);
                                break;

                            case ExifInterface.ORIENTATION_NORMAL:
                                default:
                                    rotateBitmape =bitmap;
                                break;
                        }

                        binding.imgCustomerHair.setImageBitmap(rotateBitmape);
                        binding.btnFinish.setEnabled(true);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private Bitmap rotateImage(Bitmap bitmap, int i) {

        Matrix matrix = new Matrix();
        matrix.postRotate(i);
        return Bitmap.createBitmap(bitmap , 0 , 0 , bitmap.getWidth() , bitmap.getHeight() , matrix , true);

    }

    @Override
    public void onDismissBottomSheetDialog(boolean fromBottom) {
      if (fromBottom) finish();
    }
}

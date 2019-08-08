package com.liadi.oluwaseun.travelmantics;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;
import com.liadi.oluwaseun.travelmantics.models.TravelDeal;
import com.liadi.oluwaseun.travelmantics.repository.TravelDealRepository;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AdminActivity extends AppCompatActivity {

    private TextInputEditText titleEt;

    private TextInputEditText descriptionEt;

    private TextInputEditText priceEt;

    private Button mUploadImageBtn;

    private ImageView mSelectedImage;

    private static final String EDIT_DEAL = "com.liadi.oluwaseun.travelmantics.edit_deal";

    public static final int REQUEST_IMAGE_GET = 100;

    private TravelDeal mTravelDeal;

    private String key;

    private static final String TAG = "AdminActivity";

    private boolean isInsertTravelDeal = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Travelmantics");


        titleEt = findViewById(R.id.deal_title);
        descriptionEt = findViewById(R.id.deal_description);
        priceEt = findViewById(R.id.deal_price);
        mUploadImageBtn = findViewById(R.id.deal_image);
        mSelectedImage = findViewById(R.id.selected_image);

        mTravelDeal = (TravelDeal) getIntent().getSerializableExtra(EDIT_DEAL);

        if (TravelDealRepository.getReference(this).getAdminState()) {
            if (mTravelDeal != null) {
                isInsertTravelDeal =false;
                key = mTravelDeal.getId();
                titleEt.setText(mTravelDeal.getTitle());
                descriptionEt.setText(mTravelDeal.getDescription());
                Picasso.get().load(mTravelDeal.getImageUrl()).into(mSelectedImage);
                priceEt.setText(mTravelDeal.getPrice());
            }
            else {
                mTravelDeal = new TravelDeal();
            }
        }
        else {
            titleEt.setText(mTravelDeal.getTitle());
            descriptionEt.setText(mTravelDeal.getDescription());
            Picasso.get().load(mTravelDeal.getImageUrl()).into(mSelectedImage);
            priceEt.setText(mTravelDeal.getPrice());

            titleEt.setEnabled(false);
            descriptionEt.setEnabled(false);
            priceEt.setEnabled(false);
            mUploadImageBtn.setVisibility(View.INVISIBLE);
        }



        mUploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    intent = intent.createChooser(intent,"Insert Picture");
                    startActivityForResult(intent, REQUEST_IMAGE_GET);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(TravelDealRepository.getReference(this).getAdminState()) {
            if (isInsertTravelDeal) {
                menu.findItem(R.id.delete).setVisible(false);
            }
        }
        else {
            menu.findItem(R.id.delete).setVisible(false);
            menu.findItem(R.id.save).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.delete) {

            TravelDealRepository.getReference(this).deleteTravelDeal(key);
            TravelDealRepository.getReference(this).deleteImage(mTravelDeal.getImageName());
            backtoList();
            return true;
        }

        else if(id == R.id.save) {
            if (isInsertTravelDeal) {
                mTravelDeal.setTitle(titleEt.getText().toString());
                mTravelDeal.setDescription(descriptionEt.getText().toString());
                mTravelDeal.setPrice(priceEt.getText().toString());
                TravelDealRepository.getReference(this).insertTravelDeal(mTravelDeal);
            }
            else {
                mTravelDeal.setTitle(titleEt.getText().toString());
                mTravelDeal.setDescription(descriptionEt.getText().toString());
                mTravelDeal.setPrice(priceEt.getText().toString());
                TravelDealRepository.getReference(this).editTravelDeal(key, mTravelDeal);
            }

            backtoList();
            return  true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GET && data.getData() != null ) {
                Uri imageUri = data.getData();
                mSelectedImage.setImageURI(imageUri);
                TravelDealRepository.getReference(this).savePicture(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                mTravelDeal.setImageUrl(uri.toString());
                                mTravelDeal.setImageName(taskSnapshot.getStorage().getName());
                                Log.i(TAG, "onSuccess: "+ mTravelDeal.getImageUrl());
                                Log.i(TAG, "onSuccess: "+ mTravelDeal.getImageName());
                            }
                        });
                    }
                });
            }
        }
    }

    private void  backtoList() {
        Intent i = new Intent(this, UserActivity.class);
        startActivity(i);
        finish();
    }

    public static Intent createAdminIntent(Context context, TravelDeal deal) {
        Intent intent = new Intent(context, AdminActivity.class);
        intent.putExtra(EDIT_DEAL,deal);
        return intent;
    }

}

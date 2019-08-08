package com.liadi.oluwaseun.travelmantics;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.liadi.oluwaseun.travelmantics.adapter.TravelDealAdapter;
import com.liadi.oluwaseun.travelmantics.models.TravelDeal;
import com.liadi.oluwaseun.travelmantics.repository.TravelDealRepository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Arrays;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    private RecyclerView mRecyclerview;

    private TravelDealAdapter mAdaptor;

    public static final int RC_SIGN_IN = 5;

    private static final String TAG = "UserActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Travelmantics");

        mRecyclerview = findViewById(R.id.travel_recycler_vw);
    }

    @Override
    protected void onResume() {
        super.onResume();

        TravelDealRepository.getReference(this).attachAuthStateListener();

        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mAdaptor = new TravelDealAdapter(this);
        mRecyclerview.setAdapter(mAdaptor);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem insertItem  = menu.findItem(R.id.insert_deal);
        if (TravelDealRepository.getReference(this).getAdminState() == true) {
            insertItem.setVisible(true);
        }
        else
            insertItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.logout) {

            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                           TravelDealRepository.getReference(UserActivity.this).attachAuthStateListener();
                        }
                    });
            TravelDealRepository.getReference(this).dettachAuthStateListener();
            TravelDealRepository.getReference(this).removeChildListener();
            TravelDealRepository.getReference(this).isAdmin = false;
            return true;
        }
        else if (id == R.id.insert_deal) {
            Intent i = new Intent(this, AdminActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TravelDealRepository.getReference(this).dettachAuthStateListener();
        TravelDealRepository.getReference(this).removeChildListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            if (resultCode == RESULT_OK) {
                invalidateOptionsMenu();
            }
        }
    }

}

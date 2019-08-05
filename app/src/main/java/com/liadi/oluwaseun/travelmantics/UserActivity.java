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

    private FirebaseAuth mAuth;

    private static final int RC_SIGN_IN = 5;

    private static final String TAG = "UserActivity";
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //mAuth = FirebaseAuth.getInstance();

        mRecyclerview = findViewById(R.id.travel_recycler_vw);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            startAuthentication();
        }
        Log.i(TAG, "onCreate: ");
        //TravelDealRepository.getReference().checkAdmin(firebaseUser.getUid(),this);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser == null) {
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//            finish();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mAdaptor = new TravelDealAdapter(this);
        mRecyclerview.setAdapter(mAdaptor);
//        if (TravelDealRepository.getReference().getAdminState()) {
//            invalidateOptionsMenu();
//        }
        //Log.i(TAG, "onResume: "+ firebaseUser.getUid());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (TravelDealRepository.getReference().getAdminState()) {
            menu.findItem(R.id.insert_deal).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

//        if (TravelDealRepository.getReference().getAdminState()) {
//            menu.findItem(R.id.insert_deal).setVisible(true);
//        }
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
                            startAuthentication();
                        }
                    });
            return true;
        }
        else if (id == R.id.insert_deal) {
            Intent i = new Intent(this, AdminActivity.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TravelDealRepository.getReference().removeChildListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                TravelDealRepository.getReference().checkAdmin(firebaseUser.getUid(),this);
                // ...
            }
        }
    }

    private void startAuthentication() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }
}

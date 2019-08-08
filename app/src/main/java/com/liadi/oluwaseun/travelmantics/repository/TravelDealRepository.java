package com.liadi.oluwaseun.travelmantics.repository;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.liadi.oluwaseun.travelmantics.UserActivity;
import com.liadi.oluwaseun.travelmantics.adapter.TravelDealAdapter;
import com.liadi.oluwaseun.travelmantics.models.TravelDeal;

import java.util.Arrays;
import java.util.List;

public class TravelDealRepository {

    private FirebaseDatabase database;

    private DatabaseReference myRef;

    private FirebaseStorage storage;

    private StorageReference mStorageRef;

    private static TravelDealRepository travelDealRepository;

    private ChildEventListener readChildEventListener;

    private ChildEventListener isAdminChildEventListener;

    private static final String TAG = "TravelDealRepository";

    private FirebaseAuth mFirebaseAuth;

    private Activity mActivity;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public boolean isAdmin;

    private TravelDealRepository(Activity activity) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReference("deals_picture");
        mActivity = activity;

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    startAuthentication();
                } else{
                  String userId = firebaseAuth.getUid();
                  checkAdmin(userId,(UserActivity ) mActivity);
                }

            }
        };
    }

    public static TravelDealRepository getReference(Activity activity) {
        if (travelDealRepository == null) {
            travelDealRepository = new TravelDealRepository(activity);
        }
        return travelDealRepository;
    }

    public void insertTravelDeal(TravelDeal travelDeal) {
        myRef.child("traveldeals").push().setValue(travelDeal);
    }

    public void readTravelDeals(final List<TravelDeal> travelDeals, final TravelDealAdapter adapter) {

        readChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TravelDeal travelDeal = dataSnapshot.getValue(TravelDeal.class);
                travelDeal.setId(dataSnapshot.getKey());
                travelDeals.add(travelDeal);
                Log.i(TAG, "onChildAdded: " + travelDeal.getTitle());
                adapter.notifyItemInserted(travelDeals.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        myRef.child("traveldeals").addChildEventListener(readChildEventListener);
    }

    public void removeChildListener() {
        myRef.child("traveldeals").removeEventListener(readChildEventListener);
        if (isAdmin) {
            myRef.child("administrator").removeEventListener(isAdminChildEventListener);
        }

    }

    public void editTravelDeal(String key, TravelDeal travelDeal) {
        myRef.child("traveldeals").child(key).setValue(travelDeal);
    }

    public void deleteTravelDeal(String key) {
        myRef.child("traveldeals").child(key).removeValue();
    }

    public UploadTask savePicture(final Uri imageUri) {
       return mStorageRef.child(imageUri.getLastPathSegment()).putFile(imageUri);
    }

    public void deleteImage(String imageName) {
        mStorageRef.child(imageName).delete();
    }

    public void checkAdmin(String uId, final UserActivity activity) {

        isAdmin = false;
        isAdminChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    isAdmin = true;
                    activity.invalidateOptionsMenu();
                Log.i(TAG, "onChildAdded: isAdmin "+ "user is admin");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        myRef.child("administrator").child(uId).addChildEventListener(isAdminChildEventListener);
    }

    public void attachAuthStateListener() {
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public void dettachAuthStateListener() {
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    public boolean getAdminState() {
        Log.i(TAG, "getAdminState: "+ isAdmin);
        return isAdmin;

    }

    public void startAuthentication() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
        mActivity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                UserActivity.RC_SIGN_IN);
    }
}

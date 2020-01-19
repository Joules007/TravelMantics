package ng.com.ajsprojects.travelmantics;

import android.util.Log;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    private static final int RC_SIGN_IN = 123;
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    public static ArrayList<TravelDeal> mDeals;
    public static FirebaseStorage mStorage;
    public static StorageReference mStorageRef;
    public static boolean isAdmin;
    private static FirebaseUtil firebaseUtil;
    private static FirebaseAuth mFirebaseAuth;
    private static  FirebaseAuth.AuthStateListener mAuthListener;
    private static ListActivity caller;
    private FirebaseUtil(){}

    public static void openFbReference(String ref, final ListActivity callerActivity) {
        if (firebaseUtil == null) {
            firebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (mFirebaseAuth.getCurrentUser() == null){
                        FirebaseUtil.signIn();
                    }
                    else{
                        String userId = firebaseAuth.getUid();
                        checkAdmin(userId);
                    }
                    Toast.makeText(callerActivity.getBaseContext(), "Welcome back!", Toast.LENGTH_LONG).show();
                }
            };
            connectStorage();
        }
        mDeals = new ArrayList<TravelDeal>();
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }

    public static void signIn() {
        List<AuthUI.IdpConfig> provider = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build());

        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(provider)
                        .setLogo(R.drawable.logo_a)
                        .setTosAndPrivacyPolicyUrls("https://gmail.com/terms.html",
                        "https://gmail.com/privacy.html")
                        .build(),
                RC_SIGN_IN);
    }

    private static void checkAdmin(String uid) {
        FirebaseUtil.isAdmin=false;
        DatabaseReference ref = mFirebaseDatabase.getReference().child("administrators")
                .child(uid);
        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FirebaseUtil.isAdmin=true;
                Log.d("Admin", "You are an Administrator");
                caller.showMenu();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot,@Nullable String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot,@Nullable String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.addChildEventListener(listener);
    }


    public static void attachListener(){
        mFirebaseAuth.addAuthStateListener(mAuthListener);

    }

    public static void detachListener(){
        mFirebaseAuth.removeAuthStateListener(mAuthListener);

    }

    public  static void connectStorage(){
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference().child("deals_pictures");
    }
}

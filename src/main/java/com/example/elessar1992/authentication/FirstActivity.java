package com.example.elessar1992.authentication;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetDialog;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.elessar1992.authentication.Check.Check;

import com.example.elessar1992.authentication.Fragment.EventFragment;
import com.example.elessar1992.authentication.Fragment.FirstFragment;
import android.support.v4.app.Fragment;


import com.example.elessar1992.authentication.Fragment.EventFragment;
import com.example.elessar1992.authentication.Fragment.FirstFragment;
import com.example.elessar1992.authentication.Model.User;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;



public class FirstActivity extends AppCompatActivity {

    @BindView(R.id.buttom_nav)
    BottomNavigationView bottomNavigationView;

    BottomSheetDialog bottomSheetDialog;

    CollectionReference userRef;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        ButterKnife.bind(FirstActivity.this);
        userRef = FirebaseFirestore.getInstance().collection("User");
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();


        //checking intent, if login is true, having full access
        //if login is false, just let user to see event
        if (getIntent() != null)
        {
            boolean isLogin = getIntent().getBooleanExtra(Check.IS_LOGIN, false);
            if (isLogin) {
                dialog.show();

                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(final Account account) {
                        if (account != null) {
                            DocumentReference currentUser = userRef.document(account.getPhoneNumber().toString());
                            currentUser.get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot userSnapShot = task.getResult();
                                                if (!userSnapShot.exists())
                                                    showUpdateDialog(account.getPhoneNumber().toString());

                                            }
                                        }
                                    });
                        }

                    }

                    @Override
                    public void onError(AccountKitError accountKitError) {
                        Log.e("AccountKit",accountKitError.toString());
                        //Toast.makeText(FirstActivity.this, "" +accountKitError.getErrorType().getMessage().toString());
                    }
                });
            }
        }


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            Fragment fragment = null;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                if(menuItem.getItemId() == R.id.action_shopping)
                    fragment = new Fragment();
                else if (menuItem.getItemId() == R.id.action_home)
                    fragment = new Fragment();

                return loadFragment(fragment);
            }
        });
        /*bottomNavigationView.setOnNavigationItemselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            Fragment fragment = null;
            @Override
            public boolean onNavigationItemselected(@NonNull MenuItem menuItem)
            {
                if(menuItem.getItemId() == R.id.action_home)
                    fragment = new FirstFragment();
                else if(menuItem.getItemId() == R.id.action_shopping)
                    fragment = new EventFragment();
                return loadFragment(fragment);
            }
        });*/

    }

    private boolean loadFragment(Fragment fragment)
    {
        if(fragment != null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void showUpdateDialog(final String phoneNumber)
    {
        if(dialog.isShowing())
            dialog.dismiss();
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setTitle("One more step");
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setCancelable(false);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_update_info,null);

        Button btn_update = (Button) sheetView.findViewById(R.id.button_update);
        final TextInputEditText edit_name = (TextInputEditText) sheetView.findViewById(R.id.edit_name);
        final TextInputEditText edit_address = (TextInputEditText) sheetView.findViewById(R.id.edit_address);

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User(edit_name.getText().toString(),
                        edit_address.getText().toString(),
                        phoneNumber);
                userRef.document(phoneNumber).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        bottomSheetDialog.dismiss();
                        Toast.makeText(FirstActivity.this,"Thanks",Toast.LENGTH_SHORT);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        bottomSheetDialog.dismiss();
                        Toast.makeText(FirstActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT);
                    }
                });
            }
        });

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }

}



package com.example.androiduberclone;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.androiduberclone.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn, btnRegister;
    RelativeLayout rootLayout;

    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // before setContentView
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Arkhip_font.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_main);

        // Init Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users =  db.getReference("Users");

        // Init view
        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);

        // Event
        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showRegisterDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginDialog();
            }
        });
    }

    private void showLoginDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SIGN IN");
        dialog.setMessage("Please use your email to sign in");

        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.layout_login,null);

        final MaterialEditText editMail = login_layout.findViewById(R.id.editEmail);
        final MaterialEditText editPassword = login_layout.findViewById(R.id.editPassword);



        dialog.setView(login_layout);

        // Set button
        dialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                // Check validation
                if(TextUtils.isEmpty(editMail.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                if(TextUtils.isEmpty(editPassword.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter password", Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                if(editPassword.getText().toString().length() < 6) {
                    Snackbar.make(rootLayout, "Password is too short! At least 6 characters.", Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }

                // Login
                auth.signInWithEmailAndPassword(editMail.getText().toString(), editPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                startActivity(new Intent(MainActivity.this, Welcome.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(rootLayout, "Failed " + e.getMessage(), Snackbar.LENGTH_LONG)
                                .show();
                    }
                });
            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        dialog.show();

    }

    private void showRegisterDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("REGISTER");
        dialog.setMessage("Please use your email to register");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.layout_register,null);

        final MaterialEditText editMail = register_layout.findViewById(R.id.editEmail);
        final MaterialEditText editPassword = register_layout.findViewById(R.id.editPassword);
        final MaterialEditText editName = register_layout.findViewById(R.id.editName);
        final MaterialEditText editPhone = register_layout.findViewById(R.id.editPhone);


        dialog.setView(register_layout);

        // Set button
        dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                // Check validation
                if(TextUtils.isEmpty(editMail.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                if(TextUtils.isEmpty(editPhone.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter phone number", Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                if(TextUtils.isEmpty(editPassword.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter password", Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                if(editPassword.getText().toString().length() < 6) {
                    Snackbar.make(rootLayout, "Password is too short! At least 6 characters.", Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                if(TextUtils.isEmpty(editName.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter name", Snackbar.LENGTH_LONG)
                            .show();
                }

                // Register new user
                auth.createUserWithEmailAndPassword(editMail.getText().toString(), editPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                User user = new User();
                                user.setEmail(editMail.getText().toString());
                                user.setName(editName.getText().toString());
                                user.setPhone(editPhone.getText().toString());
                                user.setPassword(editPassword.getText().toString());

                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(rootLayout,"Register successfully!", Snackbar.LENGTH_LONG)
                                                        .show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar.make(rootLayout,"Registration failed! " + e.getMessage(), Snackbar.LENGTH_LONG)
                                                        .show();                                            }
                                        });
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(rootLayout,"Failed! " + e.getMessage(), Snackbar.LENGTH_LONG)
                                .show();
                    }
                });
            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}

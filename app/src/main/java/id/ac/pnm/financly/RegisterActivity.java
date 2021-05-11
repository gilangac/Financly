package id.ac.pnm.financly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore fStore;

    EditText txtNama,txtUsername, txtPassword,txtPassword2;
    Button btnRegister;
    TextView btnLogin;
    String userID;
    ConstraintLayout layout_daftar;

    private AlertDialog progressDialog;
    private RequestQueue mQueue;
    public static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog  = new SpotsDialog(this, R.style.Custom);

        auth            = FirebaseAuth.getInstance();
        fStore          = FirebaseFirestore.getInstance();
        mQueue          = Volley.newRequestQueue(this);

        txtUsername     = findViewById(R.id.edtUsername);
        txtPassword     = findViewById(R.id.edtPassword);
        txtNama         = findViewById(R.id.edtNama);
        txtPassword2    = findViewById(R.id.edtPassword2);
        layout_daftar   = findViewById(R.id.daftar_layout);
        btnLogin        = findViewById(R.id.btnLogin);
        btnRegister     = findViewById(R.id.btnDaftar);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent daftar = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(daftar);
            }
        });
        registerUser();
    }

    private void registerUser() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                //menampung imputan user
                String namaUser      = txtNama.getText().toString().trim();
                String emailUser     = txtUsername.getText().toString().trim();
                String passwordUser  = txtPassword.getText().toString().trim();
                String passwordUser2 = txtPassword2.getText().toString().trim();

                //validasi email dan password
                // jika email kosong
                if (emailUser.isEmpty()){
                    progressDialog.dismiss();
                    txtUsername.setError("Email tidak boleh kosong");
                }
                // jika email not valid
                else if (!Patterns.EMAIL_ADDRESS.matcher(emailUser).matches()){
                    progressDialog.dismiss();
                    txtUsername.setError("Email tidak valid");
                }
                // jika password kosong
                else if (passwordUser.isEmpty()){
                    progressDialog.dismiss();
                    txtPassword.setError("Password tidak boleh kosong");
                }
                //jika password kurang dari 6 karakter
                else if (passwordUser.length() < 6){
                    progressDialog.dismiss();
                    txtPassword.setError("Password minimal terdiri dari 6 karakter");
                }
                //jika password belum sama
                else if (!passwordUser.equals(passwordUser2)){
                    progressDialog.dismiss();
                    txtPassword.setError("Password belum sama");
                    txtPassword2.setError("Password belum sama");
                }
                else {
                    //create user dengan firebase auth
                    auth.createUserWithEmailAndPassword(emailUser,passwordUser)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    //jika gagal register do something
                                    if (!task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this,
                                                "Register gagal karena "+ task.getException().getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }else {
                                        //jika sukses akan menuju ke login activity
                                        userID = auth.getCurrentUser().getUid();
                                        DocumentReference documentReference = fStore.collection("users").document(userID);
                                        Map<String,Object> user = new HashMap<>();
                                        user.put("nama", namaUser);
                                        user.put("email", emailUser);
                                        user.put("password", passwordUser);
                                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "onSuccess: Profil User " +userID);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "onFailure: " + e.toString());
                                            }
                                        });

                                        Toast.makeText(RegisterActivity.this, "Berhasil Mendaftar", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                                    }
                                }
                            });
                }}
        });
    }
}



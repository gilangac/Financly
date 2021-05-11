package id.ac.pnm.financly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {
    EditText txtEmail, txtPassword;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;

    Button btnLogin;
    TextView btnDaftar;
    ConstraintLayout layout_login;

    private AlertDialog progressDialog;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);
        progressDialog = new SpotsDialog(this, R.style.Custom);

        txtEmail     = findViewById(R.id.edtUsername);
        txtPassword  = findViewById(R.id.edtPassword);
        mQueue       = Volley.newRequestQueue(this);
        layout_login = findViewById(R.id.login_layout);
        btnDaftar    = findViewById(R.id.btnRegister);
        btnLogin     = findViewById(R.id.btnMasuk);
        auth         = FirebaseAuth.getInstance();
        db           = FirebaseDatabase.getInstance();
        users        = db.getReference("Users");
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser != null){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proses();
            }
        });

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent daftar = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(daftar);
            }
        });
    }

    private void proses() {
        progressDialog.show();
        //menampung imputan user
        final String emailUser = txtEmail.getText().toString().trim();
        final String passwordUser = txtPassword.getText().toString().trim();

        //validasi email dan password
        // jika email kosong
        if (emailUser.isEmpty()) {
            txtEmail.setError("Email tidak boleh kosong");
        }
        // jika email not valid
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailUser).matches()) {
            txtEmail.setError("Email tidak valid");
        }
        // jika password kosong
        else if (passwordUser.isEmpty()) {
            txtEmail.setError("Password tidak boleh kosong");
        }
        //jika password kurang dari 6 karakter
        else if (passwordUser.length() < 6) {
            txtEmail.setError("Password minimal terdiri dari 6 karakter");
        }else {
            auth.signInWithEmailAndPassword(emailUser, passwordUser)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            // ketika gagal locin maka akan do something
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this,
                                        "Gagal login karena " + task.getException().getMessage()
                                        , Toast.LENGTH_LONG).show();
                            } else {
                                Bundle bundle = new Bundle();
                                bundle.putString("email", emailUser);
                                bundle.putString("pass", passwordUser);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                        .putExtras(bundle));
                                finish();
                            }
                        }
                    });
        }
    }
}
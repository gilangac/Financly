package id.ac.pnm.financly;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Set;

import dmax.dialog.SpotsDialog;

public class SettingActivity extends AppCompatActivity {

    EditText edtEmail, edtPwLama, edtPwBaru, edtKonfirmPw;
    TextView btnBatal;
    Button btnSimpan;
    String nama, email, password, userID, pwLama, pwBaru, pwKonfirm, pwFinal;

    private DatabaseReference database;
    private FirebaseAuth auth;
    private AlertDialog progressDialog;
    public static final String TAG = "TAG";
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        btnBatal = findViewById(R.id.btnBatal);
        btnSimpan = findViewById(R.id.btnSimpan);
        progressDialog = new SpotsDialog(this, R.style.Custom);
        edtEmail = findViewById(R.id.edtEmail);
        edtPwLama = findViewById(R.id.edtPwLama);
        edtPwBaru = findViewById(R.id.edtPwBaru);
        edtKonfirmPw = findViewById(R.id.edtKonfirmPw);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();

        showProfil();

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePassword();
            }
        });

        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showProfil() {
        DocumentReference documentReference = firestore.collection("users").document(userID);
        documentReference.addSnapshotListener(SettingActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                nama = value.getString("nama");
                email = value.getString("email");
                password = value.getString("password");

                edtEmail.setText(email);
            }
        });
    }

    public void updatePassword() {
        progressDialog.show();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        email = edtEmail.getText().toString();
        pwLama = edtPwLama.getText().toString();
        pwBaru = edtPwBaru.getText().toString();
        pwKonfirm = edtKonfirmPw.getText().toString();

        if (email.isEmpty()) {
            progressDialog.dismiss();
            edtEmail.setError("Email tidak boleh kosong");
        } else if (pwLama.isEmpty()) {
            progressDialog.dismiss();
            edtPwLama.setError("Masukkan Password Lama");
        } else if (pwBaru.isEmpty()) {
            progressDialog.dismiss();
            edtPwBaru.setError("Masukkan Password Baru");
        } else if (pwKonfirm.isEmpty()) {
            progressDialog.dismiss();
            edtKonfirmPw.setError("Masukkan Password Baru");
        } else if (!pwBaru.equals(pwKonfirm)) {
            progressDialog.dismiss();
            edtPwBaru.setError("Password Belum Sama");
            edtKonfirmPw.setError("Password Belum Sama");
        } else {
            String currentEmail = firebaseUser.getEmail();
            AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, pwLama);

            firebaseUser.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firebaseUser.updatePassword(pwBaru)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SettingActivity.this, "Password Berhasil diupdate", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                }
                                                progressDialog.dismiss();
                                            }
                                        });
                            } else {
                                Toast.makeText(SettingActivity.this, "Password Lama Salah", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
        }
    }
}
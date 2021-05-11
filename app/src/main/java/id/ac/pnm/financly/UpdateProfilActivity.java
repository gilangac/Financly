package id.ac.pnm.financly;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class UpdateProfilActivity extends AppCompatActivity {
    TextView btnBatal;
    EditText edtEmail, edtNama;
    Button btnSimpan;

    String userID, nama, email, password, namaBaru, Nama;
    private DatabaseReference database;
    private FirebaseAuth auth;
    private AlertDialog progressDialog;
    public static final String TAG = "TAG";
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profil);
        edtEmail    = findViewById(R.id.edtEmail);
        btnBatal    = findViewById(R.id.btnBatal);
        btnSimpan   = findViewById(R.id.btnSimpan);
        edtNama     = findViewById(R.id.edtNama);
        auth        = FirebaseAuth.getInstance();
        database    = FirebaseDatabase.getInstance().getReference();
        firestore   = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new SpotsDialog(this, R.style.Custom);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();

        showProfil();

        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateProfilActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                updateProfil();
            }
        });
    }

    private void showProfil() {
        DocumentReference documentReference = firestore.collection("users").document(userID);
        documentReference.addSnapshotListener(UpdateProfilActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                nama = value.getString("nama");
                email = value.getString("email");
                password = value.getString("password");

                edtEmail.setText(email);
                edtNama.setText(nama);
            }
        });
    }

    //Proses Update data yang sudah ditentukan
    private void updateProfil() {
        Nama = edtNama.getText().toString();

        if (Nama.isEmpty()) {
            progressDialog.dismiss();
            edtNama.setError("Nama tidak boleh kosong");
        } else {
            DocumentReference profil = firestore.collection("users").document(userID);
            namaBaru = edtNama.getText().toString();

            Map<String, Object> user = new HashMap<>();
            user.put("nama", namaBaru);
            user.put("email", email);
            user.put("password", password);
            profil.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess: Profil User " + userID);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.toString());
                }
            });
            progressDialog.dismiss();
            Toast.makeText(UpdateProfilActivity.this, "Berhasil Update Profil", Toast.LENGTH_LONG).show();
            startActivity(new Intent(UpdateProfilActivity.this, MainActivity.class));
        }
        ;
    }
}
package id.ac.pnm.financly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class TambahTargetActivity extends AppCompatActivity {
    TextView edtTarget, edtNominal, edtDurasi, edtDeskripsi, edtTanggal, btnBatal;
    Button btnSimpan;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    String userID;
    private AlertDialog progressDialog;
    public static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_target);

        edtTarget = findViewById(R.id.edtTarget);
        edtNominal = findViewById(R.id.edtNominal);
        edtDurasi = findViewById(R.id.edtDurasi);
        edtDeskripsi = findViewById(R.id.edtDeskripsi);
        edtTanggal = findViewById(R.id.edtTanggal);
        btnSimpan = findViewById(R.id.btnSimpan);
        btnBatal = findViewById(R.id.btnBatal);
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressDialog = new SpotsDialog(this, R.style.Custom);

        edtTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tampilTanggal();
            }
        });

        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent batal = new Intent(TambahTargetActivity.this, MainActivity.class);
                startActivity(batal);
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tambahTarget();
            }
        });

    }

    private void tampilTanggal() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getSupportFragmentManager(), "data");
        datePickerFragment.setOnDateClickListener(new DatePickerFragment.onDateClickListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                String tahun = "" + datePicker.getYear();
                String bulan = "" + (datePicker.getMonth() + 1);
                String hari = "" + datePicker.getDayOfMonth();
                String text = tahun + " - " + bulan + " - " + hari;
                edtTanggal.setText(text);
            }
        });
    }

    private void tambahTarget() {
        progressDialog.show();

        String target   = edtTarget.getText().toString().trim();
        String nominal  = edtNominal.getText().toString().trim();
        String durasi   = edtDurasi.getText().toString().trim();
        String deskripsi = edtDeskripsi.getText().toString().trim();
        String tanggal  = edtTanggal.getText().toString().trim();
        String id       = UUID.randomUUID().toString();

        if (target.isEmpty() || nominal.isEmpty() || durasi.isEmpty() || deskripsi.isEmpty() || tanggal.isEmpty() || tanggal.isEmpty()) {
            progressDialog.dismiss();
            Toast.makeText(TambahTargetActivity.this, "Lengkapi semua form", Toast.LENGTH_LONG).show();
        } else {
            userID = auth.getCurrentUser().getUid();
            DocumentReference documentReference = fStore.collection("users").document(userID).collection("dataTarget").document(id);
            Map<String, Object> user = new HashMap<>();
            user.put("id", id);
            user.put("target", target);
            user.put("nominal", nominal);
            user.put("durasi", durasi);
            user.put("deskripsi", deskripsi);
            user.put("tanggal", tanggal);
            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    Log.d(TAG, "onSuccess: Target ");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Log.d(TAG, "onFailure: " + e.toString());
                }
            });
            progressDialog.dismiss();

            Toast.makeText(TambahTargetActivity.this, "Target Berhasil Ditambahkan", Toast.LENGTH_LONG).show();
            startActivity(new Intent(TambahTargetActivity.this, LoginActivity.class));
        }
    }
}
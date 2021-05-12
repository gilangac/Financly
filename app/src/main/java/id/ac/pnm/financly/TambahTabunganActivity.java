package id.ac.pnm.financly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.model.Model;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class TambahTabunganActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spinTarget;
    EditText edtNominal, edtDeskripsi, edtTanggal;
    Button btnSimpan;
    TextView btnBatal;

    private AlertDialog progressDialog;
    public static final String TAG = "TAG";
    private ArrayList<String> arrayList = new ArrayList<>();
    String userID, target, cicilan, tanggal, deskripsi, id;

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_tabungan);

        spinTarget = findViewById(R.id.spinTarget);
        edtNominal = findViewById(R.id.edtNominal);
        edtDeskripsi = findViewById(R.id.edtDeskripsi);
        edtTanggal = findViewById(R.id.edtTanggal);
        btnSimpan = findViewById(R.id.btnSimpan);
        btnBatal = findViewById(R.id.btnBatal);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new SpotsDialog(this, R.style.Custom);

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();

        showDataSpinner();

        edtTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tampilTanggal();
            }
        });

        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent batal = new Intent(TambahTabunganActivity.this, MainActivity.class);
                startActivity(batal);
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tambahTabungan();
            }
        });
    }

    private void tampilTanggal() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getSupportFragmentManager(), "data");
        datePickerFragment.setOnDateClickListener(new DatePickerFragment.onDateClickListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                String tahun    = "" + datePicker.getYear();
                String bulan    = "" + (datePicker.getMonth() + 1);
                String hari     = "" + datePicker.getDayOfMonth();
                String text     = tahun + " - " + bulan + " - " + hari;
                edtTanggal.setText(text);
            }
        });
    }

    public void showDataSpinner() {
        firestore.collection("users").document(userID).collection("dataTarget")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        arrayList.clear();
                        for (DocumentSnapshot doc : task.getResult()) {
                            if (doc.exists()) {
                                arrayList.add(doc.getString("target"));
                            }
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(TambahTabunganActivity.this, R.layout.style_spinner, arrayList);
                        spinTarget.setAdapter(arrayAdapter);
                        spinTarget.setOnItemSelectedListener(TambahTabunganActivity.this);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void tambahTabungan() {
        progressDialog.show();

        target      = String.valueOf(spinTarget.getSelectedItem());
        cicilan     = edtNominal.getText().toString().trim();
        deskripsi   = edtDeskripsi.getText().toString().trim();
        tanggal     = edtTanggal.getText().toString().trim();
        id          = UUID.randomUUID().toString();

        if (target.isEmpty() || cicilan.isEmpty() || deskripsi.isEmpty() || tanggal.isEmpty()) {
            progressDialog.dismiss();
            Toast.makeText(TambahTabunganActivity.this, "Lengkapi semua form", Toast.LENGTH_LONG).show();
        } else {

            userID = firebaseAuth.getCurrentUser().getUid();
            DocumentReference documentReference = firestore.collection("users").document(userID).collection("dataTabungan").document(id);
            Map<String, Object> user = new HashMap<>();
            user.put("id", id);
            user.put("target", target);
            user.put("cicilan", cicilan);
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

            Toast.makeText(TambahTabunganActivity.this, "Tabungan Berhasil Ditambahkan", Toast.LENGTH_LONG).show();
            startActivity(new Intent(TambahTabunganActivity.this, LoginActivity.class));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(this, "Anda Memlilih ", Toast.LENGTH_LONG);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
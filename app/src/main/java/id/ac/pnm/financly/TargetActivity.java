package id.ac.pnm.financly;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.math.MathUtils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.stream.IntStream;

import dmax.dialog.SpotsDialog;

import static java.lang.Integer.parseInt;

public class TargetActivity extends AppCompatActivity {

    TextView txtSisa,txtTarget,txtNominal,txtDurasi,txtDeskripsi,txtTanggal,txtPerBulan,txtPerHari,txtRiwayat;
    ImageView btnHapus;
    ConstraintLayout layout_daftar;

    String userID, id,id2,target2,cicilan, target, nominal, tanggal, durasi,deskripsi,targetUtama;
    Integer cicil ,sisa,nominall=1,perBulan,perHari,durasi2,persen,persen2,sum;
    ArrayList<Integer> list_tabung;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    private AlertDialog progressDialog;
    private ProgressBar progressBar;
    private TextView persentase;
    private int Value = 0;

    DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.JAPAN);
    DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);

        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        Bundle extras = getIntent().getExtras();
        targetUtama   = extras.getString("target");
        list_tabung   = new ArrayList<Integer>();

        progressDialog = new SpotsDialog(this, R.style.Custom);

        progressBar   = findViewById(R.id.progress);
        persentase    = findViewById(R.id.persentase);
        txtTarget     = findViewById(R.id.txtTarget);
        txtNominal    = findViewById(R.id.txtNominal);
        txtDeskripsi  = findViewById(R.id.txtDeskripsi);
        txtTanggal    = findViewById(R.id.txtTanggal);
        txtDurasi     = findViewById(R.id.txtDurasi);
        txtSisa       = findViewById(R.id.txtSisa);
        txtPerBulan   = findViewById(R.id.txtPerBulan);
        txtPerHari    = findViewById(R.id.txtPerHari);
        txtRiwayat    = findViewById(R.id.txtRiwayat);
        btnHapus      = findViewById(R.id.btnHapus);
        layout_daftar = findViewById(R.id.layoutTarget);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        txtTarget.setText(targetUtama);
        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        showData();
    }

    private void showDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TargetActivity.this);

        // set title dialog
        alertDialogBuilder.setTitle("Menghapus Target?");

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage("Riwayat Tabunganmu juga akan terhapus")
                .setCancelable(false)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        hapusData();
                    }
                })
                .setNegativeButton("Batal",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();

        // menampilkan alert dialog
        alertDialog.show();
    }

    private void hapusData() {
        progressDialog.show();
        fStore.collection("users").document(userID).collection("dataTarget")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            if (doc.exists()) {
                                HashMap<String, String> map = new HashMap<String, String>();

                                id = (doc.getString("id"));
                                target = (doc.getString("target"));

                                map.put("target", target);

                                if (targetUtama.equals(target)) {
                                    DocumentReference productIdRef =  fStore.collection("users").document(userID).collection("dataTarget").document(id);
                                    //hapus data
                                    productIdRef.delete().addOnSuccessListener(aVoid ->hapusTabung());
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void hapusTabung() {
        fStore.collection("users").document(userID).collection("dataTabungan")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot doc:task.getResult()){
                            if(doc.exists()){
                                HashMap<String, String> map = new HashMap<String, String>();

                                id2         = doc.getString("id");
                                target2     = doc.getString("target");

                                if (target2.equals(targetUtama)) {
                                    DocumentReference tabung =  fStore.collection("users").document(userID).collection("dataTabungan").document(id2);
                                    //hapus data
                                    tabung.delete().addOnSuccessListener(aVoid ->
                                            progressDialog.dismiss());
                                    Intent daftar = new Intent(TargetActivity.this, MainActivity.class);
                                    startActivity(daftar);
                                }else{
                                    progressDialog.dismiss();
                                    Intent daftar = new Intent(TargetActivity.this, MainActivity.class);
                                    startActivity(daftar);
                                }
                            }
                        }
                        progressDialog.dismiss();
                        Toast.makeText(TargetActivity.this, "Data Terhapus !", Toast.LENGTH_LONG).show();
                        Intent daftar = new Intent(TargetActivity.this, MainActivity.class);
                        startActivity(daftar);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void showData() {
        fStore.collection("users").document(userID).collection("dataTarget")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot doc:task.getResult()){
                            if(doc.exists()){
                                HashMap<String, String> map = new HashMap<String, String>();

                                target     = (doc.getString("target"));
                                nominal    = doc.getString("nominal");
                                durasi     = doc.getString("durasi");
                                tanggal    = doc.getString("tanggal");
                                deskripsi  = doc.getString("deskripsi");

                                int nominalFinal = parseInt(nominal);

                                if (targetUtama.equals(target)){
                                    txtNominal.setText(kursIndonesia.format(nominalFinal));
                                    txtDurasi.setText(durasi+" Bulan");
                                    txtTanggal.setText(tanggal);
                                    txtDeskripsi.setText(deskripsi);

                                    nominall    = parseInt(nominal);
                                    durasi2     = parseInt(durasi);
                                    perBulan    = nominall/durasi2;
                                    perHari     = nominall/(30*durasi2);

                                    txtPerBulan.setText(kursIndonesia.format(perBulan));
                                    txtPerHari.setText(kursIndonesia.format(perHari));

                                    progressBar.setMax(nominall);
                                    showTabungan();
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void showTabungan() {
        fStore.collection("users").document(userID).collection("dataTabungan")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot doc:task.getResult()){
                            if(doc.exists()){
                                HashMap<String, String> map = new HashMap<String, String>();

                                id2         = doc.getString("id");
                                target2     = doc.getString("target");
                                cicilan     = doc.getString("cicilan");
                                cicil       = parseInt(cicilan);

                                if (target2.equals(targetUtama)) {
                                    list_tabung.add(cicil);
                                }
                            }
                        }
                        sum = 0;
                        for(int i = 0; i < list_tabung.size(); i++)
                            sum += list_tabung.get(i);

                        progressBar.setProgress(sum);

                        persen2  = (100*sum);
                        persen   = persen2/nominall;
                        sisa     = (nominall-sum);

                        txtRiwayat.setText(kursIndonesia.format(sum));
                        txtSisa.setText(kursIndonesia.format(sisa));
                        persentase.setText("Target Tercapai "+persen+"%");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

}
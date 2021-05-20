package id.ac.pnm.financly.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;

import id.ac.pnm.financly.AdapterList;
import id.ac.pnm.financly.LoginActivity;
import id.ac.pnm.financly.MainActivity;
import id.ac.pnm.financly.R;
import id.ac.pnm.financly.SettingActivity;
import id.ac.pnm.financly.TargetActivity;
import id.ac.pnm.financly.UpdateProfilActivity;
import id.ac.pnm.financly.tambahDataActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfilFragment extends Fragment {
    TextView txtNama;
    LinearLayout btnKeluar,btnProfil,btnUbah;
    FirebaseAuth auth;
    String userID, Nama, email, password;

    FirebaseUser firebaseUser;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;


    private AlertDialog.Builder dialog;
    private LayoutInflater inflater;
    private View dialogView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfilFragment newInstance(String param1, String param2) {
        ProfilFragment fragment = new ProfilFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_profil, container, false);

        firestore       = FirebaseFirestore.getInstance();
        firebaseAuth    = FirebaseAuth.getInstance();
        auth            = FirebaseAuth.getInstance();
        firebaseUser    = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();

        txtNama     = v.findViewById(R.id.txtNamaAkun);
        btnProfil   = (LinearLayout) v.findViewById(R.id.btnUbahProfil);
        btnUbah     = (LinearLayout) v.findViewById(R.id.btnUbahSandi);
        btnKeluar   = (LinearLayout) v.findViewById(R.id.btnKeluar);

        btnKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        btnUbah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });

        btnProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UpdateProfilActivity.class);
                startActivity(intent);
            }
        });

        showProfil();

        return v;
    }

    private void showProfil() {
        DocumentReference documentReference = firestore.collection("users").document(userID);
        documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Nama     = (value.getString("nama"));
                email    = value.getString("email");
                password = value.getString("password");

                String nama = (Nama);
                txtNama.setText(nama);
            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        dialog = new AlertDialog.Builder(getActivity());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.alert_dialog, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);

        // set pesan dari dialog
        dialog.setCancelable(false);

        // membuat alert dialog dari builder
        AlertDialog dialoga = dialog.create();
        dialoga.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView txtTitle = (TextView) dialogView.findViewById(R.id.alertTitle);
        TextView txtIsi = (TextView) dialogView.findViewById(R.id.alertIsi);
        TextView btnBtl = (TextView) dialogView.findViewById(R.id.alertBatal);
        TextView btnYa  = (TextView) dialogView.findViewById(R.id.alertYa);

        txtTitle.setText("Logout");
        txtIsi.setText("Yakin keluar aplikasi?");

        btnBtl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialoga.dismiss();
            }
        });

        btnYa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseUser = null;
                auth.signOut();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                dialoga.dismiss();
            }
        });
        // menampilkan alert dialog
        dialoga.show();
    }

}
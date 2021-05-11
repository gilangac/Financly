package id.ac.pnm.financly.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.model.Model;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import id.ac.pnm.financly.AdapterList;
import id.ac.pnm.financly.AdapterList3;
import id.ac.pnm.financly.AdapterList5;
import id.ac.pnm.financly.LoginActivity;
import id.ac.pnm.financly.MainActivity;
import id.ac.pnm.financly.R;
import id.ac.pnm.financly.RegisterActivity;
import id.ac.pnm.financly.tambahDataActivity;

import static java.lang.Integer.parseInt;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    TextView txtNama,btnGoals,btnRiwayat,txtTabungToday;
    ImageView btnTambah;
    HistoryFragment fragmentHistory;

    ArrayList<HashMap<String, String>> list_data;
    ArrayList<HashMap<String, String>> list_tabung;
    ArrayList<Integer> list_cicil;
    List<Model> modelList = new ArrayList<>();
    List<Model> modelList2 = new ArrayList<>();

    String userID, Nama, email, password, target, nominal, durasi, tanggal,deskripsi,id,id2,
            cicilan, target2, tanggal2, deskripsi2;
    Integer cicil,sum;

    private ShimmerFrameLayout mShimmerViewContainer;
    private RecyclerView recycleview, recycleview2;

    DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.JAPAN);

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View v=inflater.inflate(R.layout.fragment_home, container, false);
        txtNama         = v.findViewById(R.id.txtNama);
        btnTambah       = v.findViewById(R.id.btnTambah);
        btnGoals        = v.findViewById(R.id.btnGoals);
        btnRiwayat      = v.findViewById(R.id.btnRiwayat);
        txtTabungToday  = v.findViewById(R.id.txtTabungToday);

        mShimmerViewContainer = v.findViewById(R.id.shimmer_view_container);

        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        recycleview = (RecyclerView) v.findViewById(R.id.recycleview);
        LinearLayoutManager llmz = new LinearLayoutManager(getActivity());
        llmz.setOrientation(LinearLayoutManager.VERTICAL);
        recycleview.setLayoutManager(llmz);

        recycleview2 = (RecyclerView) v.findViewById(R.id.recycleview2);
        LinearLayoutManager llma = new LinearLayoutManager(getActivity());
        llma.setOrientation(LinearLayoutManager.VERTICAL);
        recycleview2.setLayoutManager(llma);

        list_cicil   = new ArrayList<Integer>();
        list_data    = new ArrayList<HashMap<String, String>>();
        list_tabung  = new ArrayList<HashMap<String, String>>();
        firestore    = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();

        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), tambahDataActivity.class);
                startActivity(intent);
            }
        });

        btnGoals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentHistory = new HistoryFragment();
                FragmentTransaction ft = getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentsub, fragmentHistory);
                ft.commit();
            }
        });

        showProfil();
        showData();
        showTabungan();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }

    private void showProfil() {
        DocumentReference documentReference = firestore.collection("users").document(userID);
        documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Nama     = (value.getString("nama"));
                email    = value.getString("email");
                password = value.getString("password");

                String nama = ("Hi, "+Nama+"!");
                txtNama.setText(nama);
            }
        });
    }

    public void showData() {
        firestore.collection("users").document(userID).collection("dataTarget")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        modelList.clear();
                        for (DocumentSnapshot doc:task.getResult()){
                            if(doc.exists()){
                                HashMap<String, String> map = new HashMap<String, String>();

                                id    = doc.getString("id");
                                target     = doc.getString("target");
                                nominal    = doc.getString("nominal");
                                durasi     = doc.getString("durasi");
                                tanggal    = doc.getString("tanggal");
                                deskripsi  = doc.getString("deskripsi");

                                map.put("id",id);
                                map.put("target",target);
                                map.put("nominal",nominal);
                                map.put("durasi",durasi);
                                map.put("tanggal",tanggal);
                                map.put("deskripsi",deskripsi);
                                list_data.add(map);
                            }
                        }

                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        if (list_data.size()>0){
                            AdapterList adapter = new AdapterList(getActivity(),list_data);
                            recycleview.setAdapter(adapter);
                        } else {
                            AdapterList5 adapter = new AdapterList5(getActivity(),list_data);
                            recycleview.setAdapter(adapter);
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
        DateFormat dateFormat = new SimpleDateFormat("yyyy - M - d");
        Date date = new Date();
        firestore.collection("users").document(userID).collection("dataTabungan")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        modelList2.clear();
                        for (DocumentSnapshot doc:task.getResult()){
                            if(doc.exists()){
                                HashMap<String, String> map = new HashMap<String, String>();

                                id2         = doc.getString("id");
                                target2     = doc.getString("target");
                                cicilan     = doc.getString("cicilan");
                                tanggal2    = doc.getString("tanggal");
                                deskripsi2  = doc.getString("deskripsi");
                                cicil       = parseInt(cicilan);

                                map.put("id",id2);
                                map.put("target",target2);
                                map.put("cicilan",cicilan);
                                map.put("tanggal",tanggal2);
                                map.put("deskripsi",deskripsi2);
                                list_tabung.add(map);

                                if (tanggal2.equals(dateFormat.format(date))) {
                                    list_cicil.add(cicil);
                                }
                            }
                        }
                        if (list_tabung.size()>0){
                            AdapterList3 adapter = new AdapterList3(getActivity(),list_tabung);
                            recycleview2.setAdapter(adapter);
                        } else {
                            AdapterList5 adapter = new AdapterList5(getActivity(),list_data);
                            recycleview2.setAdapter(adapter);
                        }

                        sum = 0;
                        for(int i = 0; i < list_cicil.size(); i++)
                            sum += list_cicil.get(i);

                        txtTabungToday.setText(kursIndonesia.format(sum));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

}
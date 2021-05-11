package id.ac.pnm.financly.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.model.Model;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import id.ac.pnm.financly.AdapterList;
import id.ac.pnm.financly.AdapterList2;
import id.ac.pnm.financly.AdapterList3;
import id.ac.pnm.financly.AdapterList4;
import id.ac.pnm.financly.AdapterList5;
import id.ac.pnm.financly.R;
import id.ac.pnm.financly.tambahDataActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {
    TextView btnTarget,btnTabungan,txtKeterangan;
    ArrayList<HashMap<String, String>> list_data;
    ArrayList<HashMap<String, String>> list_tabung;
    String userID,id, target, nominal, durasi, tanggal,deskripsi,id2, target2, cicilan,tanggal2,deskripsi2;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    private ShimmerFrameLayout mShimmerViewContainer;
    private RecyclerView recycleview;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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
        View v=inflater.inflate(R.layout.fragment_history, container, false);
        btnTarget       = v.findViewById(R.id.btnTarget);
        btnTabungan     = v.findViewById(R.id.btnTabungan);
        txtKeterangan   = v.findViewById(R.id.txtKeterangan);
        mShimmerViewContainer = v.findViewById(R.id.shimmer_view_container);

        btnTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnTarget.setTextColor(getActivity().getResources().getColor(R.color.putih));
                btnTabungan.setTextColor(getActivity().getResources().getColor(R.color.ijo));
                btnTarget.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_btnhistory));
                btnTabungan.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_historytidakpilih));
                txtKeterangan.setText("Riwayat target tabunganmu");
                showData();
            }
        });

        btnTabungan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnTabungan.setTextColor(getActivity().getResources().getColor(R.color.putih));
                btnTarget.setTextColor(getActivity().getResources().getColor(R.color.ijo));
                btnTabungan.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_btnhistory));
                btnTarget.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_historytidakpilih));
                txtKeterangan.setText("Riwayat tabunganmu");
                showTabungan();
            }
        });

        recycleview = (RecyclerView) v.findViewById(R.id.recycleview);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recycleview.setLayoutManager(llm);

        list_data    = new ArrayList<HashMap<String, String>>();
        list_tabung  = new ArrayList<HashMap<String, String>>();
        firestore    = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();
        showData();

        return v;
    }

    private void showTabungan() {
        mShimmerViewContainer.startShimmerAnimation();
        firestore.collection("users").document(userID).collection("dataTabungan")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        list_tabung.clear();
                        list_data.clear();
                        for (DocumentSnapshot doc:task.getResult()){
                            if(doc.exists()){
                                HashMap<String, String> map = new HashMap<String, String>();

                                id2         = doc.getString("id");
                                target2     = doc.getString("target");
                                cicilan     = doc.getString("cicilan");
                                tanggal2    = doc.getString("tanggal");
                                deskripsi2  = doc.getString("deskripsi");

                                map.put("id",id2);
                                map.put("target",target2);
                                map.put("cicilan",cicilan);
                                map.put("tanggal",tanggal2);
                                map.put("deskripsi",deskripsi2);
                                list_tabung.add(map);
                            }
                        }
                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        if (list_tabung.size()>0){
                            AdapterList4 adapter = new AdapterList4(getActivity(),list_tabung);
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

    public void showData() {
        mShimmerViewContainer.startShimmerAnimation();
        firestore.collection("users").document(userID).collection("dataTarget")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        list_tabung.clear();
                        list_data.clear();
                        for (DocumentSnapshot doc:task.getResult()){
                            if(doc.exists()){
                                HashMap<String, String> map = new HashMap<String, String>();

                                id         = doc.getString("id");
                                target     = (doc.getString("target"));
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
                            AdapterList2 adapter = new AdapterList2(getActivity(),list_data);
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
}
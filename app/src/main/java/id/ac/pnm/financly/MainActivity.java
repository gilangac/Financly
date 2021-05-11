package id.ac.pnm.financly;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import id.ac.pnm.financly.fragment.HistoryFragment;
import id.ac.pnm.financly.fragment.HomeFragment;
import id.ac.pnm.financly.fragment.ProfilFragment;

public class MainActivity extends AppCompatActivity{

    HomeFragment fragmentHome;
    HistoryFragment fragmentHistory;
    ProfilFragment fragmentProfil;
    String userID, nama, email, password;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView BottomNav = findViewById(R.id.bottom_nav);
        BottomNav.setOnNavigationItemSelectedListener(navListener);

        menuHome();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                nama     = (value.getString("nama"));
                email    = value.getString("email");
                password = value.getString("password");
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.nav_home:
                            menuHome();
                            break;
                        case R.id.nav_history:
                            menuHistory();
                            break;
                        case R.id.nav_profil:
                            menuProfil();
                            break;
                    }
                    return true;
                }
            };

    void menuHome() {
        fragmentHome = new HomeFragment();
        FragmentTransaction ft = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentsub, fragmentHome);
        ft.commit();

    }

    void menuHistory() {
        fragmentHistory = new HistoryFragment();
        FragmentTransaction ft = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentsub, fragmentHistory);
        ft.commit();
    }

    void menuProfil() {
        fragmentProfil = new ProfilFragment();
        FragmentTransaction ft = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentsub, fragmentProfil);
        ft.commit();
    }

}
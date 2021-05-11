package id.ac.pnm.financly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class tambahDataActivity extends AppCompatActivity {
    LinearLayout btnTarget, btnTabungan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_data);

        btnTarget = findViewById(R.id.btnTarget);
        btnTabungan = findViewById(R.id.btnTabungan);

        btnTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(tambahDataActivity.this, TambahTargetActivity.class);
                startActivity(intent);
            }
        });

        btnTabungan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(tambahDataActivity.this, TambahTabunganActivity.class);
                startActivity(intent);
            }
        });
    }
}
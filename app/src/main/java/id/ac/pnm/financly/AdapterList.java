package id.ac.pnm.financly;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static java.lang.Integer.parseInt;

public class AdapterList extends RecyclerView.Adapter<AdapterList.ViewHolder>{

    Context context;
    ArrayList<HashMap<String, String>> list_data;
    String kirim;
    Integer nominalFinal;

    public AdapterList(FragmentActivity mainActivity, ArrayList<HashMap<String, String>> list_data) {
        this.context = mainActivity;
        this.list_data = list_data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.JAPAN);
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        String nominall = (list_data.get(position).get("nominal"));
        String bulann = (list_data.get(position).get("durasi"));
        String hasil = "";
        if(nominall!=null && bulann!=null ){
            Integer cicilan = parseInt(nominall);
            Integer bln = parseInt(bulann);

            Integer hslCicil = (cicilan/(30*bln));
            String cicilFinal = kursIndonesia.format(hslCicil);
            hasil = cicilFinal;
        } else{
            hasil = "Cicilan";
        }

        nominalFinal = parseInt(nominall);

        holder.txtTarget.setText(list_data.get(position).get("target"));
        holder.txtNominal.setText(kursIndonesia.format(nominalFinal));
        holder.txtCicil.setText(hasil+"/Hari");
        holder.txtDurasi.setText(list_data.get(position).get("durasi")+" Bulan");

        holder.btnTabung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kirim = holder.txtTarget.getText().toString();
                Intent daftar = new Intent(context, TargetActivity.class);
                daftar.putExtra("target", kirim);
                context.startActivity(daftar);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list_data.size() <2 ) {
            return list_data.size();
        }
        return 3;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTarget,txtNominal,txtCicil,txtDurasi;
        LinearLayout btnTabung;

        public ViewHolder(View itemView) {
            super(itemView);
            btnTabung = (LinearLayout) itemView.findViewById(R.id.btnTabung);
            txtTarget = (TextView) itemView.findViewById(R.id.txtTarget);
            txtNominal = (TextView) itemView.findViewById(R.id.txtNominal);
            txtCicil = (TextView) itemView.findViewById(R.id.txtCicil);
            txtDurasi = (TextView) itemView.findViewById(R.id.txtDurasi);
        }
    }
}

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

public class AdapterList4 extends RecyclerView.Adapter<AdapterList4.ViewHolder>{

    Context context;
    ArrayList<HashMap<String, String>> list_tabung;

    public AdapterList4(FragmentActivity mainActivity, ArrayList<HashMap<String, String>> list_tabung) {
        this.context = mainActivity;
        this.list_tabung = list_tabung;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_tabung, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.JAPAN);
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        String cicil = list_tabung.get(position).get("cicilan");
        int cicilan = parseInt(cicil);

        holder.txtTarget.setText("Tabungan "+list_tabung.get(position).get("target"));
        holder.txtCicil.setText("+ "+kursIndonesia.format(cicilan));
        holder.txtTanggal.setText(list_tabung.get(position).get("tanggal"));
    }

    @Override
    public int getItemCount() {
        return list_tabung.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTarget,txtCicil,txtTanggal;
        LinearLayout btnTabung;

        public ViewHolder(View itemView) {
            super(itemView);
            btnTabung   = (LinearLayout) itemView.findViewById(R.id.btnTabung);
            txtTarget   = (TextView) itemView.findViewById(R.id.txtTarget);
            txtCicil    = (TextView) itemView.findViewById(R.id.txtCicilan);
            txtTanggal  = (TextView) itemView.findViewById(R.id.txtTanggal);
        }
    }
}

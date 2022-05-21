package android.job.blescan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import no.nordicsemi.android.support.v18.scanner.ScanResult;

class Recycleradater extends RecyclerView.Adapter<Viewholder> {
    List<ScanResult> results;

    public Recycleradater(List<ScanResult> scandevices) {
        results = scandevices;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview, parent, false);
        return new Viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        holder.deviceid.setText(results.get(position).getDevice().getAddress());
        holder.name.setText(results.get(position).getDevice().getName());
    }

    @Override
    public int getItemCount() {
     if (results!=null)
        return results.size();
     else
     return 0;
    }
}

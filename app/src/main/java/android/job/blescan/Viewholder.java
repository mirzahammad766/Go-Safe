package android.job.blescan;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class Viewholder extends RecyclerView.ViewHolder {
public TextView name,deviceid;
    public Viewholder(@NonNull View itemView) {
        super(itemView);
        name=itemView.findViewById(R.id.Name);
        deviceid=itemView.findViewById(R.id.Deviceid);
    }
}

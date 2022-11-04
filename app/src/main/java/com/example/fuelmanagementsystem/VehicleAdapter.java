package com.example.fuelmanagementsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.Viewholder> {

    private Context context;
    private ArrayList<VehicleModel> vehicleModelArrayList;
    private View.OnClickListener mOnItemClickListener;
    private final ClickListener listener;
    private final View.OnClickListener writeClicklistner;

    public VehicleAdapter(Context context, ArrayList<VehicleModel> vehicleModelArrayList, ClickListener listener, View.OnClickListener writeClicklistner) {
        this.context = context;
        this.vehicleModelArrayList = vehicleModelArrayList;
        this.listener = listener;
        this.writeClicklistner = writeClicklistner;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_card_layout, parent, false);
        return new Viewholder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        VehicleModel model = vehicleModelArrayList.get(position);
        holder.regisno.setText(model.getRegisno());
        holder.fuellimit.setText(model.getFuellimit());
        holder.write.setOnClickListener(writeClicklistner);
    }

    @Override
    public int getItemCount() {
        return vehicleModelArrayList.size();
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    public class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView regisno,fuellimit;
        private final Button write;
        private WeakReference<ClickListener> listenerRef;

        public Viewholder(@NonNull View itemView, ClickListener listener) {
            super(itemView);
            regisno = itemView.findViewById(R.id.card_regisno);
            fuellimit = itemView.findViewById(R.id.card_fuellimit);
            write = itemView.findViewById(R.id.write);
            listenerRef = new WeakReference<>(listener);

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }

        @Override
        public void onClick(View v) {
            listenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }

    public interface ClickListener {
        void onPositionClicked(int position);
    }
}

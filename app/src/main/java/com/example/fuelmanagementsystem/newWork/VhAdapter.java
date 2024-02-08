package com.example.fuelmanagementsystem.newWork;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fuelmanagementsystem.R;

import java.util.List;
import java.util.Locale;


public class VhAdapter extends RecyclerView.Adapter<VhAdapter.MyViewHolder>{

    private Context mContext;
    private List<VehicleData> vhData;
    public VhAdapter(Context mContext, List<VehicleData> detailList) {
        this.mContext = mContext;
        this.vhData = detailList;
    }


    public VhAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_list, parent, false);
        return new VhAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(VhAdapter.MyViewHolder holder, final int position) {

        final VehicleData datum = vhData.get(position);
        holder.vhName.setText(datum.getVhName());

    }


    @Override
    public int getItemCount() {
        Log.e("vhDataSize",vhData.size()+"");
        return vhData.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView vhName;


        public MyViewHolder(View itemView) {
            super(itemView);

            vhName = itemView.findViewById(R.id.vh_name);

        }

    }

}

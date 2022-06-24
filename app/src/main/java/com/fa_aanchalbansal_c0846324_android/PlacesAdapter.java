package com.fa_aanchalbansal_c0846324_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlacesHolder> {
    private final Context context;
    private final ArrayList<PlaceModel> placesList;

    public PlacesAdapter(Context context, ArrayList<PlaceModel> placesList) {
        this.context = context;;
        this.placesList = placesList;
    }

    @NonNull
    @Override
    public PlacesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlacesHolder(LayoutInflater.from(context).inflate(R.layout.row_place, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesHolder holder, int position) {
        holder.bind(context, placesList);
    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

    static class PlacesHolder extends RecyclerView.ViewHolder {

        private final RadioButton rbvFav;
        private final TextView tvName;
        private final TextView tvAddress;
        private final ImageView ivMore;

        public PlacesHolder(@NonNull View itemView) {
            super(itemView);
            rbvFav = itemView.findViewById(R.id.rb_fav);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAddress = itemView.findViewById(R.id.tv_address);
            ivMore = itemView.findViewById(R.id.iv_more);
        }

        public void bind(Context context, ArrayList<PlaceModel> placesList) {
            tvName.setText(placesList.get(getAdapterPosition()).getTitle());
            tvAddress.setText(placesList.get(getAdapterPosition()).getAddress());
            rbvFav.setChecked(placesList.get(getAdapterPosition()).getVisited() == 1);
            rbvFav.setEnabled(false);
            ivMore.setOnClickListener(v -> {
                if (context instanceof PlacesActivity) {
                    ((PlacesActivity)context).openOptions(getAdapterPosition());
                }
            });
            itemView.setOnClickListener(v -> {
                if (context instanceof PlacesActivity) {
                    ((PlacesActivity)context).openEditScreen(getAdapterPosition());
                }
            });
        }

    }
}

package com.yuluassignment.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.yuluassignment.R;
import com.yuluassignment.databinding.XPlaceItemBinding;
import com.yuluassignment.entities.Place;

import java.text.DecimalFormat;
import java.util.List;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.PlaceItemViewHolder> {

    private Context                context;
    private List<Place>            places;
    private PlaceSelectionListener listener;

    public PlaceListAdapter(Context context) {
        this.context = context;
    }

    public void setPlaceSelectionListener(PlaceSelectionListener listener) {
        this.listener = listener;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaceItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        XPlaceItemBinding b = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.x_place_item, parent, false);
        return new PlaceItemViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceItemViewHolder holder, int position) {

        Place place = places.get(position);
        holder.setPlace(place);
        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onPlaceSelect(place));
        }

    }

    @Override
    public int getItemCount() {
        if (places == null) {
            return 0;
        }
        return places.size();
    }

    public interface PlaceSelectionListener {

        void onPlaceSelect(Place place);

    }

    class PlaceItemViewHolder extends RecyclerView.ViewHolder {

        XPlaceItemBinding b;
        DecimalFormat     decimalFormat = new DecimalFormat("#.# km");

        public PlaceItemViewHolder(XPlaceItemBinding b) {
            super(b.getRoot());
            this.b = b;
        }

        public void setPlace(Place place) {
            b.placeName.setText(place.name);
            b.shortAddress.setText(place.shortAddress);
            b.distance.setText(decimalFormat.format(place.distance / 1000));
        }

    }

}

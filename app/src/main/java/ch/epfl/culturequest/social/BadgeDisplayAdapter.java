package ch.epfl.culturequest.social;

import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


import ch.epfl.culturequest.R;

public class BadgeDisplayAdapter extends RecyclerView.Adapter<BadgeDisplayAdapter.BadgeDisplayViewHolder> {
    private final List<Pair<String, Integer>> badges;

    public BadgeDisplayAdapter(HashMap<String, Integer> badges) {
        // sort the badges by decreasing order of count
        if (badges == null) {
            this.badges = new ArrayList<>();
            return;
        }
        this.badges = badges.entrySet().stream().map(e -> new Pair<>(e.getKey(), e.getValue())).sorted((p1, p2) -> Integer.compare(p2.second, p1.second)).filter(s->ScanBadge.Badge.identifyPlace(s.first)!= ScanBadge.Country.OTHER).collect(Collectors.toList());
    }

    @NonNull
    @Override
    public BadgeDisplayAdapter.BadgeDisplayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_badge_display, parent, false);
        return new BadgeDisplayAdapter.BadgeDisplayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BadgeDisplayAdapter.BadgeDisplayViewHolder holder, int position) {
        Pair<String, Integer> badge = badges.get(position);
        int drawableId = ScanBadge.Badge.identifyPlace(badge.first).getBadge();
        holder.image.setImageResource(drawableId);
        holder.count.setText(String.valueOf(badge.second));

    }

    @Override
    public int getItemCount() {
        return badges.size();
    }

    public class BadgeDisplayViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView count;

        public BadgeDisplayViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.badge_image);
            count = itemView.findViewById(R.id.badge_count);
        }
    }
}

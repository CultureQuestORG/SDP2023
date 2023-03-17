package ch.epfl.culturequest.social;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ch.epfl.culturequest.R;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.PictureViewHolder> {

    private final List<Image> pictures;

    public PictureAdapter(List<Image> pictures) {
        if (pictures == null) {
            throw new IllegalArgumentException("pictures cannot be null");
        }
        this.pictures = pictures;
    }

    @NonNull
    @Override
    public PictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_picture, parent, false);
        return new PictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PictureViewHolder holder, int position) {
        String pictureUrl = pictures.get(position).getSrc();
        // Load the picture into the ImageView using a library like Glide or Picasso
        Picasso.get()
                .load(pictureUrl)
                .placeholder(android.R.drawable.progress_horizontal)
                .into(holder.pictureImageView);

    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    public class PictureViewHolder extends RecyclerView.ViewHolder {

        public ImageView pictureImageView;

        public PictureViewHolder(@NonNull View itemView) {
            super(itemView);
            pictureImageView = itemView.findViewById(R.id.pictureImageView);
        }
    }
}


package ch.epfl.culturequest.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SightSeeingArrayAdapter extends ArrayAdapter<String> {
    private final ArrayList<Boolean> selectedItems;
    private final ArrayList<String> selectedPlaces = new ArrayList<>();
    private final List<Button> buttons;

    public SightSeeingArrayAdapter(Context context, int resource, List<String> items, List<Button> buttons) {
        super(context, resource, items);
        this.selectedItems = new ArrayList<>(Collections.nCopies(items.size(), false));
        this.buttons = buttons;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = super.getView(position, convertView, parent);
        final TextView textView = view.findViewById(android.R.id.text1);
        if (selectedItems.get(position)) {
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextColor(Color.parseColor("#F27329"));
            selectedPlaces.add((String) textView.getText());
            buttons.forEach(b -> b.setAlpha(1f));
            buttons.forEach(b -> b.setClickable(true));
        } else {
            textView.setTypeface(null, Typeface.NORMAL);
            textView.setTextColor(Color.parseColor("#000000"));
            selectedPlaces.remove((String) textView.getText());
            if (selectedItems.stream().allMatch(Boolean.FALSE::equals)) {
                buttons.forEach(b -> b.setAlpha(0.5f));
                buttons.forEach(b -> b.setClickable(false));
                Log.d("SightseeingActivity", "Invite friends button disabled");
            }
        }
        view.setOnClickListener(v -> {
            selectedItems.set(position, !selectedItems.get(position));
            notifyDataSetChanged();
        });
        return view;
    }

    public List<String> getSelectedPlaces(){
        return selectedPlaces;
    }
}

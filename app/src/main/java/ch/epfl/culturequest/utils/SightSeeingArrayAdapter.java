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

/**
 * An array adapter for the sightseeing part of the app
 */
public class SightSeeingArrayAdapter extends ArrayAdapter<String> {
    private final ArrayList<Boolean> selectedItems;
    private final ArrayList<String> selected = new ArrayList<>();
    private final List<Button> buttons;

    public SightSeeingArrayAdapter(Context context, int resource, List<String> items, List<Button> buttons) {
        super(context, resource, items);
        //we use a list of booleans to select which items are selected. It's like if we map each element in the list view with a boolean
        this.selectedItems = new ArrayList<>(Collections.nCopies(items.size(), false));
        //we have buttons to modify their alpha and clickability based on the amount of items selected
        this.buttons = buttons;
    }

    /**
     * Here we deal with what happens in the array adapter. Basically what we do when we have a list of places to visit,
     * we highight them in orange so that users can easily see what theyve selected. This way, we add or remove items selected
     * easily. Also, we make the buttons clickable if at least one item is selected. We also use this when selecting friends to invite.
     *
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return the view with updated selected elements
     */
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = super.getView(position, convertView, parent);
        final TextView textView = view.findViewById(android.R.id.text1);
        if (selectedItems.get(position)) {
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextColor(Color.parseColor("#F27329"));
            selected.add((String) textView.getText());
            buttons.forEach(b -> b.setAlpha(1f));
            buttons.forEach(b -> b.setClickable(true));
        } else {
            textView.setTypeface(null, Typeface.NORMAL);
            textView.setTextColor(Color.parseColor("#000000"));
            selected.remove((String) textView.getText());
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

    /**
     * We use this to get the items selected easily
     * @return
     */
    public List<String> getSelected(){
        return selected;
    }
}

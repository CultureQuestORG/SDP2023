package ch.epfl.culturequest.ui.events.tournaments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import ch.epfl.culturequest.R;


public class QuizzUnavailableDialog extends DialogFragment {

    private final String text;

    public QuizzUnavailableDialog() {
        this.text = "This quiz is not available, please scan this artwork to unlock it.";
    }

    public QuizzUnavailableDialog(String text) {
        this.text = text;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(text)
                .setPositiveButton(R.string.understood, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

}
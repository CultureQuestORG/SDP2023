package ch.epfl.culturequest.ui.scan;


import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.backend.LocalStorage;
import ch.epfl.culturequest.databinding.FragmentScanBinding;

public class ScanFragment extends Fragment {

    private FragmentScanBinding binding;
    public LocalStorage localStorage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ScanViewModel ScanViewModel =
                new ViewModelProvider(this).get(ScanViewModel.class);

        binding = FragmentScanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Creates the LocalStorage to store the images locally
        ContentResolver resolver = requireActivity().getApplicationContext().getContentResolver();
        localStorage = new LocalStorage(resolver);

        // Adds a listener to the scan button and performs action
        binding.scanAction.scanButton.setOnClickListener(view -> {
            // Creates the bitmap image from the drawable folder
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.joconde);
            boolean isWifiAvailable = false;
            try {
                localStorage.storeImageLocally(bitmap, isWifiAvailable);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        final ImageButton imageButton = binding.helpButtonScan;
        imageButton.setOnClickListener(view -> helpButtonDialog());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void helpButtonDialog() {
        DialogFragment newFragment = new FragmentScanHelp();
        newFragment.show(getParentFragmentManager(), "helpScan");
    }

}
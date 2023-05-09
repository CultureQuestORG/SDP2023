package ch.epfl.culturequest.ui.quiz;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import ch.epfl.culturequest.databinding.FragmentHomeBinding;
import ch.epfl.culturequest.databinding.FragmentQuizInterBinding;

public class QuizInterFragment extends Fragment {

    private FragmentQuizInterBinding binding;

    enum WheelLocation {
        X2,
        X4,
        X8,
        PLUS_50,
        PLUS_100,
        PLUS_200,
        MINUS_50,
    }

    private WheelLocation[] wheelLocations= {
            WheelLocation.X2,
            WheelLocation.PLUS_100,
            WheelLocation.MINUS_50,
            WheelLocation.X8,
            WheelLocation.PLUS_50,
            WheelLocation.X4,
            WheelLocation.PLUS_200,

    };





    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQuizInterBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        binding.score.setText("100");


        binding.spinButton.setOnClickListener( a-> {
            binding.spinButton.setVisibility(View.INVISIBLE);
            binding.stopButton.setVisibility(View.INVISIBLE);

            //select random location
            int randomLocation = (int) Math.floor(Math.random() * wheelLocations.length);

            float angle = (float) (360*2+ randomLocation*360/wheelLocations.length + (Math.random() * 360/wheelLocations.length));
            WheelLocation location = wheelLocations[randomLocation];


            System.out.println("Spin button clicked");
            RotateAnimation rotate = new RotateAnimation(0, angle,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(5000);
            rotate.setFillAfter(true);

            rotate.setInterpolator(new DecelerateInterpolator());

            rotate.setAnimationListener(
                    new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            System.out.println("Animation started");
                        }

                        @SuppressLint("DefaultLocale")
                        @Override
                        public void onAnimationEnd(Animation animation) {

                            int newScore = computeScore(Integer.parseInt(binding.score.getText().toString()), location);
                            binding.newscore.setText(String.format("%d", newScore));

                            AlertDialog dial = new AlertDialog.Builder(getContext())
                                    .setTitle("You can have " + binding.newscore.getText().toString() + " points if you answer the next question correctly!")
                                    .setMessage("You can now go to the next question")
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        binding.nextButton.setVisibility(View.VISIBLE);
                                    })
                                    .create();
                            dial.show();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            System.out.println("Animation repeated");
                        }
                    }
            );

        binding.fortuneWheelImageView.startAnimation(rotate);


        });



        return root;

    }


    public int computeScore(int score, WheelLocation location) {
        switch (location) {
            case X2:
                return score * 2;
            case X4:
                return score * 4;
            case X8:
                return score * 8;
            case PLUS_50:
                return score + 50;
            case PLUS_100:
                return score + 100;
            case PLUS_200:
                return score + 200;
            case MINUS_50:
                return score - 50;
            default:
                return score;
        }
    }
}

package ch.epfl.culturequest.ui.commons;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import ch.epfl.culturequest.R;

public class LikeAnimation extends FrameLayout {

    private final ValueAnimator valueAnimator;
    private boolean visible = false;

    public LikeAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
        valueAnimator = ValueAnimator.ofInt(1,100);
        valueAnimator.setDuration(2000);
        valueAnimator.addUpdateListener(animation -> {
            if ((Integer) animation.getAnimatedValue() < 95) {
                if(!visible) {
                    visible = true;
                    this.setVisibility(View.VISIBLE);
                }

                if((Integer) animation.getAnimatedValue() < 10) {
                    this.setAlpha((Integer) animation.getAnimatedValue() / 10f);
                } else if((Integer) animation.getAnimatedValue() > 80) {
                    this.setAlpha(1 - ((Integer) animation.getAnimatedValue() - 80) / 20f);
                } else {
                    this.setAlpha(1);
                }

            } else {
                if(visible) {
                    visible = false;
                    invalidate();
                    this.setVisibility(View.INVISIBLE);
                }
            }
        });

        inflate(getContext(), R.layout.like_anim, this);
        this.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * Plays the animation
     */
    public void play() {
        valueAnimator.setCurrentFraction(0);
        valueAnimator.start();
    }
}

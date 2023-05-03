package ch.epfl.culturequest.ui.commons;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

import ch.epfl.culturequest.R;

public class LoadingAnimation extends View {

    private final ValueAnimator valueAnimator;
    private int animValue = 0;
    private final int STROKE_WIDTH = 30;
    private final Paint paint;
    private final RectF circle;
    private final RectF background;


    /**
     * Constructor for the loading animation
     * @param context the context of the activity
     * @param attrs the attributes of the view
     */
    public LoadingAnimation(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        circle = new RectF();
        background = new RectF();

        //Initialize the value animator
        valueAnimator = ValueAnimator.ofInt(1,360);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.addUpdateListener(animation -> setValue((Integer) animation.getAnimatedValue()));

        this.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Initialize the background and the circle
        background.set(0,0,getWidth(),getHeight());
        circle.set(STROKE_WIDTH, STROKE_WIDTH,getWidth() - STROKE_WIDTH,getWidth() - STROKE_WIDTH);

        //Draw the background circle
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH);
        paint.setColor(getResources().getColor(R.color.grey, null));
        canvas.drawArc(circle,0,360,false,paint);

        //Draw the loading circle
        paint.setColor(getResources().getColor(R.color.colorPrimary, null));
        canvas.drawArc(circle,animValue,animValue,false,paint);
    }

    /**
     * Display the loading animation
     */
    public void startLoading() {
        this.setVisibility(View.VISIBLE);
        valueAnimator.start();
    }

    /**
     * Stop the loading animation
     */
    public void stopLoading() {
        this.setVisibility(View.INVISIBLE);
        valueAnimator.end();
    }

    private void setValue(int animatedValue) {
        animValue = animatedValue;
        invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent (MotionEvent me) {
        return true;
    }
}

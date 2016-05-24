package gr.certh.ireteth.retour;

import android.support.annotation.FractionRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * Created by dimitriskoutsaftikis on 11/11/15.
 */

public class ResizeAnimation extends Animation {

    private boolean pumpUp;
    private int deltaHeight; // distance between start and end height
    private View view;

    /**
     * constructor, do not forget to use the setParams(int, int) method before
     * starting the animation
     *
     * @param v
     */
    public ResizeAnimation(View v) {
        this.view = v;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();

        if (pumpUp){
           // params.height = (int) (startHeight - deltaHeight * interpolatedTime);
            params.topMargin = (int) (-deltaHeight+deltaHeight * interpolatedTime);
        }
        else {
          //  params.height = (int) ((startHeight - deltaHeight) + deltaHeight * interpolatedTime);
            params.topMargin = (int)(deltaHeight - deltaHeight * interpolatedTime);
        }

        view.requestLayout();
    }

    /**
     * set the starting and ending height for the resize animation starting
     * height is usually the views current height, the end height is the height
     * we want to reach after the animation is completed
     *
     * @param start
     *            height in pixels
     * @param end
     *            height in pixels
     */
    public void setParams(int deltaHeight, boolean pumpUp) {

        this.pumpUp = pumpUp;
        this.deltaHeight = deltaHeight;
    }

    /**
     * set the duration for the hideshowanimation
     */
    @Override
    public void setDuration(long durationMillis) {
        super.setDuration(durationMillis);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
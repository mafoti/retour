package gr.certh.ireteth.retour;

/**
 * Created by dimitriskoutsaftikis on 11/13/15.
 */

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class WorkaroundMapFragment extends SupportMapFragment {
    private OnTouchListener mListener;
    private OnDoubleClickListener doubleClickListener;
    public boolean isDoubleClickListenerEnabled = true;

    public LatLng newMarkerLocation;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstance) {
        View layout = super.onCreateView(layoutInflater, viewGroup, savedInstance);
        TouchableWrapper frameLayout = new TouchableWrapper(getActivity());
        frameLayout.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));

        ((ViewGroup) layout).addView(frameLayout,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return layout;
    }

    public void setListener(OnTouchListener listener) {
        mListener = listener;
    }

    public interface OnTouchListener {
       void onTouch();
    }

    public void setOnDoubleClickListener(OnDoubleClickListener listener) {
        doubleClickListener = listener;
    }

    public interface OnDoubleClickListener {
        void onDoubleClick();
    }


    public class TouchableWrapper extends FrameLayout {

        /* variable for counting two successive up-down events */
        int clickCount = 0;
        /*variable for storing the time of first click*/
        long startTime;
        /* variable for calculating the total time*/
        long duration;
        /* constant for defining the time duration between the click that can be considered as double-tap */
        static final int MAX_DURATION = 500;

        public TouchableWrapper(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    mListener.onTouch();
                    /*
                    if (isDoubleClickListenerEnabled == false){
                        clickCount = 0;
                        duration = 0;
                        isDoubleClickListenerEnabled = true;
                        break;
                    }
                    */

                    clickCount++;

                    if (clickCount==1){
                        startTime = System.currentTimeMillis();
                    }

                    else if(clickCount == 2)
                    {
                        duration =  System.currentTimeMillis() - startTime;
                        if(duration <= MAX_DURATION)
                        {
                            Point point = new Point();
                            point.x = (int)event.getX();
                            point.y = (int)event.getY();
                            newMarkerLocation = getMap().getProjection().fromScreenLocation(point);
                            doubleClickListener.onDoubleClick();
                            clickCount = 0;
                            duration = 0;
                        }else{
                            clickCount = 1;
                            startTime = System.currentTimeMillis();
                        }
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    mListener.onTouch();
                    break;
                }
            }
            return super.dispatchTouchEvent(event);
        }
    }
}
package com.thealer.telehealer.common.Animation;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by rsekar on 1/8/19.
 */

public class MoveViewTouchListener
        implements View.OnTouchListener
{
    private GestureDetector mGestureDetector;
    private View mView;

    public MoveViewTouchListener(View view) {
        mGestureDetector = new GestureDetector(view.getContext(), mGestureListener);
        mView = view;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_BUTTON_PRESS) {
            v.performClick();
        }

        return mGestureDetector.onTouchEvent(event);
    }


    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener()
    {
        private float mMotionDownX, mMotionDownY;

        @Override
        public boolean onDown(MotionEvent e) {
            mMotionDownX = e.getRawX() - mView.getTranslationX();
            mMotionDownY = e.getRawY() - mView.getTranslationY();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            float transformX = e2.getRawX() - mMotionDownX;
            float transformY = e2.getRawY() - mMotionDownY;

            if (mView.getParent() == null) {
                return true;
            }

            View parent = (View) mView.getParent();
            float minX = parent.getX();
            float minY = parent.getY();
            float maxX = parent.getX() + parent.getWidth();
            float maxY = parent.getY() + parent.getHeight();

            Log.d("MoveViewTouchListener","transformX : "+transformX);
            Log.d("MoveViewTouchListener","transformY : "+transformY);

            Log.d("MoveViewTouchListener","minX : "+minX);
            Log.d("MoveViewTouchListener","maxX : "+maxX);
            Log.d("MoveViewTouchListener","maxY : "+maxY);
            Log.d("MoveViewTouchListener","minY : "+minY);
            Log.d("MoveViewTouchListener","mView.getWidth() : "+mView.getWidth());
            Log.d("MoveViewTouchListener","mView.getHeight() : "+mView.getHeight());
            Log.d("MoveViewTouchListener","mView.getX() : "+mView.getX());
            Log.d("MoveViewTouchListener","mView.getY() : "+mView.getY());

           /* if (transformX + mView.getX() > minX && transformX + mView.getX() < (maxX - mView.getWidth())) {
                mView.setTranslationX(transformX);
            }

            if (transformY + mView.getY() > minY && transformY + mView.getY() < (maxY - mView.getHeight())) {
                mView.setTranslationY(transformY);
            }*/

            mView.setTranslationX(transformX);
            mView.setTranslationY(transformY);

            return true;
        }

    };
}
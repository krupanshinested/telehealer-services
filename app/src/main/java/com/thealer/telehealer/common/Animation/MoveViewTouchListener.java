package com.thealer.telehealer.common.Animation;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by rsekar on 1/8/19.
 */

public class MoveViewTouchListener
        implements View.OnTouchListener {

    private GestureDetector mGestureDetector;
    private View mView;
    private float previousX;
    private float previousY;

    public MoveViewTouchListener(View view) {
        mGestureDetector = new GestureDetector(view.getContext(), mGestureListener);
        mView = view;
        previousX = view.getX();
        previousY = view.getY();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP && previousX == v.getX() && previousY == v.getY()) {
            v.performClick();
            return true;
        }

        previousX = v.getX();
        previousY = v.getY();

        return mGestureDetector.onTouchEvent(event);
    }


    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
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

            float actualX = mView.getX() - mView.getTranslationX();
            float actualY = mView.getY() - mView.getTranslationY();

            if (actualX + transformX >= minX && actualX + transformX <= (maxX - mView.getWidth())) {
                mView.setTranslationX(transformX);
            }

            if (actualY + transformY >= minY && actualY + transformY <= (maxY - mView.getHeight())) {
                mView.setTranslationY(transformY);
            }

            return true;
        }

    };
}
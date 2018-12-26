package com.thealer.telehealer.common.Animation;

import android.support.constraint.ConstraintSet;

/**
 * Created by rsekar on 12/27/18.
 */

public class ConstrainSetUtil {
    public static void clearLeftRightConstraint(ConstraintSet constraintSet, int viewId) {
        constraintSet.clear(viewId,ConstraintSet.LEFT);
        constraintSet.clear(viewId,ConstraintSet.RIGHT);
    }

    public static void assignLeftAndRightToMain(ConstraintSet constraintSet,int parentViewId,int childViewId,int margin)  {
        constraintSet.connect(childViewId,ConstraintSet.START,parentViewId,ConstraintSet.START,margin);
        constraintSet.connect(childViewId,ConstraintSet.END,parentViewId,ConstraintSet.END,margin);
    }

    public static void assignTopAndBottomToMain(ConstraintSet constraintSet,int parentViewId,int childViewId,int margin)  {
        constraintSet.connect(childViewId,ConstraintSet.TOP,parentViewId,ConstraintSet.TOP,margin);
        constraintSet.connect(childViewId,ConstraintSet.BOTTOM,parentViewId,ConstraintSet.BOTTOM,margin);
    }

    public static void clearAllConstraint(ConstraintSet constraintSet, int viewId) {
        constraintSet.clear(viewId, ConstraintSet.BOTTOM);
        constraintSet.clear(viewId, ConstraintSet.TOP);
        ConstrainSetUtil.clearLeftRightConstraint(constraintSet,viewId);
    }
}

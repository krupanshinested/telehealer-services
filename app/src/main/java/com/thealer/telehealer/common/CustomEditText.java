package com.thealer.telehealer.common;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextWatcher;
import android.util.AttributeSet;

import java.util.ArrayList;

/**
 * Created by Aswin on 21,May,2019
 */
public class CustomEditText extends AppCompatEditText {
    private ArrayList<TextWatcher> watcherArrayList;

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        if (watcherArrayList == null) {
            watcherArrayList = new ArrayList<>();
        }
        watcherArrayList.add(watcher);
        super.addTextChangedListener(watcher);
    }

    @Override
    public void removeTextChangedListener(TextWatcher watcher) {
        if (watcherArrayList != null) {
            int i = watcherArrayList.indexOf(watcher);
            if (i >= 0) {
                watcherArrayList.remove(i);
            }
        }
        super.removeTextChangedListener(watcher);
    }

    public void clearAllTextChangedListener() {
        if (watcherArrayList != null) {
            for (TextWatcher watcher : watcherArrayList) {
                super.removeTextChangedListener(watcher);
            }
            watcherArrayList.clear();
            watcherArrayList = null;
        }
    }
}

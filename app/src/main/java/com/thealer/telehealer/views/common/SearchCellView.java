package com.thealer.telehealer.views.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.thealer.telehealer.R;

public class SearchCellView extends ConstraintLayout implements TextWatcher {

    private EditText searchEt;
    private ImageView searchClearIV;
    @Nullable
    private SearchInterface searchInterface;

    public SearchCellView(Context context) {
        super(context);
        initView();
    }

    public SearchCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void setSearchInterface(@Nullable SearchInterface searchInterface) {
        this.searchInterface = searchInterface;
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.layout_search_view, this, true);

        searchEt = view.findViewById(R.id.search_et);
        searchClearIV = view.findViewById(R.id.search_clear_iv);

        searchEt.addTextChangedListener(this);


        searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent != null && (keyEvent.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
                    if (searchInterface != null) {
                        searchInterface.doSearch();
                    }
                }
                return false;
            }
        });

        searchClearIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEt.setText(null);
                if (searchInterface != null) {
                    searchInterface.doSearch();
                }
            }
        });

    }

    public String getCurrentSearchResult() {
        String searchText = searchEt.getText().toString();
        if (TextUtils.isEmpty(searchText)) {
            return null;
        } else {
            return searchText;
        }
    }

    public  void  setSearchEtHint(String hint){
        searchEt.setHint(hint);

    }
    public void setSearchHint(String searchHint) {
        searchEt.setHint(searchHint);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }



    @Override
    public void afterTextChanged(Editable editable) {
        if (TextUtils.isEmpty(editable.toString())) {
            searchClearIV.setVisibility(GONE);
        } else {
            searchClearIV.setVisibility(VISIBLE);
        }
    }
}


package com.thealer.telehealer.views.Procedure;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 22,July,2019
 */
public class SelectProceduresFragment extends BaseFragment implements View.OnClickListener {
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private RecyclerView cptListRv;
    private CardView selectedCv;
    private RecyclerView selectedCptRv;
    private Button doneBtn;


    private OnCloseActionInterface onCloseActionInterface;

    private ArrayList<String> selectedItems = new ArrayList<>();
    private List<String> CPT_CODE_LIST;

    private CptListAdapter cptListAdapter;
    private SelectedChipListAdapter selectedChipListAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_procedure, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        cptListRv = (RecyclerView) view.findViewById(R.id.cpt_list_rv);
        selectedCv = (CardView) view.findViewById(R.id.selected_cv);
        selectedCptRv = (RecyclerView) view.findViewById(R.id.selected_cpt_rv);
        doneBtn = (Button) view.findViewById(R.id.done_btn);

        toolbarTitle.setText(getString(R.string.cpt));

        backIv.setOnClickListener(this);
        doneBtn.setOnClickListener(this);

        CPT_CODE_LIST = ProcedureConstants.getItems();

        if (getArguments() != null) {
            selectedItems = getArguments().getStringArrayList(ArgumentKeys.SELECTED_ITEMS);
        }

        cptListAdapter = new CptListAdapter(getActivity(), new OnListItemSelectInterface() {
            @Override
            public void onListItemSelected(int position, Bundle bundle) {
                if (selectedItems.contains(CPT_CODE_LIST.get(position))) {
                    selectedItems.remove(CPT_CODE_LIST.get(position));
                } else {
                    selectedItems.add(CPT_CODE_LIST.get(position));
                }

                selectedChipListAdapter.setData(selectedItems);

                showOrhideSelectedList();
            }
        });

        cptListAdapter.setData(CPT_CODE_LIST, selectedItems);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        cptListRv.setLayoutManager(linearLayoutManager);
        cptListRv.setAdapter(cptListAdapter);


        selectedChipListAdapter = new SelectedChipListAdapter(getActivity(), new OnListItemSelectInterface() {
            @Override
            public void onListItemSelected(int position, Bundle bundle) {
                selectedItems.remove(position);
                cptListAdapter.setSelectedItems(selectedItems);
                showOrhideSelectedList();
            }
        });

        selectedChipListAdapter.setData(selectedItems);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        selectedCptRv.setLayoutManager(layoutManager);
        selectedCptRv.setAdapter(selectedChipListAdapter);
    }

    private void showOrhideSelectedList() {
        if (selectedItems.size() > 0) {
            selectedCv.setVisibility(View.VISIBLE);
        } else {
            selectedCv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onCloseActionInterface.onClose(false);
                break;
            case R.id.done_btn:
                if (getTargetFragment() != null) {
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList(ArgumentKeys.SELECTED_ITEMS, selectedItems);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent().putExtras(bundle));
                }
                onCloseActionInterface.onClose(false);
                break;
        }
    }
}

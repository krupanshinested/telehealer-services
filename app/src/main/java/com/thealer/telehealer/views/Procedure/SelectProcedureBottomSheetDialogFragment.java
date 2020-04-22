package com.thealer.telehealer.views.Procedure;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.views.base.BaseBottomSheetDialogFragment;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 23,July,2019
 */
public class SelectProcedureBottomSheetDialogFragment extends BaseBottomSheetDialogFragment {
    private RecyclerView cptListRv;
    private OnListItemSelectInterface onListItemSelectInterface;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onListItemSelectInterface = (OnListItemSelectInterface) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.cloneInContext(contextThemeWrapper).inflate(R.layout.procedure_select_dialog, container, false);
        initView(view);
        setBottomSheetHeight(view, 75);
        return view;
    }

    private void initView(View view) {
        cptListRv = (RecyclerView) view.findViewById(R.id.cpt_list_rv);

        ArrayList<String> selectedItems = new ArrayList<>();

        if (getArguments() != null) {
            String selectedItem = getArguments().getString(ArgumentKeys.SELECTED_ITEMS);
            if (selectedItem != null) {
                selectedItems.add(selectedItem);
            }
        }

        List<String> CPT_CODE_LIST = ProcedureConstants.getItems();

        CptListAdapter cptListAdapter = new CptListAdapter(getActivity(), onListItemSelectInterface);

        cptListRv.setLayoutManager(new LinearLayoutManager(getActivity()));

        cptListAdapter.setData(CPT_CODE_LIST, selectedItems);

        cptListRv.setAdapter(cptListAdapter);
    }
}

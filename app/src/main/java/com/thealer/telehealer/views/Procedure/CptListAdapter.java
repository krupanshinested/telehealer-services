package com.thealer.telehealer.views.Procedure;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 22,July,2019
 */
public class CptListAdapter extends RecyclerView.Adapter<CptListAdapter.ViewHolder> {
    private FragmentActivity activity;
    private OnListItemSelectInterface onListItemSelectInterface;
    private List<String> cptList;
    private List<String> selectedItems;

    public CptListAdapter(FragmentActivity activity, OnListItemSelectInterface onListItemSelectInterface) {
        this.activity = activity;
        this.onListItemSelectInterface = onListItemSelectInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_cpt_code_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.itemCb.setText(ProcedureConstants.getTitle(activity, cptList.get(position)));

        holder.itemCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onListItemSelectInterface.onListItemSelected(position, null);
            }
        });

        holder.infoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showAlertDialog(activity,
                        ProcedureConstants.getTitle(activity, cptList.get(position)),
                        ProcedureConstants.getDescription(activity, cptList.get(position)),
                        activity.getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, null);
            }
        });

        boolean isAvailable = false;
        for (int i = 0; i < selectedItems.size(); i++) {
            if (selectedItems.get(i).trim().equals(cptList.get(position).trim())) {
                isAvailable = true;
                break;
            }
        }

        holder.itemCb.setChecked(isAvailable);
    }

    @Override
    public int getItemCount() {
        return cptList.size();
    }

    public void setData(List<String> items, ArrayList<String> selectedItems) {
        this.cptList = items;
        this.selectedItems = selectedItems;
    }

    public void setSelectedItems(ArrayList<String> selectedItems) {
        this.selectedItems = selectedItems;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox itemCb;
        private ImageView infoIv;
        private View bottomView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCb = (CheckBox) itemView.findViewById(R.id.item_cb);
            infoIv = (ImageView) itemView.findViewById(R.id.info_iv);
            bottomView = (View) itemView.findViewById(R.id.bottom_view);
        }
    }
}

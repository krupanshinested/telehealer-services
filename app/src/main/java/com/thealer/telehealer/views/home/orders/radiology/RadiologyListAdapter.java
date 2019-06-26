package com.thealer.telehealer.views.home.orders.radiology;

import androidx.lifecycle.ViewModelProviders;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.views.common.CustomDialogs.ItemPickerDialog;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 11,December,2018
 */
class RadiologyListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private FragmentActivity activity;
    private List<RadiologyListModel> radiologyModelList;
    private List<RadiologyListModel> selectedRadiologyModelList = new ArrayList<>();
    private List<String> selectedIdList = new ArrayList<>();
    private RadiologyListViewModel radiologyListViewModel;

    public RadiologyListAdapter(FragmentActivity activity, List<RadiologyListModel> radiologyModelList) {
        this.activity = activity;
        this.radiologyModelList = new RadiologyConstants().getRadiologyListModel();
        radiologyListViewModel = ViewModelProviders.of(activity).get(RadiologyListViewModel.class);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view;
        switch (i) {
            case RadiologyConstants.TYPE_SECTION:
                view = layoutInflater.inflate(R.layout.adapter_radiology_view_section, viewGroup, false);
                return new SectionViewHolder(view);
            case RadiologyConstants.TYPE_HEADER:
                view = layoutInflater.inflate(R.layout.adapter_radiology_view_header, viewGroup, false);
                return new HeaderViewHolder(view);
            case RadiologyConstants.TYPE_SELECT_ALL:
            case RadiologyConstants.TYPE_RL:
            case RadiologyConstants.TYPE_INPUT:
            case RadiologyConstants.TYPE_ITEM:
                view = layoutInflater.inflate(R.layout.adapter_radiology_view_item, viewGroup, false);
                return new ItemViewHolder(view);
            case RadiologyConstants.TYPE_SUB_ITEM:
                view = layoutInflater.inflate(R.layout.adapter_radiology_view_sub_item, viewGroup, false);
                return new SubItemViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        switch (radiologyModelList.get(i).getType()) {
            case RadiologyConstants.TYPE_SECTION:
                SectionViewHolder sectionViewHolder = (SectionViewHolder) viewHolder;
                sectionViewHolder.sectionTv.setText(radiologyModelList.get(i).getSection());
                break;
            case RadiologyConstants.TYPE_HEADER:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
                headerViewHolder.headerTv.setText(radiologyModelList.get(i).getHeader());
                break;
            case RadiologyConstants.TYPE_SUB_ITEM:
                SubItemViewHolder subItemViewHolder = (SubItemViewHolder) viewHolder;
                subItemViewHolder.subItemCb.setText(radiologyModelList.get(i).getItem());

                subItemViewHolder.subItemCb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (subItemViewHolder.subItemCb.isChecked()) {
                            addItem(radiologyModelList.get(i));
                        } else {
                            removeItem(radiologyModelList.get(i));
                        }
                    }
                });
                if (selectedIdList.contains(radiologyModelList.get(i).getId())) {
                    subItemViewHolder.subItemCb.setChecked(true);
                } else {
                    subItemViewHolder.subItemCb.setChecked(false);
                }
                break;
            case RadiologyConstants.TYPE_ITEM:
            case RadiologyConstants.TYPE_SELECT_ALL:
            case RadiologyConstants.TYPE_RL:
            case RadiologyConstants.TYPE_INPUT:
                ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
                itemViewHolder.itemCb.setText(radiologyModelList.get(i).getItem());

                itemViewHolder.itemCb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemViewHolder.itemCb.isChecked()) {
                            if (radiologyModelList.get(i).isIsRLType()) {
                                addItemWithRLOption(itemViewHolder.itemCb, radiologyModelList.get(i));
                            } else if (radiologyModelList.get(i).isIsAdditionalTextRequired()) {
                                addItemWithInputInfo(itemViewHolder.itemCb, radiologyModelList.get(i));
                            } else {
                                addItem(radiologyModelList.get(i));
                            }
                        } else {
                            if (radiologyModelList.get(i).getType() == RadiologyConstants.TYPE_SELECT_ALL) {
                                removeAllSubItems(radiologyModelList.get(i));
                            } else {
                                removeItem(radiologyModelList.get(i));
                            }
                        }
                    }
                });

                if (selectedIdList.contains(radiologyModelList.get(i).getId())) {
                    itemViewHolder.itemCb.setChecked(true);
                } else {
                    itemViewHolder.itemCb.setChecked(false);
                }
                break;
        }

    }

    private void addItem(RadiologyListModel radiologyListModel) {
        selectedRadiologyModelList.add(radiologyListModel);
        selectedIdList.add(radiologyListModel.getId());

        radiologyListViewModel.getSelectedRadiologyListLiveData().setValue(selectedRadiologyModelList);
        radiologyListViewModel.getSelectedIdList().setValue(selectedIdList);
    }

    private void removeItem(RadiologyListModel radiologyListModel) {
        for (int j = 0; j < selectedRadiologyModelList.size(); j++) {
            if (selectedRadiologyModelList.get(j).getId().equals(radiologyListModel.getId())) {
                selectedRadiologyModelList.remove(j);
                break;
            }
        }
        selectedIdList.remove(radiologyListModel.getId());

        radiologyListViewModel.getSelectedRadiologyListLiveData().setValue(selectedRadiologyModelList);
        radiologyListViewModel.getSelectedIdList().setValue(selectedIdList);
    }

    private void removeAllSubItems(RadiologyListModel radiologyListModel) {
        for (int i = 0; i < selectedRadiologyModelList.size(); i++) {
            if (selectedRadiologyModelList.get(i).getSelectAll() != null &&
                    selectedRadiologyModelList.get(i).getSelectAll().equals(radiologyListModel.getItem())) {
                selectedIdList.remove(selectedRadiologyModelList.get(i).getId());
                selectedRadiologyModelList.remove(i);
                i--;
            }
        }

        selectedIdList.remove(radiologyListModel.getId());
        selectedRadiologyModelList.remove(radiologyListModel);

        radiologyListViewModel.getSelectedRadiologyListLiveData().setValue(selectedRadiologyModelList);
        radiologyListViewModel.getSelectedIdList().setValue(selectedIdList);
    }

    private void addItemWithRLOption(CheckBox itemCb, RadiologyListModel radiologyListModel) {
        ItemPickerDialog itemPickerDialog = new ItemPickerDialog(activity, radiologyListModel.getItem(), RadiologyConstants.rlType, new PickerListener() {
            @Override
            public void didSelectedItem(int position) {
                radiologyListModel.setRlTypeString(RadiologyConstants.rlTypeValue.get(position));

                selectedRadiologyModelList.add(radiologyListModel);
                selectedIdList.add(radiologyListModel.getId());

                radiologyListViewModel.getSelectedRadiologyListLiveData().setValue(selectedRadiologyModelList);
                radiologyListViewModel.getSelectedIdList().setValue(selectedIdList);
            }

            @Override
            public void didCancelled() {
                //do nothing
                itemCb.setChecked(false);
            }
        });
        itemPickerDialog.show();
    }

    private void addItemWithInputInfo(CheckBox itemCb, RadiologyListModel radiologyListModel) {
        AlertDialog builder = new AlertDialog.Builder(activity).create();
        View view = activity.getLayoutInflater().inflate(R.layout.layout_radiology_additional_info, null);
        EditText otherEt;
        Button cancelBtn;
        Button doneBtn;
        otherEt = (EditText) view.findViewById(R.id.other_et);
        cancelBtn = (Button) view.findViewById(R.id.cancel_btn);
        doneBtn = (Button) view.findViewById(R.id.done_btn);

        builder.setView(view);

        otherEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    doneBtn.setEnabled(false);
                    doneBtn.setTextColor(activity.getColor(R.color.colorGrey));
                } else {
                    doneBtn.setEnabled(true);
                    doneBtn.setTextColor(activity.getColor(R.color.color_blue));
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
                itemCb.setChecked(false);
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radiologyListModel.setAdditionalInformation(otherEt.getText().toString());

                selectedRadiologyModelList.add(radiologyListModel);
                selectedIdList.add(radiologyListModel.getId());

                radiologyListViewModel.getSelectedRadiologyListLiveData().setValue(selectedRadiologyModelList);
                radiologyListViewModel.getSelectedIdList().setValue(selectedIdList);

                builder.dismiss();
            }
        });

        builder.show();
    }

    @Override
    public int getItemCount() {
        return radiologyModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return radiologyModelList.get(position).getType();
    }

    public void setData(List<RadiologyListModel> radiologyModelList, List<String> selectedIdList) {
        this.selectedRadiologyModelList = radiologyModelList;
        this.selectedIdList = selectedIdList;
        notifyDataSetChanged();
    }

    public void setData(List<RadiologyListModel> radiologyModelList) {
        this.radiologyModelList = radiologyModelList;
        notifyDataSetChanged();
    }

    class SectionViewHolder extends RecyclerView.ViewHolder {
        private TextView sectionTv;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionTv = (TextView) itemView.findViewById(R.id.section_tv);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView headerTv;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTv = (TextView) itemView.findViewById(R.id.header_tv);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private CheckBox itemCb;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCb = (CheckBox) itemView.findViewById(R.id.item_cb);
        }
    }

    class SubItemViewHolder extends RecyclerView.ViewHolder {
        private CheckBox subItemCb;

        public SubItemViewHolder(@NonNull View itemView) {
            super(itemView);
            subItemCb = (CheckBox) itemView.findViewById(R.id.sub_item_cb);
        }
    }
}

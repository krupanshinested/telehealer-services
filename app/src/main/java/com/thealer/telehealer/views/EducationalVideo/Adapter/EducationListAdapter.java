package com.thealer.telehealer.views.EducationalVideo.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalVideo;
import com.thealer.telehealer.common.BaseAdapter;
import com.thealer.telehealer.common.BaseAdapterObjectModel;
import com.thealer.telehealer.common.Utils;


import java.util.ArrayList;
import java.util.HashMap;

public class EducationListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<EducationalVideo> educationalVideos;
    @Nullable
    private EducationalListSelector listSelector;
    private boolean isSelectable = false;
    private ArrayList<String> selectedIds = new ArrayList<>();

    public EducationListAdapter(Context context, ArrayList<EducationalVideo> educationalVideos,EducationalListSelector listSelector) {
        assignValues(context,educationalVideos,listSelector);
    }

    public EducationListAdapter(Context context, ArrayList<EducationalVideo> educationalVideos,EducationalListSelector listSelector,ArrayList<String> selectedIds) {
        this.selectedIds = selectedIds;
        this.isSelectable = true;
        assignValues(context,educationalVideos,listSelector);

    }

    private void assignValues(Context context, ArrayList<EducationalVideo> educationalVideos,EducationalListSelector listSelector) {
        this.context = context;
        this.educationalVideos = educationalVideos;
        this.generateModel(new ArrayList<BaseAdapterObjectModel>(educationalVideos));
        this.listSelector = listSelector;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case BaseAdapter.headerType:
                View titleView = LayoutInflater.from(context).inflate(R.layout.adapter_title, viewGroup, false);
                return new TitleHolder(titleView);
            case BaseAdapter.bodyType:
                View dataView = LayoutInflater.from(context).inflate(R.layout.layout_educational_list, viewGroup, false);
                return new EducationListAdapter.DataHolder(dataView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        BaseAdapterModel baseAdapterModel = items.get(i);

        switch (baseAdapterModel.getType()) {
            case BaseAdapter.headerType:
                ((TitleHolder) viewHolder).titleTv.setText(baseAdapterModel.title);
                break;
            case BaseAdapter.bodyType:
                if (baseAdapterModel.actualValue instanceof EducationalVideo) {
                    EducationalVideo video = (EducationalVideo) baseAdapterModel.actualValue;
                    DataHolder holder = ((DataHolder) viewHolder);
                    holder.item_tv.setText(video.getTitle());
                    holder.subitem_tv.setText(video.getDescription());
                    Utils.setImageWithGlideWithoutDefaultPlaceholder(context,holder.icon,video.getAudio_stream_screenshot(),null,false,false);

                    holder.check_box.setClickable(false);
                    if (isSelectable) {
                        holder.check_box.setVisibility(View.VISIBLE);
                        holder.check_box.setChecked(selectedIds.contains(video.getVideo_id()+""));
                    } else {
                        holder.check_box.setVisibility(View.GONE);
                    }

                    holder.cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (listSelector != null) {
                                listSelector.didSelectEducationalVideo(video);
                            }

                            if (isSelectable) {
                                updateSelection(video,holder);
                            }
                        }
                    });

                }
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);

    }

    private void updateSelection(EducationalVideo video,DataHolder holder) {
        holder.check_box.setChecked(selectedIds.contains(video.getVideo_id()+""));
    }

    public void updateSelectionList(ArrayList<String> selectedIds) {
        this.selectedIds = selectedIds;
    }

    public void appedItems(ArrayList<EducationalVideo> educationalVideos) {
        this.educationalVideos.addAll(educationalVideos);
        this.generateModel(new ArrayList<BaseAdapterObjectModel>(this.educationalVideos));
        notifyDataSetChanged();
    }

    public void reset() {
        this.educationalVideos.clear();
        this.items.clear();
    }

    @Override protected void generateModel(ArrayList<BaseAdapterObjectModel> modelList) {
        if (!isSelectable) {
            super.generateModel(modelList);
            return;
        }

        HashMap<String, ArrayList<BaseAdapterObjectModel>> map = new HashMap<>();
        items = new ArrayList<>();
        for (BaseAdapterObjectModel model : modelList) {
            items.add(new BaseAdapterModel(model));
        }
    }

    private class DataHolder extends RecyclerView.ViewHolder {

        private TextView item_tv, subitem_tv;
        private ImageView icon;
        private CardView cardView;
        private CheckBox check_box;

        private DataHolder(@NonNull View itemView) {
            super(itemView);
            item_tv = itemView.findViewById(R.id.list_title_tv);
            subitem_tv = itemView.findViewById(R.id.list_sub_title_tv);
            icon = itemView.findViewById(R.id.icon);
            cardView = itemView.findViewById(R.id.list_item_cv);
            check_box = itemView.findViewById(R.id.check_box);
        }
    }

    public interface EducationalListSelector {
        public void didSelectEducationalVideo(EducationalVideo video);
    }

}

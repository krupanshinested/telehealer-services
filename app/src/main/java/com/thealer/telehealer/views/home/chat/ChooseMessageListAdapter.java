package com.thealer.telehealer.views.home.chat;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;

import java.util.List;

/**
 * Created by Aswin on 16,May,2019
 */
public class ChooseMessageListAdapter extends RecyclerView.Adapter<ChooseMessageListAdapter.ViewHolder> {
    private Context context;
    private List<String> messageList;
    private OnListItemSelectInterface onListItemSelectInterface;
    private int selectedPosition = -1;

    public ChooseMessageListAdapter(Context context, List<String> messageList, OnListItemSelectInterface onListItemSelectInterface) {
        this.context = context;
        this.messageList = messageList;
        this.onListItemSelectInterface = onListItemSelectInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_choose_message_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        if (i == selectedPosition) {
            viewHolder.doneIv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.doneIv.setVisibility(View.INVISIBLE);
        }
        viewHolder.messageTv.setText(messageList.get(i));
        viewHolder.itemCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = i;
                Bundle bundle = new Bundle();
                bundle.putString(ArgumentKeys.SELECTED_MESSAGE, messageList.get(i));
                onListItemSelectInterface.onListItemSelected(selectedPosition, bundle);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void setData(List<String> messageList) {
        this.messageList = messageList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout itemCl;
        private View bottomView;
        private TextView messageTv;
        private ImageView doneIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCl = (ConstraintLayout) itemView.findViewById(R.id.item_cl);
            bottomView = (View) itemView.findViewById(R.id.bottom_view);
            messageTv = (TextView) itemView.findViewById(R.id.message_tv);
            doneIv = (ImageView) itemView.findViewById(R.id.done_iv);
        }
    }
}

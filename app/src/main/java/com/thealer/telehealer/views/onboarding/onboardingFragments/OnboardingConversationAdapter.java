package com.thealer.telehealer.views.onboarding.onboardingFragments;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thealer.telehealer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 06,February,2019
 */
public class OnboardingConversationAdapter extends RecyclerView.Adapter<OnboardingConversationAdapter.ViewHolder> {

    private FragmentActivity fragmentActivity;
    private List<String> conversationList;

    public OnboardingConversationAdapter(FragmentActivity activity) {
        this.fragmentActivity = activity;
        conversationList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;

        if (i % 2 == 0) {
            view = LayoutInflater.from(fragmentActivity).inflate(R.layout.adapter_chat_left, viewGroup, false);
        } else {
            view = LayoutInflater.from(fragmentActivity).inflate(R.layout.adapter_chat_right, viewGroup, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (i % 2 == 0) {
            viewHolder.chatLeftTv.setText(conversationList.get(i));
        } else {
            viewHolder.chatRightTv.setText(conversationList.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public void addConversation(String conversation) {
        conversationList.add(conversation);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView chatLeftTv, chatRightTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chatLeftTv = itemView.findViewById(R.id.chat_left_tv);
            chatRightTv = itemView.findViewById(R.id.chat_right_tv);
        }
    }
}

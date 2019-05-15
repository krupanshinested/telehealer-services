package com.thealer.telehealer.views.home.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.chat.ChatApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.CustomTextView;
import com.thealer.telehealer.common.Signal.SignalModels.SignalKey;
import com.thealer.telehealer.common.Signal.SignalUtil.SignalManager;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aswin on 15,May,2019
 */
public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    private Context context;
    private List<ChatApiResponseModel.ResultBean> chatList = new ArrayList<>();
    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_MY_MESSAGE = 2;
    private static final int VIEW_TYPE_OTHER_MESSAGE = 3;
    private List<ChatListAdapterModel> chatListAdapterModelList = new ArrayList<>();
    private CommonUserApiResponseModel userModel;
    private String userGuid;
    private String userAvatar;
    private SignalKey userSignalKey, mySignalKey;

    public ChatListAdapter(Context context, CommonUserApiResponseModel userModel) {
        this.context = context;
        this.userModel = userModel;

        userGuid = UserDetailPreferenceManager.getUser_guid();
        userAvatar = UserDetailPreferenceManager.getUser_avatar();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = null;
        switch (i) {
            case VIEW_TYPE_MY_MESSAGE:
                view = layoutInflater.inflate(R.layout.adapter_chat_my_message, viewGroup, false);
                break;
            case VIEW_TYPE_OTHER_MESSAGE:
                view = layoutInflater.inflate(R.layout.adapter_chat_other_message, viewGroup, false);
                break;
            default:
                view = layoutInflater.inflate(R.layout.adapter_chat_header_view, viewGroup, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ChatListAdapterModel chatListAdapterModel = chatListAdapterModelList.get(i);
        switch (chatListAdapterModel.getType()) {
            case VIEW_TYPE_HEADER:
                String date = Utils.getDayMonthYear(chatListAdapterModel.getHeaderText());
                if (Utils.getCurrentFomatedDate().equals(date))
                    date = context.getString(R.string.today);

                viewHolder.chatHeaderTv.setText(date);
                break;
            case VIEW_TYPE_MY_MESSAGE:
            case VIEW_TYPE_OTHER_MESSAGE:
                if (chatListAdapterModel.isShowTime()) {
                    viewHolder.chatTimeTv.setVisibility(View.VISIBLE);
                    viewHolder.chatTimeTv.setText(Utils.getFormatedTime(chatListAdapterModel.getChatItem().getCreated_at()));
                } else {
                    viewHolder.chatTimeTv.setVisibility(View.GONE);
                }

                if (chatListAdapterModel.isShowAvatar()) {
                    viewHolder.userAvatarCiv.setVisibility(View.VISIBLE);
                    Utils.setImageWithGlide(context, viewHolder.userAvatarCiv, chatListAdapterModel.getAvatarUrl(), context.getDrawable(R.drawable.profile_placeholder), true, true);
                } else {
                    viewHolder.userAvatarCiv.setVisibility(View.INVISIBLE);
                }

                if (chatListAdapterModel.getType() == VIEW_TYPE_MY_MESSAGE) {
                    viewHolder.messageTv.setText(SignalManager.decryptMessage(chatListAdapterModel.getChatItem().getChatMessage(), mySignalKey, mySignalKey));
                } else {
                    viewHolder.messageTv.setText(SignalManager.decryptMessage(chatListAdapterModel.getChatItem().getChatMessage(), mySignalKey, userSignalKey));
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return chatListAdapterModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return chatListAdapterModelList.get(position).getType();
    }

    public void setData(List<ChatApiResponseModel.ResultBean> result, int page) {
        if (page == 1) {
            chatList = result;
        } else {
            chatList.addAll(result);
        }
        chatListAdapterModelList.clear();

        for (int i = chatList.size() - 1; i >= 0; i--) {

            if (i == chatList.size() - 1) {
                chatListAdapterModelList.add(new ChatListAdapterModel(VIEW_TYPE_HEADER, chatList.get(i).getCreated_at()));
            } else if (i != 0 && !Utils.getDayMonthYear(chatList.get(i).getCreated_at()).equals(Utils.getDayMonthYear(chatList.get(i - 1).getCreated_at()))) {
                chatListAdapterModelList.add(new ChatListAdapterModel(VIEW_TYPE_HEADER, chatList.get(i - 1).getCreated_at()));
            }

            ChatApiResponseModel.ResultBean previousItem = null;

            if (i != chatList.size() - 1) {
                previousItem = chatList.get(i + 1);
            }

            chatListAdapterModelList.add(getAdapterModel(chatList.get(i), previousItem));

        }

        if (page == 1) {
            notifyDataSetChanged();
        } else {
            notifyItemRangeInserted(0, result.size());
        }
    }

    public void setSignalKeys(SignalKey mySignalKey, SignalKey userSignalKey) {
        this.mySignalKey = mySignalKey;
        this.userSignalKey = userSignalKey;
    }

    private ChatListAdapterModel getAdapterModel(ChatApiResponseModel.ResultBean newItem, ChatApiResponseModel.ResultBean previousItem) {
        int type = VIEW_TYPE_MY_MESSAGE;
        boolean showAvatar = true, showTime = false;
        String avatar = null;

        if (!newItem.getUser().getUser_guid().equals(userGuid)) {
            type = VIEW_TYPE_OTHER_MESSAGE;
            if (userModel != null) {
                avatar = userModel.getUser_avatar();
            }
        } else {
            avatar = userAvatar;
        }

        if (previousItem != null && !Utils.getFormatedTime(previousItem.getCreated_at()).equals(Utils.getFormatedTime(newItem.getCreated_at()))) {
            showTime = true;
        }

        if (previousItem != null && previousItem.getUser().getUser_guid().equals(newItem.getUser().getUser_guid()) &&
                Utils.getDayMonthYear(previousItem.getCreated_at()).equals(Utils.getDayMonthYear(newItem.getCreated_at()))) {
            showAvatar = false;
        }

        return new ChatListAdapterModel(type, newItem, showAvatar, showTime, avatar);
    }

    public void addMessage(ChatApiResponseModel.ResultBean messageBean) {

        List<ChatApiResponseModel.ResultBean> resultBeanList = new ArrayList<>();
        resultBeanList.add(messageBean);
        resultBeanList.addAll(chatList);

        ChatApiResponseModel.ResultBean previousItem = null;
        if (!chatList.isEmpty()) {
            previousItem = chatList.get(0);
        }


        if (previousItem != null && !Utils.getDayMonthYear(messageBean.getCreated_at()).equals(Utils.getDayMonthYear(previousItem.getCreated_at()))) {
            chatListAdapterModelList.add(new ChatListAdapterModel(VIEW_TYPE_HEADER, messageBean.getCreated_at()));
        }

        chatListAdapterModelList.add(getAdapterModel(messageBean, previousItem));

        notifyItemInserted(chatListAdapterModelList.size());

        chatList.clear();
        chatList.addAll(resultBeanList);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CustomButton chatHeaderTv;
        private CircleImageView userAvatarCiv;
        private CustomTextView messageTv;
        private TextView chatTimeTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chatHeaderTv = (CustomButton) itemView.findViewById(R.id.chat_header_tv);
            userAvatarCiv = (CircleImageView) itemView.findViewById(R.id.chat_avatar_civ);
            messageTv = (CustomTextView) itemView.findViewById(R.id.chat_message_tv);
            chatTimeTv = (TextView) itemView.findViewById(R.id.chat_time_tv);
        }
    }

    public class ChatListAdapterModel {
        private int type;
        private String headerText;
        private ChatApiResponseModel.ResultBean chatItem;
        private boolean isShowAvatar;
        private boolean isShowTime;
        private String avatarUrl;

        public ChatListAdapterModel(int type, String headerText) {
            this.type = type;
            this.headerText = headerText;
        }

        public ChatListAdapterModel(int type, ChatApiResponseModel.ResultBean chatItem, boolean isShowAvatar, boolean isShowTime, String avatarUrl) {
            this.type = type;
            this.chatItem = chatItem;
            this.isShowAvatar = isShowAvatar;
            this.isShowTime = isShowTime;
            this.avatarUrl = avatarUrl;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getHeaderText() {
            return headerText;
        }

        public void setHeaderText(String headerText) {
            this.headerText = headerText;
        }

        public ChatApiResponseModel.ResultBean getChatItem() {
            return chatItem;
        }

        public void setChatItem(ChatApiResponseModel.ResultBean chatItem) {
            this.chatItem = chatItem;
        }

        public boolean isShowAvatar() {
            return isShowAvatar;
        }

        public void setShowAvatar(boolean showAvatar) {
            isShowAvatar = showAvatar;
        }

        public boolean isShowTime() {
            return isShowTime;
        }

        public void setShowTime(boolean showTime) {
            isShowTime = showTime;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }
    }
}

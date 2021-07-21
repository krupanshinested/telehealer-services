package com.thealer.telehealer.apilayer.models.chat;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

public class BroadcastMessageRequestModel implements Serializable {

    private List<MessagesBean> messages;

    public List<MessagesBean> getMessages() {
        return messages;
    }

    public void setMessages(List<MessagesBean> messages) {
        this.messages = messages;
    }

    public static class MessagesBean implements Serializable {
        public String to;
        public String receiver_one_message;
        public String sender_message;

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getReceiver_one_message() {
            return receiver_one_message;
        }

        public void setReceiver_one_message(String receiver_one_message) {
            if (TextUtils.isEmpty(receiver_one_message)) {
                this.receiver_one_message = "";
            } else {
                this.receiver_one_message = receiver_one_message.replaceAll("\n", "");
            }
        }


        public String getSender_message() {
            return sender_message;
        }

        public void setSender_message(String sender_message) {
            if (sender_message != null)
                this.sender_message = sender_message.replaceAll("\n", "");
        }
    }

}

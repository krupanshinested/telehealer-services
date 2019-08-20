package com.thealer.telehealer.apilayer.models.chat;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Aswin on 16,May,2019
 */
public class ChatMessageRequestModel implements Serializable {

    private String to;
    private List<MessagesBean> messages;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public List<MessagesBean> getMessages() {
        return messages;
    }

    public void setMessages(List<MessagesBean> messages) {
        this.messages = messages;
    }

    public static class MessagesBean implements Serializable {

        private String message;
        private String receiver_one_message;
        private String sender_message;
        private String file_type;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message.replaceAll("\n", "");
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

        public String getFile_type() {
            return file_type;
        }

        public void setFile_type(String file_type) {
            this.file_type = file_type;
        }
    }
}

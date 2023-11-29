package com.ada.ada_meethem.modelo.pinnable;

import java.util.Date;
import java.util.UUID;

public class ChatMessage implements Pinnable{
    private String id;
    private String messageText;
    private String messageUser;
    private long messageTime;
    public ChatMessage(String messageText, String messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        // Initialize to current time
        messageTime = new Date().getTime();
        id = "msg" + UUID.randomUUID().toString();
    }
    public ChatMessage(){
    }
    public String getMessageText() {
        return messageText;
    }
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
    public String getMessageUser() {
        return messageUser;
    }
    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }
    public long getMessageTime() {
        return messageTime;
    }
    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getId() {
        return this.id;
    }
}

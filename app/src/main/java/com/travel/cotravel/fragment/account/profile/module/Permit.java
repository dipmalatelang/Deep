package com.travel.cotravel.fragment.account.profile.module;

public class Permit{

    String sender, receiver;
    boolean senderCheck, receiverCheck;
    int status;

    public Permit() {
    }

    public Permit(String sender, String receiver, int status, boolean senderCheck, boolean receiverCheck) {
        this.sender = sender;
        this.receiver=receiver;
        this.status = status;
        this.senderCheck=senderCheck;
        this.receiverCheck=receiverCheck;
    }

    public boolean isSenderCheck() {
        return senderCheck;
    }

    public void setSenderCheck(boolean senderCheck) {
        this.senderCheck = senderCheck;
    }

    public boolean isReceiverCheck() {
        return receiverCheck;
    }

    public void setReceiverCheck(boolean receiverCheck) {
        this.receiverCheck = receiverCheck;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

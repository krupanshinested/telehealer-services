package com.thealer.telehealer.views.inviteUser;

import java.util.List;

public class ContactModel {
    private String id;
    private String name;
    private String photo;
    private List<String> emailList;
    private List<String> numberList;
    private String selectedContact = null;

    public ContactModel() {
    }

    public ContactModel(String id, String name, String photo, List<String> emailList, List<String> numberList) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.emailList = emailList;
        this.numberList = numberList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getEmailList() {
        return emailList;
    }

    public void setEmailList(List<String> emailList) {
        this.emailList = emailList;
    }

    public List<String> getNumberList() {
        return numberList;
    }

    public void setNumberList(List<String> numberList) {
        this.numberList = numberList;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSelectedContact() {
        return selectedContact;
    }

    public void setSelectedContact(String selectedContact) {
        this.selectedContact = selectedContact;
    }
}

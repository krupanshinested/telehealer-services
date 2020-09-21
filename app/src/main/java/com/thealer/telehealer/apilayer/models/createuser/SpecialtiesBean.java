package com.thealer.telehealer.apilayer.models.createuser;

import java.io.Serializable;

/**
 * Created by Aswin on 25,October,2018
 */
public class SpecialtiesBean implements Serializable {

    private String actors;
    private String description;
    private String category;
    private String uid;
    private String name;
    private String actor;

    public SpecialtiesBean() {
    }

    public SpecialtiesBean(String actors, String description, String category, String uid, String name, String actor) {
        this.actors = actors;
        this.description = description;
        this.category = category;
        this.uid = uid;
        this.name = name;
        this.actor = actor;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }
}

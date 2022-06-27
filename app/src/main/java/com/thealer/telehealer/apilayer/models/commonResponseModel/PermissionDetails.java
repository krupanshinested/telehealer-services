package com.thealer.telehealer.apilayer.models.commonResponseModel;

import java.io.Serializable;

/**
 * Created by Nimesh Patel
 * Created Date: 15,April,2021
 **/
public class PermissionDetails  implements Serializable {
    int id;
    String name;
    int parent_id;
    String code;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

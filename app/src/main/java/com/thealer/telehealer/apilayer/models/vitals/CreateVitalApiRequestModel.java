package com.thealer.telehealer.apilayer.models.vitals;

/**
 * Created by Aswin on 27,November,2018
 */
public class CreateVitalApiRequestModel implements Cloneable {

    private String type;
    private String value;
    private String mode;
    private String display_name;
    private String user_guid;

    public CreateVitalApiRequestModel(String type, String value, String mode, String display_name, String user_guid) {
        this.type = type;
        this.value = value;
        this.mode = mode;
        this.display_name = display_name;
        this.user_guid = user_guid;
    }

    public CreateVitalApiRequestModel() {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getUser_guid() {
        return user_guid;
    }

    public void setUser_guid(String user_guid) {
        this.user_guid = user_guid;
    }

    public Object clone() throws CloneNotSupportedException{
        return (CreateVitalApiRequestModel)super.clone();
    }
}

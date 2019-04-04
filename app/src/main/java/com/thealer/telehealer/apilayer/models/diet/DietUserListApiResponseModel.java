package com.thealer.telehealer.apilayer.models.diet;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.UserBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Aswin on 04,June,2019
 */
public class DietUserListApiResponseModel extends BaseApiResponseModel implements Serializable {

    private List<UserBean> result;

    public List<UserBean> getResult() {
        return result;
    }

    public void setResult(List<UserBean> result) {
        this.result = result;
    }
}

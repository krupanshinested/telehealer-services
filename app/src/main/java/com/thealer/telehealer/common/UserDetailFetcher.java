
package com.thealer.telehealer.common;

import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;

/**
 * Created by rsekar on 12/27/18.
 */

public interface UserDetailFetcher {
    void didFetchedDetails(CommonUserApiResponseModel commonUserApiResponseModel);
}

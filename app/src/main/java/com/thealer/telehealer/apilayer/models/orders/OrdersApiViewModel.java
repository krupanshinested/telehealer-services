package com.thealer.telehealer.apilayer.models.orders;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.documents.DocumentsApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.forms.OrdersFormsApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.forms.OrdersUserFormsApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;
import com.thealer.telehealer.views.home.orders.OrderConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Aswin on 22,November,2018
 */
public class OrdersApiViewModel extends BaseApiViewModel {

    private int page_size = Constants.PAGINATION_SIZE;
    private boolean paginate = true;


    public OrdersApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void getUserLabOrders(String user_guid, String doctorGuid, int page, boolean showProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    getAuthApiService().getUserLabOrders(paginate, page, page_size, user_guid, doctorGuid)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(showProgress)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void getLabOrders(int page, boolean showProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    getAuthApiService().getLabOrders(paginate, page, page_size)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(showProgress)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void getUserPrescriptionOrders(String user_guid, String doctorGuid, int page, boolean showProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    getAuthApiService().getUserPrescriptionsOrders(paginate, page, page_size, user_guid, doctorGuid)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(showProgress)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void getPrescriptionOrders(int page, boolean showProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    getAuthApiService().getPrescriptionsOrders(paginate, page, page_size)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(showProgress)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void getUserSpecialist(String user_guid, String doctorGuid, int page, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    getAuthApiService().getUserSpecialistList(paginate, page, page_size, user_guid, doctorGuid)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void getSpecialist(int page, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    getAuthApiService().getSpecialistList(paginate, page, page_size)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);

                                }
                            });
                }
            }
        });
    }

    public void cancelOrder(String orderType, int id, String doctorGuid) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    String type = null;
                    switch (orderType) {
                        case OrderConstant.ORDER_PRESCRIPTIONS:
                            type = "prescriptions";
                            break;
                        case OrderConstant.ORDER_REFERRALS:
                            type = "specialists";
                            break;
                        case OrderConstant.ORDER_LABS:
                            type = "labs";
                            break;
                        case OrderConstant.ORDER_RADIOLOGY:
                            type = "x-rays";
                            break;
                        case OrderConstant.ORDER_MISC:
                            type = "miscellaneous";
                            break;

                    }
                    getAuthApiService().cancelOrder(type, id, doctorGuid, true)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void getUserForms(String user_guid, String doctorGuid, boolean isShowProgress, boolean assignor) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getUserForms(user_guid, doctorGuid, assignor)
                            .compose(applySchedulers())
                            .subscribe(new RAListObserver<OrdersUserFormsApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(ArrayList<OrdersUserFormsApiResponseModel> data) {
                                    ArrayList<BaseApiResponseModel> apiResponseModels = new ArrayList<>(data);
                                    baseApiArrayListMutableLiveData.setValue(apiResponseModels);
                                }
                            });
                }
            }
        });
    }

    public void getForms(boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    getAuthApiService().getForms()
                            .compose(applySchedulers())
                            .subscribe(new RAListObserver<OrdersUserFormsApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(ArrayList<OrdersUserFormsApiResponseModel> data) {
                                    ArrayList<BaseApiResponseModel> apiResponseModels = new ArrayList<>(data);
                                    baseApiArrayListMutableLiveData.setValue(apiResponseModels);
                                }
                            });
                }
            }
        });
    }

    public void getAllForms(boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    getAuthApiService().getAllForms()
                            .compose(applySchedulers())
                            .subscribe(new RAListObserver<OrdersFormsApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(ArrayList<OrdersFormsApiResponseModel> data) {
                                    ArrayList<BaseApiResponseModel> apiResponseModels = new ArrayList<>(data);
                                    baseApiArrayListMutableLiveData.setValue(apiResponseModels);
                                }
                            });
                }
            }
        });
    }

    public void getDocuments(int page, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getDocuments(paginate, page, page_size)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void getUserDocuments(int page, String userGuid, String doctorGuid, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getUserDocuments(paginate, page, page_size, userGuid, doctorGuid)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void deleteDocument(int id, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().deleteDocument(id)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void updateDocument(int id, String name, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    HashMap<String, String> param = new HashMap<>();
                    param.put("name", name);

                    getAuthApiService().updateDocument(id, param)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void getPharmacy(@Nullable String query, @Nullable String location, @NonNull int nextPage, @NonNull boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getServiceApi().getPharmacies(query, location, nextPage)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void getRadiologyList(int page, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getRadiologyList(paginate, page, page_size)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void getUserRadiologyList(String userGuid, String doctorGuid, int page, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getUserRadiologyList(paginate, page, page_size, userGuid, doctorGuid)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void getMiscellaneousList(int page, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getMiscellaneousList(paginate, page, page_size)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void getUserMiscellaneousList(String userGuid, String doctorGuid, int page, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getUserMiscellaneousList(paginate, page, page_size, userGuid, doctorGuid)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void getOrderdsDetail(String userGuid, String doctorGuid, List<Integer> idList, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    String ids = null;
                    if (idList != null)
                        ids = idList.toString().replace("[", "").replace("]", "");
                    getAuthApiService().getOrderDetails(userGuid, doctorGuid, ids)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<OrdersIdListApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(OrdersIdListApiResponseModel ordersIdListApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(ordersIdListApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void getDocumentsDetail(String userGuid, String doctorGuid, List<Integer> idList, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    String ids = idList.toString().replace("[", "").replace("]", "");
                    getAuthApiService().getDocumentDetails(userGuid, doctorGuid, ids)
                            .compose(applySchedulers())
                            .subscribe(new RAListObserver<DocumentsApiResponseModel.ResultBean>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(ArrayList<DocumentsApiResponseModel.ResultBean> data) {
                                    ArrayList<BaseApiResponseModel> apiResponseModels = new ArrayList<>(data);

                                    baseApiArrayListMutableLiveData.setValue(apiResponseModels);

                                }
                            });
                }
            }
        });
    }

    public void getFormsDetail(String userGuid, String doctorGuid, List<Integer> idList, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    String ids = idList.toString().replace("[", "").replace("]", "");
                    getAuthApiService().getFormDetails(userGuid, doctorGuid, ids)
                            .compose(applySchedulers())
                            .subscribe(new RAListObserver<OrdersUserFormsApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(ArrayList<OrdersUserFormsApiResponseModel> data) {
                                    ArrayList<BaseApiResponseModel> apiResponseModels = new ArrayList<>(data);

                                    baseApiArrayListMutableLiveData.setValue(apiResponseModels);

                                }
                            });
                }
            }
        });
    }
}

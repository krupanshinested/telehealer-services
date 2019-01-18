package com.thealer.telehealer.apilayer.api;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.Braintree.BrainTreeClientToken;
import com.thealer.telehealer.apilayer.models.Braintree.BrainTreeCustomer;
import com.thealer.telehealer.apilayer.models.CheckUserEmailMobileModel.CheckUserEmailMobileResponseModel;
import com.thealer.telehealer.apilayer.models.OpenTok.TokenFetchModel;
import com.thealer.telehealer.apilayer.models.Payments.TransactionResponse;
import com.thealer.telehealer.apilayer.models.Payments.VitalVisitResponse;
import com.thealer.telehealer.apilayer.models.addConnection.AddConnectionRequestModel;
import com.thealer.telehealer.apilayer.models.addConnection.ConnectionListResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.DataBean;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiResponseModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.TypeAHeadResponseModel;
import com.thealer.telehealer.apilayer.models.inviteUser.InviteByDemographicRequestModel;
import com.thealer.telehealer.apilayer.models.inviteUser.InviteByEmailPhoneApiResponseModel;
import com.thealer.telehealer.apilayer.models.inviteUser.InviteByEmailPhoneRequestModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersPrescriptionApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersSpecialistApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.documents.DocumentsApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.forms.CreateFormRequestModel;
import com.thealer.telehealer.apilayer.models.orders.forms.OrdersFormsApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.forms.OrdersUserFormsApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.CreateTestApiRequestModel;
import com.thealer.telehealer.apilayer.models.orders.lab.IcdCodeApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.OrdersLabApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.pharmacy.GetPharmaciesApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.pharmacy.SendFaxRequestModel;
import com.thealer.telehealer.apilayer.models.orders.prescription.CreatePrescriptionRequestModel;
import com.thealer.telehealer.apilayer.models.orders.prescription.CreatePrescriptionResponseModel;
import com.thealer.telehealer.apilayer.models.orders.radiology.CreateRadiologyRequestModel;
import com.thealer.telehealer.apilayer.models.orders.radiology.GetRadiologyResponseModel;
import com.thealer.telehealer.apilayer.models.orders.specialist.AssignSpecialistRequestModel;
import com.thealer.telehealer.apilayer.models.recents.DownloadTranscriptResponseModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.TranscriptionApiResponseModel;
import com.thealer.telehealer.apilayer.models.requestotp.OtpVerificationResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesCreateRequestModel;
import com.thealer.telehealer.apilayer.models.signature.SignatureApiResponseModel;
import com.thealer.telehealer.apilayer.models.signin.ResetPasswordRequestModel;
import com.thealer.telehealer.apilayer.models.signin.SigninApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.CreateVitalApiRequestModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Aswin on 08,October,2018
 */
public interface ApiInterface {

    String APP_TYPE = "app_type";
    String USER_DATA = "user_data";
    String USER_DETAIL = "user_detail";
    String PAGINATE = "paginate";
    String PAGE = "page";
    String PAGE_SIZE = "page_size";
    String START_KEY = "start_key";
    String TYPE = "type";
    String ID = "id";
    String CANCEL = "cancel";
    String SEARCH = "search";
    String NAME = "name";
    String FIELDS = "fields";
    String EMAIL = "email";
    String PHONE = "phone";
    String CALLS = "calls";
    String FILTER_CODE_IN = "filter_code_in";
    String FILTER_USER_GUID_IN = "filter_user_guid_in";
    String USER_GUID = "user_guid";
    String DOCTOR_GUID = "doctor_guid";
    String REFRESH_TOKEN = "refresh_token";
    String CHANNEL = "channel";
    String SESSION_ID = "sessionId";
    String CALL_QUALITY = "call_quality";
    String MONTH="month";

    @GET("users/check")
    Observable<CheckUserEmailMobileResponseModel> checkUserEmail(@Query(EMAIL) String email, @Query(APP_TYPE) String app_type);

    @GET("users/check")
    Observable<CheckUserEmailMobileResponseModel> checkUserMobile(@Query(PHONE) String phone, @Query(APP_TYPE) String app_type);

    @Multipart
    @POST("setup")
    Observable<CreateUserApiResponseModel> createPatient(@Part(USER_DATA) CreateUserRequestModel.UserDataBean user_data,
                                                         @Part MultipartBody.Part user_avatar,
                                                         @Part MultipartBody.Part insurance_front,
                                                         @Part MultipartBody.Part insurance_back);

    @Multipart
    @POST("setup")
    Observable<CreateUserApiResponseModel> createMedicalAssistant(@Part(USER_DATA) CreateUserRequestModel.UserDataBean user_data,
                                                                  @Part(USER_DETAIL) DataBean user_detail,
                                                                  @Part MultipartBody.Part user_avatar,
                                                                  @Part MultipartBody.Part certification);

    @Multipart
    @POST("setup")
    Observable<CreateUserApiResponseModel> createDoctor(@Part(USER_DATA) CreateUserRequestModel.UserDataBean user_data,
                                                        @Part(USER_DETAIL) DataBean user_detail,
                                                        @Part MultipartBody.Part user_avatar,
                                                        @Part MultipartBody.Part certification,
                                                        @Part MultipartBody.Part license);

    @Multipart
    @PUT("api/users/profile")
    Observable<CommonUserApiResponseModel> updateMedicalAssistant(@Part(USER_DATA) CreateUserRequestModel.UserDataBean user_data,
                                                                  @Part(USER_DETAIL) DataBean user_detail,
                                                                  @Part MultipartBody.Part user_avatar,
                                                                  @Part MultipartBody.Part certification);

    @Multipart
    @PUT("api/users/profile")
    Observable<CommonUserApiResponseModel> updateDoctor(@Part(USER_DATA) CreateUserRequestModel.UserDataBean user_data,
                                                        @Part(USER_DETAIL) DataBean user_detail,
                                                        @Part MultipartBody.Part user_avatar,
                                                        @Part MultipartBody.Part certification,
                                                        @Part MultipartBody.Part license);

    @Multipart
    @PUT("api/users/profile")
    Observable<CommonUserApiResponseModel> updatePatient(@Part(USER_DATA) CreateUserRequestModel.UserDataBean user_data,
                                                         @Part MultipartBody.Part user_avatar,
                                                         @Part MultipartBody.Part insurance_front,
                                                         @Part MultipartBody.Part insurance_back);

    @DELETE("api/users/insurance")
    Observable<CommonUserApiResponseModel> deleteInsurance();

    @POST("reset-password")
    Observable<BaseApiResponseModel> resetPassword(@Body ResetPasswordRequestModel resetPasswordRequestModel);

    @POST("otp")
    Observable<BaseApiResponseModel> requestOtp(@Body HashMap<String, Object> body);

    @POST("otp/validate")
    Observable<OtpVerificationResponseModel> validateOtp(@Body HashMap<String, Object> body);

    @GET("doctors")
    Observable<GetDoctorsApiResponseModel> getDoctors(@Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(NAME) String name, @Query(FIELDS) String fields);

    @GET("doctors")
    Observable<TypeAHeadResponseModel> getTypeAHeadResult(@Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(PAGINATE) boolean paginate, @Query(NAME) String name, @Query("typeahead") boolean isTypeAHead);

    @POST("login")
    Observable<SigninApiResponseModel> signinUser(@Body HashMap<String, Object> params);

    @GET("api/whoami")
    Observable<WhoAmIApiResponseModel> whoAmI();

    @GET("api/associations")
    Observable<AssociationApiResponseModel> getAssociations(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @GET("api/associations")
    Observable<ArrayList<CommonUserApiResponseModel>> getAssociations(@Query(PAGINATE) boolean paginate, @Query(DOCTOR_GUID) String doctorGuid);

    @GET("api/correspondence-history")
    Observable<RecentsApiResponseModel> getUserCorrespondentHistory(@Query(USER_GUID) String user_guid, @Query(CALLS) boolean calls, @Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @GET("api/correspondence-history")
    Observable<RecentsApiResponseModel> getMyCorrespondentHistory(@Query(CALLS) boolean calls, @Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @GET("api/unconnected-users")
    Observable<ConnectionListResponseModel> getUnConnectedUsers(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @POST("api/requests")
    Observable<BaseApiResponseModel> addConnection(@Body AddConnectionRequestModel addConnectionRequestModel);

    @DELETE("api/associations")
    Observable<BaseApiResponseModel> disconnectUser(@Query(USER_GUID) String user_guid);

    @GET("api/vitals")
    Observable<ArrayList<VitalsApiResponseModel>> getUserVitals(@Query(TYPE) String type, @Query(USER_GUID) String user_guid);

    @GET("api/vitals")
    Observable<ArrayList<VitalsApiResponseModel>> getVitals(@Query(TYPE) String type);

    @GET("api/referrals/labs")
    Observable<OrdersLabApiResponseModel> getUserLabOrders(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(USER_GUID) String user_guid);

    @GET("api/referrals/labs")
    Observable<OrdersLabApiResponseModel> getLabOrders(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @PATCH("api/referrals/labs/{id}")
    Observable<BaseApiResponseModel> cancelLabOrder(@Path(ID) int id, @Query(CANCEL) boolean cancel);

    @GET("api/icd-codes")
    Observable<IcdCodeApiResponseModel> getFilteredIcdCodes(@Query(FILTER_CODE_IN) String data);

    @GET("api/icd-codes")
    Observable<IcdCodeApiResponseModel> getAllIcdCodes(@Query(START_KEY) int key, @Query(SEARCH) String search);


    @GET("api/referrals/prescriptions")
    Observable<OrdersPrescriptionApiResponseModel> getUserPrescriptionsOrders(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(USER_GUID) String user_guid);

    @GET("api/referrals/prescriptions")
    Observable<OrdersPrescriptionApiResponseModel> getPrescriptionsOrders(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @PATCH("api/referrals/prescriptions/{id}")
    Observable<BaseApiResponseModel> cancelPrescriptionOrder(@Path(ID) int id, @Query(CANCEL) boolean cancel);

    @POST("api/referrals/prescriptions")
    Observable<CreatePrescriptionResponseModel> createPrescription(@Body CreatePrescriptionRequestModel createPrescriptionRequestModel);

    @GET("users")
    Observable<ArrayList<CommonUserApiResponseModel>> getUsersByGuid(@Query(FILTER_USER_GUID_IN) String data);

    @GET("api/referrals/specialists")
    Observable<OrdersSpecialistApiResponseModel> getUserSpecialistList(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(USER_GUID) String user_guid);

    @GET("api/referrals/specialists")
    Observable<OrdersSpecialistApiResponseModel> getSpecialistList(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @PATCH("api/referrals/specialists/{id}")
    Observable<BaseApiResponseModel> cancelSpecialistOrder(@Path(ID) int id, @Query(CANCEL) boolean cancel);

    @DELETE("api/users")
    Observable<BaseApiResponseModel> deleteAccount();

    @PUT("api/users/profile")
    Observable<BaseApiResponseModel> updateAppointmentLength(@Body RequestBody body);

    @PUT("api/users/profile")
    Observable<BaseApiResponseModel> updateUserDetail(@Body WhoAmIApiResponseModel whoAmIApiResponseModel);

    @POST("api/vitals")
    Observable<BaseApiResponseModel> createVital(@Body CreateVitalApiRequestModel vitalApiRequestModel);

    @GET("api/forms")
    Observable<ArrayList<OrdersUserFormsApiResponseModel>> getUserForms(@Query(USER_GUID) String user_guid);

    @GET("api/forms")
    Observable<ArrayList<OrdersFormsApiResponseModel>> getAllForms();

    @GET("api/forms")
    Observable<ArrayList<OrdersUserFormsApiResponseModel>> getForms();

    @POST("api/forms")
    Observable<BaseApiResponseModel> createForms(@Body CreateFormRequestModel createFormRequestModel);

    @POST("api/referrals/specialists")
    Observable<BaseApiResponseModel> assignSpecialist(@Body AssignSpecialistRequestModel assignSpecialistRequestModel);

    @Multipart
    @POST("api/users/files")
    Observable<BaseApiResponseModel> uploadDocument(@Part(NAME) RequestBody name, @Part MultipartBody.Part file);

    @GET("api/users/files")
    Observable<DocumentsApiResponseModel> getDocuments(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @GET("api/users/files")
    Observable<DocumentsApiResponseModel> getUserDocuments(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(USER_GUID) String user_guid);

    @DELETE("api/users/files")
    Observable<BaseApiResponseModel> deleteDocument(@Query("user_file_id") int id);

    @PUT("api/users/files/{id}")
    Observable<BaseApiResponseModel> updateDocument(@Path(ID) int id, @Body() Map<String, String> param);

    @GET("api/pharmacies")
    Observable<GetPharmaciesApiResponseModel> getPharmacies(@Query(SEARCH) String query, @Query("city") String city, @Query(START_KEY) int key);

    @POST("api/faxes")
    Observable<BaseApiResponseModel> sendFax(@Body SendFaxRequestModel sendFaxRequestModel);

    @POST("api/referrals/labs")
    Observable<BaseApiResponseModel> createLabOrder(@Body CreateTestApiRequestModel createTestApiRequestModel);

    @GET("api/download")
    Observable<Response<ResponseBody>> getPdfFile(@Query("path") String path, @Query("decrypt") boolean isDecrypt);

    @POST("refresh")
    Observable<SigninApiResponseModel> refreshToken(@Header(REFRESH_TOKEN) String refreshToken);

    @POST("api/referrals/x-rays")
    Observable<BaseApiResponseModel> createRadiology(@Body CreateRadiologyRequestModel createRadiologyRequestModel);

    @GET("api/referrals/x-rays")
    Observable<GetRadiologyResponseModel> getRadiologyList(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @GET("api/referrals/x-rays")
    Observable<GetRadiologyResponseModel> getUserRadiologyList(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(USER_GUID) String user_guid);

    @GET("api/schedule")
    Observable<SchedulesApiResponseModel> getSchedules(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(DOCTOR_GUID) String doctorGuidList);

    @POST("api/requests")
    Observable<BaseApiResponseModel> createSchedules(@Query(DOCTOR_GUID) String doctorGuidList, @Body SchedulesCreateRequestModel createRequestModel);

    @GET("api/schedule")
    Observable<ArrayList<SchedulesApiResponseModel.ResultBean>> getUserUpcomingSchedules(@Query(USER_GUID) String user_guid, @Query("upcoming") boolean upcoming, @Query(DOCTOR_GUID) String doctorGuid);

    @DELETE("api/schedule")
    Observable<BaseApiResponseModel> deleteSchedule(@Query("schedule_id") int schedule_id);


    @POST("api/setup/invite")
    Observable<BaseApiResponseModel> inviteUserByDemographic(@Body InviteByDemographicRequestModel demographicRequestModel, @Query(DOCTOR_GUID) String doctor_guid);

    @POST("api/setup/invite")
    Observable<InviteByEmailPhoneApiResponseModel> inviteUserByEmailPhone(@Query(DOCTOR_GUID) String doctor_user_guid, @Body InviteByEmailPhoneRequestModel emailPhoneRequestModel);

    @GET("api/transcriptions/{id}")
    Observable<TranscriptionApiResponseModel> getTranscriptionDetails(@Path(ID) int id);

    @POST("api/pubnub/grant_access")
    Observable<BaseApiResponseModel> grantPubnubAccess(@Query(CHANNEL) String channel);

    @GET("api/download")
    Observable<DownloadTranscriptResponseModel> downloadTranscript(@Query("path") String path, @Query("decrypt") boolean isDecrypt);

    @GET("api/sources/{id}")
    Observable<BaseApiResponseModel> getExperimentalFeature(@Path(ID) String id);

    @GET("api/token")
    Observable<TokenFetchModel> getOpenTokToken(@Query(SESSION_ID) String sessionId);

    @GET("api/users/{id}")
    Observable<CommonUserApiResponseModel> getUserDetail(@Path(ID) String id);

    @PUT("api/call/{id}")
    Observable<BaseApiResponseModel> updateCallStatus(@Path(ID) String sessionId, @QueryMap Map<String, String> param);

    @GET("api/archive/start")
    Observable<CommonUserApiResponseModel> startArchive(@Query(SESSION_ID) String sessionId);

    @GET("api/session")
    Observable<TokenFetchModel> getSessionId(@Query(CALL_QUALITY) String call_quality, @Query(DOCTOR_GUID) String doctor_guid);

    @POST("api/call")
    Observable<BaseApiResponseModel> postaVOIPCall(@Query(DOCTOR_GUID) String doctor_guid, @Body Map<String, String> param);

    @POST("api/setup/verification-link")
    Observable<BaseApiResponseModel> requestVerificationMain();
    @Multipart
    @POST("api/users/signature")
    Observable<SignatureApiResponseModel> uploadSignature(@Part MultipartBody.Part file);

    @DELETE("api/users/signature")
    Observable<BaseApiResponseModel> deleteSignature();

    @POST("api/review")
    Observable<BaseApiResponseModel> postReview(@Body Map<String, Object> param);


    @POST("api/log/user-details")
    Observable<BaseApiResponseModel> postCapabilityLog(@Body Map<String, Object> param);

    @POST("api/log/external-api")
    Observable<BaseApiResponseModel> postExternalApiLog(@Body Map<String, Object> param);

    @GET("api/braintree/customer")
    Observable<BrainTreeCustomer> getBrainTreeCustomer();

    @GET("api/braintree/client_token")
    Observable<BrainTreeClientToken> getBrainTreeClientToken(@QueryMap Map<String, String> param);

    @POST("api/braintree/checkout")
    Observable<BaseApiResponseModel> checkOutBrainTree(@Body Map<String, Object> param);

    @GET("/api/braintree/transactions")
    Observable<TransactionResponse> getTransactions();

    @GET("/api/log/vitals")
    Observable<VitalVisitResponse> getVitalVisit(@Query(MONTH) String month);

    @GET("api/correspondence-history")
    Observable<ArrayList<RecentsApiResponseModel.ResultBean>> getCallLogs(@Query(CALLS) boolean calls);

}

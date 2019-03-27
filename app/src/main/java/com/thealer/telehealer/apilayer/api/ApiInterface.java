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
import com.thealer.telehealer.apilayer.models.diet.DietApiResponseModel;
import com.thealer.telehealer.apilayer.models.diet.food.FoodDetailApiResponseModel;
import com.thealer.telehealer.apilayer.models.diet.food.FoodListApiResponseModel;
import com.thealer.telehealer.apilayer.models.diet.food.NutrientsDetailRequestModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiResponseModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.TypeAHeadResponseModel;
import com.thealer.telehealer.apilayer.models.inviteUser.InviteByDemographicRequestModel;
import com.thealer.telehealer.apilayer.models.inviteUser.InviteByEmailPhoneApiResponseModel;
import com.thealer.telehealer.apilayer.models.inviteUser.InviteByEmailPhoneRequestModel;
import com.thealer.telehealer.apilayer.models.medicalHistory.UpdateQuestionaryBodyModel;
import com.thealer.telehealer.apilayer.models.notification.NotificationApiResponseModel;
import com.thealer.telehealer.apilayer.models.notification.NotificationRequestUpdateResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersBaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersPrescriptionApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersSpecialistApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.documents.DocumentsApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.forms.CreateFormRequestModel;
import com.thealer.telehealer.apilayer.models.orders.forms.OrdersFormsApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.forms.OrdersUserFormsApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.CreateTestApiRequestModel;
import com.thealer.telehealer.apilayer.models.orders.lab.IcdCodeApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.OrdersLabApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.miscellaneous.CreateMiscellaneousRequestModel;
import com.thealer.telehealer.apilayer.models.orders.miscellaneous.MiscellaneousApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.pharmacy.GetPharmaciesApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.pharmacy.SendFaxRequestModel;
import com.thealer.telehealer.apilayer.models.orders.prescription.CreatePrescriptionRequestModel;
import com.thealer.telehealer.apilayer.models.orders.radiology.CreateRadiologyRequestModel;
import com.thealer.telehealer.apilayer.models.orders.radiology.GetRadiologyResponseModel;
import com.thealer.telehealer.apilayer.models.orders.specialist.AssignSpecialistRequestModel;
import com.thealer.telehealer.apilayer.models.pendingInvites.PendingInvitesNonRegisterdApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.DownloadTranscriptResponseModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.TranscriptionApiResponseModel;
import com.thealer.telehealer.apilayer.models.requestotp.OtpVerificationResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesCreateRequestModel;
import com.thealer.telehealer.apilayer.models.signature.SignatureApiResponseModel;
import com.thealer.telehealer.apilayer.models.signin.ResetPasswordRequestModel;
import com.thealer.telehealer.apilayer.models.signin.SigninApiResponseModel;
import com.thealer.telehealer.apilayer.models.userStatus.ConnectionStatusApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitalReport.VitalReportApiReponseModel;
import com.thealer.telehealer.apilayer.models.vitals.CreateVitalApiRequestModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import retrofit2.http.PartMap;
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
    String MONTH = "month";
    String FILTER = "filter";
    String DATE = "date";
    String MEDICAL_ASSISTANT = "medical_assistant";
    String STATUS = "status";
    String ACCEPTED = "accepted";

    @GET("users/check")
    Observable<CheckUserEmailMobileResponseModel> checkUserEmail(@Query(EMAIL) String email, @Query(APP_TYPE) String app_type);

    @GET("users/check")
    Observable<CheckUserEmailMobileResponseModel> checkUserMobile(@Query(PHONE) String phone, @Query(APP_TYPE) String app_type);

    @Multipart
    @POST("setup")
    Observable<CreateUserApiResponseModel> createPatient(@Part(USER_DATA) CreateUserRequestModel.UserDataBean user_data,
                                                         @Part MultipartBody.Part user_avatar,
                                                         @Part MultipartBody.Part insurance_front,
                                                         @Part MultipartBody.Part insurance_back,
                                                         @Part MultipartBody.Part secondary_insurance_front,
                                                         @Part MultipartBody.Part secondary_insurance_back);

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
                                                        @Part MultipartBody.Part license,
                                                        @Part MultipartBody.Part signature);

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
                                                         @Part MultipartBody.Part insurance_back,
                                                         @Part MultipartBody.Part secondary_insurance_front,
                                                         @Part MultipartBody.Part secondary_insurance_back);

    @DELETE("api/users/insurance")
    Observable<CommonUserApiResponseModel> deleteInsurance(@QueryMap Map<String, Boolean> params);

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
    Observable<AssociationApiResponseModel> getAssociations(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(SEARCH) String name, @Query(MEDICAL_ASSISTANT) boolean isMedicalAssistant);

    @GET("api/associations")
    Observable<ArrayList<CommonUserApiResponseModel>> getAssociations(@Query(PAGINATE) boolean paginate, @Query(DOCTOR_GUID) String doctorGuid);

    @GET("api/correspondence-history")
    Observable<RecentsApiResponseModel> getUserCorrespondentHistory(@Query(USER_GUID) String user_guid, @Query(CALLS) boolean calls, @Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @GET("api/correspondence-history")
    Observable<RecentsApiResponseModel> getMyCorrespondentHistory(@Query(CALLS) boolean calls, @Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @GET("api/unconnected-users")
    Observable<ConnectionListResponseModel> getUnConnectedUsers(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(SEARCH) String name, @Query(MEDICAL_ASSISTANT) boolean isMedicalAssistant);

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
    Observable<OrdersBaseApiResponseModel> createPrescription(@Body CreatePrescriptionRequestModel createPrescriptionRequestModel);

    @GET("api/users")
    Observable<ArrayList<CommonUserApiResponseModel>> getUsersByGuid(@Query(FILTER_USER_GUID_IN) String data);

    @GET("api/referrals/specialists")
    Observable<OrdersSpecialistApiResponseModel> getUserSpecialistList(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(USER_GUID) String user_guid);

    @GET("api/referrals/specialists")
    Observable<OrdersSpecialistApiResponseModel> getSpecialistList(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @PATCH("api/referrals/specialists/{id}")
    Observable<BaseApiResponseModel> cancelSpecialistOrder(@Path(ID) int id, @Query(CANCEL) boolean cancel);

    @DELETE("api/users")
    Observable<BaseApiResponseModel> deleteAccount();

    @Multipart
    @PUT("api/users/profile")
    Observable<BaseApiResponseModel> updateUserDetail(@Part("user_data") RequestBody body);

    @Multipart
    @PUT("api/users/profile")
    Observable<BaseApiResponseModel> updateUserHistory(@Part("history") RequestBody history);

    @Multipart
    @PUT("api/users/profile")
    Observable<BaseApiResponseModel> updateUserQuestionnaire(@Part("questionnaire") RequestBody body);

    @PUT("api/users/profile")
    Observable<BaseApiResponseModel> updateUserDetail(@Body WhoAmIApiResponseModel whoAmIApiResponseModel);

    @PATCH("api/users/questionnaire/{id}")
    Observable<BaseApiResponseModel> updateUserQuestionnaire(@Path(ID) String userGuid, @Body UpdateQuestionaryBodyModel updateQuestionaryBodyModel);

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
    Observable<OrdersBaseApiResponseModel> assignSpecialist(@Body AssignSpecialistRequestModel assignSpecialistRequestModel);

    @Multipart
    @POST("api/users/files")
    Observable<BaseApiResponseModel> uploadDocument(@Part(NAME) RequestBody name, @Part MultipartBody.Part file, @Query(USER_GUID) String userGuid);

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
    Observable<OrdersBaseApiResponseModel> createLabOrder(@Body CreateTestApiRequestModel createTestApiRequestModel);

    @GET("api/download")
    Observable<Response<ResponseBody>> getPdfFile(@Query("path") String path, @Query("decrypt") boolean isDecrypt);

    @POST("refresh")
    Observable<SigninApiResponseModel> refreshToken(@Header(REFRESH_TOKEN) String refreshToken);

    @POST("api/referrals/x-rays")
    Observable<OrdersBaseApiResponseModel> createRadiology(@Body CreateRadiologyRequestModel createRadiologyRequestModel);

    @GET("api/referrals/x-rays")
    Observable<GetRadiologyResponseModel> getRadiologyList(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @GET("api/referrals/x-rays")
    Observable<GetRadiologyResponseModel> getUserRadiologyList(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(USER_GUID) String user_guid);

    @GET("api/referrals/miscellaneous")
    Observable<MiscellaneousApiResponseModel> getMiscellaneousList(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @GET("api/referrals/miscellaneous")
    Observable<MiscellaneousApiResponseModel> getUserMiscellaneousList(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(USER_GUID) String user_guid);

    @POST("api/referrals/miscellaneous")
    Observable<BaseApiResponseModel> createMiscellaneous(@Body CreateMiscellaneousRequestModel createMiscellaneousRequestModel);

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

    @GET("api/requests")
    Observable<NotificationApiResponseModel> getNotifications(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @PUT("api/requests")
    Observable<BaseApiResponseModel> setNotificationsRead(@Body Map<String, String> body);

    @PATCH("api/requests/{id}")
    Observable<NotificationRequestUpdateResponseModel> updateNotification(@Path(ID) int id, @Query(DOCTOR_GUID) String doctorGuid, @Body Map<String, Object> body);

    @GET("api/token")
    Observable<TokenFetchModel> getOpenTokToken(@Query(SESSION_ID) String sessionId);

    @GET("api/users/{id}")
    Observable<CommonUserApiResponseModel> getUserDetail(@Path(ID) String id);

    @PUT("api/call/{id}")
    Observable<BaseApiResponseModel> updateCallStatus(@Path(ID) String sessionId, @QueryMap Map<String, String> param);

    @GET("api/archive/start")
    Observable<CommonUserApiResponseModel> startArchive(@Query(SESSION_ID) String sessionId);

    @GET("api/session")
    Observable<TokenFetchModel> getSessionId(@Query(CALL_QUALITY) String call_quality);

    @POST("api/call")
    Observable<TokenFetchModel> postaVOIPCall(@Query(DOCTOR_GUID) String doctor_guid, @Body Map<String, String> param);

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

    @GET("api/vitals/users")
    Observable<VitalReportApiReponseModel> getVitalUsers(@Query(FILTER) String filter);

    @GET("api/vitals")
    Observable<ArrayList<VitalsApiResponseModel>> getUserFilteredVitals(@Query(FILTER) String type, @Query(USER_GUID) String user_guid);

    @PUT("api/available")
    Observable<BaseApiResponseModel> updateUserStatus(@Query(STATUS) boolean status);

    @Multipart
    @POST("api/call/{id}")
    Observable<BaseApiResponseModel> postTranscript(@Path(ID) String sessionId, @Part MultipartBody.Part file);

    @GET("api/connection-status/{id}")
    Observable<ConnectionStatusApiResponseModel> getUserConnectionStatus(@Path(ID) String userGuid);

    @GET("api/user-diet")
    Observable<ArrayList<DietApiResponseModel>> getDietDetails(@Query(DATE) String date, @Query(USER_GUID) String userGuid);

    @Multipart
    @POST("api/user-diet")
    Observable<BaseApiResponseModel> addDiet(@PartMap Map<String, RequestBody> requestBodyMap, @Part MultipartBody.Part file);

    @GET("/api/food-database/parser")
    Observable<FoodListApiResponseModel> getFoodList(@Query("ingr") String query, @Query("session") int session, @Query("app_id") String appId, @Query("app_key") String appKey);

    @POST("/api/food-database/nutrients")
    Observable<FoodDetailApiResponseModel> getFoodDetail(@Query("app_id") String appId, @Query("app_key") String appKey, @Body Map<String, List<NutrientsDetailRequestModel>> param);

    @GET("api/requests")
    Observable<NotificationApiResponseModel> getPendingInvites(@QueryMap Map<String, Object> map, @Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @GET("api/invites")
    Observable<PendingInvitesNonRegisterdApiResponseModel> getNonRegisteredUserInvites(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(ACCEPTED) boolean accepted);
}

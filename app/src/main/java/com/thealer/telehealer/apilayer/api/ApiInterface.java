package com.thealer.telehealer.apilayer.api;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.Braintree.BrainTreeClientToken;
import com.thealer.telehealer.apilayer.models.Braintree.BrainTreeCustomer;
import com.thealer.telehealer.apilayer.models.Braintree.DefaultCardResp;
import com.thealer.telehealer.apilayer.models.CheckUserEmailMobileModel.CheckUserEmailMobileResponseModel;
import com.thealer.telehealer.apilayer.models.DoctorGroupedAssociations;
import com.thealer.telehealer.apilayer.models.EducationalVideo.DeleteEducationalVideoResponse;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalFetchModel;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalVideoApiResponseModel;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalVideoOrder;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalVideoRequest;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalVideoResponse;
import com.thealer.telehealer.apilayer.models.PDFUrlResponse;
import com.thealer.telehealer.apilayer.models.Payments.TransactionResponse;
import com.thealer.telehealer.apilayer.models.Payments.VitalVisitResponse;
import com.thealer.telehealer.apilayer.models.UpdateProfile.UpdateProfileApiResponseModel;
import com.thealer.telehealer.apilayer.models.accessLog.AccessLogApiResponseModel;
import com.thealer.telehealer.apilayer.models.addConnection.AddConnectionRequestModel;
import com.thealer.telehealer.apilayer.models.addConnection.ConnectionListResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.UpdateAssociationRequestModel;
import com.thealer.telehealer.apilayer.models.chat.ChatApiResponseModel;
import com.thealer.telehealer.apilayer.models.chat.ChatMessageRequestModel;
import com.thealer.telehealer.apilayer.models.chat.PrecannedMessageApiResponse;
import com.thealer.telehealer.apilayer.models.chat.UserKeysApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.DataBean;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.apilayer.models.diet.DietApiResponseModel;
import com.thealer.telehealer.apilayer.models.diet.DietUserListApiResponseModel;
import com.thealer.telehealer.apilayer.models.diet.food.FoodDetailApiResponseModel;
import com.thealer.telehealer.apilayer.models.diet.food.FoodListApiResponseModel;
import com.thealer.telehealer.apilayer.models.diet.food.NutrientsDetailRequestModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiResponseModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.TypeAHeadResponseModel;
import com.thealer.telehealer.apilayer.models.guestviewmodel.GuestLoginApiResponseModel;
import com.thealer.telehealer.apilayer.models.inviteUser.InviteByDemographicRequestModel;
import com.thealer.telehealer.apilayer.models.inviteUser.InviteByEmailPhoneApiResponseModel;
import com.thealer.telehealer.apilayer.models.inviteUser.InviteByEmailPhoneRequestModel;
import com.thealer.telehealer.apilayer.models.medicalHistory.UpdateQuestionaryBodyModel;
import com.thealer.telehealer.apilayer.models.notification.NotificationApiResponseModel;
import com.thealer.telehealer.apilayer.models.notification.NotificationRequestUpdateResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersBaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersIdListApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersPrescriptionApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersSpecialistApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.documents.DocumentsApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.forms.CreateFormRequestModel;
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
import com.thealer.telehealer.apilayer.models.recents.VisitSummaryApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.VisitsDetailApiResponseModel;
import com.thealer.telehealer.apilayer.models.requestotp.OtpVerificationResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesCreateRequestModel;
import com.thealer.telehealer.apilayer.models.signature.SignatureApiResponseModel;
import com.thealer.telehealer.apilayer.models.signin.ResetPasswordRequestModel;
import com.thealer.telehealer.apilayer.models.signin.SigninApiResponseModel;
import com.thealer.telehealer.apilayer.models.userStatus.ConnectionStatusApiResponseModel;
import com.thealer.telehealer.apilayer.models.visits.UpdateVisitRequestModel;
import com.thealer.telehealer.apilayer.models.vitalReport.VitalReportApiReponseModel;
import com.thealer.telehealer.apilayer.models.vitals.CreateVitalApiRequestModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsCreateApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsPaginatedApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.common.OpenTok.CallSettings;
import com.thealer.telehealer.common.Signal.SignalModels.SignalKeyPostModel;
import com.thealer.telehealer.apilayer.models.guestviewmodel.GuestloginViewModel;
import com.thealer.telehealer.stripe.SetUpIntentResp;
import com.thealer.telehealer.views.home.orders.OrderConstant;
import com.thealer.telehealer.views.home.vitals.vitalReport.VitalBulkPdfApiResponseModel;

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
import retrofit2.http.Url;

/**
 * Created by Aswin on 08,October,2018
 */
public interface ApiInterface {

    String APP_TYPE = "app_type";
    String USER_DATA = "user_data";
    String USER_DETAIL = "user_detail";
    String PAGINATE = "paginate";
    String SYNC_CREATE = "sync_create";
    String PAGE = "page";
    String PAGE_SIZE = "page_size";
    String START_KEY = "start_key";
    String TYPE = "type";
    String ID = "id";
    String CANCEL = "cancel";
    String SEARCH = "search";
    String SEARCH_FILTER = "filter_fullname__icontains";
    String SEARCH_TITLE_FILTER = "filter_title__icontains";
    String SEARCH_FILTER_PRESCIPTION = "filter_drug_name__icontains";
    String SEARCH_FILTER_REFERRAL = "filter_specialist_name__icontains";
    String SEARCH_FILTER_LAB = "filter_lab_description__icontains";
    String SEARCH_FILTER_XRAY = "filter_xray_name__icontains";
    String SEARCH_FILTER_NAME = "filter_name__icontains";
    String SEARCH_FILTER_NOTES = "filter_notes__icontains";
    String SEARCH_FILTER_TITLE = "filter_title__icontains";
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
    String ORDER_ID = "order_id";
    String FILTER_ID_IN = "filter_id_in";
    String START_DATE = "start_date";
    String END_DATE = "end_date";
    String ASSIGNOR = "assignor";
    String DOWNLOAD_SUMMARY = "download_summary";
    String PROFILE_COMPLETE = "profile_complete";
    String REJECT = "reject";
    String SESSIONID = "session_id";

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
    Observable<UpdateProfileApiResponseModel> updateMedicalAssistant(@Part(USER_DATA) CreateUserRequestModel.UserDataBean user_data,
                                                                     @Part(USER_DETAIL) DataBean user_detail,
                                                                     @Part(PROFILE_COMPLETE) Boolean profile_complete,
                                                                     @Part MultipartBody.Part user_avatar,
                                                                     @Part MultipartBody.Part certification);

    @Multipart
    @PUT("api/users/profile")
    Observable<UpdateProfileApiResponseModel> updateDoctor(@Part(USER_DATA) CreateUserRequestModel.UserDataBean user_data,
                                                           @Part(USER_DETAIL) DataBean user_detail,
                                                           @Part(PROFILE_COMPLETE) Boolean profile_complete,
                                                           @Part MultipartBody.Part user_avatar,
                                                           @Part MultipartBody.Part certification,
                                                           @Part MultipartBody.Part license);

    @Multipart
    @PUT("api/users/profile")
    Observable<UpdateProfileApiResponseModel> updatePatient(@Part(USER_DATA) CreateUserRequestModel.UserDataBean user_data,
                                                            @Part(PROFILE_COMPLETE) Boolean profile_complete,
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

    @POST("api/logout")
    Observable<BaseApiResponseModel> signOut();

    @GET("api/whoami")
    Observable<WhoAmIApiResponseModel> whoAmI();

    @GET("api/associations")
    Observable<AssociationApiResponseModel> getAssociations(@Query(SEARCH_FILTER) String search, @Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(MEDICAL_ASSISTANT) boolean isMedicalAssistant, @Query(DOCTOR_GUID) String doctorGuid);

    @GET("api/associations")
    Observable<ArrayList<CommonUserApiResponseModel>> getAssociations(@Query(SEARCH_FILTER) String search, @Query(PAGINATE) boolean paginate, @Query(DOCTOR_GUID) String doctorGuid);

    @GET("api/associated-doctors")
    Observable<ArrayList<DoctorGroupedAssociations>> getDoctorGroupedAssociations();

    @GET("api/associations")
    Observable<ArrayList<CommonUserApiResponseModel>> getUserAssociationDetail(@Query(USER_GUID) String userGuid, @Query(DOCTOR_GUID) String doctorGuid);

    @PUT("api/associations/{id}")
    Observable<BaseApiResponseModel> updateAssociationDetail(@Path(ID) String userGuid, @Body UpdateAssociationRequestModel requestModel);

    @GET("api/associations")
    Observable<ArrayList<CommonUserApiResponseModel>> getAssociationUserDetail(@Query(FILTER_USER_GUID_IN) String guidList, @Query(DOCTOR_GUID) String doctorGuid);

    @GET("api/associations")
    Observable<ArrayList<CommonUserApiResponseModel>> getAssociationUserDetail(@Query(FILTER_USER_GUID_IN) String guidList);

    @GET("api/correspondence-history")
    Observable<RecentsApiResponseModel> getUserCorrespondentHistory(@Query(USER_GUID) String user_guid, @Query(DOCTOR_GUID) String doctorGuid, @Query(MONTH) String month, @Query(CALLS) boolean calls, @Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @GET("api/correspondence-history")
    Observable<RecentsApiResponseModel> getMyCorrespondentHistory(@Query(SEARCH_FILTER) String search, @Query(CALLS) boolean calls, @Query(DOCTOR_GUID) String doctorGuid, @Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @GET("api/unconnected-users")
    Observable<ConnectionListResponseModel> getUnConnectedUsers(@Query(PAGINATE) boolean paginate, @Query("connection_requests") boolean connection_requests, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(SEARCH) String name, @Query(MEDICAL_ASSISTANT) boolean isMedicalAssistant, @Query("role") String role, @Query("specialty") String speciality);

    @POST("api/requests")
    Observable<BaseApiResponseModel> addConnection(@Body AddConnectionRequestModel addConnectionRequestModel, @Query(DOCTOR_GUID) String doctorGuid);

    @DELETE("api/associations")
    Observable<BaseApiResponseModel> disconnectUser(@Query(USER_GUID) String user_guid, @Query(DOCTOR_GUID) String doctorGuid);

    @GET("api/vitals")
    Observable<VitalsPaginatedApiResponseModel> getUserVitals(@Query(TYPE) String type, @Query(USER_GUID) String user_guid, @Query(DOCTOR_GUID) String doctorGuid, @Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @GET("api/vitals")
    Observable<VitalsPaginatedApiResponseModel> getVitals(@Query(TYPE) String type, @Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @GET("api/vitals")
    Observable<ArrayList<VitalsApiResponseModel>> getVitalDetail(@Query(USER_GUID) String user_guid, @Query(DOCTOR_GUID) String doctorGuid, @Query(FILTER_ID_IN) String ids);

    @GET("api/referrals/" + OrderConstant.ORDER_TYPE_LABS)
    Observable<OrdersLabApiResponseModel> getUserLabOrders(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(USER_GUID) String user_guid, @Query(DOCTOR_GUID) String doctorGuid);

    @GET("api/referrals/" + OrderConstant.ORDER_TYPE_LABS)
    Observable<OrdersLabApiResponseModel> getLabOrders(@Query(SEARCH_FILTER_LAB) String search, @Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @GET("icd-codes")
    Observable<IcdCodeApiResponseModel> getFilteredIcdCodes(@Query(FILTER_CODE_IN) String data);

    @GET("icd-codes")
    Observable<IcdCodeApiResponseModel> getAllIcdCodes(@Query(START_KEY) int key, @Query(SEARCH) String search);

    @GET("api/referrals/" + OrderConstant.ORDER_TYPE_PRESCRIPTIONS)
    Observable<OrdersPrescriptionApiResponseModel> getUserPrescriptionsOrders(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(USER_GUID) String user_guid, @Query(DOCTOR_GUID) String doctorGuid);

    @GET("api/referrals/" + OrderConstant.ORDER_TYPE_PRESCRIPTIONS)
    Observable<OrdersPrescriptionApiResponseModel> getPrescriptionsOrders(@Query(SEARCH_FILTER_PRESCIPTION) String search, @Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @PATCH("api/referrals/{type}/{id}")
    Observable<BaseApiResponseModel> cancelOrder(@Path("type") String type, @Path(ID) int id, @Query(DOCTOR_GUID) String doctorGuid, @Query(CANCEL) boolean cancel);

    @POST("api/referrals/" + OrderConstant.ORDER_TYPE_PRESCRIPTIONS)
    Observable<OrdersBaseApiResponseModel> createPrescription(@Query(SYNC_CREATE) boolean sync_create, @Body CreatePrescriptionRequestModel createPrescriptionRequestModel, @Query(DOCTOR_GUID) String doctorGuid);

    @GET("api/users")
    Observable<ArrayList<CommonUserApiResponseModel>> getUsersByGuid(@Query(FILTER_USER_GUID_IN) String data);

    @GET("api/referrals/" + OrderConstant.ORDER_TYPE_SPECIALIST)
    Observable<OrdersSpecialistApiResponseModel> getUserSpecialistList(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(USER_GUID) String user_guid, @Query(DOCTOR_GUID) String doctorGuid);

    @GET("api/referrals/" + OrderConstant.ORDER_TYPE_SPECIALIST)
    Observable<OrdersSpecialistApiResponseModel> getSpecialistList(@Query(SEARCH_FILTER_REFERRAL) String search, @Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

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
    Observable<VitalsCreateApiResponseModel> createVital(@Body CreateVitalApiRequestModel vitalApiRequestModel, @Query(DOCTOR_GUID) String doctorGuid);

    @Multipart
    @POST("api/bulk-upload/vitals")
    Observable<VitalsCreateApiResponseModel> createBulkVital(@Part MultipartBody.Part body, @Query(DOCTOR_GUID) String doctorGuid);


    @GET("api/" + OrderConstant.ORDER_TYPE_FORM)
    Observable<ArrayList<OrdersUserFormsApiResponseModel>> getUserForms(@Query(USER_GUID) String user_guid, @Query(DOCTOR_GUID) String doctorGuid, @Query(ASSIGNOR) boolean assignor);

    @GET("api/" + OrderConstant.ORDER_TYPE_FORM)
    Observable<ArrayList<OrdersUserFormsApiResponseModel>> getAllForms();

    @GET("api/" + OrderConstant.ORDER_TYPE_FORM)
    Observable<ArrayList<OrdersUserFormsApiResponseModel>> getForms(@Query(SEARCH_FILTER_NAME) String search, @Query(ASSIGNOR) boolean assignor);

    @POST("api/" + OrderConstant.ORDER_TYPE_FORM)
    Observable<BaseApiResponseModel> createForms(@Body CreateFormRequestModel createFormRequestModel, @Query(DOCTOR_GUID) String doctorGuid);

    @Multipart
    @PUT("api/" + OrderConstant.ORDER_TYPE_FORM + "/{id}")
    Observable<BaseApiResponseModel> updateForm(@Path(ID) int id, @Part("data") RequestBody data);

    @POST("api/referrals/" + OrderConstant.ORDER_TYPE_SPECIALIST)
    Observable<OrdersBaseApiResponseModel> assignSpecialist(@Query(SYNC_CREATE) boolean sync_create, @Body AssignSpecialistRequestModel assignSpecialistRequestModel, @Query(DOCTOR_GUID) String doctorGuid);

    @Multipart
    @POST("api/users/" + OrderConstant.ORDER_TYPE_FILES)
    Observable<BaseApiResponseModel> uploadDocument(@Part(NAME) RequestBody name, @Part(ORDER_ID) RequestBody vistOrderId, @Part MultipartBody.Part file, @Query(USER_GUID) String userGuid, @Query(DOCTOR_GUID) String doctorGuid);

    @GET("api/users/" + OrderConstant.ORDER_TYPE_FILES)
    Observable<DocumentsApiResponseModel> getDocuments(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @GET("api/users/" + OrderConstant.ORDER_TYPE_FILES)
    Observable<DocumentsApiResponseModel> getUserDocuments(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(USER_GUID) String user_guid, @Query(DOCTOR_GUID) String doctor_guid);

    @DELETE("api/users/" + OrderConstant.ORDER_TYPE_FILES)
    Observable<BaseApiResponseModel> deleteDocument(@Query("user_file_id") int id);

    @PUT("api/users/" + OrderConstant.ORDER_TYPE_FILES + "/{id}")
    Observable<BaseApiResponseModel> updateDocument(@Path(ID) int id, @Body() Map<String, String> param);

    @GET("pharmacies")
    Observable<GetPharmaciesApiResponseModel> getPharmacies(@Query(SEARCH) String query, @Query("city") String city, @Query(START_KEY) int key);

    @POST("api/faxes")
    Observable<BaseApiResponseModel> sendFax(@Body SendFaxRequestModel sendFaxRequestModel, @Query(DOCTOR_GUID) String doctorGuid);

    @POST("api/referrals/" + OrderConstant.ORDER_TYPE_LABS)
    Observable<OrdersBaseApiResponseModel> createLabOrder(@Query(SYNC_CREATE) boolean sync_create, @Body CreateTestApiRequestModel createTestApiRequestModel, @Query(DOCTOR_GUID) String doctorGuid);

    @GET("api/download")
    Observable<Response<ResponseBody>> getPdfFile(@Query("path") String path, @Query("decrypt") boolean isDecrypt);

    @GET
    Observable<Response<ResponseBody>> getPdfFile(@Url String fileUrl);

    @POST("refresh")
    Observable<SigninApiResponseModel> refreshToken(@Header(REFRESH_TOKEN) String refreshToken, @Query("skip_version_check") boolean skip_version_check, @Query("version") String version);

    @POST("api/referrals/" + OrderConstant.ORDER_TYPE_X_RAY)
    Observable<OrdersBaseApiResponseModel> createRadiology(@Query(SYNC_CREATE) boolean sync_create, @Body CreateRadiologyRequestModel createRadiologyRequestModel, @Query(DOCTOR_GUID) String doctorGuid);

    @GET("api/referrals/" + OrderConstant.ORDER_TYPE_X_RAY)
    Observable<GetRadiologyResponseModel> getRadiologyList(@Query(SEARCH_FILTER_XRAY) String search, @Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @GET("api/referrals/" + OrderConstant.ORDER_TYPE_X_RAY)
    Observable<GetRadiologyResponseModel> getUserRadiologyList(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(USER_GUID) String user_guid, @Query(DOCTOR_GUID) String doctorGuid);

    @GET("api/referrals/" + OrderConstant.ORDER_TYPE_MISC)
    Observable<MiscellaneousApiResponseModel> getMiscellaneousList(@Query(SEARCH_FILTER_NOTES) String search, @Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @GET("api/referrals/" + OrderConstant.ORDER_TYPE_MISC)
    Observable<MiscellaneousApiResponseModel> getUserMiscellaneousList(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(USER_GUID) String user_guid, @Query(DOCTOR_GUID) String doctorGuid);

    @GET("api/" + OrderConstant.ORDER_TYPE_EDUCATIONAL_VIDEO)
    Observable<EducationalVideoApiResponseModel> getEducationalVideoList(@Query(SEARCH_FILTER_TITLE) String search, @Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(ASSIGNOR) boolean assignor);

    @GET("api/" + OrderConstant.ORDER_TYPE_EDUCATIONAL_VIDEO)
    Observable<EducationalVideoApiResponseModel> getUserEducationalVideoList(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(USER_GUID) String user_guid, @Query(DOCTOR_GUID) String doctorGuid, @Query(ASSIGNOR) boolean assignor);

    @POST("api/referrals/" + OrderConstant.ORDER_TYPE_MISC)
    Observable<BaseApiResponseModel> createMiscellaneous(@Body CreateMiscellaneousRequestModel createMiscellaneousRequestModel, @Query(DOCTOR_GUID) String doctorGuid);

    @GET("api/referrals")
    Observable<OrdersIdListApiResponseModel> getOrderDetails(@Query(USER_GUID) String userGuid, @Query(DOCTOR_GUID) String doctorGuid, @Query(FILTER_ID_IN) String ids);

    @GET("api/users/" + OrderConstant.ORDER_TYPE_FILES)
    Observable<ArrayList<DocumentsApiResponseModel.ResultBean>> getDocumentDetails(@Query(USER_GUID) String userGuid, @Query(DOCTOR_GUID) String doctorGuid, @Query(FILTER_ID_IN) String ids);

    @GET("api/" + OrderConstant.ORDER_TYPE_FORM)
    Observable<ArrayList<OrdersUserFormsApiResponseModel>> getFormDetails(@Query(USER_GUID) String userGuid, @Query(DOCTOR_GUID) String doctorGuid, @Query(FILTER_ID_IN) String ids);

    @GET("api/schedule")
    Observable<SchedulesApiResponseModel> getSchedules(@Query(SEARCH_FILTER) String search, @Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(DOCTOR_GUID) String doctorGuidList);

    @POST("api/requests")
    Observable<BaseApiResponseModel> createSchedules(@Query(DOCTOR_GUID) String doctorGuidList, @Body SchedulesCreateRequestModel createRequestModel);

    @GET("api/schedule")
    Observable<ArrayList<SchedulesApiResponseModel.ResultBean>> getUserUpcomingSchedules(@Query(USER_GUID) String user_guid, @Query("upcoming") boolean upcoming, @Query(DOCTOR_GUID) String doctorGuid);

    @GET("api/schedule")
    Observable<ArrayList<SchedulesApiResponseModel.ResultBean>> getUserUpcomingSchedules(@Query(USER_GUID) String user_guid, @Query("upcoming") boolean upcoming, @Query(DOCTOR_GUID) String doctorGuid, @Query("day") String day, @Query("month") String month, @Query("year") String year);

    @DELETE("api/schedule")
    Observable<BaseApiResponseModel> deleteSchedule(@Query("schedule_id") int schedule_id, @Query(DOCTOR_GUID) String doctorGuid);

    @GET("api/schedule")
    Observable<SchedulesApiResponseModel.ResultBean> getScheduleDetail(@Query("schedule_id") int schedule_id, @Query(DOCTOR_GUID) String doctorGuid);

    @POST("api/setup/invite")
    Observable<BaseApiResponseModel> inviteUserByDemographic(@Body InviteByDemographicRequestModel demographicRequestModel, @Query(DOCTOR_GUID) String doctor_guid);

    @POST("api/setup/invite")
    Observable<InviteByEmailPhoneApiResponseModel> inviteUserByEmailPhone(@Query(DOCTOR_GUID) String doctor_user_guid, @Body InviteByEmailPhoneRequestModel emailPhoneRequestModel);

    @GET("api/call/{id}")
    Observable<VisitsDetailApiResponseModel> getTranscriptionDetails(@Path(ID) String id);

    @POST("api/pubnub/grant_access")
    Observable<BaseApiResponseModel> grantPubnubAccess(@Query(CHANNEL) String channel);

    @GET("api/download")
    Observable<DownloadTranscriptResponseModel> downloadTranscript(@Query("path") String path, @Query("decrypt") boolean isDecrypt);

    @GET
    Observable<DownloadTranscriptResponseModel> downloadTranscript(@Url String fileUrl);

    @GET("api/sources/{id}")
    Observable<BaseApiResponseModel> getExperimentalFeature(@Path(ID) String id);

    @GET("api/requests")
    Observable<NotificationApiResponseModel> getNotifications(@Query(SEARCH_FILTER) String search, @Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(DOCTOR_GUID) String doctorGuid, @Query(TYPE) String filters);

    @PUT("api/requests")
    Observable<BaseApiResponseModel> setNotificationsRead(@Body Map<String, String> body);

    @PATCH("api/requests/{id}")
    Observable<NotificationRequestUpdateResponseModel> updateNotification(@Path(ID) int id, @Query(DOCTOR_GUID) String doctorGuid, @Body Map<String, Object> body);

    @GET("api/token-v2")
    Observable<CallSettings> getOpenTokToken(@Query(SESSION_ID) String sessionId);

    @GET("api/users/{id}")
    Observable<CommonUserApiResponseModel> getUserDetail(@Path(ID) String id);

    @PUT("api/call/{id}")
    Observable<BaseApiResponseModel> updateCallStatus(@Path(ID) String sessionId, @QueryMap Map<String, String> param);

    @Multipart
    @POST("api/call/{id}")
    Observable<BaseApiResponseModel> uploadScreenshot(@Path(ID) String sessionId, @Part MultipartBody.Part file);

    @GET("api/archive/start")
    Observable<CommonUserApiResponseModel> startArchive(@Query(SESSION_ID) String sessionId);

    @GET("api/archive/stop")
    Observable<CommonUserApiResponseModel> stopArchive(@Query("orderId") String sessionId);

    @GET("api/session")
    Observable<CallSettings> getSessionId(@Query(CALL_QUALITY) String call_quality);

    @POST("api/call-v2")
    Observable<CallSettings> postaVOIPCall(@Query(DOCTOR_GUID) String doctor_guid, @Body Map<String, String> param);

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
    Observable<VitalReportApiReponseModel> getVitalUsers(@Query(FILTER) String filter, @Query(DOCTOR_GUID) String doctorGuid, @Query(START_DATE) String startDate, @Query(END_DATE) String endDate);

    @GET("api/vitals")
    Observable<VitalsPaginatedApiResponseModel> getUserFilteredVitals(@Query(FILTER) String type, @Query(START_DATE) String startDate, @Query(END_DATE) String endDate, @Query(USER_GUID) String user_guid, @Query(DOCTOR_GUID) String doctorGuid, @Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @GET("api/vitals")
    Observable<PDFUrlResponse> getVitalPDF(@Query(TYPE) String type, @Query(FILTER) String filter, @Query(START_DATE) String startDate, @Query(END_DATE) String endDate, @Query(USER_GUID) String user_guid, @Query(DOCTOR_GUID) String doctorGuid, @Query(DOWNLOAD_SUMMARY) boolean summary);

    @GET("api/vitals/documents")
    Observable<VitalBulkPdfApiResponseModel> getBulkVitalPDF(@Query(DOCTOR_GUID) String doctorGuid, @Query(START_DATE) String startDate, @Query(END_DATE) String endDate);

    @GET("api/vitals")
    Observable<ArrayList<VitalsApiResponseModel>> getUserFilteredVitals(@Query(TYPE) String type, @Query(FILTER) String filter, @Query(START_DATE) String startDate, @Query(END_DATE) String endDate, @Query(USER_GUID) String user_guid, @Query(DOCTOR_GUID) String doctorGuid);

    @PUT("api/available")
    Observable<BaseApiResponseModel> updateUserStatus(@Query(STATUS) boolean status);

    @Multipart
    @POST("api/call/{id}")
    Observable<BaseApiResponseModel> postTranscript(@Path(ID) String sessionId, @Part MultipartBody.Part file);

    @GET("api/connection-status/{id}")
    Observable<ConnectionStatusApiResponseModel> getUserConnectionStatus(@Path(ID) String userGuid, @Query(DOCTOR_GUID) String doctorGuid);

    @GET("api/user-diet")
    Observable<ArrayList<DietApiResponseModel>> getDietDetails(@Query(DATE) String date, @Query(USER_GUID) String userGuid, @Query(DOCTOR_GUID) String doctorGuid, @Query(FILTER_ID_IN) String ids);

    @GET("api/user-diet")
    Observable<ArrayList<DietApiResponseModel>> getDietDetails(@Query(FILTER) String filter, @Query(START_DATE) String startDate, @Query(END_DATE) String endDate, @Query(USER_GUID) String userGuid, @Query(DOCTOR_GUID) String doctorGuid);

    @GET("api/user-diet")
    Observable<PDFUrlResponse> getDietPDF(@Query(FILTER) String filter, @Query(START_DATE) String startDate, @Query(END_DATE) String endDate, @Query(USER_GUID) String userGuid, @Query(DOCTOR_GUID) String doctorGuid, @Query(DOWNLOAD_SUMMARY) boolean summary);

    @GET("api/user-diet/users")
    Observable<DietUserListApiResponseModel> getDietUserFilter(@Query(FILTER) String filter, @Query(START_DATE) String startDate, @Query(END_DATE) String endDate, @Query(USER_GUID) String userGuid, @Query(DOCTOR_GUID) String doctorGuid);

    @Multipart
    @POST("api/user-diet")
    Observable<BaseApiResponseModel> addDiet(@PartMap Map<String, RequestBody> requestBodyMap, @Part MultipartBody.Part file);

    @DELETE("api/user-diet/{id}")
    Observable<BaseApiResponseModel> deleteDiet(@Path(ID) int dietId);

    @GET("/api/food-database/parser")
    Observable<FoodListApiResponseModel> getFoodList(@Query("ingr") String query, @Query("session") int session, @Query("app_id") String appId, @Query("app_key") String appKey);

    @POST("/api/food-database/nutrients")
    Observable<FoodDetailApiResponseModel> getFoodDetail(@Query("app_id") String appId, @Query("app_key") String appKey, @Body Map<String, List<NutrientsDetailRequestModel>> param);

    @GET("api/requests")
    Observable<NotificationApiResponseModel> getPendingInvites(@QueryMap Map<String, Object> map, @Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @GET("api/invites")
    Observable<PendingInvitesNonRegisterdApiResponseModel> getNonRegisteredUserInvites(@Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query(ACCEPTED) boolean accepted);

    @GET("api/signal/keys")
    Observable<UserKeysApiResponseModel> getUserKeys(@Query(USER_GUID) String userGuid);

    @POST("api/signal/keys")
    Observable<UserKeysApiResponseModel> postUserKeys(@Body SignalKeyPostModel signalKey);

    @GET("api/messages")
    Observable<ChatApiResponseModel> getChatMessages(@Query("to") String userGuid, @Query(PAGINATE) boolean paginate, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @POST("api/messages")
    Observable<BaseApiResponseModel> sendMessage(@Query("notification") boolean notification, @Body ChatMessageRequestModel chatMessageRequestModel);

    @GET("precanned-messages")
    Observable<PrecannedMessageApiResponse> getPrecannedMessages(@Query(TYPE) String type);

    @PATCH("api/call/{id}")
    Observable<BaseApiResponseModel> updateVisit(@Path(ID) String orderId, @Body UpdateVisitRequestModel requestModel, @Query(DOCTOR_GUID) String doctorGuid);

    @GET("api/call/{id}")
    Observable<VisitsDetailApiResponseModel> getVisitDetail(@Path(ID) String orderId, @Query(DOCTOR_GUID) String doctorGuid);

    @GET("api/call/{id}")
    Observable<VisitSummaryApiResponseModel> getVisitSummary(@Path(ID) String orderId, @Query("visit_summary") boolean isVisitSummary);

    @GET("api/log/requests-log")
    Observable<AccessLogApiResponseModel> getAccessLogs(@Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize, @Query("method") String method);


    @GET("api/educational-video")
    Observable<EducationalVideoResponse> getEducationalVideo(@Query(SEARCH_TITLE_FILTER) String search, @Query(PAGINATE) boolean paginate, @Query(DOCTOR_GUID) String user_guid, @Query(PAGE) int page, @Query(PAGE_SIZE) int pageSize);

    @GET("api/educational-video")
    Observable<ArrayList<EducationalVideoOrder>> getEducationalVideos(@Query(ASSIGNOR) boolean assignor, @Query(FILTER_ID_IN) String ids, @Query(USER_GUID) String userGuid, @Query(DOCTOR_GUID) String doctorGuid);


    @Multipart
    @PATCH("api/educational-video/{id}")
    Observable<BaseApiResponseModel> uploadVideoScreenshot(@Path(ID) String sessionId, @Part MultipartBody.Part file);


    @POST("api/educational-video")
    Observable<EducationalFetchModel> postEducationalVideo(@Body EducationalVideoRequest request);

    @PATCH("api/educational-video/referral/{id}")
    Observable<EducationalFetchModel> patchEducationalVideo(@Path(ID) String videoId, @Body HashMap<String, Object> item);

    @Multipart
    @PATCH("api/educational-video/{id}")
    Observable<BaseApiResponseModel> updateEducationalVideo(@Path(ID) String sessionId, @Part("details") HashMap<String, Object> item);

    @DELETE("api/educational-video/{id}")
    Observable<DeleteEducationalVideoResponse> deleteEducationalVideo(@Path(ID) String sessionId);


    @POST("api/educational-video/remove-user")
    Observable<DeleteEducationalVideoResponse> unAssociateEducationalVideoOrder(@Query(DOCTOR_GUID) String doctorGuid, @Body HashMap<String, Object> item);


    @POST("api/educational-video/assign-user")
    Observable<BaseApiResponseModel> postEducationalOrder(@Query(DOCTOR_GUID) String doctorGuid, @Body HashMap<String, Object> item);

    @POST("setup/invite/accept")
    Observable<GuestLoginApiResponseModel> guestLogin(@Body HashMap<String, Object> params);

    @PUT("api/call/{session_id}")
    Observable<BaseApiResponseModel> kickOutPatient(@Path(SESSIONID) String sessionId, @Query(REJECT) boolean status);

    @POST("api/virtual-rooms/join")
    Observable<GuestLoginApiResponseModel> registerUserEnterWaitingRoom(@Body HashMap<String, Object> params);

    @GET("api/stripe/ephemeral-key")
    Observable<ResponseBody> getEphemeralKey(@Query("api_version") String apiVersion);

    @POST("api/stripe/cards/make-default")
    Observable<ResponseBody> makeDefault(@Body Map<String, String> params);

    @GET("api/stripe/get-setup-intent")
    Observable<SetUpIntentResp> getSetupIntent();

    @GET("api/stripe/default-card")
    Observable<DefaultCardResp> getDefaultCard();


}

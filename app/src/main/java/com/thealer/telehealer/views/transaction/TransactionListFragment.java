package com.thealer.telehealer.views.transaction;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.transaction.AskToAddCardViewModel;
import com.thealer.telehealer.apilayer.models.transaction.RefundViewModel;
import com.thealer.telehealer.apilayer.models.transaction.TransactionListViewModel;
import com.thealer.telehealer.apilayer.models.transaction.req.RefundReq;
import com.thealer.telehealer.apilayer.models.transaction.req.TransactionListReq;
import com.thealer.telehealer.apilayer.models.transaction.resp.TransactionItem;
import com.thealer.telehealer.apilayer.models.transaction.resp.TransactionListResp;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.OnItemEndListener;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.common.fragmentcontainer.FragmentContainerActivity;
import com.thealer.telehealer.stripe.AppPaymentCardUtils;
import com.thealer.telehealer.stripe.PaymentContentActivity;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.CustomDialogClickListener;
import com.thealer.telehealer.views.common.CustomDialogs.ItemPickerDialog;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.PdfViewerFragment;
import com.thealer.telehealer.views.home.DoctorPatientListingFragment;
import com.thealer.telehealer.views.settings.GeneralSettingsFragment;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.HttpException;

public class TransactionListFragment extends BaseFragment {

    private TransactionListViewModel transactionListViewModel;
    private RefundViewModel refundViewModel;
    private AskToAddCardViewModel askToAddCardViewModel;
    private OnViewChangeInterface onViewChangeInterface;

    private CustomRecyclerView rvTransactions;
    private ImageView progressBar;

    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;

    private LinearLayout searchLl;
    private View topView;
    private CardView searchCv;
    private EditText searchEt;
    private ImageView searchClearIv;
    private View bottomView;
    private ImageView filterIv;
    private LinearLayout addCardButton;
    private ImageView filterIndicator;
    private int doctorId=0,patientId=0;
    private Boolean isFromProfile=false;
    private TransactionItem selectedTransaction = null;
    private boolean isAllItemLoaded = false;
    private String userGuid="",doctorGuid="";
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
        transactionListViewModel = new ViewModelProvider(this).get(TransactionListViewModel.class);
        refundViewModel = new ViewModelProvider(this).get(RefundViewModel.class);
        askToAddCardViewModel = new ViewModelProvider(this).get(AskToAddCardViewModel.class);
        onViewChangeInterface.attachObserver(transactionListViewModel);
        onViewChangeInterface.attachObserver(refundViewModel);
        onViewChangeInterface.attachObserver(askToAddCardViewModel);
        if(getArguments()!=null){
            doctorId=getArguments().getInt(ArgumentKeys.DOCTOR_ID,0);
            patientId=getArguments().getInt(ArgumentKeys.PATIENT_ID,0);
            isFromProfile=getArguments().getBoolean(ArgumentKeys.IS_FROM_PROFILE,false);
            userGuid=getArguments().getString(ArgumentKeys.USER_GUID,"");
            doctorGuid=getArguments().getString(ArgumentKeys.DOCTOR_GUID,"");
        }

        askToAddCardViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(ErrorModel errorModel) {
                if (errorModel.geterrorCode() == null){
                    Utils.showAlertDialog(getContext(), getString(R.string.app_name),
                            errorModel.getMessage() != null && !errorModel.getMessage().isEmpty() ? errorModel.getMessage() : getString(R.string.failed_to_connect),
                            null, getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                }else if (!errorModel.geterrorCode().isEmpty() && !errorModel.geterrorCode().equals("SUBSCRIPTION")) {
                    Utils.showAlertDialog(getContext(), getString(R.string.app_name),
                            errorModel.getMessage() != null && !errorModel.getMessage().isEmpty() ? errorModel.getMessage() : getString(R.string.failed_to_connect),
                            null, getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                }
            }
        });

        askToAddCardViewModel.getBaseApiResponseModelMutableLiveData().observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if(baseApiResponseModel.getMessage() != null  && baseApiResponseModel.getMessage() !=null ){
                    showToast(baseApiResponseModel.getMessage());
                }
            }
        });

        transactionListViewModel.getBaseApiResponseModelMutableLiveData().observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModels) {
                if (baseApiResponseModels instanceof TransactionListResp) {
                    if(((TransactionListResp) baseApiResponseModels).getResult().size()==0){
                        isAllItemLoaded=true;
                    }
                    transactionListViewModel.setApiRequested(false);
                    rvTransactions.getRecyclerView().getAdapter().notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    showEmptyState();
                } else {
                    selectedTransaction = null;
                    transactionListViewModel.setPage(1);
                    loadTransactions(true);
                }
                if(baseApiResponseModels != null && baseApiResponseModels.getMessage() != null){
                    showToast(baseApiResponseModels.getMessage());
                }
            }
        });
        transactionListViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(ErrorModel errorModel) {
                if (transactionListViewModel.isApiRequested()) {
                    showEmptyState();
                    transactionListViewModel.setApiRequested(false);
                    progressBar.setVisibility(View.GONE);
                    String errorMessage = errorModel.getCode() == 404 ? getString(R.string.EMPTY_TRANSACTIONS) : errorModel.getMessage() != null ? errorModel.getMessage() : getString(R.string.failed_to_connect);
                    Utils.showAlertDialog(getContext(), getString(R.string.app_name), errorMessage, getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, null);
                } else {
                        String json = errorModel.getResponse();
                        if (json != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                if (jsonObject.has("display_button") && !errorModel.isDisplayButton()) {
                                    String errMsg = errorModel.getMessage() != null ? errorModel.getMessage() : getString(R.string.failed_to_connect);
                                    Utils.showAlertDialog(getContext(), getString(R.string.app_name), errMsg,
                                            getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }, null);
                                }else if (!jsonObject.has("is_cc_captured") || AppPaymentCardUtils.hasValidPaymentCard(errorModel)) {
                                    String message = errorModel.getMessage() != null ? errorModel.getMessage() : getString(R.string.failed_to_connect);
                                    if (UserType.isUserAssistant()) {
                                        Utils.showAlertDialog(getContext(), getString(R.string.app_name), message, getString(R.string.lbl_proceed_offline), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String currentUserGuid="";
                                                try{
                                                    currentUserGuid=doctorGuid;
                                                }catch (Exception e){
                                                    currentUserGuid="";
                                                }
                                                if(!UserType.isUserAssistant())
                                                    currentUserGuid="";

                                                transactionListViewModel.processPayment(currentUserGuid,selectedTransaction.getId(), Constants.PaymentMode.CASH);
                                                dialog.dismiss();
                                            }
                                        }, (dialog, which) -> {
                                            selectedTransaction = null;
                                            dialog.dismiss();
                                        }).getWindow().setBackgroundDrawableResource(R.drawable.border_red);
                                    } else {
                                        Runnable paymentSettingListener = new Runnable() {
                                            @Override
                                            public void run() {
                                                Constants.isRedirectProfileSetting = true;
                                                ((OnCloseActionInterface) getActivity()).onClose(false);
                                            }
                                        };

                                        Runnable proceedOfflineListener = new Runnable() {
                                            @Override
                                            public void run() {
                                                String currentUserGuid="";
                                                try{
                                                    currentUserGuid=doctorGuid;
                                                }catch (Exception e){
                                                    currentUserGuid="";
                                                }
                                                if(!UserType.isUserAssistant())
                                                    currentUserGuid="";
                                                transactionListViewModel.processPayment(currentUserGuid,selectedTransaction.getId(), Constants.PaymentMode.CASH);
                                            }
                                        };

                                        Utils.showAlertDialogWithClose(getActivity(), getString(R.string.app_name), message,
                                                getString(R.string.lbl_proceed_offline), getString(R.string.payment_settings),
                                                proceedOfflineListener, paymentSettingListener, null).getWindow().setBackgroundDrawableResource(R.drawable.border_red);
                                    }
                                } else {
                                    if (selectedTransaction != null) {
                                        showPatientCardErrorOptions();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Utils.showAlertDialog(getContext(), getString(R.string.app_name), getString(R.string.something_went_wrong_try_again), null, getString(R.string.cancel), null, (dialog, which) -> {
                                selectedTransaction = null;
                                dialog.dismiss();
                            });
                        }

                }
            }
        });

        refundViewModel.getBaseApiResponseModelMutableLiveData().observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModels) {
                transactionListViewModel.setPage(1);
                loadTransactions(true);

                if (baseApiResponseModels.getMessage() != null)
                   showToast(baseApiResponseModels.getMessage());
            }
        });
        refundViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(ErrorModel errorModel) {
                if (errorModel.geterrorCode() == null){
                    String errorMessage = errorModel.getMessage() != null ? errorModel.getMessage() : getString(R.string.failed_to_connect);
                    Utils.showAlertDialog(getContext(), getString(R.string.app_name), errorMessage, getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, null);
                }else if (!errorModel.geterrorCode().isEmpty() && !errorModel.geterrorCode().equals("SUBSCRIPTION")) {
                    String errorMessage = errorModel.getMessage() != null ? errorModel.getMessage() : getString(R.string.failed_to_connect);
                    Utils.showAlertDialog(getContext(), getString(R.string.app_name), errorMessage, getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, null);
                }
            }
        });
        loadTransactions(true);
    }

    private void showPatientCardErrorOptions() {
        ArrayList<String> options = new ArrayList<>();
        if (selectedTransaction.getDoctorId() != null)
            if (selectedTransaction.getDoctorId().isCan_view_card_status())
                options.add(getString(R.string.lbl_ask_to_add_credit_card));
        options.add(getString(R.string.lbl_proceed_offline));
        String message = getString(R.string.msg_invalid_credit_card_in_transaction_process, selectedTransaction.getPatientId().getDisplayName());

        if (options.size() == 1) {
            Utils.showAlertDialog(getContext(), getString(R.string.app_name), message, getString(R.string.lbl_proceed_offline), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String currentUserGuid="";
                    try{
                        currentUserGuid=doctorGuid;
                    }catch (Exception e){
                        currentUserGuid="";
                    }
                    if(!UserType.isUserAssistant())
                        currentUserGuid="";
                    transactionListViewModel.processPayment(currentUserGuid,selectedTransaction.getId(), Constants.PaymentMode.CASH);
                    dialog.dismiss();
                }
            }, (dialog, which) -> dialog.dismiss()).getWindow().setBackgroundDrawableResource(R.drawable.border_red);
            return;
        }
        ItemPickerDialog itemPickerDialog = new ItemPickerDialog(getActivity(), message, options, new PickerListener() {
            @Override
            public void didSelectedItem(int position) {
                String currentUserGuid="";
                try{
                    currentUserGuid=doctorGuid;
                }catch (Exception e){
                    currentUserGuid="";
                }
                if(!UserType.isUserAssistant())
                    currentUserGuid="";
                if (getString(R.string.lbl_ask_to_add_credit_card).equals(options.get(position))) {
                    askToAddCardViewModel.askToAddCard(currentUserGuid, selectedTransaction.getDoctorId().getUser_guid());
                } else if (getString(R.string.lbl_proceed_offline).equals(options.get(position))) {
                    transactionListViewModel.processPayment(currentUserGuid,selectedTransaction.getId(), Constants.PaymentMode.CASH);
                }

            }

            @Override
            public void didCancelled() {

            }
        });
        itemPickerDialog.getWindow().setBackgroundDrawableResource(R.drawable.border_red);
        itemPickerDialog.setCancelable(false);
        itemPickerDialog.show();
    }

    private void loadTransactions(boolean showProgress) {
        selectedTransaction = null;
        transactionListViewModel.loadTransactions(showProgress,doctorId,patientId,isFromProfile);
    }

    private void showEmptyState() {
        if (transactionListViewModel.getPage() == 1 && transactionListViewModel.getTransactions().size() == 0) {
            rvTransactions.setEmptyState(EmptyViewConstants.EMPTY_TRANSACTIONS);
            rvTransactions.showEmptyState();
        }else{
            rvTransactions.hideEmptyState();
            rvTransactions.updateView();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addCardButton = (LinearLayout) view.findViewById(R.id.btnAddCard);
        searchLl = (LinearLayout) view.findViewById(R.id.search_ll);
        topView = (View) view.findViewById(R.id.top_view);
        searchCv = (CardView) view.findViewById(R.id.search_cv);
        searchEt = (EditText) view.findViewById(R.id.search_et);
        searchClearIv = (ImageView) view.findViewById(R.id.search_clear_iv);
        bottomView = (View) view.findViewById(R.id.bottom_view);

        rvTransactions = view.findViewById(R.id.rvTransactions);
        progressBar = view.findViewById(R.id.progressbar);

        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        filterIv = view.findViewById(R.id.filter_iv);
        filterIndicator = view.findViewById(R.id.filter_indicatior_iv);
        if (UserType.isUserPatient())
            toolbarTitle.setText(getString(R.string.lbl_charges));
        else
            toolbarTitle.setText(getString(R.string.lbl_patient_payments));

        rvTransactions.setScrollable(false);
        rvTransactions.setEmptyState(EmptyViewConstants.EMPTY_PAYMENTS);
        rvTransactions.hideEmptyState();
        rvTransactions.getSwipeLayout().setEnabled(true);
        rvTransactions.getSwipeLayout().setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isAllItemLoaded=false;
                rvTransactions.getSwipeLayout().setRefreshing(false);
                transactionListViewModel.setPage(1);
                loadTransactions(true);
            }
        });

        if (getArguments() != null) {
            if (getArguments().getBoolean(ArgumentKeys.IS_HIDE_TOOLBAR, false)) {
                appbarLayout.setVisibility(View.GONE);
            }
        }

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnCloseActionInterface) getActivity()).onClose(false);
            }
        });

        filterIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), TransactionFilterActivity.class)
                        .putExtra(TransactionFilterActivity.EXTRA_FILTER, new Gson().toJson(transactionListViewModel.getFilterReq())), RequestID.REQ_FILTER);
            }
        });

        if (UserType.isUserPatient())
            addCardButton.setVisibility(View.VISIBLE);

        addCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PaymentContentActivity.class).putExtra(ArgumentKeys.IS_HEAD_LESS, true));
            }
        });

        Glide.with(getActivity().getApplicationContext()).load(R.raw.throbber).into(progressBar);

        rvTransactions.getRecyclerView().setAdapter(new TransactionListAdapter(transactionListViewModel.getTransactions(), new TransactionListAdapter.OnOptionSelected() {
            @Override
            public void onReceiptClick(int pos) {
                String url = transactionListViewModel.getTransactions().get(pos).getTransactionReceipt();
                if (url == null || url.isEmpty()) {
                    Utils.showAlertDialog(getContext(), getString(R.string.app_name), getString(R.string.msg_receipt_not_generated_yet_please_try_again_later), getString(R.string.ok), null, (dialog, which) -> {
                        dialog.dismiss();
                    }, null);
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString(ArgumentKeys.PDF_URL, url);
                bundle.putString(ArgumentKeys.PDF_TITLE, getString(R.string.lbl_receipt));
                startActivity(new Intent(getActivity(), FragmentContainerActivity.class)
                        .putExtra(FragmentContainerActivity.EXTRA_FRAGMENT, PdfViewerFragment.class.getName())
                        .putExtra(FragmentContainerActivity.EXTRA_SHOW_TOOLBAR, false)
                        .putExtra(FragmentContainerActivity.EXTRA_BUNDLE, bundle));
            }

            @Override
            public void onProcessPaymentClick(int pos) {
                selectedTransaction = transactionListViewModel.getTransactions().get(pos);
                if (selectedTransaction.getChargeStatus() == Constants.ChargeStatus.CHARGE_PROCESS_FAILED) {
                    if (selectedTransaction.getMaxRetries() >= Constants.MAX_TRANSACTION_RETRY) {
                        Utils.showAlertDialog(getContext(), getString(R.string.app_name), getString(R.string.msg_transaction_failed, selectedTransaction.getPatientId().getDisplayName()), getString(R.string.lbl_proceed_offline), getString(R.string.cancel), (dialog, which) -> {
                            String currentUserGuid="";
                            try{
                                currentUserGuid=doctorGuid;
                            }catch (Exception e){
                                currentUserGuid="";
                            }
                            if(!UserType.isUserAssistant())
                                currentUserGuid="";
                            transactionListViewModel.processPayment(currentUserGuid,transactionListViewModel.getTransactions().get(pos).getId(), Constants.PaymentMode.CASH);
                            dialog.dismiss();
                        }, null);
                        return;
                    }
                }
                String currentUserGuid="";
                try{
                    currentUserGuid=doctorGuid;
                }catch (Exception e){
                    currentUserGuid="";
                }
                if(!UserType.isUserAssistant())
                    currentUserGuid="";
                transactionListViewModel.processPayment(currentUserGuid,transactionListViewModel.getTransactions().get(pos).getId(), Constants.PaymentMode.STRIPE);
            }

            @Override
            public void onRefundClick(int pos) {
                showRefundDialog(pos);
            }

            @Override
            public void onAddChargeClick(int position) {
                if (UserDetailPreferenceManager.getWhoAmIResponse().getRole().equals(Constants.ROLE_ASSISTANT)){
                    startActivityForResult(new Intent(getActivity(), AddChargeActivity.class)
                            .putExtra(ArgumentKeys.DOCTOR_GUID,doctorGuid)
                            .putExtra(AddChargeActivity.EXTRA_TRANSACTION_ITEM, new Gson().toJson(transactionListViewModel.getTransactions().get(position))), RequestID.REQ_UPDATE_LIST);
                }else {
                    startActivityForResult(new Intent(getActivity(), AddChargeActivity.class)
                            .putExtra(AddChargeActivity.EXTRA_TRANSACTION_ITEM, new Gson().toJson(transactionListViewModel.getTransactions().get(position))), RequestID.REQ_UPDATE_LIST);
                }
            }

            @Override
            public void onUpdateChargeClick(int position) {
                if (UserDetailPreferenceManager.getWhoAmIResponse().getRole().equals(Constants.ROLE_ASSISTANT)){
                    startActivityForResult(new Intent(getActivity(), AddChargeActivity.class)
                            .putExtra(ArgumentKeys.DOCTOR_GUID,doctorGuid)
                            .putExtra(AddChargeActivity.EXTRA_TRANSACTION_ITEM, new Gson().toJson(transactionListViewModel.getTransactions().get(position))), RequestID.REQ_UPDATE_LIST);
                }else {
                    startActivityForResult(new Intent(getActivity(), AddChargeActivity.class)
                            .putExtra(AddChargeActivity.EXTRA_TRANSACTION_ITEM, new Gson().toJson(transactionListViewModel.getTransactions().get(position))), RequestID.REQ_UPDATE_LIST);
                }
            }

            @Override
            public void onItemClick(int position) {
                startActivity(new Intent(getActivity(), TransactionDetailsActivity.class).putExtra(TransactionDetailsActivity.EXTRA_TRANSACTION, new Gson().toJson(transactionListViewModel.getTransactions().get(position))));
            }
        }, new OnItemEndListener() {
            @Override
            public void itemEnd(int position) {
                if(!isAllItemLoaded) {
                    transactionListViewModel.setPage(transactionListViewModel.getPage() + 1);
                    loadTransactions(false);
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        }));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvTransactions.setLayoutManager(linearLayoutManager);

        /*rvTransactions.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (linearLayoutManager.getItemCount() > 0 && linearLayoutManager.getItemCount() < transactionListViewModel.getTotalCount()) {
                    if (linearLayoutManager.findLastVisibleItemPosition() == linearLayoutManager.getItemCount() - 1) {
                        transactionListViewModel.setPage(transactionListViewModel.getPage() + 1);
                        loadTransactions(false);
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
*/
        /*if (UserType.isUserAssistant()) {
            searchEt.setHint(getString(R.string.lbl_search_patient));
            filterIv.setVisibility(View.GONE);
            filterIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }*/

    }

    private void showRefundDialog(int pos) {
        Utils.showUserInputDialog(getActivity(),
                getString(R.string.lbl_refund),
                getString(R.string.msg_refund_info),
                getString(R.string.lbl_add_refund),
                getString(R.string.Done),
                getString(R.string.Cancel),
                InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL,
                new CustomDialogClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, String inputText) {
                        if (inputText != null && inputText.length() > 0) {
                            TextView textView = ((Dialog) dialog).findViewById(R.id.error_tv);
                            double amount = Double.parseDouble(inputText);
                            TransactionItem transactionItem = transactionListViewModel.getTransactions().get(pos);
                            if (amount == 0) {
                                textView.setText(getString(R.string.msg_please_enter_valid_refund_amount));
                                textView.setVisibility(View.VISIBLE);
                            } else if (amount < Constants.STRIPE_MIN_AMOUNT) {
                                textView.setText(getString(R.string.msg_refund_amount_should_be_greater_than_minimum, Constants.STRIPE_MIN_AMOUNT));
                                textView.setVisibility(View.VISIBLE);
                            } else if (amount > transactionItem.getAmount()) {
                                textView.setText(getString(R.string.msg_refund_amount_should_not_exceed_charge_amount));
                                textView.setVisibility(View.VISIBLE);
                            } else {
                                textView.setVisibility(View.GONE);
                                RefundReq req = new RefundReq();
                                req.setRefundAmount(Double.parseDouble(inputText));
                                String currentDoctorGuid=selectedTransaction.getDoctorId().getUser_guid();


                                refundViewModel.processRefund(currentDoctorGuid,transactionListViewModel.getTransactions().get(pos).getId(), req);
                                dialog.dismiss();
                            }
                        } else {
                            Utils.showAlertDialog(getContext(),
                                    getString(R.string.app_name),
                                    getString(R.string.msg_please_enter_valid_refund_amount),
                                    getString(R.string.ok),
                                    null,
                                    null,
                                    null);
                        }
                    }
                },
                new CustomDialogClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, String inputText) {
                        dialog.dismiss();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == RequestID.REQ_UPDATE_LIST) {
            transactionListViewModel.setPage(1);
            loadTransactions(true);
        }
        if (resultCode == Activity.RESULT_OK && requestCode == RequestID.REQ_FILTER) {
            String filterJson = data.getStringExtra(TransactionFilterActivity.EXTRA_FILTER);
            if (filterJson != null) {
                transactionListViewModel.setFilterReq(new Gson().fromJson(filterJson, TransactionListReq.class));
                transactionListViewModel.setPage(1);
                loadTransactions(true);
                filterIndicator.setVisibility(View.VISIBLE);
            } else
                filterIndicator.setVisibility(View.GONE);

            if (data.getBooleanExtra(TransactionFilterActivity.EXTRA_IS_RESET, false)) {
                filterIndicator.setVisibility(View.GONE);
            }

        }
    }
}

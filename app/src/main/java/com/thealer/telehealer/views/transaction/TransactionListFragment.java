package com.thealer.telehealer.views.transaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.transaction.RefundViewModel;
import com.thealer.telehealer.apilayer.models.transaction.TransactionListViewModel;
import com.thealer.telehealer.apilayer.models.transaction.req.RefundReq;
import com.thealer.telehealer.apilayer.models.transaction.resp.TransactionItem;
import com.thealer.telehealer.apilayer.models.transaction.resp.TransactionListResp;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.stripe.AppPaymentCardUtils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.CallPlacingActivity;
import com.thealer.telehealer.views.common.CustomDialogClickListener;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import java.util.ArrayList;

public class TransactionListFragment extends BaseFragment {

    private TransactionListViewModel transactionListViewModel;
    private RefundViewModel refundViewModel;
    private OnViewChangeInterface onViewChangeInterface;

    private CustomRecyclerView rvTransactions;
    private ImageView progressBar;

    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;

    private TransactionItem selectedTransaction = null;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
        transactionListViewModel = new ViewModelProvider(this).get(TransactionListViewModel.class);
        refundViewModel = new ViewModelProvider(this).get(RefundViewModel.class);
        onViewChangeInterface.attachObserver(transactionListViewModel);
        onViewChangeInterface.attachObserver(refundViewModel);

        transactionListViewModel.getBaseApiResponseModelMutableLiveData().observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModels) {
                if (baseApiResponseModels instanceof TransactionListResp) {
                    rvTransactions.getRecyclerView().getAdapter().notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    showEmptyState();
                } else {
                    transactionListViewModel.setPage(1);
                    transactionListViewModel.loadTransactions(true);
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
                    String errorMessage = errorModel.getMessage() != null ? errorModel.getMessage() : getString(R.string.failed_to_connect);
                    Utils.showAlertDialog(getContext(), getString(R.string.error), errorMessage, getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, null);
                } else {
                    if (AppPaymentCardUtils.hasValidPaymentCard(errorModel)) {
                        selectedTransaction = null;
                        String message = errorModel.getMessage() != null ? errorModel.getMessage() : getString(R.string.failed_to_connect);
                        Utils.showAlertDialog(getContext(), getString(R.string.error), message, getString(R.string.ok), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, null);
                    } else {
                        if (selectedTransaction != null) {
                            String message = getString(R.string.msg_invalid_credit_card_in_transaction_process, selectedTransaction.getPatientId().getDisplayName());
                            Utils.showAlertDialog(getContext(), getString(R.string.app_name), message, getString(R.string.lbl_mark_as_completed), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, null);
                        }
                    }
                }
            }
        });

        refundViewModel.getBaseApiResponseModelMutableLiveData().observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModels) {
                transactionListViewModel.setPage(1);
                transactionListViewModel.loadTransactions(true);
                if (baseApiResponseModels.getMessage() != null)
                    Utils.showAlertDialog(getContext(), getString(R.string.success), baseApiResponseModels.getMessage(), getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, null);
            }
        });
        refundViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(ErrorModel errorModel) {
                String errorMessage = errorModel.getMessage() != null ? errorModel.getMessage() : getString(R.string.failed_to_connect);
                Utils.showAlertDialog(getContext(), getString(R.string.error), errorMessage, getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, null);
            }
        });
        transactionListViewModel.loadTransactions(true);
    }

    private void showEmptyState() {
        if (transactionListViewModel.getPage() == 1 && transactionListViewModel.getTransactions().size() == 0) {
            rvTransactions.setEmptyState(EmptyViewConstants.EMPTY_TRANSACTIONS);
            rvTransactions.showEmptyState();
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
        rvTransactions = view.findViewById(R.id.rvTransactions);
        progressBar = view.findViewById(R.id.progressbar);

        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.lbl_patient_payments));

        rvTransactions.setScrollable(false);
        rvTransactions.setEmptyState(EmptyViewConstants.EMPTY_PAYMENTS);
        rvTransactions.hideEmptyState();


        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnCloseActionInterface) getActivity()).onClose(false);
            }
        });

        Glide.with(getActivity().getApplicationContext()).load(R.raw.throbber).into(progressBar);
        rvTransactions.getRecyclerView().setAdapter(new TransactionListAdapter(transactionListViewModel.getTransactions(), new TransactionListAdapter.OnOptionSelected() {
            @Override
            public void onReceiptClick(int pos) {

            }

            @Override
            public void onProcessPaymentClick(int pos) {
                selectedTransaction = transactionListViewModel.getTransactions().get(pos);
                transactionListViewModel.processPayment(transactionListViewModel.getTransactions().get(pos).getId());
            }

            @Override
            public void onRefundClick(int pos) {
                showRefundDialog(pos);
            }

            @Override
            public void onAddChargeClick(int position) {
                startActivity(new Intent(getActivity(), AddChargeActivity.class));
            }

            @Override
            public void onUpdateChargeClick(int position) {
                startActivity(new Intent(getActivity(), AddChargeActivity.class).putExtra(AddChargeActivity.EXTRA_TRANSACTION_ITEM, new Gson().toJson(transactionListViewModel.getTransactions().get(position))));
            }
        }));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvTransactions.setLayoutManager(linearLayoutManager);

        rvTransactions.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (linearLayoutManager.getItemCount() > 0 && linearLayoutManager.getItemCount() < transactionListViewModel.getTotalCount()) {
                    if (linearLayoutManager.findLastVisibleItemPosition() == linearLayoutManager.getItemCount() - 1) {
                        transactionListViewModel.setPage(transactionListViewModel.getPage() + 1);
                        transactionListViewModel.loadTransactions(false);
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showRefundDialog(int pos) {
        Utils.showUserInputDialog(getActivity(),
                getString(R.string.lbl_refund),
                getString(R.string.msg_refund_info),
                getString(R.string.lbl_add_charge),
                getString(R.string.Done),
                getString(R.string.Cancel),
                InputType.TYPE_CLASS_NUMBER,
                new CustomDialogClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, String inputText) {
                        if (inputText != null && inputText.length() > 0) {
                            RefundReq req = new RefundReq();
                            req.setRefundAmount(Integer.parseInt(inputText));
                            refundViewModel.processRefund(transactionListViewModel.getTransactions().get(pos).getId(), req);
                            dialog.dismiss();
                        } else {
                            Utils.showAlertDialog(getContext(),
                                    getString(R.string.error),
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
}

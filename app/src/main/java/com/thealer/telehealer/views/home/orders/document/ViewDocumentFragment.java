package com.thealer.telehealer.views.home.orders.document;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.documents.DocumentsApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;

/**
 * Created by Aswin on 29,November,2018
 */
public class ViewDocumentFragment extends BaseFragment implements Toolbar.OnMenuItemClickListener {
    private ImageView backIv;
    private TextView toolbarTitle;
    private ViewPager viewPager;
    private TextView countTv;
    private Toolbar toolbar;
    private Menu myMenu;

    private OrdersApiViewModel ordersApiViewModel;
    private DocumentsApiResponseModel documentsApiResponseModel;
    private DocumentsPagerAdapter documentsPagerAdapter;
    private OnCloseActionInterface onCloseActionInterface;
    private AttachObserverInterface attachObserverInterface;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private AppBarLayout appbarLayout;
    private ImageView backgroundIv;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
        ordersApiViewModel = ViewModelProviders.of(this).get(OrdersApiViewModel.class);
        attachObserverInterface.attachObserver(ordersApiViewModel);
        ordersApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel.isSuccess()) {
                        onCloseActionInterface.onClose(false);
                    }
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_document_view, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        countTv = (TextView) view.findViewById(R.id.count_tv);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        backgroundIv = (ImageView) view.findViewById(R.id.background_iv);

        if (getArguments() != null) {

            boolean isFromHome = getArguments().getBoolean(Constants.IS_FROM_HOME);

            if (isFromHome) {
                toolbar.inflateMenu(R.menu.documents_menu);
                toolbar.setOnMenuItemClickListener(this);
            }

            documentsApiResponseModel = (DocumentsApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
            int selectedId = getArguments().getInt(Constants.SELECTED_ITEM);

            documentsPagerAdapter = new DocumentsPagerAdapter(getActivity(), documentsApiResponseModel.getResult());

            int position = 0;
            for (int i = 0; i < documentsApiResponseModel.getResult().size(); i++) {
                if (documentsApiResponseModel.getResult().get(i).getUser_file_id() == selectedId) {
                    position = i;
                    break;
                }
            }

            viewPager.setAdapter(documentsPagerAdapter);
            viewPager.setCurrentItem(position);
            setPositionCount(position, documentsPagerAdapter.getCount());
            setToolbarTitle(documentsApiResponseModel.getResult().get(position).getName());

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    setToolbarTitle(documentsApiResponseModel.getResult().get(i).getName());
                    setPositionCount(i, documentsPagerAdapter.getCount());
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });

            backIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCloseActionInterface.onClose(false);
                }
            });
        }

    }

    private void setPositionCount(int i, int size) {
        countTv.setText((i + 1) + " of " + size);
        Utils.setImageWithGlide(getActivity().getApplicationContext(), backgroundIv, documentsApiResponseModel.getResult().get(i).getPath(), getActivity().getDrawable(R.drawable.document_placeholder_drawable), true);
    }

    private void setToolbarTitle(String name) {
        toolbarTitle.setText(name);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                Bundle bundle = new Bundle();
                bundle.putBoolean(ArgumentKeys.IS_EDIT_MODE, true);
                bundle.putSerializable(ArgumentKeys.DOCUMENT_MODEL, documentsApiResponseModel.getResult().get(viewPager.getCurrentItem()));
                CreateNewDocumentFragment createNewDocumentFragment = new CreateNewDocumentFragment();
                createNewDocumentFragment.setArguments(bundle);
                createNewDocumentFragment.setTargetFragment(this, RequestID.REQ_UPDATE_DOCUMENT);
                showSubFragmentInterface.onShowFragment(createNewDocumentFragment);
                break;
            case R.id.menu_delete:
                Utils.showAlertDialog(getActivity(), getString(R.string.alert), getString(R.string.are_you_sure_to_delete_document),
                        getString(R.string.delete),
                        getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteDocument();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                break;
        }
        return true;
    }

    private void deleteDocument() {
        ordersApiViewModel.deleteDocument(documentsApiResponseModel.getResult().get(viewPager.getCurrentItem()).getUser_file_id(), true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == RequestID.REQ_UPDATE_DOCUMENT) {
            onCloseActionInterface.onClose(false);
        }
    }
}

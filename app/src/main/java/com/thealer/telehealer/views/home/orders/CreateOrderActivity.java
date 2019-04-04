package com.thealer.telehealer.views.home.orders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CameraInterface;
import com.thealer.telehealer.common.CameraUtil;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.common.SuccessViewInterface;
import com.thealer.telehealer.views.home.orders.document.CreateNewDocumentFragment;
import com.thealer.telehealer.views.home.orders.forms.CreateNewFormFragment;
import com.thealer.telehealer.views.home.orders.labs.CreateNewLabFragment;
import com.thealer.telehealer.views.home.orders.miscellaneous.CreateNewMiscellaneousFragment;
import com.thealer.telehealer.views.home.orders.prescription.CreateNewPrescriptionFragment;
import com.thealer.telehealer.views.home.orders.radiology.CreateNewRadiologyFragment;
import com.thealer.telehealer.views.home.orders.specialist.CreateNewSpecialistFragment;
import com.thealer.telehealer.views.signin.SigninActivity;

import java.util.ArrayList;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 28,November,2018
 */
public class CreateOrderActivity extends BaseActivity implements View.OnClickListener, ShowSubFragmentInterface,
        AttachObserverInterface, OnCloseActionInterface, SuccessViewInterface, ChangeTitleInterface {

    private ImageView backIv;
    private TextView toolbarTitle;
    private LinearLayout fragmentHolder;

    private String orderType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);
        initView();
    }

    private void initView() {
        backIv = (ImageView) findViewById(R.id.back_iv);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        fragmentHolder = (LinearLayout) findViewById(R.id.fragment_holder);

        backIv.setOnClickListener(this);

        if (getIntent() != null) {

            Bundle bundle = getIntent().getExtras();

            if (bundle == null)
                bundle = new Bundle();

            if (getIntent().getAction() != null) {

                orderType = OrderConstant.ORDER_DOCUMENTS;
                bundle.putBoolean(ArgumentKeys.IS_SHARED_INTENT, true);

                ArrayList<Uri> uriArrayList = new ArrayList<>();

                if (getIntent().getAction().equals(Intent.ACTION_SEND_MULTIPLE)) {
                    uriArrayList.addAll(getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM));
                } else if (getIntent().getAction().equals(Intent.ACTION_SEND)) {
                    uriArrayList.add(getIntent().getParcelableExtra(Intent.EXTRA_STREAM));
                }

                Constants.sharedPath = new ArrayList<>();

                for (int i = 0; i < uriArrayList.size(); i++) {
                    if (CameraUtil.isTypeImage(this, uriArrayList.get(i))) {
                        String path = CameraUtil.getRealPathFromUri(this, uriArrayList.get(i));
                        if (path != null && !path.isEmpty()) {
                            Constants.sharedPath.add(path);
                        }
                    }
                }

                if (!appPreference.getBoolean(PreferenceConstants.IS_USER_LOGGED_IN)) {
                    startActivity(new Intent(this, SigninActivity.class));
                    finish();
                }

            } else {
                orderType = bundle.getString(Constants.SELECTED_ITEM);
            }

            if (orderType != null) {
                Fragment fragment = getFragment(orderType);
                if (fragment != null) {
                    fragment.setArguments(bundle);
                    setFragment(fragment);
                }
            }
        }
    }

    private Fragment getFragment(String orderType) {
        switch (orderType) {
            case OrderConstant.ORDER_FORM:
                return new CreateNewFormFragment();
            case OrderConstant.ORDER_PRESCRIPTIONS:
                return new CreateNewPrescriptionFragment();
            case OrderConstant.ORDER_DOCUMENTS:
                return new CreateNewDocumentFragment();
            case OrderConstant.ORDER_LABS:
                return new CreateNewLabFragment();
            case OrderConstant.ORDER_RADIOLOGY:
                return new CreateNewRadiologyFragment();
            case OrderConstant.ORDER_REFERRALS:
                return new CreateNewSpecialistFragment();
            case OrderConstant.ORDER_MISC:
                return new CreateNewMiscellaneousFragment();
        }
        return null;
    }

    private void setFragment(Fragment fragment) {
        if (fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right)
                    .replace(fragmentHolder.getId(), fragment)
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commit();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0 || getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onShowFragment(Fragment fragment) {
        setFragment(fragment);
    }

    @Override
    public void onClose(boolean isRefreshRequired) {
        if (isRefreshRequired) {
            finish();
        } else {
            onBackPressed();
        }
    }

    @Override
    public void onSuccessViewCompletion(boolean success) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PermissionConstants.PERMISSION_CAM_PHOTOS) {
                CameraUtil.showImageSelectionAlert(this);
            } else if (requestCode == PermissionConstants.PERMISSION_CAMERA) {
                CameraUtil.openCamera(this);
            } else if (requestCode == PermissionConstants.PERMISSION_GALLERY) {
                CameraUtil.openGallery(this);
            } else if (requestCode == PermissionConstants.GALLERY_REQUEST_CODE || requestCode == PermissionConstants.CAMERA_REQUEST_CODE) {

                String imagePath = CameraUtil.getImagePath(this, requestCode, resultCode, data);

                CameraInterface cameraInterface = (CameraInterface) getSupportFragmentManager().getFragments().get(0);
                cameraInterface.onImageReceived(imagePath);

            } else if (requestCode == PermissionConstants.PERMISSION_STORAGE) {
                getSupportFragmentManager().getFragments().get(0).onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onTitleChange(String title) {
        toolbarTitle.setText(title);
    }
}

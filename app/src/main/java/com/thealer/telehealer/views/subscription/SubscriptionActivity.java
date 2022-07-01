package com.thealer.telehealer.views.subscription;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.stripe.AppPaymentCardUtils;
import com.thealer.telehealer.stripe.PaymentContentActivity;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.HomeActivity;

import org.w3c.dom.Text;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

public class SubscriptionActivity extends BaseActivity implements
        ShowSubFragmentInterface,
        OnCloseActionInterface,
        AttachObserverInterface {

    private WhoAmIApiViewModel whoAmIApiViewModel;
    private TextView txtTitle;
    public boolean isfirsttym = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
        txtTitle = findViewById(R.id.txt_title);
        initObserver();


    }

    @Override
    protected void onResume() {
        super.onResume();
        whoAmIApiViewModel.assignWhoAmI();
    }

    private void initObserver() {
        whoAmIApiViewModel = new ViewModelProvider(this).get(WhoAmIApiViewModel.class);
        attachObserver(whoAmIApiViewModel);

        whoAmIApiViewModel.getBaseApiResponseModelMutableLiveData().observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    WhoAmIApiResponseModel whoAmIApiResponseModel = (WhoAmIApiResponseModel) baseApiResponseModel;
                    if (Constants.ROLE_DOCTOR.equals(whoAmIApiResponseModel.getRole())) {
                        if (whoAmIApiResponseModel.isFirst_time_subscription_purchased()) {
                            if (whoAmIApiResponseModel.getPayment_account_info() != null && whoAmIApiResponseModel.getPayment_account_info().isDefaultCardValid()) {
                                startActivity(new Intent(SubscriptionActivity.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();
                            } else {
                                if (isfirsttym) {
                                    startActivity(new Intent(SubscriptionActivity.this, PaymentContentActivity.class).putExtra(ArgumentKeys.IS_HEAD_LESS, true).putExtra(ArgumentKeys.IS_CLOSE_NEEDED, true).putExtra(ArgumentKeys.IS_CLOSE_NEEDED, false));
                                    isfirsttym = false;
                                }else {
                                    startActivity(new Intent(SubscriptionActivity.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                }
                            }
                        } else {
                            isfirsttym = true;
                            loadSubscriptionPlan();
                        }
                    }


                }
            }
        });

    }

    private void loadSubscriptionPlan() {
        SubscriptionPlanFragment subscriptionPlanFragment = new SubscriptionPlanFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ArgumentKeys.IS_HIDE_BACK, true);
        subscriptionPlanFragment.setArguments(bundle);
        showSubFragment(subscriptionPlanFragment);

    }

    private void showSubFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.fragment_remove_animation, R.anim.fragment_remove_exit)
                .addToBackStack(fragment.getClass().getSimpleName())
                .replace(R.id.sub_fragment_holder, fragment).commit();
    }

    @Override
    public void onShowFragment(Fragment fragment) {
        showSubFragment(fragment);
    }

    @Override
    public void onClose(boolean isRefreshRequired) {
        onBackPressed();
        whoAmIApiViewModel.assignWhoAmI();
    }
}
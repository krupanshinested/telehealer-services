package com.thealer.telehealer.views.home.monitoring.diet;

import android.app.Activity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.appbar.AppBarLayout;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.diet.food.FoodApiViewModel;
import com.thealer.telehealer.apilayer.models.diet.food.FoodListApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CameraInterface;
import com.thealer.telehealer.common.CameraUtil;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.Util.InternalLogging.TeleLogExternalAPI;
import com.thealer.telehealer.common.Util.InternalLogging.TeleLogger;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.common.SuccessViewInterface;

import java.util.HashMap;

/**
 * Created by Aswin on 21,February,2019
 */
public class SelectDietActivity extends BaseActivity implements ShowSubFragmentInterface, AttachObserverInterface, SuccessViewInterface {

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
    private ConstraintLayout fragmentHolder;
    private CustomRecyclerView foodCrv;
    private Button enterManuallyBtn;

    private FoodItemListAdapter foodItemListAdapter;
    private FoodApiViewModel foodApiViewModel;
    private FoodListApiResponseModel foodListApiResponseModel;

    private String selectedDate;
    private int session = 0;
    private boolean isApiRequested = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_diet_food);
        initViewModels();
        initView();
    }

    private void initViewModels() {
        foodApiViewModel = ViewModelProviders.of(this).get(FoodApiViewModel.class);
        attachObserver(foodApiViewModel);
        foodApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    foodListApiResponseModel = (FoodListApiResponseModel) baseApiResponseModel;

                    if (foodListApiResponseModel.getHints().size() > 0) {
                        foodItemListAdapter.setData(foodListApiResponseModel.getHints(), session, foodListApiResponseModel.getText());
                        foodCrv.showOrhideEmptyState(false);
                        enterManuallyBtn.setVisibility(View.GONE);
                    } else {
                        foodCrv.showOrhideEmptyState(true);
                        enterManuallyBtn.setVisibility(View.VISIBLE);
                    }
                    isApiRequested = false;
                    foodCrv.setScrollable(true);
                    foodCrv.hideProgressBar();
                }
            }
        });

        foodApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {

                HashMap<String, String> details = new HashMap<>();
                details.put("status", "fail");
                details.put("event", "getRecipeItems");
                String message;
                if (errorModel != null && TextUtils.isEmpty(errorModel.getMessage())) {
                    message = errorModel.getMessage();
                } else {
                    message = "unknown-reason";
                }
                details.put("reason", message);
                TeleLogger.shared.log(TeleLogExternalAPI.edamam, details);

            }
        });
    }

    private void initView() {
        appbarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backIv = (ImageView) findViewById(R.id.back_iv);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        searchLl = (LinearLayout) findViewById(R.id.search_ll);
        topView = (View) findViewById(R.id.top_view);
        searchCv = (CardView) findViewById(R.id.search_cv);
        searchEt = (EditText) findViewById(R.id.search_et);
        searchClearIv = (ImageView) findViewById(R.id.search_clear_iv);
        bottomView = (View) findViewById(R.id.bottom_view);
        fragmentHolder = (ConstraintLayout) findViewById(R.id.fragment_holder);
        foodCrv = (CustomRecyclerView) findViewById(R.id.food_crv);
        enterManuallyBtn = (Button) findViewById(R.id.enter_manually_btn);

        bottomView.setVisibility(View.VISIBLE);
        toolbarTitle.setText(getString(R.string.add_food));
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        searchEt.setHint(getString(R.string.search_food_hint));
        searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case KeyEvent.KEYCODE_ENDCALL:
                        session = 0;
                        foodListApiResponseModel = null;
                        searchFood(true);
                        break;
                }
                return false;
            }
        });

        foodCrv.setEmptyState(EmptyViewConstants.EMPTY_FOOD_ITEMS);
        foodItemListAdapter = new FoodItemListAdapter(this);
        foodCrv.getRecyclerView().setAdapter(foodItemListAdapter);

        LinearLayoutManager linearLayoutManager = foodCrv.getLayoutManager();

        foodCrv.getRecyclerView().setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (foodListApiResponseModel != null &&
                        foodListApiResponseModel.get_links() != null &&
                        foodListApiResponseModel.get_links().getNext() != null) {

                    if (linearLayoutManager.findLastVisibleItemPosition() == linearLayoutManager.getItemCount() - 1) {
                        searchFood(false);
                        foodCrv.setScrollable(false);
                        foodCrv.showProgressBar();
                    }
                } else
                    foodCrv.hideProgressBar();
            }
        });
        foodCrv.getSwipeLayout().setEnabled(false);

        selectedDate = getIntent().getStringExtra(ArgumentKeys.SELECTED_DATE);
        String mealType = getIntent().getStringExtra(ArgumentKeys.MEAL_TYPE);
        foodItemListAdapter.setSelectedDate(selectedDate, mealType);

        enterManuallyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(ArgumentKeys.SELECTED_DATE, selectedDate);
                bundle.putString(ArgumentKeys.MEAL_TYPE, mealType);
                bundle.putString(ArgumentKeys.SEARCHED_ITEM, searchEt.getText().toString());
                bundle.putBoolean(ArgumentKeys.IS_MANUAL_ENTRY, true);

                FoodDetailFragment foodDetailFragment = new FoodDetailFragment();
                foodDetailFragment.setArguments(bundle);

                onShowFragment(foodDetailFragment);

                enterManuallyBtn.setVisibility(View.GONE);
            }
        });
    }

    private void searchFood(boolean showProgress) {
        if (!isApiRequested) {
            isApiRequested = true;
            if (foodListApiResponseModel != null && foodListApiResponseModel.get_links() != null &&
                    foodListApiResponseModel.get_links().getNext() != null &&
                    foodListApiResponseModel.get_links().getNext().getHref() != null) {
                String ref = foodListApiResponseModel.get_links().getNext().getHref();
                Uri uri = Uri.parse(ref);
                session = Integer.parseInt(uri.getQueryParameter("session"));
            }
            foodApiViewModel.searchFood(searchEt.getText().toString(), session, showProgress);
        }
    }

    @Override
    public void onShowFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(fragmentHolder.getId(), fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager()
                .findFragmentById(fragmentHolder.getId());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

                if (getSupportFragmentManager().getFragments().get(0) instanceof CameraInterface) {
                    ((CameraInterface) getSupportFragmentManager().getFragments().get(0)).onImageReceived(imagePath);
                } else {
                    if (getCurrentFragment() instanceof CameraInterface) {
                        ((CameraInterface) getCurrentFragment()).onImageReceived(imagePath);
                    }
                }

            }
        }
    }

    @Override
    public void onSuccessViewCompletion(boolean success) {

    }
}

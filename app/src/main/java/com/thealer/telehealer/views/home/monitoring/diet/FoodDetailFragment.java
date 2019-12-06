package com.thealer.telehealer.views.home.monitoring.diet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ImageViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.diet.AddDietRequestModel;
import com.thealer.telehealer.apilayer.models.diet.DietApiResponseModel;
import com.thealer.telehealer.apilayer.models.diet.DietApiViewModel;
import com.thealer.telehealer.apilayer.models.diet.food.FoodApiViewModel;
import com.thealer.telehealer.apilayer.models.diet.food.FoodDetailApiResponseModel;
import com.thealer.telehealer.apilayer.models.diet.food.FoodListApiResponseModel;
import com.thealer.telehealer.apilayer.models.diet.food.NutrientsDetailBean;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CameraInterface;
import com.thealer.telehealer.common.CameraUtil;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.Util.InternalLogging.TeleLogExternalAPI;
import com.thealer.telehealer.common.Util.InternalLogging.TeleLogger;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aswin on 21,February,2019
 */
public class FoodDetailFragment extends BaseFragment implements View.OnClickListener, CameraInterface {
    private ImageView foodIv;
    private TextView foodNameTv;
    private TextView addPhotoTv;
    private Spinner spinner;
    private ImageView subIv;
    private TextView countTv;
    private ImageView addIv;
    private Button addBtn;
    private TextView noOfServesTv;
    private TabLayout tabLayout;
    private RecyclerView nutritionFactRv;
    private TextView ingrediantsTv;

    private String measureUri, searchedItem, selectedDate, mealType, uploadImage;
    private int count = 1;
    private boolean isManualEntry, isDeleteMode;

    private FoodApiViewModel foodApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private FoodListApiResponseModel.HintsBean hintsBean;
    private DietApiViewModel dietApiViewModel;
    private NutritionFactsAdapter nutritionFactsAdapter;
    private AddDietRequestModel addDietRequestModel;
    private NestedScrollView foodDetailNsv;
    private FoodDetailApiResponseModel foodDetailApiResponseModel;
    private View bottomView1;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private ConstraintLayout deleteBtn;
    private TextView unitValueTv;
    private TextView unitsCountTv;
    private LinearLayout unitCountLl;
    private DietApiResponseModel dietApiResponseModel;
    private CircleImageView foodAvatarCiv;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachObserverInterface = (AttachObserverInterface) getActivity();
        foodApiViewModel = new ViewModelProvider(this).get(FoodApiViewModel.class);
        dietApiViewModel = new ViewModelProvider(this).get(DietApiViewModel.class);
        attachObserverInterface.attachObserver(foodApiViewModel);
        attachObserverInterface.attachObserver(dietApiViewModel);
        foodApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    foodDetailApiResponseModel = (FoodDetailApiResponseModel) baseApiResponseModel;
                    if (foodDetailApiResponseModel.getTotalNutrients() != null && !foodDetailApiResponseModel.getTotalNutrients().isEmpty())
                        nutritionFactsAdapter.setData(foodDetailApiResponseModel.getTotalNutrients());
                }
            }
        });
        dietApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel.isSuccess()) {
                        sendSuccessViewBroadCast(getActivity(), true, getString(R.string.success), (isDeleteMode) ? getString(R.string.diet_delete_success) : getString(R.string.diet_post_success));
                    }
                }
            }
        });

        dietApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    sendSuccessViewBroadCast(getActivity(), false, getString(R.string.failure), isDeleteMode ? getString(R.string.diet_delete_failure) : getString(R.string.diet_post_failure));
                }
            }
        });

        foodApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {

                HashMap<String, String> details = new HashMap<>();
                details.put("status", "fail");
                details.put("event", "getFoodItems");
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_detail, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        foodIv = (ImageView) view.findViewById(R.id.food_iv);
        foodNameTv = (TextView) view.findViewById(R.id.food_name_tv);
        addPhotoTv = (TextView) view.findViewById(R.id.add_photo_tv);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        subIv = (ImageView) view.findViewById(R.id.sub_iv);
        countTv = (TextView) view.findViewById(R.id.count_tv);
        addIv = (ImageView) view.findViewById(R.id.add_iv);
        addBtn = (Button) view.findViewById(R.id.add_btn);
        noOfServesTv = (TextView) view.findViewById(R.id.no_of_serves_tv);
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        nutritionFactRv = (RecyclerView) view.findViewById(R.id.nutrition_fact_rv);
        ingrediantsTv = (TextView) view.findViewById(R.id.ingrediants_tv);
        foodDetailNsv = (NestedScrollView) view.findViewById(R.id.food_detail_nsv);
        bottomView1 = (View) view.findViewById(R.id.view_1);
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        deleteBtn = (ConstraintLayout) view.findViewById(R.id.delete_btn);
        unitValueTv = (TextView) view.findViewById(R.id.unit_value_tv);
        unitsCountTv = (TextView) view.findViewById(R.id.units_count_tv);
        unitCountLl = (LinearLayout) view.findViewById(R.id.unit_count_ll);
        foodAvatarCiv = (CircleImageView) view.findViewById(R.id.food_avatar_civ);

        setNutritionFacts();

        addPhotoTv.setOnClickListener(this);
        addBtn.setOnClickListener(this);
        addIv.setOnClickListener(this);
        subIv.setOnClickListener(this);


        nutritionFactsAdapter = new NutritionFactsAdapter(getActivity());
        nutritionFactRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        nutritionFactRv.setAdapter(nutritionFactsAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0:
                        ingrediantsTv.setVisibility(View.INVISIBLE);
                        nutritionFactRv.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        nutritionFactRv.setVisibility(View.INVISIBLE);
                        ingrediantsTv.setVisibility(View.VISIBLE);
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (getArguments() != null) {
            isDeleteMode = getArguments().getBoolean(ArgumentKeys.FOOD_DELETE_MODE, false);
            if (!isDeleteMode) {
                hintsBean = (FoodListApiResponseModel.HintsBean) getArguments().getSerializable(ArgumentKeys.FOOD_DETAIL);
                searchedItem = getArguments().getString(ArgumentKeys.SEARCHED_ITEM);
                selectedDate = getArguments().getString(ArgumentKeys.SELECTED_DATE);
                mealType = getArguments().getString(ArgumentKeys.MEAL_TYPE);
                isManualEntry = getArguments().getBoolean(ArgumentKeys.IS_MANUAL_ENTRY, false);

                if (!isManualEntry) {
                    setFoodDetail();
                    setUnits();

                    if (hintsBean.getMeasures().size() > 0)
                        measureUri = hintsBean.getMeasures().get(0).getUri();

                    getNutrientsDetail();

                } else {
                    tabLayout.setVisibility(View.GONE);
                    foodDetailNsv.setVisibility(View.GONE);

                    foodNameTv.setText(searchedItem);
                    foodIv.setImageDrawable(getActivity().getDrawable(R.drawable.diet_food_placeholder));

                    List<String> unitList = new ArrayList<>();
                    unitList.add(getString(R.string.servings));

                    setUnitsAdapter(unitList);

                }
            } else {

                foodIv.setVisibility(View.GONE);
                foodNameTv.setVisibility(View.GONE);
                addPhotoTv.setVisibility(View.GONE);
                bottomView1.setVisibility(View.GONE);
                addBtn.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                unitCountLl.setVisibility(View.GONE);

                appbarLayout.setVisibility(View.VISIBLE);
                deleteBtn.setVisibility(View.VISIBLE);
                unitsCountTv.setVisibility(View.VISIBLE);
                unitValueTv.setVisibility(View.VISIBLE);

                backIv.setOnClickListener(this);
                deleteBtn.setOnClickListener(this);

                dietApiResponseModel = (DietApiResponseModel) getArguments().getSerializable(ArgumentKeys.FOOD_DETAIL);
                if (dietApiResponseModel != null) {
                    unitValueTv.setText(dietApiResponseModel.getServing_unit());
                    unitsCountTv.setText(String.valueOf(dietApiResponseModel.getServing()));
                    toolbarTitle.setText(dietApiResponseModel.getFood().getName());

                    if (dietApiResponseModel.getDisplayFoodImage() != null) {
                        Utils.setImageWithGlide(getContext(), foodAvatarCiv, dietApiResponseModel.getDisplayFoodImage(), getActivity().getDrawable(R.drawable.diet_food_placeholder), false, false);
                    }else {
                        foodAvatarCiv.setVisibility(View.GONE);
                    }

                    if (dietApiResponseModel.getFood().getIngredients() != null) {
                        setIngredients(dietApiResponseModel.getFood().getIngredients());
                    } else {
                        setIngredients(null);
                    }

                    nutritionFactsAdapter.setData(dietApiResponseModel.getFood().getTotalNutrients());
                }
            }
        }
    }

    private void setFoodDetail() {
        setFoodImage(hintsBean.getFood().getImage());
        foodNameTv.setText(hintsBean.getFood().getLabel());

        if (hintsBean.getFood().getFoodContentsLabel() != null)
            setIngredients(hintsBean.getFood().getFoodContentsLabel());
        else
            setIngredients(null);
    }

    private void setIngredients(String ingredients) {
        if (ingredients != null) {
            ingrediantsTv.setText(ingredients.replace(";", "\n\n"));
        } else {
            ingrediantsTv.setText(getString(R.string.no_ingredients_present));
        }
    }

    private void setUnits() {
        List<String> unitList = new ArrayList<>();

        if (hintsBean.getMeasures().size() > 0) {
            for (int i = 0; i < hintsBean.getMeasures().size(); i++) {
                if (hintsBean.getMeasures().get(i).getLabel() != null)
                    unitList.add(hintsBean.getMeasures().get(i).getLabel());
            }
        }

        setUnitsAdapter(unitList);
    }

    private void setUnitsAdapter(List<String> unitList) {
        ArrayAdapter spinnerAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, unitList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isManualEntry && !hintsBean.getMeasures().isEmpty()) {
                    measureUri = hintsBean.getMeasures().get(position).getUri();
                    getNutrientsDetail();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getNutrientsDetail() {
        foodApiViewModel.getNutrientsDetail(hintsBean.getFood().getFoodId(), measureUri, 1, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_photo_tv:
                CameraUtil.showImageSelectionAlert(getActivity());
                break;
            case R.id.add_btn:
                addFood();
                break;
            case R.id.add_iv:
                modifyCount(true);
                break;
            case R.id.sub_iv:
                modifyCount(false);
                break;
            case R.id.back_iv:
                onBackPressed();
                break;
            case R.id.delete_btn:
                Bundle bundle = new Bundle();
                bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.please_wait));
                bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.deleting_your_diet));
                showSuccessView(this, RequestID.REQ_SHOW_SUCCESS_VIEW, bundle);

                dietApiViewModel.deleteDiet(dietApiResponseModel.getUser_diet_id());
                break;
        }
    }

    private void onBackPressed() {
        if (getActivity() instanceof OnCloseActionInterface)
            ((OnCloseActionInterface) getActivity()).onClose(false);
    }

    private void addFood() {
        showSuccessView(this, RequestID.REQ_SHOW_SUCCESS_VIEW, null);
        generateRequestModel();
        dietApiViewModel.addDiet(addDietRequestModel, true);
    }

    private void generateRequestModel() {
        addDietRequestModel = new AddDietRequestModel();
        addDietRequestModel.setDate(selectedDate);
        addDietRequestModel.setServing(String.valueOf(count));
        addDietRequestModel.setServing_unit(String.valueOf(spinner.getSelectedItem()));
        addDietRequestModel.setMeal_type(mealType);
        addDietRequestModel.setUploadUrl(uploadImage);

        DietApiResponseModel.FoodBean foodBean = new DietApiResponseModel.FoodBean();
        if (!isManualEntry) {
            foodBean.setName(hintsBean.getFood().getLabel());
            foodBean.setImage_url(hintsBean.getFood().getImage());

            Map<String, NutrientsDetailBean> nutrientsDetailBeanMap = foodDetailApiResponseModel.getTotalNutrients();

            Set<String> keySet = nutrientsDetailBeanMap.keySet();

            for (String key : keySet) {
                if (nutrientsDetailBeanMap.get(key) != null) {
                    double quantity = nutrientsDetailBeanMap.get(key).getQuantity() * count;
                    nutrientsDetailBeanMap.get(key).setQuantity(quantity);
                }
            }

            foodBean.setTotalNutrients(nutrientsDetailBeanMap);

            foodBean.setIngredients(hintsBean.getFood().getFoodContentsLabel());
        } else {
            foodBean.setName(searchedItem);
        }
        addDietRequestModel.setFoodBean(foodBean);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestID.REQ_SHOW_SUCCESS_VIEW && resultCode == Activity.RESULT_OK) {
            if (!isDeleteMode) {
                getActivity().setResult(resultCode);
                getActivity().finish();
            } else {
                onBackPressed();
            }
        }
    }

    private void modifyCount(boolean add) {
        count = Integer.parseInt(countTv.getText().toString());

        if (add) {
            count = count + 1;
        } else {
            if (count > 1) {
                count = count - 1;
            }
        }

        if (count > 1) {
            subIv.setClickable(true);
            ImageViewCompat.setImageTintList(subIv, ColorStateList.valueOf(getActivity().getColor(R.color.app_gradient_start)));
        } else {
            subIv.setClickable(false);
            ImageViewCompat.setImageTintList(subIv, ColorStateList.valueOf(getActivity().getColor(R.color.colorGrey)));
        }

        if (!isManualEntry)
            nutritionFactsAdapter.setCount(count);

        setNutritionFacts();
    }

    private void setNutritionFacts() {
        countTv.setText(String.valueOf(count));

    }

    private void setFoodImage(String url) {
        Utils.setImageWithGlide(getActivity().getApplicationContext(), foodIv, url, getActivity().getDrawable(R.drawable.diet_food_placeholder), false, true);
    }

    @Override
    public void onImageReceived(String imagePath) {
        foodIv.setImageBitmap(getBitmpaFromPath(imagePath));
        uploadImage = imagePath;
    }
}

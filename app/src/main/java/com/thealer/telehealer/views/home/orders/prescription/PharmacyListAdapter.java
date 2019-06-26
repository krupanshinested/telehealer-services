package com.thealer.telehealer.views.home.orders.prescription;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.orders.pharmacy.GetPharmaciesApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;

import java.util.List;

/**
 * Created by Aswin on 30,November,2018
 */
public class PharmacyListAdapter extends RecyclerView.Adapter<PharmacyListAdapter.ViewHolder> {

    private FragmentActivity activity;
    private List<GetPharmaciesApiResponseModel.ResultsBean> results;
    private OnListItemSelectInterface onListItemSelectInterface;
    private int selectedItemPosition = -1;

    public PharmacyListAdapter(FragmentActivity activity, OnListItemSelectInterface onListItemSelectInterface) {
        this.activity = activity;
        this.onListItemSelectInterface = onListItemSelectInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_pharmacy_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.pharmacyNameTv.setText(results.get(i).getContact_name());
        viewHolder.pharmacyAddressTv.setText(results.get(i).getAddr1().concat(",").concat(results.get(i).getCity()).concat(",").concat(results.get(i).getState()));

        if (i == selectedItemPosition) {
            viewHolder.selectedIv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.selectedIv.setVisibility(View.GONE);
        }
        viewHolder.itemCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedItemPosition = i;
                notifyDataSetChanged();
                Bundle bundle = new Bundle();
                bundle.putSerializable(ArgumentKeys.SELECTED_MENU_ITEM, results.get(i));
                onListItemSelectInterface.onListItemSelected(i, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (results != null) {
            return results.size();
        } else {
            return 0;
        }
    }

    public void removeSelected() {
        selectedItemPosition = -1;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView pharmacyNameTv;
        private TextView pharmacyAddressTv;
        private CardView itemCv;
        private ImageView selectedIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pharmacyNameTv = (TextView) itemView.findViewById(R.id.pharmacy_name_tv);
            pharmacyAddressTv = (TextView) itemView.findViewById(R.id.pharmacy_address_tv);
            itemCv = (CardView) itemView.findViewById(R.id.item_cv);
            selectedIv = (ImageView) itemView.findViewById(R.id.selected_iv);
        }
    }

    public void setResults(List<GetPharmaciesApiResponseModel.ResultsBean> results, int nextPage) {
        if (nextPage == 1) {
            this.results = results;
        } else {
            this.results.addAll(results);
        }
        notifyDataSetChanged();
    }
}

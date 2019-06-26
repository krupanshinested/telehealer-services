package Flavor.GoogleFit.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.thealer.telehealer.R;

import java.util.ArrayList;


import Flavor.GoogleFit.Models.GoogleFitSource;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class GoogleFitSourceAdapter extends RecyclerView.Adapter<GoogleFitSourceAdapter.DataHolder> {

    ArrayList<GoogleFitSource> sources = new ArrayList<>();
    private Context context;

    public GoogleFitSourceAdapter(ArrayList<GoogleFitSource> sources, Context context) {
        this.sources = sources;
        this.context = context;
    }

    @NonNull
    @Override
    public GoogleFitSourceAdapter.DataHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View dataView = LayoutInflater.from(context).inflate(R.layout.layout_settings_view, viewGroup, false);
        return new GoogleFitSourceAdapter.DataHolder(dataView);
    }

    @Override
    public void onBindViewHolder(@NonNull GoogleFitSourceAdapter.DataHolder dataHolder, int position) {
        GoogleFitSource source = sources.get(position);
        dataHolder.title.setText(source.getAppName());
        dataHolder.aSwitch.setChecked(source.isSelected());

        dataHolder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                source.setSelected(isChecked);
            }
        });

    }

    @Override
    public int getItemCount() {
        return sources.size();
    }

    class DataHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private Switch aSwitch;

        private DataHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            aSwitch = itemView.findViewById(R.id.settings_switch);
        }
    }

}

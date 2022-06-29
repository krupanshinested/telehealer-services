package com.thealer.telehealer.views.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.transaction.req.TransactionListReq;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.transaction.DateRangeView;

import java.util.Calendar;

public class DateFilterActivity extends BaseActivity implements View.OnClickListener {

    private Button btnSubmit;
    private Button btnReset;
    private DateRangeView dateFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_filter);

        initview();

    }

    private void initview() {

        dateFilter = findViewById(R.id.dateFilter);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnReset = findViewById(R.id.btnReset);

        btnSubmit.setOnClickListener(this);
        btnReset.setOnClickListener(this);

        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.filter));

        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit: {
                if (dateFilter.getSelectedToDate() != null) {
                    if (dateFilter.getSelectedFromDate().getTimeInMillis() > dateFilter.getSelectedToDate().getTimeInMillis()) {
                        Utils.showAlertDialog(this, getString(R.string.app_name), getString(R.string.msg_please_select_valid_date_range_for_any, getString(R.string.filter)), getString(R.string.ok), null, null, null);
                        return;
                    }
                }

                Log.d("TAG", "onClick: "+dateFilter.getSelectedFromDate().toString());

                String start = "" ,end = "";

                if (dateFilter.getSelectedFromDate() != null) {
                    Calendar calenderFROM=dateFilter.getSelectedFromDate();
                    calenderFROM.set(Calendar.HOUR_OF_DAY,0);
                    calenderFROM.set(Calendar.MINUTE,0);
                    calenderFROM.set(Calendar.SECOND,0);
                    start = Utils.getDateFromCalendar(calenderFROM);
                }
                if (dateFilter.getSelectedToDate() != null) {
                    Calendar calenderTO=dateFilter.getSelectedToDate();
                    calenderTO.set(Calendar.HOUR_OF_DAY,23);
                    calenderTO.set(Calendar.MINUTE,59);
                    calenderTO.set(Calendar.SECOND,59);
                    end = Utils.getDateFromCalendar(calenderTO);
                }

                setResult(RESULT_OK, new Intent().putExtra("START_DATE", start).putExtra("END_DATE",end));
                finish();
                break;
            }
            case R.id.btnReset: {

                Calendar calenderTO = Calendar.getInstance();
                calenderTO.set(Calendar.HOUR_OF_DAY, 23);
                calenderTO.set(Calendar.MINUTE, 59);
                calenderTO.set(Calendar.SECOND, 59);
                dateFilter.setSelectedToDate(calenderTO);

                Calendar calenderFROM = Calendar.getInstance();
                calenderFROM.set(Calendar.HOUR_OF_DAY, 0);
                calenderFROM.set(Calendar.MINUTE, 0);
                calenderFROM.set(Calendar.SECOND, 0);
                calenderFROM.add(Calendar.MONTH, -1);
                dateFilter.setSelectedFromDate(calenderFROM);


                TransactionListReq req = new TransactionListReq();
                req.setFilter(new TransactionListReq.Filter());
                req.getFilter().setFromDate(Utils.getDateFromCalendar(dateFilter.getSelectedFromDate()));
                req.getFilter().setToDate(Utils.getDateFromCalendar(dateFilter.getSelectedToDate()));

                setResult(RESULT_CANCELED, new Intent());
                finish();
            }
        }

    }
}
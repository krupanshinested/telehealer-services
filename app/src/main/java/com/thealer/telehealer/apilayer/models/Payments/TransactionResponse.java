package com.thealer.telehealer.apilayer.models.Payments;

import android.util.Log;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by rsekar on 1/22/19.
 */

public class TransactionResponse extends BaseApiResponseModel implements Serializable {

    private ArrayList<Transaction> result;

    public ArrayList<Transaction> getResult() {
        return result;
    }


    public ArrayList<Transaction> mergeTransactions() {
        if (result == null) {
            return new ArrayList<>();
        }

        HashMap<String,Transaction> modifiedTransactions = new HashMap<>();

        for (int i = 0; i< result.size();i++) {

            Transaction transaction = result.get(i);

            String monthYear = transaction.getCreatedMonthYear();
            Transaction alreadyPresent = modifiedTransactions.get(monthYear);
            if (alreadyPresent != null) {

                Double oldAmount = 0.0;
                Double newAmount = 0.0;
                try {
                    oldAmount = Double.parseDouble(alreadyPresent.getAmount());
                    newAmount = Double.parseDouble(transaction.getAmount());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                alreadyPresent.setAmount((oldAmount+newAmount)+"");
                alreadyPresent.setId(alreadyPresent.getId()+","+transaction.getId());

            } else {
                modifiedTransactions.put(monthYear,transaction);
            }

        }

        return new ArrayList<Transaction>(modifiedTransactions.values());
    }
}

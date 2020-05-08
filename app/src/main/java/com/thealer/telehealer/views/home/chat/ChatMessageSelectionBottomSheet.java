package com.thealer.telehealer.views.home.chat;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Util.TimerInterface;
import com.thealer.telehealer.common.Util.TimerRunnable;
import com.thealer.telehealer.views.base.BaseBottomSheetDialogFragment;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 16,May,2019
 */
public class ChatMessageSelectionBottomSheet extends BaseBottomSheetDialogFragment {
    private TextView cancelTv;
    private TextView doneTv;
    private TextView hintTV;
    private RecyclerView chooseMessageRv;

    private ChooseMessageListAdapter chooseMessageListAdapter;
    private List<String> messageList = new ArrayList<>();
    private OnActionCompleteInterface onActionCompleteInterface;
    private String selectedItem;
    private View topView;
    private CardView searchCv;
    private EditText searchEt;
    private ImageView searchClearIv;
    private View bottomView;

    @Nullable
    private TimerRunnable uiToggleTimer;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onActionCompleteInterface = (OnActionCompleteInterface) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.cloneInContext(contextThemeWrapper).inflate(R.layout.bottomsheet_chat_messages, container, false);
        setBottomSheetHeight(view, 80);
        initView(view);
        return view;
    }

    private void initView(View view) {
        cancelTv = (TextView) view.findViewById(R.id.cancel_tv);
        doneTv = (TextView) view.findViewById(R.id.done_tv);
        hintTV = (TextView) view.findViewById(R.id.hint_tv);
        chooseMessageRv = (RecyclerView) view.findViewById(R.id.choose_message_rv);
        searchEt = (EditText) view.findViewById(R.id.search_et);
        searchClearIv = (ImageView) view.findViewById(R.id.search_clear_iv);
        bottomView = (View) view.findViewById(R.id.bottom_view);
        bottomView.setVisibility(View.VISIBLE);

        hintTV.setText(R.string.precanned_hint_text);
        searchEt.setHint(getString(R.string.search_messages));
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    searchClearIv.setVisibility(View.GONE);
                    chooseMessageListAdapter.setData(messageList);
                } else {
                    searchClearIv.setVisibility(View.VISIBLE);
                    List<String> searchedList = new ArrayList<>();
                    if (uiToggleTimer != null) {
                        uiToggleTimer.setStopped(true);
                        uiToggleTimer = null;
                    }

                    Handler handler = new Handler();
                    TimerRunnable runnable = new TimerRunnable(new TimerInterface() {
                        @Override
                        public void run() {
                            for (int i = 0; i < messageList.size(); i++) {
                                if (messageList.get(i).toLowerCase().contains(searchEt.toString().toLowerCase())) {
                                    searchedList.add(messageList.get(i));
                                }
                            }
                        }
                    });
                    uiToggleTimer = runnable;
                    handler.postDelayed(runnable, ArgumentKeys.SEARCH_INTERVAL);
                    chooseMessageListAdapter.setData(searchedList);
                }
            }
        });

        searchClearIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEt.setText("");
            }
        });

        if (getArguments() != null) {
            messageList = getArguments().getStringArrayList(ArgumentKeys.PRECANNED_MESSAGES);
        }

        chooseMessageListAdapter = new ChooseMessageListAdapter(getActivity(), messageList, new OnListItemSelectInterface() {
            @Override
            public void onListItemSelected(int position, Bundle bundle) {
                doneTv.setEnabled(true);
                selectedItem = bundle.getString(ArgumentKeys.SELECTED_MESSAGE);
            }
        });
        chooseMessageRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        chooseMessageRv.setAdapter(chooseMessageListAdapter);

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        doneTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.SELECTED_ITEM, selectedItem);

                onActionCompleteInterface.onCompletionResult(null, true, bundle);
            }
        });

    }
}

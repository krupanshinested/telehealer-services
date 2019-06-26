package Flavor.iHealth.pairing;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CommonInterface.ToolBarInterface;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.VitalCommon.VitalDeviceType;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalManagerInstance;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

/**
 * Created by rsekar on 11/29/18.
 */

public class VitalDemoVideoFragment extends BaseFragment implements View.OnClickListener {

    private TextView titleView;
    private ImageView videoView,playButton;
    private CustomButton nextButton;

    private OnViewChangeInterface onViewChangeInterface;
    private OnActionCompleteInterface onActionCompleteInterface;
    private ToolBarInterface toolBarInterface;
    private VitalManagerInstance vitalManagerInstance;

    @NonNull
    private String deviceType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vital_demo_video, container, false);

        deviceType  = getArguments().getString(ArgumentKeys.DEVICE_TYPE);

        initView(view);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        toolBarInterface.updateTitle(getString(VitalDeviceType.shared.getTitle(deviceType)));

        onViewChangeInterface.hideOrShowClose(true);
        onViewChangeInterface.hideOrShowBackIv(true);

        toolBarInterface.updateSubTitle("",View.GONE);
        vitalManagerInstance.updateBatteryView(View.GONE,0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
        toolBarInterface = (ToolBarInterface) getActivity();
        vitalManagerInstance = (VitalManagerInstance) getActivity();
    }

    private void initView(View baseView) {
        titleView = baseView.findViewById(R.id.title_tv);
        videoView = baseView.findViewById(R.id.video_view);
        playButton = baseView.findViewById(R.id.play_button);
        nextButton = baseView.findViewById(R.id.next_bt);

        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        titleView.setText(getString(R.string.vital_tip_1, getString(VitalDeviceType.shared.getTitle(deviceType))));
        Glide.with(getActivity())
                .load("https://img.youtube.com/vi/"+VitalDeviceType.shared.getVideoId(deviceType)+"/0.jpg")
                .apply(new RequestOptions())
                .into(videoView);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play_button:
                String videoId = VitalDeviceType.shared.getVideoId(deviceType);
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + videoId));
                try {
                    startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(webIntent);
                }
                break;
            case R.id.next_bt:
                onActionCompleteInterface.onCompletionResult(RequestID.TRIGGER_DEVICE_CONNECTION,true,getArguments());
                break;
        }
    }

}

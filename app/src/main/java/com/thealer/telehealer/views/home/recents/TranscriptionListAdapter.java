package com.thealer.telehealer.views.home.recents;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.recents.DownloadTranscriptResponseModel;

/**
 * Created by Aswin on 26,December,2018
 */
class TranscriptionListAdapter extends RecyclerView.Adapter<TranscriptionListAdapter.ViewHolder> {
    private FragmentActivity activity;
    private DownloadTranscriptResponseModel transcriptResponseModel;

    private int position = -1;

    public TranscriptionListAdapter(FragmentActivity activity, DownloadTranscriptResponseModel downloadTranscriptResponseModel) {
        this.activity = activity;
        this.transcriptResponseModel = downloadTranscriptResponseModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_transcription_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String speaker = transcriptResponseModel.getSpeakerLabels().get(i).getSpeaker_label().replace("spk_", "");
        int person = Integer.parseInt(speaker) + 1;
        viewHolder.speakerTv.setText("Speaker " + person + ":");
        viewHolder.transcriptTv.setText(transcriptResponseModel.getSpeakerLabels().get(i).getTranscript());

        if (i == position) {
            viewHolder.speakerTv.setTextColor(ColorStateList.valueOf(activity.getColor(R.color.app_gradient_start)));
            viewHolder.transcriptTv.setTextColor(ColorStateList.valueOf(activity.getColor(R.color.app_gradient_start)));
        } else {
            viewHolder.speakerTv.setTextColor(ColorStateList.valueOf(activity.getColor(R.color.colorBlack)));
            viewHolder.transcriptTv.setTextColor(ColorStateList.valueOf(activity.getColor(R.color.colorBlack)));
        }
    }

    @Override
    public int getItemCount() {
        return transcriptResponseModel.getSpeakerLabels().size();
    }

    public void setCurrentTranscription(int position) {
        this.position = position;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView speakerTv;
        private TextView transcriptTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            speakerTv = (TextView) itemView.findViewById(R.id.speaker_tv);
            transcriptTv = (TextView) itemView.findViewById(R.id.transcript_tv);
        }
    }
}

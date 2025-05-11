package com.tudio.scregproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class NotificationCombinedAdapter extends RecyclerView.Adapter<NotificationCombinedAdapter.ViewHolder> {

    private Context context;
    private List<NotificationItem> campaignList;

    public NotificationCombinedAdapter(Context context, List<NotificationItem> campaignList) {
        this.context = context;
        this.campaignList = campaignList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification_combined, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem campaign = campaignList.get(position);

        holder.textTitle.setText(campaign.getTitle());
        holder.textBody.setText(campaign.getBody());

        if (campaign.getImageUrl() != null && !campaign.getImageUrl().isEmpty()) {
            holder.imageCampaign.setVisibility(View.VISIBLE);
            Glide.with(context).load(campaign.getImageUrl()).fitCenter().into(holder.imageCampaign);
        } else {
            holder.imageCampaign.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return campaignList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textBody;
        ImageView imageCampaign;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textViewNotificationTitle);
            textBody = itemView.findViewById(R.id.textViewNotificationBody);
            imageCampaign = itemView.findViewById(R.id.imageCampaignNotification);
        }
    }
}

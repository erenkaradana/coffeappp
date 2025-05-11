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

public class CampaignAdapter extends RecyclerView.Adapter<CampaignAdapter.CampaignViewHolder> {

    private Context context;
    private List<Campaign> campaignList;

    public CampaignAdapter(Context context, List<Campaign> campaignList) {
        this.context = context;
        this.campaignList = campaignList;
    }

    @NonNull
    @Override
    public CampaignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_campaign, parent, false);
        return new CampaignViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CampaignViewHolder holder, int position) {
        Campaign campaign = campaignList.get(position);
        holder.textTitle.setText(campaign.getTitle());
        holder.textDescription.setText(campaign.getDescription());

        if (campaign.getImageUrl() != null && !campaign.getImageUrl().isEmpty()) {
            Glide.with(context).load(campaign.getImageUrl()).into(holder.imageCampaign);
        } else {
            holder.imageCampaign.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return campaignList.size();
    }

    static class CampaignViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textDescription;
        ImageView imageCampaign;

        public CampaignViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textCampaignTitle);
            textDescription = itemView.findViewById(R.id.textCampaignDescription);
            imageCampaign = itemView.findViewById(R.id.imageCampaign);
        }
    }
}

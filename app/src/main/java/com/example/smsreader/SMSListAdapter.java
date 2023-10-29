package com.example.smsreader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SMSListAdapter extends RecyclerView.Adapter<SMSListAdapter.ViewHolder> {
    private List<SMS> smsList;

    public SMSListAdapter(List<SMS> userList) {
        this.smsList = userList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sms_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SMS sms = smsList.get(position);
        holder.textViewAddress.setText(sms.getAddress());
        holder.textViewName.setText(sms.getName());
        holder.textViewAmount.setText(sms.getAmount());
        holder.textViewDate.setText(sms.getDate());
        holder.textViewTime.setText(sms.getTime());
        holder.textViewType.setText(sms.getCategory());
    }

    @Override
    public int getItemCount() {
        return smsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewAddress;
        public TextView textViewName;
        public TextView textViewAmount;
        public TextView textViewDate;
        public TextView textViewTime;
        public TextView textViewType;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewAddress = itemView.findViewById(R.id.text_view_address);
            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewAmount = itemView.findViewById(R.id.text_view_amount);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            textViewTime = itemView.findViewById(R.id.text_view_time);
            textViewType = itemView.findViewById(R.id.text_view_category);
        }
    }
}
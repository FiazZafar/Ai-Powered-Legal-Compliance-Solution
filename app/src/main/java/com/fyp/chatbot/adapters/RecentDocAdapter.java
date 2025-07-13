package com.fyp.chatbot.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.chatbot.R;
import com.fyp.chatbot.models.Docoments;

import java.util.List;

public class RecentDocAdapter extends RecyclerView.Adapter<RecentDocAdapter.ViewHolder> {

    List<Docoments> docomentsList;

    public RecentDocAdapter(List<Docoments> docoments){
        this.docomentsList = docoments;
    }
    @NonNull
    @Override
    public RecentDocAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_document,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentDocAdapter.ViewHolder holder, int position) {

        Docoments doc = docomentsList.get(position);

        holder.itemNameTxt.setText(doc.getDocName());
        holder.itemDateTxt.setText(doc.getAnalyzedTime());

    }

    @Override
    public int getItemCount() {
        return docomentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTxt,itemDateTxt;
        ViewHolder(View itemView){
            super(itemView);

            itemNameTxt = itemView.findViewById(R.id.txtDocName);
            itemDateTxt = itemView.findViewById(R.id.txtDocDate);
        }

    }
}

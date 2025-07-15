package com.fyp.chatbot.adapters;

import static android.app.PendingIntent.getActivity;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.chatbot.R;
import com.fyp.chatbot.models.ClauseModel;
import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.List;

public class ClauseHistoryAdapter extends RecyclerView.Adapter<ClauseHistoryAdapter.ViewHolder> {
    List<ClauseModel> list ;
    public ClauseHistoryAdapter(List<ClauseModel> list){
        this.list = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.clause_history_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClauseModel clauseModel = list.get(position);
        holder.clauseTitle.setText(clauseModel.getClauseTitle());
        holder.clauseBody.setText(clauseModel.getClauseBody());

        String dates = DateFormat.format("d-MM-yyyy",clauseModel.getTimeStamp()).toString();
        String todayDate = DateFormat.format("d-MM-yyyy",System.currentTimeMillis()).toString();
        if (!dates.equals(todayDate))
            holder.clauseTime.setText(dates);
        else
            holder.clauseTime.setText("Today");


        holder.copyBtn.setOnClickListener(view -> {
            if (clauseModel.getClauseBody() != null){
                ClipboardManager clipboardManager = (ClipboardManager)view.getContext()
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data = ClipData.newPlainText("Clause",clauseModel.getClauseTitle() + ": \n"+clauseModel.getClauseBody() );
                clipboardManager.setPrimaryClip(data);
                holder.copyBtn.setEnabled(false);
                Toast.makeText(view.getContext(), "Clause copied to clipboard", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(view.getContext(), "Nothing to copy", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(List<ClauseModel> onClauseList) {
        this.list.clear();
        this.list.addAll(onClauseList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView clauseTitle,clauseBody,clauseTime;
        ImageButton copyBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            clauseTitle = itemView.findViewById(R.id.clauseTitle);
            clauseBody = itemView.findViewById(R.id.clauseBody);
            clauseTime = itemView.findViewById(R.id.clauseTimeStamp);
            copyBtn = itemView.findViewById(R.id.clauseCopyBtn);
        }
    }
}

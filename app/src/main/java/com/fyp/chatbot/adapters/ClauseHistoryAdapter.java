package com.fyp.chatbot.adapters;



import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.chatbot.R;
import com.fyp.chatbot.databinding.ClauseHistoryListBinding;
import com.fyp.chatbot.models.ClauseModel;
import java.util.List;

public class ClauseHistoryAdapter extends RecyclerView.Adapter<ClauseHistoryAdapter.ViewHolder> {
    List<ClauseModel> list ;
    Context context;
    public ClauseHistoryAdapter(List<ClauseModel> list,Context context){
        this.list = list;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ClauseHistoryListBinding binding = ClauseHistoryListBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClauseModel clauseModel = list.get(position);
        holder.setBinding(clauseModel);

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
        ClauseHistoryListBinding binding;
        ViewHolder(ClauseHistoryListBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
        void setBinding(ClauseModel clauseModel){

            binding.clauseCopyBtn.setEnabled(true);
            String dates = DateFormat.format("d-MMM-yyyy",  clauseModel.getTimeStamp()).toString();
            String todayDate = DateFormat.format("d-MMM-yyyy", System.currentTimeMillis()).toString();
            if (!dates.equals(todayDate)){
                binding.clauseTimeStamp.setText(dates);
            }else {
                binding.clauseTimeStamp.setText("Today");
            }
            binding.clauseCopyBtn.setOnClickListener(v -> {
                ClipboardManager clipboardManager = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("clause",clauseModel.getClauseBody() + ": \n" + clauseModel.getClauseTitle());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
                binding.clauseCopyBtn.setEnabled(false);
            });
            binding.clauseTitle.setText(clauseModel.getClauseTitle());
            binding.clauseBody.setText(clauseModel.getClauseBody());
        }
    }
}

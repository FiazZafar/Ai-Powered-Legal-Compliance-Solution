package com.fyp.chatbot.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.chatbot.MainActivity;
import com.fyp.chatbot.R;

import java.util.List;

public class DocAnalyzerAdapter extends RecyclerView.Adapter<DocAnalyzerAdapter.ViewHolder> {
    List<String> taskList;
    Context context;
    public DocAnalyzerAdapter(List<String> taskList, Context context){
        this.taskList = taskList;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.doc_type_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String currentType = taskList.get(position);
        holder.taskType.setText(currentType);
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("DocAnalyzer",true);
            intent.putExtra("TaskType", currentType);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskType;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskType = itemView.findViewById(R.id.taskType);
        }
    }
}

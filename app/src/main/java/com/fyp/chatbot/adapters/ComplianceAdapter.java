package com.fyp.chatbot.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.chatbot.R;
import com.fyp.chatbot.models.Checklist;

import java.util.List;

public class ComplianceAdapter extends RecyclerView.Adapter<ComplianceAdapter.ViewHolder> {

    private List<Checklist> checklists;
    public ComplianceAdapter(List<Checklist> checklists){
        this.checklists = checklists;
    }
    @NonNull
    @Override
    public ComplianceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplianceAdapter.ViewHolder holder, int position) {
            Checklist currentItem = checklists.get(position);
            holder.title.setText(currentItem.getTitle());
            holder.description.setText(currentItem.getDescription());
    }

    @Override
    public int getItemCount() {
        return checklists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,description;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            description = itemView.findViewById(R.id.tvDescription);
        }
    }
}

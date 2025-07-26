package com.fyp.chatbot.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.chatbot.R;
import com.fyp.chatbot.models.DocomentModel;

import java.io.File;
import java.util.List;

public class RecentDocAdapter extends RecyclerView.Adapter<RecentDocAdapter.ViewHolder> {

    List<DocomentModel> docomentModelList;
    Context context ;

    public RecentDocAdapter(List<DocomentModel> docoments){
        this.docomentModelList = docoments;
    }
    @NonNull
    @Override
    public RecentDocAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_document,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentDocAdapter.ViewHolder holder, int position) {

        DocomentModel doc = docomentModelList.get(position);

        holder.itemNameTxt.setText(doc.getDocName());
        holder.itemDateTxt.setText(doc.getAnalyzedTime());
        holder.openPdfBtn.setOnClickListener(view -> {
            try {
                String fileName = "Smart_Goval_" + doc.getDocName() + "_" + doc.getAnalyzedTime()
                        + ".pdf";
                File file = new File(context.getFilesDir(), fileName);

                Uri fileUri = FileProvider.getUriForFile(context,
                        context.getPackageName() + ".provider",
                        file);
                Intent openIntent = new Intent(Intent.ACTION_VIEW);
                openIntent.setDataAndType(fileUri, "application/pdf");
                openIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    context.startActivity(openIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "No app found to open PDF", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e) {
                    Toast.makeText(context, "Error opening file", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
            }
        });

    }

    @Override
    public int getItemCount() {
        return docomentModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTxt,itemDateTxt;
        ImageView openPdfBtn;
        ViewHolder(View itemView){
            super(itemView);

            itemNameTxt = itemView.findViewById(R.id.txtDocName);
            itemDateTxt = itemView.findViewById(R.id.txtDocDate);
            openPdfBtn = itemView.findViewById(R.id.openPdfBtn);
        }

    }
}

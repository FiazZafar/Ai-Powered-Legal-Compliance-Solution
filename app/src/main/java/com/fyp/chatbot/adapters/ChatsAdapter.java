package com.fyp.chatbot.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fyp.chatbot.R;
import com.fyp.chatbot.models.MessagesModel;
import com.fyp.chatbot.viewModels.SharedPreferenceViewModel;

import java.util.List;
import io.noties.markwon.Markwon;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
    List<MessagesModel> messagesModelList;
    private Markwon markwon;
    private Context context;

    public ChatsAdapter(List<MessagesModel> messagesModelList, Markwon markwon,String image,Context context) {
        this.messagesModelList = messagesModelList;
        this.markwon = markwon;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_bot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MessagesModel currentMessage = messagesModelList.get(position);

        // ✅ load profile image from message
        Glide.with(context)
                .load(currentMessage.getUserImage())
                .placeholder(R.drawable.profile_pic)
                .error(R.drawable.profile_pic)
                .into(holder.profileImage);

        if (currentMessage.getMessage_type().equals(MessagesModel.USER_MESSAGE)) {
            holder.userConstraint.setVisibility(View.VISIBLE);
            holder.aiConstraint.setVisibility(View.GONE);
            holder.userTextView.setText(currentMessage.getMessage());
            holder.aiTextView.setText(""); // clear AI text
            holder.sentTime.setText(currentMessage.getCurrentTime());
        } else if (currentMessage.getMessage_type().equals(MessagesModel.AI_RESPONSE)) {
            holder.aiConstraint.setVisibility(View.VISIBLE);
            holder.userConstraint.setVisibility(View.GONE);
            markwon.setMarkdown(holder.aiTextView,currentMessage.getMessage());
            holder.userTextView.setText(""); // clear user text
            holder.recieveTime.setText(currentMessage.getCurrentTime());
        }
    }

    @Override
    public int getItemCount() {
        return messagesModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout userConstraint, aiConstraint;
        TextView aiTextView, userTextView,recieveTime,sentTime;
        ImageView profileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            aiConstraint = itemView.findViewById(R.id.aiConstraint);
            userConstraint = itemView.findViewById(R.id.userConstraint);
            aiTextView = itemView.findViewById(R.id.ai_message_txt);
            userTextView = itemView.findViewById(R.id.user_message_txt);
            recieveTime = itemView.findViewById(R.id.message_recieved_time);
            sentTime = itemView.findViewById(R.id.message_sent_time);
            profileImage = itemView.findViewById(R.id.userProfile);
        }
    }
}


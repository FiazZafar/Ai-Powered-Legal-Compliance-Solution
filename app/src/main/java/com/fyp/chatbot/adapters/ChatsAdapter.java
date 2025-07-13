package com.fyp.chatbot.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.fyp.chatbot.R;
import com.fyp.chatbot.models.Messages;
import java.util.List;
import io.noties.markwon.Markwon;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
    List<Messages> messagesList;
    private Markwon markwon;

    public ChatsAdapter(List<Messages> messagesList,Markwon markwon) {
        this.messagesList = messagesList;
        this.markwon = markwon;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_bot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Messages currentMessage = messagesList.get(position);

        if (currentMessage.getMessage_type().equals(Messages.USER_MESSAGE)) {
            holder.userCardView.setVisibility(View.VISIBLE);
            holder.aiCardView.setVisibility(View.GONE);
            holder.userTextView.setText(currentMessage.getMessage());
            holder.aiTextView.setText(""); // clear AI text
            holder.sentTime.setText(currentMessage.getCurrentTime());
        } else if (currentMessage.getMessage_type().equals(Messages.AI_RESPONSE)) {
            holder.aiCardView.setVisibility(View.VISIBLE);
            holder.userCardView.setVisibility(View.GONE);
            markwon.setMarkdown(holder.aiTextView,currentMessage.getMessage());
            holder.userTextView.setText(""); // clear user text
            holder.recieveTime.setText(currentMessage.getCurrentTime());
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView aiCardView, userCardView;
        TextView aiTextView, userTextView,recieveTime,sentTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            aiCardView = itemView.findViewById(R.id.ai_style);
            userCardView = itemView.findViewById(R.id.user_style);
            aiTextView = itemView.findViewById(R.id.ai_message_txt);
            userTextView = itemView.findViewById(R.id.user_message_txt);
            recieveTime = itemView.findViewById(R.id.message_recieved_time);
            sentTime = itemView.findViewById(R.id.message_sent_time);
        }
    }
}


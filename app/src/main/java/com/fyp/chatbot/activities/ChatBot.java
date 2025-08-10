package com.fyp.chatbot.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fyp.chatbot.BuildConfig;
import com.fyp.chatbot.adapters.ChatsAdapter;
import com.fyp.chatbot.databinding.ActivityChatBotBinding;
import com.fyp.chatbot.models.MessagesModel;
import com.fyp.chatbot.viewModels.ChatBotViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import io.noties.markwon.Markwon;

public class ChatBot extends AppCompatActivity {

    List<Map<String,String>> chatHistory;
    List<MessagesModel> messagesModelList;
    List<String> questionList;
    ChatsAdapter chatsAdapter;
    ActivityChatBotBinding binding;
    ChatBotViewModel viewModel;
    private Markwon markwon;
    List<String> legalKeywords;
    private Calendar calendar;
    public static String API_KEY = BuildConfig.Google_Api_Key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(ChatBotViewModel.class);

        messagesModelList = new ArrayList<>();
        chatHistory = new ArrayList<>();
        questionList = new ArrayList<>();

        markwon = Markwon.create(getApplicationContext());

        generateText();


        binding.backBtn.setOnClickListener(view -> onBackPressed());
        LinearLayoutManager myManger = new LinearLayoutManager(this);
        myManger.setStackFromEnd(true);
        chatsAdapter = new ChatsAdapter(messagesModelList,markwon);
        binding.chatsRecycler.setAdapter(chatsAdapter);
        binding.chatsRecycler.setLayoutManager(myManger);

        chatHistory.add(Map.of(
                "role", "system",
                "content", "You are an AI-powered legal and compliance assistant. \n " +
                        "You must provide accurate, concise, and well-structured information on any legal or compliance-related query. \n " +
                        "Follow these rules strictly:\n\n" +
                        "1. Respond only to legal, compliance, or regulation-related questions.\n" +
                        "2. Your areas include but are not limited to: contract law, corporate governance, data privacy (GDPR, HIPAA, etc.), employment law, compliance regulations, intellectual property, tax law, and consumer protection.\n" +
                        "3. For each response:\n" +
                        "   - Provide a clear and concise summary.\n" +
                        "   - Use bullet points or numbered lists for clarity.\n" +
                        "   - Cite relevant laws, sections, or case precedents where possible.\n" +
                        "   - Explain in plain language so non-lawyers can understand.\n" +
                        "4. Always include a disclaimer: 'This is general information and not legal advice. Consult a qualified lawyer for specific cases.'\n" +
                        "5. If a question is outside legal/compliance scope, politely decline and invite a legal-related query.\n" +
                        "6. Do not provide personal opinions — stick to factual and legal references."
        ));

        binding.sendBtn.setOnClickListener(view -> {
            String question = binding.questionTxt.getText().toString().toLowerCase(Locale.ROOT);
            if (!question.equals("")) {

                calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a",
                        Locale.getDefault());
                String formattedDate = dateFormat.format(calendar.getTime());
                question = question.substring(0, 1).toUpperCase(Locale.ROOT) +
                        question.substring(1);
                    addtoChat(question, MessagesModel.USER_MESSAGE, formattedDate);
                    chatHistory.add(Map.of("role", "user", "content", question));
                    viewModel.setResponse(chatHistory,API_KEY);

            }
            binding.questionTxt.setText("");
        });

    }



    private void generateText() {

        viewModel.getResponse().observe(this,onReply -> {
            calendar = Calendar.getInstance();

            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String formatedDate = dateFormat.format(calendar.getTime());
            chatHistory.add(Map.of("role","assistant","content",onReply));
            addtoChat(onReply, MessagesModel.AI_RESPONSE,formatedDate);

        });
    }


    void  addtoChat(String message , String sentBY,String currentTime){
        runOnUiThread(() -> {
            messagesModelList.add(new MessagesModel(message,sentBY,currentTime));
            chatsAdapter.notifyDataSetChanged();
            binding.chatsRecycler.smoothScrollToPosition(chatsAdapter.getItemCount());
        });

    }
}

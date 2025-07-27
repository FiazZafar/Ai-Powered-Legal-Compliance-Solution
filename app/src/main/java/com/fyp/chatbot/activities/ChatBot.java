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
                "role", "user",
                "content", "You are a professional legal and compliance assistant. Follow these rules strictly:\n\n" +
                        "1. Never initiate conversation or provide unsolicited greetings\n" +
                        "2. Only respond when explicitly asked a question\n" +
                        "3. - Respond only with: \"I specialize in legal matters including [random 2 topics " +
                        "from: contract law, data privacy," +
                        " compliance regulations, intellectual property etc]. Please ask about these.\"\n" +
                        "4. For legal/compliance questions:\n" +
                        "   - Provide concise, professional answers\n" +
                        "   - Use bullet points for complex information\n" +
                        "   - Cite relevant laws/regulations when applicable\n" +
                        "   - Never provide personal legal advice\n" +
                        "5. For non-legal questions:\n" +
                        "   - Respond only with: \"I specialize in legal matters including [random 2 topics from: contract law, data privacy," +
                        " compliance regulations, intellectual property etc]. Please ask about these.\"\n" +
                        "6. Format all responses without introductory phrases like \"Hello\" or \"I'm happy to help\""
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

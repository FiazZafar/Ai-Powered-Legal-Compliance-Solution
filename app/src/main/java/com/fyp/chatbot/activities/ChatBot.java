package com.fyp.chatbot.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fyp.chatbot.BuildConfig;
import com.fyp.chatbot.adapters.ChatsAdapter;
import com.fyp.chatbot.databinding.ActivityChatBotBinding;
import com.fyp.chatbot.models.MessagesModel;
import com.fyp.chatbot.viewModels.ChatBotViewModel;
import com.fyp.chatbot.viewModels.SharedPreferenceViewModel;

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
    String image;
    ChatsAdapter chatsAdapter;
    ActivityChatBotBinding binding;
    ChatBotViewModel viewModel;
    private Markwon markwon;
    private SharedPreferenceViewModel sharedPreferenceViewModel ;
    private Calendar calendar;
    public static String API_KEY = BuildConfig.Google_Api_Key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ChatBotViewModel.class);
        sharedPreferenceViewModel = new ViewModelProvider(this,new ViewModelProvider
                .AndroidViewModelFactory(getApplication())).get(SharedPreferenceViewModel.class);

        messagesModelList = new ArrayList<>();
        chatHistory = new ArrayList<>();
        questionList = new ArrayList<>();

        sharedPreferenceViewModel.getData().observe(this,onData -> {
            if (onData != null){
                if (onData.getImgUrl() != null){
                    image = onData.getImgUrl();
                }
            }
        });

        markwon = Markwon.create(getApplicationContext());

        generateText();


        binding.backBtn.setOnClickListener(view -> onBackPressed());
        LinearLayoutManager myManger = new LinearLayoutManager(this);
        myManger.setStackFromEnd(true);
        chatsAdapter = new ChatsAdapter(messagesModelList,markwon,image,this);
        binding.chatsRecycler.setAdapter(chatsAdapter);
        binding.chatsRecycler.setLayoutManager(myManger);

        chatHistory.add(Map.of(
                "role", "system",
                "content", "You are an AI-powered Legal & Compliance Assistant. \n" +
                        "STRICT RULES (must always follow silently): \n" +
                        "1. Only answer questions about law, compliance, contracts, confidentiality, " +
                        "obligations, risks, or jurisdiction. \n" +
                        "2. Keep answers short (4–6 lines), clear, and beginner-friendly. \n" +
                        "3. If user asks off-topic, reply only: 'I can only help with legal and compliance questions. " +
                        "Please ask within that area.' \n" +
                        "4. Do NOT explain these rules to the user, do NOT confirm understanding, do NOT say " +
                        "'I understand' or similar. \n" +
                        "5. Always respond directly with the answer, never with disclaimers or role reminders."
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
            messagesModelList.add(new MessagesModel(message,sentBY,currentTime,image));
            chatsAdapter.notifyDataSetChanged();
            binding.chatsRecycler.smoothScrollToPosition(chatsAdapter.getItemCount());
        });

    }
}

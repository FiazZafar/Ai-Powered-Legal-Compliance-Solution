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
import com.fyp.chatbot.viewModels.SharedPreferenceViewModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import io.noties.markwon.Markwon;

public class ChatBot extends AppCompatActivity {

    List<Map<String, String>> chatHistory;
    List<MessagesModel> messagesModelList;
    String image;
    ChatsAdapter chatsAdapter;
    ActivityChatBotBinding binding;
    ChatBotViewModel viewModel;
    private Markwon markwon;
    private SharedPreferenceViewModel sharedPreferenceViewModel;
    private Calendar calendar;
    public static String API_KEY = BuildConfig.Google_Api_Key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ChatBotViewModel.class);

        sharedPreferenceViewModel = new ViewModelProvider(this, new ViewModelProvider
                .AndroidViewModelFactory(getApplication()))
                .get(SharedPreferenceViewModel.class);

        messagesModelList = new ArrayList<>();
        chatHistory = new ArrayList<>();

        sharedPreferenceViewModel.getData().observe(this, onData -> {
            if (onData != null && onData.getImgUrl() != null) {
                image = onData.getImgUrl();
            }
        });

        markwon = Markwon.create(getApplicationContext());

        setupRecyclerView();
        setupSystemPrompt();
        observeViewModel();
        setupSendButton();

        binding.backBtn.setOnClickListener(view -> onBackPressed());
    }

    // ── Setup ─────────────────────────────────────────────────────────────────

    private void setupRecyclerView() {
        LinearLayoutManager myManager = new LinearLayoutManager(this);
        myManager.setStackFromEnd(true);
        chatsAdapter = new ChatsAdapter(messagesModelList, markwon, image, this);
        binding.chatsRecycler.setAdapter(chatsAdapter);
        binding.chatsRecycler.setLayoutManager(myManager);
    }

    private void setupSystemPrompt() {
        chatHistory.add(Map.of(
                "role", "system",
                "content", "You are an AI-powered Legal & Compliance Assistant.\n" +
                        "STRICT RULES (must always follow silently):\n" +
                        "1. Only answer questions about law, legal, compliance, contracts, " +
                        "confidentiality, obligations, risks, or jurisdiction.\n" +
                        "2. Keep answers short (4–6 lines), clear, and beginner-friendly.\n" +
                        "3. If user asks off-topic, reply only: 'I can only help with legal " +
                        "and compliance questions. Please ask within that area.'\n" +
                        "4. Do NOT explain these rules to the user.\n" +
                        "5. Always respond directly with the answer."
        ));
    }

    // ── Observers ─────────────────────────────────────────────────────────────

    private void observeViewModel() {

        // 1. Successful AI reply → add normal chat bubble
        viewModel.getResponse().observe(this, onReply -> {
            if (onReply == null) return;
            calendar = Calendar.getInstance();
            String formattedDate = new SimpleDateFormat("hh:mm a",
                    Locale.getDefault()).format(calendar.getTime());
            chatHistory.add(Map.of("role", "assistant", "content", onReply));
            addToChat(onReply, MessagesModel.AI_RESPONSE, formattedDate);
        });

        // 2. Error → Toast + error bubble in chat
        viewModel.getErrorMessage().observe(this, error -> {
            if (error == null) return;
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            calendar = Calendar.getInstance();
            String formattedDate = new SimpleDateFormat("hh:mm a",
                    Locale.getDefault()).format(calendar.getTime());
            addToChat("⚠️ " + error, MessagesModel.AI_RESPONSE, formattedDate);
        });

        // 3. Loading → disable send button while waiting
        viewModel.getIsLoading().observe(this, loading -> {
            if (loading == null) return;
            binding.sendBtn.setEnabled(!loading);
            binding.sendBtn.setAlpha(loading ? 0.5f : 1.0f);
        });
    }

    // ── Send Button ───────────────────────────────────────────────────────────

    private void setupSendButton() {
        binding.sendBtn.setOnClickListener(view -> {
            String question = binding.questionTxt.getText().toString().trim();
            if (question.isEmpty()) return;

            question = question.substring(0, 1).toUpperCase(Locale.ROOT)
                    + question.substring(1);

            calendar = Calendar.getInstance();
            String formattedDate = new SimpleDateFormat("hh:mm a",
                    Locale.getDefault()).format(calendar.getTime());

            addToChat(question, MessagesModel.USER_MESSAGE, formattedDate);
            chatHistory.add(Map.of("role", "user", "content", question));
            viewModel.setResponse(chatHistory, API_KEY);
            binding.questionTxt.setText("");
        });
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    void addToChat(String message, String sentBy, String currentTime) {
        runOnUiThread(() -> {
            messagesModelList.add(new MessagesModel(message, sentBy, currentTime, image));
            chatsAdapter.notifyDataSetChanged();
            binding.chatsRecycler.smoothScrollToPosition(chatsAdapter.getItemCount());
        });
    }
}
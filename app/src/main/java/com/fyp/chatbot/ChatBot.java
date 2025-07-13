package com.fyp.chatbot;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fyp.chatbot.adapters.ChatsAdapter;
import com.fyp.chatbot.apimodels.Content;
import com.fyp.chatbot.apimodels.GeminiResponse;
import com.fyp.chatbot.apimodels.Part;
import com.fyp.chatbot.apimodels.RequestBodyGemini;
import com.fyp.chatbot.databinding.ActivityChatBotBinding;
import com.fyp.chatbot.helpers.RetrofitClient;
import com.fyp.chatbot.interfaces.GeminiApi;
import com.fyp.chatbot.models.Messages;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.noties.markwon.Markwon;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatBot extends AppCompatActivity {

    List<Map<String,String>> chatHistory;
    List<Messages> messagesList;
    List<String> questionList;
    ChatsAdapter chatsAdapter;
    ActivityChatBotBinding binding;
    private Markwon markwon;
    List<String> legalKeywords;
    private Calendar calendar;
    public static String API_KEY ="AIzaSyDaSlnvV51ChATz83NMtF8z4xSUIJo2S7w";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        messagesList = new ArrayList<>();
        chatHistory = new ArrayList<>();
        questionList = new ArrayList<>();

        markwon = Markwon.create(getApplicationContext());


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        LinearLayoutManager myManger = new LinearLayoutManager(this);
        myManger.setStackFromEnd(true);
        chatsAdapter = new ChatsAdapter(messagesList,markwon);
        binding.chatsRecycler.setAdapter(chatsAdapter);
        binding.chatsRecycler.setLayoutManager(myManger);

        chatHistory.add(Map.of("role","user","content","hi"));

        legalKeywords = Arrays.asList(
                "law",
                "legal",
                "compliance",
                "contract",
                "agreement",
                "regulation",
                "policy",
                "terms and conditions",
                "privacy",
                "privacy policy",
                "audit",
                "governance",
                "risk management",
                "intellectual property",
                "copyright",
                "patent",
                "trademark",
                "dispute",
                "litigation",
                "arbitration",
                "employment law",
                "labor law",
                "data protection",
                "gdpr",
                "cybersecurity compliance",
                "business law",
                "corporate law",
                "consumer protection",
                "compliance report",
                "ethics",
                "code of conduct",
                "whistleblower",
                "anti-bribery",
                "anti-corruption",
                "financial compliance",
                "tax law",
                "environmental compliance",
                "health and safety regulations",
                "regulatory compliance",
                "sanctions",
                "licensing",
                "disclosure",
                "due diligence",
                "nda",
                "non-disclosure agreement",
                "service level agreement",
                "sla",
                "breach of contract",
                "liability",
                "terms of service"
        );


        binding.sendBtn.setOnClickListener(view -> {
            String question = binding.questionTxt.getText().toString().toLowerCase(Locale.ROOT);
            if (!question.equals("")) {

                boolean isLegalQuery = false;  // Important: reset every time
                for (String keyword : legalKeywords) {
                    if (question.contains(keyword)) {
                        isLegalQuery = true;
                        break; // Stop checking after first match
                    }
                }

                calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                String formattedDate = dateFormat.format(calendar.getTime());
                question = question.substring(0, 1).toUpperCase(Locale.ROOT) + question.substring(1);
                if (isLegalQuery) {
                    addtoChat(question, Messages.USER_MESSAGE, formattedDate);
                    chatHistory.add(Map.of("role", "user", "content", question));
                    generateText();  // Now safe to call
                } else {
                    String illegalQuery = "I'm specialized in assisting with Legal and Compliance topics. How may I help you today?";
                    addtoChat(question, Messages.USER_MESSAGE, formattedDate);
                    addtoChat(illegalQuery, Messages.AI_RESPONSE, formattedDate);
                }
            }
            binding.questionTxt.setText("");
        });

    }



    private void generateText() {
        RetrofitClient retrofitClient = new RetrofitClient("https://generativelanguage.googleapis.com/");


        GeminiApi geminiApi =  retrofitClient.getRetrofit().create(GeminiApi.class);
        // Build the request
        List<Part> parts = new ArrayList<>();

        for (Map<String ,String > entry:chatHistory){
            parts.add(new Part(entry.get("content")));

        }


        List<Content> contents = new ArrayList<>();
        contents.add(new Content(parts));

        RequestBodyGemini requestBody = new RequestBodyGemini(contents);

        Call<GeminiResponse> call = geminiApi.generateContent(API_KEY, requestBody);

        call.enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                calendar = Calendar.getInstance();
                Toast.makeText(getApplicationContext(), "Currnet time is " + calendar.getTime(), Toast.LENGTH_SHORT).show();

                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                String formatedDate = dateFormat.format(calendar.getTime());
                if (response.isSuccessful() && response.body() != null) {

                    String reply = response.body()
                            .getCandidates()
                            .get(0)
                            .getContent()
                            .getParts()
                            .get(0)
                            .getText();
                    chatHistory.add(Map.of("role","assistant","content",reply));
                    addtoChat(reply, Messages.AI_RESPONSE,formatedDate);
                } else {
                    addtoChat("Failed to get response.", Messages.AI_RESPONSE,formatedDate);
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                calendar = Calendar.getInstance();
                Toast.makeText(getApplicationContext(), "Currnet time is " + calendar.getTime(), Toast.LENGTH_SHORT).show();

                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a",Locale.getDefault());
                String formatedDate = dateFormat.format(calendar.getTime());
                addtoChat("Error: " + t.getMessage(), Messages.AI_RESPONSE,formatedDate);
            }
        });
    }



    void  addtoChat(String message , String sentBY,String currentTime){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messagesList.add(new Messages(message,sentBY,currentTime));
                chatsAdapter.notifyDataSetChanged();
                binding.chatsRecycler.smoothScrollToPosition(chatsAdapter.getItemCount());
            }
        });

    }
}

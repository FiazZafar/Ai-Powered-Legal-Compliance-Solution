package com.fyp.chatbot.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import com.fyp.chatbot.databinding.FragmentPreviewContractBinding;
import com.tom_roush.pdfbox.util.PDFHighlighter;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.noties.markwon.Markwon;

public class PreviewContractFragment extends Fragment {

    private FragmentPreviewContractBinding binding;
    private String htmlContent ;

    public PreviewContractFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPreviewContractBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        String contractText = getArguments().getString("AI_RESPONSE");

        Markwon markwon = Markwon.create(getContext());
        markwon.setMarkdown(binding.contractTextView,contractText);


        binding.generatePdfButton.setOnClickListener(view1 ->{
            String finalContract = binding.contractTextView.getText().toString();

            htmlContent = convertMarkdownToHtml(finalContract);

            binding.webView.getSettings().setJavaScriptEnabled(true);
            binding.webView.getSettings().setDomStorageEnabled(true);
            binding.webView.getSettings().setUseWideViewPort(true);
            binding.webView.getSettings().setLoadWithOverviewMode(true);

            binding.webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);

            generatePdfFromWebView(binding.webView);
        });
        return view;
    }

    private String convertMarkdownToHtml(String markdown) {
        String body = HtmlRenderer.builder().build().render(Parser.builder().build().parse(markdown));

        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "  @page { margin: 2cm; }" +  // PDF page margins
                "  body {" +
                "    font-family: 'Helvetica Neue', Arial, sans-serif;" +
                "    font-size: 11pt;" +
                "    line-height: 1.6;" +
                "    color: #333;" +
                "    max-width: 800px;" +
                "    margin: 0 auto;" +
                "    padding: 40px;" +
                "  }" +
                "  h1 {" +
                "    color: #2c3e50;" +
                "    font-size: 22pt;" +
                "    font-weight: 600;" +
                "    margin-top: 30px;" +
                "    margin-bottom: 20px;" +
                "    border-bottom: 1px solid #eee;" +
                "    padding-bottom: 10px;" +
                "  }" +
                "  h2 {" +
                "    color: #34495e;" +
                "    font-size: 18pt;" +
                "    margin-top: 25px;" +
                "    margin-bottom: 15px;" +
                "  }" +
                "  p {" +
                "    margin-bottom: 16px;" +
                "    text-align: justify;" +
                "  }" +
                "  ul, ol {" +
                "    margin-left: 20px;" +
                "    margin-bottom: 20px;" +
                "  }" +
                "  li {" +
                "    margin-bottom: 8px;" +
                "  }" +
                "  code {" +
                "    background: #f8f8f8;" +
                "    padding: 2px 5px;" +
                "    border-radius: 3px;" +
                "    font-family: 'Courier New', monospace;" +
                "  }" +
                "  blockquote {" +
                "    border-left: 4px solid #3498db;" +
                "    padding-left: 15px;" +
                "    color: #7f8c8d;" +
                "    margin-left: 0;" +
                "    font-style: italic;" +
                "  }" +
                "  .header {" +
                "    text-align: center;" +
                "    margin-bottom: 40px;" +
                "  }" +
                "  .footer {" +
                "    margin-top: 40px;" +
                "    font-size: 9pt;" +
                "    color: #95a5a6;" +
                "    text-align: center;" +
                "    border-top: 1px solid #eee;" +
                "    padding-top: 10px;" +
                "  }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='header'>" +
                "  <h1>AI Analysis Report</h1>" +
                "  <p>Generated on " + new SimpleDateFormat("MMMM dd, yyyy").format(new Date()) + "</p>" +
                "</div>" +
                body +
                "<div class='footer'>" +
                "  <p>Confidential - Generated by YourApp</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }


    private void generatePdfFromWebView(WebView webView){
        PrintManager printManager =(PrintManager) requireContext().getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter documentAdapter = webView.createPrintDocumentAdapter("contracts_doc");


        // Set print attributes (A4 size with margins)
        PrintAttributes attributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setMinMargins(new PrintAttributes.Margins(2000, 2000, 2000, 2000)) // 20mm margins
                .build();

        printManager.print("contracts_doc",documentAdapter,attributes);
    }
}
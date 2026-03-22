package com.supabase.java;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class EmailReporter {

    private final Context context;
    private final String devEmail = "programmar5461396@gmail.com"; // အမှားတွေကို လက်ခံမယ့် မင်းရဲ့ email

    public EmailReporter(Context context) {
        this.context = context;
    }

    public void reportError(String errorType, String errorDetail) {
        // ဖုန်းရဲ့ အချက်အလက်တွေကိုပါ တစ်ခါတည်း ယူမယ်
        String deviceInfo = "\n\n--- Device Info ---" +
                "\nModel: " + android.os.Build.MODEL +
                "\nAndroid: " + android.os.Build.VERSION.RELEASE +
                "\nSDK: " + android.os.Build.VERSION.SDK_INT;

        String body = "Hi Developer,\n\nI found an error in the library.\n\n" + 
                     "Error Detail:\n" + errorDetail + deviceInfo;

        // Intent တည်ဆောက်ခြင်း
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // email application တွေပဲ ပေါ်လာအောင်
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{devEmail});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Library Error Report: " + errorType);
        intent.putExtra(Intent.EXTRA_TEXT, body);

        try {
            // Gmail သို့မဟုတ် Email App ကို လှမ်းဖွင့်မယ်
            context.startActivity(Intent.createChooser(intent, "Email ပို့ရန် App ကို ရွေးပါ"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "Email App ရှာမတွေ့ပါဘူး ခင်ဗျာ", Toast.LENGTH_SHORT).show();
        }
    }
}

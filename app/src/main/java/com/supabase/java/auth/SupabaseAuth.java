package com.supabase.java.auth;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import com.supabase.java.SupabaseClient;
import com.supabase.java.SupabaseListener;
import org.json.JSONException;
import org.json.JSONObject;

public class SupabaseAuth {
	private SupabaseClient client;
	private RequestQueue requestQueue;
	
	public SupabaseAuth(SupabaseClient client) {
		this.client = client;
		this.requestQueue = Volley.newRequestQueue(client.getContext());
	}
	
	// Sign Up (အကောင့်အသစ်ဖွင့်ခြင်း)
	public void signUp(String email, String password, final SupabaseListener<String> listener) {
		String url = client.getUrl() + "/auth/v1/signup";
		Log.i("-----",url);
		performAuthRequest(url, email, password, listener);
	}
	
	// Login (အကောင့်ဝင်ခြင်း)
	public void login(String email, String password, final SupabaseListener<String> listener) {
		String url = client.getUrl() + "/auth/v1/token?grant_type=password";
		performAuthRequest(url, email, password, listener);
	}
	
	private void performAuthRequest(String url, String email, String password, final SupabaseListener<String> listener) {
		JSONObject jsonBody = new JSONObject();
		try {
			jsonBody.put("email", email);
			jsonBody.put("password", password);
		} catch (JSONException e) { e.printStackTrace(); }
		
		JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
		response -> {
			try {
				String token = response.getString("access_token");
				listener.onSuccess(token);
				} catch (JSONException e) {
				listener.onError("Parsing error: " + e.getMessage());
			}
		},
		error -> {
			String errorString = handleAuthError(error);
			listener.onError(errorString);
		}
		
		) {
			@Override
			public Map<String, String> getHeaders() {
				Map<String, String> headers = new HashMap<>();
				headers.put("apikey", client.getKey());
				headers.put("Content-Type", "application/json");
				return headers;
			}
		};
		requestQueue.add(request);
	}
	private String handleAuthError(VolleyError error) {
    if (error.networkResponse == null) {
        return "အင်တာနက် ချိတ်ဆက်မှု မရှိပါဘူး။ လိုင်းပြန်စစ်ပေးပါဦး။";
    }

    int statusCode = error.networkResponse.statusCode;
    String rawData = new String(error.networkResponse.data);
    String serverMessage = "";

    try {
        JSONObject json = new JSONObject(rawData);
        serverMessage = json.optString("msg", json.optString("error_description", ""));
    } catch (Exception e) {
        serverMessage = rawData;
    }

    switch (statusCode) {
        case 400:
            if (serverMessage.contains("invalid_credentials")) {
                return "Email သို့မဟုတ် Password မှားနေပါတယ်";
            }
            return "ပို့လိုက်တဲ့ အချက်အလက် Format မမှန်ပါဘူး (400)။";
        case 422:
            if (serverMessage.contains("already registered")) {
                return "ဒီ Email က အကောင့်ဖွင့်ပြီးသား ဖြစ်နေပါတယ်။";
            }
            if (serverMessage.contains("at least 6 characters")) {
                return "Password က အနည်းဆုံး ၆ လုံး ရှိရမယ်။";
            }
            return "ဖြည့်သွင်းမှု မှားယွင်းနေပါတယ် (422)။";
        case 429:
            return "Request တွေ အရမ်းများနေလို့ ခဏနေမှ ပြန်ကြိုးစားပေးပါ။";
        case 404:
            return "Auth Service ကို ရှာမတွေ့ပါဘူး (URL မှားနိုင်ပါတယ်)။";
        default:
            return "Auth Error (" + statusCode + "): " + serverMessage;
    }
}

}
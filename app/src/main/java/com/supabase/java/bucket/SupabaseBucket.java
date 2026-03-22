package com.supabase.java.bucket;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.supabase.java.SupabaseClient;
import com.supabase.java.SupabaseListener;
import com.supabase.java.bucket.ByteArrayRequest;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class SupabaseBucket {
    private SupabaseClient client;
    private String bucketName;
    private RequestQueue requestQueue;

    public SupabaseBucket(SupabaseClient client, String bucketName) {
        this.client = client;
        this.bucketName = bucketName;
        this.requestQueue = Volley.newRequestQueue(client.getContext());
    }

    public void uploadFile(String filePath, String destinationName, final SupabaseListener listener) {
        try {
            File file = new File(filePath);
            byte[] fileBytes = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(fileBytes);
            fis.close();
			
		    String uploadUrl = client.getUrl() + "/storage/v1/object/" + bucketName + "/" + destinationName;

            ByteArrayRequest request = new ByteArrayRequest(
                Request.Method.POST, 
                uploadUrl, 
                fileBytes, 
                getMimeType(filePath),
                new Response.Listener<byte[]>() {
					@Override
					public void onResponse(byte[] responseData) {
						// ဒီနေရာမှာ byte[] ရလာပေမယ့် listener ဆီကို String ပဲ ပြန်ပို့မယ်
						listener.onSuccess("Upload Success!");
					}
				},
                error -> {
					String errorString = handleStorageError(error);
					listener.onError(errorString);
				}
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("apikey", client.getKey());
                    headers.put("Authorization", "Bearer " + client.getKey());
                    headers.put("x-upsert", "true"); 
                    return headers;
                }
            };

            requestQueue.add(request);

        } catch (Exception e) {
            listener.onError(e.getMessage());
        }
    }
	
	public void deleteFile(String fileName, final SupabaseListener<String> listener) {
    String deleteUrl = client.getUrl() + "/storage/v1/object/" + bucketName + "/" + fileName;

    StringRequest request = new StringRequest(
        Request.Method.DELETE, 
        deleteUrl, 
        response -> listener.onSuccess("ဖျက်ပြီးပါပြီ သားကြီး"),
        error -> {
            
			String errorString = handleStorageError(error);
			listener.onError(errorString);
            
        }
    ) {
        @Override
        public Map<String, String> getHeaders() {
            Map<String, String> headers = new HashMap<>();
            headers.put("apikey", client.getKey());
            headers.put("Authorization", "Bearer " + client.getKey());
            return headers;
        }

        @Override
        public byte[] getBody() {
             return null;
        }
    };

    requestQueue.add(request);
}


    public String getPublicUrl(String fileName) {
        return client.getUrl() + "/storage/v1/object/public/" + bucketName + "/" + fileName;
    }

    private String getMimeType(String path) {
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".png")) return "image/png";
        return "application/octet-stream";
    }
	
	private String handleStorageError(VolleyError error) {
    if (error.networkResponse == null) {
        return "File တင်လို့မရပါဘူး။ အင်တာနက်လိုင်း ပြန်စစ်ပေးပါ သားကြီး။";
    }

    int statusCode = error.networkResponse.statusCode;
    String rawData = new String(error.networkResponse.data);
    String errorMessage = "";

    try {
        JSONObject json = new JSONObject(rawData);
        errorMessage = json.optString("message", json.optString("error", ""));
    } catch (Exception e) {
        errorMessage = rawData;
    }

    switch (statusCode) {
        case 400:
            return "File Format မှားနေတာ ဒါမှမဟုတ် URL လမ်းကြောင်း မှားနေပါတယ် (400)။";
        case 403:
            return "ဒီ Bucket ထဲကို File တင်ခွင့် မရှိပါဘူး (Storage Policy ကို စစ်ပါ)။";
        case 404:
            return "တင်မယ့် Bucket သို့မဟုတ် Folder ကို ရှာမတွေ့ပါဘူး (404)။";
        case 413:
            return "ဖိုင်က အရမ်းကြီးလွန်းနေတယ် (Free plan က 50MB ပဲ ရပါတယ်)။";
        case 415:
            return "ဒီလို ဖိုင်အမျိုးအစား (Media Type) ကို လက်မခံပါဘူး။";
        case 409:
            return "ဒီနာမည်နဲ့ ဖိုင်က ရှိပြီးသားမို့ ထပ်တင်လို့ မရပါဘူး (Duplicate)။";
        default:
            return "Storage Error (" + statusCode + "): " + errorMessage;
    }
}


}

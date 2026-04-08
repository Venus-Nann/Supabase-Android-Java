package com.supabase.java.database;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError; // Error handle လုပ်ဖို့ လိုအပ်ပါတယ်
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley; // Request queue အတွက်
import com.supabase.java.SupabaseClient;
import com.supabase.java.SupabaseListener;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class SupabaseDatabase {
    private final SupabaseClient client;
    private final String tableName;
    private String filterQuery = "";
    private String url = "";
    private int method = Request.Method.GET;
    private JSONObject object = null;
	private String userKey = "";
	
	public void setUserKey(String userKey){
		this.userKey = userKey;
	}
	
    public SupabaseDatabase(SupabaseClient client, String tableName) {
        this.client = client;
        this.tableName = tableName;
    }

    // --- Filter Methods ---
    public SupabaseDatabase eq(String column, String value) {
        filterQuery += "&" + column + "=eq." + value;
        return this;
    }

    public SupabaseDatabase gt(String column, String value) {
        filterQuery += "&" + column + "=gt." + value;
        return this;        
    }
	
	public SupabaseDatabase lt(String column, String value) {
        filterQuery += "&" + column + "=lt." + value;
        return this;        
    }

    public SupabaseDatabase order(String column, boolean ascending) {
        filterQuery += "&order=" + column + "." + (ascending ? "asc" : "desc");
        return this;
    }
	    // --- Additional Filter Methods (Chainable) ---

    // Not Equal: တန်ဖိုး မတူညီတာကို ရှာရန်
    public SupabaseDatabase neq(String column, String value) {
        filterQuery += "&" + column + "=neq." + value;
        return this;
    }

    // Greater Than or Equal: ကြီးတာ (သို့) ညီတာ
    public SupabaseDatabase gte(String column, String value) {
        filterQuery += "&" + column + "=gte." + value;
        return this;
    }

    // Less Than or Equal: ငယ်တာ (သို့) ညီတာ
    public SupabaseDatabase lte(String column, String value) {
        filterQuery += "&" + column + "=lte." + value;
        return this;
    }

    // Case-insensitive Like: အကြီးအသေးမခွဲဘဲ စာသားပါဝင်မှုရှာရန်
    public SupabaseDatabase ilike(String column, String value) {
        // value မှာ % သို့မဟုတ် * မပါရင် keyword အနေနဲ့ ရှာဖို့ ilike.*keyword* ပုံစံသုံးရပါတယ်
        filterQuery += "&" + column + "=ilike.*" + value + "*";
        return this;
    }

    // Is: အဓိကအားဖြင့် NULL ဖြစ်မဖြစ် စစ်ရန် (value က "null", "true", "false")
    public SupabaseDatabase is(String column, String value) {
        filterQuery += "&" + column + "=is." + value;
        return this;
    }

    // In: List ထဲမှာ ပါဝင်တာကို ရှာရန် (e.g. "apple,orange,banana")
    public SupabaseDatabase in(String column, String csvValues) {
        filterQuery += "&" + column + "=in.(" + csvValues + ")";
        return this;
    }

    // Limit: ရလဒ်အရေအတွက် ကန့်သတ်ရန်
    public SupabaseDatabase limit(int count) {
        filterQuery += "&limit=" + count;
        return this;
    }

    // Offset: ရှေ့က data အရေအတွက်ကို ကျော်ပစ်ရန် (Pagination အတွက် limit နဲ့ တွဲသုံးပါတယ်)
    public SupabaseDatabase offset(int count) {
        filterQuery += "&offset=" + count;
        return this;
    }

    // --- Logical Operators (Advanced) ---

    // OR Logic: သတ်မှတ်ထားတဲ့ ပုံစံအတိုင်း filter ဆောက်ဖို့ (e.g. "id.eq.5,name.eq.Aung")
    public SupabaseDatabase or(String filters) {
        filterQuery += "&or=(" + filters + ")";
        return this;
    }

    // --- CRUD Configuration (Data မပို့သေးဘဲ ပုံစံပဲ သတ်မှတ်တာ) ---
    public SupabaseDatabase select(String columns) {
        this.url = client.getUrl() + "/rest/v1/" + tableName + "?select=" + columns;
        this.method = Request.Method.GET;
        this.object = null;
        return this;
    }

    public SupabaseDatabase insert(JSONObject data) {
        this.url = client.getUrl() + "/rest/v1/" + tableName;
        this.method = Request.Method.POST;
        this.object = data;
        return this;
    }

    public SupabaseDatabase update(JSONObject data) {
        this.url = client.getUrl() + "/rest/v1/" + tableName + "?";
        this.method = Request.Method.PATCH;
        this.object = data;      
        return this;
    }

    public SupabaseDatabase delete() {
        this.url = client.getUrl() + "/rest/v1/" + tableName + "?";
        this.method = Request.Method.DELETE;
        this.object = null;
        return this;
    }

    // --- Execution ---
    public void execute(SupabaseListener listener) {
		if (filterQuery.startsWith("&")) {
    filterQuery = filterQuery.substring(1);
		}
        // Final URL combine (Base URL + Filters)
        String finalUrl = this.url + (this.url.contains("?") ? "" : "?") + filterQuery;
        
        sendRequest(method, finalUrl, object == null ? null : object.toString(), listener);
        
        // Reset state for next use
        filterQuery = ""; 
		Log.e("----SupaDB","execute");
    }
	
    private void sendRequest(int method, String url, final String body, final SupabaseListener listener) {
        StringRequest request = new StringRequest(method, url, 
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (listener != null) listener.onSuccess(response);
                }
            }, 
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (listener != null) {
                        String errorMessage = handleDbError(error);
                        listener.onError(errorMessage);
                    }
                }
            }) {
            @Override
            public byte[] getBody() { return body == null ? null : body.getBytes(); }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", client.getKey());
				headers.put("Authorization", "Bearer " + (client.getUserKey().isEmpty() ? client.getKey() : client.getUserKey()));			
				headers.put("Content-Type", "application/json");
                headers.put("Prefer", "return=representation");
				headers.put("Accept","application/json");
				Log.e("-----key",client.getKey());
                return headers;
            }
        };
			
		Volley.newRequestQueue(client.getContext()).add(request);
		

   
    }
	
	private String handleDbError(VolleyError error) {
    if (error.networkResponse == null) {
        return "အင်တာနက် ချိတ်ဆက်မှု မရှိပါဘူး။";
    }

    int statusCode = error.networkResponse.statusCode;
    String rawData = new String(error.networkResponse.data);
    
    // Supabase က ပြန်ပေးတဲ့ error message အစစ်ကို JSON ထဲကနေ ဆွဲထုတ်မယ်
    String serverMessage = "";
    try {
        JSONObject json = new JSONObject(rawData);
        serverMessage = json.optString("message", "");
    } catch (Exception e) {
        serverMessage = rawData;
    }

    switch (statusCode) {
        case 400:
            return "အချက်အလက် ပေးပို့မှု ပုံစံမှားနေပါတယ် (Bad Request)။";
        case 401:
            return "Login Token သက်တမ်းကုန်သွားပါပြီ။ Login ပြန်ဝင်ပေးပါ။";
        case 403:
            return "RLS Policy ကြောင့် ခွင့်ပြုချက် မရှိပါဘူး (Forbidden)။";
        case 404:
            return "ရှာဖွေနေတဲ့ Table သို့မဟုတ် Data ကို မတွေ့ပါဘူး။";
        case 409:
            return "ဒီအချက်အလက်က Database ထဲမှာ ရှိပြီးသား ဖြစ်နေပါတယ် (Duplicate)။";
        case 406:
            return "Content Not Acceptable (Header တွေ ပြန်စစ်ပါ)။";
        default:
            return "Error: " + statusCode + " - " + serverMessage;
    }
}

}

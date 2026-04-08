package com.supabase.java;

import android.content.Context;
import com.supabase.java.auth.SupabaseAuth;
import com.supabase.java.bucket.SupabaseBucket;
import com.supabase.java.database.SupabaseDatabase;

public class SupabaseClient {
    private final String url;
    private final String key;
	private final Context context;
	private String userKey;
	
	public void setUserKey(String userKey){
		this.userKey = userKey;
	}
	
	public String getUserKey(){
		return userKey;
	}
	
    public SupabaseClient(Context context,String url, String key) {
        this.context = context;
		this.url = url;
        this.key = key;
    }

	public Context getContext() { return context; }
    public String getUrl() { return url; }
    public String getKey() { return key; }

   public SupabaseDatabase setDatabase(String tableName) {
        return new SupabaseDatabase(this, tableName);
    }
	public SupabaseBucket setBucket(String bucketName) {
        return new SupabaseBucket(this, bucketName);
    }
	
	public SupabaseAuth setAuth() {
        return new SupabaseAuth(this);
    }
}

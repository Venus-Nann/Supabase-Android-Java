package com.supabase.java;

public interface SupabaseListener<T> {
    void onSuccess(T response);
    void onError(String error);
}

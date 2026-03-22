package com.supabase.java.bucket;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

public class ByteArrayRequest extends Request<byte[]> {
	private final Response.Listener<byte[]> mListener;
	private final byte[] mRequestBody;
	private final String mContentType;
	
	public ByteArrayRequest(int method, String url, byte[] requestBody, String contentType,
	Response.Listener<byte[]> listener, Response.ErrorListener errorListener) {
		super(method, url, errorListener);
		this.mListener = listener;
		this.mRequestBody = requestBody;
		this.mContentType = contentType;
	}
	
	@Override
	public String getBodyContentType() {
		return mContentType;
	}
	
	@Override
	public byte[] getBody() {
		return mRequestBody;
	}
	
	@Override
	protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
		return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
	}
	
	@Override
	protected void deliverResponse(byte[] response) {
		mListener.onResponse(response);
	}
}
package com.example.moetaz.chathub.help;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class Mysingleton {
    private static Mysingleton mInstacne;
    private RequestQueue requestQueue;
    private Context context;

    private Mysingleton (Context context){
        this.context = context;
        requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue (){

        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized Mysingleton getInstance (Context context){
        if(mInstacne == null){
            mInstacne = new Mysingleton(context);
        }
        return mInstacne;
    }

    public <T>void addToRequest (Request<T> request){
        requestQueue.add(request);
    }
}

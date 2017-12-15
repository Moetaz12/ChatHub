package com.example.moetaz.chathub.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.help.Mysingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class AboutActivity extends AppCompatActivity {

    String url = "https://api.myjson.com/bins/szagr";
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        textView = findViewById(R.id.about_app);
        LoadByVolley();

    }

    private void LoadByVolley() {

        Cache cache = Mysingleton.getInstance(getApplicationContext()).getRequestQueue().getCache();
        Cache.Entry entry = cache.get(url);
        if (entry != null) {

            try {
                String data = new String(entry.data, "UTF-8");
                JSONObject jsonObject =new JSONObject(data);
                textView.setText(jsonObject.getString("desc"));

            } catch (UnsupportedEncodingException e) {

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {


            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject =new JSONObject(response);
                        textView.setText(jsonObject.getString("desc"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            Mysingleton.getInstance(getApplicationContext()).addToRequest(stringRequest);
        }
    }
}

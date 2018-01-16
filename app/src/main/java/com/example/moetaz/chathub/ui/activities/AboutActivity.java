package com.example.moetaz.chathub.ui.activities;

import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;

import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.help.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    String url = "https://api.myjson.com/bins/szagr";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_about);
        //textView = findViewById(R.id.about_app);
        simulateDayNight(0);
        if (Utilities.isNetworkConnected(getApplicationContext())) {
            new MyAsyncTask(url,getApplicationContext()).execute();
        } else {
            Utilities.message(getApplicationContext(),getString(R.string.checking_internet_msg));
        }

    }


    private class MyAsyncTask extends AsyncTask<Void, Void, String> {

        String strUrl = null;
        Context context;

        public MyAsyncTask(String url, Context context) {

            this.strUrl = url;
            this.context = context;
        }


        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonStr = null;

            try {

                URL url = new URL(strUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {

                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonStr = buffer.toString();

            } catch (IOException e) {


                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {

                    }
                }
            }
            return jsonStr;
        }

        @Override
        protected void onPostExecute(String mstring) {

            try {
                JSONObject jsonObject = new JSONObject(mstring);
                String s = jsonObject.getString("desc");
                //textView.setText(s);
                View aboutPage = new AboutPage(context)
                        .isRTL(false)
                        .setDescription(s)
                        .setImage(R.drawable.ic_launcher_logo)
                        .addItem(new Element().setTitle("Version 1.0"))
                        .addGroup("Connect with us")
                        .addEmail("moetazashraf82@gmail.com")
                        .create();

                AboutActivity.this.setContentView(aboutPage);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    void simulateDayNight(int currentSetting) {
        final int DAY = 0;
        final int NIGHT = 1;
        final int FOLLOW_SYSTEM = 3;

        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        if (currentSetting == DAY && currentNightMode != Configuration.UI_MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        } else if (currentSetting == NIGHT && currentNightMode != Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        } else if (currentSetting == FOLLOW_SYSTEM) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }
}

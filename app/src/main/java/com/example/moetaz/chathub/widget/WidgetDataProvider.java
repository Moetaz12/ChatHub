package com.example.moetaz.chathub.widget;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.moetaz.chathub.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.moetaz.chathub.provider.ConvProivderConstants.CONTENT_URI_1;

/**
 * Created by Moetaz on 12/15/2017.
 */

public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {
    ContentResolver contentResolver;
    Intent intent;
    Context context;
    private List<String> collection = new ArrayList<>();

    public WidgetDataProvider(Intent intent, Context context) {

        this.intent = intent;
        this.context = context;
    }


    @Override
    public void onCreate() {
        InitiData();
    }

    private void InitiData() {
        collection.clear();
        contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI_1, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                collection.add(cursor.getString(0));
            }
        }
        assert cursor != null;
        cursor.close();
    }

    @Override
    public void onDataSetChanged() {
        InitiData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return collection.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);
        remoteViews.setTextViewText(R.id.widgettext, collection.get(position));
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}

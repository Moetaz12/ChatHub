package com.example.moetaz.chathub.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Moetaz on 12/15/2017.
 */

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetDataProvider(intent ,this);
    }
}

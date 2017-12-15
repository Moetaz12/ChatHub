package com.example.moetaz.chathub.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.moetaz.chathub.dataStorage.DBAdadpter;


public class ConvProvider extends ContentProvider {
    public static final String AUTHORITY = "com.example.moetaz.chathub";
    public static final String PATH_ING_LIST = "CONV_LIST";
    public static final int CONV_LIST = 1;
    public static final String MIME_TYPE_1 = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + "vnd.com.moetaz.messages";
    public static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        MATCHER.addURI(AUTHORITY, PATH_ING_LIST, CONV_LIST);
    }

    private DBAdadpter dbAdadpter;

    @Override
    public boolean onCreate() {
        dbAdadpter = DBAdadpter.getDBAdadpterInstance(getContext());
        return true;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
            throws UnsupportedOperationException {
        Cursor cursor = null;
        switch (MATCHER.match(uri)) {
            case CONV_LIST:
                cursor = dbAdadpter.getMessages();
                break;

            default:
                cursor = null;
                break;
        }
        return cursor;
    }


    @Override
    public String getType(@NonNull Uri uri) {
        switch (MATCHER.match(uri)) {
            case CONV_LIST:
                return MIME_TYPE_1;

        }
        return null;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) throws UnsupportedOperationException {
        Uri Returnuri = null;
        switch (MATCHER.match(uri)) {

            case CONV_LIST:
                Returnuri = insertMsg(uri, values);
                break;
            default:
                new UnsupportedOperationException("Error");
                break;
        }
        return Returnuri;
    }

    private Uri insertMsg(Uri uri, ContentValues values) {
        long id = dbAdadpter.insertMessage(values);
        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.parse("content://" + AUTHORITY + "/" + PATH_ING_LIST + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) throws UnsupportedOperationException {
        int deleteCount = -1;
        switch (MATCHER.match(uri)) {

            case CONV_LIST:
                deleteCount = delete();
                break;
            default:
                new UnsupportedOperationException("Error");
                break;
        }
        return deleteCount;
    }

    private int delete() {
        return dbAdadpter.deleteConv();
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}

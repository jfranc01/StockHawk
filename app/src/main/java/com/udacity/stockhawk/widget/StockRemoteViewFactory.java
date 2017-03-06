package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.StockDetailsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joel on 2017-03-03.
 */

public class StockRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private int mAppWidgetId;
    private Cursor mCursor;

    public StockRemoteViewFactory(Context context, Intent intent){

        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

        //Nothing to do here

    }

    @Override
    public void onDataSetChanged() {
        if(mCursor != null){
            mCursor.close();
        }

        // This method is called by the app hosting the widget (e.g., the launcher)
        // However, our ContentProvider is not exported so it doesn't have access to the
        // data. Therefore we need to clear (and finally restore) the calling identity so
        // that calls use our process and permission
        final long identityToken = Binder.clearCallingIdentity();
        mCursor = mContext.getContentResolver().query(Contract.Quote.URI,
                StockDetailsFragment.STOCK_DETAIL_COLUMNS,
                null,
                null,
                null);
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
    }

    @Override
    public int getCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_list_item);
        if (position == AdapterView.INVALID_POSITION ||
                mCursor == null || !mCursor.moveToPosition(position)) {
            return null;
        }
        remoteViews.setTextViewText(R.id.symbol, mCursor.getString(StockDetailsFragment.COLUMN_SYMBOL));
        remoteViews.setTextViewText(R.id.price, mCursor.getString(StockDetailsFragment.COLUMN_PRICE));
        remoteViews.setTextViewText(R.id.change, mCursor.getString(StockDetailsFragment.COLUMN_PERCENTAGE_CHANGE));

        //create the fill intent
        Intent fillIntent = new Intent();
        fillIntent.setData(Contract.Quote.makeUriForStock(mCursor.getString(StockDetailsFragment.COLUMN_SYMBOL)));
        remoteViews.setOnClickFillInIntent(R.id.widget_list_root, fillIntent);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

package com.udacity.stockhawk;

import android.app.Fragment;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * A placeholder fragment containing a simple view.
 */
public class StockDetailsFragment extends Fragment {

    private Uri mUri;
    private String LOG_TAG = this.getClass().getSimpleName();
    private LineChart mLineChart;
    //create a projection for the columns
    public static final String[] STOCK_DETAIL_COLUMNS = {
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE,
            Contract.Quote.COLUMN_HISTORY
    };

    public static final int COLUMN_SYMBOL = 0;
    public static final int COLUMN_PRICE = 1;
    public static final int COLUMN_ABSOLUTE_CHANGE = 2;
    public static final int COLUMN_PERCENTAGE_CHANGE = 3;
    public static final int COLUMN_HISTORY = 4;

    public StockDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //retrieve the arguments that were passed into the fragement
        View rootView = inflater.inflate(R.layout.fragment_stock_details, container, false);
        //find the line chart
        mLineChart = (LineChart) rootView.findViewById(R.id.line_chart);
        //get the arguments
        Bundle arguments = getArguments();
        if(arguments != null){
            mUri = arguments.getParcelable(StockDetails.URI_ARGUMENT);
            Timber.d("Received: " + mUri.toString());
        }
        else{
            Timber.d("Not Arguments Received");
        }

        //use the content resolver to retrieve the data
        Cursor data = getActivity().getContentResolver().query(
                mUri,
                STOCK_DETAIL_COLUMNS,
                null,
                null,
                null);

        //check if the data is not null
        if(data != null && data.moveToFirst()) {

            //get the history string
            String history = data.getString(COLUMN_HISTORY);
            Log.i(LOG_TAG, "History: " + history);

            //we need to send this history data to be parsed to create a list
            //of entry objects
            List entries = processHistory(history);
            LineDataSet trend = new LineDataSet(entries, "Trend");
            trend.setColor(Color.GREEN);
            trend.setAxisDependency(YAxis.AxisDependency.RIGHT);
            LineData lineData = new LineData(trend);
            mLineChart.setData(lineData);
            mLineChart.invalidate();
        }

        return rootView;
    }

    private List<Entry> processHistory(String history){
        List<Entry> entries = new ArrayList<Entry>();
        String lines[] = history.split("\\r?\\n");
        for(int i=0;i<lines.length;i++){
            String values[] = lines[i].split(",");
            Entry entry = new Entry(Float.parseFloat(values[1]), Float.parseFloat(values[1]));
            entries.add(entry);
        }
        return entries;
    }
}

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
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.data.Contract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

/**
 * A placeholder fragment containing a simple view.
 */
public class StockDetailsFragment extends Fragment {

    private Uri mUri;
    private String LOG_TAG = this.getClass().getSimpleName();
    private LineChart mLineChart;
    private TextView mSymbolTextView;
    private TextView mPriceTextView;
    private TextView mAbsoluteChangeTextView;
    private TextView mPercentgeChangeTextView;
    private String[] dates;
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yy");
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

        //find the symbol text view
        mSymbolTextView = (TextView)rootView.findViewById(R.id.symbolValue);
        //find the percentage change text view
        mPercentgeChangeTextView = (TextView)rootView.findViewById(R.id.percentageChangeValue);
        //find the absolute change text view
        mAbsoluteChangeTextView = (TextView)rootView.findViewById(R.id.absolutechangeValue);
        //find the price text view
        mPriceTextView = (TextView)rootView.findViewById(R.id.priceValue);


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
            //get the symbol
            String symbol = data.getString(COLUMN_SYMBOL);
            //String symbol = data.getString();
            //get the price
            String price = data.getString(COLUMN_PRICE);
            //get the % change
            String percentage_change = data.getString(COLUMN_PERCENTAGE_CHANGE);
            //get the absolute change
            String absolute_change = data.getString(COLUMN_ABSOLUTE_CHANGE);
            Log.i(LOG_TAG, "History: " + history);

            //we need to send this history data to be parsed to create a list
            //of entry objects
            List entries = processHistory(history);
            plotChart(entries);
            setDetailsValues(symbol, price, percentage_change, absolute_change);

        }

        return rootView;
    }

    /**
     * Method that process the data points and creates a list of
     * entry objects
     * @param history
     * @return
     */
    private List<Entry> processHistory(String history){
        List<Entry> entries = new ArrayList<Entry>();
        String lines[] = history.split("\\r?\\n");
        dates = new String[lines.length];
        float index = 0f;
        for(int i=lines.length-1;i>0;i--){
            String values[] = lines[i].split(",");
            Entry entry = new Entry(index, Float.parseFloat(values[1]));
            entries.add(entry);
            //save all the dates in an array of string;
            dates[(int)index] = formaDate(values[0]);
            index++;
        }
        return entries;
    }

    private String formaDate(String value) {
        Long dateValue = Long.parseLong(value);
        String dateText = dateFormatter.format(new Date(dateValue));
        return dateText;
    }

    private void plotChart(List entries){
        LineDataSet trend = new LineDataSet(entries, "Trend for the last 12 months");
        trend.setCircleColor(Color.GRAY);
        trend.setCircleColorHole(Color.GRAY);
        //set the color of the trend line
        trend.setColor(Color.BLUE);
        //set the x axis to depend on the left side of the Y axis
        trend.setAxisDependency(YAxis.AxisDependency.LEFT);
        //get a handle to the right side
        YAxis yAxisRight = mLineChart.getAxisRight();
        //get a handle on the left side
        YAxis yAxisLeft = mLineChart.getAxisLeft();
        //remove the right side
        yAxisRight.setEnabled(false);
        yAxisLeft.setEnabled(true);
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setAxisLineWidth(2f);
        yAxisLeft.setAxisLineWidth(2f);
        xAxis.setGridLineWidth(1.5f);
        yAxisLeft.setGridLineWidth(1.5f);
        //Created a formatter for the values
        IAxisValueFormatter axisValueFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dates[(int)value];
            }
        };
        xAxis.setValueFormatter(axisValueFormatter);
        //move the x axis to the bottom of the scren
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        Description description = new Description();
        description.setText("Date");
        mLineChart.setDescription(description);
        //set the line data
        LineData lineData = new LineData(trend);
        lineData.setValueTextColor(Color.BLACK);
        //set the background
        mLineChart.setBackgroundColor(Color.WHITE);
        //set the data
        mLineChart.setData(lineData);
        //diable scrolling
        mLineChart.disableScroll();
        //draw the chart
        mLineChart.invalidate();
    }

    /**
     * Method that sets the TextView values
     * @param symbol
     * @param price
     * @param percentage_change
     * @param absolute_change
     */
    private void setDetailsValues(String symbol, String price, String percentage_change, String absolute_change){
        mSymbolTextView.setText(symbol);
        mPriceTextView.setText(price);
        mPercentgeChangeTextView.setText(percentage_change);
        mAbsoluteChangeTextView.setText(absolute_change);
    }
}

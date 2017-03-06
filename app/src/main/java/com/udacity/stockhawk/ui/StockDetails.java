package com.udacity.stockhawk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.udacity.stockhawk.R;

public class StockDetails extends AppCompatActivity {

    public static final String URI_ARGUMENT = "ur_argument";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Here we get the data that was sent and create a bundle so that we can
        //pass the data to the fragment
        Bundle arguments = new Bundle();
        arguments.putParcelable(URI_ARGUMENT, getIntent().getData());
        //create an instance of the fragment class
        StockDetailsFragment sdf = new StockDetailsFragment();
        //set the argument
        sdf.setArguments(arguments);
        //add the fragment to the container with the manager
        getFragmentManager().beginTransaction().add(R.id.stock_detail_container, sdf).commit();
    }

}

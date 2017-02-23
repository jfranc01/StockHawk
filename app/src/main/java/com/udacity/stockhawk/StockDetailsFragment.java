package com.udacity.stockhawk;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import timber.log.Timber;

/**
 * A placeholder fragment containing a simple view.
 */
public class StockDetailsFragment extends Fragment {

    private Uri mUri;

    public StockDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //retrieve the arguments that were passed into the fragement
        Bundle arguments = getArguments();
        if(arguments != null){
            mUri = arguments.getParcelable(StockDetails.URI_ARGUMENT);
            Timber.d("Received: " + mUri.toString());
        }
        else{
            Timber.d("Not Arguments Received");
        }
        return inflater.inflate(R.layout.fragment_stock_details, container, false);
    }
}

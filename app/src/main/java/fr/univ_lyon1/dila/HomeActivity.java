/*
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Ducros Alix
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package fr.univ_lyon1.dila;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.m039.beacon.keeper.content.BeaconEntity;
import com.m039.beacon.keeper.receiver.BeaconReceiver;

import java.util.ArrayList;
import java.util.List;

import fr.univ_lyon1.dila.model.Beacon;
import fr.univ_lyon1.dila.network.DownloadWebpageTask;

/**
 * Created by Alix Ducros on 24/01/16.
 */

public class HomeActivity extends AppCompatActivity {


    protected ArrayAdapter<String> topicsAdapter ;


    private BeaconReceiver mBeaconReceiver = new BeaconReceiver() {

        @Override
        protected void onFoundBeacon(Context ctx, BeaconEntity beaconEntity) {
            android.util.Log.d("MainActivity", "onFoundBeacon | " + beaconEntity.getIBeacon());
            Beacon foundBeacon = new Beacon(beaconEntity.getUuid(), beaconEntity.getDistance(), "Toto") ;
            BeaconManager.getInstance().updateBeacon(foundBeacon);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hom);

        List<String> topics = new ArrayList<>();

        topicsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, BeaconManager.getInstance().getOrdonnatedNearestBeaconsKeywords());
        BeaconManager.setAdapter(topicsAdapter);


        ListView listTopics = (ListView)findViewById(R.id.listTopics);
        listTopics.setAdapter(topicsAdapter);
        listTopics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), ((TextView)view).getText(), Toast.LENGTH_SHORT).show();
                requestAPI(((TextView)view).getText().toString());
            }
        });

        EditText searchEdit = (EditText)findViewById(R.id.search_edit) ;
        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //Toast.makeText(getApplicationContext(), v.getText(), Toast.LENGTH_SHORT).show();
                    requestAPI(v.getText().toString());
                    return true;
                }
                return false;
            }
        });

    }

    protected void requestAPI(String keywords) {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            //new DownloadWebpageTask().execute("https://www.googleapis.com/books/v1/volumes?q=flowers&key=AIzaSyBOfekFME3DXlS03_AsKNWR6xazgExX60Q");
            //new DownloadWebpageTask().execute("https://www.wikipedia.org/");
            String keywordsWithoutBlanks = keywords.trim().replace(' ', '+');
            DownloadWebpageTask dwt = new DownloadWebpageTask(this);
            dwt.execute("https://www.googleapis.com/books/v1/volumes?q=" + keywordsWithoutBlanks + "&maxResults=40", keywords);


        } else {
            Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
        }

    }

    public void showRequestResultDialog(String result) {

        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(result)
                .setTitle("Result");

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void startCollectionActivity() {
        Intent intent = new Intent(this, CollectionActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();


        mBeaconReceiver.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mBeaconReceiver.unregister(this);
    }

}

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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import fr.univ_lyon1.dila.model.Book;
import fr.univ_lyon1.dila.model.Collection;
import fr.univ_lyon1.dila.model.CollectionManager;

/**
 * Created by Alix Ducros on 24/01/16.
 */

public class HomeActivity extends AppCompatActivity {

    List<String> topics = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hom);
        topics.add("Informatique");
        topics.add("Mathématiques");
        topics.add("Physique");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, topics);
        ListView listTopics = (ListView)findViewById(R.id.listTopics);
        listTopics.setAdapter(adapter);
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
            new DownloadWebpageTask().execute("https://www.googleapis.com/books/v1/volumes?q="+keywordsWithoutBlanks+"&maxResults=40", keywords);


        } else {
            Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
        }

    }

    protected void showRequestResultDialog(String result) {

        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(result)
                .setTitle("Result");

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected  void startCollectionActivity() {
        Intent intent = new Intent(this, CollectionActivity.class);
        startActivity(intent);
    }


    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        private String keywords ;
        @Override
        protected String doInBackground(String... urls) {
            keywords = urls[1] ;
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject objects = new JSONObject(result) ;
                JSONArray volumes = (JSONArray)objects.get("items");

                Collection collection = new Collection() ;

                for(int i = 0 ; i< volumes.length() ; i++) {
                    collection.getDocumentList().add(Book.fromJSON(volumes.getJSONObject(i).getJSONObject("volumeInfo")));
                }
                CollectionManager.getInstance().addCollection(keywords, collection);
                startCollectionActivity();
            } catch (JSONException e) {
                showRequestResultDialog(result);
                Log.w("DiLA", e.toString());
            }
        }

        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(150000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("key", "AIzaSyBOfekFME3DXlS03_AsKNWR6xazgExX60Q");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                if(response > 400) {
                    is = conn.getErrorStream() ;
                }
                 else {
                    is = conn.getInputStream();
                }

                // Convert the InputStream into a string
                String contentAsString = readIt(is);
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {

                if (stream != null) {
                    Writer writer = new StringWriter();

                    char[] buffer = new char[1024];
                    try {
                        Reader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"),1024);
                        int n;
                        while ((n = reader.read(buffer)) != -1) {
                            writer.write(buffer, 0, n);
                        }
                    } finally {
                        stream.close();
                    }
                    return writer.toString();
                } else {
                    return "";
                }
        }
    }

}

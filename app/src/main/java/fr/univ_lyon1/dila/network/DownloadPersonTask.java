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

package fr.univ_lyon1.dila.network;

import android.os.AsyncTask;
import android.util.Log;

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

import fr.univ_lyon1.dila.model.Person;
import fr.univ_lyon1.dila.view.BookCard;

/**
 * Created by Alix Ducros on 03/02/16.
 */
public class DownloadPersonTask extends AsyncTask<String, Void, String> {
    private final BookCard activity;
    private String keywords;

    public DownloadPersonTask(BookCard ctx) {
        this.activity = ctx;
    }

    @Override
    protected String doInBackground(String... keywords) {
        this.keywords = keywords[0];
        // params comes from the execute() call: params[0] is the url.
        try {
            return downloadUrl(keywords[0]);
        } catch (IOException e) {
            return "Unable to retrieve web page. URL may be invalid.";
        }
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject objects = new JSONObject(result);
            JSONArray personnes = (JSONArray) objects.get("itemListElement");

            Person person = null;

            for (int i = 0; i < personnes.length(); i++) {
                person = Person.fromJSON(personnes.getJSONObject(i));
            }

            if (person == null) {
                person = new Person(null, null, null, null);
            }

            activity.startPersonActivity(person);
        } catch (JSONException e) {
            //activity.showRequestResultDialog(result);
            Log.w("DiLA", e.toString());
        }
    }

    private String downloadUrl(String keywords) throws IOException {
        InputStream is = null;

        try {
            String urlString = "https://kgsearch.googleapis.com/v1/entities:search?limit=1&query=";
            String key = "AIzaSyDo4ocHmtSfsNrNvW0LOWrINbZpyJbzSVc";
            String formattedKeywords = keywords.replace(" ", "+");
            URL url = new URL(urlString + formattedKeywords + "&key=" + key);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(150000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            if (response > 400) {
                is = conn.getErrorStream();
            } else {
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
                Reader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 1024);
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
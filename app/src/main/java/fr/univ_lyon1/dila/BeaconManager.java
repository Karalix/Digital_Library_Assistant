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

import android.os.Handler;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.univ_lyon1.dila.model.Beacon;
import fr.univ_lyon1.dila.model.BeaconsAdapter;

/**
 * Created by Alix Ducros on 03/02/16.
 */
public class BeaconManager {

    private static BeaconManager ourInstance = new BeaconManager();
    private static BeaconsAdapter adapter;
    private final int interval = 10000; // 1 Second
    private List<Beacon> beacons;
    private Map<String, Beacon> knownBeacons;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable(){
        public void run() {
            makeBeaconsGetOld();
        }
    };

    private BeaconManager() {
        beacons = new ArrayList<>();
        knownBeacons = new HashMap<>();

        InputStream is = MainApplication.getAppContext().getApplicationContext().getResources().openRawResource(R.raw.beacons);

        try {
            String jsonString = readIt(is);
            JSONArray array = new JSONArray(jsonString);

            for (int i = 0; i < array.length(); i++) {
                JSONObject pair = array.getJSONObject(i);
                JSONArray subTopics = pair.getJSONArray("topics");
                List<String> subTopicsStrings = new ArrayList<>();
                for (int j = 0; j < subTopics.length(); j++) {
                    subTopicsStrings.add(subTopics.getString(j));
                }
                knownBeacons.put(pair.getString("uuid"), new Beacon(pair.getString("uuid"), 0, pair.getString("keyword"), subTopicsStrings));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //handler.postAtTime(runnable, System.currentTimeMillis()+interval);
        handler.postDelayed(runnable, interval);
    }

    public static BeaconManager getInstance() {
        return ourInstance;
    }

    public static void setAdapter(BeaconsAdapter adapter) {
        BeaconManager.adapter = adapter;
    }

    public synchronized List<Beacon> getBeacons() {
        return beacons;
    }

    protected synchronized void makeBeaconsGetOld() {

        //TODO DEBUG
        updateBeacon("fa262fe9-2a96-41bd-8583-af062a1cd8b9", 1);
        updateBeacon("2840e656-d048-4e43-bc1f-6957b8d19c3e", 1);
        updateBeacon("8a682cc5-d702-492e-8c88-30df15a281e9", 1);


        handler.postDelayed(runnable, interval);
        List<Beacon> toDelete = new ArrayList<>();
        for (Beacon entry : this.getBeacons()) {
            entry.incAge();
            if(entry.getAge() > 2) {
                toDelete.add(entry);
            }
        }
        for (Beacon target : toDelete) {
            this.getBeacons().remove(target);
        }

        adapter.clear();
        adapter.addAll(getOrdonnatedNearestBeaconsKeywords());
        adapter.notifyDataSetChanged();
    }

    public void updateBeacon(String uuid, int distance) {
        boolean alreadyExists = false ;
        for (Beacon b : this.getBeacons()) {
            if (b.getId().equals(uuid)) {
                b.resetAge();
                b.setDistance(distance);
                alreadyExists = true ;
            }
        }
        if (!alreadyExists) {
            if (knownBeacons.containsKey(uuid)) {
                //TODO
                this.getBeacons().add(new Beacon(uuid, distance, knownBeacons.get(uuid).getKeyword(), knownBeacons.get(uuid).getSubTopics()));
            }
        }
        adapter.clear();
        adapter.addAll(getOrdonnatedNearestBeaconsKeywords());
        adapter.notifyDataSetChanged();
    }

    public List<Beacon> getOrdonnatedNearestBeaconsKeywords() {
        Collections.sort(this.getBeacons());
        List<Beacon> nearestBeacons ;
        if (this.getBeacons().size() >= 4) {
            nearestBeacons = this.getBeacons().subList(0, 4);
        } else if (this.getBeacons().size() == 0) {
            return new ArrayList<>();
        }else {
            nearestBeacons = this.getBeacons().subList(0, this.getBeacons().size());
        }
        return nearestBeacons;
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

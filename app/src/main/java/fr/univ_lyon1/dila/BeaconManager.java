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
import android.widget.ArrayAdapter;

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

/**
 * Created by Alix Ducros on 03/02/16.
 */
public class BeaconManager {

    private static BeaconManager ourInstance = new BeaconManager();
    private static ArrayAdapter<String> adapter;
    private final int interval = 10000; // 1 Second
    private List<Beacon> beacons;
    private Map<String, String> knownBeacons;
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
                knownBeacons.put(pair.getString("uuid"), pair.getString("keyword"));
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

    public static void setAdapter(ArrayAdapter<String> adapter) {
        BeaconManager.adapter = adapter;
    }

    public List<Beacon> getBeacons() {
        return beacons;
    }

    protected void makeBeaconsGetOld(){

        handler.postDelayed(runnable, interval);
        List<Beacon> toDelete = new ArrayList<>();
        for(Beacon entry : beacons) {
            entry.incAge();
            if(entry.getAge() > 2) {
                toDelete.add(entry);
            }
        }
        for (Beacon target : toDelete) {
            beacons.remove(target);
        }

        adapter.clear();
        adapter.addAll(getOrdonnatedNearestBeaconsKeywords());
        adapter.notifyDataSetChanged();
    }

    public void updateBeacon(String uuid, int distance) {
        boolean alreadyExists = false ;
        for (Beacon b : beacons) {
            if (b.getId().equals(uuid)) {
                b.resetAge();
                b.setDistance(distance);
                alreadyExists = true ;
            }
        }
        if (!alreadyExists) {
            if (knownBeacons.containsKey(uuid)) {
                beacons.add(new Beacon(uuid, distance, knownBeacons.get(uuid)));
            }
        }
        adapter.clear();
        adapter.addAll(getOrdonnatedNearestBeaconsKeywords());
        adapter.notifyDataSetChanged();
    }

    public List<String> getOrdonnatedNearestBeaconsKeywords() {
        Collections.sort(beacons);
        List<Beacon> nearestBeacons ;
        if(beacons.size()>=3) {
            nearestBeacons =  beacons.subList(0,2);
        } else if(beacons.size() == 0) {
            return new ArrayList<>();
        }else {
            nearestBeacons = beacons.subList(0, beacons.size());
        }
        List<String> results = new ArrayList<>() ;
        for(Beacon beacon : nearestBeacons) {
            results.add(beacon.getKeyword());
        }
        return results ;
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

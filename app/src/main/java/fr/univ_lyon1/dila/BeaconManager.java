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

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import fr.univ_lyon1.dila.model.Beacon;
import fr.univ_lyon1.dila.model.Collection;

/**
 * Created by Alix Ducros on 03/02/16.
 */
public class BeaconManager {

    private List<Beacon> beacons ;

    private final int interval = 10000; // 1 Second
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable(){
        public void run() {
            makeBeaconsGetOld();
        }
    };

    private static BeaconManager ourInstance = new BeaconManager();
    private static ArrayAdapter<String> adapter;

    public static BeaconManager getInstance() {
        return ourInstance;
    }

    private BeaconManager() {
        beacons = new ArrayList<>();

        //handler.postAtTime(runnable, System.currentTimeMillis()+interval);
        handler.postDelayed(runnable, interval);
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

    public void updateBeacon(Beacon beacon) {
        boolean alreadyExists = false ;
        for (Beacon b : beacons) {
            if (b.getId().equals(beacon.getId())) {
                b.resetAge();
                b.setDistance(beacon.getDistance());
                alreadyExists = true ;
            }
        }
        if (!alreadyExists) {
            beacons.add(beacon);
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

    private class BeaconCleaner extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }
    }

}

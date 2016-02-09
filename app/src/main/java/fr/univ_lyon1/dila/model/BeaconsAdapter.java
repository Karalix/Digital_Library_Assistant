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

package fr.univ_lyon1.dila.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import fr.univ_lyon1.dila.R;

/**
 * Created by Alix Ducros on 09/02/16.
 */
public class BeaconsAdapter extends ArrayAdapter {
    public BeaconsAdapter(Context context, int resource, List<Beacon> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == 0) {
            View v = View.inflate(getContext(), R.layout.nearest_beacon_item, null);
            ((TextView) v.findViewById(R.id.beacon_main_topic)).setText(((Beacon) getItem(position)).getKeyword());
            ((TextView) v.findViewById(R.id.beacon_sub_topics)).setText(((Beacon) getItem(position)).getSubTopicsFormattedString());
            return v;
        }
        if (position == 1) {
            View v = View.inflate(getContext(), R.layout.second_nearest_beacon_item, null);
            ((TextView) v.findViewById(R.id.beacon_main_topic)).setText(((Beacon) getItem(position)).getKeyword());
            ((TextView) v.findViewById(R.id.beacon_sub_topics)).setText(((Beacon) getItem(position)).getSubTopicsFormattedString());
            return v;
        }
        View v = View.inflate(getContext(), android.R.layout.simple_list_item_1, null);
        ((TextView) v.findViewById(android.R.id.text1)).setText(((Beacon) getItem(position)).getKeyword());
        return v;
    }
}

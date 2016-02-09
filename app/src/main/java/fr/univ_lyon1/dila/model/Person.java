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

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Alix Ducros on 05/02/16.
 */
public class Person implements Serializable {

    private String name;

    private String description;

    private String thumbnail;

    private String wikiUrl;

    public Person(String name, String description, String thumbnail, String wikiUrl) {
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
        this.wikiUrl = wikiUrl;
    }

    public static Person fromJSON(JSONObject jsonObject) {
        String name = null;
        String description = null;
        String thumbnail = null;
        String wikiUrl = null;
        try {
            if (jsonObject.has("result")) {
                JSONObject result = jsonObject.getJSONObject("result");
                if (result.has("name")) {
                    name = result.getString("name");
                }
                if (result.has("detailedDescription")) {
                    JSONObject detailedDescription = result.getJSONObject("detailedDescription");
                    if (detailedDescription.has("articleBody")) {
                        description = detailedDescription.getString("articleBody");
                    }
                    if (detailedDescription.has("url")) {
                        wikiUrl = detailedDescription.getString("url");
                    }
                }
                if (result.has("image")) {
                    JSONObject imageLinks = result.getJSONObject("image");
                    if (imageLinks.has("contentUrl")) {
                        thumbnail = imageLinks.getString("contentUrl");
                    }
                }
            }

        } catch (JSONException e) {
            Log.w("DiLA", e.toString());
        }
        return new Person(name, description, thumbnail, wikiUrl);
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getWikiUrl() {
        return wikiUrl;
    }
}

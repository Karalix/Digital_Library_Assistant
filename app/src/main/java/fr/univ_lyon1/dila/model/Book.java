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


import android.support.v4.app.Fragment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.univ_lyon1.dila.view.BookCard;

/**
 * Created by Alix Ducros on 27/01/16.
 */
public class Book extends Document{
    protected List<String> authors ;
    protected int nbPages ;

    public Book(String title, String date, String synopsis, String thumbnail, List<String> authors, int nbPages) {
        super(title, date, synopsis, thumbnail);
        this.authors = authors;
        this.nbPages = nbPages;
    }

    public List<String> getAuthor() {
        return authors;
    }

    public int getNbPages() {
        return nbPages;
    }

    @Override
    public Fragment getCard() {

        BookCard card = new BookCard() ;
        card.setBook(this);
        return card ;
    }

    public static Book fromJSON(JSONObject jsonObject) {

        List<String> authors = new ArrayList<>();
        String title = null;
        String date = null;
        String synopsis = null;
        int nbPages = 0;
        String thumbnail = null ;
        try {
            if (jsonObject.has("authors")) {
                for (int j = 0; j < jsonObject.getJSONArray("authors").length(); j++) {
                    authors.add(jsonObject.getJSONArray("authors").getString(j));
                }
            }
            if (jsonObject.has("pageCount")) {
                nbPages = jsonObject.getInt("pageCount");
            }
            if (jsonObject.has("title")) {
                title = jsonObject.getString("title");
            }
            if (jsonObject.has("publishedDate")) {
                date = jsonObject.getString("publishedDate");
            }
            if (jsonObject.has("description")) {
                synopsis = jsonObject.getString("description");
            }
            if(jsonObject.has("imageLinks")) {
                JSONObject imageLinks = jsonObject.getJSONObject("imageLinks");
                if(imageLinks.has("thumbnail")) {
                    thumbnail = imageLinks.getString("thumbnail");
                }
            }
        } catch (JSONException e) {
            Log.w("DiLA", e.toString());
        }
        return new Book(title, date, synopsis,thumbnail, authors, nbPages);
    }
}

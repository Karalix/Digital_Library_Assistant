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

package fr.univ_lyon1.dila.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.univ_lyon1.dila.R;
import fr.univ_lyon1.dila.model.Book;
import fr.univ_lyon1.dila.network.DownloadImageTask;

/**
 * Created by Alix Ducros on 29/01/16.
 */
public class BookCard extends Fragment {

    private Book book ;

    public void setBook(Book book) {
        this.book = book ;
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        String authors = "";
        for (String author : book.getAuthor()) {
            authors += author ;
            authors += ", ";
        }

        String baseInfos = authors + book.getDate() + ", " + book.getNbPages() + " pages.";
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.book_card, container, false);
        ((TextView) rootView.findViewById(R.id.book_title)).setText(book.getTitle());
        ((TextView) rootView.findViewById(R.id.base_infos)).setText(baseInfos);
        ((TextView) rootView.findViewById(R.id.synopsis)).setText(book.getSynopsis());

        //Download the thumbnail in async
        new DownloadImageTask((ImageView) rootView.findViewById(R.id.thumbnail))
                .execute(book.getThumbnail());
        return rootView;
    }
}

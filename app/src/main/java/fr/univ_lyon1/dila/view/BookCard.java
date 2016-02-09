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

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import fr.univ_lyon1.dila.PersonActivity;
import fr.univ_lyon1.dila.R;
import fr.univ_lyon1.dila.model.Book;
import fr.univ_lyon1.dila.model.Person;
import fr.univ_lyon1.dila.network.DownloadImageTask;
import fr.univ_lyon1.dila.network.DownloadPersonTask;

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


        String baseInfos = book.getDate() + ", " + book.getNbPages() + " pages.";
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.book_card, container, false);
        ((TextView) rootView.findViewById(R.id.book_title)).setText(book.getTitle());
        ((TextView) rootView.findViewById(R.id.base_infos)).setText(baseInfos);
        ((TextView) rootView.findViewById(R.id.synopsis)).setText(book.getSynopsis());

        for (final String author : book.getAuthor()) {
            TextView authorTextView = new TextView(getContext());
            authorTextView.setText(author);
            //Le nom des auteurs est mis en bleu souligné
            //afin que l'utilisateur l'assimile à un lien hypetexte
            //et comprenne qu'il peut cliquer dessus
            authorTextView.setTextColor(Color.BLUE);
            authorTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

            authorTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.w("Yo man !", author);
                    DownloadPersonTask task = new DownloadPersonTask(BookCard.this);
                    task.execute(author);

                }
            });
            ((LinearLayout) rootView.findViewById(R.id.authors)).addView(authorTextView);
        }

        //Download the thumbnail in async
        new DownloadImageTask((ImageView) rootView.findViewById(R.id.thumbnail))
                .execute(book.getThumbnail());
        return rootView;
    }

    public void startPersonActivity(Person person) {
        Intent intent = new Intent(this.getActivity(), PersonActivity.class);
        intent.putExtra("person", person);
        startActivity(intent);
    }
}

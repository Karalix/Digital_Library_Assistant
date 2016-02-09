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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import fr.univ_lyon1.dila.model.Person;
import fr.univ_lyon1.dila.network.DownloadImageTask;

public class PersonActivity extends AppCompatActivity {


    private Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        person = (Person) getIntent().getExtras().get("person");

        if (person.getName() != null) {
            ((TextView) findViewById(R.id.person_name)).setText(person.getName());
        } else {
            ((TextView) findViewById(R.id.person_name)).setText("Non trouvé");
        }

        if (person.getDescription() != null) {
            ((TextView) findViewById(R.id.synopsis)).setText(person.getDescription());
        } else {
            ((TextView) findViewById(R.id.synopsis)).setText("Non trouvé");
        }

        //Download the thumbnail in async
        if (person.getThumbnail() != null) {
            new DownloadImageTask((ImageView) findViewById(R.id.portrait))
                    .execute(person.getThumbnail());
        }

        if (person.getWikiUrl() != null) {
            Button button = ((Button) findViewById(R.id.person_wiki_button));
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(person.getWikiUrl()));
                    startActivity(browserIntent);
                }
            });
        }
    }
}

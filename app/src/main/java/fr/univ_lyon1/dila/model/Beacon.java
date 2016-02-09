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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alix Ducros on 03/02/16.
 */
public class Beacon implements Comparable<Beacon>{
    private String id;
    private int distance;
    private int age;
    private String keyword;
    private List<String> subTopics;

    public Beacon(String id, int distance, String keyword, List<String> subTopics) {
        this.id = id;
        this.distance = distance;
        this.keyword = keyword ;
        age = 0 ;
        this.subTopics = subTopics;
        //TODO
        this.subTopics = new ArrayList<>();
        this.subTopics.add("Coucou");
        this.subTopics.add("Toto");
        this.subTopics.add("Tata");
        this.subTopics.add("Titi");
    }

    public void resetAge() {
        age = 0 ;
    }

    public void incAge() {
        age ++ ;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    public String getKeyword() {
        return keyword;
    }

    public List<String> getSubTopics() {
        return subTopics;
    }

    public String getSubTopicsFormattedString() {
        String ret = "";
        for (String subTopic : subTopics) {
            ret += "- " + subTopic + "\n";
        }
        return ret;
    }

    @Override
    public int compareTo(Beacon another) {
        return this.getDistance() - another.getDistance() ;
    }
}

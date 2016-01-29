package fr.univ_lyon1.lida.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alix on 27/01/16.
 */
public class Collection {
    protected List<Thing> documentList ;

    public Collection() {
        documentList = new ArrayList<Thing>();
    }

    public List<Thing> getDocumentList() {
        return documentList;
    }
}

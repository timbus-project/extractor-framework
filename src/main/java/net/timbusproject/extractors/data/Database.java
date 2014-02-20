package net.timbusproject.extractors.data;

import net.timbusproject.extractors.pojo.RequestExtractionList;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: miguel
 * Date: 02-12-2013
 * Time: 13:30
 * To change this template use File | Settings | File Templates.
 */
public class Database {

    private Map<Long, RequestExtractionList> extractions;

    public Database(){
        if (extractions == null) {
            extractions = new Hashtable<>();
        }
    }

    public synchronized long add(RequestExtractionList extractionsList) {
        long key = extractions.keySet().size();
        extractions.put(key, extractionsList);
        return key;
    }

    public Set<Long> getKeySet() {
        return extractions.keySet();
    }

    public boolean containsKey(long id){
        return extractions.containsKey(id);
    }

    public RequestExtractionList get(long id){
        return extractions.get(id);
    }
}

package tqs.midterm.project.cache;

import tqs.midterm.project.model.City;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cache {
    private static Logger logger = Logger.getLogger(Cache.class.getName());
    private static long CACHE_TTL = 3600000;
    private HashMap<City, Long> map = new HashMap<>();
    private int numberOfRequests;
    private int numberOfHits;
    private int numberOfMisses;

    // constructor
    public Cache(){
        this.numberOfMisses = 0;
        this.numberOfHits = 0;
        this.numberOfRequests = 0;
    }

    // getters and setters
    public static long getCacheTtl() {
        return CACHE_TTL;
    }

    public static void setCacheTtl(long cacheTtl) {
        CACHE_TTL = cacheTtl;
    }

    public City get(double latitude, double longitude) {
        this.numberOfRequests++;
        for(City c : map.keySet()){
            if(System.currentTimeMillis() - map.get(c) > CACHE_TTL){
                map.remove(c);
                logger.log(Level.WARNING, "removed item " + c);
            } else{
                if(c.getLatitude() == latitude && c.getLongitude() == longitude){
                    this.numberOfHits++;
                    logger.log(Level.WARNING, "found item " + c);
                    return c;
                }
                logger.log(Level.WARNING,c.getLatitude() + "longitude" + c.getLongitude() + "instead of" + c.getLatitude() + c.getLongitude() );
            }
        }
        logger.log(Level.WARNING, "nothing changed");
        this.numberOfMisses++;
        return null;
    }

    public void save(City c, long ttl) {
        this.map.put(c,ttl) ;
        logger.log(Level.WARNING, "saved " + c + " in cache");
    }

    public int getNumberOfRequests() {
        return numberOfRequests;
    }

    public int getNumberOfHits() {
        return numberOfHits;
    }

    public int getNumberOfMisses() {
        return numberOfMisses;
    }

    public int getRatio(){
        if(numberOfMisses == 0){
            return numberOfHits;
        }
        return numberOfHits/numberOfMisses;
    }
}

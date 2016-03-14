package com.netflix.playback.features.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An implementation of {@link DiagnosticService}
 * This class has a request store that stores all requests across all completed periods.
 * It also has a working map that stores the requests in the ongoing period.
 * At the end of each period, the working map will be saved into the request store keyed by
 * the period number.
 */
public class MyDiagnosticService extends DiagnosticService {

    /**
     * The store that holds all requests.
     * The key of the first map is the period number.
     * The key of the nested map is the request key.
     */
    private final Map<Integer, Map<String, List<Object>>> requestStore = new ConcurrentHashMap<>(new HashMap<Integer, Map<String, List<Object>>>());

    /**
     * The map that holds the incoming requests for the current period.
     */
    private Map<String, List<Object>> workingMap = Collections.synchronizedMap(new HashMap<String, List<Object>>());

    /**
     * The period counter.
     */
    private int periodCounter = 0;

    public MyDiagnosticService(Timer timer, int numCompletedPeriods) {
        super(timer, numCompletedPeriods);
    }
    
    /**
     *  {@inheritDoc}
     */
    @Override
    public int count(String key) {
        int currentPeriod = periodCounter;
        if (requestStore.get(currentPeriod) != null) {
            List<Object> list = requestStore.get(currentPeriod).get(key);
            if (list != null) {
                return list.size();
            }
        }

        return 0;
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public int rate(String key) {
        // Save the current period counter because it might be modified by the timer.
        int currentPeriod = periodCounter;

        if (currentPeriod < 2) {
            return 0;
        }

        return count(currentPeriod, key) - count(currentPeriod - 1, key);
    }
    
    /**
     *  {@inheritDoc}
     */
    @Override
    public int avg(String key) {
        // Save the current period counter because it might be modified by the timer
        int currentPeriod = periodCounter;

        if (currentPeriod < 2) {
            return 0;
        }

        int totalRate = 0;
        int counter = currentPeriod;
        while (counter > 1) {
            totalRate += count(counter, key) - count(counter - 1, key);
            counter--;
        }
        Util.printWithTime("\t\t " + key + " : total rate " + totalRate + ", current period :" + currentPeriod);
        return totalRate/(currentPeriod - 1);
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public void log(String key, Object request) {
        synchronized (workingMap) {
            if (!workingMap.containsKey(key)) {
                workingMap.put(key, new ArrayList<Object>());
            }
            workingMap.get(key).add(request);
        }
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public List<Object> get(String key) {
        int currentPeriod = periodCounter;

        Map<String, List<Object>> map = requestStore.get(currentPeriod);

        return (map == null) ? null : requestStore.get(periodCounter).get(key);
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public Map<Integer, List<Object>> getAll(String key) {
        Map<Integer, List<Object>> requests = new HashMap<Integer, List<Object>>();

        // NOTE: Iterating through concurrent HashMap might not guarantee the thread
        //       see the new updates to the map.
        for (Map.Entry<Integer, Map<String, List<Object>>> entry : requestStore.entrySet()) {
            if (entry.getValue() != null) {
                requests.put(entry.getKey(), entry.getValue().get(key));
            } else {
                requests.put(entry.getKey(), null);
            }
        }

        return requests;
    }

    /**
     * When timer is up, save the requests in working map into the store.
     *
     * NOTE: workingMap is a synchronized map. Ideally, we should copy it
     *       into a non-synchronized map and save it into the request store
     *       for fast access by multiple client threads later. But workingMap
     *       could be huge, copy it into another non-synchronized map
     *       could take a long time. So here I chose to save it directly
     *       into the request store s.t. doAction() does not hold up the
     *       timer thread and the data is immediately available for access
     *       by the client threads.
     */
    @Override
    public void doAction() {
        if (periodCounter < numCompletedPeriods) {
            periodCounter++;
            Util.printWithTime("===========================");
            Util.printWithTime("Completed period " + periodCounter);
            Util.printWithTime("===========================");
            requestStore.put(periodCounter, workingMap);
            workingMap = Collections.synchronizedMap(new HashMap<String, List<Object>>());
            if (periodCounter == numCompletedPeriods) {
                Util.printWithTime("Done. Now stop the timer");
                timer.stop();
            }
        }
    }

    /**
     * @param periodCounter the period
     * @param key
     * @return the total count for the given completed period
     */
    private int count(int periodCounter, String key) {
        List<Object> list = requestStore.get(periodCounter).get(key);
        return (list == null) ? 0 : list.size();
    }
}

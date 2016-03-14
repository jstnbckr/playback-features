package com.netflix.playback.features.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class MyPlaybackDiagnostics extends PlaybackDiagnostics{

    private String key = null;

    public MyPlaybackDiagnostics(String key, DiagnosticService diagnosticService) {
        super(diagnosticService);
        this.key = key;
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public void log(PlaybackRequest request){
        Util.printWithTime("\t\t" + key + " : Log request (" + request + ")");
        diagnosticService.log(key, request);
    }
    
    /**
     *  {@inheritDoc}
     */
    @Override
    public int requestCount() {
        return diagnosticService.count(key);
    }
        
    /**
     *  {@inheritDoc}
     */
    @Override
    public int avgRateRequestsPerCountry(String country) {
        if (country != null) {

            int prevCount = -1;
            int totalRate = 0;

            Map<Integer, List<Object>> requests = diagnosticService.getAll(key);

            TreeSet sortedSet = new TreeSet(requests.keySet());
            Iterator it = sortedSet.iterator();

            while (it.hasNext()) {
                List<Object> list = requests.get(it.next());
                int curCount = 0;
                if (list != null) {
                    for (Object obj : list) {
                        PlaybackRequest request = (PlaybackRequest) obj;
                        if (country.equals(request.getCountry())) {
                            curCount++;
                        }
                    }
                }
                if (prevCount != -1) {
                    totalRate += (curCount - prevCount);
                }
                Util.printWithTime("current count : " + curCount + ", total rate : " + totalRate);
                prevCount = curCount;
            }

            return (requests.size() > 1) ? totalRate/(requests.size()-1) : 0;
        }

        return 0;
    }
    
    /**
     *  {@inheritDoc}
     */
    @Override
    public int requestCount(int viewableId) {
        int count = 0;
        List<Object> list = diagnosticService.get(key);
        if (list != null) {
            for (Object obj : list) {
                PlaybackRequest request = (PlaybackRequest) obj;
                if (viewableId == request.getViewableId()) {
                    count++;
                }
            }
        }
        return count;
    }
    
    /**
     *  {@inheritDoc}
     */
    @Override
    public int uniqueCustomerCount() {
        Set<String> customerSet = new HashSet<String>();
        List<Object> list = diagnosticService.get(key);
        if (list != null) {
            for (Object obj : list) {
                customerSet.add(((PlaybackRequest) obj).getCustomerId());
            }
        }
        return customerSet.size();
    }
    
    /**
     *  {@inheritDoc}
     */
    @Override
    public int uniqueViewableCount() {
        Set<Integer> idSet = new HashSet<Integer>();
        List<Object> list = diagnosticService.get(key);
        if (list != null) {
            for (Object obj : list) {
                idSet.add(((PlaybackRequest) obj).getViewableId());
            }
        }
        return idSet.size();
    }

    /**
     *  {@inheritDoc}
     */
    public int rate() {
        return diagnosticService.rate(key);
    }

    /**
     *  {@inheritDoc}
     */
    public int avgRate() {
        return diagnosticService.avg(key);
    }
}

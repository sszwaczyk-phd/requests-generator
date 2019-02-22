package pl.sszwaczyk.stats;

import pl.sszwaczyk.domain.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Statistics {

    private static Statistics instance = null;

    private Map<Service, Stats> stats;

    private Statistics() {
        stats = new ConcurrentHashMap<>();
    }

    public static synchronized Statistics getInstance() {
        if(instance == null) {
            instance = new Statistics();
        }
        return instance;
    }

    public Map<Service, Stats> getStats() {
        return stats;
    }

    public void updateSuccess(Service service, long timeOfRealization) {
        Stats statsForService = stats.get(service);
        if(statsForService == null) {
            statsForService = new Stats();
            stats.put(service, statsForService);
        }
        statsForService.updateSuccess(timeOfRealization);
    }

    public void updateFailed(Service service) {
        Stats statsForService = stats.get(service);
        if(statsForService == null) {
            statsForService = new Stats();
            stats.put(service, statsForService);
        }
        statsForService.updateFailed();
    }

}

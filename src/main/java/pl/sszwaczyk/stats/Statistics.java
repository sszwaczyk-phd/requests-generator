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

    public void addSuccessfull(Service service) {
        Stats statsForService = stats.get(service);
        if(statsForService == null) {
            statsForService = new Stats();
            statsForService.setSuccessfull(1);
            stats.put(service, statsForService);
        } else {
            statsForService.addSuccesfull();
        }
    }

    public void addFailed(Service service) {
        Stats statsForService = stats.get(service);
        if(statsForService == null) {
            statsForService = new Stats();
            statsForService.setFailed(1);
            stats.put(service, statsForService);
        } else {
            statsForService.addFailed();
        }
    }
}

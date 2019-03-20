package pl.sszwaczyk.stats;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Stats {

    private long generatedRequests;
    private long pending;
    private long success;
    private long failed;

    //times
    private List<Long> timesOfRealizations = new ArrayList<>(); //miliseconds
    private long totalTimeOfRealization;
    private long averageRealizationTime; //miliseconds

    public void updateGenerated() {
        generatedRequests++;
    }

    public void updatePending() {
        pending++;
    }

    public void updateSuccess(Long timeOfRealization) {
        pending -=1;
        success +=1;
        timesOfRealizations.add(timeOfRealization);
        totalTimeOfRealization += timeOfRealization;
        averageRealizationTime = totalTimeOfRealization / this.success;
    }

    public void updateFailed() {
        pending -=1;
        failed += 1;
    }

}

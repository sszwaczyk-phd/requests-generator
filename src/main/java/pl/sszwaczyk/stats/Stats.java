package pl.sszwaczyk.stats;

import lombok.Data;

@Data
public class Stats {

    private long successfull;
    private long failed;

    public void addSuccesfull() {
        successfull += 1;
    }

    public void addFailed() {
        failed += 1;
    }
}

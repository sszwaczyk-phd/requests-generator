package pl.sszwaczyk.stats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaveStatisticsRunnable implements Runnable {

    private final Logger log = LoggerFactory.getLogger(SaveStatisticsRunnable.class);

    private String statsFile;

    public SaveStatisticsRunnable(String statsFile) {
        this.statsFile = statsFile;
    }

    @Override
    public void run() {
        log.info("Saving statistics to file on exit...");
        Statistics.getInstance().snapshot(statsFile);
    }

}

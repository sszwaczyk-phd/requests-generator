package pl.sszwaczyk.stats;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SaveStatisticsRunnable implements Runnable {

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

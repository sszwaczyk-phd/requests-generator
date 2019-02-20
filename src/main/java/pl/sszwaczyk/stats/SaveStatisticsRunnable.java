package pl.sszwaczyk.stats;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class SaveStatisticsRunnable implements Runnable {

    private String statsFile;

    public SaveStatisticsRunnable(String statsFile) {
        this.statsFile = statsFile;
    }

    @Override
    public void run() {
        StringBuilder sb = new StringBuilder();
        Statistics statistics = Statistics.getInstance();
        statistics.getStats().forEach((service, statsForService) -> {
            sb.append("Stats for service " + service.getId());
            sb.append("Completed successfully = " + statsForService.getSuccessfull());
            sb.append("Failed = " + statsForService.getFailed());
        });

        try (PrintWriter out = new PrintWriter(statsFile)) {
            out.println(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}

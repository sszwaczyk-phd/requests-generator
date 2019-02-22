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
            sb.append("Stats for service " + service.getId() + "\n");
            sb.append("Genreated requests = " + statsForService.getGeneratedRequests() + "\n");
            sb.append("Completed successfully = " + statsForService.getSuccess() + "\n");
            sb.append("Failed = " + statsForService.getFailed() + "\n");
            sb.append("Total time of realization = " + statsForService.getTotalTimeOfRealization() + " ms\n" );
            sb.append("Avg. time of realization = " + statsForService.getAverageRealizationTime() + " ms\n");
        });

        try (PrintWriter out = new PrintWriter(statsFile)) {
            out.println(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}

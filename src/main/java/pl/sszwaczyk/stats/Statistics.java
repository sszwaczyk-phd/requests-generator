package pl.sszwaczyk.stats;

import jersey.repackaged.com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.sszwaczyk.domain.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Statistics {

    private final Logger log = LoggerFactory.getLogger(Statistics.class);

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

    synchronized public void updateGenerated(Service service) {
        Stats statsForService = stats.get(service);
        if(statsForService == null) {
            statsForService = new Stats();
            stats.put(service, statsForService);
        }
        statsForService.updateGenerated();
    }

    synchronized public void updatePending(Service service) {
        Stats statsForService = stats.get(service);
        if(statsForService == null) {
            statsForService = new Stats();
            stats.put(service, statsForService);
        }
        statsForService.updatePending();
    }

    synchronized public void updateSuccess(Service service, long timeOfRealization) {
        Stats statsForService = stats.get(service);
        if(statsForService == null) {
            statsForService = new Stats();
            stats.put(service, statsForService);
        }
        statsForService.updateSuccess(timeOfRealization);
    }

    synchronized public void updateFailed(Service service) {
        Stats statsForService = stats.get(service);
        if(statsForService == null) {
            statsForService = new Stats();
            stats.put(service, statsForService);
        }
        statsForService.updateFailed();
    }

    public void snapshot(String statsFile) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Statistics");

        sheet.createRow(1).createCell(0).setCellValue("Generated");
        sheet.createRow(2).createCell(0).setCellValue("Pending");
        sheet.createRow(3).createCell(0).setCellValue("Success");
        sheet.createRow(4).createCell(0).setCellValue("Failed");
        sheet.createRow(5).createCell(0).setCellValue("Total time [ms]");
        sheet.createRow(6).createCell(0).setCellValue("Avg. time [ms]");

        Map<Service, Stats> stats = Statistics.getInstance().getStats();
        ArrayList<Service> services = Lists.newArrayList(stats.keySet().iterator());
        Row row0 = sheet.createRow(0);
        for(int i = 0; i < services.size(); i++) {
            Service service = services.get(i);
            Stats statsForService = stats.get(service);
            row0.createCell(i + 1).setCellValue(service.getId());
            sheet.getRow(1).createCell(i + 1).setCellValue(statsForService.getGeneratedRequests());
            sheet.getRow(2).createCell(i + 1).setCellValue(statsForService.getPending());
            sheet.getRow(3).createCell(i + 1).setCellValue(statsForService.getSuccess());
            sheet.getRow(4).createCell(i + 1).setCellValue(statsForService.getFailed());
            sheet.getRow(5).createCell(i + 1).setCellValue(statsForService.getTotalTimeOfRealization());
            sheet.getRow(6).createCell(i + 1).setCellValue(statsForService.getAverageRealizationTime());
        }

        try (FileOutputStream fos = new FileOutputStream(statsFile)) {
            workbook.write(fos);
            log.info("Statitstics save to file " + statsFile);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Cannot save statistics to file " + statsFile + " because " + e.getMessage());
        }
    }

}

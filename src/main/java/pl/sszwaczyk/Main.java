package pl.sszwaczyk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.client.ClientProperties;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import pl.sszwaczyk.cmd.CmdLineSettings;
import pl.sszwaczyk.domain.Service;
import pl.sszwaczyk.stats.Stats;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        CmdLineSettings settings = new CmdLineSettings();
        CmdLineParser parser = new CmdLineParser(settings);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            parser.printUsage(System.out);
            System.exit(1);
        }

        List<Service> services = readServicesFromFile(settings.getServicesFile());
        int size = services.size();
        Map<Service, Stats> stats = initStats(services);

        Random random = new Random();
        Long time = settings.getTime();
        long currentSeconds = System.currentTimeMillis() / 1000;
        Long endTime = currentSeconds + time;


        while(endTime > (System.currentTimeMillis() / 1000)) {

            Service service = services.get(random.nextInt(size));
            Stats statsForService = stats.get(service);

            Client client = ClientBuilder.newClient();
            client.property(ClientProperties.CONNECT_TIMEOUT, 2000);

            WebTarget resource = client.target("http://" + service.getIp() + ":" + service.getPort() + service.getPath());
            Invocation.Builder request = resource.request();
            try {
                Response response = request.get();
                if(response.getStatus() >= 200 || response.getStatus() < 300) {
                    statsForService.setSuccessfull(statsForService.getSuccessfull() + 1);
                    System.out.println("Request for service " + service.getId() + " completed successfully");
                } else {
                    statsForService.setFailed(statsForService.getFailed() + 1);
                    System.out.println("Request for service " + service.getId() + " failed");
                }
            } catch (Exception ex) {
                statsForService.setFailed(statsForService.getFailed() + 1);
                System.out.println("Request for service " + service.getId() + " failed");
            }

            Thread.sleep(2000);
        }

        printStats(stats);

    }

    private static void printStats(Map<Service, Stats> stats) {
        stats.entrySet().forEach(entry -> {
            Service service = entry.getKey();
            Stats statsForService = entry.getValue();
            System.out.println("Stats for service " + service.getId());
            System.out.println("Completed successfully = " + statsForService.getSuccessfull());
            System.out.println("Failed = " + statsForService.getFailed());
        });
    }

    private static Map<Service, Stats> initStats(List<Service> services) {
        Map<Service, Stats> stats = new HashMap<>();
        services.forEach(s -> stats.put(s, new Stats()));
        return stats;
    }

    private static List<Service> readServicesFromFile(String servicesFile) throws IOException {
        File file = new File(servicesFile);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(file, new TypeReference<List<Service>>(){});
    }

}

package pl.sszwaczyk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.client.ClientProperties;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import pl.sszwaczyk.cmd.CmdLineSettings;
import pl.sszwaczyk.domain.Service;
import pl.sszwaczyk.stats.SaveStatisticsRunnable;
import pl.sszwaczyk.stats.Statistics;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.List;
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

        Thread saveStatsThread = new Thread(new SaveStatisticsRunnable(settings.getStatsFile()));
        Runtime.getRuntime().addShutdownHook(saveStatsThread);

        List<Service> services = readServicesFromFile(settings.getServicesFile());
        int size = services.size();

        Random random = new Random();
        Long time = settings.getTime();
        long currentSeconds = System.currentTimeMillis() / 1000;
        Long endTime = currentSeconds + time;

        while(endTime > (System.currentTimeMillis() / 1000)) {

            Service service = services.get(random.nextInt(size));

            Client client = ClientBuilder.newClient();
            client.property(ClientProperties.CONNECT_TIMEOUT, 2000);

            WebTarget resource = client.target("http://" + service.getIp() + ":" + service.getPort() + service.getPath());
            Invocation.Builder request = resource.request();
            try {
                Response response = request.get();
                System.out.println("Response status = " + response.getStatus());
                if(response.getStatus() >= 200 && response.getStatus() < 300) {
                    Statistics.getInstance().addSuccessfull(service);
                    System.out.println("Request for service " + service.getId() + " completed successfully");
                } else {
                    Statistics.getInstance().addFailed(service);
                    System.out.println("Request for service " + service.getId() + " failed");
                }
            } catch (Exception ex) {
                Statistics.getInstance().addFailed(service);
                System.out.println("Request for service " + service.getId() + " failed because of " + ex.getMessage());
            }

            Thread.sleep(2000);
        }

    }

    private static List<Service> readServicesFromFile(String servicesFile) throws IOException {
        File file = new File(servicesFile);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(file, new TypeReference<List<Service>>(){});
    }

}

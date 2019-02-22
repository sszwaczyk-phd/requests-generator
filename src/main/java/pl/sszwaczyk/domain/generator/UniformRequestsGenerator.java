package pl.sszwaczyk.domain.generator;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.client.ClientProperties;
import pl.sszwaczyk.domain.Service;
import pl.sszwaczyk.stats.Statistics;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class UniformRequestsGenerator extends RequestsGenerator {

    private int minGap;
    private int maxGap;

    public UniformRequestsGenerator(List<Service> services, int minGap, int maxGap) {
        super(services);
        this.minGap = minGap;
        this.maxGap = maxGap;
    }

    @Override
    public void start() throws InterruptedException {
        Random random = new Random();
        int size = services.size();

        while(true) {
            Service service = services.get(random.nextInt(size));
            log.info("Service " + service.getId() + " drawn");

            Client client = ClientBuilder.newClient();
            client.property(ClientProperties.CONNECT_TIMEOUT, 2000);

            WebTarget resource = client.target("http://" + service.getIp() + ":" + service.getPort() + service.getPath());
            Invocation.Builder request = resource.request();
            try {
                log.info("Sending request to service " + service.getId());
                long start = System.currentTimeMillis();
                Response response = request.get();
                long timeOfRealization = System.currentTimeMillis() - start;
                log.info("Response status = " + response.getStatus());
                if(response.getStatus() >= 200 && response.getStatus() < 300) {
                    Statistics.getInstance().updateSuccess(service, timeOfRealization);
                    log.info("Request for service " + service.getId() + " completed successfully in " + timeOfRealization + " ms.");
                } else {
                    Statistics.getInstance().updateFailed(service);
                    log.info("Request for service " + service.getId() + " failed");
                }
            } catch (Exception ex) {
                Statistics.getInstance().updateFailed(service);
                log.info("Request for service " + service.getId() + " failed because of " + ex.getMessage());
            }

            int gap = ThreadLocalRandom.current().nextInt(minGap, maxGap);
            log.info("Generating next request in " + gap + " seconds");
            Thread.sleep(gap * 1000);
        }
    }
}

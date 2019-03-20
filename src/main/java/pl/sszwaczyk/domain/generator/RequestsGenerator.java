package pl.sszwaczyk.domain.generator;

import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.sszwaczyk.domain.Service;
import pl.sszwaczyk.stats.Statistics;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public abstract class RequestsGenerator {

    private final Logger log = LoggerFactory.getLogger(RequestsGenerator.class);

    protected List<Service> services;
    protected Random random;

    private String everyRequestFile;

    public RequestsGenerator(List<Service> services, String everyRequestFile, long seed) {
        this.services = services;
        this.everyRequestFile = everyRequestFile;
        this.random = new Random(seed);
    }

    public void start() throws InterruptedException {
        int size = services.size();

        while(true) {
            Service service = services.get(random.nextInt(size));
            log.info("Service " + service.getId() + " drawn");

            Client client = ClientBuilder.newClient();
            client.property(ClientProperties.CONNECT_TIMEOUT, 2);
            client.property(ClientProperties.READ_TIMEOUT, 20000);

            WebTarget resource = client.target("http://" + service.getIp() + ":" + service.getPort()).queryParam("path", service.getPath());
            Invocation.Builder request = resource.request();
            Path path = Paths.get("/tmp/" + UUID.randomUUID());
            try {
                log.info("Sending request to service " + service.getId() + " for path " + service.getPath());
                long start = System.currentTimeMillis();
                Statistics.getInstance().updateGenerated(service);
                Response response = request.get();
                log.info("Response status = " + response.getStatus());
                if(response.getStatus() >= 200 && response.getStatus() < 300) {
                    Statistics.getInstance().updatePending(service);
                    Statistics.getInstance().snapshot(everyRequestFile);
                    downloadFile(response, path);
                    long timeOfRealization = System.currentTimeMillis() - start;
                    deleteFile(path);
                    Statistics.getInstance().updateSuccess(service, timeOfRealization);
                    log.info("Request for service " + service.getId() + " completed successfully in " + timeOfRealization + " ms.");
                    Statistics.getInstance().snapshot(everyRequestFile);
                } else {
                    Statistics.getInstance().updateFailed(service);
                    log.info("Request for service " + service.getId() + " failed");
                    Statistics.getInstance().snapshot(everyRequestFile);
                }
            } catch (Exception ex) {
                Statistics.getInstance().updateFailed(service);
                log.error("Request for service " + service.getId() + " failed because of " + ex.getMessage());
                Statistics.getInstance().snapshot(everyRequestFile);
                try {
                    deleteFile(path);
                } catch(IOException e) {
                    log.error("Cannot delete file becouse of " + e.getMessage());
                }
            }

            int gap = getNextGapInSeconds();
            log.info("Generating next request in " + gap + " seconds");
            Thread.sleep(gap * 1000);

        }
    }

    private void deleteFile(Path path) throws IOException {
        log.info("Deleting file " + path.toAbsolutePath() + "...");
        Files.delete(path);
        log.info("File deleted");
    }

    private Path downloadFile(Response response, Path path) throws IOException {
        InputStream inputStream = response.readEntity(InputStream.class);
        log.info("Downloading to " + path.toAbsolutePath() + "...");
        Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        inputStream.close();
        log.info("File downloaded");
        return path;
    }

    public abstract int getNextGapInSeconds();

}

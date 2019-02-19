package pl.sszwaczyk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import pl.sszwaczyk.cmd.CmdLineSettings;
import pl.sszwaczyk.domain.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        CmdLineSettings settings = new CmdLineSettings();
        CmdLineParser parser = new CmdLineParser(settings);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            parser.printUsage(System.out);
            System.exit(1);
        }

        List<Service> services = readServicesFromFile(settings.getServicesFile());

        Service service = services.get(0);

        Client client = ClientBuilder.newClient();
        WebTarget resource = client.target("http://" + service.getIp() + ":" + service.getPort() + service.getPath());
        Invocation.Builder request = resource.request();
        Response response = request.get();
        System.out.println(response);
    }

    private static List<Service> readServicesFromFile(String servicesFile) throws IOException {
        File file = new File(servicesFile);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(file, new TypeReference<List<Service>>(){});
    }

}

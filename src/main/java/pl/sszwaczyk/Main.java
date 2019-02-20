package pl.sszwaczyk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import pl.sszwaczyk.cmd.CmdLineSettings;
import pl.sszwaczyk.domain.Service;
import pl.sszwaczyk.domain.generator.RequestsGenerator;
import pl.sszwaczyk.domain.generator.UniformRequestsGenerator;
import pl.sszwaczyk.stats.SaveStatisticsRunnable;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        RequestsGenerator requestsGenerator = null;

        CmdLineSettings settings = new CmdLineSettings();
        CmdLineParser parser = new CmdLineParser(settings);
        try {
            parser.parseArgument(args);

            List<Service> services = readServicesFromFile(settings.getServicesFile());

            String generator = settings.getGenerator();
            if(generator.equals("uniform")) {
                Integer minGap = settings.getMinGap();
                Integer maxGap = settings.getMaxGap();
                if(minGap == null || minGap == 0 || maxGap == null || maxGap == 0) {
                    throw new CmdLineException(parser, "Min and Max gap must be specified for uniform generator");
                }
                if(maxGap < minGap) {
                    throw new CmdLineException(parser, "Max gap must be greater then min gap");
                }

                requestsGenerator = new UniformRequestsGenerator(services, minGap, maxGap);

            } else {

                throw new CmdLineException(parser, "Generator not specified");

            }
        } catch (CmdLineException e) {
            System.out.println(e.getMessage());
            parser.printUsage(System.out);
            System.exit(1);
        }

        Thread saveStatsThread = new Thread(new SaveStatisticsRunnable(settings.getStatsFile()));
        Runtime.getRuntime().addShutdownHook(saveStatsThread);

        requestsGenerator.start();

    }

    private static List<Service> readServicesFromFile(String servicesFile) throws IOException {
        File file = new File(servicesFile);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(file, new TypeReference<List<Service>>(){});
    }

}

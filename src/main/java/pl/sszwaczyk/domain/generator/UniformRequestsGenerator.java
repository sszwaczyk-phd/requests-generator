package pl.sszwaczyk.domain.generator;

import lombok.extern.slf4j.Slf4j;
import pl.sszwaczyk.domain.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class UniformRequestsGenerator extends RequestsGenerator {

    private int minGap;
    private int maxGap;

    public UniformRequestsGenerator(List<Service> services, String everyRequestFile, int minGap, int maxGap) {
        super(services, everyRequestFile);
        this.minGap = minGap;
        this.maxGap = maxGap;
    }

    @Override
    public int getNextGapInSeconds() {
        return ThreadLocalRandom.current().nextInt(minGap, maxGap);
    }
}

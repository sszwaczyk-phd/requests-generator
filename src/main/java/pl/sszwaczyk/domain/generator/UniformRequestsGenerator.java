package pl.sszwaczyk.domain.generator;

import lombok.extern.slf4j.Slf4j;
import pl.sszwaczyk.domain.Service;

import java.util.List;
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
    public int getNextGapInSeconds() {
        return ThreadLocalRandom.current().nextInt(minGap, maxGap);
    }
}

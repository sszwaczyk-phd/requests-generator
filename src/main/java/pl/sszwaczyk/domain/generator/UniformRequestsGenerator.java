package pl.sszwaczyk.domain.generator;

import pl.sszwaczyk.domain.Service;

import java.util.List;

public class UniformRequestsGenerator extends RequestsGenerator {

    private int minGap;
    private int maxGap;

    public UniformRequestsGenerator(List<Service> services, String everyRequestFile, long seed, int minGap, int maxGap) {
        super(services, everyRequestFile, seed);
        this.minGap = minGap;
        this.maxGap = maxGap;
    }

    @Override
    public int getNextGapInSeconds() {
        return random.nextInt((maxGap - minGap) + 1) + minGap;
    }
}

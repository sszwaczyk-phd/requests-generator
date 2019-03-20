package pl.sszwaczyk.domain.generator;

import pl.sszwaczyk.domain.Service;

import java.util.List;

public class PoissonRequestsGenerator  extends RequestsGenerator {

    private double lambda;

    public PoissonRequestsGenerator(List<Service> services, String everyRequestFile, long seed, double lambda) {
        super(services, everyRequestFile, seed);
        this.lambda = lambda;
    }

    @Override
    public int getNextGapInSeconds() {
        return (int) (Math.log(1-random.nextDouble())/(-lambda));
    }
}

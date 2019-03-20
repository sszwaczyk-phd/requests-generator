package pl.sszwaczyk.domain.generator;

import pl.sszwaczyk.domain.Service;

import java.util.List;
import java.util.Random;

public class PoissonRequestsGenerator  extends RequestsGenerator {

    private double lambda;

    private Random random;

    public PoissonRequestsGenerator(List<Service> services, String everyRequestFile, double lambda) {
        super(services, everyRequestFile);
        this.lambda = lambda;
        random = new Random();
    }

    @Override
    public int getNextGapInSeconds() {
        return (int) (Math.log(1-random.nextDouble())/(-lambda));
    }
}

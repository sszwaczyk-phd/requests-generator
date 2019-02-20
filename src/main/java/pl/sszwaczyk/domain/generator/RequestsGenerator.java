package pl.sszwaczyk.domain.generator;

import pl.sszwaczyk.domain.Service;

import java.util.List;

public abstract class RequestsGenerator {

    protected List<Service> services;

    public RequestsGenerator(List<Service> services) {
        this.services = services;
    }

    public abstract void start() throws InterruptedException;

}

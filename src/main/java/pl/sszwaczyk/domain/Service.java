package pl.sszwaczyk.domain;

import lombok.Data;

@Data
public class Service {

    private String id;
    private String ip;
    private Long port;
    private String path;

}

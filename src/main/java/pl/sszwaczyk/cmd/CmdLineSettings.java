package pl.sszwaczyk.cmd;

import lombok.Data;
import org.kohsuke.args4j.Option;

@Data
public class CmdLineSettings {

    @Option(name="-sf", aliases="--servicesFile", metaVar="FILE", usage="Services repository file", required = true)
    private String servicesFile;

    @Option(name="-st", aliases="--statsFile", metaVar="FILE", usage="Path where to save statistics file", required = true)
    private String statsFile;

    @Option(name="-er", aliases="--requestFile", metaVar="FILE", usage="Path where to save statistics file after every request", required = true)
    private String everyRequestFile;

    @Option(name="-lf", aliases="--logFile", metaVar="FILE", usage="Path where to save log file", required = true)
    private String logFile;

    @Option(name="-g", aliases="--generator", usage="Generator to use (uniform or poisson)", required = true)
    private String generator;

    @Option(name="-ming", aliases="--minGap", usage="Min gap between requests (required if generator == uniform)")
    private Integer minGap;

    @Option(name="-maxg", aliases="--maxGap", usage="Max gap between requests (required if generator == uniform)")
    private Integer maxGap;

    @Option(name="-l", aliases="--lambda", usage="Lambda parameter (1 request per seconds) for poisson generator (required if generator == poisson)")
    private Double lambda;
}

package pl.sszwaczyk.cmd;

import lombok.Data;
import org.kohsuke.args4j.Option;

@Data
public class CmdLineSettings {

    @Option(name="-sf", aliases="--servicesFile", metaVar="FILE", usage="Services repository file", required = true)
    private String servicesFile;

    @Option(name="-t", aliases="--time", metaVar="Long", usage="How long generate packets (seconds)", required = true)
    private Long time;

    @Option(name="-st", aliases="--statsFile", metaVar="FILE", usage="Path where to save statistics file", required = true)
    private String statsFile;
}

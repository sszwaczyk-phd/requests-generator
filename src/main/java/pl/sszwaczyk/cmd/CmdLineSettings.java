package pl.sszwaczyk.cmd;

import lombok.Data;
import org.kohsuke.args4j.Option;

@Data
public class CmdLineSettings {

    @Option(name="-sf", aliases="--servicesFile", metaVar="FILE", usage="Services repository file", required = true)
    private String servicesFile;


}

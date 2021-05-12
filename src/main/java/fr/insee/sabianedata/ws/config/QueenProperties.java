package fr.insee.sabianedata.ws.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Component
public class QueenProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueenProperties.class);

    @Value("${fr.insee.sabianedata.queen-api.scheme.dv}://${fr.insee.sabianedata.queen-api.host.dv}")
    private String hostDv;

    @Value("${fr.insee.sabianedata.queen-api.scheme.qf1}://${fr.insee.sabianedata.queen-api.host.qf1}")
    private String hostQf1;

    @Value("${fr.insee.sabianedata.queen-api.scheme.qf2}://${fr.insee.sabianedata.queen-api.host.qf2}")
    private String hostQf2;

    @Value("${fr.insee.sabianedata.queen-api.scheme.qf3}://${fr.insee.sabianedata.queen-api.host.qf3}")
    private String hostQf3;

    @Value("${fr.insee.sabianedata.queen-api.scheme.cloud}://${fr.insee.sabianedata.queen-api.host.cloud}")
    private String hostCloud;


    public String getHostFromEnum(Plateform plateform){
        String host=null;
        switch (plateform){
            case DV:
                host=hostDv;
                break;
            case QF1:
                host=hostQf1;
                break;
            case QF2:
                host=hostQf2;
                break;
            case QF3:
                host=hostQf3;
                break;
            case CLOUD:
                host=hostCloud;
                break;
        }
        return host;
    }


    @PostConstruct
    private void postConstruct() {
        Arrays.stream(Plateform.values()).forEach(prop -> LOGGER.info("Host Queen {}: {}", prop, getHostFromEnum(prop)));
    }

}

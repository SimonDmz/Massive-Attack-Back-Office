package fr.insee.sabianedata.ws.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import fr.insee.sabianedata.ws.model.massiveAttack.TrainingScenario;
import fr.insee.sabianedata.ws.model.pearl.Campaign;
import fr.insee.sabianedata.ws.model.pearl.CampaignDto;

@Service
public class TrainingScenarioService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MassiveAttackService.class);

    @Autowired
    PearlExtractEntities pearlExtractEntities;

    public TrainingScenario getTrainingScenario(String tsId) {

        try {
            File infoFile = ResourceUtils.getFile("scenarii/" + tsId + "/info.json");
            ObjectMapper objectMapper = new ObjectMapper();

            TrainingScenario ts = objectMapper.readValue(infoFile, TrainingScenario.class);

            File scenariiFolder = ResourceUtils.getFile("scenarii/" + tsId);
            List<CampaignDto> campaigns = Arrays.stream(scenariiFolder.listFiles()).map(f -> {
                try {
                    return pearlExtractEntities.getPearlCampaignFromFods(f);
                } catch (Exception e) {
                    LOGGER.warn("Error when extracting campaign from " + f.getAbsolutePath());
                    LOGGER.warn(e.getMessage());
                    return null;
                }
            }).collect(Collectors.toList());
            if (campaigns.contains(null)) {

                throw new RuntimeException("extraction error");
            }

            ts.setCampaigns(campaigns);
            return ts;

        } catch (Exception e) {
            LOGGER.warn("Error when getting scenario " + tsId);
            LOGGER.warn(e.getMessage());
            return null;
        }

    }

    public List<Campaign> getCampaignsFromXml() {
        return null;

    }

    public List<TrainingScenario> getTrainingScenarii(String folder) {
        try {
            File scenariiFolder = new File(folder, "scenarii");
            Stream<File> folders = Arrays.stream(scenariiFolder.listFiles());
            return folders.map(f -> getTrainingScenario(f.getName())).collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return new ArrayList<>();
        }
    }

}

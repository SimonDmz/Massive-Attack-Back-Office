package fr.insee.sabianedata.ws.model.massiveAttack;

import java.util.List;

import fr.insee.sabianedata.ws.model.pearl.CampaignDto;

public class TrainingScenario {

    public TrainingScenario(List<CampaignDto> campaigns, ScenarioType type, String label) {
        this.campaigns = campaigns;
        this.type = type;
        this.label = label;
    }

    private List<CampaignDto> campaigns;
    private ScenarioType type;
    private String label;

    public ScenarioType getType() {
        return type;
    }

    public void setType(ScenarioType type) {
        this.type = type;
    }

    public List<CampaignDto> getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(List<CampaignDto> campaigns) {
        this.campaigns = campaigns;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}

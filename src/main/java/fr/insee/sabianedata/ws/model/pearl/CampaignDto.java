package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "Campaign")
public class CampaignDto {

    @JacksonXmlProperty(localName = "Campaign")
    private String campaign;

    @JacksonXmlProperty(localName = "CampaignLabel")
    private String campaignLabel;

    @JacksonXmlElementWrapper(localName = "Visibilities")
    private List<Visibility> visibilities;

    public CampaignDto() {
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public String getCampaignLabel() {
        return campaignLabel;
    }

    public void setCampaignLabel(String campaignLabel) {
        this.campaignLabel = campaignLabel;
    }

    public List<Visibility> getVisibilities() {
        return visibilities;
    }

    public void setVisibilities(List<Visibility> visibilities) {
        this.visibilities = visibilities;
    }

}

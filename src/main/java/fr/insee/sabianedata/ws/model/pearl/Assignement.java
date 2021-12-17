package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "Assignement")
public class Assignement {

    @JacksonXmlProperty(localName = "SurveyUnitId")
    private String surveyUnitId;

    @JacksonXmlProperty(localName = "InterviewerId")
    private String interviewerId;

    public Assignement() {
    }

    public Assignement(String surveyUnitId, String interviewerId) {
        this.surveyUnitId = surveyUnitId;
        this.interviewerId = interviewerId;
    }

    public String getSurveyUnitId() {
        return surveyUnitId;
    }

    public void setSurveyUnitId(String surveyUnitId) {
        this.surveyUnitId = surveyUnitId;
    }

    public String getInterviewerId() {
        return interviewerId;
    }

    public void setInterviewerId(String interviewerId) {
        this.interviewerId = interviewerId;
    }

    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }
}

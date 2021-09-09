package fr.insee.sabianedata.ws.model.queen;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "SurveyUnit")
public class SurveyUnit {

    @JacksonXmlProperty(localName = "Id")
    @JsonProperty("id")
    private String id;

    @JacksonXmlProperty(localName = "QuestionnaireId")
    private String QuestionnaireId;

    @JacksonXmlProperty(localName = "StateData")
    private StateData stateData;

    @JacksonXmlProperty(localName = "PersonalizationFile")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String personalizationFile;

    @JacksonXmlProperty(localName = "DataFile")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dataFile;

    @JacksonXmlProperty(localName = "CommentFile")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String commentFile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestionnaireId() {
        return QuestionnaireId;
    }

    public void setQuestionnaireId(String questionnaireId) {
        QuestionnaireId = questionnaireId;
    }

    public StateData getStateData() {
        return stateData;
    }

    public void setStateData(StateData stateData) {
        this.stateData = stateData;
    }

    public String getPersonalizationFile() {
        return personalizationFile;
    }

    public void setPersonalizationFile(String personalizationFile) {
        this.personalizationFile = personalizationFile;
    }

    public String getDataFile() {
        return dataFile;
    }

    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }

    public String getCommentFile() {
        return commentFile;
    }

    public void setCommentFile(String commentFile) {
        this.commentFile = commentFile;
    }

    public SurveyUnit(String id, String questionnaireId, StateData stateData) {
        this.id = id;
        this.QuestionnaireId = questionnaireId;
        this.stateData = stateData;
    }

    public SurveyUnit(String id, String questionnaireId, StateData stateData, String dataFile, String commentFile,
            String personalizationFile) {
        this.id = id;
        this.QuestionnaireId = questionnaireId;
        this.stateData = stateData;
        this.dataFile = dataFile;
        this.commentFile = commentFile;
        this.personalizationFile = personalizationFile;
    }

    public SurveyUnit(SurveyUnit su) {
        this(su.getId(), su.getQuestionnaireId(), su.getStateData(), su.getDataFile(), su.getCommentFile(),
                su.getPersonalizationFile());
    }

    public SurveyUnit() {
    }
}

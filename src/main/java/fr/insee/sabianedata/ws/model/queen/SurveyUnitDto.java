package fr.insee.sabianedata.ws.model.queen;

import java.io.File;
import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;

import fr.insee.sabianedata.ws.utils.JsonFileToJsonNode;

public class SurveyUnitDto extends SurveyUnit {

    public static final String FOLDER = "surveyUnits";

    private JsonNode data;
    private JsonNode comment;
    private JsonNode personalization;

    public SurveyUnitDto(SurveyUnit surveyUnit, String folder) {
        super(surveyUnit.getId(), surveyUnit.getQuestionnaireId(), surveyUnit.getStateData());
        String finalFolder = folder + File.separator + FOLDER;
        File dtodataFile = new File(finalFolder + File.separator + surveyUnit.getDataFile());
        File commentFile = new File(finalFolder + File.separator + surveyUnit.getCommentFile());
        File personalizationFile = new File(finalFolder + File.separator + surveyUnit.getPersonalizationFile());
        this.data = JsonFileToJsonNode.getJsonNodeFromFile(dtodataFile);
        this.comment = JsonFileToJsonNode.getJsonNodeFromFile(commentFile);
        this.personalization = JsonFileToJsonNode.getJsonNodeFromFile(personalizationFile);
    }

    public SurveyUnitDto(SurveyUnit surveyUnit) {
        super(surveyUnit.getId(), surveyUnit.getQuestionnaireId(), surveyUnit.getStateData(), surveyUnit.getDataFile(),
                surveyUnit.getCommentFile(), surveyUnit.getPersonalizationFile());
    }

    public SurveyUnitDto(SurveyUnitDto suDto, SurveyUnit su) {
        super(su);
        this.data = suDto.getData();
        this.comment = suDto.getComment();
        this.personalization = suDto.getPersonalization();
    }

    public void extractJsonFromFiles(String folder) {
        String finalFolder = folder + File.separator + FOLDER;
        File dtodataFile = new File(finalFolder + File.separator + getDataFile());
        File commentFile = new File(finalFolder + File.separator + getCommentFile());
        File personalizationFile = new File(finalFolder + File.separator + getPersonalizationFile());
        setData(JsonFileToJsonNode.getJsonNodeFromFile(dtodataFile));
        setComment(JsonFileToJsonNode.getJsonNodeFromFile(commentFile));
        setPersonalization(JsonFileToJsonNode.getJsonNodeFromFile(personalizationFile));

        // to prevent jsonification to API calls
        setDataFile(null);
        setCommentFile(null);
        setPersonalizationFile(null);

    }

    public void generateStateData() {
        StateData stateDataToInject = new StateData();
        stateDataToInject.setCurrentPage("1");
        stateDataToInject.setDate(new Date().getTime());
        stateDataToInject.setState("INIT");
        this.setStateData(stateDataToInject);
    }

    public JsonNode getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }

    public JsonNode getComment() {
        return comment;
    }

    public void setComment(JsonNode comment) {
        this.comment = comment;
    }

    public JsonNode getPersonalization() {
        return personalization;
    }

    public void setPersonalization(JsonNode personalization) {
        this.personalization = personalization;
    }

}

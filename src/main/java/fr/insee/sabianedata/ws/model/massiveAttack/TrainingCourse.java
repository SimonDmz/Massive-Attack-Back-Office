package fr.insee.sabianedata.ws.model.massiveAttack;

import java.util.List;

import fr.insee.sabianedata.ws.model.pearl.Assignement;
import fr.insee.sabianedata.ws.model.pearl.CampaignDto;
import fr.insee.sabianedata.ws.model.pearl.SurveyUnitDto;
import fr.insee.sabianedata.ws.model.queen.NomenclatureDto;
import fr.insee.sabianedata.ws.model.queen.QuestionnaireModelDto;

public class TrainingCourse {

    public TrainingCourse(List<SurveyUnitDto> pearlSurveyUnits,
            List<fr.insee.sabianedata.ws.model.queen.SurveyUnitDto> queenSurveyUnits, CampaignDto pearlCampaign,
            fr.insee.sabianedata.ws.model.queen.CampaignDto queenCampaign,
            List<QuestionnaireModelDto> questionnaireModels, List<NomenclatureDto> nomenclatures,
            List<Assignement> assignements) {
        this.pearlSurveyUnits = pearlSurveyUnits;
        this.queenSurveyUnits = queenSurveyUnits;
        this.pearlCampaign = pearlCampaign;
        this.queenCampaign = queenCampaign;
        this.questionnaireModels = questionnaireModels;
        this.nomenclatures = nomenclatures;
        this.assignements = assignements;
    }

    private List<SurveyUnitDto> pearlSurveyUnits;
    private List<fr.insee.sabianedata.ws.model.queen.SurveyUnitDto> queenSurveyUnits;

    private CampaignDto pearlCampaign;
    private fr.insee.sabianedata.ws.model.queen.CampaignDto queenCampaign;

    private List<QuestionnaireModelDto> questionnaireModels;
    private List<NomenclatureDto> nomenclatures;
    private List<Assignement> assignements;

    public List<Assignement> getAssignements() {
        return assignements;
    }

    public void setAssignements(List<Assignement> assignements) {
        this.assignements = assignements;
    }

    public List<NomenclatureDto> getNomenclatures() {
        return nomenclatures;
    }

    public void setNomenclatures(List<NomenclatureDto> nomenclatures) {
        this.nomenclatures = nomenclatures;
    }

    public List<QuestionnaireModelDto> getQuestionnaireModels() {
        return questionnaireModels;
    }

    public void setQuestionnaireModels(List<QuestionnaireModelDto> questionnaireModels) {
        this.questionnaireModels = questionnaireModels;
    }

    public CampaignDto getPearlCampaign() {
        return pearlCampaign;
    }

    public void setPearlCampaign(CampaignDto pearlCampaign) {
        this.pearlCampaign = pearlCampaign;
    }

    public List<SurveyUnitDto> getPearlSurveyUnits() {
        return pearlSurveyUnits;
    }

    public void setPearlSurveyUnits(List<SurveyUnitDto> pearlSurveyUnits) {
        this.pearlSurveyUnits = pearlSurveyUnits;
    }

    public List<fr.insee.sabianedata.ws.model.queen.SurveyUnitDto> getQueenSurveyUnits() {
        return queenSurveyUnits;
    }

    public void setQueenSurveyUnits(List<fr.insee.sabianedata.ws.model.queen.SurveyUnitDto> queenSurveyUnits) {
        this.queenSurveyUnits = queenSurveyUnits;
    }

    public List<SurveyUnitDto> getSurveyUnits() {
        return pearlSurveyUnits;
    }

    public void setSurveyUnits(List<SurveyUnitDto> surveyUnits) {
        this.pearlSurveyUnits = surveyUnits;
    }

    public fr.insee.sabianedata.ws.model.queen.CampaignDto getQueenCampaign() {
        return queenCampaign;
    }

    public void setQueenCampaign(fr.insee.sabianedata.ws.model.queen.CampaignDto queenCampaign) {
        this.queenCampaign = queenCampaign;
    }

    public String getCampaignId() {
        return queenCampaign.getId();
    }
}

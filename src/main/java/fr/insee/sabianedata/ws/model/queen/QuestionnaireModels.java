package fr.insee.sabianedata.ws.model.queen;

import java.util.ArrayList;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "QuestionnaireModels")
public class QuestionnaireModels {

    @JacksonXmlProperty(localName = "QuestionnaireModel")
    @JacksonXmlElementWrapper(useWrapping = false)
    private ArrayList<QuestionnaireModel> questionnaireModels;

    public ArrayList<QuestionnaireModel> getQuestionnaireModels() {
        return questionnaireModels;
    }

    public void setQuestionnaireModels(ArrayList<QuestionnaireModel> questionnaireModels) {
        this.questionnaireModels = questionnaireModels;
    }

    public QuestionnaireModels() {
    }
}

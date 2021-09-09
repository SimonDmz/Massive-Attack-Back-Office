package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import fr.insee.sabianedata.ws.utils.DateParser;

@JacksonXmlRootElement(localName = "State")
public class SurveyUnitStateDto {

    @JacksonXmlProperty(localName = "Value")
    private String type;
    @JacksonXmlProperty(localName = "Date")
    private String dateString;

    private Long date;

    public SurveyUnitStateDto() {
    }

    public SurveyUnitStateDto(SurveyUnitStateDto su, Long reference) throws IllegalArgumentException {
        this.type = su.getType();
        this.date = DateParser.relativeDateParse(su.getDateString(), reference);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

}

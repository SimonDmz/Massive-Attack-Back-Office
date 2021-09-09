package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import fr.insee.sabianedata.ws.utils.DateParser;

@JacksonXmlRootElement(localName = "ContactOutcome")
public class ContactOutcomeDto {

    @JacksonXmlProperty(localName = "Value")
    private String value;
    @JacksonXmlProperty(localName = "AttemptsNumber")
    private int attemptsNumber;
    @JacksonXmlProperty(localName = "Date")
    private String dateString;

    private Long date;

    public ContactOutcomeDto() {
    }

    public ContactOutcomeDto(ContactOutcomeDto co, Long reference) throws IllegalArgumentException {
        this.value = co.getValue();
        this.dateString = co.getDateString();
        this.date = DateParser.relativeDateParse(co.getDateString(), reference);
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getAttemptsNumber() {
        return attemptsNumber;
    }

    public void setAttemptsNumber(int attemptsNumber) {
        this.attemptsNumber = attemptsNumber;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

}

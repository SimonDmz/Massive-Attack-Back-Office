package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import fr.insee.sabianedata.ws.utils.DateParser;

@JacksonXmlRootElement(localName = "ContactAttempt")
public class ContactAttemptDto {

    @JacksonXmlProperty(localName = "Value")
    private String value;
    @JacksonXmlProperty(localName = "Date")
    private String dateString;

    private Long date;

    public ContactAttemptDto(ContactAttemptDto ca, Long reference) {
        this.value = ca.getValue();
        this.date = DateParser.relativeDateParse(ca.getDateString(), reference);
    }

    public ContactAttemptDto() {
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

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

}

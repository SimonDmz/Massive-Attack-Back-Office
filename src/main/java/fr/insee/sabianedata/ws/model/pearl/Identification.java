package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "SurveyUnitIdentification")
public class Identification {

    @JacksonXmlProperty(localName = "Identification")
    private String identification;
    @JacksonXmlProperty(localName = "Access")
    private String access;
    @JacksonXmlProperty(localName = "Situation")
    private String situation;
    @JacksonXmlProperty(localName = "Category")
    private String category;
    @JacksonXmlProperty(localName = "Occupant")
    private String occupant;

}

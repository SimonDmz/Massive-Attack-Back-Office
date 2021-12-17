package fr.insee.sabianedata.ws.model.queen;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "Nomenclature")
public class Nomenclature {

    @JacksonXmlProperty(localName="Id")
    private String id;

    @JacksonXmlProperty(localName="Label")
    private String label;

    @JacksonXmlProperty(localName="FileName")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fileName;

    public Nomenclature() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Nomenclature(String id, String label) {
        this.id = id;
        this.label = label;
    }
}

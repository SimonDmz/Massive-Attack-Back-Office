package fr.insee.sabianedata.ws.model.massiveAttack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PearlUser {

    private String id;
    private String firstName;
    private String lastName;
    @JsonProperty("organizationUnit")
    private OrganisationUnitDto organisationUnit;

    public PearlUser() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public OrganisationUnitDto getOrganisationUnit() {
        return organisationUnit;
    }

    public void setOrganisationUnit(OrganisationUnitDto organisationUnit) {
        this.organisationUnit = organisationUnit;
    }

}

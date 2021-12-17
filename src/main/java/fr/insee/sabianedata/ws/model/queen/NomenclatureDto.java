package fr.insee.sabianedata.ws.model.queen;

import java.io.File;

import com.fasterxml.jackson.databind.JsonNode;

import fr.insee.sabianedata.ws.utils.JsonFileToJsonNode;

public class NomenclatureDto extends Nomenclature {

    public static final String FOLDER = "nomenclatures";

    private JsonNode value;

    public NomenclatureDto(Nomenclature nomenclature, String folder) {
        super(nomenclature.getId(), nomenclature.getLabel());
        File nomenclatureFile = new File(
                folder + File.separator + FOLDER + File.separator + nomenclature.getFileName());
        this.value = JsonFileToJsonNode.getJsonNodeFromFile(nomenclatureFile);
    }

    public JsonNode getValue() {
        return value;
    }

    public void setValue(JsonNode value) {
        this.value = value;
    }
}

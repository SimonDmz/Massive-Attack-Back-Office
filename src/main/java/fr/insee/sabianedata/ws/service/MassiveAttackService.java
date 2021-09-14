package fr.insee.sabianedata.ws.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import fr.insee.sabianedata.ws.config.Plateform;
import fr.insee.sabianedata.ws.model.ResponseModel;
import fr.insee.sabianedata.ws.model.massiveAttack.TrainingCourse;
import fr.insee.sabianedata.ws.model.massiveAttack.TrainingScenario;
import fr.insee.sabianedata.ws.model.pearl.Assignement;
import fr.insee.sabianedata.ws.model.pearl.Campaign;
import fr.insee.sabianedata.ws.model.pearl.ContactAttemptDto;
import fr.insee.sabianedata.ws.model.pearl.ContactOutcomeDto;
import fr.insee.sabianedata.ws.model.pearl.InterviewerDto;
import fr.insee.sabianedata.ws.model.pearl.InterviewersDto;
import fr.insee.sabianedata.ws.model.pearl.OrganisationUnitDto;
import fr.insee.sabianedata.ws.model.pearl.SurveyUnitStateDto;
import fr.insee.sabianedata.ws.model.pearl.Visibility;
import fr.insee.sabianedata.ws.model.queen.CampaignDto;
import fr.insee.sabianedata.ws.model.queen.NomenclatureDto;
import fr.insee.sabianedata.ws.model.queen.QuestionnaireModelDto;
import fr.insee.sabianedata.ws.model.queen.SurveyUnit;
import fr.insee.sabianedata.ws.model.queen.SurveyUnitDto;

@Service
public class MassiveAttackService {

        private static final Logger LOGGER = LoggerFactory.getLogger(MassiveAttackService.class);

        @Autowired
        private QueenExtractEntities queenExtractEntities;

        @Autowired
        private QueenApiService queenApiService;

        @Autowired
        private PearlExtractEntities pearlExtractEntities;

        @Autowired
        private TrainingScenarioService trainingScenarioService;

        @Autowired
        private PearlApiService pearlApiService;

        @Autowired
        ResourceLoader resourceLoader;

        @Value("${fr.insee.sabianedata.security}")
        private String authMode;

        private File tempFolder;
        private File tempScenariiFolder;

        private HashMap<String, TrainingScenario> scenarii = new HashMap<>();

        @PostConstruct
        private void init() {

                try {
                        tempFolder = Files.createTempDirectory("folder-").toFile();
                        tempScenariiFolder = new File(tempFolder.toString() + "/scenarii");
                        File scenarii = resourceLoader.getResource("classpath:scenarii").getFile();
                        tempScenariiFolder.mkdirs();
                        FileUtils.copyDirectory(scenarii, tempScenariiFolder);
                } catch (IOException e) {
                        e.printStackTrace();
                }

                Stream<File> folders = Arrays.stream(tempScenariiFolder.listFiles());
                List<TrainingScenario> listScenarii = folders
                                .map(f -> trainingScenarioService.getTrainingScenario(tempScenariiFolder, f.getName()))
                                .collect(Collectors.toList());
                listScenarii.forEach(scenar -> scenarii.put(scenar.getLabel(), scenar));
                LOGGER.debug("Init loading finished : {} loaded scenarii", scenarii.size());

        }

        @PreDestroy
        private void cleanup() {
                boolean result = FileSystemUtils.deleteRecursively(tempFolder);
                LOGGER.debug("Clean-up result : " + result);
        }

        private void rollBackOnFail(List<String> ids) {
                LOGGER.warn("NOTHING IMPLEMENTED YET, TIME TO CRAWL THE DATABASES!!");
                // TODO : use DELETE P&Q endpoints
        }

        private TrainingCourse prepareTrainingCourse(String campaign, String scenario, String campaignLabel,
                        HttpServletRequest request, Long referenceDate, Plateform plateform, List<String> interviewers)
                        throws Exception {

                // 1 : dossier de traitement 'folder-'
                File currentCampaignFolder = new File(tempScenariiFolder,
                                scenario + File.separator + campaign.toUpperCase());
                File queenFolder = new File(currentCampaignFolder, "queen");
                File pearlFolder = new File(currentCampaignFolder, "pearl");

                // 2 : extract Queen
                File queenFodsInput = new File(queenFolder, "queen_campaign.fods");
                CampaignDto queenCampaign = queenExtractEntities.getQueenCampaignFromFods(queenFodsInput);
                List<QuestionnaireModelDto> questionnaireModels = queenExtractEntities
                                .getQueenQuestionnaireModelsDtoFromFods(queenFodsInput, queenFolder.toString());
                List<NomenclatureDto> nomenclatures = queenExtractEntities
                                .getQueenNomenclaturesDtoFromFods(queenFodsInput, queenFolder.toString());
                List<SurveyUnitDto> queenSurveyUnits = queenExtractEntities.getQueenSurveyUnitsFromFods(queenFodsInput,
                                queenFolder.toString());

                // 3 : extract Pearl
                File pearlFodsInput = new File(pearlFolder, "pearl_campaign.fods");

                fr.insee.sabianedata.ws.model.pearl.CampaignDto pearlCampaign = pearlExtractEntities
                                .getPearlCampaignFromFods(pearlFodsInput);
                List<fr.insee.sabianedata.ws.model.pearl.SurveyUnitDto> pearlSurveyUnits = pearlExtractEntities
                                .getPearlSurveyUnitsFromFods(pearlFodsInput);

                LOGGER.debug("XML extractions completed");
                // 4 : get user organisationUnit
                OrganisationUnitDto orgaUnit = pearlApiService.getUserOrganizationUnit(request, plateform);

                // 5 : make campaignId uniq => {campaign.id}_{OU}{date}
                String newCampaignId = pearlCampaign.getCampaign() + "_" + orgaUnit.getOrganisationUnit() + "_"
                                + referenceDate;
                pearlCampaign.setCampaign(newCampaignId);
                pearlCampaign.setCampaignLabel(campaignLabel);
                LOGGER.debug("Generating new campaignId = " + newCampaignId);

                // 6 : change visibility with user OU only and
                LOGGER.debug("Updating visibilities : " + pearlCampaign.getVisibilities().size() + " visibilities");

                ArrayList<Visibility> visibilities = updatingVisibilities(referenceDate, orgaUnit,
                                pearlCampaign.getVisibilities());

                pearlCampaign.setVisibilities(visibilities);

                // 7 : generate pearl survey-units for interviewers
                LOGGER.debug("Pearl survey-units generation : " + interviewers.size() + " interviewers / "
                                + pearlSurveyUnits.size() + " survey-units");

                List<fr.insee.sabianedata.ws.model.pearl.SurveyUnitDto> distributedPearlSurveyUnits = interviewers
                                .stream().map(in -> pearlSurveyUnits.stream().map(su -> {
                                        LOGGER.debug("generating pearl su : " + su.getId() + " for " + in);
                                        fr.insee.sabianedata.ws.model.pearl.SurveyUnitDto newSu = new fr.insee.sabianedata.ws.model.pearl.SurveyUnitDto(
                                                        su);
                                        newSu.setCampaign(pearlCampaign.getCampaign());
                                        newSu.setOrganizationUnitId(orgaUnit.getOrganisationUnit());
                                        newSu.setId(su.getId() + "_" + campaign + "_" + in + "_" + referenceDate);

                                        // states
                                        List<SurveyUnitStateDto> statesList = Optional.ofNullable(su.getStates())
                                                        .orElse(new ArrayList<>()).stream()
                                                        .map(state -> new SurveyUnitStateDto(state, referenceDate))
                                                        .collect(Collectors.toList());
                                        ArrayList<SurveyUnitStateDto> newStates = new ArrayList<>(statesList);
                                        newSu.setStates(newStates);

                                        // contactOutcome
                                        ContactOutcomeDto newContactOutcomeDto = su.getContactOutcome() != null
                                                        ? new ContactOutcomeDto(su.getContactOutcome(), referenceDate)
                                                        : null;
                                        newSu.setContactOutcome(newContactOutcomeDto);

                                        // contactAttempts
                                        List<ContactAttemptDto> newCAs = Optional.ofNullable(su.getContactAttempts())
                                                        .orElse(new ArrayList<>()).stream()
                                                        .map(ca -> new ContactAttemptDto(ca, referenceDate))
                                                        .collect(Collectors.toList());
                                        ArrayList<ContactAttemptDto> newContactAttempts = new ArrayList<>(newCAs);
                                        newSu.setContactAttempts(newContactAttempts);

                                        return newSu;
                                }).collect(Collectors.toList())

                                ).flatMap(Collection::stream).collect(Collectors.toList());

                List<Assignement> assignements = distributedPearlSurveyUnits.stream().map(su -> {
                        String[] arr = su.getId().split("_");
                        return new Assignement(su.getId(), arr[2]);
                }).collect(Collectors.toList());
                LOGGER.debug(assignements.size() + " assignements generated");

                // 8 Queen : make uniq campaignId and questionnaireId
                String newQueenCampaignId = queenCampaign.getId() + "_" + orgaUnit.getOrganisationUnit() + "_"
                                + referenceDate;
                queenCampaign.setId(newQueenCampaignId);
                queenCampaign.setLabel(campaignLabel);
                LOGGER.debug("Generated queen campaignId : " + newQueenCampaignId);
                // awesome map to remember questionnaireId for queenSurveyUnits
                HashMap<String, String> questionnaireIdMapping = new HashMap<>();

                List<QuestionnaireModelDto> newQuestionnaireModels = questionnaireModels.stream().map(qm -> {
                        String newQuestionnaireModelId = qm.getIdQuestionnaireModel() + "_"
                                        + orgaUnit.getOrganisationUnit() + "_" + referenceDate;
                        questionnaireIdMapping.put(qm.getIdQuestionnaireModel(), newQuestionnaireModelId);
                        qm.setIdQuestionnaireModel(newQuestionnaireModelId);
                        qm.setCampaignId(newQueenCampaignId);
                        return qm;
                }).collect(Collectors.toList());

                List<String> newQuestionnaireIds = newQuestionnaireModels.stream()
                                .map(quest -> quest.getIdQuestionnaireModel()).collect(Collectors.toList());

                queenCampaign.setQuestionnaireIds((ArrayList<String>) newQuestionnaireIds);

                // 9 queen : generate queen survey_units with unique ids for each interviewer
                LOGGER.debug("Queen survey-units generation : " + interviewers.size() + " interviewers / "
                                + queenSurveyUnits.size() + " survey-units");

                List<SurveyUnitDto> distributedQueenSurveyUnits = interviewers.stream()
                                .map(in -> queenSurveyUnits.stream().map(sudto -> {
                                        String newId = sudto.getId() + "_" + campaign + "_" + in + "_" + referenceDate;
                                        String newQuestionnaireId = questionnaireIdMapping
                                                        .get(sudto.getQuestionnaireId());
                                        SurveyUnit newSu = new SurveyUnit(newId, newQuestionnaireId,
                                                        sudto.getStateData());
                                        SurveyUnitDto newSuDto = new SurveyUnitDto(sudto, newSu);
                                        return newSuDto;
                                }).collect(Collectors.toList())).flatMap(Collection::stream)
                                .collect(Collectors.toList());

                TrainingCourse trainingCourse = new TrainingCourse(distributedPearlSurveyUnits,
                                distributedQueenSurveyUnits, pearlCampaign, queenCampaign, newQuestionnaireModels,
                                nomenclatures, assignements);

                return trainingCourse;
        }

        private ArrayList<Visibility> updatingVisibilities(Long referenceDate, OrganisationUnitDto orgaUnit,
                        List<Visibility> previousVisibilities) {

                List<Visibility> newVisibilities = previousVisibilities.stream()
                                .map(v -> new Visibility(v, referenceDate)).map(v -> {
                                        v.setOrganizationalUnit(orgaUnit.getOrganisationUnit());
                                        return v;
                                }).collect(Collectors.toList());

                return new ArrayList<Visibility>(newVisibilities);

        }

        public List<TrainingScenario> getTrainingScenariiTitles() throws Exception {
                return new ArrayList<>(scenarii.values());
        }

        public TrainingCourse postTrainingCourse(TrainingCourse tc, HttpServletRequest request, Long referenceDate,
                        Plateform plateform, List<String> interviewers) {

                boolean pearlCampaignSuccess = false;
                boolean pearlSurveyUnitSuccess = false;
                boolean assignementSuccess = false;

                LOGGER.info("Trying to post pearl campaign");
                try {
                        pearlApiService.postCampaignToApi(request, tc.getPearlCampaign(), plateform);
                        pearlCampaignSuccess = true;
                } catch (Exception e) {
                        LOGGER.error("Error during creation campaign :" + tc.getPearlCampaign().getCampaign());
                        LOGGER.error(e.getMessage());
                }
                LOGGER.info("Trying to post " + tc.getPearlSurveyUnits().size() + " surveyUnits");
                try {
                        pearlApiService.postUesToApi(request, tc.getPearlSurveyUnits(), plateform);
                        pearlSurveyUnitSuccess = true;
                } catch (Exception e) {
                        LOGGER.error("Error during creation of surveyUnits");
                        LOGGER.error(e.getMessage());
                }
                LOGGER.info("Trying to post " + tc.getAssignements().size() + " assignements");
                try {
                        pearlApiService.postAssignementsToApi(request, tc.getAssignements(), plateform);
                        assignementSuccess = true;
                } catch (Exception e) {
                        LOGGER.error("Error during creation of assignements");
                        LOGGER.error(e.getMessage());
                }
                boolean pearlSuccess = pearlCampaignSuccess && pearlSurveyUnitSuccess && assignementSuccess;
                String pearlMessage = String.format("Campaign : %b, SurveyUnits: %b, Assignements: %b",
                                pearlCampaignSuccess, pearlSurveyUnitSuccess, assignementSuccess);
                LOGGER.info(pearlMessage);

                // POST queen entities
                long nomenclaturesSuccess;
                long questionnairesSuccess;
                long queenSurveyUnitsSuccess;
                boolean queenCampaignSuccess = false;

                LOGGER.info("Trying to post " + tc.getNomenclatures().size() + " nomenclatures");
                nomenclaturesSuccess = tc.getNomenclatures().stream().parallel().filter(n -> {
                        try {
                                queenApiService.postNomenclaturesToApi(request, n, plateform);
                                return true;
                        } catch (Exception e) {
                                LOGGER.error("Error during creation of nomenclature :" + n.getId());
                                LOGGER.error(e.getMessage());
                                return false;
                        }
                }).count();

                LOGGER.info("Trying to post " + tc.getQuestionnaireModels().size() + " questionnaires");
                questionnairesSuccess = tc.getQuestionnaireModels().stream().parallel().filter(q -> {
                        try {
                                queenApiService.postQuestionnaireModelToApi(request, q, plateform);
                                return true;
                        } catch (Exception e) {
                                LOGGER.error("Error during creation of questionnaire :" + q.getIdQuestionnaireModel());
                                LOGGER.error(e.getMessage());
                                return false;
                        }
                }).count();

                LOGGER.info("Trying to post campaign");
                try {
                        queenApiService.postCampaignToApi(request, tc.getQueenCampaign(), plateform);
                        queenCampaignSuccess = true;
                } catch (Exception e) {
                        LOGGER.error("Error during creation of campaignDto :" + tc.getQueenCampaign().getId());
                        LOGGER.error(e.getMessage());
                }
                LOGGER.info("Trying to post " + tc.getQueenSurveyUnits().size() + " survey-units");
                queenSurveyUnitsSuccess = tc.getQueenSurveyUnits().stream().parallel().filter(su -> {
                        try {
                                queenApiService.postUeToApi(request, su, tc.getQueenCampaign(), plateform);
                                return true;
                        } catch (Exception e) {
                                LOGGER.error("Error during creation of surveyUnit :" + su.getId());
                                LOGGER.error(e.getMessage());
                                return false;
                        }
                }).count();

                boolean queenSuccess = queenCampaignSuccess && nomenclaturesSuccess == tc.getNomenclatures().size()
                                && questionnairesSuccess == tc.getQuestionnaireModels().size()
                                && queenSurveyUnitsSuccess == tc.getQueenSurveyUnits().size();
                String queenMessage = String.format(
                                "Nomenclatures: %d/%d, Questionnaires: %d/%d, SurveyUnits: %d/%d, Campaign: %b",
                                nomenclaturesSuccess, tc.getNomenclatures().size(), questionnairesSuccess,
                                tc.getQuestionnaireModels().size(), queenSurveyUnitsSuccess,
                                tc.getQueenSurveyUnits().size(), queenCampaignSuccess);

                LOGGER.info(queenMessage);

                return pearlSuccess && queenSuccess ? tc : null;
        }

        public ResponseModel generateTrainingScenario(String scenarioId, String campaignLabel,
                        HttpServletRequest request, Long referenceDate, Plateform plateform,
                        List<String> interviewers) {

                boolean interviewersCheck = checkInterviewers(interviewers, request, plateform);

                if (!interviewersCheck) {
                        new ResponseModel(false, "Error when checking interviewers");
                }

                TrainingScenario scenar = trainingScenarioService.getTrainingScenario(tempScenariiFolder, scenarioId);

                List<TrainingCourse> trainingCourses = scenar.getCampaigns().stream().map(camp -> {
                        try {
                                return prepareTrainingCourse(camp.getCampaign(), scenarioId, camp.getCampaignLabel(),
                                                request, referenceDate, plateform, interviewers);
                        } catch (Exception e1) {
                                LOGGER.error("coudn't create training course " + camp.getCampaign(), e1);
                                e1.printStackTrace();
                                return null;
                        }
                }).collect(Collectors.toList());

                if (trainingCourses.contains(null)) {
                        rollBackOnFail(trainingCourses.stream().map(tc -> tc.getCampaignId())
                                        .collect(Collectors.toList()));
                        return new ResponseModel(false, "Error when loading campaigns");
                }

                boolean success = trainingCourses.stream()
                                .map(tc -> postTrainingCourse(tc, request, referenceDate, plateform, interviewers))
                                .filter(tc -> tc == null).collect(Collectors.toList()).size() == 0;

                if (!success) {
                        rollBackOnFail(trainingCourses.stream().map(tc -> tc.getCampaignId())
                                        .collect(Collectors.toList()));
                        return new ResponseModel(false, "Error when posting campaigns");
                }
                return new ResponseModel(true, "Training scenario generated");
        }

        public boolean checkInterviewers(List<String> interviewers, HttpServletRequest request, Plateform plateform) {
                InterviewerDto validInterviewer = new InterviewerDto();
                ArrayList<InterviewerDto> interviewerList = new ArrayList<>();
                interviewerList.add(validInterviewer);
                validInterviewer.setFirstName("FirstName");
                validInterviewer.setLastName("LasName");
                validInterviewer.setEmail("firstname.lastname@valid.net");
                validInterviewer.setPhoneNumer("+33000000000");
                return interviewers.stream().map(inter -> {
                        validInterviewer.setId(inter);
                        try {
                                ResponseEntity<?> postResponse = pearlApiService.postInterviewersToApi(request,
                                                interviewerList, plateform);
                                if (postResponse.getStatusCode() == HttpStatus.valueOf(200)
                                                || postResponse.getStatusCode() == HttpStatus.valueOf(400)) {
                                        return new ResponseEntity<>(HttpStatus.OK);
                                } else {
                                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                                }
                        } catch (JsonProcessingException e) {
                                LOGGER.debug("Interviewer " + inter + " already present");
                                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                        }

                }).filter(response -> !response.getStatusCode().is2xxSuccessful()).collect(Collectors.toList())
                                .size() == 0;

        }

        public ResponseEntity<String> deleteCampaign(HttpServletRequest request, Plateform plateform, String id) {
                LOGGER.debug("delete campaign : id = " + id);
                List<Campaign> pearlCampaigns = pearlApiService.getCampaigns(request, plateform);
                if (pearlCampaigns.stream().filter(camp -> camp.getId().equals(id)).count() == 0) {
                        LOGGER.error("DELETE campaign with id {} resulting in 404 because it does not exists", id);
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                ResponseEntity<String> pearlResponse = pearlApiService.deleteCampaign(request, plateform, id);
                ResponseEntity<String> queenResponse = queenApiService.deleteCampaign(request, plateform, id);
                LOGGER.info("DELETE campaign with id {} : pearl={} / queen={}", id,
                                pearlResponse.getStatusCode().toString(), queenResponse.getStatusCode().toString());
                return ResponseEntity.ok().build();

        }

}
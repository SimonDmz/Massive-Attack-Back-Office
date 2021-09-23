package fr.insee.sabianedata.ws.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fr.insee.sabianedata.ws.config.PearlProperties;
import fr.insee.sabianedata.ws.config.Plateform;
import fr.insee.sabianedata.ws.model.massiveAttack.OrganisationUnitDto;
import fr.insee.sabianedata.ws.model.massiveAttack.PearlUser;
import fr.insee.sabianedata.ws.model.pearl.Assignement;
import fr.insee.sabianedata.ws.model.pearl.Campaign;
import fr.insee.sabianedata.ws.model.pearl.CampaignDto;
import fr.insee.sabianedata.ws.model.pearl.GeoLocationDto;
import fr.insee.sabianedata.ws.model.pearl.InterviewerDto;
import fr.insee.sabianedata.ws.model.pearl.OrganisationUnitContextDto;
import fr.insee.sabianedata.ws.model.pearl.SurveyUnitDto;

@Service
public class PearlApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PearlApiService.class);

    @Autowired
    PearlProperties pearlProperties;

    @Autowired
    RestTemplate restTemplate;

    public ResponseEntity<?> postCampaignToApi(HttpServletRequest request, CampaignDto campaignDto, Plateform plateform)
            throws JsonProcessingException {
        LOGGER.info("Creating Campaign" + campaignDto.getCampaign());
        final String apiUri = pearlProperties.getHostFromEnum(plateform) + "/api/campaign";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(campaignDto, httpHeaders), String.class);
    }

    public ResponseEntity<?> postGeoLocationsToApi(HttpServletRequest request, List<GeoLocationDto> geoLocations,
            Plateform plateform) throws JsonProcessingException {
        LOGGER.info("Create GeoLocations ");
        final String apiUri = pearlProperties.getHostFromEnum(plateform) + "/api/geographical-locations";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(geoLocations, httpHeaders),
                String.class);
    }

    public ResponseEntity<?> postUesToApi(HttpServletRequest request, List<SurveyUnitDto> surveyUnits,
            Plateform plateform) throws JsonProcessingException {
        LOGGER.info("Create SurveyUnits ");
        final String apiUri = pearlProperties.getHostFromEnum(plateform) + "/api/survey-units";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(surveyUnits, httpHeaders), String.class);
    }

    public ResponseEntity<?> postInterviewersToApi(HttpServletRequest request, List<InterviewerDto> interviewers,
            Plateform plateform) throws JsonProcessingException {
        LOGGER.info("Create interviewers");
        final String apiUri = pearlProperties.getHostFromEnum(plateform) + "/api/interviewers";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(interviewers, httpHeaders),
                String.class);
    }

    public ResponseEntity<?> postAssignementsToApi(HttpServletRequest request, List<Assignement> assignements,
            Plateform plateform) {
        LOGGER.info("Create assignements");
        final String apiUri = pearlProperties.getHostFromEnum(plateform) + "/api/survey-units/interviewers";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(assignements, httpHeaders),
                String.class);
    }

    public ResponseEntity<?> postContextToApi(HttpServletRequest request,
            List<OrganisationUnitContextDto> organisationUnits, Plateform plateform) {
        LOGGER.info("Create Context (organisationUnits)");
        final String apiUri = pearlProperties.getHostFromEnum(plateform) + "/api/organization-units";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(organisationUnits, httpHeaders),
                String.class);
    }

    public HttpHeaders createSimpleHeadersAuth(HttpServletRequest request) {
        String authTokenHeader = request.getHeader("Authorization");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        if (!StringUtils.isBlank(authTokenHeader))
            httpHeaders.set("Authorization", authTokenHeader);
        return httpHeaders;
    }

    public OrganisationUnitDto getUserOrganizationUnit(HttpServletRequest request, Plateform plateform) {
        final String apiUri = pearlProperties.getHostFromEnum(plateform) + "/api/user";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {

            ResponseEntity<PearlUser> userResponse = restTemplate.exchange(apiUri, HttpMethod.GET,
                    new HttpEntity<>(httpHeaders), PearlUser.class);
            if (userResponse.getStatusCode() == HttpStatus.OK) {
                return userResponse.getBody().getOrganisationUnit();
            }
        } catch (Exception e) {
            LOGGER.error("Can't retrieve user organisational-unit");
            LOGGER.error(e.getMessage());
            return null;
        }
        return null;
    }

    public List<Campaign> getCampaigns(HttpServletRequest request, Plateform plateform) {
        final String apiUri = pearlProperties.getHostFromEnum(plateform) + "/api/campaigns";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        LOGGER.info("Trying to get campaigns list");
        ResponseEntity<Campaign[]> userResponse = restTemplate.exchange(apiUri, HttpMethod.GET,
                new HttpEntity<>(httpHeaders), Campaign[].class);
        if (userResponse.getStatusCode() == HttpStatus.OK) {
            LOGGER.info("API call for campaigns is OK");
            return Arrays.asList(userResponse.getBody());
        } else {
            LOGGER.warn("API call not OK");
        }
        return new ArrayList<>();
    }

    public ResponseEntity<String> deleteCampaign(HttpServletRequest request, Plateform plateform, String id) {
        LOGGER.info("pearl service : delete");
        final String apiUri = pearlProperties.getHostFromEnum(plateform) + "/api/campaign/" + id;
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.DELETE, new HttpEntity<>(id, httpHeaders), String.class);
    }

}

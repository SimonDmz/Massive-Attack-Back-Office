package fr.insee.sabianedata.ws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.insee.sabianedata.ws.config.Plateform;
import fr.insee.sabianedata.ws.config.QueenProperties;
import fr.insee.sabianedata.ws.model.queen.CampaignDto;
import fr.insee.sabianedata.ws.model.queen.NomenclatureDto;
import fr.insee.sabianedata.ws.model.queen.QuestionnaireModelDto;
import fr.insee.sabianedata.ws.model.queen.SurveyUnitDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Arrays;

@Service
public class QueenApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueenApiService.class);

    @Autowired
    QueenProperties queenProperties;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    @Qualifier("webClientDV")
    private WebClient webClientDV;

    @Autowired
    @Qualifier("webClientQF1")
    private WebClient webClientQF1;

    @Autowired
    @Qualifier("webClientQF2")
    private WebClient webClientQF2;

    @Autowired
    @Qualifier("webClientQF3")
    private WebClient webClientQF3;

    @Autowired
    @Qualifier("webClientCLOUD")
    private WebClient webClientCLOUD;


    public void postCampaignToApi(HttpServletRequest request, CampaignDto campaignDto, Plateform plateform) {
        LOGGER.info("Creating Campaign"+campaignDto.getId());
        WebClient webClient = getWebClientFromEnum(plateform);
        Mono<?> requestSend = webClient.post()
                .uri("/api/campaigns")
                .header("Authorization",getAuthHeader(request))
                .body(Mono.just(campaignDto), CampaignDto.class)
                .retrieve()
                .bodyToMono(Void.class);
        requestSend.subscribe();
    }

    public void postUeToApi(HttpServletRequest request, SurveyUnitDto surveyUnitDto, CampaignDto campaignDto, Plateform plateform) throws JsonProcessingException {
        LOGGER.info("Create SurveyUnit "+surveyUnitDto.getId());
        String idCampaign = campaignDto.getId();
        WebClient webClient = getWebClientFromEnum(plateform);
        Mono<?> requestSend = webClient.post()
                .uri("/api/campaign/"+idCampaign+"/survey-unit")
                .header("Authorization",getAuthHeader(request))
                .body(Mono.just(surveyUnitDto), SurveyUnitDto.class)
                .retrieve()
                .bodyToMono(Void.class);
        requestSend.subscribe();
    }
    public void postNomenclaturesToApi(HttpServletRequest request, NomenclatureDto nomenclatureDto, Plateform plateform) {
        LOGGER.info("Create nomenclature "+nomenclatureDto.getId());
        WebClient webClient = getWebClientFromEnum(plateform);
        Mono<?> requestSend = webClient.post()
                .uri("/api/nomenclature")
                .header("Authorization",getAuthHeader(request))
                .body(Mono.just(nomenclatureDto), NomenclatureDto.class)
                .retrieve()
                .bodyToMono(Void.class);
        requestSend.subscribe();
    }
    public void postQuestionnaireModelToApi(HttpServletRequest request, QuestionnaireModelDto questionnaireModelDto, Plateform plateform) {
        LOGGER.info("Create Questionnaire "+questionnaireModelDto.getIdQuestionnaireModel());
        WebClient webClient = getWebClientFromEnum(plateform);
        Mono<?> requestSend = webClient.post()
                .uri("/api/questionnaire-models")
                .header("Authorization",getAuthHeader(request))
                .body(Mono.just(questionnaireModelDto), QuestionnaireModelDto.class)
                .retrieve()
                .bodyToMono(Void.class);
        requestSend.subscribe();
    }

    public ResponseEntity<String> createFullCampaign(HttpServletRequest request, File campaignZip, Plateform plateform) {
        final String apiUri = queenProperties.getHostFromEnum(plateform) +"/api/campaign/context";
        MultiValueMap<String, Object> body
                = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(campaignZip));
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, httpHeaders);
        return restTemplate.postForEntity(apiUri, requestEntity, String.class);
    }

    private HttpHeaders createSimpleHeadersAuth(HttpServletRequest request){
        String authTokenHeader = request.getHeader("Authorization");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        if (!StringUtils.isBlank(authTokenHeader)) httpHeaders.set("Authorization",authTokenHeader);
        return httpHeaders;
    }

    private String getAuthHeader(HttpServletRequest request){
        return request.getHeader("Authorization");
    }

    private WebClient getWebClientFromEnum(Plateform plateform){
        WebClient webClient=null;
        switch (plateform){
            case DV:
                webClient=webClientDV;
                break;
            case QF1:
                webClient=webClientQF1;
                break;
            case QF2:
                webClient=webClientQF2;
                break;
            case QF3:
                webClient=webClientQF3;
                break;
            case CLOUD:
                webClient=webClientCLOUD;
                break;
        }
        return webClient;
    }


}

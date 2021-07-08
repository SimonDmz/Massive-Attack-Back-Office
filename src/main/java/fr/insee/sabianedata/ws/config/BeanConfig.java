package fr.insee.sabianedata.ws.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;


@Configuration
public class BeanConfig {

    @Autowired
    QueenProperties queenProperties;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**");
            }
        };
    }
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean(name = "webClientDV")
    public WebClient webClientDV(){
        return getWebClientFromEnum(Plateform.DV);
    }

    @Bean(name = "webClientQF1")
    public WebClient webClientQF1(){
        return getWebClientFromEnum(Plateform.QF1);
    }

    @Bean(name = "webClientQF2")
    public WebClient webClientQF2(){
        return getWebClientFromEnum(Plateform.QF2);
    }

    @Bean(name = "webClientQF3")
    public WebClient webClientQF3(){
        return getWebClientFromEnum(Plateform.QF3);
    }

    @Bean(name = "webClientCLOUD")
    public WebClient webClientCLOUD(){
        return getWebClientFromEnum(Plateform.CLOUD);
    }


    private WebClient getWebClientFromEnum(Plateform plateform) {
        return WebClient.builder()
                .baseUrl(queenProperties.getHostFromEnum(plateform))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}
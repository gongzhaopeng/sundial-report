package cn.benbenedu.sundial.report.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

@Configuration
@EnableResourceServer
public class ResourceServerConfigurer
        extends ResourceServerConfigurerAdapter {

    private static final String RESOURCE_ID = "REPORT_API";

    private RemoteTokenServices remoteTokenServices;

    @Autowired
    public ResourceServerConfigurer(
            RemoteTokenServices remoteTokenServices) {

        this.remoteTokenServices = remoteTokenServices;
    }

    @PostConstruct
    public void init() {

        remoteTokenServices.setRestTemplate(restTemplateForRemoteTokenServices());
        remoteTokenServices.setAccessTokenConverter(accessTokenConverter());
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources)
            throws Exception {

        resources.resourceId(RESOURCE_ID);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .anyRequest().authenticated();
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplateForRemoteTokenServices() {

        final var restTemplate = new RestTemplate();

        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            // Ignore 400
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 400) {
                    super.handleError(response);
                }
            }
        });

        return restTemplate;
    }

    @Bean
    public AccessTokenConverter accessTokenConverter() {

        final var accessTokenConverter = new DefaultAccessTokenConverter() {

            private static final String CLIENT = "client";

            @Override
            public OAuth2Authentication extractAuthentication(Map<String, ?> map) {

                final var oAuth2Authentication = super.extractAuthentication(map);

                final var clientDetails = map.get(CLIENT);
                if (clientDetails != null) {
                    oAuth2Authentication.getOAuth2Request()
                            .getExtensions().putAll((Map) clientDetails);
                }

                return oAuth2Authentication;
            }
        };

        accessTokenConverter.setUserTokenConverter(userAuthenticationConverter());

        return accessTokenConverter;
    }

    @Bean
    public UserAuthenticationConverter userAuthenticationConverter() {

        return new DefaultUserAuthenticationConverter() {

            private static final String USER = "user";

            @Override
            public Authentication extractAuthentication(Map<String, ?> map) {

                final var authentication = super.extractAuthentication(map);

                if (authentication != null) {

                    final var authenticationToken =
                            (AbstractAuthenticationToken) authentication;
                    authenticationToken.setDetails(map.get(USER));
                }

                return authentication;
            }
        };
    }
}

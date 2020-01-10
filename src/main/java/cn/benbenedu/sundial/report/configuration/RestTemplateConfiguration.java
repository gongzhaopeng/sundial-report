package cn.benbenedu.sundial.report.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.web.client.RestTemplate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Configuration
public class RestTemplateConfiguration {

    private static final String SERVICE_INNER_SCOPE = "service:inner";

    @Bean
    @Primary
    public OAuth2RestTemplate oauth2RestTemplate(
            UserInfoRestTemplateFactory factory) {

        return factory.getUserInfoRestTemplate();
    }

    @Bean
    @LoadBalanced
    @Pure
    public RestTemplate restTemplate() {

        return new RestTemplate();
    }

    @Bean
    @IndependentOauth
    public OAuth2RestTemplate indOauth2RestTemplate(
            ResourceServerProperties resourceServerProperties,
            OAuth2ProtectedResourceDetails oAuth2ProtectedResourceDetails,
            LoadBalancerInterceptor loadBalancerInterceptor) {

        final var resourceDetails =
                new ClientCredentialsResourceDetails();
        resourceDetails.setClientId(
                resourceServerProperties.getClientId());
        resourceDetails.setClientSecret(
                resourceServerProperties.getClientSecret());
        resourceDetails.setGrantType("client_credentials");
        resourceDetails.setScope(List.of(SERVICE_INNER_SCOPE));
        resourceDetails.setAccessTokenUri(
                oAuth2ProtectedResourceDetails.getAccessTokenUri());

        final var oauth2RestTemplate =
                new OAuth2RestTemplate(resourceDetails);

        final var clientCredentialsAccessTokenProvider =
                new ClientCredentialsAccessTokenProvider();
        clientCredentialsAccessTokenProvider.setInterceptors(
                List.of(loadBalancerInterceptor));
        final var accessTokenProvider = new AccessTokenProviderChain(
                List.of(clientCredentialsAccessTokenProvider));
        oauth2RestTemplate.setAccessTokenProvider(accessTokenProvider);

        oauth2RestTemplate.setInterceptors(
                List.of(loadBalancerInterceptor));

        return oauth2RestTemplate;
    }

    @Target({
            ElementType.CONSTRUCTOR,
            ElementType.FIELD,
            ElementType.METHOD,
            ElementType.TYPE,
            ElementType.PARAMETER
    })
    @Retention(RetentionPolicy.RUNTIME)
    @Qualifier
    public @interface IndependentOauth {
    }

    @Target({
            ElementType.CONSTRUCTOR,
            ElementType.FIELD,
            ElementType.METHOD,
            ElementType.TYPE,
            ElementType.PARAMETER
    })
    @Retention(RetentionPolicy.RUNTIME)
    @Qualifier
    public @interface Pure {
    }
}

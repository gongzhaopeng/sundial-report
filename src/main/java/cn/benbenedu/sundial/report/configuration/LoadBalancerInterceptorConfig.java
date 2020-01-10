package cn.benbenedu.sundial.report.configuration;

import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequestFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class LoadBalancerInterceptorConfig {

    @Bean
    @Primary
    public LoadBalancerInterceptor loadBalancerInterceptor(
            LoadBalancerClient loadBalancerClient,
            LoadBalancerRequestFactory requestFactory) {

        return new LoadBalancerInterceptor(loadBalancerClient, requestFactory);
    }
}

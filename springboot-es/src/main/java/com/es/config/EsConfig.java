package com.es.config;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class EsConfig {

    @Value("${es.ips}")
    private String ips;

    @Value("${es.port}")
    private int port;

    @Value("${es.connect-timeout}")
    private int connectTimeout;

    @Value("${es.socket-timeout}")
    private int socketTimeout;

    @Value("${es.maxretry-timeout}")
    private int maxRetryTimeout;

    @Bean
    public RestClient restClient() {
        String[] ipArray = ips.split(",");
        HttpHost[] httpArray = new HttpHost[ipArray.length];
        for (int i = 0; i < ipArray.length; i++) {
            httpArray[i] = new HttpHost(ipArray[i], port, "http");
        }
        RestClientBuilder restClientBuilder = RestClient.builder(httpArray)
                .setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                    @Override
                    public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder) {
                        return builder.setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout);
                    }
                }).setMaxRetryTimeoutMillis(maxRetryTimeout);
        return restClientBuilder.build();
    }
}

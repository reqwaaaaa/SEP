package com.pidu.common.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 腾讯云COS配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "cos")
public class CosConfig {

    /**
     * SecretId
     */
    private String secretId;

    /**
     * SecretKey
     */
    private String secretKey;

    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * 地域
     */
    private String region = "ap-chengdu";

    /**
     * 访问域名
     */
    private String domain;

    /**
     * 是否启用
     */
    private boolean enabled = false;

    @Bean
    @ConditionalOnProperty(prefix = "cos", name = "enabled", havingValue = "true")
    public COSClient cosClient() {
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        clientConfig.setHttpProtocol(HttpProtocol.https);
        return new COSClient(cred, clientConfig);
    }
}

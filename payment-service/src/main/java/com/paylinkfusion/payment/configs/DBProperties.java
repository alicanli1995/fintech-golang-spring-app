package com.paylinkfusion.payment.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class DBProperties {

    @Value("${spring.couchbase.bootstrap-hosts}")
    private String hostName;
    @Value("${spring.couchbase.bucket.user}")
    private String username;
    @Value("${spring.couchbase.bucket.password}")
    private String password;
    @Value("${spring.couchbase.bucket.name}")
    private String bucketName;


}

package com.paylinkfusion.payment.configs;

import com.couchbase.client.core.msg.kv.DurabilityLevel;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.BucketType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CouchbaseConfig {

    private final DBProperties dbProp;

    @Bean(destroyMethod = "disconnect")
    public Cluster getCouchbaseCluster() {
        return Cluster.connect(dbProp.getHostName(), dbProp.getUsername(), dbProp.getPassword());
    }

    @Bean
    public Bucket getCouchbaseBucket(Cluster cluster) {
        if (!cluster.buckets().getAllBuckets().containsKey(dbProp.getBucketName())) {
            cluster.buckets().createBucket(
                    BucketSettings.create(dbProp.getBucketName()).bucketType(BucketType.COUCHBASE)
                            .minimumDurabilityLevel(DurabilityLevel.NONE).ramQuotaMB(128));
        }
        final var bucket = cluster.bucket(dbProp.getBucketName());
        createIfNotExistPrimaryIndex(cluster);
        return bucket;
    }

    private void createIfNotExistPrimaryIndex(Cluster cluster) {
        try {
            cluster.query("CREATE PRIMARY INDEX ON `" + dbProp.getBucketName() + "`._default._default");
        } catch (Exception e) {
            log.info("Primary index already exists");
        }
    }
}

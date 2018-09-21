package com.zlikun.jee.conf;

import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置注册中心
 * @author zlikun
 * @date 2018-09-21 17:36
 */
@Configuration
public class RegistryCenterConfigure {

    @Value("${elastic.job.servers}")
    private String servers;
    @Value("${elastic.job.namespace}")
    private String namespace;

    @Bean
    public ZookeeperConfiguration zookeeperConfiguration() {
        ZookeeperConfiguration config = new ZookeeperConfiguration(servers, namespace);
        config.setMaxRetries(3);
        config.setBaseSleepTimeMilliseconds(1000);
        config.setSessionTimeoutMilliseconds(60_000);
        config.setMaxSleepTimeMilliseconds(3000);
        config.setConnectionTimeoutMilliseconds(15_000);
        config.setDigest(null);
        return config;
    }

    /**
     * 配置注册中心<br>
     * http://elasticjob.io/docs/elastic-job-lite/02-guide/config-manual/
     *
     * @return
     */
    @Bean(initMethod = "init")
    public CoordinatorRegistryCenter coordinatorRegistryCenter() {
        return new ZookeeperRegistryCenter(zookeeperConfiguration());
    }

}

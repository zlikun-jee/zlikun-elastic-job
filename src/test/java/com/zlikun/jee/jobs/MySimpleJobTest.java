package com.zlikun.jee.jobs;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * 测试Job执行<br>
 * http://elasticjob.io/docs/elastic-job-lite/01-start/quick-start/
 *
 * @author zlikun
 * @date 2018-09-21 16:44
 */
public class MySimpleJobTest {

    private String servers = "192.168.3.96:2181";
    private String namespace = "elastic-job-demo";

    @Test @Ignore
    public void execute() throws InterruptedException {

        // 启动任务调度器
        new JobScheduler(createRegistryCenter(), createJobConfiguration()).init();

        // 等任务执行完
        TimeUnit.SECONDS.sleep(30L);

    }

    /**
     * 创建LiteJob配置<br>
     * http://elasticjob.io/docs/elastic-job-lite/01-start/dev-guide/
     *
     * @return
     */
    private LiteJobConfiguration createJobConfiguration() {

        // 配置分三个层级，分别是Core、Type和Root，每个层级使用类似于装饰者模式的方式配置
        // Core对应JobCoreConfiguration，用于提供作业核心配置信息，如：作业名称、分片总数、CRON表达式等。
        // Type对应JobTypeConfiguration，有3个子类分别对应SIMPLE, DATAFLOW和SCRIPT类型作业。
        // Root对应JobRootConfiguration，有2个子类分别对应Lite和Cloud部署类型，提供不同部署类型所需的配置。

        // 定义CORE配置
        JobCoreConfiguration coreConfiguration = JobCoreConfiguration
                .newBuilder("my-simple-job", "0/3 * * * * ? *", 4)
                .build();
        // 定义SIMPLE配置
        SimpleJobConfiguration simpleJobConfiguration = new SimpleJobConfiguration(coreConfiguration, MySimpleJob.class.getCanonicalName());
        // 定义ROOT配置
        return LiteJobConfiguration.newBuilder(simpleJobConfiguration).build();
    }

    /**
     * 创建一个注册中心
     *
     * @return
     */
    private CoordinatorRegistryCenter createRegistryCenter() {
        CoordinatorRegistryCenter registryCenter = new ZookeeperRegistryCenter(
                new ZookeeperConfiguration(servers, namespace));
        registryCenter.init();
        return registryCenter;
    }

}
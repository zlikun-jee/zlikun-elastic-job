package com.zlikun.jee.conf;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author zlikun
 * @date 2018-09-21 17:11
 */
@Configuration
public class SimpleJobConfigure {

    @Autowired
    private CoordinatorRegistryCenter coordinatorRegistryCenter;

    @Autowired
    private DataSource dataSource;

    @Bean
    public JobEventRdbConfiguration jobEventRdbConfiguration() {
        return new JobEventRdbConfiguration(dataSource);
    }

    @Bean(initMethod = "init")
    public JobScheduler simpleJobScheduler(final SimpleJob simpleJob) {
        // 分片数应大于等于分片参数，否则会导致比分片数大的部分无法被分片
        // 分片数大于分片参数时，多出的部分分片参数为null，但分片项（Item）会自动编号
        // 如果分片参数不是从0开始计，那么其起始项前的参数都为null，比如“1=A,2=B”，那么“0=null,3=null”，分片项一定是从0开始算的
        // 分片数由最新启动的实例动态修改，默认分片方式为等分，如：8片，2个实例，实例A[0,1,2,3]、实例B[4,5,6,7]
        // ElasticJob会自动进行分片，将分片参数拆分开，分配给不同实例（实例级别，所以对同一主机多个实例也适用）
        LiteJobConfiguration liteJobConfiguration = getLiteJobConfiguration(simpleJob.getClass(),
                "0/3 * * * * ? *",
                8,
                "1=A,2=B,3=C,4=D");
        return new SpringJobScheduler(simpleJob, coordinatorRegistryCenter, liteJobConfiguration, jobEventRdbConfiguration());
    }

    private LiteJobConfiguration getLiteJobConfiguration(final Class<? extends SimpleJob> jobClass,
                                                         final String cron,
                                                         final int shardingTotalCount,
                                                         final String shardingItemParameters) {
        JobCoreConfiguration jobCoreConfiguration = JobCoreConfiguration
                .newBuilder(jobClass.getName(), cron, shardingTotalCount).shardingItemParameters(shardingItemParameters)
                .build();
        return LiteJobConfiguration
                .newBuilder(new SimpleJobConfiguration(jobCoreConfiguration, jobClass.getCanonicalName()))
                .overwrite(true)
                .build();
    }

}

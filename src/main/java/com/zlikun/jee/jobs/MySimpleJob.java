package com.zlikun.jee.jobs;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Simple作业类型<br>
 * http://elasticjob.io/docs/elastic-job-lite/01-start/quick-start/<br>
 * http://elasticjob.io/docs/elastic-job-lite/01-start/dev-guide/<br>
 *
 * @author zlikun
 * @date 2018-09-21 16:41
 */
@Slf4j
@Component
public class MySimpleJob implements SimpleJob {

    /** 这里实际上并不需要加锁，只是为了避免输出日志错乱才加，调试用 */
    private Lock lock = new ReentrantLock();

    private AtomicLong counter = new AtomicLong();

    @Override
    public void execute(ShardingContext context) {

        lock.lock();

        long num = counter.incrementAndGet();
        log.info("--------------------------------------------------{}", String.format("%04d", num));

        // 任务名称（类名）、任务分片数
        // jobName => com.zlikun.jee.jobs.MySimpleJob, shardingTotalCount => 8
        log.info("jobName => {}, shardingTotalCount => {}", context.getJobName(), context.getShardingTotalCount());

        // 同一实例在运行多个分片时，taskId是相同的，其由任务类、分片项、IP、进程号组成
        // 不同实例运行同一任务的其它分片时，taskId是不同的，参考其构成规则
        // taskId => com.zlikun.jee.jobs.MySimpleJob@-@0,1,2,3,4,5,6,7@-@READY@-@192.168.0.181@-@15144
        log.info("taskId => {}", context.getTaskId());

        // 分片项（编号）、分片值
        // shardingItem => 1, shardingParameter => A
        log.info("shardingItem => {}, shardingParameter => {}", context.getShardingItem(), context.getShardingParameter());

        log.info("=================================================={}", String.format("%04d", num));

        lock.unlock();
    }

}

package burp;

import java.time.Instant;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: jiangchunliang
 * @Data: 2022/3/30 2:50 下午
 * @Description: burp
 * @version: v1.1
 */
public class BurpThreadPoolUtil {
    private static final int CORE_POOL_SIZE = 4;
    private static final int MAX_POOL_SIZE = 8;
    private static final int QUEUE_CAPACITY = 100;
    private static final Long KEEP_ALIVE_TIME = 1L;
    private volatile static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(QUEUE_CAPACITY),
            new MyThreadFactor("burp"),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public synchronized static ThreadPoolExecutor getInstance(){
        if(executor==null){
            synchronized (BurpThreadPoolUtil.class){
                if(executor==null){
                    synchronized (BurpThreadPoolUtil.class){
                        executor = new ThreadPoolExecutor(
                                CORE_POOL_SIZE,
                                MAX_POOL_SIZE,
                                KEEP_ALIVE_TIME,
                                TimeUnit.SECONDS,
                                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                                new MyThreadFactor("burp"),
                                new ThreadPoolExecutor.CallerRunsPolicy());
                    }
                }
            }
        }
        return executor;
    }




}

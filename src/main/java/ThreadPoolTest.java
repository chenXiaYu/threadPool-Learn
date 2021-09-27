

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolTest {

    public   static final  Logger logger;

    static {
        logger = LoggerFactory.getLogger(ThreadPoolTest.class);

    }

    @Test
    public void newCachedThreadPool(){
        ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactory() {
            AtomicInteger atomicInteger = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "CachedThread" + atomicInteger.addAndGet(1));
            }
        });

       for(int i = 0; i < 10; i++){
           int finalI = i;
           executorService.submit(new Runnable() {
               @Override
               public void run() {
                   System.out.println("线程"+finalI+"执行完毕");
               }
           });
       }

       //等待线程执行完毕再关闭，没有这个可能先十次执行不能完全打印出来
       executorService.shutdown();
    }

    @Test
    public void  fixedThreadPool(){
        ExecutorService executorService = Executors.newFixedThreadPool(2, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r);
            }
        });

        for(int i =0; i < 10; i++){
            int index = i;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    logger.info("线程"+index+"执行完毕");
                }
            });
        }

        executorService.shutdown();
    }


    @Test
    public void singleThreadPool(){
        ExecutorService executorService = Executors.newSingleThreadExecutor( new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r);
            }
        });

        for(int i =0; i < 10; i++){
            int index = i;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    logger.info("线程"+index+"执行完毕");
                }
            });
        }

       executorService.shutdown();
       // executorService.shutdownNow(); // 马上关掉，线程会被打断，后续的不执行
    }


    @Test
    public void scheduleThreadPool() throws InterruptedException {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for(int i =0; i < 10; i++){
            int index = i;
            executorService.schedule(
                    new Runnable() {
                        @Override
                        public void run() {
                            logger.info("线程"+index+"执行完毕");
                            countDownLatch.countDown();
                        }
                    }, 2l, TimeUnit.SECONDS);
        }
        //这个时候，这个shutdown无法等待提交的计划完全执行完毕，
        //executorService.shutdown();

        while (true){
            if(countDownLatch.getCount()==0){
                System.exit(0);
            }
        }
    }

    /**
     * initialDelay - 延迟第一次执行的时间
     * period - 连续执行之间的时期
     * 这里的延迟都是针对单个任务而言
     * 输出结果
     * 2021-09-26 10:43:16.509 [pool-1-thread-3] INFO  ThreadPoolTest-线程2执行完毕
     * 2021-09-26 10:43:16.515 [pool-1-thread-3] INFO  ThreadPoolTest-线程3执行完毕
     * 2021-09-26 10:43:16.515 [pool-1-thread-3] INFO  ThreadPoolTest-线程4执行完毕
     * 2021-09-26 10:43:16.515 [pool-1-thread-3] INFO  ThreadPoolTest-线程5执行完毕
     * 2021-09-26 10:43:16.515 [pool-1-thread-3] INFO  ThreadPoolTest-线程6执行完毕
     * 2021-09-26 10:43:16.515 [pool-1-thread-3] INFO  ThreadPoolTest-线程7执行完毕
     * 2021-09-26 10:43:16.515 [pool-1-thread-3] INFO  ThreadPoolTest-线程8执行完毕
     * 2021-09-26 10:43:16.515 [pool-1-thread-3] INFO  ThreadPoolTest-线程9执行完毕
     * 2021-09-26 10:43:16.509 [pool-1-thread-1] INFO  ThreadPoolTest-线程0执行完毕
     * 2021-09-26 10:43:16.509 [pool-1-thread-2] INFO  ThreadPoolTest-线程1执行完毕
     * @throws InterruptedException
     */
    @Test
    public void scheduleThreadPool2() throws InterruptedException {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for(int i =0; i < 10; i++){
            int index = i;
            executorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            logger.info("线程"+index+"执行完毕");
                            countDownLatch.countDown();
                        }
                    }, 1l, 2l, TimeUnit.SECONDS);
        }
        //这个时候，这个shutdown无法等待提交的计划完全执行完毕，
        //executorService.shutdown();

        while (true){
            if(countDownLatch.getCount()==0){
                System.exit(0);
            }
        }
    }


    /**
     * 输出结果
     *2021-09-26 10:47:51.066 [pool-1-thread-1] INFO  ThreadPoolTest-线程0执行完毕
     * 2021-09-26 10:47:52.082 [pool-1-thread-1] INFO  ThreadPoolTest-线程1执行完毕
     * 2021-09-26 10:47:53.084 [pool-1-thread-1] INFO  ThreadPoolTest-线程2执行完毕
     * 2021-09-26 10:47:54.098 [pool-1-thread-1] INFO  ThreadPoolTest-线程3执行完毕
     * 2021-09-26 10:47:55.101 [pool-1-thread-1] INFO  ThreadPoolTest-线程4执行完毕
     * 2021-09-26 10:47:56.105 [pool-1-thread-1] INFO  ThreadPoolTest-线程5执行完毕
     * 2021-09-26 10:47:57.112 [pool-1-thread-1] INFO  ThreadPoolTest-线程6执行完毕
     * 2021-09-26 10:47:58.127 [pool-1-thread-1] INFO  ThreadPoolTest-线程7执行完毕
     * 2021-09-26 10:47:59.141 [pool-1-thread-1] INFO  ThreadPoolTest-线程8执行完毕
     * 2021-09-26 10:48:00.143 [pool-1-thread-1] INFO  ThreadPoolTest-线程9执行完毕
     * 2021-09-26 10:48:01.149 [pool-1-thread-1] INFO  ThreadPoolTest-线程0执行完毕
     * 这种则是卡在单线上面，如果一个线程不卡时间，在cpu快速运行下还是很快的
     * Process finished with exit code 0
     * @throws InterruptedException
     */
    @Test
    public void singleScheduleThreadPool() throws InterruptedException {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for(int i =0; i < 10; i++){
            int index = i;
            executorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            logger.info("线程"+index+"执行完毕");
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            countDownLatch.countDown();
                        }
                    }, 1l, 2l, TimeUnit.SECONDS);
        }
        //这个时候，这个shutdown无法等待提交的计划完全执行完毕，
        //executorService.shutdown();

        while (true){
            if(countDownLatch.getCount()==0){
                System.exit(0);
            }
        }
    }


    @Test
    public void testReturnTheadPool() throws ExecutionException, InterruptedException, TimeoutException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<Integer> submit = executorService.submit(() -> {
            try {
                Thread.sleep(4000);
            }catch (Exception e){
                logger.info("狗腿已经被打断");
            }

            return 123;
        });

        //Integer integer = submit.get();

        //这种情况下如果规定时间内没有返回值，则会抛出异常需要自行处理
        //Integer integer = submit.get(1L, TimeUnit.SECONDS);

        //打断正在运行的程序
        Thread.sleep(1000); // 如果主线程在线程池没有执行的情况下是不会触发打断异常的
        boolean cancel = submit.cancel(true);
        System.out.println(cancel);
    }

    //-------------------------------不建议使用Executors直接创建线程----------------------------------

    @Test
    public void test1(){
        ThreadPoolExecutor threadPoolExecutor =
                new ThreadPoolExecutor(1,
                        5,
                        5,
                        TimeUnit.SECONDS,
                        new LinkedBlockingDeque<>(2),
                        new ThreadPoolExecutor.CallerRunsPolicy());

        for(int index = 0 ; index < 20; index++){
           try{
               int finalIndex = index;
               threadPoolExecutor.submit(new Runnable() {
                   @Override
                   public void run() {
                       System.out.println("---------------------------");
                       System.out.println(finalIndex);
                       System.out.println(Thread.currentThread().getName());
                       System.out.println("poolSize"+threadPoolExecutor.getPoolSize());
                       System.out.println("MaximumPoolSize"+threadPoolExecutor.getMaximumPoolSize());
                       System.out.println("QueueSize"+threadPoolExecutor.getQueue().size());
                       System.out.println("---------------------------");
                   }
               });
           }catch (Exception e){
               if(e instanceof  RejectedExecutionException){
                   System.out.println("啊 啊 啊 满了 溢出了");
               }
           }
        }

        threadPoolExecutor.shutdown();
    }
}

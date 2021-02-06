import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.*;

import static java.lang.System.exit;

/**
 * 本周作业：（必做）思考有多少种方式，在main函数启动一个新线程或线程池，
 * 异步运行一个方法，拿到这个方法的返回值后，退出主线程？
 * 写出你的方法，越多越好，提交到github。
 *
 * 写了11种方法生成线程或线程池
 * @Author rainshell
 */
public class CreateTreadsService {

    static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);

    static ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(16);

    static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    static ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

    static ExecutorService workStealingThreadPool = Executors.newWorkStealingPool();

    static ExecutorService forkJoinPool = new ForkJoinPool();

    static ExecutorService singleThreadScheduledThreadPool = Executors.newSingleThreadScheduledExecutor();

    static ExecutorService customThreadPool = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors()
            , 2 * Runtime.getRuntime().availableProcessors()
            , 30
            , TimeUnit.SECONDS
            , new ArrayBlockingQueue<>(1000)
            , new ThreadPoolExecutor.CallerRunsPolicy()
    );

    static ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        for(int i =1; i<=11; i++) {
            long start=System.currentTimeMillis();
            // 在这里创建一个线程或线程池，异步执行 下面方法
            Integer r = getResult(i);
            System.out.println("异步计算结果为："+r);
            System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
        }
        // 然后退出main线程
        exit(0);
    }

    public static Integer getResult(int i) throws ExecutionException, InterruptedException {

        TaskCallable taskCallable = new TaskCallable();
        Integer result = null;
        switch (i) {
            case 1:
                FutureTask<Integer> futureTask = new FutureTask<>(taskCallable);
                Thread thread3 = new Thread(futureTask);
                thread3.start();
                result = futureTask.get();
                System.out.println("执行方法"+i+"：FutureTask");
                break;
            case 2:
                result =  fixedThreadPool.submit(taskCallable).get();
                System.out.println("执行方法"+i+"：fixedThreadPool.submit");
                break;
            case 3:
                result = scheduledThreadPool.submit(taskCallable).get();
                System.out.println("执行方法"+i+"：scheduledThreadPool.submit");
                break;
            case 4:
                result = cachedThreadPool.submit(taskCallable).get();
                System.out.println("执行方法"+i+"：cachedThreadPool.submit");
                break;
            case 5:
                result = singleThreadPool.submit(taskCallable).get();
                System.out.println("执行方法"+i+"：singleThreadPool.submit");
                break;
            case 6:
                result = customThreadPool.submit(taskCallable).get();
                System.out.println("执行方法"+i+"：customThreadPool.submit");
                break;
            case 7:
                result = workStealingThreadPool.submit(taskCallable).get();
                System.out.println("执行方法"+i+"：workStealingThreadPool.submit");
                break;
            case 8:
                result = forkJoinPool.submit(taskCallable).get();
                System.out.println("执行方法"+i+"：forkJoinPool.submit");
                break;
            case 9:
                result = singleThreadScheduledThreadPool.submit(taskCallable).get();
                System.out.println("执行方法"+i+"：singleThreadScheduledThreadPool.submit");
                break;
            case 10:
                CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(CreateTreadsService::sum, customThreadPool);
                result = completableFuture.get();
                System.out.println("执行方法"+i+"：CompletableFuture.supplyAsync");
                break;
            case 11:
                ListenableFuture listenableFuture = listeningExecutorService.submit(taskCallable);
                result = (Integer)listenableFuture.get();
                System.out.println("执行方法"+i+"：listeningExecutorService.submit");
                break;
        }

        return result;
    }

    public static class TaskCallable implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            return sum();
        }
    }


    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if ( a < 2)
            return 1;
        return fibo(a-1) + fibo(a-2);
    }


}

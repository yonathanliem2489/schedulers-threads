package demo.clean.code.service;

import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ThreadsTests {

  @Test
  public void singleThreads_Test() throws InterruptedException {
    for (int i = 0; i < 10; i++) {
      Thread thread = new Thread(new ThreadCommon(i));
      thread.setName("userCommonThread");
      thread.start();
    }

    Thread.sleep(3000L);
  }

  private class ThreadCommon implements Runnable {

    private String number;

    public ThreadCommon(int i) {
      this.number = String.valueOf(i);
    }

    @Override
    public void run() {
      log.info("Thread, number {}", number);
      try {
        Thread.sleep(500L);
      } catch (InterruptedException ex) {
        log.error("InterruptedException, message {}", ex.getMessage());
      }
    }
  }

  @Test
  public void multiThreads_Test() {
    for (int i = 0; i < 15; i++) {
      Thread thread = new Thread(new ThreadCommon(i));
      thread.setName("userCommonThread");
      thread.start();
    }

    for (int i = 0; i < 15; i++) {
      Thread thread2 = new Thread(new ThreadCommon(i));
      thread2.setName("userMultiThread");
      thread2.start();
    }
  }

  @Test
  public void multiThreadsMethodSynchronized_Test() throws InterruptedException {

    NumberGenerator generator = new NumberGenerator(1000, 2000);
    for (int i = 0; i < 3; i++) {
      new ThreadNumber(generator).start();
    }

    Thread.sleep(3000L); // for waiting Thread number already executed
  }

  @Test
  public void ThreadLifecycle_Test() throws InterruptedException {
    ThreadMethodsDemo de = new ThreadMethodsDemo();

    log.info("getstate1 "+de.getState());

    de.start();

    log.info("getstate2 "+ de.getState());
    log.info("getstate3 "+ de.getState());
    log.info("getstate4 "+ de.getState());
    log.info("thread Name "+ de.getName());
    log.info("thread Priority "+ de.getPriority());
    log.info("getstate5 "+ de.getState());

    Thread.sleep(1500L); // for waiting Thread number already executed

    System.out.println("getstate6 "+de.getState());
  }


  public class ThreadNumber extends Thread {

    private final NumberGenerator ng;

    public ThreadNumber(NumberGenerator ng) {
      this.ng = ng;
    }

    @Override
    public void run() {
      try {
        callGenerator();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    private void callGenerator() throws InterruptedException {
      synchronized (ng) {
        Thread.sleep(500L);
        for (int i = 0; i < 3; i++) {

          log.info(getName() + " " + ng.randomNumber());
        }
      }
    }
  }

  public class NumberGenerator {

    private final int low;
    private final int high;

    public NumberGenerator(int low, int high) {
      this.low = low;
      this.high = high;
    }

    public int randomNumber() {
      Random r = new Random();
      int result = r.nextInt(high - low) + low;
      return result;
    }
  }

  public class ThreadMethodsDemo extends Thread {
    public void run() {
//      for(int i=0; i<2; i++) {
        log.info("thread methods demo");
        try {
          log.info("thread is going to sleep");
          ThreadMethodsDemo.sleep(1000L);

          log.info("thread wake up");
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
//      }
    }

  }
}

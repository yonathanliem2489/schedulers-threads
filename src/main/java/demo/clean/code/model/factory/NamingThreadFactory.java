package demo.clean.code.model.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


public class NamingThreadFactory implements ThreadFactory {

  private static final AtomicInteger poolNumber = new AtomicInteger(1);

  private final ThreadGroup group; //NOSONAR

  private final AtomicInteger threadNumber = new AtomicInteger(1);

  private final String namePrefix;

  public NamingThreadFactory(String prefix) {
    SecurityManager s = System.getSecurityManager();
    group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
    namePrefix = prefix + "-" + poolNumber.getAndIncrement() + "-thread-";
  }

  public Thread newThread(Runnable r) {
    Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
    if (t.isDaemon()) {
      t.setDaemon(false);
    }
    if (t.getPriority() != Thread.NORM_PRIORITY) {
      t.setPriority(Thread.NORM_PRIORITY);
    }
    return t;
  }
}

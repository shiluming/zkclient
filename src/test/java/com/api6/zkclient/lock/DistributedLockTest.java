package com.api6.zkclient.lock;

/**
 * 描述:
 *
 * @author: luming.shi   Created on 18/3/5.
 */
public class DistributedLockTest {

    static int n = 500;

    public static void secskill() {
        System.out.println(--n);
    }

    public static void main(String[] args) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                DistributedLock lock = null;
                try {
                    lock = new DistributedLock("localhost:2181", "test1");
                    lock.lock();
                    secskill();
                    System.out.println(Thread.currentThread().getName() + " 正在运行");
                } catch (Exception e) {

                } finally {
                    if (lock != null) {
                        lock.unlock();
                    }
                }

            }
        };

        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }

}

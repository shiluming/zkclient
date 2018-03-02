package com.api6.zkclient;

import org.junit.Test;

import java.util.concurrent.Semaphore;

/**
 * 描述:
 *
 * @author: luming.shi   Created on 18/3/2.
 */
public class SemaphoreTest {

    @Test
    public void testSemaphoreConstructor() throws InterruptedException {

        Semaphore semaphore = new Semaphore(5);
        semaphore.acquire();
    }

    class Worker extends Thread {

        private int num;

        private Semaphore semaphore;

        Worker(int num, Semaphore semaphore) {
            this.num = num;
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            try {
                semaphore.acquire();
                System.out.println(Thread.currentThread().getName() + "占用一台机器");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
            System.out.println(Thread.currentThread().getName() + "释放一台机器");
        }
    }

    @Test
    public void testSemaphoreTest() throws InterruptedException {
        Semaphore semaphore = new Semaphore(3);
        for (int i = 0; i < 5; i++) {
            Worker worker = new Worker(5, semaphore);
            worker.start();
        }
        Thread.sleep(6000);
    }



}

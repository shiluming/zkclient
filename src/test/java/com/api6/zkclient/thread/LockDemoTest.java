package com.api6.zkclient.thread;

import org.junit.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 描述: lock 测试
 *
 * @author: luming.shi   Created on 18/3/5.
 */
public class LockDemoTest {

    /**
     * CyclicBarrier 的使用
     */
    @Test
    public void testCyclicBarrier() {
        CyclicBarrier  cyclicBarrier = new CyclicBarrier(5, new Runnable() {
            @Override
            public void run() {
                System.out.println("Barrier ... ");
            }
        });

        for (int i = 0; i < 3; i++) {
            MyThread th = new MyThread(cyclicBarrier);
            th.start();
        }

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLock() throws InterruptedException {
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        lock.lock();
        System.out.println("do someting..");
        lock.unlock();
        wait();
    }


    class MyThread extends Thread{
        CyclicBarrier cyclicBarrier;
        MyThread(CyclicBarrier cyclicBarrier) {
            this.cyclicBarrier = cyclicBarrier;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " do something .. ");
            try {
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    class MyThread1 extends Thread{
        CyclicBarrier cyclicBarrier;
        MyThread1(CyclicBarrier cyclicBarrier) {
            this.cyclicBarrier = cyclicBarrier;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " do something .. ");
        }
    }

}

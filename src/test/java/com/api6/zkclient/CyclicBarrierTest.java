package com.api6.zkclient;

import org.junit.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 描述:
 *
 * @author: luming.shi   Created on 18/3/2.
 */
public class CyclicBarrierTest {

    @Test
    public void testCyclicBarrierConstructor() {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
    }

    class Writer extends Thread {

        CyclicBarrier cyclicBarrier;

        public Writer(CyclicBarrier cyclicBarrier) {
            this.cyclicBarrier = cyclicBarrier;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + "开始写文件");
            try {
                Thread.sleep(2000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }

            System.out.println(Thread.currentThread().getName() + "开始执行其他事情");
        }
    }

    @Test
    public void testCyclicBarrierDemo() {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(5, new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "CyclicBarrier");
            }
        });
        for (int i = 1; i <= 5; i++) {
            Writer writer1 = new Writer(cyclicBarrier);
            writer1.start();
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}



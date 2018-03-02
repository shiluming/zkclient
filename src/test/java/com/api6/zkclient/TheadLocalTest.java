package com.api6.zkclient;

/**
 * 描述:
 *
 * @author: luming.shi   Created on 18/2/28.
 */
public class TheadLocalTest {

    public void test01() {
        final ThreadLocal<String> threadLocal = new ThreadLocal<>();


        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                threadLocal.set("001");
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                threadLocal.set("002");
            }
        });

        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                threadLocal.set("003");
            }
        });

        t1.start();
        t2.start();
        t3.start();

    }
}

package com.api6.zkclient;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 描述:
 *
 * @author: luming.shi   Created on 18/3/1.
 */
public class MyZKClientTest {

    private static final int SESSION_TIMEOUT = 30 * 1000;

    private ZooKeeper zooKeeper = null;

    private Watcher watcher = new Watcher() {
        @Override
        public void process(WatchedEvent watchedEvent) {
            System.out.println("【事件】watch event " + watchedEvent.toString());
        }
    };

    @Before
    public void create() {
        testCreateInstance();
    }

    @Test
    public void testCreateInstance() {
        try {
            zooKeeper = new ZooKeeper("localhost:2181", SESSION_TIMEOUT, this.watcher);
        } catch (IOException e) {
            System.out.println("实例化 ZKClient 发生错误");
        }
    }

    @Test
    public void testCreateZNode() throws KeeperException, InterruptedException {
        testCreateInstance();
        String s = zooKeeper.create("/myzkclient", "nihao".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println("s = " + s);
    }

    @Test
    public void testGetChildren() throws KeeperException, InterruptedException {
        List<String> children = null;//zooKeeper.getChildren("/", this.watcher);
        children = zooKeeper.getChildren("/", false);
        for (String s : children) {
            System.out.println(s);
        }
    }

    @Test
    public void authCreateInstance() {
        final CountDownLatch latch = new CountDownLatch(1);

    }

    @Test
    public void testCountDownLatch() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(2);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("sleep A");
                    Thread.sleep(2000);
                    latch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("sleep B");
                    Thread.sleep(2000);
                    latch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        System.out.println("main wait");
        latch.await();
        System.out.println("main run");
    }

    public static void main(String[] args) throws InterruptedException {

    }


}

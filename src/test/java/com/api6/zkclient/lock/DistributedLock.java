package com.api6.zkclient.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 描述:
 *
 * @author: luming.shi   Created on 18/3/5.
 */
public class DistributedLock implements Lock, Watcher{

    private ZooKeeper zooKeeper = null;

    //根节点
    private String ROOT_LOCK = "/lock_distributed";

    //竞争的资源
    private String lockName;

    //等待的前一个锁
    private String WAIT_LOCK;

    //当前锁
    private String CURRENT_LOCK;

    //计数器
    private CountDownLatch countDownLatch;

    //超时时间
    private int SESSION_TIMEOUT = 30000;

    private List<Exception> exceptionList = new ArrayList<>(16);

    public DistributedLock(String config, String lockName) {
        this.lockName = lockName;
        try {
            zooKeeper = new ZooKeeper(config, SESSION_TIMEOUT, this);
            Stat stat = zooKeeper.exists(ROOT_LOCK, false);
            if (null == stat) {
                zooKeeper.create(ROOT_LOCK, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }




    @Override
    public void lock() {
        if (exceptionList.size() > 0) {
            try {
                throw new Exception("error 01");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        String splitStr = "_lock_";
        if (lockName.contains(splitStr)) {
            try {
                throw new Exception("锁名有误");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //创建临时节点
        try {
            CURRENT_LOCK = zooKeeper.create(ROOT_LOCK + "/" + lockName + splitStr, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode
                    .EPHEMERAL_SEQUENTIAL);
            System.out.println(CURRENT_LOCK + "已经创建");
            //取所有的节点
            List<String> children = zooKeeper.getChildren(ROOT_LOCK, false);
            List<String> lockObjects = new ArrayList<>();
            for (String node : children) {
                String _node = node.split(splitStr)[0];
                if (_node.equals(lockName)) {
                    lockObjects.add(node);
                }
            }
            Collections.sort(lockObjects);
            System.out.println(Thread.currentThread().getName() + " 的锁是 " + CURRENT_LOCK);

            //若当前节点是最小节点，则获取锁成功
            if (CURRENT_LOCK.equals(ROOT_LOCK + "/" + lockObjects.get(0))) {
                return true;
            }

            //若不是最小节点，则找到自己的前一个节点
            String prevNode = CURRENT_LOCK.substring(CURRENT_LOCK.lastIndexOf("/") + 1);
            WAIT_LOCK = lockObjects.get(Collections.binarySearch(lockObjects, prevNode) - 1);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {

        if (this.tryLock()) {
            return true;
        }
        return false;
    }

    @Override
    public void unlock() {

    }

    @Override
    public Condition newCondition() {
        return null;
    }

    //监视器
    @Override
    public void process(WatchedEvent watchedEvent) {
        if (this.countDownLatch != null) {
            this.countDownLatch.countDown();
        }
    }

    //等待锁
    private boolean waitForLock(String prev, long waitTime) {
        try {
            Stat stat = zooKeeper.exists(ROOT_LOCK + "/" + prev, true);
            if (stat != null) {
                System.out.println(Thread.currentThread().getName() + " 等待锁 " + ROOT_LOCK + "/" + prev);
                //计数器等待，若等到前一个节点小时，则process 中进行countdown， 停止等待，获取锁
                this.countDownLatch = new CountDownLatch(1);
                this.countDownLatch.await(waitTime, TimeUnit.MILLISECONDS);
                this.countDownLatch = null;
                System.out.println(Thread.currentThread().getName() + "等到了锁");
            }
            return true;
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}

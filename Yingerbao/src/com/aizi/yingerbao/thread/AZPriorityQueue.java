package com.aizi.yingerbao.thread;

import java.util.concurrent.PriorityBlockingQueue;

public class AZPriorityQueue<E> extends PriorityBlockingQueue<E> {

    public static final int QUEQUE_MAX_SIZE = 20;

    private static final long serialVersionUID = -7828362778280478668L;

    @Override
    public boolean offer(E e) {
        if (size() >= QUEQUE_MAX_SIZE) {
            return false;
        }
        return super.offer(e);
    }

}

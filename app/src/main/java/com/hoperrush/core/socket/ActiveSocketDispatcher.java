package com.hoperrush.core.socket;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 */
public class ActiveSocketDispatcher {
    private BlockingQueue<Runnable> dispatchQueue
            = new LinkedBlockingQueue<Runnable>();

    public ActiveSocketDispatcher() {
        Thread mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        dispatchQueue.take().run();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mThread.start();
    }

    private void addPacket(String packet) {
        try {
            dispatchQueue.put(
                    new Runnable() {
                        public void run() {
                            //Long running operations
                        }
                    }
            );
        } catch (Exception e) {
        }
    }

}

package com.example.downloadmanager.utils;

public class MyThread extends Thread{
    private final Object object = new Object();
    private boolean pause = false;
    private int Max = 0;

    /*
     *设置暂停 为 ture
     */
    public void pauseThread() {
        pause = true;
    }
    /*
     * 设置暂停为false，并继续
     */
    public void rsumeThread() {
        pause = false;
        synchronized (object) {
            object.notify();
        }

    }
    /*
     *暂停方法
     */
    public void onPause() {
        synchronized (object) {
            try {
                object.wait();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    @Override
    public void run() {
        super.run();
        int index = 1;
        while (true) {
            while (pause) {
                onPause();
            }

            try {
                if (index <= 100) {
                    MsgHandleUtil.getInstance().sendNormalMsg(1, null, index);//通过回调发送消息到UI界面并更新UI
                    System.out.println("index = " + index);
                    sleep(1000);
                    index++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }

        }
    }
}

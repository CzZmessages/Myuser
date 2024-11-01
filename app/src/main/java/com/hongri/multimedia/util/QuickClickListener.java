package com.hongri.multimedia.util;

import android.view.View;

/**
 * @author cpc$
 * @version 1.0
 * @description: TODO
 * @date $ $
 */
public abstract class QuickClickListener implements View.OnClickListener {
    private long lastClickTime = 0;
    private static final long CLICK_INTERVAL = 1000; // 防止连续点击的时间间隔（毫秒）

    @Override
    public void onClick(View view) {
        if (canPerformClick()) {
            onNoDoubleClick(view);
            lastClickTime = System.currentTimeMillis();
        }
    }

    protected abstract void onNoDoubleClick(View v);

    private boolean canPerformClick() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > CLICK_INTERVAL) {
            return true;
        }
        return false;
    }
}

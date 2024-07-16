package com.hongri.multimedia.util;

import android.view.View;

/**
 * @author cpc$
 * @version 1.0
 * @description: TODO
 * @date $ $
 */
public abstract class QuickClickListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
         onNoDoubleClick(v);
    }
    protected  abstract  void onNoDoubleClick(View v);
}

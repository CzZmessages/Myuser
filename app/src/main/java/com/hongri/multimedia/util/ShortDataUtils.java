package com.hongri.multimedia.util;

import com.konovalov.vad.VadListener;

/**
 * @author cpc$
 * @version 1.0
 * @description: TODO
 * @date $ $
 */
public class ShortDataUtils {
    private static OnDataProcessedListener dataProcessedListener;

    // 定义一个回调接口
    public interface OnDataProcessedListener {
        void onDataProcessed(short[] data);
    }

    // 提供一个静态方法来设置监听器
    public static void setOnDataProcessedListener(OnDataProcessedListener listener) {
        dataProcessedListener = listener;
    }

    public static void processAudioData(byte[] audioData) {
        short[] shorts = convertByteArrayToShortArray(audioData);
        // 数据处理完成后，调用监听器
        if (dataProcessedListener != null) {
            dataProcessedListener.onDataProcessed(shorts);
        }
    }

    private static short[] convertByteArrayToShortArray(byte[] bytes) {
        short[] shorts = new short[bytes.length / 2];
        for (int i = 0; i < bytes.length; i += 2) {
            shorts[i / 2] = (short) (((bytes[i + 1] & 0xFF) << 8) | (bytes[i] & 0xFF));
        }
        return shorts;
    }
}

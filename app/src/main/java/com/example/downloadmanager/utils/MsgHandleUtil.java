package com.example.downloadmanager.utils;

public class MsgHandleUtil {
    private I_MsgCallBack callBack = null;

    private static MsgHandleUtil errUtil = new MsgHandleUtil();

    private MsgHandleUtil()
    {
    }

    public static MsgHandleUtil getInstance()
    {
        return errUtil;
    }

    public void init(I_MsgCallBack callBack)
    {
        this.callBack = callBack;
    }

    public void sendNormalMsg(int what, Object obj, int... arg) {
        if (arg.length == 1)
        {
            callBack.msgCallBack(what, obj, arg[0]);
        } else if (arg.length == 2)
        {
            callBack.msgCallBack(what, obj, arg[0], arg[1]);
        } else
        {
            callBack.msgCallBack(what, obj);
        }
    }
    public interface I_MsgCallBack
    {
        void msgCallBack(int what,Object obj,int ... arg);
    }
}

package com.smsf.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by xixixxx on 2018/1/14.
 */

public class SMSSendResultReceiver extends BroadcastReceiver {
    private Context mContext;
    // 发短信
    public final String SENT_SMS_ACTION = "SENT_SMS_ACTION";
    // 接收短信
    public final String RECEIVE_SMS_ACTION = "DELIVERED_SMS_ACTION";
    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        if (SENT_SMS_ACTION.equalsIgnoreCase(intent.getAction())){
            switch (getResultCode()){
                case Activity.RESULT_OK:
                    Toast.makeText(context, "短信发送成功", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(context, "短信发送失败", Toast.LENGTH_SHORT).show();
            }
        }else if(RECEIVE_SMS_ACTION.equalsIgnoreCase(intent.getAction())){
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(context, "短信回执接收成功", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(context, "短信回执接收失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

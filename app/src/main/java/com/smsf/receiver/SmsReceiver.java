package com.smsf.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.smsf.email.MailSenderInfo;
import com.smsf.email.SimpleMailSender;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by xixixxx on 2017/11/12.
 */

public class SmsReceiver extends BroadcastReceiver {
    private Context mContext;
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    public static final String SMS_DELIVER_ACTION = "android.provider.Telephony.SMS_DELIVER";

    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext=context;
        Toast.makeText(context, "接收短信执行了.....", Toast.LENGTH_LONG).show();
        Log.e("SMSReceiver, isOrderedBroadcast()=", isOrderedBroadcast()+"");
        Log.e("SmsReceiver onReceive...", "接收短信执行了......");
        String action = intent.getAction();
        if (SMS_RECEIVED_ACTION.equals(action) || SMS_DELIVER_ACTION.equals(action)) {
            Toast.makeText(context, "开始接收短信.....", Toast.LENGTH_LONG).show();
            Log.e("SmsReceiver onReceive...", "开始接收短信.....");

            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[])bundle.get("pdus");
                if (pdus != null && pdus.length > 0) {
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < pdus.length; i++) {
                        byte[] pdu = (byte[]) pdus[i];
                        messages[i] = SmsMessage.createFromPdu(pdu);
                    }
                    for (SmsMessage message : messages) {
                        String content = message.getMessageBody();// 得到短信内容
                        String sender = message.getOriginatingAddress();// 得到发信息的号码
                        Date date = new Date(message.getTimestampMillis());
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        format.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
                        String sendContent = content +" "+ format.format(date) + " 来自" + sender + "的短信";
                        Log.e("SmsReceicer onReceive ",sendContent +" ");

                        // 异步任务做事
                        new SendMailTask().execute(sendContent);
//                        MailSenderInfo mailInfo = new MailSenderInfo();
//                        mailInfo.setMailServerHost("smtp.163.com");
//                        mailInfo.setMailServerPort("25");
//                        mailInfo.setValidate(true);
//                        mailInfo.setUserName("xiangxixids@163.com");
//                        mailInfo.setPassword("woaicxz850305");//您的邮箱密码
//                        mailInfo.setFromAddress("xiangxixids@163.com");
//                        mailInfo.setToAddress("xiangxixids@qq.com");
//                        mailInfo.setSubject("来自西皮科技");
//                        // 短信内容设置为邮件内容
//                        mailInfo.setContent(sendContent);
//                        //这个类主要来发送邮件
//                        SimpleMailSender sms = new SimpleMailSender();
//                        String result = "短信转发失败";
//                        if(sms.sendTextMail(mailInfo))//发送文体格式
//                        {
//                            result = "短信转发成功";
//                        }
//                        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    // 第一个参数表示对task 传参, doInBackground 里面用
    // 第二个参数表示进度条
    // 第三个参数表示异步执行结果的返回值, doInBackground 的返回值
    public class SendMailTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute(){
            // 执行UI
            Toast.makeText(mContext, "开始进行短信转发...", Toast.LENGTH_LONG).show();
        }

        @Override
        // ... 表示多个参数, 按照strings[i] 来取值
        protected String doInBackground(String... strings) {
            // 异步任务做事
            MailSenderInfo mailInfo = new MailSenderInfo();
            mailInfo.setMailServerHost("smtp.163.com");
            mailInfo.setMailServerPort("25");
            mailInfo.setValidate(true);
            mailInfo.setUserName("xiangxixids@163.com");
            // 自己输入密码
            mailInfo.setPassword("XXXXXXXXX");//您的邮箱密码
            mailInfo.setFromAddress("xiangxixids@163.com");
            mailInfo.setToAddress("xiangxixids@qq.com");
            mailInfo.setSubject("西皮科技:"+strings[0]);
            mailInfo.setContent("来自西皮科技: "+strings[0]);
            //这个类主要来发送邮件
            SimpleMailSender sms = new SimpleMailSender();
            if(sms.sendTextMail(mailInfo))//发送文体格式
            {
                return "短信转发成功";
            }else{
                return "短信转发失败";
            }
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            // 更新UI
            Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
        }
    }

}

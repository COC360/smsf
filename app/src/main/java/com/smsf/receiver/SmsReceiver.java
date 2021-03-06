package com.smsf.receiver;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.smsf.email.MailSenderInfo;
import com.smsf.email.SimpleMailSender;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by xixixxx on 2017/11/12.
 */

public class SmsReceiver extends BroadcastReceiver {
    private Context mContext;
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    public static final String SMS_DELIVER_ACTION = "android.provider.Telephony.SMS_DELIVER";

    public static final String SENT_SMS_ACTION = "SENT_SMS_ACTION";
    public static final String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";

    // 持久化存储
    public SharedPreferences sp;
    public final String appName = "smsf";
    public final String emailKey = "email";
    public final String sendEmailPasswd = "send_email_pass_word";
    public final String sendEmailUserName = "send_email_username";
    public final String mailTitle = "mail_title";
    public final String smsSender = "sms_sender";
    public final String smsReceiver = "sms_receiver";
    public final String needEmail = "need_email";

    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext=context;
        // 持久化存储用户要转发的邮件地址
        sp = context.getSharedPreferences(appName, context.MODE_PRIVATE);
        Toast.makeText(context, "接收短信执行了.....", Toast.LENGTH_LONG).show();

        String email = sp.getString(emailKey, null);
        String send_email_username = sp.getString(sendEmailUserName, null);
        String send_email_passwd = sp.getString(sendEmailPasswd, null);
        String mail_title = sp.getString(mailTitle, "尊敬的用户, 请查收您的转发信息");
        String sms_sender = sp.getString(smsSender, null);
        String sms_receiver = sp.getString(smsReceiver, null);
        boolean need_email = sp.getBoolean(needEmail, false);
        String[] mailKeyInfo = new String[10];
        mailKeyInfo[0]=email;
        mailKeyInfo[1]=send_email_username;
        mailKeyInfo[2]=send_email_passwd;
        mailKeyInfo[4]=mail_title;
        mailKeyInfo[5]=sms_sender; // 发送方号码
        mailKeyInfo[6]=sms_receiver; // 接收方号码
        if (need_email){
            mailKeyInfo[7] = "true";
        }else{
            mailKeyInfo[7] = "false";
        }


        Log.e("SMSReceiver, isOrderedBroadcast()=", isOrderedBroadcast()+"");
        Log.e("SmsReceiver onReceive...", "接收短信执行了......");
        String action = intent.getAction();
        List<String> keyWords = new ArrayList<String>();
        keyWords.add("验证码");
        keyWords.add("授权码");
        keyWords.add("校验码");
        keyWords.add("信用卡");
        keyWords.add("银行");
        if (SMS_RECEIVED_ACTION.equals(action) || SMS_DELIVER_ACTION.equals(action)) {
            Toast.makeText(context, "开始接收短信.....", Toast.LENGTH_LONG).show();
            Log.e("SmsReceiver onReceive...", "开始接收短信.....");

            Bundle bundle = intent.getExtras();
            String pduFormat = intent.getStringExtra("format");
            if (bundle != null) {
                Object[] pdus = (Object[])bundle.get("pdus");
                if (pdus != null && pdus.length > 0) {
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < pdus.length; i++) {
                        byte[] pdu = (byte[]) pdus[i];
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            messages[i] = SmsMessage.createFromPdu(pdu, pduFormat);
                        }else{
                            messages[i] = SmsMessage.createFromPdu(pdu);
                        }
                    }
                    for (SmsMessage message : messages) {
                        String content = message.getMessageBody();// 得到短信内容
                        String sender = message.getOriginatingAddress();// 得到发信息的号码
                        Date date = new Date(message.getTimestampMillis());
                        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss");
                        format.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
                        String sendContent = content +" "+ format.format(date);
                        mailKeyInfo[3] = sendContent;
                        Log.e("SmsReceicer onReceive ",sendContent +" ");

                        boolean needForword = false;
                        for (String key:keyWords) {
                            // 满足任一条件, 才转发
                            if (sendContent.contains(key)){
                                needForword = true;
                                break;
                            }
                        }

                        // 异步任务做事
                        if (needForword){
                            new SendMailTask().execute(mailKeyInfo);
                        }else {
                            Toast.makeText(context, "不满足关键词, 不转发", Toast.LENGTH_LONG).show();
                        }
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
            mailInfo.setUserName(strings[1]);//您的发送邮箱用户名
            // 自己输入密码
            mailInfo.setPassword(strings[2]);//您的发送邮箱密码
            mailInfo.setFromAddress(strings[1]);//您的发送邮箱用户名
            mailInfo.setToAddress(strings[0]);//接受邮箱地址
            mailInfo.setContent(strings[3]);//邮件内容 = 短信内容
            mailInfo.setSubject(strings[4]);//邮件title, 自定义
            //这个类主要来发送邮件
            SimpleMailSender sms = new SimpleMailSender();
            // 允许email转发, 且发送成功
            if(strings[7].equalsIgnoreCase("true") && sms.sendTextMail(mailInfo))//发送文体格式
            {
                return "Email短信转发成功";
            }else{
                sendSMS(strings[6], strings[3]); // 接收方号码, 短信内容
                return "开始通过短信转发";
            }
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            // 更新UI
            Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 直接调用短信接口发短信
     * @param message
     */
    public void sendSMS(String receiver,String message){

        Intent sendIntent = new Intent(SENT_SMS_ACTION);
        PendingIntent sendPI = PendingIntent.getBroadcast(this.mContext, 0, sendIntent, 0);

        Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
        PendingIntent deliverPI = PendingIntent.getBroadcast(this.mContext, 0, deliverIntent, 0);


        //获取短信管理器
        SmsManager smsManager = SmsManager.getDefault();
        //拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            smsManager.sendTextMessage(receiver, null, text, sendPI, deliverPI);
            // 只发送第一段短信
            break;
        }
    }

}

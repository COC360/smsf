package com.example.xixixxx.helloandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.com.utils.CrashHandler;
import com.smsf.email.MailSenderInfo;
import com.smsf.email.SimpleMailSender;

import static com.com.utils.Checker.isEmail;

public class MainActivity extends AppCompatActivity {
    // 持久化存储
    public SharedPreferences sp;
    public static final String appName = "smsf";
    public static final String emailKey = "email";
    public static final String sendEmailPasswd = "send_email_pass_word";
    public static final String sendEmailUserName = "send_email_username";
    public static final String mailTitle = "mail_title";
    public static final String smsSender = "sms_sender";
    public static final String smsReceiver = "sms_receiver";
    public static final String needEmail = "need_email";
    public static final String canEmailSMSF = "允许Email发送";
    public static final String notEmailSMSF = "禁止Email发送";


    // 局部UI
    public EditText user_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 全局未捕获 Excption 处理类, 最后的兜底手段
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());

        // 持久化存储用户要转发的邮件地址
        sp = getSharedPreferences(appName, getApplicationContext().MODE_PRIVATE);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        // tv.setText(stringFromJNI());
        tv.setText("SMSF");

        user_email = (EditText) findViewById(R.id.email_text);

        final Button sendEmail = (Button)findViewById(R.id.send_email_btn);
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!user_email.getText().toString().isEmpty() && isEmail(user_email.getText().toString()))
                {
                    String echo = "保存成功";
                    if(sp.edit().putString(emailKey, user_email.getText().toString()).commit()){
                        echo = user_email.getText().toString() + ", "+echo;
                    }else{
                        echo = user_email.getText().toString() + ", 存储失败";
                    }
                    Toast.makeText(getApplicationContext(), echo, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "地址不合法, 请不要逗比!", Toast.LENGTH_LONG).show();
                }
            }
        });

        final Button echoEmail = (Button) findViewById(R.id.echo_email_btn);
        echoEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = sp.getString(emailKey, null);
                String result = "尚未保存任何邮件地址";
                if (email == null){

                }else {
                    result = "已存地址为: " + email;
                }
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
        });
        // 保存发送邮箱
        final Button saveSendMailUsername = (Button) findViewById(R.id.save_email_username);
        saveSendMailUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String echo = "保存成功";
                if (user_email.getText().toString().isEmpty() || !user_email.getText().toString().contains("@163.com")){
                    echo = "目前只支持163邮箱作为发送邮箱";
                }else{
                    if (sp.edit().putString(sendEmailUserName, user_email.getText().toString()).commit()){
                        echo = "发送邮箱地址:"+user_email.getText().toString()+echo;
                    }else{
                        echo = "保存失败";
                    }
                }
                Toast.makeText(getApplicationContext(), echo, Toast.LENGTH_LONG).show();

            }
        });
        // 保存发送邮箱的密码
        final Button saveSendMailPasswd = (Button) findViewById(R.id.save_email_passwd);
        saveSendMailPasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String echo = "保存成功";
                if (user_email.getText().toString().isEmpty()){
                    echo = "密码不能为空";
                }else{
                    if (sp.edit().putString(sendEmailPasswd, user_email.getText().toString()).commit()){
                        // nothing to do
                    }else{
                        echo = "保存失败";
                    }
                }
                Toast.makeText(getApplicationContext(), echo, Toast.LENGTH_LONG).show();
            }
        });
        // 保存自定义邮件标题
        final Button saveMailTitle = (Button) findViewById(R.id.save_mail_title);
        saveMailTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String echo = "保存成功";
                if (user_email.getText().toString().isEmpty()){
                    echo = "邮件标题不能为空";
                }else{
                    if (sp.edit().putString(mailTitle, user_email.getText().toString()).commit()){
                        // nothing to do
                    }else{
                        echo = "保存失败";
                    }
                }
                Toast.makeText(getApplicationContext(), echo, Toast.LENGTH_LONG).show();
            }
        });
        // 是否允许Email 转发
        final Button forbidenEmail = (Button) findViewById(R.id.forbiden_email);
        boolean needEmailBoolean = sp.getBoolean(needEmail, false);
        if (needEmailBoolean){
            forbidenEmail.setText(notEmailSMSF);
        }else{
            forbidenEmail.setText(canEmailSMSF);
        }
        forbidenEmail.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String echo = "保存成功";
                if (user_email.getText().toString().isEmpty()){
                    echo = "禁止Email发送";
                    if (sp.edit().putBoolean(needEmail, true).commit()){
                        // nothing to do
                        forbidenEmail.setText(canEmailSMSF);
                    }else{
                        echo = "保存失败";
                    }
                }else{
                    if (sp.edit().putBoolean(needEmail, false).commit()){
                        // nothing to do
                        echo = "允许Email发送";
                        forbidenEmail.setText(notEmailSMSF);
                    }else{
                        echo = "保存失败";
                    }
                }
                Toast.makeText(getApplicationContext(), echo, Toast.LENGTH_LONG).show();
            }
        });
        // 保存备份系统, 接收方手机号
        final Button saveSmsReceiverNumber = (Button) findViewById(R.id.save_sms_receiver);
        saveSmsReceiverNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String echo = "保存成功";
                if (user_email.getText().toString().isEmpty()){
                    echo = "接收方号码不能为空";
                }else{
                    if (sp.edit().putString(smsReceiver, user_email.getText().toString()).commit()){
                        // nothing to do
                    }else{
                        echo = "保存失败";
                    }
                }
                Toast.makeText(getApplicationContext(), echo, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}

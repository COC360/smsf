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

        Button sendEmail = (Button)findViewById(R.id.send_email_btn);
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

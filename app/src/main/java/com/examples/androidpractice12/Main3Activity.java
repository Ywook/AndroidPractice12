package com.examples.androidpractice12;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class Main3Activity extends AppCompatActivity {
    TextView tv;
    EditText et1;
    EditText et2;

    String userid = "";
    String password = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        tv = (TextView)findViewById(R.id.tv);
        et1 = (EditText)findViewById(R.id.et1);
        et2 = (EditText)findViewById(R.id.et2);
    }

    public void onClick3(View v){
        userid = et1.getText().toString();
        password = et2.getText().toString();
        if(userid.equals("") || password.equals("")) {
            Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        new mThread().start();
    }

    class mThread extends Thread{
        @Override
        public void run() {
            try{
                URL url = new URL("http://jerry1004.dothome.co.kr/info/login.php");

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);

                String postData = "userid=" + URLEncoder.encode(userid)
                        + "&password=" + URLEncoder.encode(password);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postData.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                InputStream inputStream;
                if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
                    inputStream = httpURLConnection.getInputStream();
                else
                    inputStream = httpURLConnection.getErrorStream();
                final String result = loginResult(inputStream);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(result.equalsIgnoreCase("fail")){
                            tv.setText("로그인이 실패했습니다.");
                        }else if(result.equalsIgnoreCase("guest")){
                            tv.setText("Guset님 로그인 성공");
                        }else{
                            tv.setText("Admin님 로그인 성공");
                        }
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Handler handler = new Handler();

    public String loginResult(InputStream is){
        String result = "";

        Scanner s = new Scanner(is);
        while(s.hasNext()) result += s.nextLine();
        return result;
    }
}

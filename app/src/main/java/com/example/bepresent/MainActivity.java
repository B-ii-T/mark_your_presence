package com.example.bepresent;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    //variables declaration
    LottieAnimationView done;
    LinearLayout present;
    TextView group;
    EditText firstname, lastname, card_number, ip, port;
    NumberPicker group_id;
    String message;
    Socket client;
    PrintWriter printwriter;
    Vibrator vibrator;
    String full_name_str;
    String group_str;
    String ip_address;
    int port_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //initializing variables
        done = findViewById(R.id.done);
        present = findViewById(R.id.present);
        group = findViewById(R.id.group_label);
        firstname = findViewById(R.id.first_name_input);
        lastname = findViewById(R.id.last_name_input);
        card_number = findViewById(R.id.card_number_input);
        ip = findViewById(R.id.ip_input);
        port = findViewById(R.id.port_input);
        group_id = findViewById(R.id.group);
        group_id.setMaxValue(4);
        group_id.setMinValue(1);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);


        //setting animator listener
        done.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        //setting on click listener
        present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });


    }
    
    private boolean check_inputs(){
        return !firstname.getText().toString().trim().equals("") &&
                !lastname.getText().toString().trim().equals("") &&
                !card_number.getText().toString().trim().equals("") &&
                !ip.getText().toString().trim().equals("") && !port.getText().toString().trim().equals("");
    }

    private void showSnackBar(){
            Snackbar.make(findViewById(R.id.root), "some fileds are empty", Snackbar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.green)).setTextColor(getResources().getColor(R.color.white)).show();
            vibrator.vibrate(300);
            Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);
            findViewById(R.id.root).startAnimation(shake);
    }

    private void changeVisibility(){
        present.setVisibility(View.GONE);
        firstname.setVisibility(View.GONE);
        lastname.setVisibility(View.GONE);
        card_number.setVisibility(View.GONE);
        group.setVisibility(View.GONE);
        group_id.setVisibility(View.GONE);
        ip.setVisibility(View.GONE);
        port.setVisibility(View.GONE);
        done.setVisibility(View.VISIBLE);
    }

    private void getInfo(){
        full_name_str = lastname.getText().toString().trim() +" "+ firstname.getText().toString().trim();
        group_str = String.valueOf(group_id.getValue());
        ip_address = ip.getText().toString().trim();
        port_number = Integer.parseInt(port.getText().toString());
    }

    private void send() {
        if(check_inputs()){
            vibrator.vibrate(100);
            changeVisibility();
            done.playAnimation();
            //preparing the message to send
            message = full_name_str +", "+ card_number.getText().toString().trim() + ", group : "+ group;
            //starting the new thread
            new Thread(new clientThread(message, ip_address, port_number)).start();
        } else{
        showSnackBar();
        }
    }




    class clientThread implements Runnable {
        //variables declaration
        private final String message;
        private final String ip;
        private final int port;

        //constructor
        public clientThread(String message, String ip, int port) {
            this.message =  message;
            this.ip =  ip;
            this.port =  port;
        }

        @Override
        public void run() {
            try{
                //creating new socket
                client = new Socket(ip, port);
                //getting the output stream
                printwriter = new PrintWriter(client.getOutputStream());
                //writing the message on the stream
                printwriter.write(message);
                //flushing the stream
                printwriter.flush();
                //closing the writer
                printwriter.close();
                //closing the socket
                client.close();
            } catch(IOException e){e.printStackTrace();}




            //handling changes on the UI main thread
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
////                    present.setEnabled(false);
////                    firstname.setEnabled(false);
////                    lastname.setEnabled(false);
////                    card_number.setEnabled(false);
////                    group_id.setEnabled(false);
//                }
//            });
        }
    }
}
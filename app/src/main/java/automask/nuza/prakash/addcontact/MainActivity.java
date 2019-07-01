package automask.nuza.prakash.addcontact;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;

import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    Button save,send;
    EditText text1;
    TextView codetxt;
    String un,ms,sm;
      private  Socket mSocket;
    private EditText mInputMessageView;
    private static final int PERMISSION_REQUEST_CODE = 1;




//    {
//        try {
//            mSocket = IO.socket("http://192.168.2.57:3001");
//
//        } catch (URISyntaxException e) {
//            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
//        }
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            mSocket=IO.socket("http://192.168.2.57:3001");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//
//            if (checkSelfPermission(Manifest.permission.SEND_SMS)
//                    == PackageManager.PERMISSION_DENIED) {
//
//                Log.d("permission", "permission denied to SEND_SMS - requesting it");
//                String[] permissions = {Manifest.permission.SEND_SMS};
//
//                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
//
//            }
//        }

        send=(Button)findViewById(R.id.send);
        mSocket.on("message", onNewMessage);


        mSocket.connect();

        addMessage(un,ms,sm);








        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSend();
            }
        });




codetxt= (TextView)findViewById(R.id.codetxt);


mInputMessageView=(EditText)findViewById(R.id.text1);



    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {




        @Override
        public void call(final Object... args) {

            MainActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {




                        JSONObject data = (JSONObject) args[0];
                        String phone;
                        String message;
                        String sim;

                        try {
                            phone = data.getString("phone");
                            message = data.getString("message");
                            sim =data.getString("sim");



                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, ""+e.toString(), Toast.LENGTH_SHORT).show();

                            return ;
                        }

                        // add the message to view
                    addMessage(phone, message,sim);
                    Toast.makeText(MainActivity.this, ""+data.toString(), Toast.LENGTH_SHORT).show();
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS)
                                != PackageManager.PERMISSION_GRANTED) {
                            // Permission is not granted
                            // Ask for permision
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);
                        } else {
                            try {

                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage(phone, null,message, null, null);
                                    Toast.makeText(getApplicationContext(), "SMS Sent!",
                                            Toast.LENGTH_LONG).show();
                                Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();



                               codetxt.setText(phone+" "+message);
                                attemptSend();


                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(),
                                        "SMS faild, please try again later!" + e,
                                        Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }

                    }

            });


        }
    };

    void addMessage(String phone,String message,String sim)
    {
         un=phone;
         ms=message;
         sm=sim;
    }


    private void attemptSend() {

        String message ="message sent";

//                mInputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }


        Log.e("prakash8520",message);

        mInputMessageView.setText("");
        mSocket.emit("message", "message sent");





    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("message", onNewMessage);
    }
}

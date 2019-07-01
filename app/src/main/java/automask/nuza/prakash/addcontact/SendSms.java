package automask.nuza.prakash.addcontact;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;


public class SendSms extends AppCompatActivity {
    TextView codetxt;
    String un,ms;
    private Socket mSocket;
    private EditText mInputMessageView;
    private static final int PERMISSION_REQUEST_CODE = 1;

    {
        try {
            mSocket = IO.socket("http://192.168.1.13:3001");

        } catch (URISyntaxException e) {
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.sms_layout);
        super.onCreate(savedInstanceState);


        mSocket.on("message", onNewMessage);
        mSocket.connect();
    }
    private Emitter.Listener onNewMessage = new Emitter.Listener() {




        @Override
        public void call(final Object... args) {

            SendSms.this.runOnUiThread(new Runnable() {



                @Override
                public void run() {



                    JSONObject data = (JSONObject) args[0];
                    String phone;
                    String message;
                    try {
                        phone = data.getString("phone");
                        message = data.getString("message");


                    } catch (JSONException e) {
                        Toast.makeText(SendSms.this, ""+e.toString(), Toast.LENGTH_SHORT).show();

                        return ;
                    }

                    // add the message to view
                    addMessage(phone, message);
                    Toast.makeText(SendSms.this, ""+data.toString(), Toast.LENGTH_SHORT).show();
                    if (ContextCompat.checkSelfPermission(SendSms.this, Manifest.permission.SEND_SMS)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted
                        // Ask for permision
                        ActivityCompat.requestPermissions(SendSms.this, new String[]{Manifest.permission.SEND_SMS}, 1);
                    } else {
                        try {

                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(phone, null,message, null, null);
                            Toast.makeText(getApplicationContext(), "SMS Sent!",
                                    Toast.LENGTH_LONG).show();
                            Toast.makeText(SendSms.this, ""+message, Toast.LENGTH_SHORT).show();



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

    void addMessage(String phone,String message)
    {
        un=phone;
        ms=message;
    }


    private void attemptSend() {

        String message = mInputMessageView.getText().toString().trim();
//        if (TextUtils.isEmpty(message)) {
//            return;
//        }

        Log.e("prakash8520",message);

//        mInputMessageView.setText("");
        mSocket.emit("toserver", message);





    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("message", onNewMessage);
    }
}

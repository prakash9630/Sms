package automask.nuza.prakash.addcontact;

import android.os.AsyncTask;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class MessageSender extends AsyncTask<String,Void,Void> {
    Socket s;
    DataInputStream dos;
PrintWriter pw;

    @Override
    protected Void doInBackground(String... voids) {

        String message=voids[1];

        try {
            s=new Socket("192.168.2.57",3001);
            pw=new PrintWriter(s.getOutputStream());
            pw.write(message);
            pw.flush();
            pw.close();
            s.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}


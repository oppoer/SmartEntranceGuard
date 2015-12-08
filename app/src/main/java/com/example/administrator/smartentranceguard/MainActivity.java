package com.example.administrator.smartentranceguard;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.PrivilegedAction;

/**
 * created by xukang.wang on 12/6/2015
 * this Activity is just for test smartentranceguard kit
 *
 */

public class MainActivity extends AppCompatActivity {

    /**
     * varible  param
     * @param
     */
        Button AutoOpenTime_btn;
        EditText AutoOpenTime_edit;
        EditText HandOpenTime_edit;
        public Boolean IsThreadDisable = Boolean.valueOf(false);
        Button PWD_btn;
        EditText PWD_edit;
        Button RSSI_btn1;
        Button RSSI_btn2;
        Button RSSI_btn3;
        Button RSSI_btn4;
        EditText RSSI_edit1;
        EditText RSSI_edit2;
        EditText RSSI_edit3;
        EditText RSSI_edit4;
        Button SSID_btn;
        EditText SSID_edit;
        Button ServiceIp_btn;
        EditText ServiceIP_edit;
        EditText UDPtext;
        public UDPThread UDPThread = null;
        Boolean UDPreceive = Boolean.valueOf(false);
        String buffer = "";
        Bundle bundle = null;
        String closeStr;
        Button closedoor_btn;
        EditText closedoor_edit;
        DatagramPacket datagramPacket = null;
        DatagramSocket datagramSocket = null;
        Button handleOpenTime_btn;
        DataInputStream input = null;
        Integer intPort;
        EditText ip;
        Button link_btn;
        TextView messageView;
        Message msg = null;

    /**
     * create by xukang.wang on 2015/12/7
     * it handler message method
     */

    public Handler myHandler;

    {
        myHandler = new Handler() {
            public void handleMesasge(Message paramAnonymousMessage){
                Bundle localBundle = paramAnonymousMessage.getData();
                if(paramAnonymousMessage.what == 17){
                    MainActivity.this.messageView.setText(localBundle.getString("msg") + "\n");
                }
                do {
                    return;
                    if(paramAnonymousMessage.what == 16){
                        MainActivity.this.messageView.setText(localBundle.getString("msg") + "\n");
                        Toast.makeText(MainActivity.this.getApplicationContext(),"link success",0).show();
                        return;
                    }
                    if(paramAnonymousMessage.what == 9){
                        MainActivity.this.messageView.setText(localBundle.getString("msg") + "\n");
                        Toast.makeText(MainActivity.this.getApplicationContext(),"break success",0).show();
                        return;
                    }
                    if(paramAnonymousMessage.what == 18){
                        MainActivity.this.ip.setText("");
                        MainActivity.this.ip.setText(localBundle.getString("msg").trim() + "\n");
                        MainActivity.this.setButtonView(Boolean.valueOf(true));
                        return;
                    }
                } while (paramAnonymousMessage.what == 22);

                MainActivity.this.messageView.setText(localBundle.getString("msg") + "\n");
                Toast.makeText(MainActivity.this.getApplicationContext(),localBundle.getString("msg"),0).show();
            }

        };
    }
        String openStr;
        Button opendoor_btn;
        EditText opendoor_edit;
        DataOutputStream out = null;
        EditText port;
        Button restart_btn;
        Socket socket = null;
//        public socketThread socketThread = null;
        String strIP;
        EditText updEditText;
        EditText value_1;
        EditText value_2;
        EditText value_3;
        EditText value_4;
        Button value_btn_1;
        Button value_btn_2;
        Button value_btn_3;
        Button value_btn_4;

    /**
     * created by xukang.wang on 2015/12/7
     * check Number  what is substring, INT to HEX AND String
     *  getBytes.
     * @param paramString
     * @return
     */

    public static String getCheckNumber(String paramString){
        int i = paramString.substring(0,1).getBytes()[0];
        for(int j =0;;j++){
            if(j>=paramString.length())
                return intToHex(Integer.valueOf(i));
            i=(byte)(i^paramString.substring(j,j+1).getBytes()[0]);
        }
    }

    /**
     * int to Hex
     * create by xukang.wang on 2015/12/7
      * @param paramInteger
     * @return
     */
    public static String intToHex(Integer paramInteger){
        return Integer.toHexString(paramInteger.intValue());
    }

    /**
     * create response command, need to know what the format of command
     * and valueOf(),getCheckNumber() meaning.
     * xukang.wang on 2015/12/7.
     * @param paramString1
     * @param paramString2
     * @param paramString3
     * @param paramString4
     * @param paramString5
     * @param paramString6
     * @return
     */

    public static String responseCommand(String paramString1,String paramString2,String paramString3,String paramString4,String paramString5,String paramString6){
        String str1 = intToHex(Integer.valueOf(4+(2+(paramString1.length()+paramString2.length()+paramString3.length()+paramString4.length()))+paramString5.length()));
        String str2 = "00" + intToHex(Integer.valueOf(2+(4+(2+(paramString1.length()+paramString2.length()+paramString3.length()+paramString4.length()))+paramString5.length()+paramString6.length())));
        String str3 = getCheckNumber(paramString1.toUpperCase()+paramString2+paramString3+paramString4+str1.toUpperCase()+str2.toUpperCase()+paramString5+paramString6);
        if(str3.length()>1);
        while(true){
            return paramString1.toUpperCase()+paramString2+paramString3+paramString4+str1.toUpperCase()+str2.toUpperCase()+paramString5+paramString6+str3.toUpperCase();
            str3="0" + str3;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * created by xukang.wang.on 2015/12/7
     * set Button state view
     *
     * @param paramBoolean
     */

    public void setButtonView(Boolean paramBoolean){

        this.link_btn.setEnabled(paramBoolean.booleanValue());
        this.opendoor_btn.setEnabled(paramBoolean.booleanValue());
        this.closedoor_btn.setEnabled(paramBoolean.booleanValue());
        this.SSID_btn.setEnabled(paramBoolean.booleanValue());
        this.PWD_btn.setEnabled(paramBoolean.booleanValue());
        this.ServiceIp_btn.setEnabled(paramBoolean.booleanValue());
        this.handleOpenTime_btn.setEnabled(paramBoolean.booleanValue());
        this.AutoOpenTime_btn.setEnabled(paramBoolean.booleanValue());
        this.restart_btn.setEnabled(paramBoolean.booleanValue());
        this.RSSI_btn1.setEnabled(paramBoolean.booleanValue());
        this.RSSI_btn2.setEnabled(paramBoolean.booleanValue());
        this.RSSI_btn3.setEnabled(paramBoolean.booleanValue());
        this.RSSI_btn4.setEnabled(paramBoolean.booleanValue());
        this.value_btn_1.setEnabled(paramBoolean.booleanValue());
        this.value_btn_2.setEnabled(paramBoolean.booleanValue());
        this.value_btn_3.setEnabled(paramBoolean.booleanValue());
        this.value_btn_4.setEnabled(paramBoolean.booleanValue());

    }

    /**
     * created by xukang.wang on 2015/12/18
     * this is just a thread for UDP tansport
     */
class UDPThread extends Thread{
    public  String ip;
    private DataJsonList json;
    public  String messageView;
    public  Integer port;
    public UDPThread(){

    }
    public void run(){
        try{
            while (true){
                MainActivity.this.datagramSocket.receive(MainActivity.this.datagramPacket);
                String str1 = new String(MainActivity.this.datagramPacket.getData()).trim();
                str1.substring(0,4);
                str1.substring(4,6);
                str1.substring(6,8);
                str1.substring(8,10);
                str1.substring(10,12);
                str1.substring(12,16);
                str1.substring(16,18);
                String str2=str1.substring(18, -2 + str1.length());
                json=new Gson().fromJson(str2,DataJsonList.class);
                MainActivity.this.bundle.clear();
                MainActivity.this.bundle.putString("msg", json.getLocalIP());
                MainActivity.this.msg = MainActivity.this.myHandler.obtainMessage();
                MainActivity.this.msg.what = 18;
                MainActivity.this.msg.setData(MainActivity.this.bundle);
                MainActivity.this.myHandler.sendMessage(MainActivity.this.msg);
                Log.d("ThreadLog", "continue");

            }
        }catch (IOException localIOException){
            localIOException.printStackTrace();
        }
    }
}

    /**
     * created by xukang.wang on 2015/12/8
     * this is socket for telecommunication
     */

    class socketThread extends Thread{
        public String ip;
        public String messageView;
        public Integer port;
        public socketThread(String paramInteger,Integer arg3){
            this.ip = paramInteger;
            Object localObject;
            this.port = localObject;
        }
        public void run(){
            try{
                MainActivity.this.socket = new Socket();
                MainActivity.this.socket.connect(new InetSocketAddress(this.ip,this.port.intValue()),5000);
                MainActivity.this.input= new DataInputStream(MainActivity.this.socket.getInputStream());
                MainActivity.this.out = new DataOutputStream(MainActivity.this.socket.getOutputStream());
                MainActivity.this.bundle.clear();
                MainActivity.this.bundle.putString("msg", "connect success");
                MainActivity.this.msg =MainActivity.this.myHandler.obtainMessage();
                MainActivity.this.msg.what =16;
                MainActivity.this.msg.setData(MainActivity.this.bundle);
                MainActivity.this.myHandler.sendMessage(MainActivity.this.msg);
                String str = MainActivity.responseCommand("FFFE","01","00","00","00","");
                long l1 =System.currentTimeMillis();
                while(true){
                    if(MainActivity.this.socket.isClosed())
                        return;
                    long l2 =System.currentTimeMillis();
                    if(l2-l1>5000L){
                        try{
                            MainActivity.this.out.write(str.getBytes());
                            MainActivity.this.out.flush();
                            l1 =System.currentTimeMillis();
                            MainActivity.this.bundle.clear();
                            MainActivity.this.bundle.putString("msg", "心跳" + str);
                            MainActivity.this.msg = MainActivity.this.myHandler.obtainMessage();
                            MainActivity.this.msg.setData(MainActivity.this.bundle);
                            MainActivity.this.msg.what =17;
                            MainActivity.this.myHandler.sendMessage(MainActivity.this.msg);
                        }catch (IOException localIOException2){
                            localIOException2.printStackTrace();
                        }
                    }
                }
            }catch (SocketTimeoutException localSocketTimeoutException){
                MainActivity.this.bundle.clear();
                MainActivity.this.bundle.putString("msg", "服务器连接失败，请检查网络是否打开");
                MainActivity.this.msg = MainActivity.this.myHandler.obtainMessage();
                MainActivity.this.msg.setData(MainActivity.this.bundle);
                MainActivity.this.msg.what = 22;
                MainActivity.this.myHandler.sendMessage(MainActivity.this.msg);
                return;
            }
            catch (IOException localIOException1){
                localIOException1.printStackTrace();
            }
        }
    }

}


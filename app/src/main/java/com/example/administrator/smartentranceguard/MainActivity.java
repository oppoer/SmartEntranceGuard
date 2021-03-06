package com.example.administrator.smartentranceguard;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * created by xukang.wang on 12/6/2015
 * this Activity is just for test smartentranceguard kit
 *
 */

public class MainActivity extends AppCompatActivity {

    /**
     * varible  param
     *
     */
        TextView messageView;
        EditText updEditText;
        EditText ip;
        EditText port;
        Button link_btn;
        String openStr;
        Button opendoor_btn;
        EditText opendoor_edit;
        String closeStr;
        Button closedoor_btn;
        EditText closedoor_edit;
        Button AutoOpenTime_btn;
        EditText AutoOpenTime_edit;
        Button ManualOpenTime_btn;
        EditText ManualOpenTime_edit;
        public Boolean IsThreadDisable = Boolean.valueOf(false);
        Button PWD_btn;
        EditText PWD_edit;
        Button RSSI_btn;
        EditText RSSI_edit;
        Button SSID_btn;
        EditText SSID_edit;
        Button ServiceIp_btn;
        EditText ServiceIP_edit;
        EditText wifi_value;
        Button wifi_value_btn;
        Button restart_btn;
        EditText UDPtext;
//        public UDPThread UDPThread = null;
//        Boolean UDPreceive = Boolean.valueOf(false);


        DatagramPacket datagramPacket = null;
        DatagramSocket datagramSocket = null;
        DataInputStream input = null;
        DataOutputStream out = null;
        Socket socket = null;
        public socketThread socketThread = null;
        Message msg = null;
        String buffer = "";
        Bundle bundle = null;

        private static final String IP ="10.10.10.254";
        private static final Integer Port = Integer.valueOf(6602);

    /**
     * create by xukang.wang on 2015/12/7
     * it handler message method
     */

    public   Handler myHandler;

    {
        myHandler = new Handler() {
            public void handleMesasge(Message paramAnonymousMessage){
                Bundle localBundle = paramAnonymousMessage.getData();
                if(paramAnonymousMessage.what == 17){
                    MainActivity.this.messageView.setText(localBundle.getString("msg") + "\n");
                }
                do {
                    if(paramAnonymousMessage.what == 16){
                        MainActivity.this.messageView.setText(localBundle.getString("msg") + "\n");
                        Toast.makeText(MainActivity.this.getApplicationContext(),"link success",Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(paramAnonymousMessage.what == 9){
                        MainActivity.this.messageView.setText(localBundle.getString("msg") + "\n");
                        Toast.makeText(MainActivity.this.getApplicationContext(),"break success",Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(paramAnonymousMessage.what == 18){
                        MainActivity.this.ip.setText("");
                        MainActivity.this.ip.setText(localBundle.getString("msg").trim() + "\n");
//                        MainActivity.this.setButtonView(Boolean.valueOf(true));
                        return;
                    }
                } while (paramAnonymousMessage.what !=22);

                MainActivity.this.messageView.setText(localBundle.getString("msg") + "\n");
                Toast.makeText(MainActivity.this.getApplicationContext(),localBundle.getString("msg"),Toast.LENGTH_LONG).show();
            }

        };
    }


    /**
     * created by xukang.wang on 2015/12/7
     * new version on 2015.12.29
     * check Number  what is substring, INT to HEX AND String
     *  getBytes.
     */

    public static String getCheckNumber(String paramString){
        int i =paramString.substring(0, 1).getBytes()[0];
        for(int j=1;;j++){
            if(j>=paramString.length())
                return intToHex(Integer.valueOf(i));
            i=(byte)(i^paramString.substring(j, j+1).getBytes()[0]);

        }
    }

    /**
     * int to Hex()
     * create by xukang.wang on 2015/12/7
     * new version on 2015.12.29
     */
    public static String intToHex(Integer paramInteger){
        return Integer.toHexString((paramInteger.intValue()&0x000000FF)|0xFFFFFF00).substring(6);

    }

    /**
     * create response command, need to know what the format of command
     * and valueOf(),getCheckNumber() meaning.
     * xukang.wang on 2015/12/7.
     */

    public static String CombineCommand(String paramString1,String paramString2,String paramString3,String paramString4,String paramString5,String paramString6){
        String str1 = intToHex(Integer.valueOf((4+(2+(paramString1.length()+paramString2.length()+paramString3.length()+paramString4.length()))+paramString5.length())));
        String str2 = "00"+intToHex(Integer.valueOf(Integer.valueOf(2 + (4 + (2 + (paramString1.length() + paramString2.length() + paramString3.length() + paramString4.length())) + paramString5.length() + paramString6.length()))));
        String str3 = getCheckNumber(paramString1.toUpperCase()+paramString2+paramString3+paramString4+str1.toUpperCase()+str2.toUpperCase()+paramString5+paramString6);

        return paramString1.toUpperCase()+paramString2+paramString3+paramString4+str1.toUpperCase()+str2.toUpperCase()+paramString5+paramString6+str3.toUpperCase();

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

        this.ip =((EditText)findViewById(R.id.ip_edit));
        this.port=((EditText)findViewById(R.id.port_edit));
        this.port.setText("6602");
        this.ip.setText("10.10.10.254");
        this.msg = new Message();
        this.bundle = new Bundle();
        this.link_btn = ((Button)findViewById(R.id.link_btn));
        this.link_btn.setOnClickListener(new View.OnClickListener() {

                                             public void onClick(View paramAnonymousView) {
                                                 if ((MainActivity.this.ip.getText().toString().trim().equals("")) || (MainActivity.this.port.getText().toString().trim().equals(""))) {

                                                     Toast.makeText(MainActivity.this.getApplicationContext(), "IP AND PORT is not null", Toast.LENGTH_LONG).show();
                                                     return;
                                                 }
                                                 if ((MainActivity.this.socket != null) && (!MainActivity.this.socket.isClosed()))
                                                     try {
                                                         MainActivity.this.out.close();
                                                         MainActivity.this.input.close();
                                                         MainActivity.this.socket.close();
                                                         MainActivity.this.bundle.clear();
                                                         MainActivity.this.bundle.putString("msg", "close");
                                                         MainActivity.this.msg = MainActivity.this.myHandler.obtainMessage();
                                                         MainActivity.this.msg.what = 9;
                                                         MainActivity.this.msg.setData(MainActivity.this.bundle);
                                                         MainActivity.this.myHandler.sendMessage(MainActivity.this.msg);
                                                         return;
                                                     } catch (IOException localIOException) {
                                                         localIOException.printStackTrace();
                                                         return;
                                                     }
                                                 MainActivity.this.socketThread = new MainActivity.socketThread(IP, Port);
                                                 new Thread(MainActivity.this.socketThread).start();
                                                 MainActivity.this.link_btn.setText("close");
                                             }
                                         }


        );

        this.messageView =(TextView)findViewById(R.id.messageView);
        this.opendoor_btn =(Button)findViewById(R.id.opendoor_btn);
        this.opendoor_edit =(EditText)findViewById(R.id.opendoor_edit);
        this.opendoor_edit.setText(CombineCommand("FFFE", "01", "04", "01", "00", ""));
        this.opendoor_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                if ((MainActivity.this.socket == null) || (MainActivity.this.socket.isClosed())) {
                    Toast.makeText(MainActivity.this.getApplicationContext(), "please connect server", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    MainActivity.this.out.write(MainActivity.this.opendoor_edit.getText().toString().getBytes());
                    MainActivity.this.out.flush();
                    MainActivity.this.bundle.clear();
                    MainActivity.this.bundle.putString("msg", "门开");
                    MainActivity.this.msg = MainActivity.this.myHandler.obtainMessage();
                    MainActivity.this.msg.what = 22;
                    MainActivity.this.msg.setData(MainActivity.this.bundle);
                    MainActivity.this.myHandler.sendMessage(MainActivity.this.msg);
                } catch (IOException localIOException) {
                    localIOException.printStackTrace();
                }

            }
        });

        this.closedoor_btn =(Button)findViewById(R.id.closedoor_btn);
        this.closedoor_edit=(EditText)findViewById(R.id.closedoor_edit);
        this.closedoor_edit.setText(CombineCommand("FFFE", "01", "04", "02", "00", ""));
        this.closedoor_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                if ((MainActivity.this.socket == null) || (MainActivity.this.socket.isClosed())) {
                    Toast.makeText(MainActivity.this.getApplicationContext(), "please connect server", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    MainActivity.this.out.write(MainActivity.this.closedoor_edit.getText().toString().getBytes());
                    MainActivity.this.out.flush();
                    MainActivity.this.bundle.clear();
                    MainActivity.this.bundle.putString("msg", "门关");
                    MainActivity.this.msg = MainActivity.this.myHandler.obtainMessage();
                    MainActivity.this.msg.what = 22;
                    MainActivity.this.msg.setData(MainActivity.this.bundle);
                    MainActivity.this.myHandler.sendMessage(MainActivity.this.msg);

                } catch (IOException localIOException) {
                    localIOException.printStackTrace();
                }
            }
        });

        this.SSID_edit=(EditText)findViewById(R.id.ssid_edit);
        this.SSID_btn =(Button)findViewById(R.id.ssid_btn);
        this.SSID_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((MainActivity.this.socket==null)||(MainActivity.this.socket.isClosed())){
                    Toast.makeText(MainActivity.this.getApplicationContext(),"please connect servicer",Toast.LENGTH_LONG).show();
                    return;
                }
                if (MainActivity.this.SSID_edit.getText().toString().trim().equals("")){
                    Toast.makeText(MainActivity.this.getApplicationContext(),"SSID isnot blank",Toast.LENGTH_LONG).show();
                    return;
                }
                try{
                    String str = CombineCommand("FFFE","01","04","07","00","{\"SSID\":\""+MainActivity.this.SSID_edit.getText().toString()+"\"}");
                    MainActivity.this.out.write(str.getBytes());
                    MainActivity.this.out.flush();
                    MainActivity.this.bundle.clear();
                    MainActivity.this.bundle.putString("msg", "change SSID"+str);
                    MainActivity.this.msg = MainActivity.this.myHandler.obtainMessage();
                    MainActivity.this.msg.what=22;
                    MainActivity.this.msg.setData(MainActivity.this.bundle);
                    MainActivity.this.msg.sendToTarget();
                }catch (IOException localIOException){
                    localIOException.printStackTrace();
                }

            }
        });

        this.PWD_edit =(EditText)findViewById(R.id.ssid_passwd_edit);
        this.PWD_btn = (Button)findViewById(R.id.ssid_passwd_btn);
        this.PWD_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((MainActivity.this.socket==null)||(MainActivity.this.socket.isClosed())){
                    Toast.makeText(MainActivity.this.getApplicationContext(),"please connect servicer",Toast.LENGTH_LONG).show();
                    return;
                }
                if(MainActivity.this.PWD_edit.getText().toString().trim().equals("")){
                    Toast.makeText(MainActivity.this.getApplicationContext(),"please insert password!",Toast.LENGTH_LONG).show();
                    return;
                }
                try{
                    String str = CombineCommand("FFFE","01","04","08","00","{\"PWD\":\""+MainActivity.this.PWD_edit.getText().toString()+"\"}");
                    MainActivity.this.out.write(str.getBytes());
                    MainActivity.this.out.flush();
                    MainActivity.this.bundle.clear();
                    MainActivity.this.bundle.putString("msg", "chang PWD" + str);
                    MainActivity.this.msg = MainActivity.this.myHandler.obtainMessage();
                    MainActivity.this.msg.what =22;
                    MainActivity.this.msg.setData(MainActivity.this.bundle);
                    MainActivity.this.msg.sendToTarget();
                }catch (IOException localIOException){
                    localIOException.printStackTrace();
                }
            }
        });

        this.wifi_value =(EditText)findViewById(R.id.wifi_value_edit);
        this.wifi_value_btn = (Button)findViewById(R.id.wifi_value_btn);
        this.wifi_value_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((MainActivity.this.socket==null)||(MainActivity.this.socket.isClosed())){
                    Toast.makeText(MainActivity.this.getApplicationContext(),"please connect server",Toast.LENGTH_LONG).show();
                    return;
                }
                if(MainActivity.this.wifi_value.getText().toString().trim().equals("")){
                    Toast.makeText(MainActivity.this.getApplicationContext(),"please isnert wifi value",Toast.LENGTH_LONG).show();
                    return;
                }
                try{
                    String str = CombineCommand("FFFE","O1","04","16","00","{\"RSSI_Change_Value\":\""+MainActivity.this.wifi_value.getText().toString()+"\"}");
                    MainActivity.this.out.write(str.getBytes());
                    MainActivity.this.out.flush();
                    MainActivity.this.bundle.clear();
                    MainActivity.this.bundle.putString("msg", "wifi value" + str);
                    MainActivity.this.msg =MainActivity.this.myHandler.obtainMessage();
                    MainActivity.this.msg.what=22;
                    MainActivity.this.msg.setData(MainActivity.this.bundle);
                    MainActivity.this.msg.sendToTarget();

                }catch (IOException localIOException){
                    localIOException.printStackTrace();
                }

            }
        });

        this.RSSI_edit =(EditText)findViewById(R.id.rssi_value_edit);
        this.RSSI_btn =(Button)findViewById(R.id.rssi_value_btn);
        this.RSSI_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((MainActivity.this.socket==null)||(MainActivity.this.socket.isClosed())){
                    Toast.makeText(MainActivity.this.getApplicationContext(),"please connect server",Toast.LENGTH_LONG).show();
                    return;
                }
                if(MainActivity.this.RSSI_edit.getText().toString().trim().equals("")){
                    Toast.makeText(MainActivity.this.getApplicationContext(),"please insert Rssi value",Toast.LENGTH_LONG).show();
                    return;
                }
                try{
                    String str = CombineCommand("FFFE","01","04","12","00","{\"RSSI_Value\":\""+MainActivity.this.RSSI_edit.getText().toString()+"\"}");
                    MainActivity.this.out.write(str.getBytes());
                    MainActivity.this.out.flush();
                    MainActivity.this. bundle.clear();
                    MainActivity.this.bundle.putString("msg", "RSSI VALUE" + str);
                    MainActivity.this.msg = MainActivity.this.myHandler.obtainMessage();
                    MainActivity.this.msg.what = 22;
                    MainActivity.this.msg.setData(MainActivity.this.bundle);
                    MainActivity.this.msg.sendToTarget();

                }catch (IOException localIOException){
                    localIOException.printStackTrace();
                }
            }
        });

        this.ServiceIP_edit=(EditText)findViewById(R.id.service_ip_edit);
        this.ServiceIp_btn =(Button)findViewById(R.id.service_ip_btn);
        this.ServiceIp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((MainActivity.this.socket==null)||(MainActivity.this.socket.isClosed())){
                    Toast.makeText(MainActivity.this.getApplicationContext(),"please connect server",Toast.LENGTH_LONG).show();
                    return;
                }
                if(MainActivity.this.ServiceIP_edit.getText().toString().trim().equals("")){
                    Toast.makeText(MainActivity.this.getApplicationContext(),"please insert Service ip",Toast.LENGTH_LONG).show();
                    return;
                }
                try{
                    String str = CombineCommand("FFFE","01","04","11","00","{\"ServiceIP\":\""+MainActivity.this.ServiceIP_edit.getText().toString()+"\"}");
                    MainActivity.this.out.write(str.getBytes());
                    MainActivity.this.out.flush();
                    MainActivity.this.bundle.clear();
                    MainActivity.this.bundle.putString("msg", "ServiceIP" + str);
                    MainActivity.this.msg = MainActivity.this.myHandler.obtainMessage();
                    MainActivity.this.msg.what =22;
                    MainActivity.this.msg.setData(MainActivity.this.bundle);
                    MainActivity.this.msg.sendToTarget();
                }catch (IOException localIOException){

                    localIOException.printStackTrace();
                }
            }
        });

        this.ManualOpenTime_edit = (EditText)findViewById(R.id.manual_odt_edit);
        this.ManualOpenTime_btn =(Button)findViewById(R.id.manual_odt_btn);
        this.ManualOpenTime_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((MainActivity.this.socket==null)||(MainActivity.this.socket.isClosed())){
                    Toast.makeText(MainActivity.this.getApplicationContext(),"please connect server",Toast.LENGTH_LONG).show();
                    return;
                }
                if(MainActivity.this.ManualOpenTime_edit.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this.getApplicationContext(),"please insert time",Toast.LENGTH_LONG).show();
                    return;
                }
                try{
                    String str = CombineCommand("FFFE","01","04","0B","00","{\"HandCloseTime\":\""+MainActivity.this.ManualOpenTime_edit.getText().toString()+"\"}");
                    MainActivity.this.out.write(str.getBytes());
                    MainActivity.this.out.flush();
                    MainActivity.this.bundle.clear();
                    MainActivity.this.bundle.putString("msg", "Manual time" + str);
                    MainActivity.this.msg = MainActivity.this.myHandler.obtainMessage();
                    MainActivity.this.msg.what = 22;
                    MainActivity.this.msg.setData(MainActivity.this.bundle);
                    MainActivity.this.msg.sendToTarget();
                }catch (IOException localIOException){
                    localIOException.printStackTrace();
                }
            }
        });

        this.AutoOpenTime_edit =(EditText)findViewById(R.id.auto_odt_edit);
        this.AutoOpenTime_btn =(Button)findViewById(R.id.auto_odt_btn);
        this.AutoOpenTime_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((MainActivity.this.socket == null)||(MainActivity.this.socket.isClosed())){
                    Toast.makeText(MainActivity.this.getApplicationContext(),"please connect server",Toast.LENGTH_LONG).show();
                    return;
                }
                if(MainActivity.this.AutoOpenTime_edit.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this.getApplicationContext(),"please insert auto time",Toast.LENGTH_LONG).show();
                    return;
                }
                try{
                    String str = CombineCommand("FFFE","01","04","0C","00","{\"AutoOpenTime\":\""+MainActivity.this.AutoOpenTime_edit.getText().toString()+"\"}");
                    MainActivity.this.out.write(str.getBytes());
                    MainActivity.this.out.flush();
                    MainActivity.this.bundle.clear();
                    MainActivity.this.bundle.putString("msg", "auto time" + str);
                    MainActivity.this.msg = MainActivity.this.myHandler.obtainMessage();
                    MainActivity.this.msg.what =22;
                    MainActivity.this.msg.setData(MainActivity.this.bundle);
                    MainActivity.this.msg.sendToTarget();
                }catch (IOException localIOException){
                    localIOException.printStackTrace();
                }


            }
        });




        



        this.updEditText =(EditText)findViewById(R.id.udptext_edit);
        this.updEditText.setText("6603");

        try{
            byte[] arrayOfByte = new byte[100];
            this.datagramSocket = new DatagramSocket(Integer.valueOf(this.updEditText.getText().toString()).intValue());
//            this.datagramSocket.setBroadcast(true);
            this.datagramPacket = new DatagramPacket(arrayOfByte,arrayOfByte.length);
//            this.UDPThread = new UDPThread();
//            this.UDPThread.start();
//            this.UDPreceive = Boolean.valueOf(true);
            this.bundle.clear();
            this.bundle.putString("msg", "监听成功");
            this.msg = this.myHandler.obtainMessage();
            this.msg.what =22;
            this.msg.setData(this.bundle);
            this.myHandler.sendMessage(this.msg);
//            setButtonView(Boolean.valueOf(true));
          //  CombineCommand("FFFE", "01", "05", "00", "00", "{\"LocalIp\":\"192.168.1.111\"}");

        }catch (IOException localIOException){

                localIOException.printStackTrace();

        }


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



    protected  void onDestroy(){
        while (true){
            if((this.socket ==null)||(this.socket.isClosed())){
                this.datagramSocket = null;
                this.datagramPacket = null;
//                this.UDPreceive = Boolean.valueOf(false);
//                this.UDPThread = null;
//                this.UDPThread.interrupt();
                Log.i("ThreadLog", "销毁");
                super.onDestroy();
                return;

            }
            try{
                this.out.close();
                this.input.close();
                this.socket.close();
            }
            catch (IOException localIOException){
                localIOException.printStackTrace();
            }

        }

    }
    /**
     * created by xukang.wang.on 2015/12/7
     * set Button state view
     *
     * @param paramBoolean
     */

//    public void setButtonView(Boolean paramBoolean){
//
//        this.link_btn.setEnabled(paramBoolean.booleanValue());
//        this.opendoor_btn.setEnabled(paramBoolean.booleanValue());
//        this.closedoor_btn.setEnabled(paramBoolean.booleanValue());
//        this.SSID_btn.setEnabled(paramBoolean.booleanValue());
//        this.PWD_btn.setEnabled(paramBoolean.booleanValue());
//        this.ServiceIp_btn.setEnabled(paramBoolean.booleanValue());
//        this.handleOpenTime_btn.setEnabled(paramBoolean.booleanValue());
//        this.AutoOpenTime_btn.setEnabled(paramBoolean.booleanValue());
//        this.restart_btn.setEnabled(paramBoolean.booleanValue());
//        this.RSSI_btn1.setEnabled(paramBoolean.booleanValue());
//        this.RSSI_btn2.setEnabled(paramBoolean.booleanValue());
//        this.RSSI_btn3.setEnabled(paramBoolean.booleanValue());
//        this.RSSI_btn4.setEnabled(paramBoolean.booleanValue());
//        this.value_btn_1.setEnabled(paramBoolean.booleanValue());
//        this.value_btn_2.setEnabled(paramBoolean.booleanValue());
//        this.value_btn_3.setEnabled(paramBoolean.booleanValue());
//        this.value_btn_4.setEnabled(paramBoolean.booleanValue());
//
//    }

    /**
     * created by xukang.wang on 2015/12/18
     * this is just a thread for UDP tansport
     */
    /*
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

  */

    /**
     * created by xukang.wang on 2015/12/8
     * this is socket for telecommunication
     */

    class socketThread implements Runnable{
        public String ip;
        public Integer port;
        public socketThread(String ip,Integer port){
            this.ip = ip;
            this.port= port;
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
                String str = MainActivity.CombineCommand("FFFE", "01", "00", "00", "00", "");
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

            }
            catch (IOException localIOException1){
                localIOException1.printStackTrace();
            }
        }
    }

}


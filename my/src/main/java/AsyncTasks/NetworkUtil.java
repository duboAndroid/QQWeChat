package AsyncTasks;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import com.example.dubo.qqwechat.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

public class NetworkUtil {
    private NetworkUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    //判断网络是否连接
    public static boolean isConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(null != connectivity){
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if(null != info && info.isConnected())if(info.getState() == NetworkInfo.State.CONNECTED)return true;
        }//end of if
        return false;
    }

    //打开网络设置界面
    public static void openSettingUI(Activity activity) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }

    //监测URL地址是否有效
    public static boolean checkURL(String url){
        try {
            URL u = new URL(url);
            HttpURLConnection urlConn = (HttpURLConnection)u.openConnection();
            urlConn.connect();
            if(urlConn.getResponseCode()== HttpsURLConnection.HTTP_OK){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    //判断wifi状态
    public static boolean isWifiConnected(Context context) {
        if (null != context) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (null != mWiFiNetworkInfo)return mWiFiNetworkInfo.isAvailable();
        }//end of if
        return false;
    }

    //判断移动网络
    public static boolean isMobileConnected(Context context) {
        if (null != context) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (null != mMobileNetworkInfo)return mMobileNetworkInfo.isAvailable();
        }//end of if
        return false;
    }

    //获取连接类型
    public static String getConnectedType(Context context) {
        String strNetworkType = "";
        if(null != context) {
            NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    strNetworkType = "WIFI";
                } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    String _strSubTypeName = networkInfo.getSubtypeName();
                    //System.out.println("Network getSubtypeName : " + _strSubTypeName);
                    int networkType = networkInfo.getSubtype();//TD-SCDMA  networkType is 17
                    switch (networkType) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                            strNetworkType = "2G";
                            break;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                        case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                        case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                            strNetworkType = "3G";
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                            strNetworkType = "4G";
                            break;
                        default:
                            //TD-SCDMA 中国移动 联通 电信 三种3G制式
                            if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                strNetworkType = "3G";
                            } else {
                                strNetworkType = _strSubTypeName;
                            }
                            break;
                    }
                    //System.out.println("Network getSubtype : " + Integer.valueOf(networkType).toString());
                }
            }
            //System.out.println("Network Type : " + strNetworkType);
        }//end of if
        return strNetworkType;
    }

    public String getLocalIpAddress() {
        try{
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }catch(SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    ////////////////////////////////////////网络请求

    //使用GET去访问网络
    public static String RequestHttpGet(Context context, String requestUrlAndData){
        if (isConnected(context)) {
            HttpURLConnection conn=null;
            try {
                URL url=new URL(requestUrlAndData);
                conn=(HttpURLConnection) url.openConnection();
                conn.setRequestMethod(VickyConfig.RequestType_Get);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(5000);

                //conn.setRequestProperty("Content-Type","application/json;charset=utf-8");

                conn.connect();
                int code=conn.getResponseCode();
                if(200 == code){
                    return getStringFromInputStream(conn.getInputStream());
                }else{
                    return context.getString(R.string.NetworkRequestErrorCode)+code;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return context.getString(R.string.NetworkRequestExceptionStr)+e.getMessage();
            }finally{
                if(null != conn)conn.disconnect();
            }
        }else{
            return context.getString(R.string.NetworkNotConnErrorStr);
        }
    }

    //使用POST去访问网络,传json字符串
    public static String RequestHttpPost(Context context, String requestUrl, String requestJsonData){
        if(isConnected(context)){
            HttpURLConnection conn=null;
            try {
                URL url=new URL(requestUrl);
                conn=(HttpURLConnection) url.openConnection();
                conn.setRequestMethod(VickyConfig.RequestType_Post);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type","application/json;charset=utf-8");

                OutputStream out=conn.getOutputStream();
                out.write(requestJsonData.getBytes());
                out.flush();
                out.close();

                conn.connect();
                int code=conn.getResponseCode();
                if(200 == code){
                    return getStringFromInputStream(conn.getInputStream());
                }else{
                    return context.getString(R.string.NetworkRequestErrorCode)+code;
                }
            }catch(Exception e){
                e.printStackTrace();
                return context.getString(R.string.NetworkRequestExceptionStr)+e.getMessage();
            }finally{
                if(null != conn)conn.disconnect();
            }
        }else{
            return context.getString(R.string.NetworkNotConnErrorStr);
        }
    }

    //使用PUT去访问网络,传json字符串
    public static String RequestHttpPut(Context context, String requestUrl, String requestJsonData){
        if (isConnected(context)) {
            HttpURLConnection conn=null;
            try {
                URL url=new URL(requestUrl);
                conn=(HttpURLConnection) url.openConnection();
                conn.setRequestMethod(VickyConfig.RequestType_Put);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type","application/json;charset=utf-8");

                OutputStream out=conn.getOutputStream();
                out.write(requestJsonData.getBytes());
                out.flush();
                out.close();

                conn.connect();
                int code=conn.getResponseCode();
                if(200 == code){
                    return getStringFromInputStream(conn.getInputStream());
                }else{
                    return context.getString(R.string.NetworkRequestErrorCode)+code;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return context.getString(R.string.NetworkRequestExceptionStr)+e.getMessage();
            }finally{
                if(null != conn)conn.disconnect();
            }
        }else{
            return context.getString(R.string.NetworkNotConnErrorStr);
        }
    }

    //使用DELETE去访问网络,传json字符串
    public static String RequestHttpDelete(Context context, String requestUrlAndData){
        if(isConnected(context)){
            HttpURLConnection conn=null;
            try {
                URL url=new URL(requestUrlAndData);
                conn=(HttpURLConnection) url.openConnection();
                conn.setRequestMethod(VickyConfig.RequestType_Delete);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(5000);
                //conn.setRequestProperty("Content-Type","application/json;charset=utf-8");

                conn.connect();
                int code=conn.getResponseCode();
                if(200 == code){
                    return getStringFromInputStream(conn.getInputStream());
                }else{
                    return context.getString(R.string.NetworkRequestErrorCode)+code;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return context.getString(R.string.NetworkRequestExceptionStr)+e.getMessage();
            }finally{
                if(null != conn)conn.disconnect();
            }
        }else{
            return context.getString(R.string.NetworkNotConnErrorStr);
        }
    }

    //根据输入流返回一个字符串
    private static String getStringFromInputStream(InputStream is) throws Exception {
        //BufferedReader input = new BufferedReader(new InputStreamReader(is));//使用BufferedReader替代BufferedInputStream获取时间从100ms降低到3ms
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        byte[] buff=new byte[1024];
        int len = -1;
        while((len = is.read(buff))!=-1)baos.write(buff, 0, len);
        is.close();
        String html=baos.toString();
        baos.close();
        return html;
    }

    /////////////////////////////////////////////////////
    //post提交得到信息,传参数map
    public static String sendPostRequest(Context context, String uri, List<NameValuePair> params)throws Exception {
        if(isConnected(context)){
            HttpPost request = new HttpPost(uri);
            if(null != params && params.size() > 0)request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            DefaultHttpClient client = new DefaultHttpClient();
            client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10 * 1000);
            client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 0);
            HttpResponse httpResponse = client.execute(request);
            return EntityUtils.toString(httpResponse.getEntity());
        }else{
            return context.getString(R.string.NetworkNotConnErrorStr);
        }
    }

    //get提交得到信息
    public static String sendGetRequest(Context context, String uri)throws Exception {
        if(isConnected(context)){
            HttpGet request = new HttpGet(uri);
            DefaultHttpClient client = new DefaultHttpClient();
            client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
            client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 0);
            HttpResponse httpResponse = client.execute(request);
            return EntityUtils.toString(httpResponse.getEntity());
        }else{
            return context.getString(R.string.NetworkNotConnErrorStr);
        }
    }

    //post上传文件(form形式),name里面的值为服务端需要key.只有这个key才可以得到对应的文件,filename是文件的名字,包含后缀名的 比如:abc.png
    public static String sendPostUploadFile(Context context, String uri, String key, String filePath)throws Exception {
        if(isConnected(context)){
            String BOUNDARY = UUID.randomUUID().toString();//边界标识,随机生成
            String PREFIX = "--", LINE_END = "\r\n";
            String CONTENT_TYPE = "multipart/form-data";//内容类型
            HttpURLConnection conn = null;
            File file = new File(filePath);
            try {
                URL url = new URL(uri);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10 * 1000);
                conn.setConnectTimeout(10 * 1000);
                conn.setDoInput(true); //允许输入流
                conn.setDoOutput(true); //允许输出流
                conn.setUseCaches(false); //不允许使用缓存
                conn.setRequestMethod("POST"); //请求方式
                conn.setRequestProperty("Charset", "utf-8"); //设置编码
                conn.setRequestProperty("connection", "keep-alive");
                conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                StringBuffer sb = new StringBuffer();

                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                sb.append("Content-Disposition: form-data; name=\""+ key +"\"; filename=\""+ file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset=utf-8" + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while((len = is.read(bytes)) != -1)dos.write(bytes, 0, len);
                is.close();
                dos.write(LINE_END.getBytes());

                dos.write((PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes());//请求结束标志
                dos.flush();

                int code=conn.getResponseCode();
                if(200 == code){
                    return getStringFromInputStream(conn.getInputStream());
                }else{
                    return context.getString(R.string.NetworkRequestErrorCode)+code;
                }
            }catch(Exception e){
                e.printStackTrace();
                return context.getString(R.string.NetworkRequestExceptionStr)+e.getMessage();
            }finally{
                if(null != conn)conn.disconnect();
            }
        }else{
            return context.getString(R.string.NetworkNotConnErrorStr);
        }
    }

    //post上传文件并附带其他参数信息
    public static String sendPostInfoAndUploadFile(Context context, String uri, List<NameValuePair> params, Map<String, File> files)throws Exception {
        if(isConnected(context)){
            String BOUNDARY = UUID.randomUUID().toString();//边界标识,随机生成
            String PREFIX = "--", LINE_END = "\r\n";
            String CONTENT_TYPE = "multipart/form-data";//内容类型
            String CHARSET = "utf-8";

            HttpURLConnection conn = null;
            try {
                URL url = new URL(uri);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10 * 1000);
                conn.setConnectTimeout(10 * 1000);
                conn.setDoInput(true); //允许输入流
                conn.setDoOutput(true); //允许输出流
                conn.setUseCaches(false); //不允许使用缓存
                conn.setRequestMethod("POST"); //请求方式
                conn.setRequestProperty("Charset", CHARSET); //设置编码
                conn.setRequestProperty("connection", "keep-alive");
                conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

                //首先组拼文本类型的参数
                StringBuilder sb = new StringBuilder();
                for(NameValuePair entry : params) {
                    sb.append(PREFIX);
                    sb.append(BOUNDARY);
                    sb.append(LINE_END);
                    sb.append("Content-Disposition: form-data; name=\"" + entry.getName() + "\"" + LINE_END);
                    sb.append("Content-Type: text/plain; charset=" + CHARSET + LINE_END);
                    sb.append("Content-Transfer-Encoding: 8bit" + LINE_END);
                    sb.append(LINE_END);
                    sb.append(entry.getValue());
                    sb.append(LINE_END);
                }//end of for

                DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
                outStream.write(sb.toString().getBytes());
                //发送文件数据
                if(null != files){
                    for(Map.Entry<String, File> file : files.entrySet()) {
                        StringBuilder sb1 = new StringBuilder();
                        sb1.append(PREFIX);
                        sb1.append(BOUNDARY);
                        sb1.append(LINE_END);
                        sb1.append("Content-Disposition: form-data; name=\""+ file.getKey() +"\"; filename=\""+ file.getValue().getName() + "\"" + LINE_END);
                        sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
                        sb1.append(LINE_END);
                        outStream.write(sb1.toString().getBytes());

                        InputStream is = new FileInputStream(file.getValue());
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while((len = is.read(buffer)) != -1)outStream.write(buffer, 0, len);
                        is.close();
                        outStream.write(LINE_END.getBytes());
                    }//end of for
                }//end of if
                outStream.write((PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes());//请求结束标志
                outStream.flush();

                int code=conn.getResponseCode();
                if(200 == code){
                    return getStringFromInputStream(conn.getInputStream());
                }else{
                    return context.getString(R.string.NetworkRequestErrorCode)+code;
                }
            }catch(Exception e){
                e.printStackTrace();
                return context.getString(R.string.NetworkRequestExceptionStr)+e.getMessage();
            }finally{
                if(null != conn)conn.disconnect();
            }
        }else{
            return context.getString(R.string.NetworkNotConnErrorStr);
        }
    }
}
package AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.example.dubo.qqwechat.R;

import org.apache.http.NameValuePair;

import java.io.File;
import java.util.List;
import java.util.Map;

public class DubRequestAsyncTask extends AsyncTask<String, Integer, String> {
    private Context activity;
    private Handler mainHandler;
    private int requestHttpCode = -1;
    private String requestWebUrl,requestHttpType;
    private boolean showLoading = false,isParamJson = true;
    private RequestCallback requestCallback = null;
    private List<NameValuePair> nameValuePairParams = null;
    private Map<String, File> uploadFileMap = null;

    public DubRequestAsyncTask(Context activity, Handler mainHandler, String requestWebUrl, int requestHttpCode, String requestHttpType, boolean showLoading) {//json
        this.isParamJson = true;
        this.activity = activity;
        this.mainHandler = mainHandler;
        this.requestWebUrl = requestWebUrl;
        this.requestHttpCode = requestHttpCode;
        this.requestHttpType = requestHttpType;
        this.showLoading = showLoading;
        this.requestCallback = null;
    }

    public DubRequestAsyncTask(Context activity, Handler mainHandler, String requestWebUrl, String requestHttpType, boolean showLoading, RequestCallback requestCallback) {//json(回调处理结果)
        this.isParamJson = true;
        this.activity = activity;
        this.mainHandler = mainHandler;
        this.requestWebUrl = requestWebUrl;
        this.requestHttpType = requestHttpType;
        this.showLoading = showLoading;
        this.requestCallback = requestCallback;
    }

    public DubRequestAsyncTask(Context activity, Handler mainHandler, String requestWebUrl, int requestHttpCode, String requestHttpType, boolean showLoading, List<NameValuePair> nameValuePairParams) {//form
        this.isParamJson = false;
        this.activity = activity;
        this.mainHandler = mainHandler;
        this.requestWebUrl = requestWebUrl;
        this.requestHttpCode = requestHttpCode;
        this.requestHttpType = requestHttpType;
        this.showLoading = showLoading;
        this.nameValuePairParams = nameValuePairParams;
    }

    public DubRequestAsyncTask(Context activity, Handler mainHandler, String requestWebUrl, int requestHttpCode, String requestHttpType, boolean showLoading, List<NameValuePair> nameValuePairParams, Map<String, File> uploadFileMap) {//form
        this.isParamJson = false;
        this.activity = activity;
        this.mainHandler = mainHandler;
        this.requestWebUrl = requestWebUrl;
        this.requestHttpCode = requestHttpCode;
        this.requestHttpType = requestHttpType;
        this.showLoading = showLoading;
        this.nameValuePairParams = nameValuePairParams;
        this.uploadFileMap = uploadFileMap;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(showLoading && null != mainHandler)mainHandler.sendEmptyMessage(VickyConfig.msg_showLoading);
        if(null != requestCallback)requestCallback.onRequestPrepare();
    }

    @Override
    protected String doInBackground(String... requestParams) {//请求参数
        try {
            if(isParamJson){//json
                if(requestHttpType.equals(VickyConfig.RequestType_Get)){
                    //StephenToolUtils.LogD(requestHttpCode+"网络请求Url及参数==>"+requestWebUrl);
                    return NetworkUtil.RequestHttpGet(activity,requestWebUrl);
                }else if(requestHttpType.equals(VickyConfig.RequestType_Post)){
                    //StephenToolUtils.LogD(requestHttpCode+"网络请求Url==>"+requestWebUrl);
                    //StephenToolUtils.LogD(requestHttpCode+"网络请求参数==>"+requestParams[0]);
                    return NetworkUtil.RequestHttpPost(activity,requestWebUrl,requestParams[0]);
                }else if(requestHttpType.equals(VickyConfig.RequestType_Put)){
                    //StephenToolUtils.LogD(requestHttpCode+"网络请求Url==>"+requestWebUrl);
                    //StephenToolUtils.LogD(requestHttpCode+"网络请求参数==>"+requestParams[0]);
                    return NetworkUtil.RequestHttpPut(activity,requestWebUrl,requestParams[0]);
                }else if(requestHttpType.equals(VickyConfig.RequestType_Delete)){

                    return NetworkUtil.RequestHttpDelete(activity,requestWebUrl);
                }else if(requestHttpType.equals(VickyConfig.RequestType_Upload)){
                    //StephenToolUtils.LogD(requestHttpCode+"网络请求Url==>"+requestWebUrl);
                    //StephenToolUtils.LogD(requestHttpCode+"网络请求参数==>Key:"+requestParams[0]+"\n==>FilePath:"+requestParams[1]);
                    return NetworkUtil.sendPostUploadFile(activity,requestWebUrl,requestParams[0],requestParams[1]);
                }else{
                    return activity.getString(R.string.NetworkRequestExceptionStr)+"访问方式不存在!请求取消!";
                }
            }else{//form
                if(requestHttpType.equals(VickyConfig.RequestType_Get)){
                    //StephenToolUtils.LogD(requestHttpCode+"网络请求Url及参数==>"+requestWebUrl);
                    return NetworkUtil.sendGetRequest(activity,requestWebUrl);
                }else if(requestHttpType.equals(VickyConfig.RequestType_Post)){
                    //StephenToolUtils.LogD(requestHttpCode+"网络请求Url==>"+requestWebUrl);
                    //StephenToolUtils.LogD(requestHttpCode+"网络请求参数==>"+ JsonUtil.toJson(nameValuePairParams));
                    return NetworkUtil.sendPostRequest(activity,requestWebUrl,nameValuePairParams);
                }else if(requestHttpType.equals(VickyConfig.RequestType_Upload)){
                    //StephenToolUtils.LogD(requestHttpCode+"网络请求Url==>"+requestWebUrl);
                    //StephenToolUtils.LogD(requestHttpCode+"网络请求参数==>Params:"+JsonUtil.toJson(JsonUtil.toJson(nameValuePairParams))+"\n==>Files:"+JsonUtil.toJson(uploadFileMap));
                    return NetworkUtil.sendPostInfoAndUploadFile(activity,requestWebUrl,nameValuePairParams,uploadFileMap);
                }else{
                    return activity.getString(R.string.NetworkRequestExceptionStr)+"访问方式不存在!请求取消!";
                }
            }
        } catch (Exception e) {
            return activity.getString(R.string.NetworkRequestExceptionStr)+e.getMessage();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if(null != requestCallback)requestCallback.onChangeProgress(values[0],values[1]);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        //StephenToolUtils.LogD(requestHttpCode+"网络请求结果==>"+(HtmlRegexpUtil.hasSpecialChars(result) ? HtmlRegexpUtil.filterHtml(result) : result));
        if(null != requestCallback){
            requestCallback.onCompleted(result);
            if(showLoading && null != mainHandler)mainHandler.sendEmptyMessage(VickyConfig.msg_closeLoading);
        }else{
            Message msg= Message.obtain();
            msg.what = VickyConfig.msg_returnJson;
            msg.arg1 = requestHttpCode;
            msg.obj = result;
            if(showLoading)msg.arg2 = 1;
            if(null != mainHandler)mainHandler.sendMessage(msg);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if(null != requestCallback)requestCallback.onCancel();
    }

    public interface RequestCallback {
        void onRequestPrepare();
        void onChangeProgress(int progress, int successFlag);
        void onCompleted(String returnMsg);
        boolean onCancel();
    }
}
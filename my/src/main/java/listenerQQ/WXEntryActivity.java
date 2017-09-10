package listenerQQ;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import AsyncTasks.DubConfig;
import test.MyApplication;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI WXApi;
    private String Tag = WXEntryActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            WXApi = WXAPIFactory.createWXAPI(WXEntryActivity.this, DubConfig.openIdForWeiXin, false); //微信的入口 appId
            WXApi.handleIntent(getIntent(), WXEntryActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReq(BaseReq arg0) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if (null != resp) {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK://成功
                    if(resp instanceof SendAuth.Resp){
                        ((MyApplication)getApplication()).setWeiXinFlag(((SendAuth.Resp) resp).code);
                        Log.i(Tag,"使用微信登录成功!");
                    }else if(resp instanceof SendMessageToWX.Resp){
                        ((MyApplication)getApplication()).setWeiXinFlag("weChatShareSuccess");
                        Log.i(Tag,"微信分享成功!");
                    }else{
                        Log.i(Tag,"微信返回:成功,当前未处理!");
                    }
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    Log.i(Tag,"微信操作取消!");
                    break;
                default:
                    Log.i(Tag,"微信返回错误信息:"+resp.errStr);
                    break;
            }//end of switch
        }//end of if
        finish();//将微信启动的这个界面关闭掉会导致使用微信的界面触发onResume
    }
}
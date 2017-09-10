package test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.example.dubo.qqwechat.R;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import AsyncTasks.DubConfig;
import bean.EntityQQUserInfo;
import bean.EntityWeiBoUserInfo;
import bean.EntityWeiXinUserInfo;
import listenerQQ.DubThirdPartyTool;


public class MainActivity extends AppCompatActivity {
    private DubThirdPartyTool dubThirdPartyTool;
    private IUiListener loginQQListener;
    private String thirdOpenId,thirdUserName,thirdHeadUrl;
    private int curLoginCallBack = -1;
    private static final int QQLoginCallBack = 1, WBLoginCallBack = 2;
    public MainHandler mainHandler;
    private SsoHandler weiBoSsoHandler;
    private Tencent tencentSsoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mainHandler = new MainHandler(this.getMainLooper());
        dubThirdPartyTool = new DubThirdPartyTool(this);

        ///////////////////////   QQ    /////////////////////////////
        findViewById(R.id.qq_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 你的 appid
                tencentSsoHandler = Tencent.createInstance(DubConfig.openIdForQQ, MainActivity.this);
                curLoginCallBack = QQLoginCallBack;
                loginQQListener = dubThirdPartyTool.createLoginQQListener(tencentSsoHandler, new DubThirdPartyTool.QQLoginGetUserInfoSuccess() {
                    @Override
                    public void getUserInfoSuccess(String openId, EntityQQUserInfo qqUserInfo) {//第三方返回
                        thirdOpenId = openId;
                        thirdUserName = qqUserInfo.getNickname();
                        thirdHeadUrl = qqUserInfo.getFigureurl_qq_1();
                    }
                });
                //showLoadingDialog("QQ登录请求中,请稍候...");
                tencentSsoHandler.login(MainActivity.this,"all", loginQQListener); //// TODO: 2017/9/9 这里报空
            }
        });


        ///////////////////////   微信    /////////////////////////////
        findViewById(R.id.we_chat_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dubThirdPartyTool.startWeiXinLogin(DubConfig.openIdForWeiXin);//// TODO: 2017/9/10  微信点击没反应
            }
        });

        ///////////////////////   微博    /////////////////////////////
        findViewById(R.id.webo_chat_login).setOnClickListener(new View.OnClickListener() { //// TODO: 2017/9/10 21322
            @Override
            public void onClick(View v) {
                weiBoSsoHandler = new SsoHandler(MainActivity.this, new AuthInfo(MainActivity.this,
                        DubConfig.openIdForWeiBo,
                        "https://api.weibo.com/oauth2/default.html",
                        DubConfig.weiBoAllScope));
                curLoginCallBack = WBLoginCallBack;
                //showLoadingDialog("微博登录请求中,请稍候...");
                weiBoSsoHandler.authorize(dubThirdPartyTool.createLoginWeiBoListenerListener(new DubThirdPartyTool.WeiBoLoginGetUserInfoSuccess() {
                    @Override
                    public void getUserInfoSuccess(String openId, EntityWeiBoUserInfo weiBoUserInfo) {
                        thirdOpenId = openId;
                        thirdUserName = weiBoUserInfo.getScreen_name();
                        thirdHeadUrl = weiBoUserInfo.getAvatar_large();
                        //String requestWeiBoUrl = DubConfig.WeBoCheckFirstLogin + "openid/" + openId;
                        //sendHttpRequestToWebServerForType(curActivity, DubConfig.repWeBoCheckFirstLogin, DubConfig.ServeIpAndPort + requestWeiBoUrl, VickyConfig.RequestType_Get, null, true);//------>请求
                    }
                }));
            }
        });
    }


    //只用于微信返回
    @Override
    protected void onResume() {
        super.onResume();
        String weiXinFlag = (( MyApplication)getApplication()).getWeiXinFlag();
        if(!TextUtils.isEmpty(weiXinFlag)){
            dubThirdPartyTool.getWeiXinUserInfo(DubConfig.openIdForWeiXin, DubConfig.WXSecert, weiXinFlag, new DubThirdPartyTool.WeiXinLoginGetUserInfoSuccess() {
                @Override
                public void getUserInfoSuccess(String openId, EntityWeiXinUserInfo weiXinUserInfo) {
                    thirdOpenId = openId;
                    thirdUserName = weiXinUserInfo.getNickname();
                    thirdHeadUrl = weiXinUserInfo.getHeadimgurl();
                }
            });
            ((MyApplication) getApplication()).setWeiXinFlag(null);
        }else{
            //将loading关闭
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(curLoginCallBack){
            case QQLoginCallBack:
                Tencent.onActivityResultData(requestCode, resultCode, data, loginQQListener);
                break;
            case WBLoginCallBack:
                if(null != weiBoSsoHandler)weiBoSsoHandler.authorizeCallBack(requestCode, resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }//end of switch
    }

    //解析用户信息保存
    /*private boolean parseUserInfoSave(String loginJson){
        BeanResponseLoginBase loginBase= (BeanResponseLoginBase) JsonUtil.fromJson(loginJson,BeanResponseLoginBase.class);
        if(null != loginBase && null != loginBase.getData()){
            (new StephenUserInfoTool(this)).saveLoginUserInfo(userNameE.getText().toString(),passWordE.getText().toString(),loginBase.getData().getUid(),loginBase.getData().getTokenid());//把帐号保存起来后面都要用
            return true;
        }//end of if
        return false;
    }*/

    //handler
    public class MainHandler extends Handler {
        public MainHandler(Looper looper) {}

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            }//end of switch
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tencentSsoHandler.logout(this);//调用QQ注销接口
        try {
            //StephenToolUtils.closeLoadingDialog();
            //StephenToolUtils.closeAlertInfoDialog();
            if (null != mainHandler) mainHandler.removeCallbacksAndMessages(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

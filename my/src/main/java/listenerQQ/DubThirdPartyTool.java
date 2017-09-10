package listenerQQ;

import android.os.Bundle;
import android.util.Log;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.UserInfo;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import AsyncTasks.DubRequestAsyncTask;
import AsyncTasks.VickyConfig;
import bean.EntityQQResult;
import bean.EntityQQUserInfo;
import bean.EntityWeiBoUserInfo;
import bean.EntityWeiXinResult;
import bean.EntityWeiXinUserInfo;
import test.MainActivity;
import utils.JsonUtil;

public class DubThirdPartyTool {
    private MainActivity baseActivity;
    private String Tag = DubThirdPartyTool.class.getSimpleName();

    public DubThirdPartyTool(MainActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    ////////////////        QQ            ///////////////
    public IUiListener createLoginQQListener(final Tencent tencentSsoHandler, final QQLoginGetUserInfoSuccess qqLoginGetUserInfoSuccess) {
        return new BaseQQUiListener(baseActivity) {
            @Override
            protected void doComplete(String rstJson) {
                Log.i(Tag, "抱歉,QQ登录授权异常!");
                System.out.println("===========>qq result json:" + rstJson);
                EntityQQResult qqResult = (EntityQQResult) JsonUtil.fromJson(rstJson, EntityQQResult.class);
                if (null != qqResult) {
                    tencentSsoHandler.setOpenId(qqResult.getOpenid());
                    tencentSsoHandler.setAccessToken(qqResult.getAccess_token(), String.valueOf(qqResult.getExpires_in()));
                    Log.i(Tag, "使用QQ登录授权成功!开始获取用户信息...");
                    UserInfo userInfo = new UserInfo(baseActivity, tencentSsoHandler.getQQToken());
                    userInfo.getUserInfo(createGetQQUserInfoListener(qqResult.getOpenid(), qqLoginGetUserInfoSuccess));
                } else {
                    Log.i(Tag, "抱歉,解析QQ返回的授权信息失败!");
                }
            }

            @Override
            protected void doError(String error) {
                Log.i(Tag, "抱歉,QQ登录授权异常!");
            }

            @Override
            protected void doCancel() {
                Log.i(Tag, "QQ登录已取消!");
            }
        };
    }

    private IUiListener createGetQQUserInfoListener(final String openId, final QQLoginGetUserInfoSuccess qqLoginGetUserInfoSuccess) {
        return new BaseQQUiListener(baseActivity) {
            @Override
            protected void doComplete(String rstJson) {
                System.out.println("===========>qq result json:" + rstJson);
                EntityQQUserInfo qqUserInfo = (EntityQQUserInfo) JsonUtil.fromJson(rstJson, EntityQQUserInfo.class);
                if (null != qqUserInfo) {
                    if (null != qqLoginGetUserInfoSuccess)
                        qqLoginGetUserInfoSuccess.getUserInfoSuccess(openId, qqUserInfo);
                    Log.i(Tag, "获取QQ授权登录用户信息成功!昵称:" + qqUserInfo.getNickname());
                } else {
                    Log.i(Tag, "抱歉,解析QQ返回的授权登录用户信息失败!");
                }
            }

            @Override
            protected void doError(String error) {
                Log.i(Tag, "抱歉,获取QQ授权登录用户信息异常!");
            }

            @Override
            protected void doCancel() {
                Log.i(Tag, "获取QQ授权登录用户信息已取消!");
            }
        };
    }

    ////////////////        微信             ///////////////
    public void startWeiXinLogin(String openIdForWeiXin) {
        IWXAPI WXApi = WXAPIFactory.createWXAPI(baseActivity, openIdForWeiXin, true);
        WXApi.registerApp(openIdForWeiXin);
        if (WXApi.isWXAppInstalled() && WXApi.isWXAppSupportAPI()) {
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_demo";
            if (WXApi.sendReq(req)) {
                Log.i(Tag, "微信登录请求中,请等待微信响应...");
            } else {
                Log.i(Tag, "抱歉,微信通信失败,请重试!");
            }
        } else {
            Log.i(Tag, "抱歉,您未安装微信,请先安装微信!");
        }
    }

    public void getWeiXinUserInfo(String openIdForWeiXin, String openSecertForWeiXin, String code, final WeiXinLoginGetUserInfoSuccess weiXinLoginGetUserInfoSuccess) {
        DubRequestAsyncTask requestAsyncTask1 = new DubRequestAsyncTask(baseActivity,
                baseActivity.mainHandler,
                "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" +
                        openIdForWeiXin + "&secret=" +
                        openSecertForWeiXin + "&code=" +
                        code + "&grant_type=authorization_code",
                VickyConfig.RequestType_Get, false, new DubRequestAsyncTask.RequestCallback() {
            @Override
            public void onRequestPrepare() {
            }

            @Override
            public void onChangeProgress(int progress, int successFlag) {
            }

            @Override
            public void onCompleted(String returnMsg) {
                System.out.println("===========>weixin1 result json:" + returnMsg);
                final EntityWeiXinResult weiXinResult = (EntityWeiXinResult) JsonUtil.fromJson(returnMsg, EntityWeiXinResult.class);
                if (null != weiXinResult) {
                    Log.i(Tag, "使用微信登录授权成功!开始获取用户信息...");
                    DubRequestAsyncTask requestAsyncTask2 = new DubRequestAsyncTask(baseActivity,
                            baseActivity.mainHandler,
                            "https://api.weixin.qq.com/sns/userinfo?access_token=" +
                                    weiXinResult.getAccess_token() + "&openid=" +
                                    weiXinResult.getOpenid(),
                            VickyConfig.RequestType_Get, false, new DubRequestAsyncTask.RequestCallback() {
                        @Override
                        public void onRequestPrepare() {
                        }

                        @Override
                        public void onChangeProgress(int progress, int successFlag) {
                        }

                        @Override
                        public void onCompleted(String returnMsg) {
                            System.out.println("===========>weixin2 result json:" + returnMsg);
                            EntityWeiXinUserInfo weiXinUserInfo = (EntityWeiXinUserInfo) JsonUtil.fromJson(returnMsg, EntityWeiXinUserInfo.class);
                            if (null != weiXinUserInfo) {
                                if (null != weiXinLoginGetUserInfoSuccess)
                                    weiXinLoginGetUserInfoSuccess.getUserInfoSuccess(weiXinResult.getOpenid(), weiXinUserInfo);
                                Log.i(Tag, "获取微信授权登录用户信息成功!昵称:" + weiXinUserInfo.getNickname());
                            } else {
                                Log.i(Tag, "抱歉,解析微信返回的授权登录用户信息失败!");
                            }
                        }

                        @Override
                        public boolean onCancel() {
                            Log.i(Tag, "微信登录已取消!");
                            return false;
                        }
                    });
                    requestAsyncTask2.execute();
                } else {
                    Log.i(Tag, "抱歉,解析微信返回的授权信息失败!");
                }
            }

            @Override
            public boolean onCancel() {
                Log.i(Tag, "微信登录已取消!");
                return false;
            }
        });
        requestAsyncTask1.execute();
    }

    public WeiboAuthListener createLoginWeiBoListenerListener(WeiBoLoginGetUserInfoSuccess weiBoLoginGetUserInfoSuccess) {
        return new WeiBoAuthListener(weiBoLoginGetUserInfoSuccess);
    }

    //////////////////////////////////////////    微博    //////////////////////////////////////////
    private class WeiBoAuthListener implements WeiboAuthListener {
        private WeiBoLoginGetUserInfoSuccess weiBoLoginGetUserInfoSuccess;

        public WeiBoAuthListener(WeiBoLoginGetUserInfoSuccess weiBoLoginGetUserInfoSuccess) {
            this.weiBoLoginGetUserInfoSuccess = weiBoLoginGetUserInfoSuccess;
        }

        @Override
        public void onComplete(Bundle values) {
            baseActivity.mainHandler.sendEmptyMessage(VickyConfig.msg_closeLoading);//将loading关闭
            final Oauth2AccessToken mAccessToken = Oauth2AccessToken.parseAccessToken(values);//从Bundle中解析Token
            if (null != mAccessToken) {
                if (mAccessToken.isSessionValid()) {
                    Log.i(Tag, "使用微博登录授权成功!");
                    DubRequestAsyncTask requestAsyncTask = new DubRequestAsyncTask(baseActivity,
                            baseActivity.mainHandler,
                            "https://api.weibo.com/2/users/show.json?access_token=" +
                                    mAccessToken.getToken() + "&uid=" +
                                    mAccessToken.getUid(),
                            VickyConfig.RequestType_Get, false, new DubRequestAsyncTask.RequestCallback() {
                        @Override
                        public void onRequestPrepare() {
                        }

                        @Override
                        public void onChangeProgress(int progress, int successFlag) {
                        }

                        @Override
                        public void onCompleted(String returnMsg) {
                            Log.i(Tag, "weibo result json:" + returnMsg);
                            EntityWeiBoUserInfo weiBoUserInfo = (EntityWeiBoUserInfo) JsonUtil.fromJson(returnMsg, EntityWeiBoUserInfo.class);
                            if (null != weiBoUserInfo) {
                                if (null != weiBoLoginGetUserInfoSuccess)
                                    weiBoLoginGetUserInfoSuccess.getUserInfoSuccess(mAccessToken.getUid(), weiBoUserInfo);
                                Log.i(Tag, "获取微博授权登录用户信息成功!昵称:" + weiBoUserInfo.getScreen_name());
                            } else {
                                Log.i(Tag, "抱歉,解析微博返回的授权登录用户信息失败!");
                            }
                        }

                        @Override
                        public boolean onCancel() {
                            Log.i(Tag, "微博登录已取消!");
                            return false;
                        }
                    });
                    requestAsyncTask.execute();
                } else {
                    Log.i(Tag, "抱歉,微博登录授权失败!Code:" + values.getString("code"));
                }
            } else {
                Log.i(Tag, "抱歉,解析微博返回的授权信息失败!");
            }
        }

        @Override
        public void onCancel() {
            baseActivity.mainHandler.sendEmptyMessage(VickyConfig.msg_closeLoading);//将loading关闭
            Log.i(Tag, "微博登录已取消!");
        }

        @Override
        public void onWeiboException(WeiboException error) {
            baseActivity.mainHandler.sendEmptyMessage(VickyConfig.msg_closeLoading);//将loading关闭
            Log.i(Tag, "抱歉,微博登录异常!" + error.getMessage());
        }
    }

    ////////////////////////////////////////////    interface    ///////////////////////////////////////////
    public interface QQLoginGetUserInfoSuccess {
        void getUserInfoSuccess(String openId, EntityQQUserInfo qqUserInfo);
    }

    public interface WeiXinLoginGetUserInfoSuccess {
        void getUserInfoSuccess(String openId, EntityWeiXinUserInfo weiXinUserInfo);
    }

    public interface WeiBoLoginGetUserInfoSuccess {
        void getUserInfoSuccess(String openId, EntityWeiBoUserInfo weiBoUserInfo);
    }
}

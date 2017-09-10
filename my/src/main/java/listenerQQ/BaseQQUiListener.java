package listenerQQ;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;


public class BaseQQUiListener implements IUiListener {//QQ登录回调
    private Context context;
    private String Tag = BaseQQUiListener.class.getSimpleName();

    public BaseQQUiListener(Context context) {
        this.context = context;
    }

    @Override
    public void onComplete(Object response) {
        if(null != response) {
            String rstJson = response.toString();
            if (!TextUtils.isEmpty(rstJson)) {
                doComplete(rstJson);
            }else{
                Log.i(Tag,"抱歉,QQ返回操作失败,请重试!");
            }
        }else{
            Log.i(Tag,"抱歉,QQ返回操作失败,请重试!");
        }
    }
    protected void doComplete(String rstJson) {}

    @Override
    public void onError(UiError e) {
        doError(e.errorMessage);
    }

    protected void doError(String error) {}

    @Override
    public void onCancel() {
        doCancel();
    }

    protected void doCancel() {}
}

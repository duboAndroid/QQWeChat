package AsyncTasks;

public class VickyConfig {
    //value
    public static final String ProjectDir = "YiDingSports";
    public static final int successCode = 1;//请求成功码
    public static final int successCode1 = 0;//其他请求成功码
    public static final int successCode2 = 2;//其他请求成功码
    public static final int eachPageSize = 15;//分页大小
    public static final int eachPageIsMax = Integer.MAX_VALUE;//设一个最大值不分页
    public static final long CountDownNum = 1 * 60 * 1000;//倒计时
    //http
    public static final String RequestType_Post = "POST";
    public static final String RequestType_Get = "GET";
    public static final String RequestType_Put = "PUT";
    public static final String RequestType_Delete = "DELETE";
    public static final String RequestType_Upload = "UPLOAD";//自定义的
    //request code
    public static final int req_ActivityCameraPhoto = 1,req_ActivityPictureSelect = 2,req_ActivityPictureCut = 3;
    //handler msg
    public static final int msg_showInfo = -1,msg_showLoading = -2,msg_returnJson = -3,msg_closeLoading = -4,msg_showInput = -5,msg_alipay = -6;
}

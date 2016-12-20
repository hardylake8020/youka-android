package com.zzqs.app_kc.z_kc.entitiy;


/**
 * Created by lance on 16/4/6.
 */
public class ErrorInfo<T> {
    public final static String ERR = "err";
    public final static String SUCCESS = "success";
    private String type;
    private String message;
    public T object;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        if (object != null) {
            return "ErrorInfo{" +
                    "type='" + type + '\'' +
                    ", message='" + message + '\'' +
                    ", object=" + object.toString() +
                    '}';
        } else {
            return "ErrorInfo{" +
                    "type='" + type + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}

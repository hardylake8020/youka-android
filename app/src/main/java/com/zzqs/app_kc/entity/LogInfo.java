package com.zzqs.app_kc.entity;

import com.zzqs.app_kc.db.hibernate.annotation.COLUMN;
import com.zzqs.app_kc.db.hibernate.annotation.ID;
import com.zzqs.app_kc.db.hibernate.annotation.TABLE;

/**
 * Created by lance on 15/4/17.
 */
@TABLE(name = "log_info")
public class LogInfo {
    @ID
    @COLUMN(name = "_id")
    private int _id;
    @COLUMN(name = "type")
    private String type;
    @COLUMN(name = "time")
    private long time;
    @COLUMN(name = "content")
    private String content;

    public static final String TYPE_OF_NORMAL = "流程事件";
    public static final String TYPE_OF_ERR = "错误事件";
    public static final String TYPE_OF_WARN = "警告事件";

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "LogInfo{" +
                "type='" + type + '\'' +
                ", time='" + time + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}

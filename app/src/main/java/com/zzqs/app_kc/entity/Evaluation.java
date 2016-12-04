package com.zzqs.app_kc.entity;

/**
 * Created by lance on 15/11/2.
 */
public class Evaluation {
    private String serialNo;
    private String companyName;
    private String content;
    private String updateTime;
    private String createTime;
    private int type;
    public static final int ALL = 0;
    public static final int GOOD = 1;
    public static final int MID = 2;
    public static final int BAD = 3;
    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Evaluation{" +
                "serialNo='" + serialNo + '\'' +
                ", companyName='" + companyName + '\'' +
                ", content='" + content + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", createTime='" + createTime + '\'' +
                ", type=" + type +
                '}';
    }
}

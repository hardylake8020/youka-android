package com.zzqs.app_kc.entity;

/**
 * Created by lx on 12/21/14.
 */
public  class Upgrade {
    private  int version;
    private String url;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Upgrade{" +
                "version=" + version +
                ", url='" + url + '\'' +
                '}';
    }
}

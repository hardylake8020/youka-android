package com.zzqs.app_kc.z_kc.entitiy;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lance on 2016/12/26.
 */

public class TenderEventPhoto implements Parcelable {
  private String name;
  private String url;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeString(this.url);
  }

  public TenderEventPhoto() {
  }

  protected TenderEventPhoto(Parcel in) {
    this.name = in.readString();
    this.url = in.readString();
  }

  public static final Parcelable.Creator<TenderEventPhoto> CREATOR = new Parcelable.Creator<TenderEventPhoto>() {
    @Override
    public TenderEventPhoto createFromParcel(Parcel source) {
      return new TenderEventPhoto(source);
    }

    @Override
    public TenderEventPhoto[] newArray(int size) {
      return new TenderEventPhoto[size];
    }
  };
}

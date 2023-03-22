package com.jiangtai.count.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class FeelListBean implements Parcelable {
    //纬度上限
    private Double topLatitude;
    //纬度下限
    private Double bottomLatitude;
    //经度上限
    private Double topLongitude;
    //经度下限
    private Double bottomLongitude;


    private String type;
    private String targetType;


    private String time;
    public static final Creator<FeelListBean> CREATOR = new Creator<FeelListBean>() {
        @Override
        public FeelListBean createFromParcel(Parcel in) {
            return new FeelListBean(in);
        }

        @Override
        public FeelListBean[] newArray(int size) {
            return new FeelListBean[size];
        }
    };

    public FeelListBean(){

    }

    protected FeelListBean(Parcel in) {
        if (in.readByte() == 0) {
            topLatitude = null;
        } else {
            topLatitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            bottomLatitude = null;
        } else {
            bottomLatitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            topLongitude = null;
        } else {
            topLongitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            bottomLongitude = null;
        } else {
            bottomLongitude = in.readDouble();
        }
        type = in.readString();
        targetType = in.readString();
        time = in.readString();
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Double getTopLatitude() {
        return topLatitude;
    }

    public void setTopLatitude(Double topLatitude) {
        this.topLatitude = topLatitude;
    }

    public Double getBottomLatitude() {
        return bottomLatitude;
    }

    public void setBottomLatitude(Double bottomLatitude) {
        this.bottomLatitude = bottomLatitude;
    }

    public Double getTopLongitude() {
        return topLongitude;
    }

    public void setTopLongitude(Double topLongitude) {
        this.topLongitude = topLongitude;
    }

    public Double getBottomLongitude() {
        return bottomLongitude;
    }

    public void setBottomLongitude(Double bottomLongitude) {
        this.bottomLongitude = bottomLongitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (topLatitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(topLatitude);
        }
        if (bottomLatitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(bottomLatitude);
        }
        if (topLongitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(topLongitude);
        }
        if (bottomLongitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(bottomLongitude);
        }
        dest.writeString(type);
        dest.writeString(targetType);
        dest.writeString(time);
    }
}

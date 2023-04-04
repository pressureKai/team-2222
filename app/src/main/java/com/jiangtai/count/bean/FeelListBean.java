package com.jiangtai.count.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class FeelListBean implements Parcelable {
    //纬度上限
    private Double wdmin;
    //纬度下限
    private Double wdmax;
    //经度上限
    private Double jdmin;
    //经度下限
    private Double jdmax;


    private String fxsj;
    private String mblb;
    private String fxmb = "";



    private Double id;


    public Double getId() {
        return id;
    }

    public void setId(Double id) {
        this.id = id;
    }

    public String getFxsj() {
        return fxsj;
    }

    public void setFxsj(String fxsj) {
        this.fxsj = fxsj;
    }

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
            wdmin = null;
        } else {
            wdmin = in.readDouble();
        }
        if (in.readByte() == 0) {
            wdmax = null;
        } else {
            wdmax = in.readDouble();
        }
        if (in.readByte() == 0) {
            jdmin = null;
        } else {
            jdmin = in.readDouble();
        }
        if (in.readByte() == 0) {
            jdmax = null;
        } else {
            jdmax = in.readDouble();
        }
        mblb = in.readString();
        fxmb = in.readString();
        time = in.readString();
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMblb() {
        return mblb;
    }

    public void setMblb(String mblb) {
        this.mblb = mblb;
    }

    public String getFxmb() {
        return fxmb;
    }

    public void setFxmb(String fxmb) {
        this.fxmb = fxmb;
    }

    public Double getWdmin() {
        return wdmin;
    }

    public void setWdmin(Double wdmin) {
        this.wdmin = wdmin;
    }

    public Double getWdmax() {
        return wdmax;
    }

    public void setWdmax(Double wdmax) {
        this.wdmax = wdmax;
    }

    public Double getJdmin() {
        return jdmin;
    }

    public void setJdmin(Double jdmin) {
        this.jdmin = jdmin;
    }

    public Double getJdmax() {
        return jdmax;
    }

    public void setJdmax(Double jdmax) {
        this.jdmax = jdmax;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (wdmin == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(wdmin);
        }
        if (wdmax == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(wdmax);
        }
        if (jdmin == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(jdmin);
        }
        if (jdmax == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(jdmax);
        }
        dest.writeString(mblb);
        dest.writeString(fxmb);
        dest.writeString(time);
    }
}

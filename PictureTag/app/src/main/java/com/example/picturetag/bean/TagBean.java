package com.example.picturetag.bean;

import android.os.Parcel;

public class TagBean implements ITagBean {

    private int type = -1;           //标签类型 0: 普通标签 1: 商品标签
    private String tagName;
    private String goodsId;
    private String tagUrl;
    private int arrow = -1;         //标签朝向 0：向右展开，1：向左展开
    private float sx = -1;          //左边距比例
    private float sy = -1;          //上边距比例
    private boolean isHasAdded = false;


    public TagBean(int type, String tagName, String tagUrl, int arrow, float sx, float sy) {
        this.type = type;
        this.tagName = tagName;
        this.tagUrl = tagUrl;
        this.arrow = arrow;
        this.sx = sx;
        this.sy = sy;
    }

    public int getType() {
        return type;
    }

    public boolean isHasAdded() {
        return isHasAdded;
    }

    public void setHasAdded(boolean hasAdded) {
        isHasAdded = hasAdded;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int getIsCustom() {
        if(ITagBean.TYPE_CUSTOM == type){
            return 1;
        }else {
            return 0;
        }
    }

    public String getTagName() {
        return tagName;
    }

    @Override
    public String getGoodsId() {
        return goodsId;
    }

    @Override
    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagUrl() {
        return tagUrl;
    }

    public void setTagUrl(String tagUrl) {
        this.tagUrl = tagUrl;
    }

    public int getArrow() {
        return arrow;
    }

    public void setArrow(int arrow) {
        this.arrow = arrow;
    }

    public float getSx() {
        return sx;
    }

    public void setSx(float sx) {
        this.sx = sx;
    }

    public float getSy() {
        return sy;
    }

    public void setSy(float sy) {
        this.sy = sy;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeString(this.tagName);
        dest.writeString(this.goodsId);
        dest.writeString(this.tagUrl);
        dest.writeInt(this.arrow);
        dest.writeFloat(this.sx);
        dest.writeFloat(this.sy);
    }

    protected TagBean(Parcel in) {
        this.type = in.readInt();
        this.tagName = in.readString();
        this.goodsId = in.readString();
        this.tagUrl = in.readString();
        this.arrow = in.readInt();
        this.sx = in.readFloat();
        this.sy = in.readFloat();
    }

    public static final Creator<TagBean> CREATOR = new Creator<TagBean>() {
        @Override
        public TagBean createFromParcel(Parcel source) {
            return new TagBean(source);
        }

        @Override
        public TagBean[] newArray(int size) {
            return new TagBean[size];
        }
    };
}

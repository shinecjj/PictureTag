package com.example.picturetag.bean;

import android.os.Parcelable;

public interface ITagBean extends Parcelable {

    int MAX_TAG_COUNT = 15;

    int ARROW_RIGHT = 0;    //name在铆点的右侧
    int ARROW_LEFT = 1;     //name在铆点的左侧

    int TYPE_GENERAL = 0;    //普通标签
    int TYPE_GOODS = 1;      //商品标签
    int TYPE_CUSTOM = 2;     //自定义标签

    int getType();

    boolean isHasAdded();

    void setHasAdded(boolean hasAdded);

    int getIsCustom();

    void setType(int id);

    String getTagName();

    String getGoodsId();

    void setGoodsId(String goodsId);

    void setTagName(String tagName);

    String getTagUrl();

    void setTagUrl(String tagUrl);

    int getArrow() ;

    void setArrow(int arrow);

    float getSx();

    void setSx(float sx);

    float getSy();

    void setSy(float sy);
}

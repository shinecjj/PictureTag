package com.example.picturetag.bean;

import java.io.Serializable;

/**
 * TYPE中值的定义和MediaModel中值相同
 *
 */

public interface IChooserModel extends Serializable{

    int TYPE_IMAGE = 0;
    int TYPE_VIDEO = 1;
    int TYPE_GIF = 2;

    String getFilePath();

    int getType();

    int getId();

    long getDate();

    long getDuration();

    long getFileSize();

    String getMimeType();

    String getThumbnail();

    int getWidth();

    int getHeight();
}

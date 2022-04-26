package org.vincenttsang.viewer;

import java.io.File;
import java.net.URL;

public class ViewerImageItem {
    private File ImageFile;
    private String ImageName;
    private String ImageInfo;

    ViewerImageItem() {
        this.ImageFile = null;
        this.ImageName = "";
        this.ImageInfo = "";
    }

    public File getImageFile() {
        return ImageFile;
    }

    public void setImageFile(File imageFile) {
        ImageFile = imageFile;
    }

    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String imageName) {
        ImageName = imageName;
    }

    public String getImageInfo() {
        return ImageInfo;
    }

    public void setImageInfo(String imageInfo) {
        ImageInfo = imageInfo;
    }

    public String getImageUrl() {
        return "file://" + ImageFile.getAbsolutePath();
    }
}

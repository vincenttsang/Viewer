package org.vincenttsang.viewer;

import org.apache.sanselan.ImageInfo;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ViewerImageItem {
    private File imageFile;
    private String imageName;
    private String imageInfoString;

    ViewerImageItem() {
        this.imageFile = null;
        this.imageName = "";
        this.imageInfoString = "";
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
        if (isImgFile()) {
            try {
                this.setImageInfo();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ImageReadException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.imageInfoString = "该文件非图片文件";
        }
        System.out.println(this.imageInfoString);
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageInfo() {
        return imageInfoString;
    }

    public void setImageInfo() throws IOException, ImageReadException {
        ImageInfo imageInfo = Sanselan.getImageInfo(imageFile);
        this.imageInfoString = "图片颜色类型：" + imageInfo.getColorTypeDescription();
        this.imageInfoString += "\n图片文件格式：" + imageInfo.getFormatName();
        this.imageInfoString += "\n图片文件格式(根据Magic Number)：" + Sanselan.guessFormat(imageFile);
        this.imageInfoString += "\n图片Mime信息：" + imageInfo.getMimeType();
        var imageSize = Sanselan.getImageSize(imageFile);
        this.imageInfoString += "\n图片尺寸：" + imageSize.getWidth() + "*" + imageSize.getHeight();
    }

    public String getImageUrl() {
        return Path.of(imageFile.getAbsolutePath()).toUri().toString();
    }

    public boolean isImgFile() {
        return Sanselan.hasImageFileExtension(imageFile);
    }
}

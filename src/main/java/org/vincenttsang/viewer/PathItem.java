package org.vincenttsang.viewer;

import javafx.beans.property.SimpleStringProperty;

import java.io.File;

public class PathItem {
    private SimpleStringProperty mDirName;
    private SimpleStringProperty mDirSize;
    private File mFile;

    public PathItem(String path) {
        mFile = new File(path);
        String name = mFile.getName();
        if (name.equals("")) {
            name = "根目录:  /";
        }
        //System.out.println(mFile.toPath());
        mDirName = new SimpleStringProperty(name);
        if (mFile.isDirectory()) {
            mDirSize = new SimpleStringProperty("");
        } else {
            mDirSize = new SimpleStringProperty("" + mFile.length());
        }
    }

    public String getDirName() {
        return mDirName.get();
    }

    public void setDirName(String dirName) {
        this.mDirName.set(dirName);
    }

    public String getDirSize() {
        return mDirSize.get();
    }

    public void setDirSize(String dirSize) {
        this.mDirSize.set(dirSize);
    }

    public File getFile() {
        return mFile;
    }
}

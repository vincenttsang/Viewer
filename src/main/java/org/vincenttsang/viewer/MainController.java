package org.vincenttsang.viewer;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.css.Style;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

public class MainController {
    @FXML
    private TreeTableView<PathItem> dirsTree;
    @FXML
    private ListView ImgList;

    public ViewerImageItem ImgItem;

    public void initialize() {
        ImgItem = new ViewerImageItem();
        initDirsTree();
    }

    public void initDirsTree() {
        //TreeTableView<PathItem> dirsTree = new MainController().dirsTree;
        TreeTableColumn<PathItem, String> dirsColumn = new TreeTableColumn<>(
                "名称");
        dirsColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<PathItem, String> param) -> {
                    ReadOnlyStringWrapper stringWrapper = new ReadOnlyStringWrapper("");
                    if(param.getValue().getValue() != null) {
                        stringWrapper = new ReadOnlyStringWrapper(param.getValue().getValue().getDirName());
                        return stringWrapper;
                    } else {
                        System.out.println("null");
                    }
                    return stringWrapper;
                }
        );

        TreeTableColumn<PathItem, String> sizeColumn = new TreeTableColumn<>(
                "大小");
        sizeColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<PathItem, String> param) -> {
                    ReadOnlyStringWrapper stringWrapper = new ReadOnlyStringWrapper("");
                    if(param.getValue().getValue() != null) {
                        stringWrapper = new ReadOnlyStringWrapper(param.getValue().getValue().getDirSize());
                        return stringWrapper;
                    } else {
                        System.out.println("null");
                    }
                    return stringWrapper;
                });
        dirsTree.getColumns().clear();
        dirsColumn.setPrefWidth(dirsTree.getPrefWidth() / 2);
        sizeColumn.setPrefWidth(dirsTree.getPrefWidth() / 2);
        dirsTree.getColumns().add(dirsColumn);
        dirsTree.getColumns().add(sizeColumn);

        TreeItem<PathItem> root = new TreeItem<>(new PathItem("/")); //todo 暂时设为根目录
        //应用数据集：
        dirsTree.setRoot(root);
        //为文件夹树设置点击事件：
        dirsTree.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                TreeItem node = dirsTree.getSelectionModel().getSelectedItem();
                PathItem item = (PathItem) node.getValue();
                System.out.println("Node click: " + item.getDirName());
                File[] filesList;
                if (item.getFile().isDirectory()) {
                    node.getChildren().clear();
                    filesList = item.getFile().listFiles();
                    if (filesList == null || filesList.length == 0) {
                        System.out.println("this is an empty directory.");
                    } else {
                        Arrays.stream(filesList).map(s -> new PathItem(s.getAbsolutePath())).forEachOrdered(nextDirUnit -> {
                            TreeItem nextDirItem = new TreeItem<>(nextDirUnit);
                            if (nextDirUnit.getFile().isDirectory() && nextDirUnit.getFile().list() != null && Objects.requireNonNull(nextDirUnit.getFile().list()).length > 0) { //如果当前添加的叶子文件是非空文件夹，则添加一个空数据作为叶子的叶子，使得用户看到这个文件夹时知道可以继续点击
                                nextDirItem.getChildren().add(new TreeItem<>(new PathItem(" ")));
                            }
                            node.getChildren().add(nextDirItem);
                        });
                    }
                } else {
                    ImgItem.setImageFile(item.getFile());
                    System.out.println(ImgItem.getImageUrl());
                    addImg(ImgItem);
                    System.out.println("this a file.");
                }
            }
        });
    }

    public void addImg(ViewerImageItem imgItem) {
        Image image = new Image(imgItem.getImageUrl(),
                350, // requested width
                350, // requested height
                true, // preserve ratio
                true, // smooth rescaling
                false // load in background
        );
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        ImgList.getItems().add(imageView);
    }
}
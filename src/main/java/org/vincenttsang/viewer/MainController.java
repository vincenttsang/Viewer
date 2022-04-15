package org.vincenttsang.viewer;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseEvent;

import java.io.File;

public class MainController {
    @FXML
    private TreeTableView<PathItem> dirsTree;

    public void initialize() {
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
                if (item.getFile().isDirectory()) {
                    node.getChildren().clear();
                    File[] filesList = item.getFile().listFiles();
                    if (filesList == null || filesList.length == 0) {
                        System.out.println("this is an empty directory.");
                    } else {
                        for (File s : filesList) {
                            PathItem nextDirUnit = new PathItem(s.getAbsolutePath());
                            TreeItem nextDirItem = new TreeItem<>(nextDirUnit);
                            if (nextDirUnit.getFile().isDirectory() && nextDirUnit.getFile().list() != null && nextDirUnit.getFile().list().length > 0) { //如果当前添加的叶子文件是飞空文件夹，则添加一个空数据作为叶子的叶子，使得用户看到这个文件夹时知道可以继续点击
                                nextDirItem.getChildren().add(new TreeItem<>());
                            }
                            node.getChildren().add(nextDirItem);
                        }
                    }
                } else {
                    System.out.println("this is not a directory.");
                }
            }
        });
    }
}
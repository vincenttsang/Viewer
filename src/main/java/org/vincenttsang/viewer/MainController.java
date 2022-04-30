package org.vincenttsang.viewer;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

public class MainController {
    public ViewerImageItem ImgItem;
    @FXML
    private TreeTableView<PathItem> dirsTree;
    @FXML
    private ListView ImgList;
    @FXML
    private Label infoLabel;
    private String clickedImgUrl;

    @FXML
    public void clickOnImage(MouseEvent arg0) throws MalformedURLException, URISyntaxException {
        System.out.println("你点击了 " + ImgList.getSelectionModel().getSelectedItem());
        ImageView imgView = (ImageView) ImgList.getSelectionModel().getSelectedItem();
        clickedImgUrl = imgView.getId();
        ViewerImageItem tmpImgItem = new ViewerImageItem();
        tmpImgItem.setImageFile(new File(new URL(clickedImgUrl).toURI()));
        infoLabel.setText(tmpImgItem.getImageInfo());
        infoLabel.setWrapText(true);
    }

    @FXML
    public void clickOnOpenBtn(MouseEvent arg0) {
        if (clickedImgUrl != null) {
            System.out.println("打开图片" + clickedImgUrl);
            Stage secondStage = new Stage();
            Label label = new Label("新窗口"); // 放一个标签
            Image image = new Image(clickedImgUrl,
                    1280, // requested width
                    720, // requested height
                    true, // preserve ratio
                    true, // smooth rescaling
                    false // load in background
            );
            ImageView imgView = new ImageView();
            imgView.setImage(image);
            StackPane secondPane = new StackPane(imgView);
            Scene secondScene = new Scene(secondPane, 1280, 720);
            secondStage.setTitle("查看" + clickedImgUrl);
            secondStage.setScene(secondScene);
            secondStage.show();
        } else {
            System.out.println("未选中任何图片");
        }
    }

    public void initialize() {
        ImgItem = new ViewerImageItem();
        initDirsTree();
        infoLabel.setText("请选择一张图片");
        infoLabel.setWrapText(true);
    }

    public void initDirsTree() {
        //TreeTableView<PathItem> dirsTree = new MainController().dirsTree;
        TreeTableColumn<PathItem, String> dirsColumn = new TreeTableColumn<>(
                "名称");
        dirsColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<PathItem, String> param) -> {
                    ReadOnlyStringWrapper stringWrapper = new ReadOnlyStringWrapper("");
                    if (param.getValue().getValue() != null) {
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
                    if (param.getValue().getValue() != null) {
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
                ImgList.getItems().clear();
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
                        for (File s : filesList) {
                            PathItem nextDirUnit = new PathItem(s.getAbsolutePath());
                            TreeItem nextDirItem = new TreeItem<>(nextDirUnit);
                            if (nextDirUnit.getFile().isDirectory() && nextDirUnit.getFile().list() != null && Objects.requireNonNull(nextDirUnit.getFile().list()).length > 0) { //如果当前添加的叶子文件是非空文件夹，则添加一个空数据作为叶子的叶子，使得用户看到这个文件夹时知道可以继续点击
                                nextDirItem.getChildren().add(new TreeItem<>(new PathItem(" ")));
                            }
                            node.getChildren().add(nextDirItem);
                            addImgFromFile(s);
                        }
                    }
                }
            }
        });
    }

    public void addImgFromFile(File file) {
        ImgItem.setImageFile(file);
        System.out.println(ImgItem.getImageUrl());
        if (ImgItem.isImgFile()) {
            addImg(ImgItem);
        } else {
            System.out.println("该文件不是图片文件");
        }
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
        imageView.setId(imgItem.getImageUrl());
        /*
        imageView.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event){
                System.out.println(imageView.getId());
            }
        });
        */
        ImgList.getItems().add(imageView);
        /*
        ImgList.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event){
                System.out.println(imageView.getId());
            }
        });
        */
    }
}
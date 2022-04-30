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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.sanselan.ImageReadException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

public class MainController {
    public ViewerImageItem ImgItem;
    @FXML
    private TreeTableView<PathItem> dirsTree;
    @FXML
    private ListView ImgList;
    @FXML
    private Label infoLabel;
    private String clickedImgUrl;
    private Stage myStage;
    private ImageView chosenImgView;

    public void setStage(Stage stage) {
        this.myStage = stage;
    }

    @FXML
    public void clickOnImage(MouseEvent arg0) throws MalformedURLException, URISyntaxException {
        System.out.println("你点击了 " + ImgList.getSelectionModel().getSelectedItem());
        chosenImgView = (ImageView) ImgList.getSelectionModel().getSelectedItem();
        clickedImgUrl = chosenImgView.getId();
        ViewerImageItem tmpImgItem = new ViewerImageItem();
        tmpImgItem.setImageFile(new File(new URL(clickedImgUrl).toURI()));
        infoLabel.setText(tmpImgItem.getImageInfo());
        infoLabel.setWrapText(true);
    }

    @FXML
    public void clickOnOpenBtn(MouseEvent arg0) throws IOException, URISyntaxException, ImageReadException {
        if (clickedImgUrl != null) {
            System.out.println("打开图片" + clickedImgUrl);
            Stage secondStage = new Stage();
            ViewerImageItem tmpImgItem = new ViewerImageItem();
            tmpImgItem.setImageFile(new File(new URL(clickedImgUrl).toURI()));
            Image image = new Image(clickedImgUrl,
                    tmpImgItem.getImageWidth(), // requested width
                    tmpImgItem.getImageHeight(), // requested height
                    true, // preserve ratio
                    true, // smooth rescaling
                    false // load in background
            );
            ImageView imgView = new ImageView();
            imgView.setImage(image);
            StackPane secondPane = new StackPane(imgView);
            Scene secondScene = new Scene(secondPane, tmpImgItem.getImageWidth(), tmpImgItem.getImageHeight());
            secondStage.setTitle("查看" + clickedImgUrl);
            secondStage.setScene(secondScene);
            secondStage.show();
        } else {
            System.out.println("未选中任何图片");
        }
    }

    @FXML
    public void clickOnDelBtn(MouseEvent arg0) throws MalformedURLException, URISyntaxException {
        if (clickedImgUrl != null) {
            System.out.println("删除图片" + clickedImgUrl);
            File tmpFile = new File(new URL(clickedImgUrl).toURI());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("删除图片");
            alert.setHeaderText("即将删除一张图片");
            alert.setContentText("将删除图片\"" + tmpFile.getName() + "\"");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                // ... user chose OK
                if (tmpFile.delete()) {
                    System.out.println("删除成功");
                    ImgList.getItems().remove(chosenImgView);
                    infoLabel.setText("请选择一张图片");
                    infoLabel.setWrapText(true);
                } else {
                    System.out.println("删除失败");
                }
            } else {
                // ... user chose CANCEL or closed the dialog
                System.out.println("已取消");
            }
        } else {
            System.out.println("未选中任何图片");
        }
    }

    @FXML
    public void clickOnCopyBtn(MouseEvent arg0) throws IOException, URISyntaxException {
        FileChooser pasteChooser = new FileChooser();
        File oldFile = new File(new URL(clickedImgUrl).toURI());
        pasteChooser.setTitle("复制图片");
        pasteChooser.setInitialFileName("new_" + oldFile.getName());
        File newFile = pasteChooser.showSaveDialog(myStage);
        if (newFile != null) {
            Files.copy(oldFile.toPath(), newFile.toPath());
            addImgFromFile(newFile);
        }
    }

    @FXML
    public void clickOnRenameBtn(MouseEvent arg0) throws IOException, URISyntaxException {
        if (clickedImgUrl != null) {
            File tmpFile = new File(new URL(clickedImgUrl).toURI());
            final TextInputDialog textInputDialog = new TextInputDialog(tmpFile.getName()); // 實體化TextInputDialog物件，並直接在建構子設定預設的文字內容。由於輸入一定是字串，所以對話框會直接回傳String物件，而不使用泛型
            textInputDialog.setTitle("图片重命名"); //設定對話框視窗的標題列文字
            textInputDialog.setHeaderText("重命名\"" + tmpFile.getName() + "\"？"); //設定對話框視窗裡的標頭文字。若設為空字串，則表示無標頭
            textInputDialog.setContentText("确认名称："); //設定對話框的訊息文字
            final Optional<String> opt = textInputDialog.showAndWait(); //顯示對話框，並等待對話框被關閉時才繼續執行之後的程式。
            String newFileName;
            try {
                newFileName = opt.get(); //可以直接用「textInputDialog.getResult()」來取代
            } catch (final NoSuchElementException ex) {
                newFileName = null;
            }
            if (newFileName == null) {
                //沒有確認輸入文字，而是直接關閉對話框
                System.out.println("沒有回答");
            } else {
                Path source = tmpFile.toPath();
                Files.move(source, source.resolveSibling(newFileName));
                ImgList.getItems().remove(chosenImgView);
                tmpFile = new File(String.valueOf(source.resolveSibling(newFileName)));
                addImgFromFile(tmpFile);
                infoLabel.setText("请选择一张图片");
                infoLabel.setWrapText(true);
            }
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
        // System.out.println(ImgItem.getImageUrl());
        if (ImgItem.isImgFile()) {
            addImg(ImgItem);
        } else {
            // System.out.println("该文件不是图片文件");
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
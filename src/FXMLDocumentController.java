package imagesorter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class FXMLDocumentController implements Initializable {
    
    @FXML private ImageView imageview;
    @FXML private ImageAndFile currimage;
    @FXML private Label label;
    private File[] directoryListing;
    private int counter;
    private int imagenum;
    private int totalnumofimages;
    private LinkedList<ImageAndFile> imagequeue;
    private File processfolder;
    private File keepfolder;
    private File trashfolder;
    private File wallpaperfolder;
    private File zipsfolder;
    
    @FXML
    private void process() {
        if(!imagequeue.isEmpty())
        {
            currimage = imagequeue.poll();
            imageview.setImage(currimage.getImage());
            label.setText(imagenum+"/"+totalnumofimages);
            imagenum++;
            imageview.setMouseTransparent(false);
        }
        else
        {
            //printDebugDialog("Finished");
            Platform.exit();
            System.exit(1);
        }
    }
    
    @FXML
    private void keep(File f) {
        if(f.renameTo(new File(keepfolder.getPath()+"/"+f.getName())))
        {
            f.delete();
        }
    }
    
    @FXML
    private void trash(File f) {
        if(f.renameTo(new File(trashfolder.getPath()+"/"+f.getName())))
        {
            f.delete();
        }
    }
    
    @FXML
    private void wallpaper(File f) {
        if(f.renameTo(new File(wallpaperfolder.getPath()+"/"+f.getName())))
        {
            f.delete();
        }
    }
    
    @FXML
    private void getFolderPath(String foldername)
    {
        try
        {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(null);
            if(selectedDirectory != null)
            {
                switch(foldername)
                {
                    case "process":
                        processfolder = selectedDirectory;
                        break;
                    case "keep":
                        keepfolder = selectedDirectory;
                        break;
                    case "trash":
                        trashfolder = selectedDirectory;
                        break;
                    case "wallpaper":
                        wallpaperfolder = selectedDirectory;
                        break;
                    default:
                        break;
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void showHelpDialog() {
        Alert helpdialog = new Alert(AlertType.INFORMATION);
        helpdialog.setResizable(true);
        helpdialog.setTitle("Help");
        helpdialog.setHeaderText("Help");
        helpdialog.setContentText("Click file -> begin to begin sorting through images.\n"+
                                  "left click -> keep\n"+
                                  "right click -> remove\n"+
                                  "middle mouse -> wallpaper");
        helpdialog.showAndWait();
    }
    
    private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException
    {
        File destfile = new File(destinationDir,zipEntry.getName());
        
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destfile.getCanonicalPath();
        
        if(!destFilePath.startsWith(destDirPath+File.separator))
        {
            throw new IOException("Entry is outside of the target dir: "+zipEntry.getName());
        }
        
        return destfile;
    }
    
    private void unzip(String filezip) throws IOException
    {
        File destDir = new File("process");
        byte[] buffer = new byte[1024000];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(filezip));
        ZipEntry zipentry = zis.getNextEntry();
        while(zipentry != null)
        {
            File newfile = newFile(destDir, zipentry);
            FileOutputStream fos = new FileOutputStream(newfile);
            int len;
            while((len = zis.read(buffer)) > 0)
            {
                fos.write(buffer,0,len);
            }
            fos.close();
            zipentry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }
    
    private void printDebugDialog(String dialog) {
        Alert helpdialog = new Alert(AlertType.INFORMATION);
        helpdialog.setResizable(true);
        helpdialog.setTitle("Debug Dialog");
        helpdialog.setHeaderText("Debug Dialog");
        helpdialog.setContentText(dialog);
        helpdialog.showAndWait();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try
        {
            //processfolder = new File("/home/abtahi/Desktop/pictest/process");
            //keepfolder = new File("/home/abtahi/Desktop/pictest/keep");
            //trashfolder = new File("/home/abtahi/Desktop/pictest/trash");
            //wallpaperfolder = new File("/home/abtahi/Desktop/pictest/wallpaper");
            
            processfolder = new File("process");
            keepfolder = new File("keep");
            trashfolder = new File("trash");
            wallpaperfolder = new File("wallpaper");
            zipsfolder = new File("zips");
            //File[] ziplist = zipsfolder.listFiles();
            
            /*
            AnchorPane root = new AnchorPane();
            Scene scene = new Scene(root, 1000, 600);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Choose artist");
            stage.setResizable(false);

            ListView<File> files = new ListView<File>();
            for (File file : ziplist) {
                files.getItems().add(file);
            }
            files.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    if (event.getClickCount() == 2) {
                        File selectedfile = files.getSelectionModel().getSelectedItem();
                        try {
                            unzip(selectedfile.getPath());
                            selectedfile.delete();
                            stage.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            files.setPrefSize(1000, 600);
            root.getChildren().add(files);

            stage.showAndWait();
            */
            
            counter = -1;
            directoryListing = processfolder.listFiles();
            imagequeue = new LinkedList<ImageAndFile>();
            if(directoryListing != null)
            {
                if(directoryListing.length<5)
                {
                    for(File child : directoryListing)
                    {
                        imagequeue.add(new ImageAndFile(child));
                        counter++;
                    }
                }
                else
                {
                    for(int i=0;i<4;i++)
                    {
                        imagequeue.add(new ImageAndFile(directoryListing[i]));
                        counter = i;
                    }
                    counter++;
                }
            }
            else
            {
                //printDebugDialog("Process folder is empty");
                Platform.exit();
                System.exit(0);
            }

            totalnumofimages = directoryListing.length;
            imagenum = 1;

            imageview.setMouseTransparent(true);
            imageview.setFitHeight(600);
            imageview.setFitWidth(1200);
            imageview.setPreserveRatio(true);
            imageview.setSmooth(true);
            imageview.setCache(true);
            imageview.setOnMouseClicked(event -> {
                imageview.setMouseTransparent(true);
                if(event.getButton() == MouseButton.PRIMARY)
                {
                    keep(currimage.getFile());
                }
                else if(event.getButton() == MouseButton.SECONDARY)
                {
                    trash(currimage.getFile());
                }
                else if(event.getButton() == MouseButton.MIDDLE)
                {
                    wallpaper(currimage.getFile());
                }
                if(counter<directoryListing.length)
                {
                    imagequeue.add(new ImageAndFile(directoryListing[counter++]));
                }
                process();
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }    
    
}

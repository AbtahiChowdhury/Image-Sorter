/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagesorter;

import javafx.scene.image.Image;
import java.io.File;

/**
 *
 * @author abtahi
 */
public class ImageAndFile {
    private File imagefile;
    private Image image;
    
    public ImageAndFile(File f)
    {
        imagefile = f;
        image = new Image(f.toURI().toString(),true);
    }
    public File getFile()
    {
        return imagefile;
    }
    public Image getImage()
    {
        return image;
    }
}

package skin;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SkinDetectorOutput {

    public SkinDetectorOutput() throws IOException {
    }

    public SkinDetectorOutput(String ImageInput, String ImageName, double[][][] ratio, double threshold, int input) throws IOException {
        File fileO = new File(ImageInput);
        BufferedImage imgO = ImageIO.read(fileO);
        for (int y = 0; y < imgO.getHeight(); y++) {
            for (int x = 0; x < imgO.getWidth(); x++) {
                editPixels(x,y,imgO,ratio, threshold,input);
            }
        }
        ImageIO.write(imgO,"jpg",new File("src/output/"+ImageName));
    }

    void editPixels(int width, int height, BufferedImage imgMask,double ratio[][][], double threshold, int input){
        int pixel = imgMask.getRGB(width,height);
        Color color = new Color(pixel);
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        if(ratio[red][green][blue]>=threshold)
        {
            if(input==1) {
                color = new Color(255, 255, 255); // make WHITE
            }
            imgMask.setRGB(width,height,color.getRGB());
        }
        else
        {
            color = new Color(0,0,0); // make BLACK
            imgMask.setRGB(width,height,color.getRGB());
        }
    }
}

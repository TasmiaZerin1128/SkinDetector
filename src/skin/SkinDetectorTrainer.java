package skin;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SkinDetectorTrainer {

    int[][][] skinPixel = new int[256][256][256];
    int[][][] nonskinPixel = new int[256][256][256];
    int skinPixelNumber=0;
    int nonskinPixelNumber=0;
    double[][][] ratio = new double[256][256][256];

    SkinDetectorTrainer(){

    }

    void Train(String MaskPath, String ImagePath) throws IOException {
        File fileM = new File(MaskPath);
        File fileI = new File(ImagePath);
        BufferedImage imgM = ImageIO.read(fileM);
        BufferedImage imgI = ImageIO.read(fileI);
        for (int y = 0; y < imgI.getHeight(); y++) {
            for (int x = 0; x < imgI.getWidth(); x++) {
                getSkinNonSkin(x,y, imgM,imgI);
            }
        }
    }


    void getSkinNonSkin(int width, int height, BufferedImage imgMask, BufferedImage imgImage)
    {
        int pixelM = imgMask.getRGB(width,height);
        //System.out.println("pixel " + pixelM);
        Color color = new Color(pixelM,true);
        int redM = color.getRed();
        int greenM = color.getGreen();
        int blueM = color.getBlue();


        int pixelI = imgImage.getRGB(width,height);
        Color colorI = new Color(pixelI,true);
        int redI = colorI.getRed();
        int greenI = colorI.getGreen();
        int blueI = colorI.getBlue();

        if(doesShowSkin(redM,greenM,blueM))
        {
            skinPixel[redI][greenI][blueI]++;
        }
        else
        {
            nonskinPixel[redI][greenI][blueI]++;
        }
    }

    boolean doesShowSkin(int R, int G, int B){
        if(R>250 && G>250 && B>250)
        {
            return false;   // for database train, return true
        }
        else{
            return true;
        }
    }

    void Calculate(){
        calculateProbability(skinPixel,nonskinPixel);
    }

    void calculateProbability(int[][][] skinPixel, int[][][] nonskinPixel){
        for(int i=0;i<256;i++){
            for(int j=0;j<256;j++){
                for(int k=0;k<256;k++){
                    skinPixel[i][j][k]++;
                    skinPixelNumber+=skinPixel[i][j][k];

                    nonskinPixel[i][j][k]++;
                    nonskinPixelNumber+=nonskinPixel[i][j][k];
                }
            }
        }

        double probOfSkin = (double) skinPixelNumber/(skinPixelNumber+nonskinPixelNumber);
        System.out.println("Skin & non Skin "+ skinPixelNumber+ " "+ nonskinPixelNumber);
        System.out.println("Probability: "+ probOfSkin);
        for(int i=0;i<256;i++){
            for(int j=0;j<256;j++){
                for(int k=0;k<256;k++){
                    ratio[i][j][k] = skinPixel[i][j][k]*probOfSkin/(skinPixel[i][j][k]+nonskinPixel[i][j][k]); //naive-bayes theorem
                }
            }
        }
    }

    void SkinDetection(String ImageInput, String ImageName, double threshold) throws IOException {
        File fileO = new File(ImageInput);
        BufferedImage imgO = ImageIO.read(fileO);
        for (int y = 0; y < imgO.getHeight(); y++) {
            for (int x = 0; x < imgO.getWidth(); x++) {
                editPixels(x,y,imgO, threshold);
            }
        }
        ImageIO.write(imgO,"jpg",new File("src/output/"+ImageName));
    }

    void editPixels(int width, int height, BufferedImage imgMask, double threshold){
        int pixel = imgMask.getRGB(width,height);
        Color color = new Color(pixel);
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        if(ratio[red][green][blue]>=threshold)
        {
            color = new Color(255,255,255); // make WHITE
            imgMask.setRGB(width,height,color.getRGB());
        }
        else
        {
            color = new Color(0,0,0); // make BLACK
            imgMask.setRGB(width,height,color.getRGB());
        }
    }

    public static void main(String[] args) throws IOException {

        String imagePath="src/ibtd/";
        String name = "tom.jpg";
        String image = "src/testImages/"+name;  //test Image path
        String maskPath="src/ibtd/Mask/";
        SkinDetectorTrainer st = new SkinDetectorTrainer();

        System.out.println("Starting......");

	for(int i=0;i<450;i++)
    {
        String maskFile= maskPath + String.format("%04d",i)+".bmp";
        String imageFile= imagePath + String.format("%04d",i)+".jpg";
//        String imageFile = "src/dataset/image/image ("+ i + ").jpg";       //these 2 lines for database input
//        String maskFile = "src/dataset/mask/mask ("+ i + ").png";
        st.Train(maskFile,imageFile);
        System.out.println("Image Processed: " + i);	//notification
    }
    st.Calculate();
    System.out.println("Machine Trained");
    st.SkinDetection(image,name,0.15);
    System.out.println("Image Processed");
    }
}

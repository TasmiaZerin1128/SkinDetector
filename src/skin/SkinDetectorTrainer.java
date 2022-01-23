package skin;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class SkinDetectorTrainer {

    int[][][] skinPixel = new int[256][256][256];
    int[][][] nonskinPixel = new int[256][256][256];
    int skinPixelNumber = 0;
    int nonskinPixelNumber = 0;
    double[][][] ratio = new double[256][256][256];

    SkinDetectorTrainer() {

    }

    void Train(String MaskPath, String ImagePath) throws IOException {
        File fileM = new File(MaskPath);
        File fileI = new File(ImagePath);
        BufferedImage imgM = ImageIO.read(fileM);
        BufferedImage imgI = ImageIO.read(fileI);
        for (int y = 0; y < imgI.getHeight(); y++) {
            for (int x = 0; x < imgI.getWidth(); x++) {
                getSkinNonSkin(x, y, imgM, imgI);
            }
        }
    }


    void getSkinNonSkin(int width, int height, BufferedImage imgMask, BufferedImage imgImage) {
        int pixelM = imgMask.getRGB(width, height);
        //System.out.println("pixel " + pixelM);
        Color color = new Color(pixelM, true);
        int redM = color.getRed();
        int greenM = color.getGreen();
        int blueM = color.getBlue();


        int pixelI = imgImage.getRGB(width, height);
        Color colorI = new Color(pixelI, true);
        int redI = colorI.getRed();
        int greenI = colorI.getGreen();
        int blueI = colorI.getBlue();

        if (doesShowSkin(redM, greenM, blueM)) {
            skinPixel[redI][greenI][blueI]++;
        } else {
            nonskinPixel[redI][greenI][blueI]++;
        }
    }

    boolean doesShowSkin(int R, int G, int B) {
        if (R > 250 && G > 250 && B > 250) {
            return true;   // for database train, return true
        } else {
            return false;
        }
    }

    void Calculate() {
        calculateProbability(skinPixel, nonskinPixel);
    }

    void calculateProbability(int[][][] skinPixel, int[][][] nonskinPixel) {
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                for (int k = 0; k < 256; k++) {
                    skinPixel[i][j][k]++;
                    skinPixelNumber += skinPixel[i][j][k];

                    nonskinPixel[i][j][k]++;
                    nonskinPixelNumber += nonskinPixel[i][j][k];
                }
            }
        }

        double probOfSkin = (double) skinPixelNumber / (skinPixelNumber + nonskinPixelNumber);
        System.out.println("Skin & non Skin " + skinPixelNumber + " " + nonskinPixelNumber);
        System.out.println("Probability: " + probOfSkin);
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                for (int k = 0; k < 256; k++) {
                    ratio[i][j][k] = skinPixel[i][j][k] * probOfSkin / (skinPixel[i][j][k] + nonskinPixel[i][j][k]); //naive-bayes theorem
                }
            }
        }
    }

    double[][][] getRatio() {
        return ratio;
    }

    public static void main(String[] args) throws IOException {

        String imagePath = "src/ibtd/";
        String name = "robert-downey.jpg";
        String image = "src/testImages/" + name;  //test Image path
        String maskPath = "src/ibtd/Mask/";
        SkinDetectorTrainer st = new SkinDetectorTrainer();

        System.out.println("Starting......");

        System.out.println("Use dataset or ibdb? 1 or 2");
        Scanner sc = new Scanner(System.in);
        int data = sc.nextInt();

        if (data == 1) { //dataset
            for (int i = 1; i < 87; i++) {
                String imageFile = "src/dataset/image/image (" + i + ").jpg";       //these 2 lines for database input
                String maskFile = "src/dataset/mask/mask (" + i + ").png";
                st.Train(maskFile, imageFile);
                System.out.println("Image Processed: " + i);    //notification
            }
        } else {
            for (int i = 0; i < 200; i++)  // for database, i=1; i<87
            {
                String maskFile = maskPath + String.format("%04d", i) + ".bmp";
                String imageFile = imagePath + String.format("%04d", i) + ".jpg";
                st.Train(maskFile, imageFile);
                System.out.println("Image Processed: " + i);    //notification
            }
        }
        st.Calculate();
        System.out.println("Machine Trained");
        System.out.println("Which Method?\n1.Black & White\t2.Skin Tone");
        sc = new Scanner(System.in);
        int input = sc.nextInt();
        if (data == 1) {
            SkinDetectorOutput sdO = new SkinDetectorOutput(image, name, st.getRatio(), 0.25, input);
        } else {
            SkinDetectorOutput sdO = new SkinDetectorOutput(image, name, st.getRatio(), 0.15, input);
        }
        System.out.println("Image Processed");
    }
}

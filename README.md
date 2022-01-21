# Skin Detector 👨 👩

### Detects human skin from images
This program is a simple **machine learning implementation in Java** for detecting skin pixels.

## How to run?
```
1. Enter the file name in name variable for testing.
2. Put the test image file in testImages folder.
2. The project contains 2 database set, database and ibdb. 
3. The output folder contains the result pictures.
```

## How it works?
```
We have used Naive Bayes here for classification (skin or non-skin pixel). As it is a colour image there are 256*256*256 types of pixels.

In the training function, pixel frequencies of being skin or non-skin is calculated. 
We take every pixel of the image and see if it is a pixel of skin by using the mask 
(when using ibtb folder, masks have skin pixels and white pixels, 
and in database folder, the masks contain white pixels in the places of skin tone & other pixels are black)
If the pixel is on skin, we increase its skin-frequency. 
Else we increase the non-skin-frequency. 
After processing all images, probability of a skin-pixels is calculated from the frequency using Bayes Theorem. We store this data in a file.

During testing, we simply map each pixel with the probability we calculated in training phase. 
If the probability is greater than a certain threshold, we mark that pixel as skin.
```

## Sample Test and Result Image
Solarized dark             |  Solarized Ocean
:-------------------------:|:-------------------------:
![Test Image](https://github.com/TasmiaZerin1128/SkinDetector/blob/master/src/testImages/robert-downey.jpg) | ![Result Image](https://github.com/TasmiaZerin1128/SkinDetector/blob/master/src/output/robert-downey.jpg)


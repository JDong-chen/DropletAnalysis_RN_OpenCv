package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.android.Utils;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.CvType.CV_8UC1;


public class RNOpenCvLibraryModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public RNOpenCvLibraryModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNOpenCvLibrary";
    }

    @ReactMethod
    public void pretreat(String path, String name, String imgBase64, Callback errorCallback, Callback successCallback) {
        try {

            Mat srcImg = Imgcodecs.imread(path,Imgcodecs.IMREAD_GRAYSCALE);
            Mat destImg = new Mat();

            String newPath = path.replace(name, name.replace(".jpg","_Pretre.jpg"));

            Imgproc.medianBlur(srcImg,destImg, 5);//中值滤波，降噪
            Imgcodecs.imwrite(newPath, destImg);


            String result = returnBase64Img(destImg, imgBase64);

            successCallback.invoke("success",result, newPath);

        } catch (Exception e) {
            errorCallback.invoke(e.getMessage());
        }
    }

    @ReactMethod
    public  void  thresholding(String path, String name, String imgBase64, Callback errorCallback, Callback successCallback) {
        try {

            Mat srcImg = Imgcodecs.imread(path,Imgcodecs.IMREAD_UNCHANGED);
            Mat destImg = new Mat();


            Imgproc.adaptiveThreshold(srcImg,destImg,255,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                    Imgproc.THRESH_BINARY_INV,11,2);//自适应二值化
            String newPath = path.replace(name, name.replace(".jpg","_Thre.jpg"));
            Imgcodecs.imwrite(newPath, destImg);

            String result = returnBase64Img(destImg, imgBase64);

            successCallback.invoke("success",result, newPath);



        } catch (Exception e) {
            errorCallback.invoke(e.getMessage());
        }
    }

    @ReactMethod
    public  void  imgCanny(String path, String name, String imgBase64, Callback errorCallback, Callback successCallback) {
        try {
            Mat srcImg_origin = Imgcodecs.imread(path,Imgcodecs.IMREAD_UNCHANGED);
            Mat srcImg = Imgcodecs.imread(path,Imgcodecs.IMREAD_UNCHANGED);
            Mat destImg = new Mat();
            double diff = 50.0;//填充度
            org.opencv.core.Point seedPoint = new org.opencv.core.Point(30, 35);//种子
            Mat mask = new Mat(srcImg_origin.height() + 2 , srcImg_origin.width() + 2, CV_8UC1);//遮罩比源图像宽高均大两个像素，8UC1格式
            Imgproc.floodFill(srcImg_origin, mask,seedPoint, new Scalar(.0, .0, .0, .0), new org.opencv.core.Rect(), new Scalar(diff), new Scalar(diff),
                    4 | Imgproc.FLOODFILL_MASK_ONLY | (255 << 8));


            org.opencv.core.Rect roi = new org.opencv.core.Rect(1, 1, srcImg_origin.width(), srcImg_origin.height());//roi
            mask = mask.submat(roi);//剪裁成源图像大小
            srcImg = srcImg.setTo(new Scalar(.0, .0, .0, .0),mask);

            Imgproc.Canny(srcImg, destImg, 10, 100); //边缘检测

            String newPath = path.replace(name, name.replace(".jpg","_Con.jpg"));
            Imgcodecs.imwrite(newPath, destImg);

            String result = returnBase64Img(destImg, imgBase64);

            successCallback.invoke("success",result, newPath);
        } catch (Exception e) {
            Log.e("jdlogE",e.getMessage());
            errorCallback.invoke(e.getMessage());
        }
    }


    @ReactMethod
    public  void  markContour(String path, String name, String imgBase64, Callback errorCallback, Callback successCallback) {
        try {

            Mat srcImg = Imgcodecs.imread(path);
            Mat destImg = new Mat();

            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();

            Imgproc.cvtColor(srcImg, destImg, Imgproc.COLOR_RGB2GRAY, 1); //转为单通道图像
            Imgproc.findContours(destImg, contours, hierarchy,Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_NONE);//寻找轮廓


            for(int i=0;i<contours.size();i++){
                //绘制轮廓
                Imgproc.drawContours(srcImg, contours, i, new Scalar(Math.random()*255,Math.random()*255,Math.random()*255),2);
            }


            String newPath = path.replace(name, name.replace(".jpg","_Mark.jpg"));
            Imgcodecs.imwrite(newPath, srcImg);


            String result = returnBase64Img(srcImg, imgBase64);

            successCallback.invoke("success",result, path);

        } catch (Exception e) {
            Log.e("jdlogE",e.getMessage());
            errorCallback.invoke(e.getMessage());
        }
    }

    private String returnBase64Img(Mat destImg,String imgBase64) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        byte[] decodedString = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
            decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
        }
        Bitmap bitImg = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        Utils.matToBitmap(destImg, bitImg);

        ByteArrayOutputStream bos=new ByteArrayOutputStream();

        bitImg.compress(Bitmap.CompressFormat.JPEG, 90, bos);//将bitmap放入字节数组流中


        try {
            bos.flush();//将bos流缓存在内存中的数据全部输出，清空缓存
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



        byte []bitmapByte=bos.toByteArray();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
            String result=Base64.encodeToString(bitmapByte, Base64.DEFAULT);
            return result;
        }
        return new String("error");
    }

    @ReactMethod
    public  void  DropletAnalysis(String path, String name, String imgBase64, Callback errorCallback, Callback successCallback) {
        try {

            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();

            Mat srcImg = Imgcodecs.imread(path,Imgcodecs.IMREAD_UNCHANGED);
            Mat destImg = new Mat();

            Imgproc.cvtColor(srcImg, destImg, Imgproc.COLOR_RGB2GRAY, 1);
            Imgproc.findContours(destImg, contours, hierarchy,Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_TC89_L1);


            double sq1 = 0;
            double D = 0;
            double sum_D = 0;
            int j = 0;


                for (Mat m: contours) {
                    int i = 0;
                     double temp = Imgproc.contourArea(m);
                     sq1 = sq1 + temp;
                     D = 2*Math.sqrt(temp/Math.PI);
                     sum_D = sum_D + D;
                     i++;
                }
            double avg_D = sum_D / contours.size();


            successCallback.invoke(contours.size(),sq1,avg_D,srcImg.width(),srcImg.height());

        } catch (Exception e) {
            Log.e("jdlogE",e.getMessage());
            errorCallback.invoke(e.getMessage());
        }
    }



}

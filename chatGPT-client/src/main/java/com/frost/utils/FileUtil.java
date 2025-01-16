package com.frost.utils;
import com.madgag.gif.fmsware.AnimatedGifEncoder;
import com.madgag.gif.fmsware.GifDecoder;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by ${Frost-YAN} on 2024/2/18
 */
public class FileUtil {

    /**
     * 把InputStream输出为文件
     * @param inputStream
     * @param filePath
     */
    public static void conver(InputStream inputStream,String filePath){
        try {
            File file = new File(filePath);
            FileOutputStream outputStream = new FileOutputStream(file);
            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = inputStream.read(buffer)) != -1){
                outputStream.write(buffer,0,bytesRead);
            }
            outputStream.close();
            inputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 读取流
     *
     * @param inStream
     * @return 字节数组
     * @throws IOException
     */
    public static byte[] readStream(InputStream inStream) throws IOException {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        return outSteam.toByteArray();
    }


    /**
     * 按尺寸比例压缩gif到1MB以下
     * @param sourcePath
     * @return
     */
    public static String compressGif(String sourcePath){
        File source = new File(sourcePath);
        long fileSzie = source.length() / 1024;
        //判断是否需要压缩
        if (fileSzie < 1024){
            return sourcePath;
        }

        String newFilePath = sourcePath.replace(".gif","_compress");
        int count = 1;
        while (fileSzie >= 1024){
            try {
                //文件尺寸
                BufferedImage image = ImageIO.read(source);
                int width = image.getWidth();
                int height = image.getHeight();
                if (count == 1){
                    width = width/2;
                    height = height/2;
                }else {
                    width = width * 4/5;
                    height = height * 4/5;
                }
                //zoomGifByQuality(imgPath,"gif",0.3F,newFilePath);
                //按尺寸比例压缩
                newFilePath = newFilePath+count;
                zoomGifBySize(sourcePath,"gif",width,height,newFilePath);

                //新文件
                sourcePath = newFilePath+".gif";
                source = new File(sourcePath);
                fileSzie = source.length() / 1024;
                count++;

            } catch (IOException e) {
                return null;
            }
        }
        return newFilePath+".gif";
    }


    /**
     * 质量压缩
     * @param bimg
     * @param quality
     * @param tagFilePath
     * @return
     */
    public static File zoomImg2File(BufferedImage bimg, float quality, String tagFilePath){
        try {
            Thumbnails.of(bimg).scale(1f).outputQuality(quality).toFile(tagFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File newFile = new File(tagFilePath);
        System.out.println("new file size====>"+getPrintSize(newFile.length()));
        return newFile;
    }


    /**
     * GIF压缩质量，尺寸不变
     * @param imagePath 原图片路径地址，如：F:\\a.png
     * @param imgStyle 目标文件类型
     * @param quality 输出的图片质量，范围：0.0~1.0，1为最高质量。
     * @param outputPath 输出文件路径（不带后缀），如：F:\\b，默认与原图片路径相同，为空时将会替代原文件
     * @throws IOException
     */
    public static void zoomGifByQuality(String imagePath, String imgStyle, float quality, String outputPath) throws IOException {
        // 防止图片后缀与图片本身类型不一致的情况
        outputPath = outputPath + "." + imgStyle;
        // GIF需要特殊处理
        GifDecoder decoder = new GifDecoder();
        int status = decoder.read(imagePath);
        if (status != GifDecoder.STATUS_OK) {
            throw new IOException("read image " + imagePath + " error!");
        }
        // 拆分一帧一帧的压缩之后合成
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.start(outputPath);// 设置合成位置
        //encoder.setRepeat(decoder.getLoopCount());// 设置GIF重复次数
        encoder.setRepeat(0);// 设置GIF重复次数
        encoder.setQuality(1);//质量1~10，从低到高
        encoder.setFrameRate(200);//针率，内部会以100作为除数，这里是200，实际=100/200
        int frameCount = decoder.getFrameCount();// 获取GIF有多少个frame
        for (int i = 0; i < frameCount; i++) {
            encoder.setDelay(decoder.getDelay(i));// 设置GIF延迟时间
            BufferedImage bufferedImage = decoder.getFrame(i);
            // 利用java SDK压缩BufferedImage
            //byte[] tempByte = zoomBufferedImageByQuality(bufferedImage, quality);
            //ByteArrayInputStream in = new ByteArrayInputStream(tempByte);
            //BufferedImage zoomImage = ImageIO.read(in);
            File file = zoomImg2File(bufferedImage, quality,outputPath+i+".jpg");
            BufferedImage zoomImage = ImageIO.read(file);
            encoder.addFrame(zoomImage);// 合成
        }
        encoder.finish();
        File outFile = new File(outputPath);
        BufferedImage image = ImageIO.read(outFile);
        ImageIO.write(image, outFile.getName(), outFile);
    }



    /**
     * GIF压缩尺寸大小
     * @param imagePath 原图片路径地址，如：F:\\a.png
     * @param imgStyle 目标文件类型
     * @param width 目标文件宽
     * @param height 目标文件高
     * @param outputPath 输出文件路径（不带后缀），如：F:\\b，默认与原图片路径相同，为空时将会替代原文件
     * @throws IOException
     */
    public static void zoomGifBySize(String imagePath, String imgStyle, int width, int height, String outputPath) throws IOException {
        // 防止图片后缀与图片本身类型不一致的情况
        outputPath = outputPath + "." + imgStyle;
        // GIF需要特殊处理
        GifDecoder decoder = new GifDecoder();
        int status = decoder.read(imagePath);
        if (status != GifDecoder.STATUS_OK) {
            throw new IOException("read image " + imagePath + " error!");
        }
        // 拆分一帧一帧的压缩之后合成
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.start(outputPath);
        encoder.setRepeat(decoder.getLoopCount());
        for (int i = 0; i < decoder.getFrameCount(); i++) {
            encoder.setDelay(decoder.getDelay(i));// 设置播放延迟时间
            BufferedImage bufferedImage = decoder.getFrame(i);// 获取每帧BufferedImage流
            BufferedImage zoomImage = new BufferedImage(width, height, bufferedImage.getType());
            Image image = bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            Graphics gc = zoomImage.getGraphics();
            gc.setColor(Color.WHITE);
            gc.drawImage(image, 0, 0, null);
            encoder.addFrame(zoomImage);
        }
        encoder.finish();
        File outFile = new File(outputPath);
        BufferedImage image = ImageIO.read(outFile);
        ImageIO.write(image, outFile.getName(), outFile);
    }


    /**
     * 获取文件大小
     *
     * @param size
     * @return
     */
    public static String getPrintSize(long size) {
        // 如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (size < 1024) {
            return String.valueOf(size) + "B";
        } else {
            size = size / 1024;
        }
        // 如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        // 因为还没有到达要使用另一个单位的时候
        // 接下去以此类推
        if (size < 1024) {
            return String.valueOf(size) + "KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            // 因为如果以MB为单位的话，要保留最后1位小数，
            // 因此，把此数乘以100之后再取余
            size = size * 100;
            return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "MB";
        } else {
            // 否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "GB";
        }
    }

}
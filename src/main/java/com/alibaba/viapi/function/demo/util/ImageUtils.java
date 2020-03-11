package com.alibaba.viapi.function.demo.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;

import com.alibaba.viapi.function.demo.object.ImageEdge;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author benxiang.hhq
 */
public class ImageUtils {

    /**
     * 解析图片格式
     * @param stream
     * @return
     */
    public static String parseImageFormat(InputStream stream) {
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(stream);

            Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);
            while (imageReaders.hasNext()) {
                ImageReader reader = imageReaders.next();
                return reader.getFormatName();
            }
        } catch (IOException e) {
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return null;
    }

    public static String cutoutPicture(String imageOssUrl, ImageEdge imageEdge, OSSClient ossClient) throws IOException {

        BufferedImage originImg = null;
        BufferedImage bodyImg = null;
        try {
            originImg = readImage(imageOssUrl, ossClient);
            if (originImg == null) {
                throw new RuntimeException("failed to read image, url=" + imageOssUrl);
            }

            bodyImg = new BufferedImage(imageEdge.getMainBodyWidth(), imageEdge.getMainBodyHeight(),
                                        BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bodyImg.createGraphics();
            g2d.drawImage(originImg, -imageEdge.getMainBodyLeftX(), -imageEdge.getMainBodyLeftY(), null);
            g2d.dispose();
            return saveToOss(bodyImg, ossClient, imageEdge.getCropOSSUrl());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (null != bodyImg) {
                bodyImg.flush();
            }
            if (null != originImg) {
                originImg.flush();
            }
        }

    }

    private static String saveToOss(BufferedImage img, OSSClient ossClient, String targetPictureOssUrl) {
        ByteArrayOutputStream os = null;
        InputStream is = null;
        try {
            os = new ByteArrayOutputStream();
            ImageIO.write(img, "PNG", os);
            is = new ByteArrayInputStream(os.toByteArray());

            ObjectMetadata objectMetaData = new ObjectMetadata();
            objectMetaData.setContentType("image/png");
            OSSUtils.put(ossClient, targetPictureOssUrl, is, objectMetaData);
            return targetPictureOssUrl;
        } catch (Exception e) {
            return null;
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
            img.flush();
        }
    }

    /**
     * 读取图片信息
     *
     * @param ossPath
     * @return
     * @throws IOException
     */
    public static BufferedImage readImage(String ossPath, OSSClient ossClient) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = OSSUtils.getInputStreamWithStyle(ossClient, ossPath, null);
            String format = parseImageFormat(inputStream);
            boolean isPng = StringUtils.equalsIgnoreCase(format, "PNG");

            if (isPng) {
                return ImageIO.read(inputStream);
            } else {
                String url = OSSUtils.generatePresignedUrl(ossClient, ossPath, null);
                url = url.replace("https", "http");
                Image image = Toolkit.getDefaultToolkit().getImage(new URL(url));
                if (image != null) {
                    return toBufferedImage(image);
                }

            }
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return null;
    }


    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }
        image = new ImageIcon(image).getImage();

        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
            // log.error("toBufferedImage error", e);
        }

        if (bimage == null) {
            int type = BufferedImage.TYPE_INT_RGB;
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }

        Graphics g = bimage.createGraphics();

        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }


}

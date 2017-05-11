package imageHandles;

/**
 * Created by Zechen on 2016/6/30.
 */

import com.sun.org.apache.xml.internal.security.utils.Base64;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public final class ImageUtil {


    public static ByteArrayOutputStream crop(ByteArrayInputStream bais, int width, int height) throws IOException {
        BufferedImage src = ImageIO.read(bais);
        BufferedImage clipping = crop(src, width, height);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(clipping, "JPG", baos);
        return baos;
    }

    public static BufferedImage crop(BufferedImage src, int width, int height) throws IOException {
        int x = src.getWidth() / 2 - width / 2;
        int y = src.getHeight() / 2 - height / 2;

//        System.out.println("---" + imageHandles.getWidth() + " - " + imageHandles.getHeight() + " - " + x + " - " + y);

        /*BufferedImage clipping = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);//imageHandles.getType());
        Graphics2D area = (Graphics2D) clipping.getGraphics().create();
        area.drawImage(imageHandles, 0, 0, clipping.getWidth(), clipping.getHeight(), x, y, x + clipping.getWidth(),
                y + clipping.getHeight(), null);
        area.dispose();*/
        BufferedImage clipping = src.getSubimage(10, 10, 900, 1200);
        return clipping;
    }

    public static ByteArrayOutputStream smartCrop(ByteArrayInputStream bais, int width, int height) throws IOException {
        BufferedImage src = ImageIO.read(bais);

        Float scale;
        if (src.getWidth() > src.getHeight()) {
            scale = Float.valueOf(height) / Float.valueOf(src.getHeight());
            if (src.getWidth() * scale < width) {
                scale = Float.valueOf(width) / Float.valueOf(src.getWidth());
            }
        } else {
            scale = Float.valueOf(width) / Float.valueOf(src.getWidth());
            if (src.getHeight() * scale < height) {
                scale = Float.valueOf(height) / Float.valueOf(src.getHeight());
            }
        }

        System.out.println("--- " + src.getWidth() + " - " + width);
        System.out.println("--- " + src.getHeight() + " - " + height);
        System.out.println("--- " + scale + " -- " + Float.valueOf(src.getWidth() * scale).intValue() + " -- " + Float.valueOf(src.getHeight() * scale).intValue());

        BufferedImage temp = scale(src, Float.valueOf(src.getWidth() * scale).intValue(),
                Float.valueOf(src.getHeight() * scale).intValue());

        temp = crop(temp, width, height);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(temp, "JPG", baos);

        return baos;
    }

    public static ByteArrayOutputStream scale(ByteArrayInputStream bais, int width, int height) throws IOException {
        BufferedImage src = ImageIO.read(bais);
        BufferedImage dest = scale(src, width, height);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(dest, "JPG", baos);
        return baos;
    }

    public static BufferedImage scale(BufferedImage src, int width, int height) throws IOException {
        BufferedImage dest = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = dest.createGraphics();
        AffineTransform at = AffineTransform.getScaleInstance(
                (double) width / src.getWidth(),
                (double) height / src.getHeight());
        g.drawRenderedImage(src, at);
        return dest;
    }

    public static BufferedImage rotateImage(BufferedImage src, int angle) {
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = src.getWidth();
        int h = src.getHeight();
        int neww = (int) Math.floor(w * cos + h * sin);
        int newh = (int) Math.floor(h * cos + w * sin);
        if (angle == 90 || angle == -90 || angle == 270 || angle == -270) {
            neww = src.getHeight();
            newh = src.getWidth();
        }
        if (angle > 360) {
            angle = 360 - angle;
        }

        //using Sin


        int diameter = (int) Math.round(Math.sqrt(Math.pow(src.getHeight(), 2) + Math.pow(src.getWidth(), 2)));
        BufferedImage result = new BufferedImage(newh, neww, src.getType());
        Graphics2D graphics2D = result.createGraphics();
        int centerY = src.getHeight() / 2, centerX = src.getWidth() / 2;

        graphics2D.translate((neww - src.getWidth()) / 2, (newh - src.getHeight()) / 2);
        graphics2D.rotate(Math.toRadians(angle), centerX, centerY);
        graphics2D.translate((neww - src.getWidth()) / 2, (newh - src.getHeight()) / 2);
        graphics2D.drawRenderedImage(src, null);
        graphics2D.dispose();

        System.out.println("CenterX : " + centerX + " Height : " + src.getHeight());
        System.out.println("CenterY : " + centerY + " Width : " + src.getWidth() + " ---- Radians: " + Math.toRadians(angle) + " angle : " + angle);
        System.out.println("Type : " + src.getType());
        return result;
    }

    public static BufferedImage rotateAffineTrans(BufferedImage src, int angle) {
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = src.getWidth();
        int h = src.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage newImage = new BufferedImage(newWidth, newHeight, src.getType());
        Graphics2D g2 = newImage.createGraphics();
        AffineTransform tx = new AffineTransform();
        tx.translate((newWidth - w) / 2, (newHeight - h) / 2);

        tx.rotate(Math.toRadians(angle), w / 2, h / 2);
        g2.setTransform(tx);
        g2.drawImage(src, 0, 0, null);
        g2.setColor(Color.WHITE);
        g2.dispose();


        return newImage;
    }


    public static void main(String args[]) {

        File image = new File("G:/sand.jpeg");
        BufferedImage bufferedImage = null;
        BufferedImage rotatesin = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            bufferedImage = ImageUtil.rotateImage(ImageIO.read(image), -45);
            rotatesin = ImageUtil.rotateAffineTrans(ImageIO.read(image), -45);
            ImageIO.write(rotatesin, "jpeg", byteArrayOutputStream);//todo image type is just jpeg
            byte[] ba = byteArrayOutputStream.toByteArray();
            System.out.println("byteArrayOutputStream : " + ba.length);
            System.out.println("Size : " + rotatesin.getRaster().getDataBuffer().getSize());
            DataBufferByte data = (DataBufferByte) rotatesin.getRaster().getDataBuffer();
//            for (byte b : data.getData()) {
//                System.out.println("Bytes : " + b);
//            }

            byte[] res;
            ImageIO.write(rotatesin, "jpeg", baos);
            res = baos.toByteArray();
            String encodedImage = Base64.encode(baos.toByteArray());
            System.out.println("The encoded image byte array is shown below.Please use this in your webpage image tag.\n" + encodedImage + "\n" + res.length);


        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            /*assert bufferedImage != null;
            bufferedImage.writeTo(new FileOutputStream(new File("G:/sandcrop2.jpg")));*/
            assert bufferedImage != null;
            ImageIO.write(bufferedImage, "jpeg", new File("G:/sandcrop3.jpeg"));
            assert rotatesin != null;
            ImageIO.write(rotatesin, "jpeg", new File("G:/sandcropAffine.jpeg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


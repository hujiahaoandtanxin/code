package com.word.utils;


import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PngUtils {

    public static void main(String[] args) throws IOException {
        try {
            System.out.println(EmfToPng("/Users/hujh/Downloads/EwB3RQxXv6.emf"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean checkFileTypeByName(String pathname, String destType) {
        String fileType = pathname.substring(pathname.lastIndexOf(".") + 1, pathname.length()).toLowerCase();
        return destType.equalsIgnoreCase(fileType);
    }

    public static String EmfToPng(String pathName) throws Exception {
        if (checkFileTypeByName(pathName, "emf")) {
            EMFInputStream in = null;
            try {
                in = new EMFInputStream(new FileInputStream(pathName), EMFInputStream.DEFAULT_VERSION);
                EMFRenderer emfRenderer = new EMFRenderer(in);
                final int width = (int) in.readHeader().getBounds().getWidth();
                final int height = (int) in.readHeader().getBounds().getHeight();
                final BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
                Graphics2D g2 = result.createGraphics();
                emfRenderer.paint(g2);
                String destFile = pathName.substring(0, pathName.toLowerCase().lastIndexOf(".emf")) + ".png";
                ImageIO.write(result, "png", new File(destFile));
                return destFile;
            } finally {
                if (null != in) {
                    in.close();
                }
            }
        } else {
            throw new RuntimeException("Not EMF file!");
        }
    }



}

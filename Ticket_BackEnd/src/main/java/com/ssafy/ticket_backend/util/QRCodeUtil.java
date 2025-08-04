package com.ssafy.ticket_backend.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Hashtable;
import javax.imageio.ImageIO;

public class QRCodeUtil {

    public static String generateQRCodeImage(String text, int width, int height, String logoPath)
        throws IOException, WriterException {
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hintMap);

        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // Load logo image
        BufferedImage logoImage = ImageIO.read(new File(logoPath));

        // Calculate the logo position
        int logoWidth = logoImage.getWidth();
        int logoHeight = logoImage.getHeight();
        int positionX = (width - logoWidth) / 2;
        int positionY = (height - logoHeight) / 2;

        // Add logo to the QR code
        Graphics2D graphics = (Graphics2D) qrImage.getGraphics();
        graphics.drawImage(logoImage, positionX, positionY, logoWidth, logoHeight, null);
        graphics.dispose();


        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "png", pngOutputStream);

        return Base64.getEncoder().encodeToString(pngOutputStream.toByteArray());
    }
}

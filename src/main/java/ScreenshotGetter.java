import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.annotation.WebServlet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.*;
import java.util.Iterator;

public class ScreenshotGetter {

    private static final int height = 3500;
    private static final int width = 1000;

    public static BufferedImage getScreenshot(String url, float quality) throws IOException, InterruptedException {

        ChromeOptions headlessOptions = new ChromeOptions();
        headlessOptions.addArguments("--headless");
        headlessOptions.addArguments("--disable-dev-shm-usage");
        headlessOptions.addArguments("--no-sandbox");
        headlessOptions.addArguments("window-size=" + width + "," + height);

        BufferedImage uncompressedImage;
        WebDriver driver = new ChromeDriver(headlessOptions);
        try {
            try (ByteArrayOutputStream arrayOutput = new ByteArrayOutputStream()) {
                do {
                    driver.get(url);
                    File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                    BufferedImage tmp = ImageIO.read(screenshot);
                    uncompressedImage = new BufferedImage(tmp.getWidth(),
                            tmp.getHeight(),
                            BufferedImage.TYPE_INT_RGB);
                    uncompressedImage.createGraphics().drawImage(tmp, 0, 0, Color.WHITE, null);


                    Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
                    ImageWriter writer = writers.next();
                    try (ImageOutputStream ios = ImageIO.createImageOutputStream(arrayOutput)) {
                        writer.setOutput(ios);
                        try {
                            ImageWriteParam param = writer.getDefaultWriteParam();

                            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                            param.setCompressionQuality(quality);  // Change the quality value you prefer
                            writer.write(null, new IIOImage(uncompressedImage, null, null), param);
                        } finally {
                            writer.dispose();
                        }
                    }
                } while (isWhite(uncompressedImage));

                arrayOutput.flush();
                try (InputStream resultInputStream = new ByteArrayInputStream(arrayOutput.toByteArray())) {
                    return ImageIO.read(resultInputStream);
                }
            }
        } finally {
            driver.quit();
        }
    }

    private static boolean isWhite(BufferedImage img) throws InterruptedException {
        int width = img.getWidth(null);
        int height = img.getHeight(null);
        int[] pixels = new int[width * height];
        PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, pixels, 0, width);
        pg.grabPixels();
        for (int pixel : pixels) {
            Color color = new Color(pixel);
            if (color.getRGB() != Color.WHITE.getRGB()) {
                return false;
            }
        }
        return true;
    }
}

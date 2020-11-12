import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.*;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class ScamBot {

    private static final int imageHeight = 3500;
    private static final int headerHeight = 25;
    private static final int imageWidth = 1000;

    public static synchronized BufferedImage getScreenshot(String url, float quality, int waitTime)
            throws IOException, InterruptedException {
        BufferedImage uncompressedImage;
        WebDriver driver = getHeadlessDriver();
        try {
            try (ByteArrayOutputStream arrayOutput = new ByteArrayOutputStream()) {
                do {
                    driver.get(url);

                    TimeUnit.MILLISECONDS.sleep(waitTime);

                    File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                    BufferedImage tmp = ImageIO.read(screenshot);
                    uncompressedImage = new BufferedImage(tmp.getWidth(),
                            tmp.getHeight() + headerHeight,
                            BufferedImage.TYPE_INT_RGB);

                    uncompressedImage.getGraphics().drawImage(tmp, 0, headerHeight, Color.WHITE, null);


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
                    BufferedImage image = ImageIO.read(resultInputStream);
                    Graphics2D graphics = image.createGraphics();
                    Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
                    graphics.setFont(font);
                    graphics.drawString(url,5, headerHeight - 5);
                    return image;
                }
            }
        } finally {
            driver.quit();
        }
    }

    public static String getHtml(String url) {
        ChromeDriver driver = getHeadlessDriver();
        try {
            driver.get(url);
            return driver.getPageSource();
        } finally {
            driver.quit();
        }
    }

    private static ChromeDriver getHeadlessDriver() {
        ChromeOptions headlessOptions = new ChromeOptions();
        headlessOptions.addArguments("--headless");
        headlessOptions.addArguments("--disable-dev-shm-usage");
        headlessOptions.addArguments("--no-sandbox");
        headlessOptions.addArguments("window-size=" + imageWidth + "," + imageHeight);

        return new ChromeDriver(headlessOptions);
    }

    private static boolean isWhite(BufferedImage img) throws InterruptedException {
        int width = imageWidth;
        int height = imageHeight;
        int[] pixels = new int[width * height];
        PixelGrabber pg = new PixelGrabber(img, 0, headerHeight, width, height, pixels, 0, width);
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

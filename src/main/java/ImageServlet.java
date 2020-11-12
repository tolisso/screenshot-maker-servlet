import org.apache.commons.codec.CharEncoding;

import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;

@WebServlet("/ImageServlet")
public class ImageServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        String url;
        int waitTime = 0;
        float quality = 0.3f;
        try {
            Object urlObject = request.getParameter("url");
            if (urlObject == null) {
                response.sendError(400, "no 'url' attribute found");
                return;
            }
            url = URLDecoder.decode((String) urlObject, CharEncoding.UTF_8);
        } catch (ClassCastException exc) {
            response.sendError(400, "'url' attribute must be a string!:\n" + exc);
            return;
        }
        Object qualityObject = request.getParameter("quality");
        if (qualityObject != null) {
            try {
                quality = Float.parseFloat(qualityObject.toString());
            } catch (NumberFormatException exc) {
                response.sendError(400, "'quality' attribute must be a float!:\n" + exc);
                return;
            }
        }
        Object waitTimeObject = request.getParameter("wait");
        if (waitTimeObject != null) {
            try {
                waitTime = Integer.parseInt(waitTimeObject.toString());
            } catch (NumberFormatException exc) {
                response.sendError(400, "'wait' attribute must be an int!:\n" + exc);
                return;
            }
        }

        try (OutputStream responseOutputStream = response.getOutputStream()) {
            try {
                ImageIO.write(ScamBot.getScreenshot(url, quality, waitTime), "jpg", responseOutputStream);
            } catch (InterruptedException exc) {
                response.sendError(500, "Unexpected interrupting of the servlet:\n" + exc);
            } catch (Exception exc) {
                response.sendError(500, "Internal error occurred:\n" + exc);
            }
        }
    }
}

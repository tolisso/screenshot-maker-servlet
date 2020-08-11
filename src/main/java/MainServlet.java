import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

@WebServlet("/MainServlet")
public class MainServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("image/jpeg");
        String url;
        float quality = 0.3f;
        try {
            Object urlObject = request.getParameter("url");
            if (urlObject == null) {
                response.sendError(400, "no 'url' attribute found");
                return;
            }
            url = (String) urlObject;
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

        try (OutputStream responseOutputStream = response.getOutputStream()) {
            try {
                ImageIO.write(ScreenshotGetter.getScreenshot(url, quality), "jpg", responseOutputStream);
            } catch (InterruptedException exc) {
                response.sendError(500, "Unexpected interrupting of the servlet:\n" + exc);
                return;
            } catch (Exception exc) {
                response.sendError(500, "Internal error occurred:\n" + exc);
            }
        }
    }
}

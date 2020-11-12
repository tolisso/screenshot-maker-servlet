import org.apache.commons.codec.CharEncoding;
import sun.misc.CharacterEncoder;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.net.URLEncoder;

@WebServlet("/HtmlServlet")
public class HtmlServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        String url;
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

        String encoding = request.getParameter("encoding");
        if (encoding == null) {
            encoding = CharEncoding.UTF_8;
        }
        response.setCharacterEncoding(encoding);

        try (Writer writer = response.getWriter()) {
            writer.write(ScamBot.getHtml(url));
        }
    }
}

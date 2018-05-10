import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.lightcouch.ChangesResult;
import org.lightcouch.CouchDbClient;
import org.lightcouch.Document;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "getSentData")
public class getSentData extends HttpServlet {
    CouchDbClient dbClient = new CouchDbClient("sentimentdb", true, "http", "localhost", 7778, "admin", "password");

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int queryLimit = 280;
        Gson gson = new Gson();
        String viewId = "sentiment/analysis";
        String option = request.getParameter("option");
        if (option != null) {
            if (option.equals("sent_total") | option.equals("sent_positive")) {
                queryLimit = 10;
            }
            if (option.equals("sent_positive")) {
                viewId = "sentiment/positive";
            }
        }
        System.out.println(queryLimit);
        List<Sentiment> docList = dbClient.view(viewId).limit(queryLimit)
                .includeDocs(true)
                .descending(true)
                .query(Sentiment.class);

        List<Sentiment> sentimentList = new ArrayList<>();
        for (Sentiment d : docList) {
            d.calculate_rate();
            sentimentList.add(d);
        }

        String jsonString = gson.toJson(sentimentList);
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = response.getWriter();
        try {
            out.write(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}

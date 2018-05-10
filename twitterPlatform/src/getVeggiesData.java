import com.google.gson.Gson;
import org.lightcouch.CouchDbClient;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "getVeggiesData")
public class getVeggiesData extends HttpServlet {
    CouchDbClient dbClient = new CouchDbClient("veggiesdb", true, "http", "localhost", 7778, "admin", "password");

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        int queryLimit=10;
        String option=request.getParameter("option");
        List <CountSuburb> countList=new ArrayList<>();
        if(option.equals("veggies_chart")) {
            dbClient = new CouchDbClient("veggiesdb", true, "http", "localhost", 7778, "admin", "password");
            countList = dbClient.view("veggies/count").limit(queryLimit)
                    .includeDocs(true)
                    .descending(true)
                    .query(CountSuburb.class);
            System.out.println("veggies");
        }
         else if (option.equals("beauty_chart")){
            dbClient=new CouchDbClient("beautydb", true, "http", "localhost", 7778, "admin", "password");
            countList = dbClient.view("beauty/count").limit(queryLimit)
                    .includeDocs(true)
                    .descending(true)
                    .query(CountSuburb.class);
        }
            Gson gson = new Gson();
            ChartRankData chartRankData = new ChartRankData();
            for (CountSuburb food : countList) {
                chartRankData.addLabel(food.get_id());
                chartRankData.addDataItem(food.getCount());
            }
            String jsonString = gson.toJson(chartRankData);

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

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        doPost(request, response);
    }
}

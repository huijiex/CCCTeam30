

import com.google.gson.Gson;
import org.lightcouch.CouchDbClient;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class getFoodData extends javax.servlet.http.HttpServlet {
    CouchDbClient dbClient = new CouchDbClient("fooddb", true, "http", "localhost", 7778, "admin", "password");

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        int queryLimit=280;
        Gson gson=new Gson();
        String option=request.getParameter("option");
        System.out.println(option);
        String jsonString="";
        if(option!=null){
            if (option.equals("food_chart")){
                queryLimit=10;
            }}
        System.out.println(queryLimit);
        List<CountSuburb> foodList =dbClient.view("foodcount/count").limit(queryLimit)
                .includeDocs(true)
                .descending(true)
                .query(CountSuburb.class);
        if(option!=null){
        ChartRankData chartRankData =new ChartRankData();
        for (CountSuburb food:foodList){
            chartRankData.addLabel(food.get_id());
            chartRankData.addDataItem(food.getCount());
        }
        jsonString=gson.toJson(chartRankData);
        }
        else{
            jsonString=gson.toJson(foodList);
        }
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

import com.google.gson.Gson;
import org.lightcouch.CouchDbClient;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "getFemaleData")
public class getFemaleData extends HttpServlet {
    CouchDbClient dbClient = new CouchDbClient("aurin_female_db", true, "http", "localhost", 7778, "admin", "password");
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int queryLimit=280;

        String option=request.getParameter("option");
        if(option!=null){
            if (option.equals("female_total")){
                queryLimit=10;
            }}

        List<FemaleData> docList =dbClient.view("aurin/female").limit(queryLimit)
                .includeDocs(true)
                .descending(true)
                .query(FemaleData.class);

        Gson gson=new Gson();
        String jsonString=gson.toJson(docList);
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

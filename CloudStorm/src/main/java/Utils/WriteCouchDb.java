package Utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.lightcouch.CouchDbClient;

import java.util.ArrayList;
import java.util.List;

public class WriteCouchDb {
    public static void main(String[] args){
        List<Person> list = new ArrayList<Person>();
        Person person1 = new Person("minjiez","444");
        list.add(person1);
        Person person2 = new Person("huijiex","222");
        list.add(person2);
        Person person3 = new Person("youqi","333");
        list.add(person3);

        CouchDbClient couchDbClient = new CouchDbClient("testChange.properties");
        JsonParser jsonParser = new JsonParser();
        Gson gson = new Gson();
        for(Person person:list){
            JsonObject p = jsonParser.parse(gson.toJson(person)).getAsJsonObject();
            couchDbClient.save(p);
        }



        //couchDbClient.save(p1);


    }
}

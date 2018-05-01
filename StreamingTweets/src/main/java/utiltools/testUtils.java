package utiltools;

import org.apache.commons.lang3.StringUtils;

import java.io.*;

public class testUtils {
    public static void main(String[] args) throws IOException {
        //testGeoCoding();
        //GeoCoder geoCoding = new GeoCoder();
        double[] coordinates = {151.20676517,-33.87722111};

        System.out.println(Supply.inBoundingBox(coordinates));
        //System.out.println(geoCoding.getSuburbByGeometry(coordinates));
    }

    private static void testGeoCoding() throws IOException {
        FileReader fileReader = new FileReader(new File("E://cloud_data/test.json"));
        GeoCoder geoCoding = new GeoCoder();

        BufferedReader br = new BufferedReader(fileReader);
        String line = "";
        while (StringUtils.isNotBlank(line=br.readLine())){

            //geoCoding.getSuburbByGeometry()
        }
    }
}

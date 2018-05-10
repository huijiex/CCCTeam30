import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;

public class DataSet {
    private List<Integer> data=new ArrayList();

    public List<Integer> getData() {
        return data;
    }

    public void setData(List<Integer> data) {
        this.data = data;
    }

    public void addDataItem(Integer item){
        data.add(item);
    }
}

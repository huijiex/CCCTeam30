import java.util.ArrayList;
import java.util.List;

public class ChartRankData {
    private List<String> labels=new ArrayList<>();
    private List<DataSet> datasets=new ArrayList<>();

    public ChartRankData(){
        DataSet d=new DataSet();
        datasets.add(d);
    }
    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<DataSet> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<DataSet> datasets) {
        this.datasets = datasets;
    }

    public void addLabel(String label){
        labels.add(label);
    }

    public void addDataItem(Integer item){
        datasets.get(0).addDataItem(item);
    }
}

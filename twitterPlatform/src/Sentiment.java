public class Sentiment {
    private String _id="";
    private int negative_number=0;
    private int positive_number=0;
    private int total_number=0;
    private float positive_rate=0;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getNegative_number() {
        return negative_number;
    }

    public void setNegative_number(int negative_number) {
        this.negative_number = negative_number;
    }

    public int getPositive_number() {
        return positive_number;
    }

    public void setPositive_number(int positive_number) {
        this.positive_number = positive_number;
    }

    public int getTotal_number() {
        return total_number;
    }

    public void setTotal_number(int total_number) {
        this.total_number = total_number;
    }

    public void calculate_rate(){
        positive_rate=(float)positive_number/(float)total_number;
    }
}

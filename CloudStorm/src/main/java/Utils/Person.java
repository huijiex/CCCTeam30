package Utils;

public class Person {
    String name;
    String _id;

    public Person(String name, String _id) {
        this.name = name;
        this._id = _id;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", _id='" + _id + '\'' +
                '}';
    }
}

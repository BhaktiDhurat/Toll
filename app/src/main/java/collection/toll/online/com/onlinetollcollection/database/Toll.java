package collection.toll.online.com.onlinetollcollection.database;

/**
 * Created by opulent on 12/1/17.
 */

public class Toll {

    String name,id,status,amount,tollId,date;

    public Toll(String name,String id, String status, String amount, String tollId,String date) {
        this.name=name;
        this.id = id;
        this.status = status;
        this.amount = amount;
        this.tollId = tollId;
        this.date = date;
    }
    public Toll(){

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTollId() {
        return tollId;
    }

    public void setTollId(String tollId) {
        this.tollId = tollId;
    }
}

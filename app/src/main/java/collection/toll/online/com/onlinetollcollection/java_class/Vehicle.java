package collection.toll.online.com.onlinetollcollection.java_class;

/**
 * Created by opulent on 11/1/17.
 */

public class Vehicle {

    // {"vehicle Type":"4 Wheeler","userVehicle":"bmw","vehicle id":"2"}
    String vehicleType, userVehicle, vehicleId;

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleType='" + vehicleType + '\'' +
                ", userVehicle='" + userVehicle + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                '}';
    }

    public Vehicle(String vehicleType, String userVehicle, String vehicleId) {
        this.vehicleType = vehicleType;
        this.userVehicle = userVehicle;
        this.vehicleId = vehicleId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getUserVehicle() {
        return userVehicle;
    }

    public void setUserVehicle(String userVehicle) {
        this.userVehicle = userVehicle;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }
}

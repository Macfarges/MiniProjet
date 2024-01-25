package helloandroid.ut3.miniprojet.data.domain;

public class Restaurant {
    private String id;
    private String title;
    private String openingHours;
    private String closingTime;
    private String shortDesc;
    private String lat;
    private String lon;
    private String zone;
    private String infos;
    private String contact;

    private String crousAndGoSrc;

    // Constructor
    public Restaurant(String id, String title, String openingHours, String closingTime,
                      String shortDesc, String lat, String lon, String zone,
                      String infos, String contact, String crousAndGoSrc) {
        this.id = id;
        this.title = title;
        this.openingHours = openingHours;
        this.closingTime = closingTime;
        this.shortDesc = shortDesc;
        this.lat = lat;
        this.lon = lon;
        this.zone = zone;
        this.infos = infos;
        this.contact = contact;
        this.crousAndGoSrc = crousAndGoSrc;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getZone() {
        return zone;
    }

    public String getInfos() {
        return infos;
    }

    public String getContact() {
        return contact;
    }

    public String getCrousAndGoSrc() {
        return crousAndGoSrc;
    }

    // Override toString for easy printing
    @Override
    public String toString() {
        return "Restaurant{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", openingHours='" + openingHours + '\'' +
                ", closingTime='" + closingTime + '\'' +
                ", shortDesc='" + shortDesc + '\'' +
                ", lat='" + lat + '\'' +
                ", lon='" + lon + '\'' +
                ", zone='" + zone + '\'' +
                ", infos='" + infos + '\'' +
                ", contact='" + contact + '\'' +
                ", crousAndGoSrc='" + crousAndGoSrc + '\'' +
                '}';
    }
}

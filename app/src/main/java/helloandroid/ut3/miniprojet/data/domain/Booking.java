package helloandroid.ut3.miniprojet.data.domain;

import java.util.Date;
import java.util.UUID;

public class Booking {
    private String id;

    private String restaurantName;
    private Date date;

    private boolean isMidi;

    private int nbPersonnes;

    protected Booking() {
    }


    public Booking(Date date, boolean isMidi, int nbPersonnes, String restaurantName) {
        if (nbPersonnes < 1 || nbPersonnes > 10) {
            throw new IllegalArgumentException("Number of person must be between 1 and 10");
        }
        this.id = UUID.randomUUID().toString();
        this.date = date;
        this.isMidi = isMidi;
        this.nbPersonnes = nbPersonnes;
        this.restaurantName = restaurantName;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public boolean isMidi() {
        return isMidi;
    }

    public int getNbPersonnes() {
        return nbPersonnes;
    }
}

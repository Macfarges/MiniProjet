package helloandroid.ut3.miniprojet.data.domain;

import java.util.Date;
import java.util.UUID;

public class Booking {
    private String id;
    private String reservationName;

    private String restaurantName;
    private Date date;

    private boolean isMidi;

    private int nbPersons;

    protected Booking() {
        // Default constructor required for calls to DataSnapshot.getValue(Booking.class)
    }


    public Booking(Date date, boolean isMidi, int nbPersons, String restaurantName, String reservationName) {
        if (nbPersons < 1 || nbPersons > 10) {
            throw new IllegalArgumentException("Number of person must be between 1 and 10");
        }
        this.id = UUID.randomUUID().toString();
        this.date = date;
        this.isMidi = isMidi;
        this.nbPersons = nbPersons;
        this.restaurantName = restaurantName;
        this.reservationName = reservationName;
    }

    public String getReservationName() {
        return reservationName;
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

    public int getNbPersons() {
        return nbPersons;
    }
}

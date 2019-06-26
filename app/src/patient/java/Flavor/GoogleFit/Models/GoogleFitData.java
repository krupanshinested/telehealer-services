package Flavor.GoogleFit.Models;

import java.io.Serializable;
import java.util.Date;

public class GoogleFitData implements Serializable {
    private GoogleFitSource source;
    private String type;
    private String value;
    private Date date;

    public GoogleFitData(GoogleFitSource source, String type, String value, Date date) {
        this.source = source;
        this.type = type;
        this.value = value;
        this.date = date;
    }

    public GoogleFitSource getSource() {
        return source;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public Date getDate() {
        return date;
    }
}

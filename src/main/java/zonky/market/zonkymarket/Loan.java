package zonky.market.zonkymarket;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Loan {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("YYY-MM-dd HH:MM:ss");

    private final String url;
    private final String name;
    private final ZonedDateTime datePublished;

    @JsonCreator
    Loan(@JsonProperty("url") String url,
         @JsonProperty("name") String name,
         @JsonProperty("datePublished") ZonedDateTime datePublished) {
        this.url = url;
        this.name = name;
        this.datePublished = datePublished;
    }

    String formatForPublish() {
        return String.format("\t- %s\n\t  %s\n\t  %s", name,
                datePublished.format(DATE_TIME_FORMATTER), url);
    }

    ZonedDateTime getDatePublished() {
        return datePublished;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Loan)) return false;
        Loan loan = (Loan) o;
        return Objects.equals(url, loan.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }

    @Override
    public String toString() {
        return "Loan{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", datePublished=" + datePublished +
                '}';
    }
}

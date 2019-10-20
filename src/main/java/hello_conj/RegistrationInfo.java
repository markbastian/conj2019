package hello_conj;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

public class RegistrationInfo {
    public RegistrationInfo(String firstName, String lastName, String conferenceName,
                            Dates dates, Collection<String> classes) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.conferenceName = conferenceName;
        this.dates = dates;
        this.classes = classes;
    }

    static class Dates {
        private final Date startDate;
        private final Date endDate;

        Dates(Date startDate, Date endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    private final String firstName;
    private final String lastName;
    private final String conferenceName;
    private final Dates dates;
    private final Collection<String> classes;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getConferenceName() {
        return conferenceName;
    }

    public Dates getDates() {
        return dates;
    }

    public Collection<String> getClasses() {
        return classes;
    }

    public static void main(String[] args) {
        Collection<String> classes = new LinkedList<String>();

        RegistrationInfo info = new RegistrationInfo("Mark",
                "Bastian",
                "Clojure/conj 2019",
                new Dates(new Date(2019, 11, 21), new Date(2019, 11, 23)),
                classes);

        System.out.println(info);
    }
}

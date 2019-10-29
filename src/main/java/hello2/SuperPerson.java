package hello2;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Arrays;

@Entity
public class SuperPerson {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    private String[] powers;

    protected SuperPerson() {}

    public SuperPerson(String firstName, String lastName, String[] powers) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.powers = powers;
    }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%d, firstName='%s', lastName='%s', powers='%s']",
                id, firstName, lastName, Arrays.toString(powers));
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String[] getPowers() {
        return powers;
    }
}
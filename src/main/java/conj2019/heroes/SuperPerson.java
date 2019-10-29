package conj2019.heroes;

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
    private String name;
    private String universe;
    private String[] powers;

    protected SuperPerson() {}

    public SuperPerson(String name, String universe, String[] powers) {
        this.name = name;
        this.universe = universe;
        this.powers = powers;
    }

    @Override
    public String toString() {
        return String.format(
                "SuperPerson[id=%d, name='%s', universe='%s', powers='%s']",
                id, name, universe, Arrays.toString(powers));
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUniverse() {
        return universe;
    }

    public String[] getPowers() {
        return powers;
    }
}
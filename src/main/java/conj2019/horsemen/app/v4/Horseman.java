package conj2019.horsemen.app.v4;

import org.jetbrains.annotations.Contract;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.Objects;

@Entity
public class Horseman {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private final String name;
    private final String[] weapons;

    @Contract(pure = true)
    public Horseman(String name, String[] weapons) {
        this.name = name;
        this.weapons = weapons;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Horseman horseman = (Horseman) o;
        return name.equals(horseman.name) &&
                Arrays.equals(weapons, horseman.weapons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, weapons);
    }

    @Override
    public String toString() {
        return "Horseman{" +
                "name='" + name + '\'' +
                ", weapons=" + Arrays.toString(weapons) +
                '}';
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String[] getWeapons() {
        return weapons;
    }
}
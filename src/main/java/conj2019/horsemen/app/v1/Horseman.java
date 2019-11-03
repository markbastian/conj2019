package conj2019.horsemen.app.v1;

import org.jetbrains.annotations.Contract;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Collection;
import java.util.Objects;

@Entity
public class Horseman {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private final String name;
    private final Collection<String> weapons;

    @Contract(pure = true)
    public Horseman(String name, Collection<String> weapons) {
        this.name = name;
        this.weapons = weapons;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Horseman horseman = (Horseman) o;
        return name.equals(horseman.name) &&
                weapons.equals(horseman.weapons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, weapons);
    }

    @Override
    public String toString() {
        return "Horseman{" +
                "name='" + name + '\'' +
                ", weapons=" + weapons +
                '}';
    }
}
package conj2019.horsemen.app.v0;

import org.jetbrains.annotations.Contract;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Horseman {
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

    public static void main(String[] args) {
        List<Horseman> horsemen =
                List.of(new Horseman("Famine", List.of("Scales")),
                        new Horseman("Pestilence", List.of("Bow", "Arrow")),
                        new Horseman("War", List.of("Sword")),
                        new Horseman("Death", Collections.emptyList()));

        System.out.println(horsemen);
    }
}
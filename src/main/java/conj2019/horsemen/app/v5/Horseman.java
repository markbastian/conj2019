package conj2019.horsemen.app.v5;

//import org.jetbrains.annotations.Contract;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Arrays;

@Entity
public class Horseman {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String[] weapons;

    //@Contract(pure = true)
    public Horseman(String name, String[] weapons) {
        this.name = name;
        this.weapons = weapons;
    }

    protected Horseman() {
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Horseman horseman = (Horseman) o;
//        return name.equals(horseman.name) &&
//                Arrays.equals(weapons, horseman.weapons);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(name, weapons);
//    }

//    @Override
//    public String toString() {
//        return "Horseman{" +
//                "name='" + name + '\'' +
//                ", weapons=" + Arrays.toString(weapons) +
//                '}';
//    }


    @Override
    public String toString() {
        return "Horseman{" +
                "id=" + id +
                ", name='" + name + '\'' +
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
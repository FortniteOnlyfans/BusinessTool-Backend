package dev.gr1.db.bind;

import dev.gr1.db.orm.Id;
import dev.gr1.db.orm.Table;
import dev.gr1.proj.GeldType;

@Table(name = "Geld")
public class Geld {

    @Id
    public int ID;
    public String target;
    public int targetID;
    public String Name;
    public float Wert;
    public Float Zinsen;
    public Integer Laufzeit;

    public boolean isTarget(GeldType geldType) {
        return geldType.name().equals(target);
    }
}

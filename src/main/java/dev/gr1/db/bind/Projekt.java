package dev.gr1.db.bind;

import dev.gr1.db.orm.Id;
import dev.gr1.db.orm.Table;

import java.util.Date;

@Table(name = "Projekt")
public class Projekt {
    @Id
    public int ID;
    public int userID;
    public int startKostenID;
    public String type;
    public long creationDate;
    public Integer latestID;
    public String Name;
}

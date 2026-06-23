package dev.gr1.db.bind;

import dev.gr1.db.orm.Id;
import dev.gr1.db.orm.Table;
import dev.gr1.proj.ProjectType;

import java.util.Date;

@Table(name = "Projekt")
public class Projekt {
    @Id
    public int ID;
    public int userID;
    public Integer startKostenID;
    public Integer kapitalID;
    public String type;
    public long creationDate;
    public Integer latestID;
    public Integer firstID;
    public String Name;

    public boolean isFreemium() {
        return type.equals(ProjectType.Freemium.name());
    }
}

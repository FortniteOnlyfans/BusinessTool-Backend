package dev.gr1.db.bind;

import dev.gr1.db.orm.Id;
import dev.gr1.db.orm.Table;
import java.util.Date;

@Table(name ="ProjektVersion")
public class ProjektVersion {
    @Id
    public int ID;
    public int ProjektID;
    public long erstellt;
    public int userID;
    public int finanzierungID;
    public int kostenID;
    public int privatID;
    public int ertragID;
}

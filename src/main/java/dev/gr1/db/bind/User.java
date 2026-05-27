package dev.gr1.db.bind;

import dev.gr1.db.orm.Id;
import dev.gr1.db.orm.Table;

import java.util.Date;

@Table(name = "User")
public class User {
    @Id
    public int ID;
    public String Name;
    public String Pwd;
    public long creationDate;
}

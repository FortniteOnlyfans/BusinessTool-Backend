package dev.gr1.db.bind;

import dev.gr1.db.orm.Id;
import dev.gr1.db.orm.Table;

@Table(name = "USERS")
public class User {
    @Id
    public int ID;
    public String NAME;
    public String PWDHASH;
}

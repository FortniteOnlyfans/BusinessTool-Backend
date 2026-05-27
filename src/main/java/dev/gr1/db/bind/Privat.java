package dev.gr1.db.bind;

import dev.gr1.db.orm.Id;
import dev.gr1.db.orm.Table;

@Table(name = "Privat")
public class Privat {
    @Id
    public int ID;
}


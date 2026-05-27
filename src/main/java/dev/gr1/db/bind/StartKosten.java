package dev.gr1.db.bind;

import dev.gr1.db.orm.Id;
import dev.gr1.db.orm.Table;

@Table(name = "StartKosten")
public class StartKosten {
    @Id
    public int ID;
}

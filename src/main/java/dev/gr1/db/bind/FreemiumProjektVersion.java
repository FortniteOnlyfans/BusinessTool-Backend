package dev.gr1.db.bind;

import dev.gr1.db.orm.Id;
import dev.gr1.db.orm.Table;

@Table(name = "FreemiumProjektVersion")
public class FreemiumProjektVersion {
    @Id
    public int ID;
    public int BasisNutzer;
    public int PremiumNutzer;
    public double PreisPremium;
    public int AboZeit;
    public double Wachstumsrate;
    public int ProjektVersionID;
}

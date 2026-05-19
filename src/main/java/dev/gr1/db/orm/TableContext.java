package dev.gr1.db.orm;

import java.sql.PreparedStatement;

public class TableContext {
    public final TableDesc desc;

    public final PreparedStatement selectById;
    public final PreparedStatement insert;
    public final PreparedStatement update;
    public final PreparedStatement delete;
    public final PreparedStatement selectAll;

    public TableContext(TableDesc desc, PreparedStatement selectById, PreparedStatement insert, PreparedStatement update, PreparedStatement delete, PreparedStatement selectAll) {
        this.desc = desc;
        this.selectById = selectById;
        this.insert = insert;
        this.update = update;
        this.delete = delete;
        this.selectAll = selectAll;
    }
}
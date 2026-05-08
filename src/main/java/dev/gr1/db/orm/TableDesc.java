package dev.gr1.db.orm;

import java.util.List;

public record TableDesc(Class<?> clazz, String tableName, List<String> fieldNames, String PKIDFieldName) {
}

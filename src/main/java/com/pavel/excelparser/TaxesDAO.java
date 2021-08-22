package com.pavel.excelparser;

import java.sql.SQLException;
import java.util.List;

/**Интерфейс для взаимодействия с таблицей nalog1nom_chistyakov
 */
public interface TaxesDAO {

    /**сохраняет все предоставленные записи в таблицу
     * @param records список записей для занесения в базу данных, все поля должны быть не null
     * @throws SQLException Если произошла ошибка в работе с базой данных
     */
    void saveAll(List<Record> records) throws SQLException;
}

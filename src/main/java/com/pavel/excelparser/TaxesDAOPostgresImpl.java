package com.pavel.excelparser;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class TaxesDAOPostgresImpl implements TaxesDAO {

    private final DataSource ds;

    public TaxesDAOPostgresImpl(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public void saveAll(List<Record> records) throws SQLException {
        String query = "INSERT INTO " +
                "nalog1nom_chistyakov(fielda, fieldb, fieldv, field1, field2, field3, field4, field5, field6, field7, " +
                "field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18" +
                ", field19, field20, field21, field22, field23, field24, field25, ter, dat) VALUES(?, ?, ?, ?, ?, ?, ?," +
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ds.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement st = conn.prepareStatement(query)){
                for (Record record : records) {
                    st.setString(1, record.getFielda());
                    st.setString(2, record.getFieldb());
                    st.setString(3, record.getFieldv());
                    st.setString(4, record.getField1());
                    st.setString(5, record.getField2());
                    st.setString(6, record.getField3());
                    st.setString(7, record.getField4());
                    st.setString(8, record.getField5());
                    st.setString(9, record.getField6());
                    st.setString(10, record.getField7());
                    st.setString(11, record.getField8());
                    st.setString(12, record.getField9());
                    st.setString(13, record.getField10());
                    st.setString(14, record.getField11());
                    st.setString(15, record.getField12());
                    st.setString(16, record.getField13());
                    st.setString(17, record.getField14());
                    st.setString(18, record.getField15());
                    st.setString(19, record.getField16());
                    st.setString(20, record.getField17());
                    st.setString(21, record.getField18());
                    st.setString(22, record.getField19());
                    st.setString(23, record.getField20());
                    st.setString(24, record.getField21());
                    st.setString(25, record.getField22());
                    st.setString(26, record.getField23());
                    st.setString(27, record.getField24());
                    st.setString(28, record.getField25());
                    st.setString(29, record.getTer());
                    st.setString(30, record.getDat());
                    st.addBatch();
                }
                st.executeBatch();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }
}

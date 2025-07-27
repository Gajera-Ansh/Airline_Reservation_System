package airline.dao;

import airline.ds.ArrayList;
import airline.model.Admin;
import airline.util.DBUtil;

import java.sql.ResultSet;
import java.sql.Statement;

public class AdminDAO {
    public static ArrayList<Admin> admins = new ArrayList<>();

    public static ArrayList<Admin> getAdmins() throws Exception {
        String sql = "SELECT * FROM admins";
        Statement st = DBUtil.con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        admins.clear();
        while (rs.next()) {
            admins.add(new Admin(rs.getInt(1), rs.getString(2), rs.getString(3)));
        }
        return admins;
    }
}

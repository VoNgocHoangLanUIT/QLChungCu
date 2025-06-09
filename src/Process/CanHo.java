/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Process;

import ConnectDB.ConnectionUtils;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author DELL
 */
public class CanHo {
    public int themCanHo(int maCanHo, double dienTich, String trangThai, int chiSoDien, int chiSoNuoc) {
        int i = 0;
        // TODO add your handling code here:
        try (Connection con = ConnectionUtils.getMyConnection()) {
        
//            String query = "INSERT INTO "
//                    + "DOIBONG(MAD,TENDOI,QUOCGIA)"
//                    +" VALUES('"
//                    +maDoi+"','"+tenDoi+"','"+quocGia+"')";

            String query = "INSERT INTO CANHO VALUES(?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, maCanHo);
            ps.setDouble(2, dienTich);
            ps.setString(3, trangThai);
            ps.setInt(4, chiSoDien);
            ps.setInt(5, chiSoNuoc);
            i = ps.executeUpdate();
//            Statement stat = con.createStatement();
//            i = stat.executeUpdate(query);
            
        }
        catch(Exception e){
            System.out.println(e);
        }
        return i;
    }
}

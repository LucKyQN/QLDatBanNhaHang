//package DAO;
//
//import Entity.DanhMuc;
//import Entity.MonAn;
//import connectDatabase.ConnectDB;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.Types;
//import java.util.ArrayList;
//import java.util.List;
//
//public class MonAnDAO {
//
//    private Connection getConnection() throws Exception {
//        ConnectDB.getInstance().connect();
//        return ConnectDB.getInstance().getConnection();
//    }
//
//    public List<DanhMuc> getAllDanhMuc() {
//        List<DanhMuc> list = new ArrayList<>();
//        String sql = "SELECT maDM, tenDM FROM DanhMucMonAn ORDER BY maDM";
//
//        try {
//            Connection con = getConnection();
//            PreparedStatement stmt = con.prepareStatement(sql);
//            ResultSet rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                DanhMuc dm = new DanhMuc();
//                dm.setMaDM(rs.getString("maDM"));
//                dm.setTenDM(rs.getString("tenDM"));
//                list.add(dm);
//            }
//
//            rs.close();
//            stmt.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return list;
//    }
//
//    public List<MonAn> getAllMonAn() {
//        List<MonAn> list = new ArrayList<>();
//
//        String sql =
//                "SELECT m.maMonAn, m.maDM, d.tenDM, m.tenMonAn, m.donVi, m.soLuongTon, m.giaBan, " +
//                "       m.moTa, m.ghiChu, m.anhMon, m.tinhTrang " +
//                "FROM MonAn m " +
//                "LEFT JOIN DanhMucMonAn d ON m.maDM = d.maDM " +
//                "ORDER BY m.maMonAn";
//
//        try {
//            Connection con = getConnection();
//            PreparedStatement stmt = con.prepareStatement(sql);
//            ResultSet rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                list.add(mapMonAn(rs));
//            }
//
//            rs.close();
//            stmt.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return list;
//    }
//
//    public boolean themMonAn(MonAn mon) {
//        String sql =
//                "INSERT INTO MonAn (maMonAn, maDM, tenMonAn, donVi, soLuongTon, giaBan, moTa, ghiChu, anhMon, tinhTrang) " +
//                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//
//        try {
//            Connection con = getConnection();
//            PreparedStatement stmt = con.prepareStatement(sql);
//
//            stmt.setString(1, mon.getMaMonAn());
//
//            if (mon.getDanhMuc() != null && mon.getDanhMuc().getMaDM() != null && !mon.getDanhMuc().getMaDM().trim().isEmpty()) {
//                stmt.setString(2, mon.getDanhMuc().getMaDM());
//            } else {
//                stmt.setNull(2, Types.VARCHAR);
//            }
//
//            stmt.setString(3, mon.getTenMon());
//            stmt.setString(4, mon.getDonVi());
//            stmt.setInt(5, mon.getSoLuong());
//            stmt.setDouble(6, mon.getGiaMon());
//            stmt.setString(7, mon.getMoTa());
//            stmt.setString(8, mon.getGhiChu());
//            stmt.setString(9, mon.getAnhMon());
//            stmt.setBoolean(10, mon.isTinhTrang());
//
//            int rows = stmt.executeUpdate();
//            stmt.close();
//
//            return rows > 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public boolean suaMonAn(MonAn mon) {
//        String sql =
//                "UPDATE MonAn SET maDM = ?, tenMonAn = ?, donVi = ?, soLuongTon = ?, giaBan = ?, " +
//                "moTa = ?, ghiChu = ?, anhMon = ?, tinhTrang = ? " +
//                "WHERE maMonAn = ?";
//
//        try {
//            Connection con = getConnection();
//            PreparedStatement stmt = con.prepareStatement(sql);
//
//            if (mon.getDanhMuc() != null && mon.getDanhMuc().getMaDM() != null && !mon.getDanhMuc().getMaDM().trim().isEmpty()) {
//                stmt.setString(1, mon.getDanhMuc().getMaDM());
//            } else {
//                stmt.setNull(1, Types.VARCHAR);
//            }
//
//            stmt.setString(2, mon.getTenMon());
//            stmt.setString(3, mon.getDonVi());
//            stmt.setInt(4, mon.getSoLuong());
//            stmt.setDouble(5, mon.getGiaMon());
//            stmt.setString(6, mon.getMoTa());
//            stmt.setString(7, mon.getGhiChu());
//            stmt.setString(8, mon.getAnhMon());
//            stmt.setBoolean(9, mon.isTinhTrang());
//            stmt.setString(10, mon.getMaMonAn());
//
//            int rows = stmt.executeUpdate();
//            stmt.close();
//
//            return rows > 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public boolean xoaMemMonAn(String maMonAn) {
//        String sql = "UPDATE MonAn SET tinhTrang = 0 WHERE maMonAn = ?";
//
//        try {
//            Connection con = getConnection();
//            PreparedStatement stmt = con.prepareStatement(sql);
//            stmt.setString(1, maMonAn);
//
//            int rows = stmt.executeUpdate();
//            stmt.close();
//
//            return rows > 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public boolean tonTaiMaMonAn(String maMonAn) {
//        String sql = "SELECT COUNT(*) FROM MonAn WHERE maMonAn = ?";
//
//        try {
//            Connection con = getConnection();
//            PreparedStatement stmt = con.prepareStatement(sql);
//            stmt.setString(1, maMonAn);
//
//            ResultSet rs = stmt.executeQuery();
//            boolean exists = false;
//            if (rs.next()) {
//                exists = rs.getInt(1) > 0;
//            }
//
//            rs.close();
//            stmt.close();
//            return exists;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    private MonAn mapMonAn(ResultSet rs) throws Exception {
//        MonAn mon = new MonAn();
//        mon.setMaMonAn(rs.getString("maMonAn"));
//        mon.setTenMon(rs.getString("tenMonAn"));
//        mon.setDonVi(rs.getString("donVi"));
//        mon.setSoLuong(rs.getInt("soLuongTon"));
//        mon.setGiaMon(rs.getDouble("giaBan"));
//        mon.setMoTa(rs.getString("moTa"));
//        mon.setGhiChu(rs.getString("ghiChu"));
//        mon.setAnhMon(rs.getString("anhMon"));
//        mon.setTinhTrang(rs.getBoolean("tinhTrang"));
//
//        String maDM = rs.getString("maDM");
//        String tenDM = rs.getString("tenDM");
//        if (maDM != null) {
//            DanhMuc dm = new DanhMuc();
//            dm.setMaDM(maDM);
//            dm.setTenDM(tenDM);
//            mon.setDanhMuc(dm);
//        }
//
//        return mon;
//    }
//}
package DAO;

import Entity.DanhMuc;
import Entity.MonAn;
import connectDatabase.ConnectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MonAnDAO {

    private Connection getConnection() throws Exception {
        ConnectDB.getInstance().connect();
        return ConnectDB.getInstance().getConnection();
    }

    // --- 1. LẤY TẤT CẢ MÓN ĂN ---
    public List<MonAn> getAllMonAn() {
        List<MonAn> list = new ArrayList<>();
        // Đã khớp 100% tên cột: tenMonAn, giaBan, soLuongTon
        String sql = "SELECT maMonAn, tenMonAn, giaBan, soLuongTon, donVi, moTa, ghiChu, anhMon, tinhTrang FROM MonAn";

        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MonAn mon = new MonAn();
                mon.setMaMonAn(rs.getString("maMonAn"));
                mon.setTenMon(rs.getString("tenMonAn"));
                mon.setGiaMon(rs.getDouble("giaBan"));     // Lấy từ cột giaBan
                mon.setSoLuong(rs.getInt("soLuongTon"));   // Lấy từ cột soLuongTon
                mon.setDonVi(rs.getString("donVi"));
                mon.setMoTa(rs.getString("moTa"));
                mon.setGhiChu(rs.getString("ghiChu"));
                mon.setAnhMon(rs.getString("anhMon"));
                mon.setTinhTrang(rs.getBoolean("tinhTrang"));

                list.add(mon);
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println("❌ LỖI SQL TẠI getAllMonAn(): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // --- 2. LẤY DANH SÁCH DANH MỤC CHO COMBOBOX ---
    public List<DanhMuc> getAllDanhMuc() {
        List<DanhMuc> list = new ArrayList<>();
        String sql = "SELECT maDM, tenDM FROM DanhMuc";

        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                DanhMuc dm = new DanhMuc();
                dm.setMaDM(rs.getString("maDM"));
                dm.setTenDM(rs.getString("tenDM"));
                list.add(dm);
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println("❌ LỖI SQL TẠI getAllDanhMuc(): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // --- 3. KIỂM TRA MÃ TỒN TẠI ---
    public boolean tonTaiMaMonAn(String maMon) {
        String sql = "SELECT COUNT(*) FROM MonAn WHERE maMonAn = ?";
        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maMon);

            ResultSet rs = stmt.executeQuery();
            boolean exists = false;
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }

            rs.close();
            stmt.close();
            return exists;
        } catch (Exception e) {
            System.err.println("❌ LỖI SQL TẠI tonTaiMaMonAn(): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // --- 4. THÊM MÓN ĂN ---
    public boolean themMonAn(MonAn mon) {
        // Đã sửa tên cột: tenMonAn, giaBan, soLuongTon
        String sql = "INSERT INTO MonAn (maMonAn, tenMonAn, giaBan, soLuongTon, donVi, moTa, ghiChu, anhMon, maDM, tinhTrang) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setString(1, mon.getMaMonAn());
            stmt.setString(2, mon.getTenMon());
            stmt.setDouble(3, mon.getGiaMon());
            stmt.setInt(4, mon.getSoLuong());
            stmt.setString(5, mon.getDonVi());
            stmt.setString(6, mon.getMoTa());
            stmt.setString(7, mon.getGhiChu());
            stmt.setString(8, mon.getAnhMon());
            stmt.setString(9, mon.getDanhMuc() != null ? mon.getDanhMuc().getMaDM() : null);
            stmt.setBoolean(10, mon.isTinhTrang());

            int rows = stmt.executeUpdate();
            stmt.close();
            return rows > 0;
        } catch (Exception e) {
            System.err.println("❌ LỖI SQL TẠI themMonAn(): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // --- 5. SỬA MÓN ĂN ---
    public boolean suaMonAn(MonAn mon) {
        // Đã sửa tên cột: tenMonAn, giaBan, soLuongTon
        String sql = "UPDATE MonAn SET tenMonAn=?, giaBan=?, soLuongTon=?, donVi=?, moTa=?, ghiChu=?, anhMon=?, maDM=?, tinhTrang=? " +
                     "WHERE maMonAn=?";
        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setString(1, mon.getTenMon());
            stmt.setDouble(2, mon.getGiaMon());
            stmt.setInt(3, mon.getSoLuong());
            stmt.setString(4, mon.getDonVi());
            stmt.setString(5, mon.getMoTa());
            stmt.setString(6, mon.getGhiChu());
            stmt.setString(7, mon.getAnhMon());
            stmt.setString(8, mon.getDanhMuc() != null ? mon.getDanhMuc().getMaDM() : null);
            stmt.setBoolean(9, mon.isTinhTrang());
            stmt.setString(10, mon.getMaMonAn());

            int rows = stmt.executeUpdate();
            stmt.close();
            return rows > 0;
        } catch (Exception e) {
            System.err.println("❌ LỖI SQL TẠI suaMonAn(): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // --- 6. XÓA MỀM (NGỪNG KINH DOANH) ---
    public boolean xoaMemMonAn(String maMon) {
        String sql = "UPDATE MonAn SET tinhTrang = 0 WHERE maMonAn = ?";
        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maMon);

            int rows = stmt.executeUpdate();
            stmt.close();
            return rows > 0;
        } catch (Exception e) {
            System.err.println("❌ LỖI SQL TẠI xoaMemMonAn(): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
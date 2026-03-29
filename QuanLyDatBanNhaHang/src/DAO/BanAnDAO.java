//package DAO;
//
//import Entity.BanAn;
//import Entity.LoaiBan;
//import connectDatabase.ConnectDB;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.ArrayList;
//import java.util.List;
//
//public class BanAnDAO {
//
//    private Connection getConnection() throws Exception {
//        ConnectDB.getInstance().connect();
//        return ConnectDB.getInstance().getConnection();
//    }
//
//    public List<LoaiBan> getAllLoaiBan() {
//        List<LoaiBan> list = new ArrayList<>();
//        String sql = "SELECT maLB, tenLB, soGhe FROM LoaiBan ORDER BY maLB";
//
//        try {
//            Connection con = getConnection();
//            PreparedStatement stmt = con.prepareStatement(sql);
//            ResultSet rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                LoaiBan lb = new LoaiBan();
//                lb.setMaLB(rs.getString("maLB"));
//                lb.setTenLB(rs.getString("tenLB"));
//                lb.setSoGhe(rs.getInt("soGhe"));
//                list.add(lb);
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
//    public List<BanAn> getAllBanAn() {
//        List<BanAn> list = new ArrayList<>();
//
//        String sql =
//                "SELECT b.maBan, b.maLB, l.tenLB, l.soGhe, b.tenBan, b.viTri, b.sucChua, b.trangThai, b.moTa " +
//                "FROM BanAn b " +
//                "LEFT JOIN LoaiBan l ON b.maLB = l.maLB " +
//                "ORDER BY b.maBan";
//
//        try {
//            Connection con = getConnection();
//            PreparedStatement stmt = con.prepareStatement(sql);
//            ResultSet rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                list.add(mapBanAn(rs));
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
//    public boolean themBanAn(BanAn ban) {
//        String sql =
//                "INSERT INTO BanAn (maBan, maLB, tenBan, viTri, sucChua, trangThai, moTa) " +
//                "VALUES (?, ?, ?, ?, ?, ?, ?)";
//
//        try {
//            Connection con = getConnection();
//            PreparedStatement stmt = con.prepareStatement(sql);
//
//            stmt.setString(1, ban.getMaBan());
//            stmt.setString(2, ban.getLoaiBan() != null ? ban.getLoaiBan().getMaLB() : null);
//            stmt.setString(3, ban.getTenBan());
//            stmt.setString(4, ban.getViTri());
//            stmt.setInt(5, ban.getSucChua());
//            stmt.setString(6, ban.getTrangThai());
//            stmt.setString(7, ban.getMoTa());
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
//    public boolean suaBanAn(BanAn ban) {
//        String sql =
//                "UPDATE BanAn SET maLB = ?, tenBan = ?, viTri = ?, sucChua = ?, trangThai = ?, moTa = ? " +
//                "WHERE maBan = ?";
//
//        try {
//            Connection con = getConnection();
//            PreparedStatement stmt = con.prepareStatement(sql);
//
//            stmt.setString(1, ban.getLoaiBan() != null ? ban.getLoaiBan().getMaLB() : null);
//            stmt.setString(2, ban.getTenBan());
//            stmt.setString(3, ban.getViTri());
//            stmt.setInt(4, ban.getSucChua());
//            stmt.setString(5, ban.getTrangThai());
//            stmt.setString(6, ban.getMoTa());
//            stmt.setString(7, ban.getMaBan());
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
//    public boolean xoaMemBanAn(String maBan) {
//        String sql = "UPDATE BanAn SET trangThai = N'Ngưng sử dụng' WHERE maBan = ?";
//
//        try {
//            Connection con = getConnection();
//            PreparedStatement stmt = con.prepareStatement(sql);
//            stmt.setString(1, maBan);
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
//    public boolean tonTaiMaBan(String maBan) {
//        String sql = "SELECT COUNT(*) FROM BanAn WHERE maBan = ?";
//
//        try {
//            Connection con = getConnection();
//            PreparedStatement stmt = con.prepareStatement(sql);
//            stmt.setString(1, maBan);
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
//    private BanAn mapBanAn(ResultSet rs) throws Exception {
//        BanAn ban = new BanAn();
//        ban.setMaBan(rs.getString("maBan"));
//        ban.setTenBan(rs.getString("tenBan"));
//        ban.setViTri(rs.getString("viTri"));
//        ban.setSucChua(rs.getInt("sucChua"));
//        ban.setTrangThai(rs.getString("trangThai"));
//        ban.setMoTa(rs.getString("moTa"));
//
//        String maLB = rs.getString("maLB");
//        if (maLB != null) {
//            LoaiBan lb = new LoaiBan();
//            lb.setMaLB(maLB);
//            lb.setTenLB(rs.getString("tenLB"));
//            lb.setSoGhe(rs.getInt("soGhe"));
//            ban.setLoaiBan(lb);
//        }
//
//        return ban;
//    }
//}
package DAO;

import Entity.BanAn;
import Entity.LoaiBan;
import connectDatabase.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BanAnDAO {

    private Connection getConnection() throws Exception {
        ConnectDB.getInstance().connect();
        return ConnectDB.getInstance().getConnection();
    }

    public List<LoaiBan> getAllLoaiBan() {
        List<LoaiBan> list = new ArrayList<>();
        String sql = "SELECT maLB, tenLB, soGhe FROM LoaiBan ORDER BY maLB";

        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LoaiBan lb = new LoaiBan();
                lb.setMaLB(rs.getString("maLB"));
                lb.setTenLB(rs.getString("tenLB"));
                lb.setSoGhe(rs.getInt("soGhe"));
                list.add(lb);
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<BanAn> getAllBanAn() {
        List<BanAn> list = new ArrayList<>();

        String sql =
                "SELECT b.maBan, b.maLB, l.tenLB, l.soGhe, b.tenBan, b.viTri, b.sucChua, b.trangThai, b.moTa " +
                "FROM BanAn b " +
                "LEFT JOIN LoaiBan l ON b.maLB = l.maLB " +
                "ORDER BY b.maBan";

        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapBanAn(rs));
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean themBanAn(BanAn ban) {
        String sql =
                "INSERT INTO BanAn (maBan, maLB, tenBan, viTri, sucChua, trangThai, moTa) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setString(1, ban.getMaBan());
            stmt.setString(2, ban.getLoaiBan() != null ? ban.getLoaiBan().getMaLB() : null);
            stmt.setString(3, ban.getTenBan());
            stmt.setString(4, ban.getViTri());
            stmt.setInt(5, ban.getSucChua());
            stmt.setString(6, ban.getTrangThai());
            stmt.setString(7, ban.getMoTa());

            int rows = stmt.executeUpdate();
            stmt.close();

            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean suaBanAn(BanAn ban) {
        String sql =
                "UPDATE BanAn SET maLB = ?, tenBan = ?, viTri = ?, sucChua = ?, trangThai = ?, moTa = ? " +
                "WHERE maBan = ?";

        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setString(1, ban.getLoaiBan() != null ? ban.getLoaiBan().getMaLB() : null);
            stmt.setString(2, ban.getTenBan());
            stmt.setString(3, ban.getViTri());
            stmt.setInt(4, ban.getSucChua());
            stmt.setString(5, ban.getTrangThai());
            stmt.setString(6, ban.getMoTa());
            stmt.setString(7, ban.getMaBan());

            int rows = stmt.executeUpdate();
            stmt.close();

            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========================================================
    // CẬP NHẬT TRẠNG THÁI BÀN ĐƠN LẺ
    // ========================================================
    public boolean capNhatTrangThaiBan(String tenBan, String trangThaiMoi) {
        String sql = "UPDATE BanAn SET trangThai = ? WHERE tenBan = ?";
        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, trangThaiMoi);
            stmt.setString(2, tenBan);
            
            int rows = stmt.executeUpdate();
            stmt.close();
            
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========================================================
    // HÀM MỚI: CHUYỂN BÀN & GỘP BÀN (DÙNG TRANSACTION)
    // ========================================================
    public boolean chuyenHoacGopBan(String banCu, String banMoi) {
        Connection con = null;
        try {
            con = getConnection();
            // Tắt auto commit để gộp 2 lệnh SQL thành 1 khối an toàn
            con.setAutoCommit(false); 

            // 1. Đặt bàn cũ (Bàn chuyển đi / Bàn bị gộp) thành "Trống"
            String sql1 = "UPDATE BanAn SET trangThai = N'Trống' WHERE tenBan = ?";
            PreparedStatement ps1 = con.prepareStatement(sql1);
            ps1.setString(1, banCu);
            ps1.executeUpdate();
            ps1.close();

            // 2. Đặt bàn mới (Bàn chuyển đến / Bàn nhận gộp) thành "Có khách"
            String sql2 = "UPDATE BanAn SET trangThai = N'Có khách' WHERE tenBan = ?";
            PreparedStatement ps2 = con.prepareStatement(sql2);
            ps2.setString(1, banMoi);
            ps2.executeUpdate();
            ps2.close();

            // Xác nhận lưu toàn bộ thay đổi
            con.commit(); 
            return true;
            
        } catch (Exception e) {
            // Nếu có lỗi ở bất kỳ bước nào, hoàn tác lại (Rollback)
            try { if(con != null) con.rollback(); } catch(Exception ex) {}
            e.printStackTrace();
            return false;
        } finally {
            // Bật lại auto commit
            try { if(con != null) con.setAutoCommit(true); } catch(Exception ex) {}
        }
    }

    public boolean xoaMemBanAn(String maBan) {
        String sql = "UPDATE BanAn SET trangThai = N'Ngưng sử dụng' WHERE maBan = ?";

        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maBan);

            int rows = stmt.executeUpdate();
            stmt.close();

            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean tonTaiMaBan(String maBan) {
        String sql = "SELECT COUNT(*) FROM BanAn WHERE maBan = ?";

        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maBan);

            ResultSet rs = stmt.executeQuery();
            boolean exists = false;
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }

            rs.close();
            stmt.close();
            return exists;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private BanAn mapBanAn(ResultSet rs) throws Exception {
        BanAn ban = new BanAn();
        ban.setMaBan(rs.getString("maBan"));
        ban.setTenBan(rs.getString("tenBan"));
        ban.setViTri(rs.getString("viTri"));
        ban.setSucChua(rs.getInt("sucChua"));
        ban.setTrangThai(rs.getString("trangThai"));
        ban.setMoTa(rs.getString("moTa"));

        String maLB = rs.getString("maLB");
        if (maLB != null) {
            LoaiBan lb = new LoaiBan();
            lb.setMaLB(maLB);
            lb.setTenLB(rs.getString("tenLB"));
            lb.setSoGhe(rs.getInt("soGhe"));
            ban.setLoaiBan(lb);
        }

        return ban;
    }
}
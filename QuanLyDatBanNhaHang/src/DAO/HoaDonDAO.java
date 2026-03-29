//package DAO;
//
//import connectDatabase.ConnectDB;
//import Model.BanAnModel;
//import Model.MonAnModel;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class HoaDonDAO {
//
//    private Connection getConnection() throws SQLException {
//        ConnectDB.getInstance().connect();
//        return ConnectDB.getInstance().getConnection();
//    }
//
//   
//    public List<BanAnModel> getDanhSachBanChuaThanhToan() {
//        List<BanAnModel> list = new ArrayList<>();
//        String sql =
//            "SELECT b.maBan, b.tenBan, b.sucChua, h.maHD, " +
//            "       ISNULL(SUM(ct.thanhTien), 0) AS tamTinh " +
//            "FROM BanAn b " +
//            "JOIN HoaDon h ON b.maBan = h.maBan " +
//            "LEFT JOIN ChiTietHoaDon ct ON h.maHD = ct.maHD " +
//            "WHERE h.trangThaiThanhToan = N'Chưa thanh toán' " +
//            "GROUP BY b.maBan, b.tenBan, b.sucChua, h.maHD " +
//            "ORDER BY b.maBan";
//
//        try {
//            Connection con = getConnection();
//            try (PreparedStatement ps = con.prepareStatement(sql);
//                 ResultSet rs = ps.executeQuery()) {
//
//                while (rs.next()) {
//                    BanAnModel ban = new BanAnModel();
//                    ban.maBan   = rs.getString("maBan");
//                    ban.tenBan  = rs.getString("tenBan");
//                    ban.sucChua = rs.getInt("sucChua");
//                    ban.maHD    = rs.getString("maHD");
//                    ban.tamTinh = (long) rs.getDouble("tamTinh");
//                    list.add(ban);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//
//    public List<MonAnModel> getChiTietHoaDon(String maHD) {
//        List<MonAnModel> list = new ArrayList<>();
//        String sql =
//            "SELECT m.tenMonAn, ct.soLuong, ct.donGia, ct.thanhTien " +
//            "FROM ChiTietHoaDon ct " +
//            "JOIN MonAn m ON ct.maMonAn = m.maMonAn " +
//            "WHERE ct.maHD = ?";
//
//        try {
//            Connection con = getConnection();
//            try (PreparedStatement ps = con.prepareStatement(sql)) {
//                ps.setString(1, maHD);
//
//                try (ResultSet rs = ps.executeQuery()) {
//                    while (rs.next()) {
//                        MonAnModel mon = new MonAnModel();
//                        mon.tenMonAn  = rs.getString("tenMonAn");
//                        mon.soLuong   = rs.getInt("soLuong");
//                        mon.donGia    = (long) rs.getDouble("donGia");
//                        mon.thanhTien = (long) rs.getDouble("thanhTien");
//                        list.add(mon);
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//
//    
//    public List<String[]> getKhuyenMaiHieuLuc() {
//        List<String[]> list = new ArrayList<>();
//        list.add(new String[]{"", "Không giảm giá", "0", "Phần trăm"});
//
//        String sql =
//            "SELECT maKM, tenKM, giaTriKM, loaiKM " +
//            "FROM KhuyenMai " +
//            "WHERE trangThai = 1 " +
//            "  AND GETDATE() BETWEEN ngayBatDau AND ngayKetThuc " +
//            "ORDER BY tenKM";
//
//        try {
//            Connection con = getConnection();
//            try (PreparedStatement ps = con.prepareStatement(sql);
//                 ResultSet rs = ps.executeQuery()) {
//
//                while (rs.next()) {
//                    list.add(new String[]{
//                        rs.getString("maKM"),
//                        rs.getString("tenKM"),
//                        String.valueOf(rs.getDouble("giaTriKM")),
//                        rs.getString("loaiKM")
//                    });
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//
//   
//    public boolean thanhToan(String maHD, double chietKhau, String maKM) {
//        String sql = "{call sp_ThanhToanHoaDon(?, ?, ?)}";
//        String updBan =
//            "UPDATE BanAn SET trangThai = N'Trống' " +
//            "WHERE maBan = (SELECT maBan FROM HoaDon WHERE maHD = ?)";
//
//        Connection con = null;
//        try {
//            con = getConnection();
//            con.setAutoCommit(false);
//
//            try (CallableStatement cs = con.prepareCall(sql)) {
//                cs.setString(1, maHD);
//                cs.setDouble(2, chietKhau);
//
//                if (maKM == null || maKM.trim().isEmpty()) {
//                    cs.setNull(3, Types.VARCHAR);
//                } else {
//                    cs.setString(3, maKM);
//                }
//
//                cs.executeUpdate();
//            }
//
//            try (PreparedStatement ps = con.prepareStatement(updBan)) {
//                ps.setString(1, maHD);
//                ps.executeUpdate();
//            }
//
//            con.commit();
//            return true;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            if (con != null) {
//                try {
//                    con.rollback();
//                } catch (SQLException ex) {
//                    ex.printStackTrace();
//                }
//            }
//            return false;
//
//        } finally {
//            if (con != null) {
//                try {
//                    con.setAutoCommit(true);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    
//    public boolean huyHoaDon(String maHD) {
//        Connection con = null;
//        try {
//            con = getConnection();
//            con.setAutoCommit(false);
//
//            try (PreparedStatement ps = con.prepareStatement(
//                    "UPDATE BanAn SET trangThai = N'Trống' " +
//                    "WHERE maBan = (SELECT maBan FROM HoaDon WHERE maHD = ?)")) {
//                ps.setString(1, maHD);
//                ps.executeUpdate();
//            }
//
//            try (PreparedStatement ps = con.prepareStatement(
//                    "DELETE FROM HoaDonKhuyenMai WHERE maHD = ?")) {
//                ps.setString(1, maHD);
//                ps.executeUpdate();
//            }
//
//            try (PreparedStatement ps = con.prepareStatement(
//                    "DELETE FROM ChiTietHoaDon WHERE maHD = ?")) {
//                ps.setString(1, maHD);
//                ps.executeUpdate();
//            }
//
//            try (PreparedStatement ps = con.prepareStatement(
//                    "DELETE FROM HoaDon WHERE maHD = ?")) {
//                ps.setString(1, maHD);
//                ps.executeUpdate();
//            }
//
//            con.commit();
//            return true;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            if (con != null) {
//                try {
//                    con.rollback();
//                } catch (SQLException ex) {
//                    ex.printStackTrace();
//                }
//            }
//            return false;
//
//        } finally {
//            if (con != null) {
//                try {
//                    con.setAutoCommit(true);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    
//    public long getDoanhThuHomNay() {
//        String sql =
//            "SELECT ISNULL(SUM(tongTien), 0) AS dt FROM HoaDon " +
//            "WHERE trangThaiThanhToan = N'Đã thanh toán' " +
//            "  AND CAST(ngayGioThanhToan AS DATE) = CAST(GETDATE() AS DATE)";
//
//        try {
//            Connection con = getConnection();
//            try (PreparedStatement ps = con.prepareStatement(sql);
//                 ResultSet rs = ps.executeQuery()) {
//
//                if (rs.next()) {
//                    return (long) rs.getDouble("dt");
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }
//
//    public int getTongDonHomNay() {
//        String sql =
//            "SELECT COUNT(*) AS tong FROM HoaDon " +
//            "WHERE CAST(ngayGioLap AS DATE) = CAST(GETDATE() AS DATE)";
//
//        try {
//            Connection con = getConnection();
//            try (PreparedStatement ps = con.prepareStatement(sql);
//                 ResultSet rs = ps.executeQuery()) {
//
//                if (rs.next()) {
//                    return rs.getInt("tong");
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }
//
//   
//    public int getSoNhanVienDangLam() {
//        String sql = "SELECT COUNT(*) AS tong FROM NhanVien WHERE trangThai = 1";
//
//        try {
//            Connection con = getConnection();
//            try (PreparedStatement ps = con.prepareStatement(sql);
//                 ResultSet rs = ps.executeQuery()) {
//
//                if (rs.next()) {
//                    return rs.getInt("tong");
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }
//
//   
//    public double getTyLeHuy() {
//        String sql =
//            "SELECT CASE WHEN COUNT(*) = 0 THEN 0 " +
//            "ELSE CAST(SUM(CASE WHEN trangThaiThanhToan = N'Hủy' THEN 1 ELSE 0 END) AS FLOAT) " +
//            "     / COUNT(*) * 100 END AS tyLe " +
//            "FROM HoaDon " +
//            "WHERE CAST(ngayGioLap AS DATE) = CAST(GETDATE() AS DATE)";
//
//        try {
//            Connection con = getConnection();
//            try (PreparedStatement ps = con.prepareStatement(sql);
//                 ResultSet rs = ps.executeQuery()) {
//
//                if (rs.next()) {
//                    return rs.getDouble("tyLe");
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }
//
//    
//    public List<long[]> getDoanhThu7Ngay() {
//        List<long[]> list = new ArrayList<>();
//        String sql =
//            "SELECT CAST(ngayGioThanhToan AS DATE) AS ngay, " +
//            "       ISNULL(SUM(tongTien), 0) AS dt " +
//            "FROM HoaDon " +
//            "WHERE trangThaiThanhToan = N'Đã thanh toán' " +
//            "  AND ngayGioThanhToan >= DATEADD(DAY, -6, CAST(GETDATE() AS DATE)) " +
//            "GROUP BY CAST(ngayGioThanhToan AS DATE) " +
//            "ORDER BY ngay";
//
//        try {
//            Connection con = getConnection();
//            try (PreparedStatement ps = con.prepareStatement(sql);
//                 ResultSet rs = ps.executeQuery()) {
//
//                while (rs.next()) {
//                    list.add(new long[]{
//                        rs.getDate("ngay").getTime(),
//                        (long) rs.getDouble("dt")
//                    });
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//
//   
//    public List<String[]> getMonAnBanChay() {
//        List<String[]> list = new ArrayList<>();
//        String sql =
//            "SELECT TOP 5 tenMonAn, SUM(soLuong) AS sl, SUM(doanhThu) AS dt " +
//            "FROM vw_MonAnBanChay " +
//            "GROUP BY tenMonAn " +
//            "ORDER BY SUM(soLuong) DESC";
//
//        try {
//            Connection con = getConnection();
//            try (PreparedStatement ps = con.prepareStatement(sql);
//                 ResultSet rs = ps.executeQuery()) {
//
//                while (rs.next()) {
//                    list.add(new String[]{
//                        rs.getString("tenMonAn"),
//                        String.valueOf(rs.getInt("sl")),
//                        String.valueOf((long) rs.getDouble("dt"))
//                    });
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//
//    
//    public List<String[]> getCaLamViec() {
//        List<String[]> list = new ArrayList<>();
//        String sql =
//            "SELECT nv.hoTenNV, nv.caLam, " +
//            "  CASE WHEN nv.trangThai = 1 THEN N'Đang mở' ELSE N'Chưa mở' END AS trangThai, " +
//            "  CASE nv.caLam WHEN N'Ca sáng' THEN '07:00' " +
//            "                WHEN N'Ca chiều' THEN '14:00' " +
//            "                WHEN N'Ca tối'   THEN '18:00' ELSE '--' END AS gioBatDau, " +
//            "  ISNULL((SELECT SUM(h.tongTien) FROM HoaDon h " +
//            "          WHERE h.maNV = nv.maNV " +
//            "            AND CAST(h.ngayGioLap AS DATE) = CAST(GETDATE() AS DATE)), 0) AS tienKet " +
//            "FROM NhanVien nv " +
//            "WHERE nv.chucVu IN (N'Thu ngân', N'Nhân viên') " +
//            "ORDER BY nv.caLam";
//
//        try {
//            Connection con = getConnection();
//            try (PreparedStatement ps = con.prepareStatement(sql);
//                 ResultSet rs = ps.executeQuery()) {
//
//                while (rs.next()) {
//                    list.add(new String[]{
//                        rs.getString("hoTenNV"),
//                        rs.getString("caLam") != null ? rs.getString("caLam") : "--",
//                        rs.getString("trangThai"),
//                        rs.getString("gioBatDau"),
//                        String.valueOf((long) rs.getDouble("tienKet")),
//                        "0"
//                    });
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//}
//package DAO;
//
//import connectDatabase.ConnectDB;
//import Model.BanAnModel;
//import Model.MonAnModel;
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class HoaDonDAO {
//
//    private Connection getConnection() throws SQLException {
//        ConnectDB.getInstance().connect();
//        return ConnectDB.getInstance().getConnection();
//    }
//
//    // 1. Lấy danh sách bàn chưa thanh toán
//    public List<BanAnModel> getDanhSachBanChuaThanhToan() {
//        List<BanAnModel> list = new ArrayList<>();
//        String sql = "SELECT b.maBan, b.tenBan, b.sucChua, h.maHD, ISNULL(SUM(ct.thanhTien), 0) AS tamTinh " +
//                     "FROM BanAn b JOIN HoaDon h ON b.maBan = h.maBan " +
//                     "LEFT JOIN ChiTietHoaDon ct ON h.maHD = ct.maHD " +
//                     "WHERE h.trangThaiThanhToan = N'Chưa thanh toán' " +
//                     "GROUP BY b.maBan, b.tenBan, b.sucChua, h.maHD ORDER BY b.maBan";
//        try (Connection con = getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
//            while (rs.next()) {
//                BanAnModel ban = new BanAnModel();
//                ban.maBan = rs.getString("maBan");
//                ban.tenBan = rs.getString("tenBan");
//                ban.sucChua = rs.getInt("sucChua");
//                ban.maHD = rs.getString("maHD");
//                ban.tamTinh = (long) rs.getDouble("tamTinh");
//                list.add(ban);
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return list;
//    }
//
//    // 2. Lấy chi tiết món ăn (Fix lỗi gạch đỏ)
//    public List<MonAnModel> getChiTietHoaDon(String maHD) {
//        List<MonAnModel> list = new ArrayList<>();
//        String sql = "SELECT m.tenMonAn, ct.soLuong, ct.donGia, ct.thanhTien FROM ChiTietHoaDon ct " +
//                     "JOIN MonAn m ON ct.maMonAn = m.maMonAn WHERE ct.maHD = ?";
//        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
//            ps.setString(1, maHD);
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    MonAnModel mon = new MonAnModel();
//                    mon.tenMonAn = rs.getString("tenMonAn");
//                    mon.soLuong = rs.getInt("soLuong");
//                    mon.donGia = (long) rs.getDouble("donGia");
//                    mon.thanhTien = (long) rs.getDouble("thanhTien");
//                    list.add(mon);
//                }
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return list;
//    }
//
//    // 3. Lấy khuyến mãi (Fix lỗi gạch đỏ chỗ dsKM_Current)
//    public List<String[]> getKhuyenMaiHieuLuc() {
//        List<String[]> list = new ArrayList<>();
//        list.add(new String[]{"NONE", "Không giảm giá", "0", "Phần trăm"});
//        String sql = "SELECT maKM, tenKM, giaTriKM, loaiKM FROM KhuyenMai WHERE trangThai = 1";
//        try (Connection con = getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
//            while (rs.next()) {
//                list.add(new String[]{ rs.getString("maKM"), rs.getString("tenKM"), 
//                                       String.valueOf(rs.getDouble("giaTriKM")), rs.getString("loaiKM") });
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return list;
//    }
//
//    // 4. Thanh toán (Gọi Procedure)
//    public boolean thanhToan(String maHD, double chietKhau, String maKM) {
//        String sql = "{call sp_ThanhToanHoaDon(?, ?, ?)}";
//        try (Connection con = getConnection(); CallableStatement cs = con.prepareCall(sql)) {
//            cs.setString(1, maHD);
//            cs.setDouble(2, chietKhau);
//            if (maKM.equals("NONE")) cs.setNull(3, Types.VARCHAR);
//            else cs.setString(3, maKM);
//            return cs.executeUpdate() > 0;
//        } catch (SQLException e) { e.printStackTrace(); return false; }
//    }
//
//    // 5. Hủy hóa đơn (Fix lỗi gạch đỏ chỗ huyHoaDon)
//    public boolean huyHoaDon(String maHD) {
//        String sql = "UPDATE HoaDon SET trangThaiThanhToan = N'Hủy' WHERE maHD = ?";
//        String sqlBan = "UPDATE BanAn SET trangThai = N'Trống' WHERE maBan = (SELECT maBan FROM HoaDon WHERE maHD = ?)";
//        try (Connection con = getConnection()) {
//            con.setAutoCommit(false);
//            try (PreparedStatement ps1 = con.prepareStatement(sql);
//                 PreparedStatement ps2 = con.prepareStatement(sqlBan)) {
//                ps1.setString(1, maHD); ps1.executeUpdate();
//                ps2.setString(1, maHD); ps2.executeUpdate();
//                con.commit();
//                return true;
//            } catch (SQLException ex) { con.rollback(); return false; }
//        } catch (SQLException e) { e.printStackTrace(); return false; }
//    }
//}
package DAO;

import connectDatabase.ConnectDB;
import Model.BanAnModel;
import Model.MonAnModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    private Connection getConnection() throws SQLException {
        ConnectDB.getInstance().connect();
        return ConnectDB.getInstance().getConnection();
    }

    // ============================================================
    //  THUNGAN: Bàn đang có hóa đơn chưa thanh toán
    // ============================================================
    public List<BanAnModel> getDanhSachBanChuaThanhToan() {
        List<BanAnModel> list = new ArrayList<>();
        String sql =
            "SELECT b.maBan, b.tenBan, b.sucChua, h.maHD, " +
            "       ISNULL(SUM(ct.thanhTien), 0) AS tamTinh " +
            "FROM BanAn b " +
            "JOIN HoaDon h ON b.maBan = h.maBan " +
            "LEFT JOIN ChiTietHoaDon ct ON h.maHD = ct.maHD " +
            "WHERE h.trangThaiThanhToan = N'Chưa thanh toán' " +
            "GROUP BY b.maBan, b.tenBan, b.sucChua, h.maHD " +
            "ORDER BY b.maBan";

        try {
            Connection con = getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    BanAnModel ban = new BanAnModel();
                    ban.maBan   = rs.getString("maBan");
                    ban.tenBan  = rs.getString("tenBan");
                    ban.sucChua = rs.getInt("sucChua");
                    ban.maHD    = rs.getString("maHD");
                    ban.tamTinh = (long) rs.getDouble("tamTinh");
                    list.add(ban);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ============================================================
    //  THUNGAN: Chi tiết món của một hóa đơn
    // ============================================================
    public List<MonAnModel> getChiTietHoaDon(String maHD) {
        List<MonAnModel> list = new ArrayList<>();
        String sql =
            "SELECT m.maMonAn, m.tenMonAn, ct.soLuong, ct.donGia, ct.thanhTien " +
            "FROM ChiTietHoaDon ct " +
            "JOIN MonAn m ON ct.maMonAn = m.maMonAn " +
            "WHERE ct.maHD = ?";

        try {
            Connection con = getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maHD);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        MonAnModel mon = new MonAnModel();
                        mon.maMonAn   = rs.getString("maMonAn");
                        mon.tenMonAn  = rs.getString("tenMonAn");
                        mon.soLuong   = rs.getInt("soLuong");
                        mon.donGia    = (long) rs.getDouble("donGia");
                        mon.thanhTien = (long) rs.getDouble("thanhTien");
                        list.add(mon);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ============================================================
    //  PHỤC VỤ: CRUD chi tiết món trên hóa đơn chưa thanh toán
    // ============================================================

    /** Cộng dồn số lượng nếu món đã có trong hóa đơn, ngược lại thêm dòng mới. */
    public boolean themHoacTangMon(String maHD, String maMonAn, int soLuongThem) {
        if (maHD == null || maMonAn == null || soLuongThem <= 0) {
            return false;
        }

        String sqlGia =
            "SELECT giaBan FROM MonAn WHERE maMonAn = ? AND tinhTrang = 1";
        String sqlSel =
            "SELECT soLuong, donGia FROM ChiTietHoaDon WHERE maHD = ? AND maMonAn = ?";
        String sqlIns =
            "INSERT INTO ChiTietHoaDon (maHD, maMonAn, soLuong, donGia, thanhTien) " +
            "VALUES (?, ?, ?, ?, ?)";
        String sqlUpd =
            "UPDATE ChiTietHoaDon SET soLuong = ?, thanhTien = donGia * ? " +
            "WHERE maHD = ? AND maMonAn = ?";

        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);

            double donGia;
            try (PreparedStatement ps = con.prepareStatement(sqlGia)) {
                ps.setString(1, maMonAn);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        con.rollback();
                        return false;
                    }
                    donGia = rs.getDouble("giaBan");
                }
            }

            int slCu = -1;
            try (PreparedStatement ps = con.prepareStatement(sqlSel)) {
                ps.setString(1, maHD);
                ps.setString(2, maMonAn);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        slCu = rs.getInt("soLuong");
                    }
                }
            }

            if (slCu >= 0) {
                int slMoi = slCu + soLuongThem;
                try (PreparedStatement ps = con.prepareStatement(sqlUpd)) {
                    ps.setInt(1, slMoi);
                    ps.setInt(2, slMoi);
                    ps.setString(3, maHD);
                    ps.setString(4, maMonAn);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = con.prepareStatement(sqlIns)) {
                    ps.setString(1, maHD);
                    ps.setString(2, maMonAn);
                    ps.setInt(3, soLuongThem);
                    ps.setDouble(4, donGia);
                    ps.setDouble(5, donGia * soLuongThem);
                    ps.executeUpdate();
                }
            }

            capNhatTongTienHoaDon(con, maHD);
            con.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /** Đặt lại số lượng món; nếu {@code soLuongMoi <= 0} thì xóa dòng chi tiết. */
    public boolean capNhatSoLuongMon(String maHD, String maMonAn, int soLuongMoi) {
        if (maHD == null || maMonAn == null) {
            return false;
        }
        if (soLuongMoi <= 0) {
            return xoaMonKhoiChiTiet(maHD, maMonAn);
        }

        String sql =
            "UPDATE ChiTietHoaDon SET soLuong = ?, thanhTien = donGia * ? " +
            "WHERE maHD = ? AND maMonAn = ?";

        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, soLuongMoi);
                ps.setInt(2, soLuongMoi);
                ps.setString(3, maHD);
                ps.setString(4, maMonAn);
                if (ps.executeUpdate() == 0) {
                    con.rollback();
                    return false;
                }
            }
            capNhatTongTienHoaDon(con, maHD);
            con.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean xoaMonKhoiChiTiet(String maHD, String maMonAn) {
        if (maHD == null || maMonAn == null) {
            return false;
        }

        String sqlDel =
            "DELETE FROM ChiTietHoaDon WHERE maHD = ? AND maMonAn = ?";

        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sqlDel)) {
                ps.setString(1, maHD);
                ps.setString(2, maMonAn);
                if (ps.executeUpdate() == 0) {
                    con.rollback();
                    return false;
                }
            }
            capNhatTongTienHoaDon(con, maHD);
            con.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void capNhatTongTienHoaDon(Connection con, String maHD) throws SQLException {
        String sql =
            "UPDATE HoaDon SET tongTien = ISNULL(" +
            "(SELECT SUM(thanhTien) FROM ChiTietHoaDon WHERE maHD = ?), 0) " +
            "WHERE maHD = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHD);
            ps.setString(2, maHD);
            ps.executeUpdate();
        }
    }

    // ============================================================
    //  THUNGAN: Khuyến mãi đang hiệu lực
    // ============================================================
    public List<String[]> getKhuyenMaiHieuLuc() {
        List<String[]> list = new ArrayList<>();
        list.add(new String[]{"", "Không giảm giá", "0", "Phần trăm"});

        String sql =
            "SELECT maKM, tenKM, giaTriKM, loaiKM " +
            "FROM KhuyenMai " +
            "WHERE trangThai = 1 " +
            "  AND GETDATE() BETWEEN ngayBatDau AND ngayKetThuc " +
            "ORDER BY tenKM";

        try {
            Connection con = getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    list.add(new String[]{
                        rs.getString("maKM"),
                        rs.getString("tenKM"),
                        String.valueOf(rs.getDouble("giaTriKM")),
                        rs.getString("loaiKM")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ============================================================
    //  THUNGAN: Thanh toán (gọi SP)
    // ============================================================
    public boolean thanhToan(String maHD, double chietKhau, String maKM) {
        String sql = "{call sp_ThanhToanHoaDon(?, ?, ?)}";
        String updBan =
            "UPDATE BanAn SET trangThai = N'Trống' " +
            "WHERE maBan = (SELECT maBan FROM HoaDon WHERE maHD = ?)";

        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);

            try (CallableStatement cs = con.prepareCall(sql)) {
                cs.setString(1, maHD);
                cs.setDouble(2, chietKhau);

                if (maKM == null || maKM.trim().isEmpty()) {
                    cs.setNull(3, Types.VARCHAR);
                } else {
                    cs.setString(3, maKM);
                }

                cs.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(updBan)) {
                ps.setString(1, maHD);
                ps.executeUpdate();
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;

        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ============================================================
    //  THUNGAN: Hủy hóa đơn (transaction)
    // ============================================================
    public boolean huyHoaDon(String maHD) {
        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE BanAn SET trangThai = N'Trống' " +
                    "WHERE maBan = (SELECT maBan FROM HoaDon WHERE maHD = ?)")) {
                ps.setString(1, maHD);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM HoaDonKhuyenMai WHERE maHD = ?")) {
                ps.setString(1, maHD);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM ChiTietHoaDon WHERE maHD = ?")) {
                ps.setString(1, maHD);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM HoaDon WHERE maHD = ?")) {
                ps.setString(1, maHD);
                ps.executeUpdate();
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;

        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ============================================================
    //  DASHBOARD: Doanh thu hôm nay
    // ============================================================
    public long getDoanhThuHomNay() {
        String sql =
            "SELECT ISNULL(SUM(tongTien), 0) AS dt FROM HoaDon " +
            "WHERE trangThaiThanhToan = N'Đã thanh toán' " +
            "  AND CAST(ngayGioThanhToan AS DATE) = CAST(GETDATE() AS DATE)";

        try {
            Connection con = getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return (long) rs.getDouble("dt");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ============================================================
    //  DASHBOARD: Tổng đơn hôm nay
    // ============================================================
    public int getTongDonHomNay() {
        String sql =
            "SELECT COUNT(*) AS tong FROM HoaDon " +
            "WHERE CAST(ngayGioLap AS DATE) = CAST(GETDATE() AS DATE)";

        try {
            Connection con = getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("tong");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ============================================================
    //  DASHBOARD: Nhân viên đang làm (trangThai = 1)
    // ============================================================
    public int getSoNhanVienDangLam() {
        String sql = "SELECT COUNT(*) AS tong FROM NhanVien WHERE trangThai = 1";

        try {
            Connection con = getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("tong");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ============================================================
    //  DASHBOARD: Tỷ lệ hủy %
    // ============================================================
    public double getTyLeHuy() {
        String sql =
            "SELECT CASE WHEN COUNT(*) = 0 THEN 0 " +
            "ELSE CAST(SUM(CASE WHEN trangThaiThanhToan = N'Hủy' THEN 1 ELSE 0 END) AS FLOAT) " +
            "     / COUNT(*) * 100 END AS tyLe " +
            "FROM HoaDon " +
            "WHERE CAST(ngayGioLap AS DATE) = CAST(GETDATE() AS DATE)";

        try {
            Connection con = getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getDouble("tyLe");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ============================================================
    //  DASHBOARD: Doanh thu 7 ngày qua [{ngayMs, doanhThu}]
    // ============================================================
    public List<long[]> getDoanhThu7Ngay() {
        List<long[]> list = new ArrayList<>();
        String sql =
            "SELECT CAST(ngayGioThanhToan AS DATE) AS ngay, " +
            "       ISNULL(SUM(tongTien), 0) AS dt " +
            "FROM HoaDon " +
            "WHERE trangThaiThanhToan = N'Đã thanh toán' " +
            "  AND ngayGioThanhToan >= DATEADD(DAY, -6, CAST(GETDATE() AS DATE)) " +
            "GROUP BY CAST(ngayGioThanhToan AS DATE) " +
            "ORDER BY ngay";

        try {
            Connection con = getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    list.add(new long[]{
                        rs.getDate("ngay").getTime(),
                        (long) rs.getDouble("dt")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ============================================================
    //  DASHBOARD: Top 5 món bán chạy [{tenMon, soLuong, doanhThu}]
    // ============================================================
    public List<String[]> getMonAnBanChay() {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT TOP 5 tenMonAn, SUM(soLuong) AS sl, SUM(doanhThu) AS dt " +
            "FROM vw_MonAnBanChay " +
            "GROUP BY tenMonAn " +
            "ORDER BY SUM(soLuong) DESC";

        try {
            Connection con = getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    list.add(new String[]{
                        rs.getString("tenMonAn"),
                        String.valueOf(rs.getInt("sl")),
                        String.valueOf((long) rs.getDouble("dt"))
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ============================================================
    //  DASHBOARD: Ca làm việc [{hoTen, caLam, trangThai, gioBD, tienKet, chenhLech}]
    // ============================================================
    public List<String[]> getCaLamViec() {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT nv.hoTenNV, nv.caLam, " +
            "  CASE WHEN nv.trangThai = 1 THEN N'Đang mở' ELSE N'Chưa mở' END AS trangThai, " +
            "  CASE nv.caLam WHEN N'Ca sáng' THEN '07:00' " +
            "                WHEN N'Ca chiều' THEN '14:00' " +
            "                WHEN N'Ca tối'   THEN '18:00' ELSE '--' END AS gioBatDau, " +
            "  ISNULL((SELECT SUM(h.tongTien) FROM HoaDon h " +
            "          WHERE h.maNV = nv.maNV " +
            "            AND CAST(h.ngayGioLap AS DATE) = CAST(GETDATE() AS DATE)), 0) AS tienKet " +
            "FROM NhanVien nv " +
            "WHERE nv.chucVu IN (N'Thu ngân', N'Nhân viên') " +
            "ORDER BY nv.caLam";

        try {
            Connection con = getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    list.add(new String[]{
                        rs.getString("hoTenNV"),
                        rs.getString("caLam") != null ? rs.getString("caLam") : "--",
                        rs.getString("trangThai"),
                        rs.getString("gioBatDau"),
                        String.valueOf((long) rs.getDouble("tienKet")),
                        "0"
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
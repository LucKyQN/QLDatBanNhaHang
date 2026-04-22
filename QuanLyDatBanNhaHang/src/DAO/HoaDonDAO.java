package DAO;

import connectDatabase.ConnectDB;
import Model.BanAnModel;
import Model.MonAnModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    private Connection getConnection() throws SQLException {
        ConnectDB.getInstance().connect();
        return ConnectDB.getInstance().getConnection();
    }

    public List<BanAnModel> getDanhSachBanChuaThanhToan() {
        List<BanAnModel> list = new ArrayList<>();

        String sql = "SELECT hd.maHD, b.maBan, b.tenBan, hd.tenKhachLe AS tenKhachHang, hd.ngayGioLap AS gioVao, " +
                "       ISNULL(p.tienCoc, 0) AS tienCoc, " +
                "       ISNULL((SELECT SUM(thanhTien) " +
                "               FROM ChiTietHoaDon " +
                "               WHERE maHD = hd.maHD AND ISNULL(trangThaiPhucVu, '') <> N'Hủy'), 0) AS tamTinh " +
                "FROM HoaDon hd " +
                "JOIN BanAn b ON hd.maBan = b.maBan " +
                "LEFT JOIN PhieuDatBan p ON hd.maPhieuDatBan = p.maPhieu " +
                "WHERE hd.trangThaiThanhToan = N'Chờ thanh toán' " +

                "UNION " +

                "SELECT hd.maHD, b.maBan, b.tenBan, hd.tenKhachLe AS tenKhachHang, hd.ngayGioLap AS gioVao, " +
                "       ISNULL(p.tienCoc, 0) AS tienCoc, " +
                "       ISNULL((SELECT SUM(thanhTien) " +
                "               FROM ChiTietHoaDon " +
                "               WHERE maHD = hd.maHD AND ISNULL(trangThaiPhucVu, '') <> N'Hủy'), 0) AS tamTinh " +
                "FROM HoaDon hd " +
                "JOIN ChiTietDatBan ct ON hd.maPhieuDatBan = ct.maPhieu " +
                "JOIN BanAn b ON ct.maBan = b.maBan " +
                "LEFT JOIN PhieuDatBan p ON hd.maPhieuDatBan = p.maPhieu " +
                "WHERE hd.trangThaiThanhToan = N'Chờ thanh toán'";

        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            java.util.Map<String, BanAnModel> map = new java.util.LinkedHashMap<>();

            while (rs.next()) {
                String maHD = rs.getString("maHD");
                String tenBan = rs.getString("tenBan");

                if (map.containsKey(maHD)) {
                    BanAnModel ban = map.get(maHD);
                    if (!ban.tenBan.contains(tenBan)) {
                        ban.tenBan += ", " + tenBan;
                    }
                } else {
                    BanAnModel ban = new BanAnModel();
                    ban.maHD = maHD;
                    ban.maBan = rs.getString("maBan");
                    ban.tenBan = tenBan;
                    ban.tenKH = rs.getString("tenKhachHang");

                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
                    ban.gioVao = rs.getTimestamp("gioVao") != null
                            ? sdf.format(rs.getTimestamp("gioVao"))
                            : "--:--";

                    ban.tienCoc = rs.getLong("tienCoc");
                    ban.tamTinh = rs.getLong("tamTinh");

                    map.put(maHD, ban);
                }
            }

            list.addAll(map.values());
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private String xoaTrungLapTenBan(String raw) {
        if (raw == null)
            return "Bàn vãng lai";
        java.util.Set<String> set = new java.util.LinkedHashSet<>(java.util.Arrays.asList(raw.split(", ")));
        return String.join(", ", set);
    }

    // Chi tiết món ăn theo maHD
    public List<MonAnModel> getChiTietHoaDon(String maHD) {
        List<MonAnModel> ds = new ArrayList<>();

        String sql = "SELECT c.ID_CTHD, c.maMonAn, m.tenMonAn, c.soLuong, c.donGia, c.thanhTien, c.trangThaiPhucVu "
                + "FROM ChiTietHoaDon c " + "JOIN MonAn m ON c.maMonAn = m.maMonAn "
                + "WHERE c.maHD = ? AND ISNULL(c.trangThaiPhucVu, '') <> N'Hủy'";

        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maHD);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                MonAnModel m = new MonAnModel();
                m.id_cthd = rs.getInt("ID_CTHD");
                m.maMonAn = rs.getString("maMonAn");
                m.tenMonAn = rs.getString("tenMonAn");
                m.soLuong = rs.getInt("soLuong");
                m.giaBan = (long) rs.getDouble("donGia");
                m.thanhTien = (long) rs.getDouble("thanhTien");
                m.trangThaiPhucVu = rs.getString("trangThaiPhucVu");
                ds.add(m);
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    public String getMaHoaDonDangPhucVuTheoBan(String maBan) {
        String sql = "SELECT TOP 1 maHD FROM ( " +
                "   SELECT h.maHD, h.ngayGioLap AS thoiGianTao " +
                "   FROM HoaDon h " +
                "   WHERE h.maBan = ? " +
                "     AND h.trangThaiThanhToan IN (N'Chưa thanh toán', N'Chờ thanh toán') " +
                "   UNION " +
                "   SELECT h.maHD, h.ngayGioLap AS thoiGianTao " +
                "   FROM HoaDon h " +
                "   JOIN ChiTietDatBan ct ON h.maPhieuDatBan = ct.maPhieu " +
                "   WHERE ct.maBan = ? " +
                "     AND h.trangThaiThanhToan IN (N'Chưa thanh toán', N'Chờ thanh toán') " +
                ") x " +
                "ORDER BY thoiGianTao DESC";

        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maBan);
            ps.setString(2, maBan);
            ResultSet rs = ps.executeQuery();

            String maHD = null;
            if (rs.next()) {
                maHD = rs.getString("maHD");
            }

            rs.close();
            ps.close();
            return maHD;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Khuyến mãi đang hiệu lực
    public List<String[]> getKhuyenMaiHieuLuc() {
        List<String[]> list = new ArrayList<>();
        list.add(new String[] { "NONE", "Không giảm giá", "0", "Phần trăm" });

        String sql = "SELECT maKM, tenKM, giaTriKM, loaiKM " + "FROM KhuyenMai "
                + "WHERE trangThai = 1 AND GETDATE() BETWEEN ngayBatDau AND ngayKetThuc " + "ORDER BY tenKM";

        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new String[] { rs.getString("maKM"), rs.getString("tenKM"),
                        String.valueOf(rs.getDouble("giaTriKM")), rs.getString("loaiKM") });
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thanh toán hóa đơn
    public boolean thanhToan(String maHD, double tongThanhToan, double giaTriGiam,
                             String maKM, double tienKhachDua, double tienThuaTraKhach) {
        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);

            double tongTienGoc = tinhTongTienChiTiet(con, maHD);

            if (tongThanhToan < 0) {
                tongThanhToan = 0;
            }
            if (giaTriGiam < 0) {
                giaTriGiam = 0;
            }

            String sql1 = "UPDATE HoaDon " +
                    "SET trangThaiThanhToan = N'Đã thanh toán', " +
                    "    ngayGioThanhToan = GETDATE(), " +
                    "    chietKhau = ?, " +
                    "    tongTien = ?, " +
                    "    tienKhachDua = ?, " +
                    "    tienThuaTraKhach = ? " +
                    "WHERE maHD = ?";

            PreparedStatement ps1 = con.prepareStatement(sql1);
            ps1.setDouble(1, giaTriGiam);
            ps1.setDouble(2, tongThanhToan);
            ps1.setDouble(3, tienKhachDua);
            ps1.setDouble(4, tienThuaTraKhach);
            ps1.setString(5, maHD);
            int r1 = ps1.executeUpdate();
            ps1.close();


            capNhatTrangThaiBanTheoHD(con, maHD, "Trống");
            int r2 = 1;

            PreparedStatement psDelete = con.prepareStatement("DELETE FROM HoaDonKhuyenMai WHERE maHD = ?");
            psDelete.setString(1, maHD);
            psDelete.executeUpdate();
            psDelete.close();

            if (maKM != null && !maKM.trim().isEmpty() && !"NONE".equalsIgnoreCase(maKM)) {
                PreparedStatement psInsert = con
                        .prepareStatement("INSERT INTO HoaDonKhuyenMai(maHD, maKM, giaTriGiam) VALUES (?, ?, ?)");
                psInsert.setString(1, maHD);
                psInsert.setString(2, maKM);
                psInsert.setDouble(3, giaTriGiam);
                psInsert.executeUpdate();
                psInsert.close();
            }
            try {
                PreparedStatement psGetKhach = con
                        .prepareStatement("SELECT tenKhachLe, sdtKhachLe FROM HoaDon WHERE maHD = ?");
                psGetKhach.setString(1, maHD);
                ResultSet rsKhach = psGetKhach.executeQuery();
                if (rsKhach.next()) {
                    String tenKhach = rsKhach.getString("tenKhachLe");
                    String sdtKhach = rsKhach.getString("sdtKhachLe");

                    if (sdtKhach != null && !sdtKhach.trim().isEmpty() && !sdtKhach.equalsIgnoreCase("Trống")) {

                        new KhachHangDAO().luuHoacCapNhatKhachHang(con, sdtKhach, tenKhach, (long) tongThanhToan);
                    }
                }
                rsKhach.close();
                psGetKhach.close();
            } catch (Exception ex) {
                System.err.println("Lỗi tự động lưu khách hàng: " + ex.getMessage());
            }
            if (r1 > 0 && r2 > 0) {
                con.commit();
                System.out.println("Thanh toán & giải phóng bàn thành công!");
                return true;
            } else {
                con.rollback();
                System.err.println("Lỗi: Không tìm thấy hóa đơn hoặc bàn để cập nhật.");
                return false;
            }

        } catch (SQLException e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("SQL Error: " + e.getMessage());
            return false;
        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Hủy hóa đơn
    public boolean huyHoaDon(String maHD) {
        try {
            Connection con = getConnection();
            con.setAutoCommit(false);
            try {
                PreparedStatement ps0 = con.prepareStatement("DELETE FROM HoaDonKhuyenMai WHERE maHD = ?");
                ps0.setString(1, maHD);
                ps0.executeUpdate();
                ps0.close();

                PreparedStatement ps1 = con.prepareStatement("DELETE FROM ChiTietHoaDon WHERE maHD = ?");
                ps1.setString(1, maHD);
                ps1.executeUpdate();
                ps1.close();

                capNhatTrangThaiBanTheoHD(con, maHD, "Trống");

                PreparedStatement ps2 = con.prepareStatement("DELETE FROM HoaDon WHERE maHD = ?");
                ps2.setString(1, maHD);
                ps2.executeUpdate();
                ps2.close();

                con.commit();
                return true;
            } catch (SQLException ex) {
                con.rollback();
                ex.printStackTrace();
                return false;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DASHBOARD
    public long getDoanhThuHomNay() {
        String sql = "SELECT ISNULL(SUM(tongTien), 0) AS dt " + "FROM HoaDon "
                + "WHERE trangThaiThanhToan = N'Đã thanh toán' "
                + "AND CAST(ngayGioThanhToan AS DATE) = CAST(GETDATE() AS DATE)";
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            long result = rs.next() ? (long) rs.getDouble("dt") : 0;
            rs.close();
            ps.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getTongDonHomNay() {
        String sql = "SELECT COUNT(*) AS tong FROM HoaDon "
                + "WHERE CAST(ngayGioLap AS DATE) = CAST(GETDATE() AS DATE)";
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            int result = rs.next() ? rs.getInt("tong") : 0;
            rs.close();
            ps.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getSoNhanVienDangLam() {
        String sql = "SELECT COUNT(*) AS tong FROM NhanVien WHERE trangThai = 1";
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            int result = rs.next() ? rs.getInt("tong") : 0;
            rs.close();
            ps.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public double getTyLeHuy() {
        String sql = "SELECT CASE WHEN COUNT(*) = 0 THEN 0 "
                + "ELSE CAST(SUM(CASE WHEN trangThaiThanhToan = N'Hủy' THEN 1 ELSE 0 END) AS FLOAT) / COUNT(*) * 100 END AS tyLe "
                + "FROM HoaDon " + "WHERE CAST(ngayGioLap AS DATE) = CAST(GETDATE() AS DATE)";
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            double result = rs.next() ? rs.getDouble("tyLe") : 0;
            rs.close();
            ps.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<String[]> getCaLamViec() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT nv.hoTenNV, nv.caLam, "
                + "CASE WHEN nv.trangThai = 1 THEN N'Đang mở' ELSE N'Chưa mở' END AS trangThai, "
                + "CASE nv.caLam WHEN N'Ca sáng' THEN '07:00' " + "              WHEN N'Ca chiều' THEN '14:00' "
                + "              WHEN N'Ca tối' THEN '18:00' ELSE '--' END AS gioBatDau, "
                + "ISNULL((SELECT SUM(h.tongTien) FROM HoaDon h " + "        WHERE h.maNV = nv.maNV "
                + "        AND CAST(h.ngayGioLap AS DATE) = CAST(GETDATE() AS DATE)), 0) AS tienKet "
                + "FROM NhanVien nv " + "WHERE nv.chucVu IN (N'Thu ngân', N'Nhân viên') " + "ORDER BY nv.caLam";
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new String[] { rs.getString("hoTenNV"),
                        rs.getString("caLam") != null ? rs.getString("caLam") : "--", rs.getString("trangThai"),
                        rs.getString("gioBatDau"), String.valueOf((long) rs.getDouble("tienKet")), "0" });
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void capNhatTrangThaiBanTheoHD(Connection con, String maHD, String trangThaiMoi) throws SQLException {
        String sql = "UPDATE BanAn SET trangThai = ? " +
                "WHERE maBan = (SELECT maBan FROM HoaDon WHERE maHD = ?) " +
                "   OR maBan IN ( " +
                "       SELECT ct.maBan " +
                "       FROM ChiTietDatBan ct " +
                "       WHERE ct.maPhieu = (SELECT maPhieuDatBan FROM HoaDon WHERE maHD = ?) " +
                "   )";

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, trangThaiMoi);
        ps.setString(2, maHD);
        ps.setString(3, maHD);
        ps.executeUpdate();
        ps.close();
    }

    private double tinhTongTienChiTiet(Connection con, String maHD) throws SQLException {
        String sql = "SELECT COALESCE(SUM(thanhTien), 0) AS tong FROM ChiTietHoaDon "
                + "WHERE maHD = ? AND ISNULL(trangThaiPhucVu, '') <> N'Hủy'";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, maHD);
        ResultSet rs = ps.executeQuery();
        double tong = 0;
        if (rs.next()) {
            tong = rs.getDouble("tong");
        }
        rs.close();
        ps.close();
        return tong;
    }

    // PHỤC VỤ: Thêm hoặc tăng số lượng món
    public boolean themHoacTangMon(String maHD, String maMonAn, int soLuongThem) {
        String sqlGia = "SELECT giaBan FROM MonAn WHERE maMonAn = ?";
        String sqlCheck = "SELECT soLuong FROM ChiTietHoaDon WHERE maHD = ? AND maMonAn = ?";
        String sqlUpdate = "UPDATE ChiTietHoaDon SET soLuong = soLuong + ?, thanhTien = (soLuong + ?) * donGia "
                + "WHERE maHD = ? AND maMonAn = ?";
        String sqlInsert = "INSERT INTO ChiTietHoaDon (maHD, maMonAn, soLuong, donGia, thanhTien, trangThaiPhucVu) "
                + "VALUES (?, ?, ?, ?, ?, N'Chưa lên')";
        String sqlCapNhatTong = "UPDATE HoaDon SET tongTien = "
                + "(SELECT ISNULL(SUM(thanhTien), 0) FROM ChiTietHoaDon WHERE maHD = ?) " + "WHERE maHD = ?";

        try {
            Connection con = getConnection();
            con.setAutoCommit(false);
            try {
                double donGia = 0;
                try (PreparedStatement ps = con.prepareStatement(sqlGia)) {
                    ps.setString(1, maMonAn);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        donGia = rs.getDouble(1);
                    } else {
                        con.rollback();
                        return false;
                    }
                    rs.close();
                }

                boolean daCoMon = false;
                try (PreparedStatement ps = con.prepareStatement(sqlCheck)) {
                    ps.setString(1, maHD);
                    ps.setString(2, maMonAn);
                    ResultSet rs = ps.executeQuery();
                    daCoMon = rs.next();
                    rs.close();
                }

                if (daCoMon) {
                    try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                        ps.setInt(1, soLuongThem);
                        ps.setInt(2, soLuongThem);
                        ps.setString(3, maHD);
                        ps.setString(4, maMonAn);
                        ps.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = con.prepareStatement(sqlInsert)) {
                        ps.setString(1, maHD);
                        ps.setString(2, maMonAn);
                        ps.setInt(3, soLuongThem);
                        ps.setDouble(4, donGia);
                        ps.setDouble(5, donGia * soLuongThem);
                        ps.executeUpdate();
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(sqlCapNhatTong)) {
                    ps.setString(1, maHD);
                    ps.setString(2, maHD);
                    ps.executeUpdate();
                }

                con.commit();
                return true;
            } catch (SQLException ex) {
                con.rollback();
                ex.printStackTrace();
                return false;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // PHỤC VỤ: Cập nhật số lượng món
    public boolean capNhatSoLuongMon(String maHD, String maMonAn, int soLuongMoi) {
        if (soLuongMoi <= 0)
            return xoaMonKhoiChiTiet(maHD, maMonAn);

        String sql = "UPDATE ChiTietHoaDon SET soLuong = ?, thanhTien = ? * donGia " + "WHERE maHD = ? AND maMonAn = ?";
        String sqlTong = "UPDATE HoaDon SET tongTien = "
                + "(SELECT ISNULL(SUM(thanhTien), 0) FROM ChiTietHoaDon WHERE maHD = ?) " + "WHERE maHD = ?";

        try {
            Connection con = getConnection();
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, soLuongMoi);
                    ps.setInt(2, soLuongMoi);
                    ps.setString(3, maHD);
                    ps.setString(4, maMonAn);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(sqlTong)) {
                    ps.setString(1, maHD);
                    ps.setString(2, maHD);
                    ps.executeUpdate();
                }

                con.commit();
                return true;
            } catch (SQLException ex) {
                con.rollback();
                ex.printStackTrace();
                return false;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean taoHoaDonMoi(String maHD, String maNV, String maBan, String tenKH, String sdt, int soNguoi,
            String maPhieuDatBan) {
        String sql = "INSERT INTO HoaDon "
                + "(maHD, maNV, maBan, ngayGioLap, trangThaiThanhToan, tongTien, tenKhachLe, sdtKhachLe, soLuongKhach, maPhieuDatBan) "
                + "VALUES (?, ?, ?, GETDATE(), N'Chưa thanh toán', 0, ?, ?, ?, ?)";

        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setString(1, maHD);
            stmt.setString(2, maNV);
            stmt.setString(3, maBan);
            stmt.setString(4, tenKH);
            stmt.setString(5, sdt);
            stmt.setInt(6, soNguoi);
            stmt.setString(7, maPhieuDatBan);

            int rows = stmt.executeUpdate();
            stmt.close();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // PHỤC VỤ: Xóa món khỏi hóa đơn
    public boolean xoaMonKhoiChiTiet(String maHD, String maMonAn) {
        String sql = "DELETE FROM ChiTietHoaDon WHERE maHD = ? AND maMonAn = ?";
        String sqlTong = "UPDATE HoaDon SET tongTien = "
                + "(SELECT ISNULL(SUM(thanhTien), 0) FROM ChiTietHoaDon WHERE maHD = ?) " + "WHERE maHD = ?";

        try {
            Connection con = getConnection();
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, maHD);
                    ps.setString(2, maMonAn);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(sqlTong)) {
                    ps.setString(1, maHD);
                    ps.setString(2, maHD);
                    ps.executeUpdate();
                }

                con.commit();
                return true;
            } catch (SQLException ex) {
                con.rollback();
                ex.printStackTrace();
                return false;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getMaHoaDonChuaThanhToanCuaBan(String maBan) {
        String sql = "SELECT maHD FROM HoaDon WHERE maBan = ? AND trangThaiThanhToan = N'Chưa thanh toán'";
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maBan);
            ResultSet rs = ps.executeQuery();

            String result = null;
            if (rs.next()) {
                result = rs.getString("maHD");
            }

            rs.close();
            ps.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Phục vụ đổi trạng thái món theo maHD + maMonAn
    public boolean capNhatTrangThaiMon(String maHD, String maMonAn, String trangThaiMoi) {
        String sql = "UPDATE ChiTietHoaDon SET trangThaiPhucVu = ? WHERE maHD = ? AND maMonAn = ?";
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, trangThaiMoi);
            ps.setString(2, maHD);
            ps.setString(3, maMonAn);

            boolean ok = ps.executeUpdate() > 0;
            ps.close();
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Phục vụ đổi trạng thái món theo ID_CTHD

    public boolean capNhatTrangThaiMon(int idCTHD, String trangThaiMoi) {
        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(false); // Bật Transaction an toàn

            // Bước Đổi trạng thái món thành Hủy
            String sql = "UPDATE ChiTietHoaDon SET trangThaiPhucVu = ? WHERE ID_CTHD = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, trangThaiMoi);
            ps.setInt(2, idCTHD);
            boolean ok = ps.executeUpdate() > 0;
            ps.close();

            // Bước Cập nhật lại tổng tiền của Hóa Đơn đó (loại bỏ món Hủy)
            if (ok) {
                String sqlTong = "UPDATE HoaDon SET tongTien = ("
                        + "SELECT ISNULL(SUM(thanhTien), 0) FROM ChiTietHoaDon "
                        + "WHERE maHD = (SELECT maHD FROM ChiTietHoaDon WHERE ID_CTHD = ?) "
                        + "AND ISNULL(trangThaiPhucVu, '') <> N'Hủy') "
                        + "WHERE maHD = (SELECT maHD FROM ChiTietHoaDon WHERE ID_CTHD = ?)";
                PreparedStatement psTong = con.prepareStatement(sqlTong);
                psTong.setInt(1, idCTHD);
                psTong.setInt(2, idCTHD);
                psTong.executeUpdate();
                psTong.close();
            }

            con.commit();
            return ok;
        } catch (Exception e) {
            if (con != null)
                try {
                    con.rollback();
                } catch (Exception ex) {
                }
            e.printStackTrace();
            return false;
        } finally {
            if (con != null)
                try {
                    con.setAutoCommit(true);
                } catch (Exception ex) {
                }
        }
    }

    public String[] getThongTinKhachVuaMo(String maBan) {
        // Dùng UNION để tìm hóa đơn: Lấy bàn chính (HoaDon) + Lấy bàn phụ
        // (ChiTietDatBan)
        String sql = "SELECT TOP 1 * FROM (" +
                "  SELECT tenKhachLe, sdtKhachLe, soLuongKhach, ngayGioLap AS thoiGianTao " +
                "  FROM HoaDon WHERE maBan = ? AND trangThaiThanhToan = N'Chưa thanh toán' " +
                "  UNION " +
                "  SELECT hd.tenKhachLe, hd.sdtKhachLe, hd.soLuongKhach, hd.ngayGioLap AS thoiGianTao " +
                "  FROM HoaDon hd JOIN ChiTietDatBan ct ON hd.maPhieuDatBan = ct.maPhieu " +
                "  WHERE ct.maBan = ? AND hd.trangThaiThanhToan = N'Chưa thanh toán' " +
                ") AS T ORDER BY thoiGianTao DESC";
        try {
            java.sql.Connection con = connectDatabase.ConnectDB.getInstance().getConnection();
            java.sql.PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maBan);
            ps.setString(2, maBan); // Truyền maBan cho cả 2 vế UNION
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
                return new String[] {
                        rs.getString("tenKhachLe"),
                        rs.getString("sdtKhachLe"),
                        rs.getString("soLuongKhach"),
                        rs.getTimestamp("thoiGianTao") != null ? sdf.format(rs.getTimestamp("thoiGianTao")) : "--:--"
                };
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<MonAnModel> getMonAnTheoBan(String maBan, String trangThaiBan) {
        List<MonAnModel> ds = new ArrayList<>();
        String sql = "";

        // Bàn đang có khách -> lấy từ ChiTietHoaDon
        if (trangThaiBan.equalsIgnoreCase("Có khách")) {
            sql = "SELECT m.tenMonAn, c.soLuong, c.donGia, c.thanhTien, c.trangThaiPhucVu " + "FROM ChiTietHoaDon c "
                    + "JOIN MonAn m ON c.maMonAn = m.maMonAn " + "JOIN HoaDon h ON c.maHD = h.maHD "
                    + "WHERE h.maBan = ? AND h.trangThaiThanhToan = N'Chưa thanh toán'";
        }
        // Bàn đã đặt -> lấy từ ChiTietDonDatMon
        else if (trangThaiBan.equalsIgnoreCase("Đã đặt")) {
            sql = "SELECT m.tenMonAn, ct.soLuong, m.giaBan, (ct.soLuong * m.giaBan) AS thanhTien "
                    + "FROM ChiTietDonDatMon ct " + "JOIN MonAn m ON ct.maMonAn = m.maMonAn "
                    + "JOIN DonDatMon d ON ct.maDon = d.maDon " + "WHERE d.maBan = ? AND d.trangThai = N'Chờ khách'";
        } else {
            return ds;
        }

        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maBan);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                MonAnModel mon = new MonAnModel();
                mon.tenMonAn = rs.getString("tenMonAn");
                mon.soLuong = rs.getInt("soLuong");
                mon.giaBan = rs.getLong(3);
                mon.thanhTien = rs.getLong("thanhTien");

                if (trangThaiBan.equalsIgnoreCase("Có khách")) {
                    mon.trangThaiPhucVu = rs.getString("trangThaiPhucVu");
                    if (mon.trangThaiPhucVu == null || mon.trangThaiPhucVu.trim().isEmpty()) {
                        mon.trangThaiPhucVu = "Chưa lên";
                    }
                } else {
                    mon.trangThaiPhucVu = "Chưa lên";
                }

                ds.add(mon);
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean copyMonAnTuPhieuSangHoaDon(String maPhieu, String maHD) {
        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);

            if (maPhieu == null || maPhieu.trim().isEmpty()) {
                con.commit();
                return true;
            }

            String sqlInsert = "INSERT INTO ChiTietHoaDon (maHD, maMonAn, soLuong, donGia, thanhTien, trangThaiPhucVu, ghiChu) "
                    +
                    "SELECT ?, x.maMonAn, SUM(x.soLuong) AS tongSoLuong, x.donGia, SUM(x.soLuong) * x.donGia AS thanhTien, N'Chưa lên', x.ghiChu "
                    +
                    "FROM ( " +
                    "    SELECT ct.maMonAn, ct.soLuong, m.giaBan AS donGia, ISNULL(ct.ghiChu, N'') AS ghiChu " +
                    "    FROM ChiTietDatBan ctdb " +
                    "    JOIN DonDatMon d ON d.maBan = ctdb.maBan " +
                    "    JOIN ChiTietDonDatMon ct ON ct.maDon = d.maDon " +
                    "    JOIN MonAn m ON m.maMonAn = ct.maMonAn " +
                    "    WHERE ctdb.maPhieu = ? " +
                    "      AND d.trangThai = N'Chờ khách' " +
                    ") x " +
                    "GROUP BY x.maMonAn, x.donGia, x.ghiChu";

            PreparedStatement psInsert = con.prepareStatement(sqlInsert);
            psInsert.setString(1, maHD);
            psInsert.setString(2, maPhieu);
            int rows = psInsert.executeUpdate();
            psInsert.close();

            // 2) Cập nhật tổng tiền hóa đơn
            String sqlTong = "UPDATE HoaDon " +
                    "SET tongTien = (SELECT ISNULL(SUM(thanhTien), 0) FROM ChiTietHoaDon WHERE maHD = ?) " +
                    "WHERE maHD = ?";
            PreparedStatement psTong = con.prepareStatement(sqlTong);
            psTong.setString(1, maHD);
            psTong.setString(2, maHD);
            psTong.executeUpdate();
            psTong.close();

            // 3) Đánh dấu tất cả đơn đặt món của các bàn trong phiếu là đã vào bàn
            String sqlUpdateDon = "UPDATE DonDatMon " +
                    "SET trangThai = N'Đã vào bàn' " +
                    "WHERE maBan IN (SELECT maBan FROM ChiTietDatBan WHERE maPhieu = ?) " +
                    "  AND trangThai = N'Chờ khách'";
            PreparedStatement psUpdateDon = con.prepareStatement(sqlUpdateDon);
            psUpdateDon.setString(1, maPhieu);
            psUpdateDon.executeUpdate();
            psUpdateDon.close();

            con.commit();

            System.out.println(">>> So dong mon copy sang hoa don: " + rows);
            return true; // vẫn true kể cả rows = 0 nếu khách không đặt món trước
        } catch (Exception e) {
            try {
                if (con != null)
                    con.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (con != null)
                    con.setAutoCommit(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Phục vụ yêu cầu thanh toán
    public boolean yeuCauThanhToan(String maHD, String maBan) {
        try {
            Connection con = getConnection();

            String sqlHD = "UPDATE HoaDon SET trangThaiThanhToan = N'Chờ thanh toán' WHERE maHD = ?";
            PreparedStatement ps1 = con.prepareStatement(sqlHD);
            ps1.setString(1, maHD);
            ps1.executeUpdate();
            ps1.close();

            String sqlBan = "UPDATE BanAn SET trangThai = N'Chờ thanh toán' WHERE maBan = ?";
            PreparedStatement ps2 = con.prepareStatement(sqlBan);
            ps2.setString(1, maBan);
            ps2.executeUpdate();
            ps2.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // PHỤC VỤ YÊU CẦU: CHUYỂN BÀN & GỘP BÀN

    // 1. Chuyển bàn
    public boolean chuyenBan(String maBanCu, String maBanMoi) {
        // Chỉ đổi mã bàn đối với Hóa Đơn đang 'Chưa thanh toán'
        String sql = "UPDATE HoaDon SET maBan = ? WHERE maBan = ? AND trangThaiThanhToan = N'Chưa thanh toán'";
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maBanMoi);
            ps.setString(2, maBanCu);

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Gộp bàn
    public boolean gopBan(String maBanBiGop, String maBanChinh) {
        // Lấy mã hóa đơn đang active của 2 bàn
        String maHDChinh = getMaHoaDonChuaThanhToanCuaBan(maBanChinh);
        String maHDBiGop = getMaHoaDonChuaThanhToanCuaBan(maBanBiGop);

        if (maHDChinh == null || maHDBiGop == null)
            return false;

        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(false); // Bật Transaction

            // BƯỚC Nếu 2 bàn có gọi CÙNG 1 MÓN -> Cộng dồn số lượng và thành tiền vào HD
            // Chính
            String sqlUpdateTrung = "UPDATE cChinh " + "SET cChinh.soLuong = cChinh.soLuong + cGop.soLuong, "
                    + "    cChinh.thanhTien = cChinh.thanhTien + cGop.thanhTien " + "FROM ChiTietHoaDon cChinh "
                    + "INNER JOIN ChiTietHoaDon cGop ON cChinh.maMonAn = cGop.maMonAn "
                    + "WHERE cChinh.maHD = ? AND cGop.maHD = ?";
            PreparedStatement ps1 = con.prepareStatement(sqlUpdateTrung);
            ps1.setString(1, maHDChinh);
            ps1.setString(2, maHDBiGop);
            ps1.executeUpdate();
            ps1.close();

            // BƯỚC Xóa những món trùng vừa được cộng dồn ở HD Bị Gộp
            String sqlDeleteTrung = "DELETE FROM ChiTietHoaDon WHERE maHD = ? AND maMonAn IN "
                    + "(SELECT maMonAn FROM ChiTietHoaDon WHERE maHD = ?)";
            PreparedStatement ps2 = con.prepareStatement(sqlDeleteTrung);
            ps2.setString(1, maHDBiGop);
            ps2.setString(2, maHDChinh);
            ps2.executeUpdate();
            ps2.close();

            // BƯỚC Những món CÒN LẠI (không trùng) -> Đổi ID Hóa đơn sang HD Chính
            String sqlMove = "UPDATE ChiTietHoaDon SET maHD = ? WHERE maHD = ?";
            PreparedStatement ps3 = con.prepareStatement(sqlMove);
            ps3.setString(1, maHDChinh);
            ps3.setString(2, maHDBiGop);
            ps3.executeUpdate();
            ps3.close();

            // BƯỚC Cập nhật lại TỔNG TIỀN và SỐ LƯỢNG KHÁCH cho HD Chính
            String sqlUpdateHDChinh = "UPDATE HoaDon SET "
                    + "tongTien = (SELECT ISNULL(SUM(thanhTien), 0) FROM ChiTietHoaDon WHERE maHD = ?), "
                    + "soLuongKhach = soLuongKhach + (SELECT soLuongKhach FROM HoaDon WHERE maHD = ?) "
                    + "WHERE maHD = ?";
            PreparedStatement ps4 = con.prepareStatement(sqlUpdateHDChinh);
            ps4.setString(1, maHDChinh);
            ps4.setString(2, maHDBiGop);
            ps4.setString(3, maHDChinh);
            ps4.executeUpdate();
            ps4.close();

            // BƯỚC Xóa rác (Khuyến mãi nếu có) của HD bị gộp để tránh dính khóa ngoại
            PreparedStatement ps5 = con.prepareStatement("DELETE FROM HoaDonKhuyenMai WHERE maHD = ?");
            ps5.setString(1, maHDBiGop);
            ps5.executeUpdate();
            ps5.close();

            // BƯỚC Xóa Hóa đơn rỗng của bàn bị gộp
            PreparedStatement ps6 = con.prepareStatement("DELETE FROM HoaDon WHERE maHD = ?");
            ps6.setString(1, maHDBiGop);
            ps6.executeUpdate();
            ps6.close();

            // Hoàn tất Transaction
            con.commit();
            return true;

        } catch (SQLException e) {
            try {
                if (con != null)
                    con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (con != null)
                    con.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public List<String[]> getDoanhThu7NgayGanNhat() {
        List<String[]> list = new ArrayList<>();

        String sql = "SELECT FORMAT(CAST(ngayGioThanhToan AS DATE), 'dd/MM') AS ngay, "
                + "       CAST(ISNULL(SUM(tongTien), 0) AS BIGINT) AS doanhThu " + "FROM HoaDon "
                + "WHERE trangThaiThanhToan = N'Đã thanh toán' "
                + "  AND CAST(ngayGioThanhToan AS DATE) >= CAST(DATEADD(DAY, -6, GETDATE()) AS DATE) "
                + "GROUP BY CAST(ngayGioThanhToan AS DATE) " + "ORDER BY CAST(ngayGioThanhToan AS DATE) ASC";

        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new String[] { rs.getString("ngay"), String.valueOf(rs.getLong("doanhThu")) });
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String[]> getTop5MonBanChay() {
        List<String[]> list = new ArrayList<>();

        String sql = "SELECT TOP 5 m.tenMonAn, " + "       SUM(ct.soLuong) AS tongSoLuong, "
                + "       CAST(SUM(ct.thanhTien) AS BIGINT) AS tongDoanhThu " + "FROM ChiTietHoaDon ct "
                + "JOIN MonAn m ON ct.maMonAn = m.maMonAn " + "JOIN HoaDon h ON ct.maHD = h.maHD "
                + "WHERE h.trangThaiThanhToan = N'Đã thanh toán' " + "  AND ISNULL(ct.trangThaiPhucVu, N'') <> N'Hủy' "
                + "GROUP BY m.tenMonAn " + "ORDER BY SUM(ct.soLuong) DESC, SUM(ct.thanhTien) DESC";

        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new String[] { rs.getString("tenMonAn"), String.valueOf(rs.getInt("tongSoLuong")),
                        String.valueOf(rs.getLong("tongDoanhThu")) });
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // LẤY LỊCH SỬ HÓA ĐƠN KÈM TÌM KIẾM THEO NGÀY

    public List<String[]> getLichSuHoaDonTheoNgay(java.util.Date tuNgay, java.util.Date denNgay) {
        List<String[]> list = new ArrayList<>();

        String topClause = (tuNgay == null && denNgay == null) ? "TOP 100" : "";

        String sql =
                "SELECT " + topClause + " * FROM ( " +
                        "   SELECT h.maHD, b.tenBan, h.tenKhachLe, h.ngayGioThanhToan, h.tongTien, h.trangThaiThanhToan " +
                        "   FROM HoaDon h " +
                        "   LEFT JOIN BanAn b ON h.maBan = b.maBan " +
                        "   WHERE h.trangThaiThanhToan IN (N'Đã thanh toán', N'Hủy') " +

                        (tuNgay != null && denNgay != null
                                ? " AND CAST(h.ngayGioThanhToan AS DATE) >= CAST(? AS DATE) " +
                                " AND CAST(h.ngayGioThanhToan AS DATE) <= CAST(? AS DATE) "
                                : "") +

                        "   UNION " +

                        "   SELECT h.maHD, b.tenBan, h.tenKhachLe, h.ngayGioThanhToan, h.tongTien, h.trangThaiThanhToan " +
                        "   FROM HoaDon h " +
                        "   JOIN ChiTietDatBan ct ON h.maPhieuDatBan = ct.maPhieu " +
                        "   JOIN BanAn b ON ct.maBan = b.maBan " +
                        "   WHERE h.trangThaiThanhToan IN (N'Đã thanh toán', N'Hủy') " +

                        (tuNgay != null && denNgay != null
                                ? " AND CAST(h.ngayGioThanhToan AS DATE) >= CAST(? AS DATE) " +
                                " AND CAST(h.ngayGioThanhToan AS DATE) <= CAST(? AS DATE) "
                                : "") +

                        ") X ORDER BY ngayGioThanhToan DESC";

        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            if (tuNgay != null && denNgay != null) {
                ps.setDate(1, new java.sql.Date(tuNgay.getTime()));
                ps.setDate(2, new java.sql.Date(denNgay.getTime()));
                ps.setDate(3, new java.sql.Date(tuNgay.getTime()));
                ps.setDate(4, new java.sql.Date(denNgay.getTime()));
            }

            ResultSet rs = ps.executeQuery();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm dd/MM/yyyy");

            java.util.Map<String, String[]> map = new java.util.LinkedHashMap<>();

            while (rs.next()) {
                String maHD = rs.getString("maHD");
                String tenBan = rs.getString("tenBan") != null ? rs.getString("tenBan") : "Mang về";
                String tenKhach = rs.getString("tenKhachLe") != null ? rs.getString("tenKhachLe") : "Khách vãng lai";
                String thoiGian = rs.getTimestamp("ngayGioThanhToan") != null
                        ? sdf.format(rs.getTimestamp("ngayGioThanhToan"))
                        : "";
                String tongTien = String.format("%,.0f", rs.getDouble("tongTien")).replace(",", ".") + " đ";
                String trangThai = rs.getString("trangThaiThanhToan");

                if (map.containsKey(maHD)) {
                    String[] old = map.get(maHD);
                    if (!old[1].contains(tenBan)) {
                        old[1] += ", " + tenBan;
                    }
                } else {
                    map.put(maHD, new String[] {
                            maHD,
                            tenBan,
                            tenKhach,
                            thoiGian,
                            tongTien,
                            trangThai
                    });
                }
            }

            list.addAll(map.values());

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public Object[] getThongTinChiTietHoaDonLichSu(String maHD) {

        Object[] result = new Object[8];

        String sql = "SELECT h.ngayGioLap, h.ngayGioThanhToan, nv.hoTenNV, "
                + "ISNULL(p.tienCoc, 0) AS tienCoc, "
                + "ISNULL(hk.giaTriGiam, 0) AS tienGiamGia, "
                + "ISNULL(km.tenKM, N'Không') AS tenKM, "
                + "ISNULL(h.tienKhachDua, 0) AS tienKhachDua, "
                + "ISNULL(h.tienThuaTraKhach, 0) AS tienThuaTraKhach "
                + "FROM HoaDon h "
                + "LEFT JOIN NhanVien nv ON h.maNV = nv.maNV "
                + "LEFT JOIN PhieuDatBan p ON h.maPhieuDatBan = p.maPhieu "
                + "LEFT JOIN HoaDonKhuyenMai hk ON h.maHD = hk.maHD "
                + "LEFT JOIN KhuyenMai km ON hk.maKM = km.maKM "
                + "WHERE h.maHD = ?";

        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maHD);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm dd/MM/yyyy");

                result[0] = rs.getTimestamp("ngayGioLap") != null
                        ? sdf.format(rs.getTimestamp("ngayGioLap"))
                        : "--:--";

                result[1] = rs.getTimestamp("ngayGioThanhToan") != null
                        ? sdf.format(rs.getTimestamp("ngayGioThanhToan"))
                        : "--:--";

                result[2] = rs.getString("hoTenNV") != null
                        ? rs.getString("hoTenNV")
                        : "Không xác định";

                result[3] = rs.getLong("tienCoc");
                result[4] = rs.getLong("tienGiamGia");
                result[5] = rs.getString("tenKM");
                result[6] = rs.getLong("tienKhachDua");
                result[7] = rs.getLong("tienThuaTraKhach");
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
}
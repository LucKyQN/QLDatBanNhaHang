package DAO;

import Entity.PhieuDatBan;
import connectDatabase.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PhieuDatBanDAO {

    private Connection getConnection() throws Exception {
        ConnectDB.getInstance().connect();
        return ConnectDB.getInstance().getConnection();
    }

    // Hàm thêm mới phiếu đặt bàn
    public boolean taoPhieuDatBan(PhieuDatBan phieu) {
        String sql = "INSERT INTO PhieuDatBan (maPhieu, tenKhachHang, soDienThoai, thoiGianDen, soLuongKhach, ghiChu, maBan, trangThai, ngayTao, tienMonDatTruoc, tienCoc) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, N'Chờ khách', GETDATE(), ?, ?)";
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, phieu.getMaPhieu());
            ps.setString(2, phieu.getTenKhachHang());
            ps.setString(3, phieu.getSoDienThoai());
            // Convert java.util.Date sang java.sql.Timestamp để lưu được cả ngày và giờ
            ps.setTimestamp(4, new Timestamp(phieu.getThoiGianDen().getTime()));
            ps.setInt(5, phieu.getSoLuongKhach());
            ps.setString(6, phieu.getGhiChu());
            ps.setString(7, phieu.getMaBan());
            ps.setDouble(8, phieu.getTienMonDatTruoc());
            ps.setDouble(9, phieu.getTienCoc());
            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 1. Lấy danh sách phiếu đang "Chờ khách" (Hôm nay)
    public java.util.List<PhieuDatBan> getDanhSachDatChoChuaCheckIn() {
        java.util.List<PhieuDatBan> list = new java.util.ArrayList<>();
        // JOIN với bảng BanAn để lấy cái tên bàn hiển thị cho đẹp
        String sql = "SELECT p.*, b.tenBan FROM PhieuDatBan p " + "JOIN BanAn b ON p.maBan = b.maBan "
                + "WHERE p.trangThai = N'Chờ khách' " + "ORDER BY p.thoiGianDen ASC";
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PhieuDatBan p = new PhieuDatBan();
                p.setMaPhieu(rs.getString("maPhieu"));
                p.setTenKhachHang(rs.getString("tenKhachHang"));
                p.setSoDienThoai(rs.getString("soDienThoai"));
                p.setThoiGianDen(rs.getTimestamp("thoiGianDen"));
                p.setSoLuongKhach(rs.getInt("soLuongKhach"));
                p.setGhiChu(rs.getString("ghiChu"));
                p.setMaBan(rs.getString("maBan"));
                p.setTrangThai(rs.getString("trangThai"));
                // Lấy thêm tên bàn từ câu JOIN
                p.setTenBan(rs.getString("tenBan"));
                p.setTienMonDatTruoc(rs.getDouble("tienMonDatTruoc"));
                p.setTienCoc(rs.getDouble("tienCoc"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Cập nhật trạng thái phiếu (VD: Khi khách tới thì đổi thành 'Đã đến')
    public boolean capNhatTrangThaiPhieu(String maPhieu, String trangThaiMoi) {
        String sql = "UPDATE PhieuDatBan SET trangThai = ? WHERE maPhieu = ?";
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, trangThaiMoi);
            ps.setString(2, maPhieu);
            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean kiemTraBanAnToanChoKhachVangLai(String maBan, int thoiGianDuKienAn_Phut) {
        // Ví dụ: thoiGianDuKienAn_Phut = 150 (2.5 tiếng)
        String sql = "SELECT thoiGianNhanBan FROM PhieuDatBan "
                + "WHERE maBan = ? AND trangThai = N'Chờ nhận bàn' "
                + "AND CAST(thoiGianNhanBan AS DATE) = CAST(GETDATE() AS DATE)";
        try {
            Connection con = ConnectDB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maBan);
            ResultSet rs = ps.executeQuery();

            long thoiGianAnXong = System.currentTimeMillis() + (thoiGianDuKienAn_Phut * 60 * 1000L);

            while (rs.next()) {
                long gioKhachDat = rs.getTimestamp("thoiGianNhanBan").getTime();
                if (thoiGianAnXong > gioKhachDat) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // LẤY DANH SÁCH ĐẶT BÀN CHO LỄ TÂN (CÓ BỘ LỌC VÀ TÌM KIẾM)
    public List<Object[]> getDanhSachDatBanChoLeTan(String boLocNgay, String tuKhoa) {
        List<Object[]> list = new ArrayList<>();

        // Chú ý: Thay đổi tên cột cho khớp với Database của bạn nếu cần
        StringBuilder sql = new StringBuilder(
                "SELECT p.maPhieu, k.tenKH, k.soDienThoai, b.tenBan, p.thoiGianNhanBan, p.tienCoc " +
                        "FROM PhieuDatBan p " +
                        "LEFT JOIN KhachHang k ON p.maKH = k.maKH " +
                        "LEFT JOIN BanAn b ON p.maBan = b.maBan " +
                        "WHERE p.trangThai = N'Chờ nhận bàn' "
        );

        // Lọc theo ngày
        if ("Hôm nay".equals(boLocNgay)) {
            sql.append("AND CAST(p.thoiGianNhanBan AS DATE) = CAST(GETDATE() AS DATE) ");
        } else if ("Ngày mai".equals(boLocNgay)) {
            sql.append("AND CAST(p.thoiGianNhanBan AS DATE) = CAST(DATEADD(day, 1, GETDATE()) AS DATE) ");
        }

        // Lọc theo từ khóa SĐT hoặc Tên
        if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            sql.append("AND (k.soDienThoai LIKE ? OR k.tenKH LIKE ?) ");
        }

        sql.append("ORDER BY p.thoiGianNhanBan ASC"); // Ưu tiên giờ đến sớm lên đầu

        try {
            Connection con = ConnectDB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql.toString());

            if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
                String searchPattern = "%" + tuKhoa.trim() + "%";
                ps.setString(1, searchPattern);
                ps.setString(2, searchPattern);
            }

            ResultSet rs = ps.executeQuery();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm dd/MM");

            while (rs.next()) {
                String gioDen = rs.getTimestamp("thoiGianNhanBan") != null ? sdf.format(rs.getTimestamp("thoiGianNhanBan")) : "";
                String tienCoc = String.format("%,.0f", rs.getDouble("tienCoc")).replace(",", ".") + "đ";

                list.add(new Object[]{
                        rs.getString("maPhieu"),
                        rs.getString("tenKH"),
                        rs.getString("soDienThoai"),
                        rs.getString("tenBan"),
                        gioDen,
                        tienCoc
                });
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
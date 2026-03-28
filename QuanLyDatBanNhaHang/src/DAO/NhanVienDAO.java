//
//package DAO;
//
//import Entity.NhanVien;
//import connectDatabase.ConnectDB;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//
//public class NhanVienDAO {
//
//    public NhanVien dangNhap(String username, String password) {
//        NhanVien nv = null;
//
//        String sql = "SELECT * FROM NhanVien WHERE username = ? AND password = ? AND trangThai = 1";
//
//        try {
//            Connection con = ConnectDB.getInstance().getConnection();
//            PreparedStatement stmt = con.prepareStatement(sql);
//            stmt.setString(1, username);
//            stmt.setString(2, password);
//
//            ResultSet rs = stmt.executeQuery();
//
//            if (rs.next()) {
//                nv = new NhanVien();
//                nv.setMaNV(rs.getString("maNV"));
//                nv.setHoTenNV(rs.getString("hoTenNV"));
//                nv.setNgaySinh(rs.getDate("ngaySinh"));
//                nv.setGioiTinh(rs.getString("gioiTinh"));
//                nv.setSoDienThoai(rs.getString("soDienThoai"));
//                nv.setDiaChi(rs.getString("diaChi"));
//                nv.setHeSoLuong(rs.getDouble("heSoLuong"));
//                nv.setCaLam(rs.getString("caLam"));
//                nv.setKhuVucQuanLy(rs.getString("khvuQuanLy"));
//                nv.setKhuVucPhucVu(rs.getString("khvuPhucVu"));
//                nv.setKhuVucTiepTan(rs.getString("khvuTiepTan"));
//                nv.setTrangThai(rs.getBoolean("trangThai"));
//
//                // thông tin đăng nhập gộp trong nhân viên
//                nv.setTenDangNhap(rs.getString("username"));
//                nv.setMatKhau(rs.getString("password"));
//                nv.setVaiTro(rs.getString("chucVu"));
//            }
//
//            rs.close();
//            stmt.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return nv;
//    }
//}
package DAO;

import Entity.NhanVien;
import connectDatabase.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {

    private Connection getConnection() throws Exception {
        ConnectDB.getInstance().connect();
        return ConnectDB.getInstance().getConnection();
    }

    public NhanVien dangNhap(String username, String password) {
        NhanVien nv = null;
        String sql = "SELECT * FROM NhanVien WHERE username = ? AND password = ? AND trangThai = 1";

        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nv = mapNhanVien(rs);
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return nv;
    }

    public List<NhanVien> getAllNhanVien() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien ORDER BY maNV";

        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapNhanVien(rs));
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean themNhanVien(NhanVien nv) {
        String sql = "INSERT INTO NhanVien (" +
                "maNV, hoTenNV, ngaySinh, gioiTinh, soDienThoai, diaChi, heSoLuong, " +
                "caLam, khvuQuanLy, khvuPhucVu, khvuTiepTan, username, password, chucVu, trangThai" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setString(1, nv.getMaNV());
            stmt.setString(2, nv.getHoTenNV());
            stmt.setDate(3, nv.getNgaySinh() != null ? new Date(nv.getNgaySinh().getTime()) : null);
            stmt.setString(4, nv.getGioiTinh());
            stmt.setString(5, nv.getSoDienThoai());
            stmt.setString(6, nv.getDiaChi());
            stmt.setDouble(7, nv.getHeSoLuong());
            stmt.setString(8, nv.getCaLam());
            stmt.setString(9, nv.getKhuVucQuanLy());
            stmt.setString(10, nv.getKhuVucPhucVu());
            stmt.setString(11, nv.getKhuVucTiepTan());
            stmt.setString(12, nv.getTenDangNhap());
            stmt.setString(13, nv.getMatKhau());
            stmt.setString(14, nv.getVaiTro());
            stmt.setBoolean(15, nv.isTrangThai());

            int rows = stmt.executeUpdate();
            stmt.close();

            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean suaNhanVien(NhanVien nv) {
        String sql = "UPDATE NhanVien SET " +
                "hoTenNV = ?, ngaySinh = ?, gioiTinh = ?, soDienThoai = ?, diaChi = ?, heSoLuong = ?, " +
                "caLam = ?, khvuQuanLy = ?, khvuPhucVu = ?, khvuTiepTan = ?, " +
                "username = ?, password = ?, chucVu = ?, trangThai = ? " +
                "WHERE maNV = ?";

        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setString(1, nv.getHoTenNV());
            stmt.setDate(2, nv.getNgaySinh() != null ? new Date(nv.getNgaySinh().getTime()) : null);
            stmt.setString(3, nv.getGioiTinh());
            stmt.setString(4, nv.getSoDienThoai());
            stmt.setString(5, nv.getDiaChi());
            stmt.setDouble(6, nv.getHeSoLuong());
            stmt.setString(7, nv.getCaLam());
            stmt.setString(8, nv.getKhuVucQuanLy());
            stmt.setString(9, nv.getKhuVucPhucVu());
            stmt.setString(10, nv.getKhuVucTiepTan());
            stmt.setString(11, nv.getTenDangNhap());
            stmt.setString(12, nv.getMatKhau());
            stmt.setString(13, nv.getVaiTro());
            stmt.setBoolean(14, nv.isTrangThai());
            stmt.setString(15, nv.getMaNV());

            int rows = stmt.executeUpdate();
            stmt.close();

            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean xoaMemNhanVien(String maNV) {
        String sql = "UPDATE NhanVien SET trangThai = 0 WHERE maNV = ?";

        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maNV);

            int rows = stmt.executeUpdate();
            stmt.close();

            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean tonTaiMaNV(String maNV) {
        String sql = "SELECT COUNT(*) FROM NhanVien WHERE maNV = ?";

        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maNV);

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

    public boolean tonTaiUsername(String username) {
        String sql = "SELECT COUNT(*) FROM NhanVien WHERE username = ?";

        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, username);

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

    public boolean tonTaiUsernameKhacMa(String username, String maNV) {
        String sql = "SELECT COUNT(*) FROM NhanVien WHERE username = ? AND maNV <> ?";

        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, maNV);

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

    private NhanVien mapNhanVien(ResultSet rs) throws Exception {
        NhanVien nv = new NhanVien();
        nv.setMaNV(rs.getString("maNV"));
        nv.setHoTenNV(rs.getString("hoTenNV"));
        nv.setNgaySinh(rs.getDate("ngaySinh"));
        nv.setGioiTinh(rs.getString("gioiTinh"));
        nv.setSoDienThoai(rs.getString("soDienThoai"));
        nv.setDiaChi(rs.getString("diaChi"));
        nv.setHeSoLuong(rs.getDouble("heSoLuong"));
        nv.setCaLam(rs.getString("caLam"));
        nv.setKhuVucQuanLy(rs.getString("khvuQuanLy"));
        nv.setKhuVucPhucVu(rs.getString("khvuPhucVu"));
        nv.setKhuVucTiepTan(rs.getString("khvuTiepTan"));
        nv.setTrangThai(rs.getBoolean("trangThai"));
        nv.setTenDangNhap(rs.getString("username"));
        nv.setMatKhau(rs.getString("password"));
        nv.setVaiTro(rs.getString("chucVu"));
        return nv;
    }
}
package DAO;

import Entity.MonAn;
import Model.BanAnModel;
import Model.MonAnModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Dữ liệu mẫu trong RAM — không cần JDBC.
 * Dùng {@link #demo()} để chạy GUI thử nghiệm.
 */
public class PhucVuServiceMemory implements PhucVuService {

    private final Map<String, MonAn> thucDonTheoMa = new LinkedHashMap<>();
    /** Thông tin bàn cố định (mã HĐ mở). */
    private final List<BanAnModel> banMoTa;
    /** Chi tiết: maHD → danh sách dòng (mutable). */
    private final Map<String, List<MonAnModel>> chiTiet = new LinkedHashMap<>();

    public static PhucVuServiceMemory demo() {
        return new PhucVuServiceMemory();
    }

    public PhucVuServiceMemory() {
        themMonMau("MA01", "Phở bò tái", 65_000);
        themMonMau("MA02", "Gỏi cuốn tôm thịt", 45_000);
        themMonMau("MA03", "Cơm chiên Dương Châu", 75_000);
        themMonMau("MA04", "Lẩu Thái chua cay", 320_000);
        themMonMau("MA05", "Trà đá", 5_000);
        themMonMau("MA06", "Nước ép cam", 35_000);

        banMoTa = new ArrayList<>();
        banMoTa.add(ban("B01", "Bàn 1 — Tầng 1", 4, "HD20260328001"));
        banMoTa.add(ban("B02", "Bàn 2 — Tầng 1", 2, "HD20260328002"));
        banMoTa.add(ban("VIP01", "Phòng VIP Hoa Mai", 10, "HD20260328003"));

        ghiChiTiet("HD20260328001", dong("MA01", 2), dong("MA05", 4));
        ghiChiTiet("HD20260328002", dong("MA02", 1), dong("MA03", 1));
        ghiChiTiet("HD20260328003", dong("MA04", 1), dong("MA06", 3));
    }

    private void themMonMau(String ma, String ten, long gia) {
        MonAn m = new MonAn();
        m.setMaMonAn(ma);
        m.setTenMon(ten);
        m.setGiaMon(gia);
        m.setDonVi("Phần");
        m.setTinhTrang(true);
        thucDonTheoMa.put(ma, m);
    }

    private static BanAnModel ban(String maBan, String tenBan, int sucChua, String maHD) {
        BanAnModel b = new BanAnModel();
        b.maBan = maBan;
        b.tenBan = tenBan;
        b.sucChua = sucChua;
        b.maHD = maHD;
        b.tamTinh = 0;
        return b;
    }

    private MonAnModel dong(String maMon, int sl) {
        MonAn cat = thucDonTheoMa.get(maMon);
        MonAnModel row = new MonAnModel();
        row.maMonAn = maMon;
        row.tenMonAn = cat.getTenMon();
        row.soLuong = sl;
        row.donGia = (long) cat.getGiaMon();
        row.thanhTien = row.donGia * sl;
        return row;
    }

    private void ghiChiTiet(String maHD, MonAnModel... rows) {
        List<MonAnModel> list = new ArrayList<>();
        for (MonAnModel r : rows) {
            list.add(copyRow(r));
        }
        chiTiet.put(maHD, list);
    }

    private static MonAnModel copyRow(MonAnModel src) {
        MonAnModel d = new MonAnModel();
        d.maMonAn = src.maMonAn;
        d.tenMonAn = src.tenMonAn;
        d.soLuong = src.soLuong;
        d.donGia = src.donGia;
        d.thanhTien = src.thanhTien;
        return d;
    }

    private long tamTinh(String maHD) {
        List<MonAnModel> list = chiTiet.get(maHD);
        if (list == null) {
            return 0;
        }
        long s = 0;
        for (MonAnModel m : list) {
            s += m.thanhTien;
        }
        return s;
    }

    @Override
    public synchronized List<BanAnModel> getDanhSachBanChuaThanhToan() {
        List<BanAnModel> out = new ArrayList<>();
        for (BanAnModel b : banMoTa) {
            BanAnModel c = new BanAnModel();
            c.maBan = b.maBan;
            c.tenBan = b.tenBan;
            c.sucChua = b.sucChua;
            c.maHD = b.maHD;
            c.tamTinh = tamTinh(b.maHD);
            out.add(c);
        }
        return out;
    }

    @Override
    public synchronized List<MonAnModel> getChiTietHoaDon(String maHD) {
        List<MonAnModel> list = chiTiet.get(maHD);
        if (list == null) {
            return new ArrayList<>();
        }
        List<MonAnModel> out = new ArrayList<>();
        for (MonAnModel m : list) {
            out.add(copyRow(m));
        }
        return out;
    }

    @Override
    public synchronized boolean themHoacTangMon(String maHD, String maMonAn, int soLuongThem) {
        if (soLuongThem <= 0 || maHD == null || maMonAn == null) {
            return false;
        }
        MonAn cat = thucDonTheoMa.get(maMonAn);
        if (cat == null || !cat.isTinhTrang()) {
            return false;
        }
        List<MonAnModel> list = chiTiet.computeIfAbsent(maHD, k -> new ArrayList<>());
        long donGia = (long) cat.getGiaMon();
        for (MonAnModel row : list) {
            if (maMonAn.equals(row.maMonAn)) {
                row.soLuong += soLuongThem;
                row.thanhTien = row.donGia * row.soLuong;
                return true;
            }
        }
        MonAnModel n = new MonAnModel();
        n.maMonAn = maMonAn;
        n.tenMonAn = cat.getTenMon();
        n.soLuong = soLuongThem;
        n.donGia = donGia;
        n.thanhTien = donGia * soLuongThem;
        list.add(n);
        return true;
    }

    @Override
    public synchronized boolean capNhatSoLuongMon(String maHD, String maMonAn, int soLuongMoi) {
        if (soLuongMoi <= 0) {
            return xoaMonKhoiChiTiet(maHD, maMonAn);
        }
        List<MonAnModel> list = chiTiet.get(maHD);
        if (list == null) {
            return false;
        }
        for (MonAnModel row : list) {
            if (maMonAn.equals(row.maMonAn)) {
                row.soLuong = soLuongMoi;
                row.thanhTien = row.donGia * row.soLuong;
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean xoaMonKhoiChiTiet(String maHD, String maMonAn) {
        List<MonAnModel> list = chiTiet.get(maHD);
        if (list == null) {
            return false;
        }
        return list.removeIf(row -> maMonAn.equals(row.maMonAn));
    }

    @Override
    public synchronized List<MonAn> getMonAnDangPhucVu() {
        return thucDonTheoMa.values().stream()
                .filter(MonAn::isTinhTrang)
                .map(m -> {
                    MonAn c = new MonAn();
                    c.setMaMonAn(m.getMaMonAn());
                    c.setTenMon(m.getTenMon());
                    c.setGiaMon(m.getGiaMon());
                    c.setDonVi(m.getDonVi());
                    c.setTinhTrang(m.isTinhTrang());
                    return c;
                })
                .collect(Collectors.toList());
    }
}

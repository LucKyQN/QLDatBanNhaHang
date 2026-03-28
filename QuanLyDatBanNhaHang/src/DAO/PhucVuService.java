package DAO;

import Entity.MonAn;
import Model.BanAnModel;
import Model.MonAnModel;

import java.util.List;

/** Trừu tượng hóa dữ liệu phục vụ: DB hoặc bộ nhớ (demo). */
public interface PhucVuService {

    List<BanAnModel> getDanhSachBanChuaThanhToan();

    List<MonAnModel> getChiTietHoaDon(String maHD);

    boolean themHoacTangMon(String maHD, String maMonAn, int soLuongThem);

    boolean capNhatSoLuongMon(String maHD, String maMonAn, int soLuongMoi);

    boolean xoaMonKhoiChiTiet(String maHD, String maMonAn);

    /** Món đang kinh doanh (đổ vào combo thêm món). */
    List<MonAn> getMonAnDangPhucVu();
}

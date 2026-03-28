package DAO;

import Entity.MonAn;
import Model.BanAnModel;
import Model.MonAnModel;

import java.util.List;
import java.util.stream.Collectors;

/** Kết nối SQL Server qua HoaDonDAO / MonAnDAO. */
public class PhucVuServiceDb implements PhucVuService {

    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final MonAnDAO monAnDAO = new MonAnDAO();

    @Override
    public List<BanAnModel> getDanhSachBanChuaThanhToan() {
        return hoaDonDAO.getDanhSachBanChuaThanhToan();
    }

    @Override
    public List<MonAnModel> getChiTietHoaDon(String maHD) {
        return hoaDonDAO.getChiTietHoaDon(maHD);
    }

    @Override
    public boolean themHoacTangMon(String maHD, String maMonAn, int soLuongThem) {
        return hoaDonDAO.themHoacTangMon(maHD, maMonAn, soLuongThem);
    }

    @Override
    public boolean capNhatSoLuongMon(String maHD, String maMonAn, int soLuongMoi) {
        return hoaDonDAO.capNhatSoLuongMon(maHD, maMonAn, soLuongMoi);
    }

    @Override
    public boolean xoaMonKhoiChiTiet(String maHD, String maMonAn) {
        return hoaDonDAO.xoaMonKhoiChiTiet(maHD, maMonAn);
    }

    @Override
    public List<MonAn> getMonAnDangPhucVu() {
        return monAnDAO.getAllMonAn().stream()
                .filter(MonAn::isTinhTrang)
                .collect(Collectors.toList());
    }
}

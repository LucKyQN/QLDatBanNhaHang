package Entity;

import java.util.Objects;

public class MonAnKhuyenMai {
	private int soLuongToiThieu;
	private KhuyenMai khuyenMai;
	private MonAn monAn;
	private double phanTram;

	public MonAnKhuyenMai() {
	}

	public MonAnKhuyenMai(int soLuongToiThieu, KhuyenMai khuyenMai, MonAn monAn, double phanTram) {
		this.soLuongToiThieu = soLuongToiThieu;
		this.khuyenMai = khuyenMai;
		this.monAn = monAn;
		this.phanTram = phanTram;
	}

	public int getSoLuongToiThieu() {
		return soLuongToiThieu;
	}

	public void setSoLuongToiThieu(int soLuongToiThieu) {
		this.soLuongToiThieu = soLuongToiThieu;
	}

	public KhuyenMai getKhuyenMai() {
		return khuyenMai;
	}

	public void setKhuyenMai(KhuyenMai khuyenMai) {
		this.khuyenMai = khuyenMai;
	}

	public MonAn getMonAn() {
		return monAn;
	}

	public void setMonAn(MonAn monAn) {
		this.monAn = monAn;
	}

	public double getPhanTram() {
		return phanTram;
	}

	public void setPhanTram(double phanTram) {
		this.phanTram = phanTram;
	}

	public void sanPhamKM() {
	}

	public double tinhPhanTramGiamGia() {
		return phanTram;
	}

	@Override
	public String toString() {
		return "MonAnKhuyenMai{" + "soLuongToiThieu=" + soLuongToiThieu + ", monAn="
				+ (monAn != null ? monAn.getMaMonAn() : null) + ", khuyenMai="
				+ (khuyenMai != null ? khuyenMai.getMaKM() : null) + ", phanTram=" + phanTram + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof MonAnKhuyenMai))
			return false;
		MonAnKhuyenMai that = (MonAnKhuyenMai) o;
		return Objects.equals(monAn, that.monAn) && Objects.equals(khuyenMai, that.khuyenMai);
	}

	@Override
	public int hashCode() {
		return Objects.hash(monAn, khuyenMai);
	}
}
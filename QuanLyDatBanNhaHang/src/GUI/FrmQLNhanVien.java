
package GUI;

import DAO.NhanVienDAO;
import Entity.NhanVien;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FrmQLNhanVien extends JPanel {

	private final NhanVienDAO dao = new NhanVienDAO();

	private JTable tblNhanVien;
	private DefaultTableModel model;
	private List<NhanVien> dsNhanVien = new ArrayList<>();

	public FrmQLNhanVien() {
		initUI();
		loadTable();
	}

	private void initUI() {
		setLayout(new BorderLayout());
		setBackground(new Color(248, 248, 248));

		JPanel root = new JPanel(new BorderLayout(16, 16));
		root.setBackground(new Color(248, 248, 248));
		root.setBorder(new EmptyBorder(20, 24, 24, 24));

		root.add(createTopBar(), BorderLayout.NORTH);
		root.add(createTablePanel(), BorderLayout.CENTER);

		add(root, BorderLayout.CENTER);
	}

	private JPanel createTopBar() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true), new EmptyBorder(16, 16, 16, 16)));

		JLabel lblTitle = new JLabel("Quản lý nhân sự");
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));

		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		btnPanel.setOpaque(false);

		JButton btnThem = new JButton("Thêm");
		JButton btnSua = new JButton("Sửa");
		JButton btnXoaMem = new JButton("Xóa mềm");
		JButton btnXoaHoanToan = new JButton("Xóa hoàn toàn"); // Thay nút Làm mới

		btnThem.addActionListener(e -> moDialogThem());
		btnSua.addActionListener(e -> moDialogSua());
		btnXoaMem.addActionListener(e -> xoaMemNhanVien());
		btnXoaHoanToan.addActionListener(e -> xoaHoanToanNhanVien()); // Sự kiện nút Xóa hoàn toàn

		btnPanel.add(btnXoaHoanToan);
		btnPanel.add(btnXoaMem);
		btnPanel.add(btnSua);
		btnPanel.add(btnThem);

		panel.add(lblTitle, BorderLayout.WEST);
		panel.add(btnPanel, BorderLayout.EAST);

		return panel;
	}

	private JPanel createTablePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true), new EmptyBorder(16, 16, 16, 16)));

		model = new DefaultTableModel(
				new String[] { "Mã NV", "Họ tên", "Ngày sinh", "Giới tính", "SĐT", "Vai trò", "Ca làm", "Trạng thái" },
				0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tblNhanVien = new JTable(model);
		tblNhanVien.setRowHeight(30);
		tblNhanVien.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblNhanVien.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
		tblNhanVien.setFont(new Font("Segoe UI", Font.PLAIN, 13));

		JScrollPane scrollPane = new JScrollPane(tblNhanVien);

		panel.add(scrollPane, BorderLayout.CENTER);
		return panel;
	}

	private void loadTable() {
		model.setRowCount(0);
		dsNhanVien = dao.getAllNhanVien();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (NhanVien nv : dsNhanVien) {
			model.addRow(new Object[] { nv.getMaNV(), nv.getHoTenNV(),
					nv.getNgaySinh() != null ? sdf.format(nv.getNgaySinh()) : "", nv.getGioiTinh(), nv.getSoDienThoai(),
					nv.getVaiTro(), nv.getCaLam(), nv.isTrangThai() ? "Đang hoạt động" : "Ngưng hoạt động" });
		}
	}

	private NhanVien getNhanVienDangChon() {
		int row = tblNhanVien.getSelectedRow();
		if (row < 0 || row >= dsNhanVien.size())
			return null;
		return dsNhanVien.get(row);
	}

	private void moDialogThem() {
		Window window = SwingUtilities.getWindowAncestor(this);
		Frame owner = window instanceof Frame ? (Frame) window : null;

		NhanVienDialog dialog = new NhanVienDialog(owner, dao, null);
		dialog.setVisible(true);

		if (dialog.isSucceeded()) {
			loadTable();
		}
	}

	private void moDialogSua() {
		NhanVien nv = getNhanVienDangChon();
		if (nv == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa.");
			return;
		}

		Window window = SwingUtilities.getWindowAncestor(this);
		Frame owner = window instanceof Frame ? (Frame) window : null;

		NhanVienDialog dialog = new NhanVienDialog(owner, dao, nv);
		dialog.setVisible(true);

		if (dialog.isSucceeded()) {
			loadTable();
		}
	}

	private void xoaMemNhanVien() {
		NhanVien nv = getNhanVienDangChon();
		if (nv == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa mềm.");
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(this,
				"Xác nhận xóa mềm nhân viên " + nv.getMaNV() + " - " + nv.getHoTenNV() + "?", "Xóa mềm nhân viên",
				JOptionPane.YES_NO_OPTION);

		if (confirm != JOptionPane.YES_OPTION)
			return;

		if (dao.xoaMemNhanVien(nv.getMaNV())) {
			JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái nghỉ làm (Ngưng hoạt động).");
			loadTable();
		} else {
			JOptionPane.showMessageDialog(this, "Xóa mềm thất bại.");
		}
	}

	// HÀM MỚI: XÓA HOÀN TOÀN NHÂN VIÊN
	private void xoaHoanToanNhanVien() {
		NhanVien nv = getNhanVienDangChon();
		if (nv == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa hoàn toàn.");
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(this,
				"CẢNH BÁO: Xóa hoàn toàn nhân viên " + nv.getMaNV() + " - " + nv.getHoTenNV() + "?\n"
						+ "Hành động này sẽ xóa dữ liệu vĩnh viễn và không thể hoàn tác!",
				"Xóa hoàn toàn nhân viên", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if (confirm != JOptionPane.YES_OPTION)
			return;

		if (dao.xoaHoanToanNhanVien(nv.getMaNV())) {
			JOptionPane.showMessageDialog(this, "Đã xóa vĩnh viễn nhân viên khỏi hệ thống.");
			loadTable();
		} else {
			JOptionPane.showMessageDialog(this,
					"Xóa thất bại! Nhân viên này có thể đang liên kết với Hóa Đơn hoặc dữ liệu khác.", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// ======================= DIALOG CRUD =======================
	static class NhanVienDialog extends JDialog {

		private final NhanVienDAO dao;
		private final NhanVien nhanVienSua;
		private boolean succeeded = false;

		private JTextField txtMaNV;
		private JTextField txtHoTen;
		private JTextField txtNgaySinh;
		private JComboBox<String> cboGioiTinh;
		private JTextField txtSoDienThoai;
		private JTextField txtDiaChi;
		private JTextField txtHeSoLuong;
		private JComboBox<String> cboCaLam;
		private JComboBox<String> cboKhuVucQuanLy;
		private JComboBox<String> cboKhuVucPhucVu;
		private JComboBox<String> cboKhuVucTiepTan;
		private JTextField txtUsername;
		private JPasswordField txtPassword;
		private JComboBox<String> cboVaiTro;
		private JCheckBox chkTrangThai;

		public NhanVienDialog(Frame owner, NhanVienDAO dao, NhanVien nhanVienSua) {
			super(owner, true);
			this.dao = dao;
			this.nhanVienSua = nhanVienSua;

			setTitle(nhanVienSua == null ? "Thêm nhân viên" : "Sửa nhân viên");
			initUI();

			if (nhanVienSua != null) {
				doDuLieuLenForm();
			}

			pack();
			setSize(980, 520);
			setLocationRelativeTo(owner);
		}

		public boolean isSucceeded() {
			return succeeded;
		}

		private void initUI() {
			JPanel root = new JPanel(new BorderLayout(16, 16));
			root.setBorder(new EmptyBorder(20, 20, 20, 20));
			root.setBackground(Color.WHITE);

			JPanel fields = new JPanel(new GridLayout(5, 3, 12, 12));
			fields.setOpaque(false);

			txtMaNV = new JTextField();
			txtHoTen = new JTextField();
			txtNgaySinh = new JTextField();

			cboGioiTinh = new JComboBox<>(new String[] { "Nam", "Nữ" });

			txtSoDienThoai = new JTextField();
			txtDiaChi = new JTextField();
			txtHeSoLuong = new JTextField();

			cboCaLam = new JComboBox<>(new String[] { "Ca sáng", "Ca chiều", "Ca tối" });

			cboKhuVucQuanLy = new JComboBox<>(new String[] { "", "Khu A", "Khu B", "Khu C", "Sảnh chính", "VIP" });
			cboKhuVucPhucVu = new JComboBox<>(new String[] { "", "Khu A", "Khu B", "Khu C", "Sảnh chính", "VIP" });
			cboKhuVucTiepTan = new JComboBox<>(new String[] { "", "Quầy 1", "Quầy 2", "Sảnh chính", "Lối vào VIP" });

			cboKhuVucQuanLy.setEditable(true);
			cboKhuVucPhucVu.setEditable(true);
			cboKhuVucTiepTan.setEditable(true);

			txtUsername = new JTextField();
			txtPassword = new JPasswordField();

			cboVaiTro = new JComboBox<>(new String[] { "Quản lý", "Thu ngân", "Nhân viên", "Lễ tân", "Phục vụ" });
			chkTrangThai = new JCheckBox("Đang hoạt động");
			chkTrangThai.setSelected(true);
			chkTrangThai.setOpaque(false);

			fields.add(createField("Mã NV", txtMaNV));
			fields.add(createField("Họ tên", txtHoTen));
			fields.add(createField("Ngày sinh (yyyy-MM-dd)", txtNgaySinh));

			fields.add(createField("Giới tính", cboGioiTinh));
			fields.add(createField("Số điện thoại", txtSoDienThoai));
			fields.add(createField("Địa chỉ", txtDiaChi));

			fields.add(createField("Hệ số lương", txtHeSoLuong));
			fields.add(createField("Ca làm", cboCaLam));
			fields.add(createField("Vai trò", cboVaiTro));

			fields.add(createField("KV Quản lý", cboKhuVucQuanLy));
			fields.add(createField("KV Phục vụ", cboKhuVucPhucVu));
			fields.add(createField("KV Tiếp tân", cboKhuVucTiepTan));

			fields.add(createField("Tên đăng nhập", txtUsername));
			fields.add(createField("Mật khẩu", txtPassword));
			fields.add(createField("Trạng thái", chkTrangThai));

			JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
			btnPanel.setOpaque(false);

			JButton btnHuy = new JButton("Hủy");
			JButton btnLuu = new JButton(nhanVienSua == null ? "Thêm" : "Lưu");

			btnHuy.addActionListener(e -> dispose());
			btnLuu.addActionListener(e -> luuNhanVien());

			btnPanel.add(btnHuy);
			btnPanel.add(btnLuu);

			root.add(fields, BorderLayout.CENTER);
			root.add(btnPanel, BorderLayout.SOUTH);

			setContentPane(root);
		}

		private JPanel createField(String label, JComponent comp) {
			JPanel p = new JPanel(new BorderLayout(6, 6));
			p.setOpaque(false);

			JLabel lb = new JLabel(label);
			lb.setFont(new Font("Segoe UI", Font.PLAIN, 13));

			p.add(lb, BorderLayout.NORTH);
			p.add(comp, BorderLayout.CENTER);

			return p;
		}

		private void doDuLieuLenForm() {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			txtMaNV.setText(nhanVienSua.getMaNV());
			txtMaNV.setEditable(false);

			txtHoTen.setText(nhanVienSua.getHoTenNV());
			txtNgaySinh.setText(nhanVienSua.getNgaySinh() != null ? sdf.format(nhanVienSua.getNgaySinh()) : "");
			cboGioiTinh.setSelectedItem(nhanVienSua.getGioiTinh());
			txtSoDienThoai.setText(nhanVienSua.getSoDienThoai());
			txtDiaChi.setText(nhanVienSua.getDiaChi());
			txtHeSoLuong.setText(String.valueOf(nhanVienSua.getHeSoLuong()));
			cboCaLam.setSelectedItem(nhanVienSua.getCaLam());
			cboKhuVucQuanLy.setSelectedItem(nhanVienSua.getKhuVucQuanLy());
			cboKhuVucPhucVu.setSelectedItem(nhanVienSua.getKhuVucPhucVu());
			cboKhuVucTiepTan.setSelectedItem(nhanVienSua.getKhuVucTiepTan());
			txtUsername.setText(nhanVienSua.getTenDangNhap());
			txtPassword.setText(nhanVienSua.getMatKhau());
			cboVaiTro.setSelectedItem(nhanVienSua.getVaiTro());
			chkTrangThai.setSelected(nhanVienSua.isTrangThai());
		}

		private void luuNhanVien() {
			try {
				NhanVien nv = layDuLieuForm();

				if (nhanVienSua == null) {
					if (dao.tonTaiMaNV(nv.getMaNV())) {
						JOptionPane.showMessageDialog(this, "Mã nhân viên đã tồn tại.");
						return;
					}
					if (dao.tonTaiUsername(nv.getTenDangNhap())) {
						JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại.");
						return;
					}

					if (dao.themNhanVien(nv)) {
						JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công.");
						succeeded = true;
						dispose();
					} else {
						JOptionPane.showMessageDialog(this, "Thêm nhân viên thất bại.");
					}
				} else {
					if (dao.tonTaiUsernameKhacMa(nv.getTenDangNhap(), nv.getMaNV())) {
						JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại ở nhân viên khác.");
						return;
					}

					if (dao.suaNhanVien(nv)) {
						JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công.");
						succeeded = true;
						dispose();
					} else {
						JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thất bại.");
					}
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
		}

		private NhanVien layDuLieuForm() throws Exception {
			String maNV = txtMaNV.getText().trim();
			String hoTen = txtHoTen.getText().trim();
			String ngaySinh = txtNgaySinh.getText().trim();
			String sdt = txtSoDienThoai.getText().trim();
			String diaChi = txtDiaChi.getText().trim();
			String heSoLuongText = txtHeSoLuong.getText().trim();
			String username = txtUsername.getText().trim();
			String password = new String(txtPassword.getPassword()).trim();

			if (maNV.isEmpty() || hoTen.isEmpty() || ngaySinh.isEmpty() || sdt.isEmpty() || username.isEmpty()
					|| password.isEmpty() || heSoLuongText.isEmpty()) {
				throw new Exception("Vui lòng nhập đầy đủ thông tin bắt buộc.");
			}

			if (!sdt.matches("\\d{10,11}")) {
				throw new Exception("Số điện thoại không hợp lệ (Phải là 10-11 số).");
			}

			double heSoLuong;
			try {
				heSoLuong = Double.parseDouble(heSoLuongText);
			} catch (Exception e) {
				throw new Exception("Hệ số lương phải là số hợp lệ.");
			}

			if (heSoLuong <= 0) {
				throw new Exception("Hệ số lương phải lớn hơn 0.");
			}

			java.util.Date parsedDate;
			try {
				parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(ngaySinh);
			} catch (Exception e) {
				throw new Exception("Ngày sinh không đúng định dạng (yyyy-MM-dd).");
			}

			NhanVien nv = new NhanVien();
			nv.setMaNV(maNV);
			nv.setHoTenNV(hoTen);
			nv.setNgaySinh(parsedDate);
			nv.setGioiTinh(cboGioiTinh.getSelectedItem().toString());
			nv.setSoDienThoai(sdt);
			nv.setDiaChi(diaChi);
			nv.setHeSoLuong(heSoLuong);
			nv.setCaLam(cboCaLam.getSelectedItem().toString());
			nv.setKhuVucQuanLy(layGiaTriCombo(cboKhuVucQuanLy));
			nv.setKhuVucPhucVu(layGiaTriCombo(cboKhuVucPhucVu));
			nv.setKhuVucTiepTan(layGiaTriCombo(cboKhuVucTiepTan));
			nv.setTenDangNhap(username);
			nv.setMatKhau(password);
			nv.setVaiTro(cboVaiTro.getSelectedItem().toString());
			nv.setTrangThai(chkTrangThai.isSelected());

			return nv;
		}

		private String layGiaTriCombo(JComboBox<String> cbo) {
			Object value = cbo.getEditor().getItem();
			return value != null ? value.toString().trim() : "";
		}
	}
}
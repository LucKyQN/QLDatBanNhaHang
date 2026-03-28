package GUI;

import DAO.MonAnDAO;
import Entity.DanhMuc;
import Entity.MonAn;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class FrmQLMonAn extends JPanel {

	private final MonAnDAO dao = new MonAnDAO();

	private JTable tblMonAn;
	private DefaultTableModel model;
	private List<MonAn> dsMonAn = new ArrayList<>();
	private JLabel lblMaMon;
	private JLabel lblTenMon;
	private JLabel lblDanhMuc;
	private JLabel lblDonVi;
	private JLabel lblSoLuongTon;
	private JLabel lblGiaBan;
	private JLabel lblTinhTrang;
	private JLabel lblAnhMon;
	private JTextArea txtMoTaChiTiet;
	private JTextArea txtGhiChuChiTiet;

	public FrmQLMonAn() {
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

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createTablePanel(), createDetailPanel());
		splitPane.setResizeWeight(0.62);
		splitPane.setBorder(null);
		splitPane.setDividerSize(8);

		root.add(splitPane, BorderLayout.CENTER);

		add(root, BorderLayout.CENTER);
	}

	private JPanel createTopBar() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true), new EmptyBorder(16, 16, 16, 16)));

		JLabel lblTitle = new JLabel("Quản lý thực đơn");
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));

		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		btnPanel.setOpaque(false);

		JButton btnThem = new JButton("Thêm");
		JButton btnSua = new JButton("Sửa");
		JButton btnXoa = new JButton("Xóa mềm");
		JButton btnLamMoi = new JButton("Làm mới");

		btnThem.addActionListener(e -> moDialogThem());
		btnSua.addActionListener(e -> moDialogSua());
		btnXoa.addActionListener(e -> xoaMemMonAn());
		btnLamMoi.addActionListener(e -> loadTable());

		btnPanel.add(btnLamMoi);
		btnPanel.add(btnXoa);
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
				new String[] { "Mã món", "Tên món", "Danh mục", "Đơn vị", "Tồn kho", "Giá bán", "Trạng thái" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tblMonAn = new JTable(model);
		tblMonAn.setRowHeight(30);
		tblMonAn.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblMonAn.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
		tblMonAn.setFont(new Font("Segoe UI", Font.PLAIN, 13));

		tblMonAn.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				MonAn mon = getMonAnDangChon();
				if (mon != null) {
					hienThiChiTietMonAn(mon);
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(tblMonAn);

		panel.add(scrollPane, BorderLayout.CENTER);
		return panel;
	}

	private JPanel createDetailPanel() {
		JPanel panel = new JPanel(new BorderLayout(16, 16));
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true), new EmptyBorder(16, 16, 16, 16)));

		JLabel lblTitle = new JLabel("Chi tiết món ăn");
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

		JPanel infoPanel = new JPanel(new GridLayout(4, 2, 12, 10));
		infoPanel.setOpaque(false);

		lblMaMon = new JLabel("-");
		lblTenMon = new JLabel("-");
		lblDanhMuc = new JLabel("-");
		lblDonVi = new JLabel("-");
		lblSoLuongTon = new JLabel("-");
		lblGiaBan = new JLabel("-");
		lblTinhTrang = new JLabel("-");

		infoPanel.add(createInfoItem("Mã món:", lblMaMon));
		infoPanel.add(createInfoItem("Tên món:", lblTenMon));
		infoPanel.add(createInfoItem("Danh mục:", lblDanhMuc));
		infoPanel.add(createInfoItem("Đơn vị:", lblDonVi));
		infoPanel.add(createInfoItem("Tồn kho:", lblSoLuongTon));
		infoPanel.add(createInfoItem("Giá bán:", lblGiaBan));
		infoPanel.add(createInfoItem("Trạng thái:", lblTinhTrang));

		txtMoTaChiTiet = new JTextArea(3, 20);
		txtMoTaChiTiet.setLineWrap(true);
		txtMoTaChiTiet.setWrapStyleWord(true);
		txtMoTaChiTiet.setEditable(false);

		txtGhiChuChiTiet = new JTextArea(3, 20);
		txtGhiChuChiTiet.setLineWrap(true);
		txtGhiChuChiTiet.setWrapStyleWord(true);
		txtGhiChuChiTiet.setEditable(false);

		JPanel textPanel = new JPanel(new GridLayout(2, 1, 10, 10));
		textPanel.setOpaque(false);
		textPanel.add(createTextAreaItem("Mô tả:", txtMoTaChiTiet));
		textPanel.add(createTextAreaItem("Ghi chú:", txtGhiChuChiTiet));

		JPanel leftPanel = new JPanel(new BorderLayout(12, 12));
		leftPanel.setOpaque(false);
		leftPanel.add(infoPanel, BorderLayout.NORTH);
		leftPanel.add(textPanel, BorderLayout.CENTER);

		lblAnhMon = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
		lblAnhMon.setPreferredSize(new Dimension(240, 180));
		lblAnhMon.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
		lblAnhMon.setOpaque(true);
		lblAnhMon.setBackground(new Color(245, 245, 245));

		panel.add(lblTitle, BorderLayout.NORTH);
		panel.add(leftPanel, BorderLayout.CENTER);
		panel.add(lblAnhMon, BorderLayout.EAST);

		return panel;
	}

	private JPanel createInfoItem(String title, JLabel valueLabel) {
		JPanel p = new JPanel(new BorderLayout(4, 4));
		p.setOpaque(false);

		JLabel lb = new JLabel(title);
		lb.setFont(new Font("Segoe UI", Font.BOLD, 13));

		valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

		p.add(lb, BorderLayout.NORTH);
		p.add(valueLabel, BorderLayout.CENTER);
		return p;
	}

	private JPanel createTextAreaItem(String title, JTextArea area) {
		JPanel p = new JPanel(new BorderLayout(4, 4));
		p.setOpaque(false);

		JLabel lb = new JLabel(title);
		lb.setFont(new Font("Segoe UI", Font.BOLD, 13));

		JScrollPane sp = new JScrollPane(area);
		sp.setPreferredSize(new Dimension(100, 60));

		p.add(lb, BorderLayout.NORTH);
		p.add(sp, BorderLayout.CENTER);
		return p;
	}

	private void hienThiChiTietMonAn(MonAn mon) {
		lblMaMon.setText(mon.getMaMonAn() != null ? mon.getMaMonAn() : "-");
		lblTenMon.setText(mon.getTenMon() != null ? mon.getTenMon() : "-");
		lblDanhMuc.setText(mon.getDanhMuc() != null ? mon.getDanhMuc().getTenDM() : "-");
		lblDonVi.setText(mon.getDonVi() != null ? mon.getDonVi() : "-");
		lblSoLuongTon.setText(String.valueOf(mon.getSoLuong()));
		lblGiaBan.setText(String.format("%,.0f đ", mon.getGiaMon()).replace(",", "."));
		lblTinhTrang.setText(mon.isTinhTrang() ? "Đang bán" : "Ngừng bán");
		txtMoTaChiTiet.setText(mon.getMoTa() != null ? mon.getMoTa() : "");
		txtGhiChuChiTiet.setText(mon.getGhiChu() != null ? mon.getGhiChu() : "");

		hienThiAnh(lblAnhMon, mon.getAnhMon(), 240, 180);
	}

	private void hienThiAnh(JLabel label, String imagePath, int width, int height) {
		label.setIcon(null);

		if (imagePath == null || imagePath.trim().isEmpty()) {
			label.setText("Chưa có ảnh");
			return;
		}

		File file = new File(imagePath);
		if (!file.exists()) {
			label.setText("Không tìm thấy ảnh");
			return;
		}

		ImageIcon icon = new ImageIcon(imagePath);
		Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
		label.setText("");
		label.setIcon(new ImageIcon(img));
	}

	private void loadTable() {
		model.setRowCount(0);
		dsMonAn = dao.getAllMonAn();

		for (MonAn mon : dsMonAn) {
			model.addRow(new Object[] { mon.getMaMonAn(), mon.getTenMon(),
					mon.getDanhMuc() != null ? mon.getDanhMuc().getTenDM() : "", mon.getDonVi(), mon.getSoLuong(),
					String.format("%,.0f", mon.getGiaMon()).replace(",", "."),
					mon.isTinhTrang() ? "Đang bán" : "Ngừng bán" });
		}

		if (!dsMonAn.isEmpty()) {
			tblMonAn.setRowSelectionInterval(0, 0);
			hienThiChiTietMonAn(dsMonAn.get(0));
		}
	}

	private MonAn getMonAnDangChon() {
		int row = tblMonAn.getSelectedRow();
		if (row < 0 || row >= dsMonAn.size())
			return null;
		return dsMonAn.get(row);
	}

	private void moDialogThem() {
		Window window = SwingUtilities.getWindowAncestor(this);
		Frame owner = window instanceof Frame ? (Frame) window : null;

		MonAnDialog dialog = new MonAnDialog(owner, dao, null);
		dialog.setVisible(true);

		if (dialog.isSucceeded()) {
			loadTable();
		}
	}

	private void moDialogSua() {
		MonAn mon = getMonAnDangChon();
		if (mon == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn món ăn cần sửa.");
			return;
		}

		Window window = SwingUtilities.getWindowAncestor(this);
		Frame owner = window instanceof Frame ? (Frame) window : null;

		MonAnDialog dialog = new MonAnDialog(owner, dao, mon);
		dialog.setVisible(true);

		if (dialog.isSucceeded()) {
			loadTable();
		}
	}

	private void xoaMemMonAn() {
		MonAn mon = getMonAnDangChon();
		if (mon == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn món ăn cần xóa.");
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(this,
				"Xác nhận xóa mềm món " + mon.getMaMonAn() + " - " + mon.getTenMon() + "?", "Xóa mềm món ăn",
				JOptionPane.YES_NO_OPTION);

		if (confirm != JOptionPane.YES_OPTION)
			return;

		if (dao.xoaMemMonAn(mon.getMaMonAn())) {
			JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái ngừng bán.");
			loadTable();
		} else {
			JOptionPane.showMessageDialog(this, "Xóa mềm thất bại.");
		}
	}

	// ======================= DIALOG CRUD =======================
	static class MonAnDialog extends JDialog {

		private final MonAnDAO dao;
		private final MonAn monAnSua;
		private boolean succeeded = false;

		private JTextField txtMaMon;
		private JTextField txtTenMon;
		private JComboBox<DanhMuc> cboDanhMuc;
		private JComboBox<String> cboDonVi;
		private JTextField txtSoLuongTon;
		private JTextField txtGiaBan;
		private JTextField txtMoTa;
		private JTextField txtGhiChu;
		private JTextField txtAnhMon;
		private JCheckBox chkTinhTrang;
		private JLabel lblPreviewAnh;

		public MonAnDialog(Frame owner, MonAnDAO dao, MonAn monAnSua) {
			super(owner, true);
			this.dao = dao;
			this.monAnSua = monAnSua;

			setTitle(monAnSua == null ? "Thêm món ăn" : "Sửa món ăn");
			initUI();

			if (monAnSua != null) {
				doDuLieuLenForm();
			}

			pack();
			setSize(920, 430);
			setLocationRelativeTo(owner);
		}

		public boolean isSucceeded() {
			return succeeded;
		}

		private void initUI() {
			JPanel root = new JPanel(new BorderLayout(16, 16));
			root.setBorder(new EmptyBorder(20, 20, 20, 20));
			root.setBackground(Color.WHITE);

			JPanel fields = new JPanel(new GridLayout(4, 3, 12, 12));
			fields.setOpaque(false);

			txtMaMon = new JTextField();
			txtTenMon = new JTextField();

			cboDanhMuc = new JComboBox<>();
			loadDanhMucToCombo();
			cboDanhMuc.setRenderer(new DefaultListCellRenderer() {
				@Override
				public Component getListCellRendererComponent(JList<?> list, Object value, int index,
						boolean isSelected, boolean cellHasFocus) {
					super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					if (value instanceof DanhMuc) {
						DanhMuc dm = (DanhMuc) value;
						setText(dm.getMaDM() + " - " + dm.getTenDM());
					} else {
						setText("");
					}
					return this;
				}
			});

			cboDonVi = new JComboBox<>(new String[] { "Phần", "Dĩa", "Tô", "Ly", "Chai", "Cái", "Suất" });
			cboDonVi.setEditable(true);

			txtSoLuongTon = new JTextField();
			txtGiaBan = new JTextField();
			txtMoTa = new JTextField();
			txtGhiChu = new JTextField();
			txtAnhMon = new JTextField();

			JButton btnChonAnh = new JButton("Chọn ảnh");
			btnChonAnh.addActionListener(e -> chonAnh());

			chkTinhTrang = new JCheckBox("Đang bán");
			chkTinhTrang.setSelected(true);
			chkTinhTrang.setOpaque(false);

			fields.add(createField("Mã món", txtMaMon));
			fields.add(createField("Tên món", txtTenMon));
			fields.add(createField("Danh mục", cboDanhMuc));

			fields.add(createField("Đơn vị", cboDonVi));
			fields.add(createField("Số lượng tồn", txtSoLuongTon));
			fields.add(createField("Giá bán", txtGiaBan));

			fields.add(createField("Mô tả", txtMoTa));
			fields.add(createField("Ghi chú", txtGhiChu));
			fields.add(createField("Tình trạng", chkTinhTrang));

			JPanel anhPanel = new JPanel(new BorderLayout(8, 8));
			anhPanel.setOpaque(false);
			anhPanel.add(createField("Đường dẫn ảnh", txtAnhMon), BorderLayout.CENTER);
			anhPanel.add(btnChonAnh, BorderLayout.EAST);

			lblPreviewAnh = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
			lblPreviewAnh.setPreferredSize(new Dimension(180, 140));
			lblPreviewAnh.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
			lblPreviewAnh.setOpaque(true);
			lblPreviewAnh.setBackground(new Color(245, 245, 245));

			JPanel center = new JPanel(new BorderLayout(16, 16));
			center.setOpaque(false);
			center.add(fields, BorderLayout.CENTER);
			center.add(anhPanel, BorderLayout.SOUTH);

			JPanel imageWrap = new JPanel(new BorderLayout());
			imageWrap.setOpaque(false);
			imageWrap.add(lblPreviewAnh, BorderLayout.NORTH);

			JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
			btnPanel.setOpaque(false);

			JButton btnHuy = new JButton("Hủy");
			JButton btnLuu = new JButton(monAnSua == null ? "Thêm" : "Lưu");

			btnHuy.addActionListener(e -> dispose());
			btnLuu.addActionListener(e -> luuMonAn());

			btnPanel.add(btnHuy);
			btnPanel.add(btnLuu);

			root.add(center, BorderLayout.CENTER);
			root.add(imageWrap, BorderLayout.EAST);
			root.add(btnPanel, BorderLayout.SOUTH);

			setContentPane(root);
		}

		private void chonAnh() {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Chọn ảnh món ăn");

			int result = chooser.showOpenDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				txtAnhMon.setText(file.getAbsolutePath());
				hienThiPreviewAnh(file.getAbsolutePath());
			}
		}

		private void hienThiPreviewAnh(String imagePath) {
			lblPreviewAnh.setIcon(null);

			if (imagePath == null || imagePath.trim().isEmpty()) {
				lblPreviewAnh.setText("Chưa có ảnh");
				return;
			}

			File file = new File(imagePath);
			if (!file.exists()) {
				lblPreviewAnh.setText("Không tìm thấy ảnh");
				return;
			}

			ImageIcon icon = new ImageIcon(imagePath);
			Image img = icon.getImage().getScaledInstance(180, 140, Image.SCALE_SMOOTH);
			lblPreviewAnh.setText("");
			lblPreviewAnh.setIcon(new ImageIcon(img));
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

		private void loadDanhMucToCombo() {
			List<DanhMuc> dsDanhMuc = dao.getAllDanhMuc();
			DefaultComboBoxModel<DanhMuc> model = new DefaultComboBoxModel<>();
			for (DanhMuc dm : dsDanhMuc) {
				model.addElement(dm);
			}
			cboDanhMuc.setModel(model);
		}

		private void doDuLieuLenForm() {
			txtMaMon.setText(monAnSua.getMaMonAn());
			txtMaMon.setEditable(false);

			txtTenMon.setText(monAnSua.getTenMon());
			txtSoLuongTon.setText(String.valueOf(monAnSua.getSoLuong()));
			txtGiaBan.setText(String.valueOf((long) monAnSua.getGiaMon()));
			txtMoTa.setText(monAnSua.getMoTa() != null ? monAnSua.getMoTa() : "");
			txtGhiChu.setText(monAnSua.getGhiChu() != null ? monAnSua.getGhiChu() : "");
			txtAnhMon.setText(monAnSua.getAnhMon() != null ? monAnSua.getAnhMon() : "");
			chkTinhTrang.setSelected(monAnSua.isTinhTrang());

			if (monAnSua.getDonVi() != null) {
				cboDonVi.setSelectedItem(monAnSua.getDonVi());
			}

			if (monAnSua.getDanhMuc() != null) {
				for (int i = 0; i < cboDanhMuc.getItemCount(); i++) {
					DanhMuc dm = cboDanhMuc.getItemAt(i);
					if (dm.getMaDM().equals(monAnSua.getDanhMuc().getMaDM())) {
						cboDanhMuc.setSelectedIndex(i);
						break;
					}
				}
			}
			hienThiPreviewAnh(monAnSua.getAnhMon());
		}

		private void luuMonAn() {
			try {
				MonAn mon = layDuLieuForm();

				if (monAnSua == null) {
					if (dao.tonTaiMaMonAn(mon.getMaMonAn())) {
						JOptionPane.showMessageDialog(this, "Mã món ăn đã tồn tại.");
						return;
					}

					if (dao.themMonAn(mon)) {
						JOptionPane.showMessageDialog(this, "Thêm món ăn thành công.");
						succeeded = true;
						dispose();
					} else {
						JOptionPane.showMessageDialog(this, "Thêm món ăn thất bại.");
					}
				} else {
					if (dao.suaMonAn(mon)) {
						JOptionPane.showMessageDialog(this, "Cập nhật món ăn thành công.");
						succeeded = true;
						dispose();
					} else {
						JOptionPane.showMessageDialog(this, "Cập nhật món ăn thất bại.");
					}
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
		}

		private MonAn layDuLieuForm() throws Exception {
			String maMon = txtMaMon.getText().trim();
			String tenMon = txtTenMon.getText().trim();
			DanhMuc danhMuc = (DanhMuc) cboDanhMuc.getSelectedItem();
			String donVi = layGiaTriCombo(cboDonVi);
			String soLuongTonText = txtSoLuongTon.getText().trim();
			String giaBanText = txtGiaBan.getText().trim();

			if (maMon.isEmpty() || tenMon.isEmpty() || danhMuc == null || donVi.isEmpty() || soLuongTonText.isEmpty()
					|| giaBanText.isEmpty()) {
				throw new Exception("Vui lòng nhập đầy đủ thông tin bắt buộc.");
			}

			int soLuongTon;
			double giaBan;

			try {
				soLuongTon = Integer.parseInt(soLuongTonText);
			} catch (Exception e) {
				throw new Exception("Số lượng tồn phải là số nguyên.");
			}

			try {
				giaBan = Double.parseDouble(giaBanText);
			} catch (Exception e) {
				throw new Exception("Giá bán phải là số.");
			}

			if (soLuongTon < 0) {
				throw new Exception("Số lượng tồn không được âm.");
			}

			if (giaBan <= 0) {
				throw new Exception("Giá bán phải lớn hơn 0.");
			}

			MonAn mon = new MonAn();
			mon.setMaMonAn(maMon);
			mon.setTenMon(tenMon);
			mon.setDanhMuc(danhMuc);
			mon.setDonVi(donVi);
			mon.setSoLuong(soLuongTon);
			mon.setGiaMon(giaBan);
			mon.setMoTa(txtMoTa.getText().trim());
			mon.setGhiChu(txtGhiChu.getText().trim());
			mon.setAnhMon(txtAnhMon.getText().trim());
			mon.setTinhTrang(chkTinhTrang.isSelected());

			return mon;
		}

		private String layGiaTriCombo(JComboBox<String> cbo) {
			Object value = cbo.isEditable() ? cbo.getEditor().getItem() : cbo.getSelectedItem();
			return value != null ? value.toString().trim() : "";
		}
	}
}
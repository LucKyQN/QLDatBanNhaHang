
package GUI;

import DAO.MonAnDAO;
import Entity.DanhMuc;
import Entity.MonAn;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class FrmQLMonAn extends JPanel {

	private final MonAnDAO dao = new MonAnDAO();

	private JTable tblMonAn;
	private DefaultTableModel model;
	private List<MonAn> dsMonAn = new ArrayList<>();

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
		root.add(createTablePanel(), BorderLayout.CENTER);

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
		JButton btnXoaMem = new JButton("Xóa");

		btnThem.addActionListener(e -> moDialogThem());
		btnSua.addActionListener(e -> moDialogSua());
		btnXoaMem.addActionListener(e -> xoaMemMonAn());

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
				new String[] { "Mã món", "Tên món", "Danh Mục", "Đơn vị", "Tồn kho", "Giá bán", "Trạng thái" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tblMonAn = new JTable(model);
		tblMonAn.setRowHeight(32);
		tblMonAn.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblMonAn.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
		tblMonAn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		tblMonAn.setGridColor(new Color(235, 235, 235));
		tblMonAn.setShowGrid(true);

		// Hint cho người dùng
		JLabel lblHint = new JLabel("  Nhấn đúp chuột vào một món để xem chi tiết");
		lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
		lblHint.setForeground(new Color(150, 150, 150));
		lblHint.setBorder(new EmptyBorder(6, 0, 0, 0));

		// ===== DOUBLE CLICK → hiện dialog chi tiết =====
		tblMonAn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					MonAn mon = getMonAnDangChon();
					if (mon != null)
						moDialogChiTiet(mon);
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(tblMonAn);
		scrollPane.setBorder(null);

		panel.add(scrollPane, BorderLayout.CENTER);
		panel.add(lblHint, BorderLayout.SOUTH);
		return panel;
	}


	private void moDialogChiTiet(MonAn mon) {
		Window window = SwingUtilities.getWindowAncestor(this);
		Frame owner = window instanceof Frame ? (Frame) window : null;

		JDialog dialog = new JDialog(owner, "Chi tiết món ăn", true);
		dialog.setSize(700, 520);
		dialog.setLocationRelativeTo(this);
		dialog.setResizable(false);

		JPanel root = new JPanel(new BorderLayout(16, 16));
		root.setBackground(Color.WHITE);
		root.setBorder(new EmptyBorder(20, 24, 20, 24));

		// Tiêu đề
		JLabel lblTitle = new JLabel("Chi tiết món ăn");
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblTitle.setBorder(new EmptyBorder(0, 0, 8, 0));

		// Panel thông tin
		JPanel infoPanel = new JPanel(new GridLayout(5, 2, 16, 10));
		infoPanel.setOpaque(false);

		infoPanel.add(createInfoItem("Mã món:", mon.getMaMonAn() != null ? mon.getMaMonAn() : "-"));
		infoPanel.add(createInfoItem("Tên món:", mon.getTenMon() != null ? mon.getTenMon() : "-"));
		infoPanel.add(createInfoItem("Danh mục:", mon.getDanhMuc() != null ? mon.getDanhMuc().getTenDM() : "-"));
		infoPanel.add(createInfoItem("Đơn vị:", mon.getDonVi() != null ? mon.getDonVi() : "-"));
		infoPanel.add(createInfoItem("Tồn kho:", String.valueOf(mon.getSoLuong())));
		infoPanel.add(createInfoItem("Giá bán:", String.format("%,.0f đ", mon.getGiaMon()).replace(",", ".")));
		infoPanel.add(createInfoItem("Trạng thái:", mon.isTinhTrang() ? "Đang bán" : "Ngừng bán"));

		JPanel emptyPanel = new JPanel();
		emptyPanel.setOpaque(false);
		infoPanel.add(emptyPanel);

		// Mô tả + Ghi chú
		JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 10));
		textPanel.setOpaque(false);

		JTextArea txtMoTa = new JTextArea(mon.getMoTa() != null ? mon.getMoTa() : "");
		txtMoTa.setEditable(false);
		txtMoTa.setLineWrap(true);
		txtMoTa.setWrapStyleWord(true);
		txtMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		textPanel.add(createTextAreaItem("Mô tả:", txtMoTa));

		JTextArea txtGhiChu = new JTextArea(mon.getGhiChu() != null ? mon.getGhiChu() : "");
		txtGhiChu.setEditable(false);
		txtGhiChu.setLineWrap(true);
		txtGhiChu.setWrapStyleWord(true);
		txtGhiChu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		textPanel.add(createTextAreaItem("Ghi chú:", txtGhiChu));

		JPanel leftPanel = new JPanel(new BorderLayout(0, 12));
		leftPanel.setOpaque(false);
		leftPanel.add(infoPanel, BorderLayout.NORTH);
		leftPanel.add(textPanel, BorderLayout.CENTER);

		// Ảnh món
		JLabel lblAnh = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
		lblAnh.setPreferredSize(new Dimension(200, 200));
		lblAnh.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
		lblAnh.setOpaque(true);
		lblAnh.setBackground(new Color(245, 245, 245));
		lblAnh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblAnh.setForeground(new Color(150, 150, 150));
		hienThiAnh(lblAnh, mon.getAnhMon(), 200, 200);

		// Nút đóng
		JButton btnDong = new JButton("Đóng");
		btnDong.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		btnDong.addActionListener(e -> dialog.dispose());
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnPanel.setOpaque(false);
		btnPanel.add(btnDong);

		JPanel wrapAnhPanel = new JPanel(new GridBagLayout());
		wrapAnhPanel.setOpaque(false); // Xóa nền của panel bọc ngoài
		wrapAnhPanel.add(lblAnh);
		root.add(lblTitle, BorderLayout.NORTH);
		root.add(leftPanel, BorderLayout.CENTER);
		root.add(wrapAnhPanel, BorderLayout.EAST);
		root.add(btnPanel, BorderLayout.SOUTH);

		dialog.setContentPane(root);
		dialog.setVisible(true);
	}

	private JPanel createInfoItem(String title, String value) {
		JPanel p = new JPanel(new BorderLayout(4, 2));
		p.setOpaque(false);
		JLabel lb = new JLabel(title);
		lb.setFont(new Font("Segoe UI", Font.BOLD, 13));
		JLabel lv = new JLabel(value);
		lv.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		p.add(lb, BorderLayout.NORTH);
		p.add(lv, BorderLayout.CENTER);
		return p;
	}

	private JPanel createTextAreaItem(String title, JTextArea area) {
		JPanel p = new JPanel(new BorderLayout(4, 4));
		p.setOpaque(false);
		JLabel lb = new JLabel(title);
		lb.setFont(new Font("Segoe UI", Font.BOLD, 13));
		JScrollPane sp = new JScrollPane(area);
		sp.setPreferredSize(new Dimension(100, 55));
		p.add(lb, BorderLayout.NORTH);
		p.add(sp, BorderLayout.CENTER);
		return p;
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
		if (dialog.isSucceeded())
			loadTable();
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
		if (dialog.isSucceeded())
			loadTable();
	}

	private void xoaMemMonAn() {
		MonAn mon = getMonAnDangChon();
		if (mon == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn món ăn cần xóa mềm.");
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



	static class MonAnDialog extends JDialog {

		private final MonAnDAO dao;
		private final MonAn monAnSua;
		private boolean succeeded = false;

		private JTextField txtMaMon, txtTenMon, txtSoLuongTon, txtGiaBan, txtAnhMon;
		private JTextArea txtMoTa, txtGhiChu; // Đã đổi thành JTextArea
		private JComboBox<DanhMuc> cboDanhMuc;
		private JComboBox<String> cboDonVi;
		private JCheckBox chkTinhTrang;
		private JLabel lblPreviewAnh;

		public MonAnDialog(Frame owner, MonAnDAO dao, MonAn monAnSua) {
			super(owner, true);
			this.dao = dao;
			this.monAnSua = monAnSua;
			setTitle(monAnSua == null ? "Thêm món ăn" : "Sửa món ăn");
			initUI();
			if (monAnSua != null)
				doDuLieuLenForm();
			else
				txtMaMon.setText(dao.getMaMonTuDong());
			pack();
			// Tăng chiều cao Dialog lên để chứa đủ 2 ô Text Area to
			setSize(920, 580);
			setLocationRelativeTo(owner);
		}

		public boolean isSucceeded() {
			return succeeded;
		}

		private void initUI() {
			JPanel root = new JPanel(new BorderLayout(16, 16));
			root.setBorder(new EmptyBorder(20, 20, 20, 20));
			root.setBackground(Color.WHITE);

			// 1. Nhóm các trường thông tin ngắn (3 dòng x 3 cột)
			JPanel topFields = new JPanel(new GridLayout(3, 3, 12, 12));
			topFields.setOpaque(false);

			txtMaMon = new JTextField();
			txtMaMon.setEditable(false);
			txtMaMon.setBackground(new Color(240, 240, 240));
			txtTenMon = new JTextField();

			cboDanhMuc = new JComboBox<>();
			loadDanhMucToCombo();
			cboDanhMuc.setRenderer(new DefaultListCellRenderer() {
				@Override
				public Component getListCellRendererComponent(JList<?> list, Object value, int index,
						boolean isSelected, boolean cellHasFocus) {
					super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					if (value instanceof DanhMuc dm)
						setText(dm.getMaDM() + " - " + dm.getTenDM());
					else
						setText("");
					return this;
				}
			});

			cboDonVi = new JComboBox<>(new String[] { "Phần", "Dĩa", "Tô", "Ly", "Chai", "Cái", "Suất","Lon" });
			cboDonVi.setEditable(true);

			txtSoLuongTon = new JTextField();
			txtGiaBan = new JTextField();
			txtAnhMon = new JTextField();

			chkTinhTrang = new JCheckBox("Đang bán");
			chkTinhTrang.setSelected(true);
			chkTinhTrang.setOpaque(false);

			topFields.add(createField("Mã món", txtMaMon));
			topFields.add(createField("Tên món", txtTenMon));
			topFields.add(createField("Danh mục", cboDanhMuc));
			topFields.add(createField("Đơn vị", cboDonVi));
			topFields.add(createField("Số lượng tồn", txtSoLuongTon));
			topFields.add(createField("Giá bán", txtGiaBan));
			topFields.add(createField("Tình trạng", chkTinhTrang));
			topFields.add(new JLabel());
			topFields.add(new JLabel());

			// 2. Nhóm Mô tả và Ghi chú (JTextArea)
			JPanel textAreasPanel = new JPanel(new GridLayout(2, 1, 12, 12));
			textAreasPanel.setOpaque(false);

			txtMoTa = new JTextArea(3, 20);
			txtMoTa.setLineWrap(true);
			txtMoTa.setWrapStyleWord(true);
			txtMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 13));

			txtGhiChu = new JTextArea(3, 20);
			txtGhiChu.setLineWrap(true);
			txtGhiChu.setWrapStyleWord(true);
			txtGhiChu.setFont(new Font("Segoe UI", Font.PLAIN, 13));

			textAreasPanel.add(createTextAreaField("Mô tả", txtMoTa));
			textAreasPanel.add(createTextAreaField("Ghi chú", txtGhiChu));

			// 3. Gom phần fields lại
			JPanel fieldsContainer = new JPanel(new BorderLayout(0, 12));
			fieldsContainer.setOpaque(false);
			fieldsContainer.add(topFields, BorderLayout.NORTH);
			fieldsContainer.add(textAreasPanel, BorderLayout.CENTER);

			// 4. Panel chọn ảnh đường dẫn
			JButton btnChonAnh = new JButton("Chọn ảnh");
			btnChonAnh.addActionListener(e -> chonAnh());
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
			center.add(fieldsContainer, BorderLayout.CENTER);
			center.add(anhPanel, BorderLayout.SOUTH);

			JPanel imageWrap = new JPanel(new BorderLayout());
			imageWrap.setOpaque(false);
			imageWrap.add(lblPreviewAnh, BorderLayout.NORTH);

			// 6. Nút chức năng
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

		private JPanel createTextAreaField(String label, JTextArea area) {
			JPanel p = new JPanel(new BorderLayout(6, 6));
			p.setOpaque(false);
			JLabel lb = new JLabel(label);
			lb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
			p.add(lb, BorderLayout.NORTH);
			JScrollPane sp = new JScrollPane(area);
			p.add(sp, BorderLayout.CENTER);
			return p;
		}

		private void chonAnh() {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Chọn ảnh món ăn");
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
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
			DefaultComboBoxModel<DanhMuc> m = new DefaultComboBoxModel<>();
			for (DanhMuc dm : dsDanhMuc)
				m.addElement(dm);
			cboDanhMuc.setModel(m);
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
			if (monAnSua.getDonVi() != null)
				cboDonVi.setSelectedItem(monAnSua.getDonVi());
			if (monAnSua.getDanhMuc() != null) {
				for (int i = 0; i < cboDanhMuc.getItemCount(); i++) {
					if (cboDanhMuc.getItemAt(i).getMaDM().equals(monAnSua.getDanhMuc().getMaDM())) {
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
					} else
						JOptionPane.showMessageDialog(this, "Thêm món ăn thất bại.");
				} else {
					if (dao.suaMonAn(mon)) {
						JOptionPane.showMessageDialog(this, "Cập nhật món ăn thành công.");
						succeeded = true;
						dispose();
					} else
						JOptionPane.showMessageDialog(this, "Cập nhật món ăn thất bại.");
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
			String slText = txtSoLuongTon.getText().trim();
			String giaText = txtGiaBan.getText().trim();

			if (maMon.isEmpty() || tenMon.isEmpty() || danhMuc == null || donVi.isEmpty() || slText.isEmpty()
					|| giaText.isEmpty())
				throw new Exception("Vui lòng nhập đầy đủ thông tin");

			int soLuong;
			double giaBan;
			try {
				soLuong = Integer.parseInt(slText);
			} catch (Exception e) {
				throw new Exception("Số lượng tồn phải là số nguyên.");
			}
			try {
				giaBan = Double.parseDouble(giaText);
			} catch (Exception e) {
				throw new Exception("Giá bán phải là số.");
			}
			if (soLuong < 0)
				throw new Exception("Số lượng tồn không được âm.");
			if (giaBan <= 0)
				throw new Exception("Giá bán phải lớn hơn 0.");

			MonAn mon = new MonAn();
			mon.setMaMonAn(maMon);
			mon.setTenMon(tenMon);
			mon.setDanhMuc(danhMuc);
			mon.setDonVi(donVi);
			mon.setSoLuong(soLuong);
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
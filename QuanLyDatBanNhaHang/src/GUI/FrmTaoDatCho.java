package GUI;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import DAO.BanAnDAO;
import DAO.DonDatMonDAO;
import DAO.MonAnDAO;
import DAO.PhieuDatBanDAO;
import Entity.BanAn;
import Entity.MonAn;
import Entity.PhieuDatBan;

public class FrmTaoDatCho extends JDialog {

	private static final Color RED_MAIN = new Color(220, 38, 38);
	private static final Color BORDER_CLR = new Color(230, 230, 230);
	private static final Color TEXT_GRAY = new Color(120, 120, 120);

	private static final int PHI_DAT_BAN_CO_DINH = 250000;

	private DefaultTableModel tbModelDaChon;
	private JLabel lblTongTien;
	private JLabel lblPhiDatBan;
	private JLabel lblCocMon;
	private JLabel lblTongTienCoc;

	private int tongTien = 0;

	private JComboBox<ComboItem> cbBan;

	private JTextField txtTenKhach;
	private JTextField txtSDT;
	private JTextField txtSoLuong;
	private JTextField txtThoiGian;
	private JTextArea txtNote;

	public FrmTaoDatCho(JFrame parent) {
		super(parent, true);
		setUndecorated(true);
		setSize(1000, 780);
		setLocationRelativeTo(parent);

		JPanel root = new JPanel(new BorderLayout());
		root.setBackground(Color.WHITE);
		root.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));

		root.add(createHeader(), BorderLayout.NORTH);
		root.add(createBody(), BorderLayout.CENTER);
		root.add(createFooter(), BorderLayout.SOUTH);

		setContentPane(root);
	}

	private JPanel createHeader() {
		JPanel header = new JPanel(new BorderLayout());
		header.setBackground(Color.WHITE);
		header.setBorder(new EmptyBorder(20, 30, 20, 30));

		JLabel title = new JLabel("Tạo đặt chỗ mới");
		title.setFont(new Font("Segoe UI", Font.BOLD, 22));

		JButton btnClose = new JButton("✕");
		btnClose.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnClose.setContentAreaFilled(false);
		btnClose.setBorderPainted(false);
		btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnClose.addActionListener(e -> dispose());

		header.add(title, BorderLayout.WEST);
		header.add(btnClose, BorderLayout.EAST);

		JPanel bottomLine = new JPanel(new BorderLayout());
		bottomLine.add(header, BorderLayout.CENTER);
		bottomLine.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR));

		return bottomLine;
	}

	private JPanel createBody() {
		JPanel body = new JPanel(new GridLayout(1, 2, 40, 0));
		body.setBackground(Color.WHITE);
		body.setBorder(new EmptyBorder(20, 30, 20, 30));

		JPanel pnlLeft = new JPanel();
		pnlLeft.setLayout(new BoxLayout(pnlLeft, BoxLayout.Y_AXIS));
		pnlLeft.setBackground(Color.WHITE);

		JLabel lblInfoTitle = new JLabel("Thông tin khách hàng");
		lblInfoTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
		pnlLeft.add(lblInfoTitle);
		pnlLeft.add(Box.createVerticalStrut(15));

		txtTenKhach = new JTextField();
		txtSDT = new JTextField();
		pnlLeft.add(createInputGroup("Tên khách hàng *", txtTenKhach));
		pnlLeft.add(Box.createVerticalStrut(15));
		pnlLeft.add(createInputGroup("Số điện thoại *", txtSDT));
		pnlLeft.add(Box.createVerticalStrut(15));

		JPanel rowTime = new JPanel(new GridLayout(1, 2, 15, 0));
		rowTime.setBackground(Color.WHITE);

		txtSoLuong = new JTextField("2");
		txtThoiGian = new JTextField("--:-- --");
		rowTime.add(createInputGroup("Số lượng khách *", txtSoLuong));
		rowTime.add(createInputGroup("Thời gian *", txtThoiGian));
		rowTime.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
		pnlLeft.add(rowTime);
		pnlLeft.add(Box.createVerticalStrut(15));

		BanAnDAO dao = new BanAnDAO();
		List<BanAn> dsBan = dao.getAllBanAn();

		cbBan = new JComboBox<>();
		cbBan.addItem(new ComboItem("", "Chọn bàn"));

		for (BanAn ban : dsBan) {
			if (ban.getTrangThai() != null && ban.getTrangThai().trim().equalsIgnoreCase("Trống")) {
				cbBan.addItem(new ComboItem(ban.getMaBan(), ban.getMaBan() + " - " + ban.getTenBan()));
			}
		}

		pnlLeft.add(createInputGroup("Chọn bàn ", cbBan));
		pnlLeft.add(Box.createVerticalStrut(15));

		txtNote = new JTextArea();
		txtNote.setLineWrap(true);
		txtNote.setWrapStyleWord(true);
		txtNote.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
		JScrollPane scrollNote = new JScrollPane(txtNote);
		scrollNote.setPreferredSize(new Dimension(0, 80));
		pnlLeft.add(createInputGroup("Ghi chú", scrollNote));

		// Đổi sang BorderLayout để linh hoạt ép tỷ lệ
		JPanel pnlRight = new JPanel(new BorderLayout(0, 15));
		pnlRight.setBackground(Color.WHITE);

		JPanel pnlMenu = new JPanel(new BorderLayout(0, 5));
		pnlMenu.setPreferredSize(new Dimension(0, 190));
		pnlMenu.setBackground(Color.WHITE);
		JLabel lblMenuTitle = new JLabel("Thực đơn (Tùy chọn)");
		lblMenuTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
		pnlMenu.add(lblMenuTitle, BorderLayout.NORTH);

		JPanel listFood = new JPanel();
		listFood.setLayout(new BoxLayout(listFood, BoxLayout.Y_AXIS));
		listFood.setBackground(Color.WHITE);

		MonAnDAO monAnDAO = new MonAnDAO();
		List<MonAn> dsMon = monAnDAO.getAllMonAn();

		for (MonAn mon : dsMon) {
			if (!mon.isTinhTrang()) {
				continue;
			}

			String ten = mon.getTenMon();
			int gia = (int) mon.getGiaMon();
			String icon = getIconByName(ten);

			listFood.add(createFoodItem(mon.getMaMonAn(), icon, ten, gia, "Món ăn"));
			listFood.add(Box.createVerticalStrut(10));
		}

		JScrollPane scrollFood = new JScrollPane(listFood);
		scrollFood.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
		scrollFood.getVerticalScrollBar().setUnitIncrement(16);
		pnlMenu.add(scrollFood, BorderLayout.CENTER);

		JPanel pnlCart = new JPanel(new BorderLayout(0, 8));
		pnlCart.setBackground(Color.WHITE);

		JLabel lblCartTitle = new JLabel("Món đã chọn");
		lblCartTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
		pnlCart.add(lblCartTitle, BorderLayout.NORTH);

		String[] columns = { "Mã món", "Tên món", "SL", "Đơn giá" };
		tbModelDaChon = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		JTable tbDaChon = new JTable(tbModelDaChon);
		tbDaChon.setRowHeight(25);

		tbDaChon.setToolTipText("Nhấp đúp chuột vào món để GIẢM số lượng hoặc XÓA");
		tbDaChon.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getClickCount() == 2) { // Bắt sự kiện nhấp đúp
					int row = tbDaChon.getSelectedRow();
					if (row != -1) {
						int slCu = Integer.parseInt(tbModelDaChon.getValueAt(row, 2).toString());
						long donGia = Long.parseLong(tbModelDaChon.getValueAt(row, 3).toString());

						// Trừ tổng tiền
						tongTien -= donGia;

						if (slCu > 1) {
							tbModelDaChon.setValueAt(slCu - 1, row, 2);
						} else {
							tbModelDaChon.removeRow(row);
						}

						capNhatTienCoc();
					}
				}
			}
		});
		tbDaChon.getColumnModel().getColumn(0).setMinWidth(0);
		tbDaChon.getColumnModel().getColumn(0).setMaxWidth(0);
		tbDaChon.getColumnModel().getColumn(0).setWidth(0);

		tbDaChon.getColumnModel().getColumn(1).setPreferredWidth(180);
		tbDaChon.getColumnModel().getColumn(2).setPreferredWidth(40);
		tbDaChon.getColumnModel().getColumn(3).setPreferredWidth(100);

		JScrollPane scrollCart = new JScrollPane(tbDaChon);
		scrollCart.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
		scrollCart.setPreferredSize(new Dimension(0, 150));
		pnlCart.add(scrollCart, BorderLayout.CENTER);

		JPanel pnlBottomRight = new JPanel();
		pnlBottomRight.setLayout(new BoxLayout(pnlBottomRight, BoxLayout.Y_AXIS));
		pnlBottomRight.setBackground(Color.WHITE);

		lblTongTien = new JLabel("Tổng cộng: 0 đ");
		lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblTongTien.setForeground(RED_MAIN);
		lblTongTien.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTongTien.setAlignmentX(Component.RIGHT_ALIGNMENT);

		pnlBottomRight.add(Box.createVerticalStrut(8));
		pnlBottomRight.add(lblTongTien);
		pnlBottomRight.add(Box.createVerticalStrut(12));
		pnlBottomRight.add(createDepositPanel());

		pnlCart.add(pnlBottomRight, BorderLayout.SOUTH);

		pnlRight.add(pnlMenu, BorderLayout.NORTH);
		pnlRight.add(pnlCart, BorderLayout.CENTER);


		body.add(pnlLeft);
		body.add(pnlRight);
		return body;
	}

	private JPanel createDepositPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(new Color(255, 252, 235));
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(245, 203, 92), 1, true), new EmptyBorder(14, 16, 14, 16)));

		JPanel content = new JPanel();
		content.setOpaque(false);
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

		JLabel lblTitle = new JLabel("Tiền cọc phải thu");
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblTitle.setForeground(new Color(146, 64, 14));
		content.add(lblTitle);
		content.add(Box.createVerticalStrut(14));

		lblPhiDatBan = new JLabel("250.000 đ");
		lblCocMon = new JLabel("0 đ");
		lblTongTienCoc = new JLabel("250.000 đ");

		content.add(createMoneyInfoRow("Phí đặt bàn:", lblPhiDatBan));
		content.add(Box.createVerticalStrut(8));
		content.add(createMoneyInfoRow("Cọc món đặt trước (30%):", lblCocMon));
		content.add(Box.createVerticalStrut(10));

		JSeparator sep = new JSeparator();
		sep.setForeground(new Color(245, 203, 92));
		content.add(sep);
		content.add(Box.createVerticalStrut(10));

		JPanel totalRow = new JPanel(new BorderLayout());
		totalRow.setOpaque(false);

		JLabel lblTotalText = new JLabel("Tổng tiền cọc:");
		lblTotalText.setFont(new Font("Segoe UI", Font.BOLD, 15));
		lblTotalText.setForeground(new Color(146, 64, 14));

		lblTongTienCoc.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblTongTienCoc.setForeground(RED_MAIN);

		totalRow.add(lblTotalText, BorderLayout.WEST);
		totalRow.add(lblTongTienCoc, BorderLayout.EAST);

		content.add(totalRow);
		content.add(Box.createVerticalStrut(10));

		JLabel lblNote = new JLabel("* Phí đặt bàn: 250.000đ | Đặt món trước: thu 30% tổng tiền món");
		lblNote.setFont(new Font("Segoe UI", Font.ITALIC, 12));
		lblNote.setForeground(TEXT_GRAY);
		content.add(lblNote);

		panel.add(content, BorderLayout.CENTER);
		return panel;
	}

	private JPanel createMoneyInfoRow(String label, JLabel valueLabel) {
		JPanel row = new JPanel(new BorderLayout());
		row.setOpaque(false);

		JLabel lbl = new JLabel(label);
		lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lbl.setForeground(new Color(80, 80, 80));

		valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		valueLabel.setForeground(new Color(80, 80, 80));
		valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		row.add(lbl, BorderLayout.WEST);
		row.add(valueLabel, BorderLayout.EAST);
		return row;
	}

	private void capNhatTienCoc() {
		int tienCocMon = (int) Math.round(tongTien * 0.3);
		int tongTienCoc = PHI_DAT_BAN_CO_DINH + tienCocMon;

		lblTongTien.setText("Tổng cộng: " + formatMoney(tongTien));
		lblPhiDatBan.setText(formatMoney(PHI_DAT_BAN_CO_DINH));
		lblCocMon.setText(formatMoney(tienCocMon));
		lblTongTienCoc.setText(formatMoney(tongTienCoc));
	}

	private String getIconByName(String ten) {
		if (ten == null) {
			return "🍽️";
		}
		ten = ten.toLowerCase();

		if (ten.contains("bò") || ten.contains("heo") || ten.contains("nướng"))
			return "🥩";
		if (ten.contains("gà") || ten.contains("vịt"))
			return "🍗";
		if (ten.contains("lẩu") || ten.contains("canh"))
			return "🍲";
		if (ten.contains("bia") || ten.contains("nước") || ten.contains("trà") || ten.contains("cà phê"))
			return "🥤";
		if (ten.contains("salad") || ten.contains("rau"))
			return "🥗";

		return "🍽️";
	}

	private JPanel createFooter() {
		JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
		footer.setBackground(Color.WHITE);
		footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR));

		JButton btnHuy = new JButton("Hủy");
		btnHuy.setPreferredSize(new Dimension(150, 40));
		btnHuy.setBackground(Color.WHITE);
		btnHuy.setFocusPainted(false);
		btnHuy.addActionListener(e -> dispose());

		JButton btnXacNhan = new JButton("Xác nhận đặt chỗ");
		btnXacNhan.setPreferredSize(new Dimension(200, 40));
		btnXacNhan.setBackground(RED_MAIN);
		btnXacNhan.setForeground(Color.WHITE);
		btnXacNhan.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnXacNhan.setFocusPainted(false);
		btnXacNhan.setBorderPainted(false);

		btnXacNhan.addActionListener(e -> {
			String tenKhach = txtTenKhach.getText().trim();
			String sdt = txtSDT.getText().trim();
			String soLuongStr = txtSoLuong.getText().trim();
			String thoiGianStr = txtThoiGian.getText().trim();
			String ghiChu = txtNote.getText().trim();

			if (tenKhach.isEmpty() || sdt.isEmpty() || soLuongStr.isEmpty() || thoiGianStr.isEmpty()
					|| thoiGianStr.equals("--:-- --")) {
				JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin bắt buộc (*)", "Cảnh báo",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			ComboItem selectedBan = (ComboItem) cbBan.getSelectedItem();
			if (selectedBan == null || selectedBan.getKey().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn để đặt!", "Lưu ý", JOptionPane.WARNING_MESSAGE);
				return;
			}

			int soLuongKhach;
			try {
				soLuongKhach = Integer.parseInt(soLuongStr);
				if (soLuongKhach <= 0) {
					throw new Exception();
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Số lượng khách phải là số nguyên dương!", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			java.util.Date thoiGianDen;
			try {
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
				thoiGianDen = sdf.parse(thoiGianStr);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this,
						"Thời gian không đúng định dạng (yyyy-MM-dd HH:mm)!\nVí dụ: 2026-04-02 19:30", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			java.util.Date now = new java.util.Date();
			long diffMillis = thoiGianDen.getTime() - now.getTime();
			long diffMinutes = diffMillis / (60 * 1000);

			if (diffMinutes < 60) {
				JOptionPane.showMessageDialog(this,
						"Thời gian đặt bàn phải cách hiện tại ít nhất 60 phút!\n"
								+ "Thời gian sớm nhất có thể đặt: "
								+ new java.text.SimpleDateFormat("HH:mm dd/MM/yyyy")
								.format(new java.util.Date(now.getTime() + 60 * 60 * 1000)),
						"Thời gian không hợp lệ", JOptionPane.WARNING_MESSAGE);
				return;
			}

			double tienMonDatTruoc = tongTien;
			double tienCoc = PHI_DAT_BAN_CO_DINH + Math.round(tongTien * 0.3);

			String maBan = selectedBan.getKey();
			String tenHienThi = selectedBan.getValue();

			PhieuDatBan phieu = new PhieuDatBan();
			String maPhieu = "PDB" + System.currentTimeMillis();
			phieu.setMaPhieu(maPhieu);
			phieu.setTenKhachHang(tenKhach);
			phieu.setSoDienThoai(sdt);
			phieu.setSoLuongKhach(soLuongKhach);
			phieu.setThoiGianDen(thoiGianDen);
			phieu.setGhiChu(ghiChu);
			phieu.setMaBan(maBan);
			phieu.setTienMonDatTruoc(tienMonDatTruoc);
			phieu.setTienCoc(tienCoc);

			PhieuDatBanDAO phieuDAO = new PhieuDatBanDAO();
			BanAnDAO banDAO = new BanAnDAO();
			DonDatMonDAO donDAO = new DonDatMonDAO();

			String maDon = "DDM" + System.currentTimeMillis();
			String maNV = (Entity.LuuLog.nhanVienDangNhap != null) ? Entity.LuuLog.nhanVienDangNhap.getMaNV() : "NV001";

			boolean phieuOk = phieuDAO.taoPhieuDatBan(phieu);
			boolean donOk = true;
			boolean ctOk = true;

			if (phieuOk && tbModelDaChon.getRowCount() > 0) {
				donOk = donDAO.taoDonDatMon(maDon, maNV, maBan, ghiChu);

				if (donOk) {
					for (int i = 0; i < tbModelDaChon.getRowCount(); i++) {
						String maMonAn = tbModelDaChon.getValueAt(i, 0).toString();
						int soLuongMon = Integer.parseInt(tbModelDaChon.getValueAt(i, 2).toString());
						long donGia = Long.parseLong(tbModelDaChon.getValueAt(i, 3).toString());

						if (!donDAO.themChiTietDonDatMon(maDon, maMonAn, soLuongMon, donGia, "")) {
							ctOk = false;
							break;
						}
					}
				}
			}

			if (phieuOk && donOk && ctOk) {
				if (banDAO.capNhatTrangThai(maBan, "Đã đặt")) {
					JOptionPane.showMessageDialog(this,
							"Đặt chỗ thành công cho " + tenKhach + " tại " + tenHienThi + "!\nTiền món đặt trước: "
									+ formatMoney((int) tienMonDatTruoc) + "\nTổng tiền cọc cần thu: "
									+ formatMoney((int) tienCoc));

					JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
					if (parentFrame instanceof FrmLeTan) {
						((FrmLeTan) parentFrame).refreshSoDoBan();
						((FrmLeTan) parentFrame).loadDanhSachDatCho();
					}
					dispose();
				} else {
					JOptionPane.showMessageDialog(this, "Lỗi: Đã tạo phiếu nhưng không thể cập nhật trạng thái bàn!",
							"Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this,
						"Lỗi: Không thể lưu thông tin đặt chỗ / món đặt trước vào cơ sở dữ liệu!", "Lỗi CSDL",
						JOptionPane.ERROR_MESSAGE);
			}
		});

		footer.add(btnHuy);
		footer.add(btnXacNhan);
		return footer;
	}

	private JPanel createInputGroup(String title, JComponent input) {
		JPanel panel = new JPanel(new BorderLayout(0, 5));
		panel.setBackground(Color.WHITE);
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

		JLabel lbl = new JLabel(title);
		lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

		if (input instanceof JTextField) {
			((JTextField) input).setPreferredSize(new Dimension(0, 35));
			input.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_CLR),
					new EmptyBorder(0, 10, 0, 10)));
		} else if (input instanceof JComboBox) {
			input.setPreferredSize(new Dimension(0, 35));
		}

		panel.add(lbl, BorderLayout.NORTH);
		panel.add(input, BorderLayout.CENTER);
		return panel;
	}

	private JPanel createFoodItem(String maMonAn, String emoji, String name, int price, String category) {
		JPanel item = new JPanel(new BorderLayout(10, 0));
		item.setBackground(Color.WHITE);
		item.setBorder(new EmptyBorder(10, 10, 10, 10));
		item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

		JLabel lblImg = new JLabel(emoji, SwingConstants.CENTER);
		lblImg.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
		lblImg.setPreferredSize(new Dimension(60, 60));
		lblImg.setBorder(BorderFactory.createLineBorder(BORDER_CLR));

		JPanel info = new JPanel(new GridLayout(3, 1));
		info.setBackground(Color.WHITE);

		JLabel lblName = new JLabel(name);
		lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));

		JLabel lblPrice = new JLabel(formatMoney(price));
		lblPrice.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblPrice.setForeground(RED_MAIN);

		JLabel lblCat = new JLabel(category);
		lblCat.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblCat.setForeground(TEXT_GRAY);

		info.add(lblName);
		info.add(lblPrice);
		info.add(lblCat);

		JButton btnAdd = new JButton("Thêm");
		btnAdd.setBackground(RED_MAIN);
		btnAdd.setForeground(Color.WHITE);
		btnAdd.setFocusPainted(false);
		btnAdd.setBorderPainted(false);

		btnAdd.addActionListener(e -> {
			boolean isExist = false;

			for (int i = 0; i < tbModelDaChon.getRowCount(); i++) {
				String maMonDangCo = tbModelDaChon.getValueAt(i, 0).toString();
				if (maMonDangCo.equals(maMonAn)) {
					int slCu = Integer.parseInt(tbModelDaChon.getValueAt(i, 2).toString());
					tbModelDaChon.setValueAt(slCu + 1, i, 2);
					isExist = true;
					break;
				}
			}

			if (!isExist) {
				tbModelDaChon.addRow(new Object[] { maMonAn, name, 1, price });
			}

			tongTien += price;
			capNhatTienCoc();
		});

		item.add(lblImg, BorderLayout.WEST);
		item.add(info, BorderLayout.CENTER);
		item.add(btnAdd, BorderLayout.EAST);

		return item;
	}

	private String formatMoney(int amount) {
		return String.format("%,d đ", amount).replace(',', '.');
	}

	class ComboItem {
		private final String key;
		private final String value;

		public ComboItem(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return value;
		}
	}
}
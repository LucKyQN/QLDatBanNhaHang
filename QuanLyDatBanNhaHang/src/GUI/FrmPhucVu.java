package GUI;

import DAO.PhucVuService;
import DAO.PhucVuServiceDb;
import Entity.MonAn;
import Entity.NhanVien;
import Model.BanAnModel;
import Model.MonAnModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FrmPhucVu extends JFrame {

	private final NhanVien nhanVien;
	private final PhucVuService phucVuService = new PhucVuServiceDb();

	private static final Color RED_MAIN = new Color(220, 38, 38);
	private static final Color BG_MAIN = new Color(248, 248, 248);
	private static final Color TEXT_DARK = new Color(20, 20, 20);
	private static final Color TEXT_GRAY = new Color(120, 120, 120);
	private static final Color BORDER_CLR = new Color(225, 225, 225);

	private List<BanAnModel> danhSachBan;
	private BanAnModel banDangChon;
	private JPanel pnlDanhSachBan;
	private JPanel pnlChiTiet;
	private JTable tblMon;

	private DefaultTableModel tblModel;

	private JButton btnYeuCauTT;
	private JButton btnThemMon;
	private SwingWorker<List<BanAnModel>, Void> currentWorker;

	public FrmPhucVu(NhanVien nhanVien) {
		this.nhanVien = nhanVien;
		initUI();
		taiDanhSachBan();

		Timer timer = new Timer(5000, e -> {
			if (tblMon != null && !tblMon.isEditing()) {
				taiDanhSachBan();
			}
		});
		timer.start();
	}

	private void initUI() {
		setTitle("Phục vụ & Gọi món - Nhà Hàng Ngói Đỏ");
		setSize(1440, 860);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		JPanel root = new JPanel(new BorderLayout());
		root.setBackground(BG_MAIN);
		root.add(createTopBar(), BorderLayout.NORTH);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createLeftPanel(), createRightPanel());
		split.setDividerLocation(300);
		split.setDividerSize(1);
		split.setBorder(null);
		root.add(split, BorderLayout.CENTER);
		setContentPane(root);
	}

	private void taiDanhSachBan() {
		if (currentWorker != null && !currentWorker.isDone()) {
			currentWorker.cancel(true);
		}

		currentWorker = new SwingWorker<>() {
			@Override
			protected List<BanAnModel> doInBackground() {
				return phucVuService.getDanhSachBanCanPhucVu();
			}

			@Override
			protected void done() {
				if (isCancelled())
					return;
				try {
					danhSachBan = get();
					veLaiDanhSachBan();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		currentWorker.execute();
	}

	private JPanel createTopBar() {
		JPanel bar = new JPanel(new BorderLayout(16, 0));
		bar.setBackground(Color.WHITE);
		bar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
				new EmptyBorder(14, 20, 14, 24)));

		// --- BÊN TRÁI: LOGO & TIÊU ĐỀ
		JPanel west = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
		west.setOpaque(false);
		JLabel lblLogo = new JLabel("🏮");
		lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
		lblLogo.setForeground(RED_MAIN);

		JPanel info = new JPanel();
		info.setOpaque(false);
		info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
		JLabel lbTitle = new JLabel("Phục vụ — Quản lý món theo bàn");
		lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
		JLabel lbRole = new JLabel("Nhân viên: " + nhanVien.getHoTenNV());
		lbRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lbRole.setForeground(TEXT_GRAY);
		info.add(lbTitle);
		info.add(lbRole);

		west.add(lblLogo);
		west.add(info);

		// --- BÊN PHẢI: CÀI ĐẶT, THỰC ĐƠN, ĐĂNG XUẤT ---
		JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
		right.setOpaque(false);

		JButton btnCaiDat = new JButton("Cài đặt");
		btnCaiDat.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnCaiDat.setBackground(new Color(240, 240, 240));
		btnCaiDat.setFocusPainted(false);
		btnCaiDat.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnCaiDat.addActionListener(e -> {
			JDialog dialog = new JDialog(this, "Cài đặt cá nhân", true);
			dialog.setSize(900, 700);
			dialog.setLocationRelativeTo(this);
			dialog.setContentPane(new GUI.FrmCaiDat());
			dialog.setVisible(true);
		});

		JButton btnMenu = new JButton("Xem thực đơn");
		btnMenu.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnMenu.setBackground(RED_MAIN);
		btnMenu.setForeground(Color.WHITE);
		btnMenu.setFocusPainted(false);
		btnMenu.addActionListener(e -> {
			FrmMenu frmMenu = new FrmMenu();
			frmMenu.setVisible(true);
		});

		// NÚT ĐĂNG XUẤT CHUẨN
		JButton btnLogout = new JButton("Đăng xuất");
		btnLogout.setContentAreaFilled(false);
		btnLogout.setBorderPainted(false);
		btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnLogout.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnLogout.setForeground(RED_MAIN);
		btnLogout.addActionListener(e -> {
			int c = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất không?", "Xác nhận",
					JOptionPane.YES_NO_OPTION);
			if (c == JOptionPane.YES_OPTION) {
				dispose();
				new FrmDangNhap().setVisible(true);
			}
		});

		right.add(btnCaiDat);
		right.add(btnMenu);
		right.add(btnLogout);

		bar.add(west, BorderLayout.WEST);
		bar.add(right, BorderLayout.EAST);
		return bar;
	}

	private JPanel createLeftPanel() {
		JPanel left = new JPanel(new BorderLayout());
		left.setBackground(Color.WHITE);
		left.setPreferredSize(new Dimension(300, 0));
		left.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_CLR));

		JLabel lbTitle = new JLabel("Bàn đang phục vụ");
		lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lbTitle.setBorder(new EmptyBorder(16, 16, 12, 16));

		pnlDanhSachBan = new JPanel();
		pnlDanhSachBan.setBackground(Color.WHITE);
		pnlDanhSachBan.setLayout(new BoxLayout(pnlDanhSachBan, BoxLayout.Y_AXIS));
		pnlDanhSachBan.setBorder(new EmptyBorder(0, 12, 12, 12));

		JScrollPane scroll = new JScrollPane(pnlDanhSachBan);
		scroll.setBorder(null);

		left.add(lbTitle, BorderLayout.NORTH);
		left.add(scroll, BorderLayout.CENTER);
		return left;
	}

	private void veLaiDanhSachBan() {
		SwingUtilities.invokeLater(() -> {
			pnlDanhSachBan.removeAll();

			if (danhSachBan != null && !danhSachBan.isEmpty()) {
				java.util.Set<String> processedIds = new java.util.HashSet<>();

				for (BanAnModel ban : danhSachBan) {
					if (!processedIds.contains(ban.maBan)) {
						pnlDanhSachBan.add(taoTheBan(ban));
						pnlDanhSachBan.add(Box.createVerticalStrut(10));
						processedIds.add(ban.maBan);
					}
				}
			}

			pnlDanhSachBan.revalidate();
			pnlDanhSachBan.repaint();
		});
	}

	private JPanel taoTheBan(BanAnModel ban) {
		boolean selected = ban == banDangChon;

		JPanel card = new JPanel(new BorderLayout(0, 4));
		card.setBackground(selected ? new Color(254, 242, 242) : Color.WHITE);
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(selected ? RED_MAIN : BORDER_CLR, selected ? 2 : 1, true),
				new EmptyBorder(12, 14, 12, 14)));
		card.setCursor(new Cursor(Cursor.HAND_CURSOR));
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

		JLabel lbBan = new JLabel(ban.tenBan);
		lbBan.setFont(new Font("Segoe UI", Font.BOLD, 14));

		JLabel lbTong = new JLabel(formatTien(ban.tamTinh) + " đ");
		lbTong.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lbTong.setForeground(RED_MAIN);

		card.add(lbBan, BorderLayout.NORTH);
		card.add(lbTong, BorderLayout.SOUTH);

		card.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				banDangChon = ban;
				veLaiDanhSachBan();
				hienThiChiTietBan(ban);
			}
		});
		return card;
	}

	private JPanel createRightPanel() {
		pnlChiTiet = new JPanel(new BorderLayout());
		pnlChiTiet.setBackground(BG_MAIN);
		hienThiGoiChonBan();
		return pnlChiTiet;
	}

	private void hienThiGoiChonBan() {
		pnlChiTiet.removeAll();
		JLabel lb = new JLabel("Chọn một bàn để xem chi tiết.");
		lb.setHorizontalAlignment(SwingConstants.CENTER);
		pnlChiTiet.add(lb, BorderLayout.CENTER);
		pnlChiTiet.revalidate();
		pnlChiTiet.repaint();
	}

	private void hienThiChiTietBan(BanAnModel ban) {
		pnlChiTiet.removeAll();

		JPanel wrap = new JPanel(new BorderLayout(0, 12));
		wrap.setBorder(new EmptyBorder(20, 24, 20, 24));
		wrap.setBackground(BG_MAIN);

		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		JLabel lbHd = new JLabel(ban.tenBan + " — HĐ " + (ban.maHD == null ? "---" : ban.maHD));
		lbHd.setFont(new Font("Segoe UI", Font.BOLD, 18));
		header.add(lbHd, BorderLayout.WEST);

		tblModel = new DefaultTableModel(
				new String[] { "ID", "Mã món", "Tên món", "SL", "Đơn giá", "Thành tiền", "Trạng thái" }, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return c == 6 && !"Chờ thanh toán".equalsIgnoreCase(ban.trangThai);
			}
		};

		tblMon = new JTable(tblModel);
		tblMon.setRowHeight(35);
		tblMon.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

		tblMon.getColumnModel().getColumn(0).setMinWidth(0);
		tblMon.getColumnModel().getColumn(0).setMaxWidth(0);
		tblMon.getColumnModel().getColumn(1).setMinWidth(0);
		tblMon.getColumnModel().getColumn(1).setMaxWidth(0);

		JComboBox<String> cboStatus = new JComboBox<>(new String[] { "Chưa lên", "Đã lên", "Mang về", "Hủy" });
		tblMon.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(cboStatus));

		btnYeuCauTT = new JButton("YÊU CẦU THANH TOÁN");
		btnYeuCauTT.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnYeuCauTT.setForeground(Color.WHITE);
		btnYeuCauTT.setFocusPainted(false);
		btnYeuCauTT.setBorderPainted(false);

		btnThemMon = new JButton("Thêm món");
		btnThemMon.setFocusPainted(false);

		Runnable checkNutThanhToan = () -> {
			boolean conMonChuaLen = false;

			for (int i = 0; i < tblModel.getRowCount(); i++) {
				Object obj = tblModel.getValueAt(i, 6);
				String trangThai = obj == null ? "" : obj.toString().trim();
				if ("Chưa lên".equalsIgnoreCase(trangThai)) {
					conMonChuaLen = true;
					break;
				}
			}

			boolean choThanhToan = "Chờ thanh toán".equalsIgnoreCase(ban.trangThai);

			if (btnYeuCauTT != null) {
				boolean enable = !conMonChuaLen && !choThanhToan && tblModel.getRowCount() > 0;
				btnYeuCauTT.setEnabled(enable);
				btnYeuCauTT.setBackground(enable ? RED_MAIN : Color.LIGHT_GRAY);
				btnYeuCauTT.setForeground(enable ? Color.WHITE : Color.DARK_GRAY);
			}

			if (btnThemMon != null) {
				boolean enableThem = !choThanhToan;
				btnThemMon.setEnabled(enableThem);
				btnThemMon.setBackground(enableThem ? null : Color.LIGHT_GRAY);
				btnThemMon.setForeground(enableThem ? null : Color.DARK_GRAY);
			}
		};

		tblModel.addTableModelListener(e -> {
			if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 6) {
				int row = e.getFirstRow();

				Object idObj = tblModel.getValueAt(row, 0);
				if (idObj == null) {
					System.err.println("LỖI: Cột ID đang bị trống (null)!");
					return;
				}

				int idCTHD = Integer.parseInt(idObj.toString());
				String ttMoi = String.valueOf(tblModel.getValueAt(row, 6));

				boolean success = phucVuService.capNhatTrangThaiMon(idCTHD, ttMoi);

				if (success) {
					SwingUtilities.invokeLater(checkNutThanhToan);
				} else {
					System.err.println("SQL: Cập nhật trạng thái thất bại!");
				}
			}
		});

		napBangMonTuHoaDon(ban.maHD);

		JScrollPane scroll = new JScrollPane(tblMon);
		scroll.setBorder(BorderFactory.createLineBorder(BORDER_CLR));

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		actions.setOpaque(false);

		btnThemMon.addActionListener(e -> {
			if ("Chờ thanh toán".equalsIgnoreCase(ban.trangThai)) {
				JOptionPane.showMessageDialog(this, "Bàn này đã yêu cầu thanh toán, không thể thêm món nữa.",
						"Không thể thêm món", JOptionPane.WARNING_MESSAGE);
				return;
			}

			boolean daThem = moHopThoaiThemNhieuMon(ban);
			if (daThem) {
				napBangMonTuHoaDon(ban.maHD);
				tblModel.fireTableDataChanged();
				SwingUtilities.invokeLater(checkNutThanhToan);
				taiDanhSachBan();
			}
		});

		btnYeuCauTT.addActionListener(e -> {
			int c = JOptionPane.showConfirmDialog(this, "Xác nhận yêu cầu thanh toán cho " + ban.tenBan + "?",
					"Xác nhận", JOptionPane.YES_NO_OPTION);

			if (c == JOptionPane.YES_OPTION) {
				if (phucVuService.yeuCauThanhToan(ban.maHD, ban.maBan)) {
					JOptionPane.showMessageDialog(this, "Đã gửi yêu cầu!");

					ban.trangThai = "Chờ thanh toán";

					btnThemMon.setEnabled(false);
					btnThemMon.setBackground(Color.LIGHT_GRAY);
					btnThemMon.setForeground(Color.DARK_GRAY);

					btnYeuCauTT.setEnabled(false);
					btnYeuCauTT.setBackground(Color.LIGHT_GRAY);
					btnYeuCauTT.setForeground(Color.DARK_GRAY);

					napBangMonTuHoaDon(ban.maHD);
					tblModel.fireTableDataChanged();
					SwingUtilities.invokeLater(checkNutThanhToan);
					taiDanhSachBan();
				}
			}
		});

		actions.add(btnThemMon);
		actions.add(Box.createHorizontalStrut(100));
		actions.add(btnYeuCauTT);

		wrap.add(header, BorderLayout.NORTH);
		wrap.add(scroll, BorderLayout.CENTER);
		wrap.add(actions, BorderLayout.SOUTH);
		pnlChiTiet.add(wrap, BorderLayout.CENTER);

		checkNutThanhToan.run();

		pnlChiTiet.revalidate();
		pnlChiTiet.repaint();
	}

	private void napBangMonTuHoaDon(String maHD) {
		tblModel.setRowCount(0);

		List<MonAnModel> ds = phucVuService.getChiTietHoaDon(maHD);

		for (MonAnModel m : ds) {
			tblModel.addRow(new Object[] {
					m.id_cthd,
					m.maMonAn,
					m.tenMonAn,
					m.soLuong,
					formatTien(m.giaBan),
					formatTien(m.thanhTien),
					m.trangThaiPhucVu
			});
		}
	}

	private boolean moHopThoaiThemNhieuMon(BanAnModel ban) {
		if (ban.maHD == null || ban.maHD.trim().isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"Bàn " + ban.tenBan + " chưa có hóa đơn (có thể là khách đặt trước).\n"
							+ "Vui lòng liên hệ Lễ Tân Check-in để có Mã HD trước khi thêm món!",
					"Không tìm thấy Hóa Đơn", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		if ("Chờ thanh toán".equalsIgnoreCase(ban.trangThai)) {
			JOptionPane.showMessageDialog(this, "Bàn này đã yêu cầu thanh toán, không thể thêm món nữa.",
					"Không thể thêm món", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		List<MonAn> dsMon = phucVuService.getMonAnDangPhucVu();

		DefaultListModel<MonAn> listModel = new DefaultListModel<>();
		for (MonAn mon : dsMon) {
			listModel.addElement(mon);
		}

		JList<MonAn> listMon = new JList<>(listModel);
		listMon.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listMon.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof MonAn m) {
					setText(m.getTenMon() + " - " + formatTien((long) m.getGiaMon()) + " đ");
				}
				return this;
			}
		});
		JTextField txtSearch = new JTextField();
		txtSearch.setPreferredSize(new Dimension(0, 32));
		txtSearch.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(200, 200, 200)),
				new EmptyBorder(0, 8, 0, 8)));

		// Bắt sự kiện gõ phím đến đâu, lọc danh sách đến đó
		txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			public void insertUpdate(javax.swing.event.DocumentEvent e) {
				filter();
			}

			public void removeUpdate(javax.swing.event.DocumentEvent e) {
				filter();
			}

			public void changedUpdate(javax.swing.event.DocumentEvent e) {
				filter();
			}

			private void filter() {
				String keyword = txtSearch.getText().toLowerCase();
				listModel.clear(); // Xóa danh sách hiện tại
				for (MonAn mon : dsMon) {

					if (mon.getTenMon().toLowerCase().contains(keyword)) {
						listModel.addElement(mon);
					}
				}
			}
		});
		DefaultTableModel modelTam = new DefaultTableModel(
				new String[] { "Mã món", "Tên món", "SL", "Đơn giá", "Ghi chú" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 2 || column == 4;
			}
		};

		JTable tblTam = new JTable(modelTam);
		tblTam.setRowHeight(28);
		tblTam.getColumnModel().getColumn(0).setMinWidth(0);
		tblTam.getColumnModel().getColumn(0).setMaxWidth(0);
		tblTam.getColumnModel().getColumn(0).setWidth(0);

		JSpinner spSoLuong = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
		JTextField txtGhiChu = new JTextField();

		JButton btnThemVaoDS = new JButton("Thêm vào danh sách");
		btnThemVaoDS.addActionListener(e -> {
			MonAn mon = listMon.getSelectedValue();
			if (mon == null) {
				JOptionPane.showMessageDialog(this, "Vui lòng chọn món.");
				return;
			}

			int soLuong = (int) spSoLuong.getValue();
			String ghiChu = txtGhiChu.getText().trim();

			boolean daCo = false;
			for (int i = 0; i < modelTam.getRowCount(); i++) {
				String maMon = modelTam.getValueAt(i, 0).toString();
				String ghiChuCu = modelTam.getValueAt(i, 4) == null ? "" : modelTam.getValueAt(i, 4).toString();

				if (maMon.equals(mon.getMaMonAn()) && ghiChuCu.equals(ghiChu)) {
					int slCu = Integer.parseInt(modelTam.getValueAt(i, 2).toString());
					modelTam.setValueAt(slCu + soLuong, i, 2);
					daCo = true;
					break;
				}
			}

			if (!daCo) {
				modelTam.addRow(
						new Object[] { mon.getMaMonAn(), mon.getTenMon(), soLuong, (long) mon.getGiaMon(), ghiChu });
			}

			spSoLuong.setValue(1);
			txtGhiChu.setText("");
		});

		JButton btnXoaDong = new JButton("Xóa món đã chọn");
		btnXoaDong.addActionListener(e -> {
			int row = tblTam.getSelectedRow();
			if (row >= 0) {
				modelTam.removeRow(row);
			}
		});

		// Bố cục lại panel bên trái để nhét ô tìm kiếm vào
		JPanel pnlTopLeft = new JPanel(new BorderLayout(0, 5));
		JLabel lblTitleSearch = new JLabel("Tìm món nhanh:");
		lblTitleSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
		pnlTopLeft.add(lblTitleSearch, BorderLayout.NORTH);
		pnlTopLeft.add(txtSearch, BorderLayout.CENTER);

		JPanel left = new JPanel(new BorderLayout(0, 8));
		left.add(pnlTopLeft, BorderLayout.NORTH);
		left.add(new JScrollPane(listMon), BorderLayout.CENTER);

		JPanel addBox = new JPanel(new GridLayout(0, 1, 6, 6));
		addBox.add(new JLabel("Số lượng:"));
		addBox.add(spSoLuong);
		addBox.add(new JLabel("Ghi chú:"));
		addBox.add(txtGhiChu);
		addBox.add(btnThemVaoDS);
		left.add(addBox, BorderLayout.SOUTH);

		JPanel right = new JPanel(new BorderLayout(0, 8));
		right.add(new JLabel("Món sẽ thêm vào bàn"), BorderLayout.NORTH);
		right.add(new JScrollPane(tblTam), BorderLayout.CENTER);
		right.add(btnXoaDong, BorderLayout.SOUTH);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
		split.setDividerLocation(320);
		split.setResizeWeight(0.45);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(820, 420));
		panel.add(split, BorderLayout.CENTER);

		int result = JOptionPane.showConfirmDialog(this, panel, "Thêm món cho " + ban.tenBan,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result != JOptionPane.OK_OPTION) {
			return false;
		}

		if (modelTam.getRowCount() == 0) {
			JOptionPane.showMessageDialog(this, "Bạn chưa thêm món nào.");
			return false;
		}

		boolean allOk = true;

		for (int i = 0; i < modelTam.getRowCount(); i++) {
			String maMonAn = modelTam.getValueAt(i, 0).toString();
			int soLuong = Integer.parseInt(modelTam.getValueAt(i, 2).toString());
			String ghiChu = modelTam.getValueAt(i, 4) == null ? "" : modelTam.getValueAt(i, 4).toString();

			boolean ok = phucVuService.themHoacTangMon(ban.maHD, maMonAn, soLuong, ghiChu);
			if (!ok) {
				allOk = false;
				break;
			}
		}

		if (allOk) {
			return true;
		} else {
			JOptionPane.showMessageDialog(this, "Có lỗi khi thêm món vào hóa đơn!");
			return false;
		}
	}

	private static String formatTien(long so) {
		return NumberFormat.getInstance(new Locale("vi", "VN")).format(so);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			NhanVien nv = new NhanVien();
			nv.setHoTenNV("Nguyễn Văn A");
			new FrmPhucVu(nv).setVisible(true);
		});
	}
}

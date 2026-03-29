package GUI;

import DAO.PhucVuService;
import DAO.PhucVuServiceDb;
import Entity.MonAn;
import Model.BanAnModel;
import Model.MonAnModel;
import DAO.PhucVuServiceMemory;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Giao diện phục vụ: xem các bàn đang có khách (hóa đơn chưa thanh toán) và
 * thao tác CRUD món trên hóa đơn của từng bàn.
 */
public class FrmPhucVu extends JFrame {

	private final String tenNhanVien;
	private final PhucVuService phucVuService;

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

//    public FrmPhucVu(String tenNhanVien) {
//        this.tenNhanVien = tenNhanVien;
//        initUI();
//        taiDanhSachBan();
//    }
	// Sửa lại thành 2 tham số
	public FrmPhucVu(String tenNhanVien, PhucVuServiceMemory phucVuService) {
		this.phucVuService = phucVuService; // Gán giá trị được truyền vào
		this.tenNhanVien = tenNhanVien;
		initUI();
		taiDanhSachBan();
	}

	// Thêm hàm này để hỗ trợ gọi 1 tham số từ giao diện Đăng nhập
	public FrmPhucVu(String tenNhanVien) {
        // Gọi lại hàm 2 tham số, nhưng truyền vào một Service mới (hoặc null tùy logic của bạn)
        this(tenNhanVien, new DAO.PhucVuServiceMemory()); 
    }

	private void initUI() {
		setTitle("Phục vụ & Gọi món - Nhà Hàng Ngói Đỏ");
		setSize(1440, 860);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

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
		String maBanGiu = banDangChon != null ? banDangChon.maBan : null;
		String maHDGiu = banDangChon != null ? banDangChon.maHD : null;

		SwingWorker<List<BanAnModel>, Void> worker = new SwingWorker<>() {
			@Override
			protected List<BanAnModel> doInBackground() {
				return phucVuService.getDanhSachBanChuaThanhToan();
			}

			@Override
			protected void done() {
				try {
					danhSachBan = get();
					banDangChon = null;
					if (maBanGiu != null && danhSachBan != null) {
						for (BanAnModel b : danhSachBan) {
							if (maBanGiu.equals(b.maBan) && maHDGiu != null && maHDGiu.equals(b.maHD)) {
								banDangChon = b;
								break;
							}
						}
					}
					veLaiDanhSachBan();
					if (banDangChon != null) {
						hienThiChiTietBan(banDangChon);
					} else {
						hienThiGoiChonBan();
					}
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(FrmPhucVu.this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		worker.execute();
	}

	private JPanel createTopBar() {
		JPanel bar = new JPanel(new BorderLayout(16, 0));
		bar.setBackground(Color.WHITE);
		bar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
				new EmptyBorder(14, 20, 14, 24)));

		JPanel west = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
		west.setOpaque(false);

		JLabel btnBack = new JLabel("←");
		btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 22));
		btnBack.setForeground(TEXT_DARK);
		btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnBack.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dispose();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				btnBack.setForeground(RED_MAIN);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btnBack.setForeground(TEXT_DARK);
			}
		});

		JButton btnLogout = new JButton("Đăng xuất");
		btnLogout.setFocusPainted(false);
		btnLogout.setContentAreaFilled(false);
		btnLogout.setBorderPainted(false);
		btnLogout.setForeground(RED_MAIN);
		btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnLogout.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnLogout.addActionListener(e -> {
			int c = JOptionPane.showConfirmDialog(this, "Đăng xuất và quay về màn hình đăng nhập?", "Xác nhận",
					JOptionPane.YES_NO_OPTION);
			if (c == JOptionPane.YES_OPTION) {
				dispose();
				new FrmDangNhap().setVisible(true);
			}
		});

		west.add(btnBack);
		west.add(btnLogout);

		JPanel info = new JPanel();
		info.setOpaque(false);
		info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
		JLabel lbTitle = new JLabel("Phục vụ — Quản lý món theo bàn");
		lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
		JLabel lbRole = new JLabel("Phục vụ  |  " + tenNhanVien);
		lbRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lbRole.setForeground(TEXT_GRAY);
		info.add(lbTitle);
		info.add(lbRole);

		bar.add(west, BorderLayout.WEST);
		bar.add(info, BorderLayout.CENTER);
		return bar;
	}

	private JPanel createLeftPanel() {
		JPanel left = new JPanel(new BorderLayout());
		left.setBackground(Color.WHITE);
		left.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_CLR));
		left.setPreferredSize(new Dimension(300, 0));

		JLabel lbTitle = new JLabel("Bàn đang phục vụ");
		lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lbTitle.setBorder(new EmptyBorder(16, 16, 12, 16));

		pnlDanhSachBan = new JPanel();
		pnlDanhSachBan.setBackground(Color.WHITE);
		pnlDanhSachBan.setLayout(new BoxLayout(pnlDanhSachBan, BoxLayout.Y_AXIS));
		pnlDanhSachBan.setBorder(new EmptyBorder(0, 12, 12, 12));
		JScrollPane scroll = new JScrollPane(pnlDanhSachBan);
		scroll.setBorder(null);
		scroll.getVerticalScrollBar().setUnitIncrement(12);

		left.add(lbTitle, BorderLayout.NORTH);
		left.add(scroll, BorderLayout.CENTER);
		return left;
	}

	private void veLaiDanhSachBan() {
		pnlDanhSachBan.removeAll();
		if (danhSachBan == null || danhSachBan.isEmpty()) {
			JLabel lb = new JLabel(
					"<html><p style='width:220px'>Chưa có bàn nào đang có khách<br>(hóa đơn chưa thanh toán).</p></html>");
			lb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
			lb.setForeground(TEXT_GRAY);
			lb.setBorder(new EmptyBorder(12, 8, 0, 8));
			pnlDanhSachBan.add(lb);
		} else {
			for (BanAnModel ban : danhSachBan) {
				pnlDanhSachBan.add(taoTheBan(ban));
				pnlDanhSachBan.add(Box.createVerticalStrut(10));
			}
		}
		pnlDanhSachBan.revalidate();
		pnlDanhSachBan.repaint();
	}

	private JPanel taoTheBan(BanAnModel ban) {
		boolean selected = ban == banDangChon;
		JPanel card = new JPanel(new BorderLayout(0, 4));
		card.setBackground(selected ? new Color(254, 242, 242) : Color.WHITE);
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(selected ? RED_MAIN : BORDER_CLR, selected ? 2 : 1, true),
				new EmptyBorder(12, 14, 12, 14)));
		card.setCursor(new Cursor(Cursor.HAND_CURSOR));
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 88));

		JPanel topRow = new JPanel(new BorderLayout());
		topRow.setOpaque(false);
		JLabel lbBan = new JLabel(ban.tenBan);
		lbBan.setFont(new Font("Segoe UI", Font.BOLD, 14));
		JLabel lbNguoi = new JLabel(ban.sucChua + " chỗ");
		lbNguoi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lbNguoi.setForeground(TEXT_GRAY);
		topRow.add(lbBan, BorderLayout.WEST);
		topRow.add(lbNguoi, BorderLayout.EAST);

		JLabel lbMa = new JLabel("Mã HĐ: " + ban.maHD);
		lbMa.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lbMa.setForeground(TEXT_GRAY);

		JLabel lbTong = new JLabel(formatTien(ban.tamTinh) + " đ");
		lbTong.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lbTong.setForeground(RED_MAIN);

		JPanel mid = new JPanel();
		mid.setOpaque(false);
		mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));
		mid.add(lbMa);
		mid.add(Box.createVerticalStrut(2));
		mid.add(lbTong);

		card.add(topRow, BorderLayout.NORTH);
		card.add(mid, BorderLayout.CENTER);

		card.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				banDangChon = ban;
				veLaiDanhSachBan();
				hienThiChiTietBan(ban);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (ban != banDangChon) {
					card.setBackground(new Color(250, 250, 250));
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (ban != banDangChon) {
					card.setBackground(Color.WHITE);
				}
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
		JLabel lb = new JLabel("Chọn một bàn bên trái để xem và chỉnh sửa món.");
		lb.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lb.setForeground(TEXT_GRAY);
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
		JLabel lbHd = new JLabel(ban.tenBan + " — HĐ " + ban.maHD);
		lbHd.setFont(new Font("Segoe UI", Font.BOLD, 18));
		JLabel lbSub = new JLabel("Sức chứa " + ban.sucChua + " · Tạm tính: " + formatTien(ban.tamTinh) + " đ");
		lbSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lbSub.setForeground(TEXT_GRAY);
		JPanel leftH = new JPanel();
		leftH.setOpaque(false);
		leftH.setLayout(new BoxLayout(leftH, BoxLayout.Y_AXIS));
		leftH.add(lbHd);
		leftH.add(Box.createVerticalStrut(4));
		leftH.add(lbSub);
		header.add(leftH, BorderLayout.WEST);

		JButton btnRefresh = new JButton("Làm mới");
		btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		btnRefresh.addActionListener(e -> taiDanhSachBan());
		header.add(btnRefresh, BorderLayout.EAST);

		tblModel = new DefaultTableModel(new String[] { "Mã món", "Tên món", "SL", "Đơn giá", "Thành tiền" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tblMon = new JTable(tblModel);
		tblMon.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		tblMon.setRowHeight(26);
		tblMon.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblMon.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

		napBangMonTuHoaDon(ban.maHD);

		if (tblMon.getColumnModel().getColumnCount() > 0) {
			tblMon.getColumnModel().getColumn(0).setMinWidth(0);
			tblMon.getColumnModel().getColumn(0).setMaxWidth(0);
			tblMon.getColumnModel().getColumn(0).setWidth(0);
		}

		JScrollPane scroll = new JScrollPane(tblMon);
		scroll.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
		scroll.getVerticalScrollBar().setUnitIncrement(12);

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		actions.setOpaque(false);

		JButton btnThem = new JButton("Thêm món");
		JButton btnSuaSl = new JButton("Đổi số lượng");
		JButton btnXoa = new JButton("Xóa món");
		for (JButton b : new JButton[] { btnThem, btnSuaSl, btnXoa }) {
			b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		}
		btnThem.addActionListener(e -> moHopThoaiThemMon(ban));
		btnSuaSl.addActionListener(e -> doiSoLuongDongChon(ban));
		btnXoa.addActionListener(e -> xoaDongChon(ban));

		actions.add(btnThem);
		actions.add(btnSuaSl);
		actions.add(btnXoa);

		JPanel center = new JPanel(new BorderLayout(0, 10));
		center.setOpaque(false);
		center.add(scroll, BorderLayout.CENTER);
		center.add(actions, BorderLayout.SOUTH);

		wrap.add(header, BorderLayout.NORTH);
		wrap.add(center, BorderLayout.CENTER);
		pnlChiTiet.add(wrap, BorderLayout.CENTER);
		pnlChiTiet.revalidate();
		pnlChiTiet.repaint();
	}

	private void napBangMonTuHoaDon(String maHD) {
		tblModel.setRowCount(0);
		List<MonAnModel> ds = phucVuService.getChiTietHoaDon(maHD);
		for (MonAnModel m : ds) {
			tblModel.addRow(
					new Object[] { m.maMonAn, m.tenMonAn, m.soLuong, formatTien(m.donGia), formatTien(m.thanhTien) });
		}
	}

	private void moHopThoaiThemMon(BanAnModel ban) {
		List<MonAn> hopLe = phucVuService.getMonAnDangPhucVu();
		if (hopLe.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Không có món nào đang kinh doanh.", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		MonAn[] arr = hopLe.toArray(new MonAn[0]);
		JComboBox<MonAn> cb = new JComboBox<>(arr);
		cb.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof MonAn) {
					MonAn x = (MonAn) value;
					setText(x.getTenMon() + " (" + formatTien((long) x.getGiaMon()) + " đ)");
				}
				return this;
			}
		});

		SpinnerNumberModel spModel = new SpinnerNumberModel(1, 1, 999, 1);
		JSpinner spSl = new JSpinner(spModel);
		JPanel p = new JPanel(new GridLayout(0, 1, 8, 8));
		p.add(new JLabel("Chọn món:"));
		p.add(cb);
		p.add(new JLabel("Số lượng:"));
		p.add(spSl);

		int ok = JOptionPane.showConfirmDialog(this, p, "Thêm món vào " + ban.tenBan, JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		if (ok != JOptionPane.OK_OPTION) {
			return;
		}

		MonAn chon = (MonAn) cb.getSelectedItem();
		if (chon == null) {
			return;
		}
		int sl = (Integer) spSl.getValue();
		boolean done = phucVuService.themHoacTangMon(ban.maHD, chon.getMaMonAn(), sl);
		if (done) {
			taiDanhSachBan();
		} else {
			JOptionPane.showMessageDialog(this, "Không thêm được món. Kiểm tra món và hóa đơn.", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void doiSoLuongDongChon(BanAnModel ban) {
		int row = tblMon.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Chọn một dòng món trong bảng.", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		String maMon = (String) tblModel.getValueAt(row, 0);
		int slCu = (Integer) tblModel.getValueAt(row, 2);
		String input = JOptionPane.showInputDialog(this, "Số lượng mới (0 = xóa món khỏi hóa đơn):", slCu);
		if (input == null) {
			return;
		}
		int slMoi;
		try {
			slMoi = Integer.parseInt(input.trim());
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}
		boolean done = phucVuService.capNhatSoLuongMon(ban.maHD, maMon, slMoi);
		if (done) {
			taiDanhSachBan();
		} else {
			JOptionPane.showMessageDialog(this, "Cập nhật không thành công.", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void xoaDongChon(BanAnModel ban) {
		int row = tblMon.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Chọn một dòng món để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
			return;
		}
		String maMon = (String) tblModel.getValueAt(row, 0);
		String ten = String.valueOf(tblModel.getValueAt(row, 1));
		int c = JOptionPane.showConfirmDialog(this, "Xóa \"" + ten + "\" khỏi hóa đơn?", "Xác nhận",
				JOptionPane.YES_NO_OPTION);
		if (c != JOptionPane.YES_OPTION) {
			return;
		}
		boolean done = phucVuService.xoaMonKhoiChiTiet(ban.maHD, maMon);
		if (done) {
			taiDanhSachBan();
		} else {
			JOptionPane.showMessageDialog(this, "Xóa không thành công.", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private static String formatTien(long so) {
		NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
		return nf.format(so);
	}
}

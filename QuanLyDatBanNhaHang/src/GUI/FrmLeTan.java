package GUI;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.util.List;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import Entity.PhieuDatBan;
import Entity.DlgNhapThongTinKhach;
import Entity.LuuLog;
import Entity.BanAn;
import Model.NhanVienModel;

public class FrmLeTan extends JFrame {

	private static final Color RED_MAIN = new Color(220, 38, 38);
	private static final Color BG_MAIN = new Color(248, 248, 248);
	private static final Color BORDER_CLR = new Color(230, 230, 230);
	private static final Color TEXT_DARK = new Color(40, 40, 40);
	private static final Color TEXT_GRAY = new Color(120, 120, 120);

	// Màu cho bàn TRỐNG (Xanh lá)
	private static final Color BG_TRONG = new Color(220, 252, 231);
	private static final Color BORDER_TRONG = new Color(34, 197, 94);

	// Màu cho bàn CÓ KHÁCH (Đỏ)
	private static final Color BG_KHACH = new Color(254, 226, 226);
	private static final Color BORDER_KHACH = new Color(239, 68, 68);

	// Màu cho bàn ĐÃ ĐẶT (Vàng)
	private static final Color BG_DAT = new Color(254, 249, 195);
	private static final Color BORDER_DAT = new Color(234, 179, 8);

	// Màu cho BÀN ĐANG GHÉP (Cam)
	private static final Color BG_GHEP = new Color(255, 237, 213);
	private static final Color BORDER_GHEP = new Color(249, 115, 22);

	private JPanel gridMap;
	private JLabel lblClock;
	private String currentTab = "Tầng 1";
	private JPanel tabsContainer;
	private JLabel lblMapTitle;
	private JComboBox<String> cboLocSucChua; // Bộ lọc tìm bàn trống

	// Panel chứa danh sách đặt bàn
	private JPanel pnlDanhSachDatCho;

	// DAO dùng chung
	private final DAO.BanAnDAO banAnDAO = new DAO.BanAnDAO();
	private final DAO.HoaDonDAO hoaDonDAO = new DAO.HoaDonDAO();
	private final DAO.PhieuDatBanDAO phieuDAO = new DAO.PhieuDatBanDAO();
	private JTextField txtTimKiemDatCho;
	private JComboBox<String> cboLocThoiGian;
	private DefaultTableModel modelDatCho;
	public FrmLeTan() {
		initUI();
		startClock();
	}

	private void initUI() {
		setTitle("Nhà Hàng Ngói Đỏ - Lễ Tân");
		setSize(1440, 860);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		JPanel root = new JPanel(new BorderLayout());
		root.setBackground(BG_MAIN);
		root.add(createTopBar(), BorderLayout.NORTH);

		JPanel centerWrap = new JPanel(new BorderLayout());
		centerWrap.setOpaque(false);
		centerWrap.add(createTabs(), BorderLayout.NORTH);
		centerWrap.add(createMapArea(), BorderLayout.CENTER);

		root.add(centerWrap, BorderLayout.CENTER);
		root.add(createRightSidebar(), BorderLayout.EAST);
		setContentPane(root);

		// Load danh sách ngay khi mở form
		loadDanhSachDatCho();
	}

	// TOP BAR & TABS

	private JPanel createTopBar() {
		JPanel bar = new JPanel(new BorderLayout());
		bar.setBackground(Color.WHITE);
		bar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
				new EmptyBorder(10, 20, 10, 20)));

		JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
		left.setOpaque(false);
		JLabel lblLogo = new JLabel("🏮");
		lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
		lblLogo.setForeground(RED_MAIN);
		JPanel textWrap = new JPanel(new GridLayout(2, 1));
		textWrap.setOpaque(false);
		JLabel lblName = new JLabel("Nhà Hàng Ngói Đỏ - Lễ Tân");
		lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
		JLabel lblSub = new JLabel("Quản lý đặt chỗ & Check-in");
		lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblSub.setForeground(TEXT_GRAY);
		textWrap.add(lblName);
		textWrap.add(lblSub);
		left.add(lblLogo);
		left.add(textWrap);

		JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
		right.setOpaque(false);
		lblClock = new JLabel("00:00:00");
		lblClock.setFont(new Font("Segoe UI", Font.PLAIN, 14));


		String tenNV = (Entity.LuuLog.nhanVienDangNhap != null)
				? Entity.LuuLog.nhanVienDangNhap.getTenNV()
				: "Lễ tân";
		JLabel lblUser = new JLabel("" + tenNV);
		lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));

		JButton btnCaiDat = new JButton("Cài đặt");
		btnCaiDat.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnCaiDat.setBackground(new Color(240, 240, 240));
		btnCaiDat.setFocusPainted(false);
		btnCaiDat.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnCaiDat.addActionListener(e -> {
			JDialog dialog = new JDialog(this, "Cài đặt cá nhân", true);
			dialog.setSize(900, 700);
			dialog.setLocationRelativeTo(this);
			dialog.setContentPane(new GUI.FrmCaiDat()); // Gọi form cài đặt
			dialog.setVisible(true);
		});
		JButton btnLogout = new JButton("Đăng xuất");
		btnLogout.setContentAreaFilled(false);
		btnLogout.setBorderPainted(false);
		btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnLogout.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnLogout.addActionListener(e -> {
			int c = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất không?", "Xác nhận",
					JOptionPane.YES_NO_OPTION);
			if (c == JOptionPane.YES_OPTION) {
				dispose();
				new FrmDangNhap().setVisible(true);
			}
		});
		right.add(lblClock);
		right.add(lblUser);
		right.add(btnCaiDat);
		right.add(btnLogout);

		bar.add(left, BorderLayout.WEST);
		bar.add(right, BorderLayout.EAST);
		return bar;
	}

	private void startClock() {
		new Timer(1000, e -> lblClock.setText("" + new SimpleDateFormat("HH:mm:ss").format(new Date()))).start();
	}

	private JPanel createTabs() {
		tabsContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
		tabsContainer.setBackground(Color.WHITE);
		tabsContainer.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR), new EmptyBorder(10, 20, 0, 20)));

		String[] tabNames = { "Tầng 1", "Tầng 2", "Phòng VIP" };
		for (int i = 0; i < tabNames.length; i++) {
			JLabel lbl = new JLabel(tabNames[i]);
			lbl.setFont(new Font("Segoe UI", i == 0 ? Font.BOLD : Font.PLAIN, 14));
			lbl.setForeground(i == 0 ? RED_MAIN : TEXT_DARK);
			lbl.setBorder(
					i == 0 ? BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, RED_MAIN),
							new EmptyBorder(0, 0, 7, 0)) : new EmptyBorder(0, 0, 10, 0));
			lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
			lbl.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					currentTab = lbl.getText();
					updateTabUI();
					refreshSoDoBan();
				}
			});
			tabsContainer.add(lbl);
		}
		return tabsContainer;
	}

	private void updateTabUI() {
		for (Component c : tabsContainer.getComponents()) {
			if (c instanceof JLabel lbl) {
				boolean sel = lbl.getText().equals(currentTab);
				lbl.setFont(new Font("Segoe UI", sel ? Font.BOLD : Font.PLAIN, 14));
				lbl.setForeground(sel ? RED_MAIN : TEXT_DARK);
				lbl.setBorder(
						sel ? BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, RED_MAIN),
								new EmptyBorder(0, 0, 7, 0)) : new EmptyBorder(0, 0, 10, 0));
			}
		}
	}

	// SƠ ĐỒ BÀN (Tích hợp bộ lọc)
	private JPanel createMapArea() {
		JPanel mapWrap = new JPanel(new BorderLayout());
		mapWrap.setOpaque(false);
		mapWrap.setBorder(new EmptyBorder(20, 20, 20, 20));

		// --- HEADER CHỨA TIÊU ĐỀ VÀ BỘ LỌC ---
		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setBorder(new EmptyBorder(0, 0, 20, 0));

		// Tiêu đề bên trái
		JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 5));
		pnlTitle.setOpaque(false);
		lblMapTitle = new JLabel("Sơ đồ bàn - " + currentTab);
		lblMapTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
		JLabel sub = new JLabel("Click vào bàn trống hoặc đã đặt để bắt đầu phục vụ");
		sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		sub.setForeground(TEXT_GRAY);
		pnlTitle.add(lblMapTitle);
		pnlTitle.add(sub);

		// Bộ lọc bên phải
		JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
		pnlFilter.setOpaque(false);
		JLabel lblFilter = new JLabel("Tìm bàn trống:");
		lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblFilter.setForeground(TEXT_DARK);

		String[] filterOptions = { "Tất cả", ">= 2 người", ">= 4 người", ">= 6 người", ">= 8 người", ">= 10 người" };
		cboLocSucChua = new JComboBox<>(filterOptions);
		cboLocSucChua.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		cboLocSucChua.setPreferredSize(new Dimension(120, 32));
		cboLocSucChua.setBackground(Color.WHITE);
		cboLocSucChua.setCursor(new Cursor(Cursor.HAND_CURSOR));

		// Bắt sự kiện lọc
		cboLocSucChua.addActionListener(e -> refreshSoDoBan());

		pnlFilter.add(lblFilter);
		pnlFilter.add(cboLocSucChua);

		header.add(pnlTitle, BorderLayout.WEST);
		header.add(pnlFilter, BorderLayout.EAST);
		// -------------------------------------

		gridMap = new JPanel(new GridLayout(0, 4, 15, 15));
		gridMap.setOpaque(false);
		JPanel wrapGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		wrapGrid.setOpaque(false);
		wrapGrid.add(gridMap);

		refreshSoDoBan();

		JScrollPane scroll = new JScrollPane(wrapGrid);
		scroll.setBorder(null);
		scroll.setOpaque(false);
		scroll.getViewport().setOpaque(false);
		scroll.getVerticalScrollBar().setUnitIncrement(16);

		mapWrap.add(header, BorderLayout.NORTH);
		mapWrap.add(scroll, BorderLayout.CENTER);
		return mapWrap;
	}

	private JPanel createTableCard(String maBan, String tenBan, int capacity, String status) {
		Color bg, border;

		// Áp dụng màu sắc dựa theo trạng thái bàn
		if (status.equalsIgnoreCase("Trống")) {
			bg = BG_TRONG;
			border = BORDER_TRONG;
		} else if (status.equalsIgnoreCase("Có khách")) {
			bg = BG_KHACH;
			border = BORDER_KHACH;
		} else if (status.equalsIgnoreCase("Đang ghép")) {
			bg = BG_GHEP;
			border = BORDER_GHEP;
		} else { // Đã đặt
			bg = BG_DAT;
			border = BORDER_DAT;
		}

		JPanel card = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(bg);
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
				g2.setColor(border);
				g2.setStroke(new BasicStroke(1.5f));
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
				g2.dispose();
			}
		};
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setPreferredSize(new Dimension(220, 130));
		card.setOpaque(false);
		card.setCursor(new Cursor(Cursor.HAND_CURSOR));

		JLabel icon = new JLabel("🪑", SwingConstants.CENTER);
		icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
		icon.setForeground(border);
		icon.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel lblName = new JLabel(tenBan, SwingConstants.CENTER);
		lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));
		lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel lblCap = new JLabel("Sức chứa: " + capacity + " người", SwingConstants.CENTER);
		lblCap.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblCap.setForeground(new Color(80, 80, 80));
		lblCap.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel lblStatus = new JLabel(status, SwingConstants.CENTER);
		lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

		card.add(Box.createVerticalStrut(15));
		card.add(icon);
		card.add(Box.createVerticalStrut(5));
		card.add(lblName);
		card.add(Box.createVerticalStrut(5));
		card.add(lblCap);
		card.add(Box.createVerticalStrut(5));
		card.add(lblStatus);

		card.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				card.setBorder(BorderFactory.createLineBorder(border, 2));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				card.setBorder(null);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (status.equalsIgnoreCase("Trống")) {
					// Bổ sung truyền capacity và tenBan vào hàm xuLyMoBan
					xuLyMoBan(maBan, tenBan, capacity);
				} else if (status.equalsIgnoreCase("Có khách")) {
					hienThiThongTinBan(maBan, tenBan);
				} else if (status.equalsIgnoreCase("Đã đặt")) {
					xuLyClickBanDaDat(maBan, tenBan, capacity);
				} else if (status.equalsIgnoreCase("Đang ghép")) {
					// Xử lý khi click vào Bàn Đang Ghép (Cam)
					String msg = "Bàn này đang được ghép chung hóa đơn với bàn khác.\n"
							+ "Khách đã thanh toán xong và bạn muốn dọn bàn này về trạng thái TRỐNG?";
					int choice = JOptionPane.showConfirmDialog(FrmLeTan.this, msg, "Giải phóng bàn ghép",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

					if (choice == JOptionPane.YES_OPTION) {
						banAnDAO.capNhatTrangThai(maBan, "Trống");
						refreshSoDoBan();
						JOptionPane.showMessageDialog(FrmLeTan.this, "Đã dọn dẹp " + tenBan + " về trạng thái Trống!");
					}
				}
			}
		});
		return card;
	}
	// HÀM XỬ LÝ THÔNG MINH: TÍNH TOÁN THỜI GIAN AN TOÀN CHO BÀN "ĐÃ ĐẶT"
	private void xuLyClickBanDaDat(String maBan, String tenBan, int capacity) {
		List<PhieuDatBan> dsPhieu = phieuDAO.getDanhSachDatChoChuaCheckIn();
		PhieuDatBan phieuCuaBanNay = null;
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		String today = sdfDate.format(new Date());

		// Tìm phiếu đặt của bàn này TRONG HÔM NAY
		for (PhieuDatBan p : dsPhieu) {
			if (p.getMaBan() != null && p.getMaBan().equals(maBan) && p.getThoiGianDen() != null) {
				if (sdfDate.format(p.getThoiGianDen()).equals(today)) {
					phieuCuaBanNay = p;
					break;
				}
			}
		}

		if (phieuCuaBanNay != null) {
			long gioDat = phieuCuaBanNay.getThoiGianDen().getTime();
			long hienTai = System.currentTimeMillis();

			long thoiGianConLai_Phut = (gioDat - hienTai) / (60 * 1000);

			if (thoiGianConLai_Phut >= 150) {
				SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
				String thoiGianDatStr = sdfTime.format(phieuCuaBanNay.getThoiGianDen());

				long tieng = thoiGianConLai_Phut / 60;
				long phut = thoiGianConLai_Phut % 60;

				String msg = "Bàn " + tenBan + " có khách đặt trước lúc " + thoiGianDatStr + ".\n"
						+ "Hiện tại còn trống " + tieng + " tiếng " + phut + " phút nữa khách mới đến.\n"
						+ "ĐỦ THỜI GIAN AN TOÀN (>= 2.5 tiếng) để đón khách vãng lai.\n\n"
						+ "Bạn có muốn mở bàn này cho khách vãng lai ngồi tạm không?";

				int choice = JOptionPane.showConfirmDialog(this, msg, "Mở bàn an toàn", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (choice == JOptionPane.YES_OPTION) {
					xuLyMoBan(maBan, tenBan, capacity);
				}
			} else {
				SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
				String thoiGianDatStr = sdfTime.format(phieuCuaBanNay.getThoiGianDen());
				JOptionPane.showMessageDialog(this,
						"Bàn " + tenBan + " có khách đặt lúc " + thoiGianDatStr + ".\n"
								+ "Chỉ còn " + thoiGianConLai_Phut + " phút nữa khách sẽ đến.\n"
								+ "KHÔNG ĐỦ thời gian an toàn để nhận thêm khách vãng lai!",
						"Từ chối mở bàn", JOptionPane.WARNING_MESSAGE);
			}
		} else {
			// Trường hợp khách đặt cho ngày mai, thì hôm nay bàn đó Lễ tân vẫn xài vô tư
			String msg = "Bàn " + tenBan + " được đặt cho ngày khác, không phải hôm nay.\nHôm nay vẫn có thể sử dụng bình thường.\nBạn có muốn mở bàn không?";
			int choice = JOptionPane.showConfirmDialog(this, msg, "Mở bàn", JOptionPane.YES_NO_OPTION);
			if(choice == JOptionPane.YES_OPTION) {
				xuLyMoBan(maBan, tenBan, capacity);
			}
		}
	}
	// Cập nhật hàm xuLyMoBan nhận thêm tham số tenBan và capacity
	private void xuLyMoBan(String maBan, String tenBan, int capacity) {
		DlgNhapThongTinKhach dlg = new DlgNhapThongTinKhach(FrmLeTan.this);
		dlg.setVisible(true);

		if (dlg.isConfirmed()) {
			String ten = dlg.getTen();
			String sdt = dlg.getSDT();
			int soNguoi = dlg.getSoNguoi();


			// LOGIC KIỂM TRA SỨC CHỨA BÀN KHI TẠO MỚI (MỞ BÀN TRỰC TIẾP)

			if (soNguoi > capacity) {
				String msgCanhBao = "Bàn " + tenBan + " chỉ có sức chứa " + capacity + " người.\n" + "Số lượng "
						+ soNguoi + " khách đã vượt quá mức quy định.\n\n"
						+ "Bạn có muốn tiếp tục xếp khách vào bàn này (kê thêm ghế) không?";

				int choice = JOptionPane.showConfirmDialog(this, msgCanhBao, "Cảnh báo vượt sức chứa",
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

				if (choice != JOptionPane.YES_OPTION) {
					return; // Lễ tân chọn NO -> Hủy mở bàn
				}
			}


			String maHD = "HD" + System.currentTimeMillis();
			String maNV = "NV005";

			if (Entity.LuuLog.nhanVienDangNhap != null) {
				maNV = Entity.LuuLog.nhanVienDangNhap.getMaNV();
			}

			boolean result = hoaDonDAO.taoHoaDonMoi(maHD, maNV, maBan, ten, sdt, soNguoi, null);

			if (result) {
				banAnDAO.capNhatTrangThai(maBan, "Có khách");
				refreshSoDoBan();
				JOptionPane.showMessageDialog(this, "Mở bàn " + maBan + " thành công cho khách " + ten);
			} else {
				JOptionPane.showMessageDialog(this, "Lỗi khi tạo hóa đơn mới!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	// RIGHT SIDEBAR
	// RIGHT SIDEBAR
	private JPanel createRightSidebar() {
		JPanel sidebar = new JPanel(new BorderLayout());
		sidebar.setBackground(Color.WHITE);
		sidebar.setPreferredSize(new Dimension(340, 0)); // Tăng chút xíu để chứa Combobox cho thoải mái
		sidebar.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER_CLR));

		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
		top.setBackground(Color.WHITE);
		top.setBorder(new EmptyBorder(20, 20, 10, 20));

		JLabel title = new JLabel("Quản lý Đặt chỗ");
		title.setFont(new Font("Segoe UI", Font.BOLD, 16));
		title.setAlignmentX(Component.LEFT_ALIGNMENT);

		// --- Ô TÌM KIẾM ---
		txtTimKiemDatCho = new JTextField();
		txtTimKiemDatCho.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
		txtTimKiemDatCho.setPreferredSize(new Dimension(0, 35));
		// Nếu bạn không có lớp PlaceholderTextField thì dùng setToolTipText hoặc Label
		txtTimKiemDatCho.setToolTipText("Gõ SĐT hoặc Tên khách...");
		txtTimKiemDatCho.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true),
				new EmptyBorder(0, 10, 0, 10)));

		// Bắt sự kiện gõ tới đâu tìm tới đó
		txtTimKiemDatCho.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				loadDanhSachDatCho();
			}
		});

		// --- COMBOBOX LỌC NGÀY ---
		JPanel pnlFilter = new JPanel(new BorderLayout(10, 0));
		pnlFilter.setOpaque(false);
		pnlFilter.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

		JLabel subTitle = new JLabel("Hiển thị: ");
		subTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));

		cboLocThoiGian = new JComboBox<>(new String[]{"Hôm nay", "Ngày mai", "Tất cả"});
		cboLocThoiGian.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		cboLocThoiGian.setBackground(Color.WHITE);
		cboLocThoiGian.setCursor(new Cursor(Cursor.HAND_CURSOR));
		cboLocThoiGian.addActionListener(e -> loadDanhSachDatCho());

		pnlFilter.add(subTitle, BorderLayout.WEST);
		pnlFilter.add(cboLocThoiGian, BorderLayout.CENTER);

		top.add(title);
		top.add(Box.createVerticalStrut(15));
		top.add(txtTimKiemDatCho);
		top.add(Box.createVerticalStrut(15));
		top.add(pnlFilter);

		pnlDanhSachDatCho = new JPanel();
		pnlDanhSachDatCho.setLayout(new BoxLayout(pnlDanhSachDatCho, BoxLayout.Y_AXIS));
		pnlDanhSachDatCho.setBackground(Color.WHITE);
		pnlDanhSachDatCho.setBorder(new EmptyBorder(10, 20, 0, 20));

		JScrollPane scroll = new JScrollPane(pnlDanhSachDatCho);
		scroll.setBorder(null);

		JPanel actions = new JPanel(new GridLayout(4, 1, 0, 10));
		actions.setBackground(Color.WHITE);
		actions.setBorder(new EmptyBorder(20, 20, 20, 20));
		JLabel lblQuick = new JLabel("Thao tác nhanh");
		lblQuick.setFont(new Font("Segoe UI", Font.BOLD, 14));
		actions.add(lblQuick);

		JButton btnTaoDatCho = createSolidButton("Tạo đặt chỗ mới", RED_MAIN, Color.WHITE);
		btnTaoDatCho.addActionListener(e -> {
			new FrmTaoDatCho(this).setVisible(true);
		});
		actions.add(btnTaoDatCho);

		JButton btnChuyenBan = createOutlineButton("Chuyển bàn");
		btnChuyenBan.addActionListener(e -> {
			new FrmChuyenBan(this).setVisible(true);
		});
		actions.add(btnChuyenBan);

		JButton btnGopBan = createOutlineButton("Gộp bàn");
		btnGopBan.addActionListener(e -> {
			new FrmGopBan(this).setVisible(true);
		});
		actions.add(btnGopBan);

		sidebar.add(top, BorderLayout.NORTH);
		sidebar.add(scroll, BorderLayout.CENTER);
		sidebar.add(actions, BorderLayout.SOUTH);
		return sidebar;
	}

	// Hàm load dữ liệu Đã được nâng cấp để ăn theo Từ khóa và Thời gian
	public void loadDanhSachDatCho() {
		if (pnlDanhSachDatCho == null)
			return;
		pnlDanhSachDatCho.removeAll();

		// Lấy dữ liệu lọc
		String tuKhoa = (txtTimKiemDatCho != null) ? txtTimKiemDatCho.getText().trim() : "";
		String thoiGianLoc = (cboLocThoiGian != null && cboLocThoiGian.getSelectedItem() != null)
				? cboLocThoiGian.getSelectedItem().toString()
				: "Hôm nay";

		// Tạm thời gọi hàm getDanhSachDatChoChuaCheckIn() cũ của bạn,
		// Sau đó mình sẽ dùng Java Code để tự lọc luôn cho nhanh gọn khỏi đụng DAO!
		List<PhieuDatBan> danhSachPhieuToanBo = phieuDAO.getDanhSachDatChoChuaCheckIn();
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

		String todayDate = sdfDate.format(new Date());
		String tomorrowDate = sdfDate.format(new Date(System.currentTimeMillis() + 86400000L)); // +1 ngày

		int count = 0;

		for (PhieuDatBan phieu : danhSachPhieuToanBo) {
			// 1. LỌC TỪ KHÓA
			boolean matchesSearch = true;
			if (!tuKhoa.isEmpty()) {
				matchesSearch = (phieu.getTenKhachHang() != null && phieu.getTenKhachHang().toLowerCase().contains(tuKhoa.toLowerCase())) ||
						(phieu.getSoDienThoai() != null && phieu.getSoDienThoai().contains(tuKhoa));
			}

			// 2. LỌC THỜI GIAN
			boolean matchesTime = true;
			if (phieu.getThoiGianDen() != null) {
				String phieuDate = sdfDate.format(phieu.getThoiGianDen());
				if ("Hôm nay".equals(thoiGianLoc) && !phieuDate.equals(todayDate)) {
					matchesTime = false;
				} else if ("Ngày mai".equals(thoiGianLoc) && !phieuDate.equals(tomorrowDate)) {
					matchesTime = false;
				}
			}

			// Nếu thỏa cả 2 điều kiện thì vẽ ra Card
			if (matchesSearch && matchesTime) {
				String timeStr = (phieu.getThoiGianDen() != null) ? sdfTime.format(phieu.getThoiGianDen()) : "--:--";
				pnlDanhSachDatCho.add(createBookingCard(
						phieu.getMaPhieu(),
						phieu.getTenKhachHang(),
						phieu.getSoDienThoai(),
						timeStr,
						phieu.getMaBan(),
						phieu.getTenBan(),
						phieu.getSoLuongKhach()
				));
				pnlDanhSachDatCho.add(Box.createVerticalStrut(15));
				count++;
			}
		}

		if (count == 0) {
			JLabel emptyLabel = new JLabel("Không tìm thấy kết quả phù hợp.");
			emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
			emptyLabel.setForeground(TEXT_GRAY);
			emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			pnlDanhSachDatCho.add(emptyLabel);
		}

		pnlDanhSachDatCho.revalidate();
		pnlDanhSachDatCho.repaint();
	}

	private JPanel createBookingCard(String maPhieu, String name, String phone, String time, String maBan,
			String tenBan, int guests) {
		JPanel card = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new Color(254, 252, 232));
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
				g2.setColor(new Color(253, 224, 71));
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
				g2.dispose();
			}
		};
		card.setLayout(new BorderLayout(10, 10));
		card.setOpaque(false);
		card.setBorder(new EmptyBorder(10, 12, 10, 12));
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

		JPanel info = new JPanel(new GridLayout(3, 2));
		info.setOpaque(false);
		JLabel lName = new JLabel(name);
		lName.setFont(new Font("Segoe UI", Font.BOLD, 14));
		JLabel lTime = new JLabel(time, SwingConstants.RIGHT);
		lTime.setFont(new Font("Segoe UI", Font.BOLD, 12));
		JLabel lPhone = new JLabel(phone);
		lPhone.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lPhone.setForeground(TEXT_GRAY);
		JLabel lTable = new JLabel("Bàn: " + tenBan);
		lTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lTable.setForeground(TEXT_GRAY);
		JLabel lGuests = new JLabel(guests + " người", SwingConstants.RIGHT);
		lGuests.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		info.add(lName);
		info.add(lTime);
		info.add(lPhone);
		info.add(new JLabel(""));
		info.add(lTable);
		info.add(lGuests);

		JButton btnCheckIn = createSolidButton("Check-in Khách", RED_MAIN, Color.WHITE);
		btnCheckIn.setPreferredSize(new Dimension(0, 30));

		btnCheckIn.addActionListener(e -> xuLyCheckIn(maPhieu, name, maBan, tenBan, card));

		card.add(info, BorderLayout.CENTER);
		card.add(btnCheckIn, BorderLayout.SOUTH);
		return card;
	}

	private void xuLyCheckIn(String maPhieu, String tenKhach, String maBan, String tenBan, JPanel cardRef) {

		String sdtKhach = "0000000000";
		int soLuongKhach = 1;

		List<PhieuDatBan> dsPhieu = phieuDAO.getDanhSachDatChoChuaCheckIn();
		for (PhieuDatBan p : dsPhieu) {
			if (p.getMaPhieu().equals(maPhieu)) {
				sdtKhach = p.getSoDienThoai();
				soLuongKhach = p.getSoLuongKhach();
				break;
			}
		}

		// LOGIC KIỂM TRA SỨC CHỨA KHI CHECK-IN KHÁCH ĐẶT TRƯỚC
		int capacity = 0;
		List<BanAn> allBan = banAnDAO.getAllBanAn();
		for (BanAn ban : allBan) {
			if (ban.getMaBan().equals(maBan)) {
				capacity = ban.getSucChua();
				break;
			}
		}

		if (soLuongKhach > capacity) {
			String msgCanhBao = "Bàn " + tenBan + " chỉ có sức chứa " + capacity + " người.\n" + "Khách đặt "
					+ soLuongKhach + " người, đã vượt quá sức chứa.\n\n"
					+ "Bạn có muốn tiếp tục check-in (kê thêm ghế) không?";

			int choice = JOptionPane.showConfirmDialog(cardRef, msgCanhBao, "Cảnh báo vượt sức chứa",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

			if (choice != JOptionPane.YES_OPTION) {
				return; // Dừng lại nếu bấm NO
			}
		} else {
			// Nếu sức chứa bình thường thì hỏi xác nhận check-in như cũ
			int check = JOptionPane.showConfirmDialog(cardRef,
					"Check-in cho khách " + tenKhach + " vào " + tenBan + "?", "Xác nhận Check-in",
					JOptionPane.YES_NO_OPTION);

			if (check != JOptionPane.YES_OPTION)
				return;
		}


		String maHD = "HD" + System.currentTimeMillis();
		String maNV = (LuuLog.nhanVienDangNhap != null) ? LuuLog.nhanVienDangNhap.getMaNV() : "NV001";

		boolean hdOk = hoaDonDAO.taoHoaDonMoi(maHD, maNV, maBan, tenKhach, sdtKhach, soLuongKhach, maPhieu);
		boolean copyMonOk = false;
		if (hdOk) {
			copyMonOk = hoaDonDAO.copyMonAnTuPhieuSangHoaDon(maBan, maHD);
		}

		boolean banOk = banAnDAO.capNhatTrangThai(maBan, "Có khách");
		boolean phieuOk = phieuDAO.capNhatTrangThaiPhieu(maPhieu, "Đã đến");

		if (hdOk && copyMonOk && banOk && phieuOk) {
			JOptionPane.showMessageDialog(cardRef, "Check-in thành công cho " + tenKhach + "!\nMã HĐ: " + maHD,
					"Thành công", JOptionPane.INFORMATION_MESSAGE);
			refreshSoDoBan();
			loadDanhSachDatCho();
		} else {
			JOptionPane.showMessageDialog(cardRef,
					"Lỗi khi lưu dữ liệu Check-in!\n" + "hdOk = " + hdOk + "\n" + "copyMonOk = " + copyMonOk + "\n"
							+ "banOk = " + banOk + "\n" + "phieuOk = " + phieuOk,
					"Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
		}
	}

	private JButton createSolidButton(String text, Color bg, Color fg) {
		JButton btn = new JButton(text);
		btn.setBackground(bg);
		btn.setForeground(fg);
		btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btn.setFocusPainted(false);
		btn.setBorderPainted(false);
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return btn;
	}

	private JButton createOutlineButton(String text) {
		JButton btn = new JButton(text);
		btn.setBackground(Color.WHITE);
		btn.setForeground(TEXT_DARK);
		btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		btn.setFocusPainted(false);
		btn.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return btn;
	}

	private void hienThiThongTinBan(String maBan, String tenBan) {
		String[] infoKhach = hoaDonDAO.getThongTinKhachVuaMo(maBan);

		if (infoKhach != null) {
			String ten = infoKhach[0];
			String sdt = infoKhach[1];
			String sl = infoKhach[2];
			String gio = infoKhach[3];

			String msg = "🏮 NHÀ HÀNG NGÓI ĐỎ 🏮\n" + "Bàn: " + tenBan + "\n" + "Khách hàng: "
					+ (ten != null ? ten : "Khách lẻ") + "\n" + "Số điện thoại: " + (sdt != null ? sdt : "Trống") + "\n"
					+ "Số lượng: " + sl + " người\n" + "Giờ vào bàn: " + gio + "\n";

			JOptionPane.showMessageDialog(this, msg, "Thông tin khách đang ngồi", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, "Bàn đang trống hoặc chưa có dữ liệu khách!");
		}
	}

	// LÀM MỚI VÀ LỌC SƠ ĐỒ BÀN
	public void refreshSoDoBan() {
		if (gridMap == null)
			return;
		gridMap.removeAll();
		if (lblMapTitle != null)
			lblMapTitle.setText("Sơ đồ bàn - " + currentTab);

		// 1. Đọc giá trị từ Bộ lọc sức chứa
		int requiredCap = 0;
		if (cboLocSucChua != null && cboLocSucChua.getSelectedIndex() > 0) {
			String sel = (String) cboLocSucChua.getSelectedItem();
			// Dùng Regex để lấy số từ chuỗi ">= 4 người"
			requiredCap = Integer.parseInt(sel.replaceAll("[^0-9]", ""));
		}

		// 2. Lấy danh sách bàn từ DAO
		List<Entity.BanAn> danhSachBan = banAnDAO.getAllBanAn();

		for (Entity.BanAn ban : danhSachBan) {
			String viTri = ban.getViTri();
			if (viTri != null && viTri.trim().equalsIgnoreCase(currentTab)) {

				boolean showTable = true;

				// 3. Logic Lọc
				if (requiredCap > 0) {

					if (!ban.getTrangThai().trim().equalsIgnoreCase("Trống") || ban.getSucChua() < requiredCap) {
						showTable = false;
					}
				}

				// 4. Nếu qua được bộ lọc thì thêm vào giao diện
				if (showTable) {
					gridMap.add(createTableCard(ban.getMaBan(), ban.getTenBan(), ban.getSucChua(),
							ban.getTrangThai().trim()));
				}
			}
		}

		gridMap.revalidate();
		gridMap.repaint();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new FrmLeTan().setVisible(true));
	}
}
package GUI;

import DAO.NhanVienDAO;
import Entity.LuuLog;
import Entity.NhanVien;
import connectDatabase.ConnectDB;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.RenderingHints;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FrmCaiDat extends JPanel {

	private static final Color BG_MAIN   = new Color(243, 244, 246);
	private static final Color BG_CARD   = Color.WHITE;
	private static final Color BORDER_CLR = new Color(229, 231, 235);
	private static final Color TEXT_TITLE = new Color(17, 24, 39);
	private static final Color TEXT_DARK  = new Color(31, 41, 55);
	private static final Color TEXT_GRAY  = new Color(107, 114, 128);
	private static final Color BTN_BG    = new Color(15, 23, 42);
	private static final Color GREEN_CLR = new Color(22, 163, 74);
	private static final Color RED_MAIN  = new Color(220, 38, 38);

	private static final Font FONT_H2    = new Font("Segoe UI", Font.BOLD, 16);
	private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 13);
	private static final Font FONT_VALUE = new Font("Segoe UI", Font.PLAIN, 15);

	private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
	private NhanVien nhanVienHienTai;

	// Thời điểm đăng nhập — lưu static để tính thời gian làm việc
	private static final LocalDateTime thoiDiemDangNhap = LocalDateTime.now();

	// Label thời gian làm việc — cần update mỗi phút
	private JLabel lbThoiGianLamViec;
	private Timer timerThoiGian;

	public FrmCaiDat() {
		taiDuLieuNhanVien();
		initUI();
	}

	private void taiDuLieuNhanVien() {
		try {
			if (LuuLog.nhanVienDangNhap != null && LuuLog.nhanVienDangNhap.getMaNV() != null) {
				nhanVienHienTai = nhanVienDAO.getNhanVienTheoMa(LuuLog.nhanVienDangNhap.getMaNV());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initUI() {
		setLayout(new BorderLayout());
		setBackground(BG_MAIN);

		JPanel root = new JPanel(new BorderLayout(0, 24));
		root.setBackground(BG_MAIN);
		root.setBorder(new EmptyBorder(30, 40, 40, 40));

		JPanel bodyContent = new JPanel();
		bodyContent.setLayout(new BoxLayout(bodyContent, BoxLayout.Y_AXIS));
		bodyContent.setBackground(BG_MAIN);

		// Ai cũng thấy
		bodyContent.add(createAccountCard());

		// Chỉ quản lý mới thấy
		if (laQuanLy()) {
			bodyContent.add(Box.createVerticalStrut(24));
			bodyContent.add(createRestaurantCard());
			bodyContent.add(Box.createVerticalStrut(24));
			bodyContent.add(createThongTinHeThongCard()); // ← thay createSystemCard()
		}

		JPanel bodyWrapper = new JPanel(new BorderLayout());
		bodyWrapper.setBackground(BG_MAIN);
		bodyWrapper.add(bodyContent, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane(bodyWrapper);
		scrollPane.setBorder(null);
		scrollPane.getViewport().setBackground(BG_MAIN);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		root.add(scrollPane, BorderLayout.CENTER);
		add(root, BorderLayout.CENTER);
	}

	private boolean laQuanLy() {
		return nhanVienHienTai != null
				&& nhanVienHienTai.getVaiTro() != null
				&& "Quản lý".equalsIgnoreCase(nhanVienHienTai.getVaiTro().trim());
	}

	// ==================== CARD TÀI KHOẢN ====================
	private JPanel createAccountCard() {
		JPanel card = createCustomCard();
		card.add(createCardHeader("Thông tin tài khoản"));
		card.add(Box.createVerticalStrut(20));

		String maNV = "...";
		String hoTen = "Chưa cập nhật";
		String sdt = "Chưa cập nhật";
		String vaiTro = "Chưa cập nhật";
		String tenDangNhap = "Chưa cập nhật";

		if (nhanVienHienTai != null) {
			if (nhanVienHienTai.getMaNV() != null)        maNV        = nhanVienHienTai.getMaNV();
			if (nhanVienHienTai.getHoTenNV() != null)     hoTen       = nhanVienHienTai.getHoTenNV();
			if (nhanVienHienTai.getSoDienThoai() != null) sdt         = nhanVienHienTai.getSoDienThoai();
			if (nhanVienHienTai.getVaiTro() != null)      vaiTro      = nhanVienHienTai.getVaiTro();
			if (nhanVienHienTai.getTenDangNhap() != null) tenDangNhap = nhanVienHienTai.getTenDangNhap();
		}

		card.add(createTwoColRow(createLabelValue("Mã nhân viên", maNV),
				createLabelValue("Họ và tên", hoTen)));
		card.add(Box.createVerticalStrut(20));
		card.add(createTwoColRow(createLabelValue("Tên đăng nhập", tenDangNhap),
				createLabelValue("Vai trò", vaiTro)));
		card.add(Box.createVerticalStrut(20));
		card.add(createTwoColRow(createLabelValue("Số điện thoại", sdt),
				createEmptyBlock()));
		card.add(Box.createVerticalStrut(24));
		card.add(createDivider());
		card.add(Box.createVerticalStrut(16));

		JButton btnDoiMatKhau = new JButton("Đổi mật khẩu");
		btnDoiMatKhau.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnDoiMatKhau.setBackground(BTN_BG);
		btnDoiMatKhau.setForeground(Color.WHITE);
		btnDoiMatKhau.setFocusPainted(false);
		btnDoiMatKhau.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnDoiMatKhau.setBorder(new EmptyBorder(8, 16, 8, 16));
		btnDoiMatKhau.addActionListener(e -> hienThiDialogDoiMatKhau());

		JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		btnWrap.setOpaque(false);
		btnWrap.add(btnDoiMatKhau);
		card.add(btnWrap);
		return card;
	}

	// ==================== CARD NHÀ HÀNG ====================
	private JPanel createRestaurantCard() {
		JPanel card = createCustomCard();
		card.add(createCardHeader("Thông tin nhà hàng"));
		card.add(Box.createVerticalStrut(20));

		card.add(createTwoColRow(
				createLabelValue("Tên nhà hàng", "Nhà hàng Ngói Đỏ"),
				createLabelValue("Số điện thoại", "0123 456 789")));
		card.add(Box.createVerticalStrut(20));

		JPanel row2 = new JPanel(new BorderLayout());
		row2.setOpaque(false);
		row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
		JPanel pnlAddress = new JPanel();
		pnlAddress.setLayout(new BoxLayout(pnlAddress, BoxLayout.Y_AXIS));
		pnlAddress.setOpaque(false);
		JLabel lblDiaChi = new JLabel("Địa chỉ");
		lblDiaChi.setFont(FONT_LABEL); lblDiaChi.setForeground(TEXT_GRAY);
		lblDiaChi.setAlignmentX(Component.LEFT_ALIGNMENT);
		JPanel pnlValueAndBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		pnlValueAndBtn.setOpaque(false);
		pnlValueAndBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
		String diaChi = "123 Đường Trung Tâm, TP. Hồ Chí Minh";
		JLabel valDiaChi = new JLabel(diaChi);
		valDiaChi.setFont(FONT_VALUE); valDiaChi.setForeground(TEXT_DARK);
		valDiaChi.setBorder(new EmptyBorder(0, 0, 0, 15));
		JButton btnMap = new JButton("Xem bản đồ");
		btnMap.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnMap.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnMap.setFocusPainted(false);
		btnMap.addActionListener(e -> {
			try {
				String url = "https://www.google.com/maps/search/?api=1&query="
						+ java.net.URLEncoder.encode(diaChi, "UTF-8");
				java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
			} catch (Exception ex) { ex.printStackTrace(); }
		});
		pnlValueAndBtn.add(valDiaChi); pnlValueAndBtn.add(btnMap);
		pnlAddress.add(lblDiaChi); pnlAddress.add(Box.createVerticalStrut(6)); pnlAddress.add(pnlValueAndBtn);
		row2.add(pnlAddress, BorderLayout.CENTER);
		card.add(row2);
		card.add(Box.createVerticalStrut(20));

		JPanel row3 = new JPanel(new GridLayout(1, 2, 20, 0));
		row3.setOpaque(false);
		row3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
		row3.add(createLabelValue("Email liên hệ", "ngoido@gmail.com"));
		JPanel pnlWebsite = new JPanel();
		pnlWebsite.setLayout(new BoxLayout(pnlWebsite, BoxLayout.Y_AXIS));
		pnlWebsite.setOpaque(false);
		JLabel lblWebTitle = new JLabel("Website");
		lblWebTitle.setFont(FONT_LABEL); lblWebTitle.setForeground(TEXT_GRAY);
		lblWebTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		JPanel pnlWebBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		pnlWebBtn.setOpaque(false);
		pnlWebBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
		JButton btnGoWeb = new JButton("Tới trang web Nhà hàng");
		btnGoWeb.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnGoWeb.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnGoWeb.addActionListener(e -> {
			try {
				java.io.File htmlFile = new java.io.File("index.html");
				if (htmlFile.exists()) java.awt.Desktop.getDesktop().browse(htmlFile.toURI());
				else JOptionPane.showMessageDialog(this, "Không thấy file index.html!");
			} catch (Exception ex) { ex.printStackTrace(); }
		});
		pnlWebBtn.add(btnGoWeb);
		pnlWebsite.add(lblWebTitle); pnlWebsite.add(Box.createVerticalStrut(6)); pnlWebsite.add(pnlWebBtn);
		row3.add(pnlWebsite);
		card.add(row3);
		return card;
	}

	// ==================== CARD THÔNG TIN HỆ THỐNG ====================
	private JPanel createThongTinHeThongCard() {
		JPanel card = createCustomCard();
		card.add(createCardHeader("Thông tin hệ thống"));
		card.add(Box.createVerticalStrut(20));

		// --- PHẦN 1: VỀ PHẦN MỀM ---
		JLabel lblPhanMem = new JLabel("Về phần mềm");
		lblPhanMem.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblPhanMem.setForeground(TEXT_GRAY);
		lblPhanMem.setAlignmentX(Component.LEFT_ALIGNMENT);
		card.add(lblPhanMem);
		card.add(Box.createVerticalStrut(12));

		card.add(createTwoColRow(
				createLabelValue("Tên phần mềm", "Hệ thống Quản lý Nhà hàng Ngói Đỏ"),
				createLabelValue("Phiên bản", "1.0.0")
		));
		card.add(Box.createVerticalStrut(16));
		card.add(createTwoColRow(
				createLabelValue("Nhà phát triển", "Nhóm PTUD N8 — ĐH Công Nghiệp TPHCM"),
				createLabelValue("Năm phát hành", "2026")
		));

		card.add(Box.createVerticalStrut(24));
		card.add(createDivider());
		card.add(Box.createVerticalStrut(20));

		// --- PHẦN 2: CƠ SỞ DỮ LIỆU ---
		JLabel lblDB = new JLabel("Cơ sở dữ liệu");
		lblDB.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblDB.setForeground(TEXT_GRAY);
		lblDB.setAlignmentX(Component.LEFT_ALIGNMENT);
		card.add(lblDB);
		card.add(Box.createVerticalStrut(12));

		card.add(createTwoColRow(
				createLabelValue("Máy chủ", "localhost:1433"),
				createLabelValue("Tên cơ sở dữ liệu", "QuanLyNhaHang")
		));
		card.add(Box.createVerticalStrut(16));

		// Trạng thái kết nối — kiểm tra thật
		boolean ketNoi = kiemTraKetNoiDB();
		JPanel pnlKetNoi = new JPanel();
		pnlKetNoi.setLayout(new BoxLayout(pnlKetNoi, BoxLayout.Y_AXIS));
		pnlKetNoi.setOpaque(false);

		JLabel lblKetNoiTitle = new JLabel("Trạng thái kết nối");
		lblKetNoiTitle.setFont(FONT_LABEL);
		lblKetNoiTitle.setForeground(TEXT_GRAY);
		lblKetNoiTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel pnlBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		pnlBadge.setOpaque(false);
		pnlBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

		// Badge màu trạng thái
		JLabel badge = new JLabel(ketNoi ? "  ● Đang kết nối  " : "  ● Mất kết nối  ");
		badge.setFont(new Font("Segoe UI", Font.BOLD, 13));
		badge.setOpaque(true);
		badge.setBorder(new EmptyBorder(4, 10, 4, 10));
		if (ketNoi) {
			badge.setBackground(new Color(220, 252, 231));
			badge.setForeground(GREEN_CLR);
		} else {
			badge.setBackground(new Color(254, 226, 226));
			badge.setForeground(RED_MAIN);
		}

		// Nút kiểm tra lại
		JButton btnKiemTra = new JButton("Kiểm tra lại");
		btnKiemTra.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		btnKiemTra.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnKiemTra.setFocusPainted(false);
		btnKiemTra.setBorder(new EmptyBorder(4, 12, 4, 12));
		btnKiemTra.addActionListener(e -> {
			boolean ketNoiMoi = kiemTraKetNoiDB();
			badge.setText(ketNoiMoi ? "  ● Đang kết nối  " : "  ● Mất kết nối  ");
			if (ketNoiMoi) {
				badge.setBackground(new Color(220, 252, 231));
				badge.setForeground(GREEN_CLR);
			} else {
				badge.setBackground(new Color(254, 226, 226));
				badge.setForeground(RED_MAIN);
			}
		});

		pnlBadge.add(badge);
		pnlBadge.add(Box.createHorizontalStrut(12));
		pnlBadge.add(btnKiemTra);

		pnlKetNoi.add(lblKetNoiTitle);
		pnlKetNoi.add(Box.createVerticalStrut(6));
		pnlKetNoi.add(pnlBadge);

		JPanel rowKetNoi = new JPanel(new GridLayout(1, 2, 20, 0));
		rowKetNoi.setOpaque(false);
		rowKetNoi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
		rowKetNoi.add(pnlKetNoi);
		rowKetNoi.add(createEmptyBlock());
		card.add(rowKetNoi);

		card.add(Box.createVerticalStrut(24));
		card.add(createDivider());
		card.add(Box.createVerticalStrut(20));

		// PHẦN 3: PHIÊN LÀM VIỆC
		JLabel lblPhien = new JLabel("Phiên làm việc hiện tại");
		lblPhien.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblPhien.setForeground(TEXT_GRAY);
		lblPhien.setAlignmentX(Component.LEFT_ALIGNMENT);
		card.add(lblPhien);
		card.add(Box.createVerticalStrut(12));

		// Thời điểm đăng nhập
		String thoiDiemStr = thoiDiemDangNhap
				.format(DateTimeFormatter.ofPattern("HH:mm  dd/MM/yyyy"));

		// Thời gian làm việc (tính thật)
		lbThoiGianLamViec = new JLabel(tinhThoiGianLamViec());
		lbThoiGianLamViec.setFont(FONT_VALUE);
		lbThoiGianLamViec.setForeground(TEXT_DARK);
		lbThoiGianLamViec.setAlignmentX(Component.LEFT_ALIGNMENT);

		// Timer cập nhật mỗi 60 giây
		timerThoiGian = new Timer(60_000, e ->
				lbThoiGianLamViec.setText(tinhThoiGianLamViec()));
		timerThoiGian.start();

		JPanel pnlThoiGianLV = new JPanel();
		pnlThoiGianLV.setLayout(new BoxLayout(pnlThoiGianLV, BoxLayout.Y_AXIS));
		pnlThoiGianLV.setOpaque(false);
		JLabel lblTGLVTitle = new JLabel("Thời gian làm việc");
		lblTGLVTitle.setFont(FONT_LABEL);
		lblTGLVTitle.setForeground(TEXT_GRAY);
		lblTGLVTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		pnlThoiGianLV.add(lblTGLVTitle);
		pnlThoiGianLV.add(Box.createVerticalStrut(6));
		pnlThoiGianLV.add(lbThoiGianLamViec);

		card.add(createTwoColRow(
				createLabelValue("Đăng nhập lúc", thoiDiemStr),
				pnlThoiGianLV
		));

		return card;
	}

	//HELPER: KIỂM TRA KẾT NỐI DB
	private boolean kiemTraKetNoiDB() {
		try {
			ConnectDB.getInstance().connect();
			java.sql.Connection con = ConnectDB.getInstance().getConnection();
			return con != null && !con.isClosed();
		} catch (Exception e) {
			return false;
		}
	}

	//HELPER: TÍNH THỜI GIAN LÀM VIỆC
	private String tinhThoiGianLamViec() {
		Duration duration = Duration.between(thoiDiemDangNhap, LocalDateTime.now());
		long gio  = duration.toHours();
		long phut = duration.toMinutesPart();
		if (gio == 0) return phut + " phút";
		return gio + " giờ " + phut + " phút";
	}

	// HELPER COMPONENTS=
	private JPanel createCustomCard() {
		JPanel card = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(BG_CARD);
				g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
				g2.setColor(BORDER_CLR);
				g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setOpaque(false);
		card.setBorder(new EmptyBorder(24, 24, 24, 24));
		return card;
	}

	private JPanel createCardHeader(String title) {
		JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		pnl.setOpaque(false);
		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(FONT_H2);
		lblTitle.setForeground(TEXT_TITLE);
		pnl.add(lblTitle);
		pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		return pnl;
	}

	private JPanel createLabelValue(String label, String value) {
		JPanel pnl = new JPanel();
		pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
		pnl.setOpaque(false);
		JLabel lbl = new JLabel(label);
		lbl.setFont(FONT_LABEL); lbl.setForeground(TEXT_GRAY);
		lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
		JLabel val = new JLabel(value);
		val.setFont(FONT_VALUE); val.setForeground(TEXT_DARK);
		val.setAlignmentX(Component.LEFT_ALIGNMENT);
		pnl.add(lbl); pnl.add(Box.createVerticalStrut(6)); pnl.add(val);
		return pnl;
	}

	private JPanel createTwoColRow(JPanel col1, JPanel col2) {
		JPanel row = new JPanel(new GridLayout(1, 2, 20, 0));
		row.setOpaque(false);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
		row.add(col1); row.add(col2);
		return row;
	}

	private JPanel createEmptyBlock() {
		JPanel pnl = new JPanel(); pnl.setOpaque(false); return pnl;
	}

	private JPanel createDivider() {
		JPanel line = new JPanel();
		line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
		line.setPreferredSize(new Dimension(0, 1));
		line.setBackground(BORDER_CLR);
		return line;
	}

	//ĐỔI MẬT KHẨU
	private void hienThiDialogDoiMatKhau() {
		if (nhanVienHienTai == null) {
			JOptionPane.showMessageDialog(this, "Không lấy được thông tin tài khoản hiện tại!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		JPanel panel = new JPanel(new GridLayout(3, 2, 10, 15));
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		JPasswordField txtOld     = new JPasswordField();
		JPasswordField txtNew     = new JPasswordField();
		JPasswordField txtConfirm = new JPasswordField();
		panel.add(new JLabel("Mật khẩu cũ:"));     panel.add(txtOld);
		panel.add(new JLabel("Mật khẩu mới:"));    panel.add(txtNew);
		panel.add(new JLabel("Xác nhận mật khẩu mới:")); panel.add(txtConfirm);

		int result = JOptionPane.showConfirmDialog(this, panel, "Đổi mật khẩu",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result != JOptionPane.OK_OPTION) return;

		String oldPass     = new String(txtOld.getPassword()).trim();
		String newPass     = new String(txtNew.getPassword()).trim();
		String confirmPass = new String(txtConfirm.getPassword()).trim();

		if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!newPass.equals(confirmPass)) {
			JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (newPass.length() < 4) {
			JOptionPane.showMessageDialog(this, "Mật khẩu mới phải có ít nhất 4 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (nhanVienDAO.doiMatKhau(nhanVienHienTai.getMaNV(), oldPass, newPass)) {
			JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
		} else {
			JOptionPane.showMessageDialog(this, "Mật khẩu cũ không đúng hoặc cập nhật thất bại!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}


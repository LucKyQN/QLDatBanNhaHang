package GUI;

import DAO.HoaDonDAO;
import Model.BanAnModel;
import Model.MonAnModel;
import Entity.NhanVien;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.BaseFont;
@SuppressWarnings("serial")
public class FrmThuNgan extends JFrame {

	private final String tenNhanVien;
	private final HoaDonDAO dao = new HoaDonDAO();

	private static final Color RED_MAIN = new Color(220, 38, 38);
	private static final Color BG_MAIN = new Color(248, 248, 248);
	private static final Color TEXT_DARK = new Color(20, 20, 20);
	private static final Color TEXT_GRAY = new Color(120, 120, 120);
	private static final Color BORDER_CLR = new Color(225, 225, 225);

	private List<BanAnModel> danhSachBan = new ArrayList<>();
	private BanAnModel banDangChon = null;
	private JPanel pnlDanhSachBan;
	private JPanel pnlHoaDon;
	private JLabel lbTienThua;
	private JTextField txtKhachDua;
	private JLabel lbTienCoc;
	private JLabel lbConPhaiThanhToan;
	private JLabel lbTienHoanKhach;
	private long tongCuoiCung = 0;
	private long tienGiamHienTai = 0;
	private long phiDichVuHienTai = 0;
	private long vatHienTai = 0;
	private long soTienCanThu = 0;
	private long soTienHoanKhach = 0;

	private JComboBox<String> cboKM_Current;
	private List<String[]> dsKM_Current = new ArrayList<>();
	private JLabel lbTamTinh, lbGiamGia, lbPhiDV, lbVAT, lbTongTien;
	private long giaTriTamTinh = 0;

	private List<MonAnModel> dsMonHienTai = new ArrayList<>();

	public FrmThuNgan(String tenNhanVien) {
		this.tenNhanVien = tenNhanVien;
		initUI();
		taiDanhSachBan();
		Timer timer = new Timer(5000, e -> {
			System.out.println("Đang tự động cập nhật danh sách bàn");
			taiDanhSachBan();
		});
		timer.start();
	}

	public FrmThuNgan(NhanVien nhanVien) {
		this(nhanVien.getHoTenNV());
	}

	private void initUI() {
		setTitle("Thanh toán & Hóa đơn - Nhà Hàng Ngói Đỏ");
		setSize(1440, 860);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		JPanel root = new JPanel(new BorderLayout());
		root.setBackground(BG_MAIN);
		root.add(createTopBar(), BorderLayout.NORTH);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createLeftPanel(), createRightPanel());
		split.setDividerLocation(350);
		split.setDividerSize(1);
		split.setBorder(null);

		root.add(split, BorderLayout.CENTER);
		setContentPane(root);
	}

	private void taiDanhSachBan() {
		new SwingWorker<List<BanAnModel>, Void>() {
			@Override
			protected List<BanAnModel> doInBackground() {
				return dao.getDanhSachBanChuaThanhToan();
			}

			@Override
			protected void done() {
				try {
					danhSachBan = get();
					capNhatGiaoDienDanhSachBan();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.execute();
	}

	private void capNhatGiaoDienDanhSachBan() {
		pnlDanhSachBan.removeAll();
		if (danhSachBan == null || danhSachBan.isEmpty()) {
			JLabel lbNull = new JLabel("Không có bàn chờ thanh toán");
			lbNull.setForeground(TEXT_GRAY);
			lbNull.setBorder(new EmptyBorder(20, 20, 0, 0));
			pnlDanhSachBan.add(lbNull);
		} else {
			for (BanAnModel ban : danhSachBan) {
				pnlDanhSachBan.add(createBanCard(ban));
				pnlDanhSachBan.add(Box.createVerticalStrut(12));
			}
		}
		pnlDanhSachBan.revalidate();
		pnlDanhSachBan.repaint();
	}

	private JPanel createTopBar() {
		JPanel bar = new JPanel(new BorderLayout());
		bar.setBackground(Color.WHITE);
		bar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
				new EmptyBorder(15, 25, 15, 25)));

		// --- BÊN TRÁI: LOGO & TIÊU ĐỀ (Đã xóa nút Quay lại) ---
		JPanel west = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
		west.setOpaque(false);

		JLabel lblLogo = new JLabel("🏮");
		lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
		lblLogo.setForeground(RED_MAIN);

		JLabel lbTitle = new JLabel("Nhà Hàng Ngói Đỏ - Thu Ngân");
		lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

		west.add(lblLogo);
		west.add(lbTitle);

		// --- BÊN PHẢI: USER, CÀI ĐẶT & ĐĂNG XUẤT ---
		JLabel lbUser = new JLabel(
				"" + tenNhanVien + " | " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		lbUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lbUser.setForeground(TEXT_DARK);

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

		// NÚT ĐĂNG XUẤT MỚI
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
				dispose(); // Đóng form Thu Ngân
				new FrmDangNhap().setVisible(true); // Mở lại form Đăng Nhập
			}
		});

		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
		rightPanel.setOpaque(false);
		rightPanel.add(lbUser);
		rightPanel.add(btnCaiDat);
		rightPanel.add(btnLogout); // Add nút đăng xuất vào góc phải

		bar.add(west, BorderLayout.WEST);
		bar.add(rightPanel, BorderLayout.EAST);
		return bar;
	}

	private JPanel createLeftPanel() {
		JPanel left = new JPanel(new BorderLayout());
		left.setBackground(Color.WHITE);
		left.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_CLR));

		JLabel lbTitle = new JLabel("DANH SÁCH CHỜ THANH TOÁN");
		lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lbTitle.setForeground(TEXT_GRAY);
		lbTitle.setBorder(new EmptyBorder(25, 20, 15, 20));

		pnlDanhSachBan = new JPanel();
		pnlDanhSachBan.setBackground(Color.WHITE);
		pnlDanhSachBan.setLayout(new BoxLayout(pnlDanhSachBan, BoxLayout.Y_AXIS));
		pnlDanhSachBan.setBorder(new EmptyBorder(0, 20, 20, 20));

		JScrollPane scroll = new JScrollPane(pnlDanhSachBan);
		scroll.setBorder(null);
		scroll.getVerticalScrollBar().setUnitIncrement(10);

		left.add(lbTitle, BorderLayout.NORTH);
		left.add(scroll, BorderLayout.CENTER);
		return left;
	}

	private JPanel createBanCard(BanAnModel ban) {
		boolean isSelected = (banDangChon != null && ban.maBan.equals(banDangChon.maBan));
		JPanel card = new JPanel(new BorderLayout(15, 5));
		card.setBackground(isSelected ? new Color(254, 242, 242) : Color.WHITE);
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(isSelected ? RED_MAIN : BORDER_CLR, isSelected ? 2 : 1, true),
				new EmptyBorder(15, 18, 15, 18)));
		card.setCursor(new Cursor(Cursor.HAND_CURSOR));
		card.setMaximumSize(new Dimension(320, 95));

		JLabel lbTen = new JLabel(ban.tenBan);
		lbTen.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lbTen.setForeground(isSelected ? RED_MAIN : TEXT_DARK);

		JLabel lbTong = new JLabel(formatTien(ban.tamTinh) + " đ");
		lbTong.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lbTong.setForeground(isSelected ? RED_MAIN : new Color(100, 100, 100));

		card.add(lbTen, BorderLayout.NORTH);
		card.add(lbTong, BorderLayout.CENTER);

		card.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				banDangChon = ban;
				capNhatGiaoDienDanhSachBan();
				hienThiHoaDon(ban);
			}
		});
		return card;
	}

	private JPanel createRightPanel() {
		pnlHoaDon = new JPanel(new BorderLayout());
		pnlHoaDon.setBackground(BG_MAIN);
		hienThiChoChon();
		return pnlHoaDon;
	}

	private void hienThiChoChon() {
		pnlHoaDon.removeAll();
		JLabel lbHint = new JLabel(
				"<html><center><font size='5' color='#777777'>🏮</font><br>Vui lòng chọn bàn cần thanh toán bên trái</center></html>");
		lbHint.setHorizontalAlignment(SwingConstants.CENTER);
		pnlHoaDon.add(lbHint, BorderLayout.CENTER);
		pnlHoaDon.revalidate();
		pnlHoaDon.repaint();
	}

	private void hienThiHoaDon(BanAnModel ban) {
		pnlHoaDon.removeAll();

		List<MonAnModel> dsMon = dao.getChiTietHoaDon(ban.maHD);
		dsMonHienTai = dsMon;
		dsKM_Current = dao.getKhuyenMaiHieuLuc();

		String[] tenKMs = dsKM_Current.stream().map(k -> k[1]).toArray(String[]::new);
		cboKM_Current = new JComboBox<>(tenKMs);
		cboKM_Current.setPreferredSize(new Dimension(200, 30));
		cboKM_Current.addActionListener(e -> tinhToanLai());
		JPanel container = new JPanel(new BorderLayout(0, 20));
		container.setOpaque(false);
		container.setBorder(new EmptyBorder(30, 50, 30, 50));

		JPanel billPaper = buildBillPaper(ban, dsMon);

		JScrollPane scrollBill = new JScrollPane(billPaper);
		scrollBill.setBorder(null);
		scrollBill.getViewport().setBackground(BG_MAIN);
		scrollBill.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollBill.getVerticalScrollBar().setUnitIncrement(16);

		container.add(scrollBill, BorderLayout.CENTER);
		container.add(buildFooterActions(ban), BorderLayout.SOUTH);

		pnlHoaDon.add(container, BorderLayout.CENTER);

		pnlHoaDon.revalidate();
		pnlHoaDon.repaint();

		tinhToanLai();
	}

	private JPanel buildBillPaper(BanAnModel ban, List<MonAnModel> dsMon) {
		JPanel paper = new JPanel();
		paper.setBackground(Color.WHITE);
		paper.setLayout(new BoxLayout(paper, BoxLayout.Y_AXIS));
		paper.setBorder(new CompoundBorder(new LineBorder(BORDER_CLR, 1), new EmptyBorder(35, 45, 35, 45)));

		JLabel lbHdTitle = new JLabel("HÓA ĐƠN CHI TIẾT");
		lbHdTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
		paper.add(lbHdTitle);
		paper.add(Box.createVerticalStrut(10));

		String ten = (ban.tenKH != null) ? ban.tenKH : "Khách vãng lai";
		String sdt = (ban.sdt != null) ? ban.sdt : "Trống";
		String gioVao = (ban.gioVao != null) ? ban.gioVao : "--:--";

		JPanel pnlInfo = new JPanel(new GridLayout(2, 2, 20, 5));
		pnlInfo.setOpaque(false);
		pnlInfo.add(new JLabel("👤 Khách: " + ten));
		pnlInfo.add(new JLabel("🕒 Vào: " + gioVao));
		pnlInfo.add(new JLabel("📞 SĐT: " + sdt));
		pnlInfo.add(new JLabel("⌛ Ra: " + new SimpleDateFormat("HH:mm").format(new Date())));

		paper.add(pnlInfo);
		paper.add(Box.createVerticalStrut(25));
		paper.add(new JSeparator());
		paper.add(Box.createVerticalStrut(20));

		giaTriTamTinh = 0;
		for (MonAnModel mon : dsMon) {
			paper.add(createItemRow(mon));
			paper.add(Box.createVerticalStrut(12));
			giaTriTamTinh += mon.thanhTien;
		}

		paper.add(Box.createVerticalStrut(20));
		paper.add(new JSeparator());
		paper.add(Box.createVerticalStrut(20));

		lbTamTinh = new JLabel(formatTien(giaTriTamTinh) + " đ");
		lbGiamGia = new JLabel("0 đ");
		lbPhiDV = new JLabel("0 đ");
		lbVAT = new JLabel("0 đ");
		lbTongTien = new JLabel("0 đ");
		lbTienCoc = new JLabel("0 đ");
		lbTienCoc.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lbTienCoc.setForeground(new Color(0, 128, 0));

		lbConPhaiThanhToan = new JLabel("0 đ");
		lbConPhaiThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 24));
		lbConPhaiThanhToan.setForeground(RED_MAIN);

		lbTienHoanKhach = new JLabel("0 đ");
		lbTienHoanKhach.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lbTienHoanKhach.setForeground(new Color(34, 197, 94));
		lbTongTien.setFont(new Font("Segoe UI", Font.BOLD, 24));
		lbTongTien.setForeground(RED_MAIN);

		txtKhachDua = new JTextField("");
		txtKhachDua.setPreferredSize(new Dimension(150, 30));
		txtKhachDua.setFont(new Font("Segoe UI", Font.BOLD, 14));
		txtKhachDua.setHorizontalAlignment(JTextField.RIGHT);

		lbTienThua = new JLabel("0 đ");
		lbTienThua.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lbTienThua.setForeground(new Color(0, 150, 0));

		txtKhachDua.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				tinhTienThua();
			}
		});

		paper.add(createSummaryRow("Tạm tính tổng món:", lbTamTinh));
		paper.add(createSummaryRow("Khuyến mãi:", cboKM_Current, lbGiamGia));
		paper.add(createSummaryRow("Phí dịch vụ (5%):", lbPhiDV));
		paper.add(createSummaryRow("VAT (10%):", lbVAT));
		paper.add(createSummaryRow("Tổng cộng:", lbTongTien));
		paper.add(createSummaryRow("Đã cọc:", lbTienCoc));
		paper.add(createSummaryRow("Còn phải thanh toán:", lbConPhaiThanhToan));
		paper.add(createSummaryRow("Hoàn lại khách:", lbTienHoanKhach));
		paper.add(Box.createVerticalStrut(10));
		paper.add(createSummaryRow("Khách đưa:", txtKhachDua));
		paper.add(createSummaryRow("Tiền thừa trả khách:", lbTienThua));
		return paper;
	}

	private void tinhToanLai() {
		if (dsKM_Current == null || dsKM_Current.isEmpty() || cboKM_Current == null) {
			return;
		}

		int idx = cboKM_Current.getSelectedIndex();
		double giaTriKM = Double.parseDouble(dsKM_Current.get(idx)[2]);
		String loaiKM = dsKM_Current.get(idx)[3];

		long tienGiam;
		if ("Phần trăm".equalsIgnoreCase(loaiKM)) {
			tienGiam = (long) (giaTriTamTinh * giaTriKM / 100.0);
		} else {
			tienGiam = (long) giaTriKM;
		}

		if (tienGiam < 0)
			tienGiam = 0;
		if (tienGiam > giaTriTamTinh)
			tienGiam = giaTriTamTinh;

		long sauGiam = giaTriTamTinh - tienGiam;
		long phiDV = (long) (sauGiam * 0.05);
		long vat = (long) (sauGiam * 0.10);

		tienGiamHienTai = tienGiam;
		phiDichVuHienTai = phiDV;
		vatHienTai = vat;

		tongCuoiCung = Math.max(0, sauGiam + phiDV + vat);

		long tienCoc = (banDangChon != null) ? banDangChon.tienCoc : 0;
		soTienCanThu = Math.max(0, tongCuoiCung - tienCoc);
		soTienHoanKhach = Math.max(0, tienCoc - tongCuoiCung);

		lbGiamGia.setText("-" + formatTien(tienGiamHienTai) + " đ");
		lbPhiDV.setText(formatTien(phiDichVuHienTai) + " đ");
		lbVAT.setText(formatTien(vatHienTai) + " đ");
		lbTongTien.setText(formatTien(tongCuoiCung) + " đ");
		lbTienCoc.setText("-" + formatTien(tienCoc) + " đ");
		lbConPhaiThanhToan.setText(formatTien(soTienCanThu) + " đ");
		lbTienHoanKhach.setText(formatTien(soTienHoanKhach) + " đ");

		tinhTienThua();
	}

	private void tinhTienThua() {
		try {
			if (soTienCanThu == 0) {
				lbTienThua.setText("0 đ");
				lbTienThua.setForeground(new Color(0, 150, 0));
				return;
			}

			String s = txtKhachDua.getText().trim().replace(".", "").replace(",", "");
			if (s.isEmpty()) {
				lbTienThua.setText("0 đ");
				lbTienThua.setForeground(new Color(0, 150, 0));
				return;
			}

			long khachDua = Long.parseLong(s);
			long thua = khachDua - soTienCanThu;

			if (thua < 0) {
				lbTienThua.setText("Chưa đủ tiền");
				lbTienThua.setForeground(Color.RED);
			} else {
				lbTienThua.setText(formatTien(thua) + " đ");
				lbTienThua.setForeground(new Color(0, 150, 0));
			}
		} catch (Exception e) {
			lbTienThua.setText("Số tiền không hợp lệ");
			lbTienThua.setForeground(Color.RED);
		}
	}

	private JPanel buildFooterActions(BanAnModel ban) {
		JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
		pnl.setOpaque(false);

		JButton btnHuy = new JButton("Hủy hóa đơn");
		btnHuy.setPreferredSize(new Dimension(150, 50));
		btnHuy.addActionListener(e -> xacNhanHuyDon(ban));

		JButton btnPay = new JButton("XÁC NHẬN THANH TOÁN");
		btnPay.setBackground(RED_MAIN);
		btnPay.setForeground(Color.WHITE);
		btnPay.setFont(new Font("Segoe UI", Font.BOLD, 15));
		btnPay.setPreferredSize(new Dimension(280, 50));
		btnPay.setFocusPainted(false);
		btnPay.setBorderPainted(false);
		btnPay.addActionListener(e -> xuLyThanhToan(ban));

		pnl.add(btnHuy);
		pnl.add(btnPay);
		return pnl;
	}

	private void xuLyThanhToan(BanAnModel ban) {
		long khachDua = 0;
		long tienThua = 0;

		if (soTienCanThu > 0) {
			String s = txtKhachDua.getText().trim().replace(".", "").replace(",", "");

			if (s.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền khách đưa.");
				txtKhachDua.requestFocus();
				return;
			}

			try {
				khachDua = Long.parseLong(s);
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "Số tiền khách đưa không hợp lệ.");
				txtKhachDua.requestFocus();
				txtKhachDua.selectAll();
				return;
			}

			if (khachDua <= 0) {
				JOptionPane.showMessageDialog(this, "Số tiền khách đưa phải lớn hơn 0.");
				txtKhachDua.requestFocus();
				txtKhachDua.selectAll();
				return;
			}

			if (khachDua < soTienCanThu) {
				JOptionPane.showMessageDialog(this, "Khách đưa chưa đủ tiền để thanh toán.\n" + "Khách đưa: "
						+ formatTien(khachDua) + " đ\n" + "Còn phải thanh toán: " + formatTien(soTienCanThu) + " đ");
				txtKhachDua.requestFocus();
				txtKhachDua.selectAll();
				return;
			}

			tienThua = khachDua - soTienCanThu;
		}

		String msg;
		if (soTienHoanKhach > 0) {
			msg = "Xác nhận thanh toán cho " + ban.tenBan + "?\n\n" + "Tổng cộng: " + formatTien(tongCuoiCung) + " đ\n"
					+ "Đã cọc: " + formatTien(ban.tienCoc) + " đ\n" + "Còn phải thanh toán: 0 đ\n" + "Hoàn lại khách: "
					+ formatTien(soTienHoanKhach) + " đ";
		} else {
			msg = "Xác nhận thanh toán cho " + ban.tenBan + "?\n\n" + "Tổng cộng: " + formatTien(tongCuoiCung) + " đ\n"
					+ "Đã cọc: " + formatTien(ban.tienCoc) + " đ\n" + "Còn phải thanh toán: " + formatTien(soTienCanThu)
					+ " đ\n" + "Khách đưa: " + formatTien(khachDua) + " đ\n" + "Tiền thừa: " + formatTien(tienThua)
					+ " đ";
		}

		int opt = JOptionPane.showConfirmDialog(this, msg, "Thanh toán", JOptionPane.YES_NO_OPTION);
		if (opt != JOptionPane.YES_OPTION) {
			return;
		}

		String maKM = dsKM_Current.get(cboKM_Current.getSelectedIndex())[0];

		if (dao.thanhToan(ban.maHD, soTienCanThu, tienGiamHienTai, maKM)) {
			JOptionPane.showMessageDialog(this, "Đã lưu hóa đơn & giải phóng bàn!");

			int export = JOptionPane.showConfirmDialog(this, "Bạn có muốn xuất hóa đơn PDF ngay bây giờ không?",
					"Xuất hóa đơn PDF", JOptionPane.YES_NO_OPTION);

			if (export == JOptionPane.YES_OPTION) {
				xuatHoaDonPDF(ban, dsMonHienTai, khachDua, tienThua);
			}

			banDangChon = null;
			taiDanhSachBan();
			hienThiChoChon();
		} else {
			JOptionPane.showMessageDialog(this, "Lỗi: Không thể kết nối cơ sở dữ liệu!");
		}
	}

	private void xacNhanHuyDon(BanAnModel ban) {
		int opt = JOptionPane.showConfirmDialog(this,
				"Bạn có chắc muốn HỦY đơn hàng này?\nBàn sẽ quay về trạng thái Trống.", "Cảnh báo",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if (opt == JOptionPane.YES_OPTION && dao.huyHoaDon(ban.maHD)) {
			banDangChon = null;
			taiDanhSachBan();
			hienThiChoChon();
		}
	}

	private JPanel createItemRow(MonAnModel mon) {
		JPanel p = new JPanel(new BorderLayout());
		p.setOpaque(false);

		JLabel lbTen = new JLabel(mon.tenMonAn + " (x" + mon.soLuong + ")");
		lbTen.setFont(new Font("Segoe UI", Font.PLAIN, 15));

		JLabel lbGia = new JLabel(formatTien(mon.thanhTien) + " đ");
		lbGia.setFont(new Font("Segoe UI", Font.BOLD, 15));

		p.add(lbTen, BorderLayout.WEST);
		p.add(lbGia, BorderLayout.EAST);
		return p;
	}

	private JPanel createSummaryRow(String label, JComponent... components) {
		JPanel p = new JPanel(new BorderLayout());
		p.setOpaque(false);
		p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

		JLabel lb = new JLabel(label);
		lb.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lb.setForeground(new Color(80, 80, 80));
		p.add(lb, BorderLayout.WEST);

		JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
		right.setOpaque(false);
		for (JComponent c : components) {
			right.add(c);
		}
		p.add(right, BorderLayout.EAST);
		return p;
	}

	// ==================== CODE XUẤT HÓA ĐƠN MỚI ====================
	private void xuatHoaDonPDF(BanAnModel ban, List<MonAnModel> dsMon, long khachDua, long tienThua) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Chọn nơi lưu hóa đơn PDF");
		chooser.setSelectedFile(new File("HoaDon_" + ban.tenBan.replaceAll("\\s+", "_") + "_" + ban.maHD + ".pdf"));

		int result = chooser.showSaveDialog(this);
		if (result != JFileChooser.APPROVE_OPTION) return;

		File file = chooser.getSelectedFile();
		if (!file.getName().toLowerCase().endsWith(".pdf")) {
			file = new File(file.getAbsolutePath() + ".pdf");
		}

		// Dùng giấy A5 nhìn giống hóa đơn nhà hàng (bill) hơn A4
		Document document = new Document(PageSize.A5, 20, 20, 30, 30);

		try {
			PdfWriter.getInstance(document, new FileOutputStream(file));
			document.open();

			// 1. NẠP FONT ARIAL CỦA WINDOWS ĐỂ VIẾT TIẾNG VIỆT
			BaseFont bf = BaseFont.createFont("C:\\Windows\\Fonts\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			com.lowagie.text.Font fontTitle = new com.lowagie.text.Font(bf, 16, com.lowagie.text.Font.BOLD);
			com.lowagie.text.Font fontBold = new com.lowagie.text.Font(bf, 11, com.lowagie.text.Font.BOLD);
			com.lowagie.text.Font fontNormal = new com.lowagie.text.Font(bf, 10, com.lowagie.text.Font.NORMAL);
			com.lowagie.text.Font fontItalic = new com.lowagie.text.Font(bf, 10, com.lowagie.text.Font.ITALIC);

			// 2. HEADER NHÀ HÀNG
			Paragraph title = new Paragraph("NHÀ HÀNG NGÓI ĐỎ", fontTitle);
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);

			Paragraph sub = new Paragraph("HÓA ĐƠN THANH TOÁN\n---------------------------------------------------", fontNormal);
			sub.setAlignment(Element.ALIGN_CENTER);
			sub.setSpacingAfter(10);
			document.add(sub);

			// 3. THÔNG TIN HÓA ĐƠN
			document.add(new Paragraph("Mã hóa đơn: " + ban.maHD, fontNormal));
			document.add(new Paragraph("Bàn: " + ban.tenBan, fontNormal));
			document.add(new Paragraph("Khách hàng: " + (ban.tenKH != null ? ban.tenKH : "Khách vãng lai"), fontNormal));
			document.add(new Paragraph("Giờ vào: " + (ban.gioVao != null ? ban.gioVao : "--:--"), fontNormal));
			document.add(new Paragraph("Giờ ra: " + new SimpleDateFormat("HH:mm dd/MM/yyyy").format(new Date()), fontNormal));
			document.add(new Paragraph("Thu ngân: " + tenNhanVien, fontNormal));
			document.add(new Paragraph("-----------------------------------------------------------------------------------------", fontNormal));

			// 4. BẢNG MÓN ĂN
			PdfPTable table = new PdfPTable(new float[] { 4f, 1f, 2f });
			table.setWidthPercentage(100);
			table.setSpacingBefore(5);
			table.setSpacingAfter(5);

			table.addCell(createCell("Tên món", fontBold, Element.ALIGN_LEFT, true));
			table.addCell(createCell("SL", fontBold, Element.ALIGN_CENTER, true));
			table.addCell(createCell("Thành tiền", fontBold, Element.ALIGN_RIGHT, true));

			for (MonAnModel mon : dsMon) {
				table.addCell(createCell(mon.tenMonAn, fontNormal, Element.ALIGN_LEFT, false));
				table.addCell(createCell(String.valueOf(mon.soLuong), fontNormal, Element.ALIGN_CENTER, false));
				table.addCell(createCell(formatTien(mon.thanhTien), fontNormal, Element.ALIGN_RIGHT, false));
			}
			document.add(table);
			document.add(new Paragraph("-----------------------------------------------------------------------------------------", fontNormal));

			// 5. PHẦN TỔNG TIỀN (Căn lề phải)
			PdfPTable tSummary = new PdfPTable(new float[] { 3f, 2f });
			tSummary.setWidthPercentage(100);

			String tenKM = cboKM_Current != null && cboKM_Current.getSelectedItem() != null ? cboKM_Current.getSelectedItem().toString() : "Không";

			tSummary.addCell(createCell("Tạm tính:", fontNormal, Element.ALIGN_RIGHT, false));
			tSummary.addCell(createCell(formatTien(giaTriTamTinh), fontNormal, Element.ALIGN_RIGHT, false));

			tSummary.addCell(createCell("Khuyến mãi (" + tenKM + "):", fontNormal, Element.ALIGN_RIGHT, false));
			tSummary.addCell(createCell(lbGiamGia.getText().replace(" đ", ""), fontNormal, Element.ALIGN_RIGHT, false));

			tSummary.addCell(createCell("Phí dịch vụ:", fontNormal, Element.ALIGN_RIGHT, false));
			tSummary.addCell(createCell(lbPhiDV.getText().replace(" đ", ""), fontNormal, Element.ALIGN_RIGHT, false));

			tSummary.addCell(createCell("VAT:", fontNormal, Element.ALIGN_RIGHT, false));
			tSummary.addCell(createCell(lbVAT.getText().replace(" đ", ""), fontNormal, Element.ALIGN_RIGHT, false));

			tSummary.addCell(createCell("TỔNG CỘNG:", fontBold, Element.ALIGN_RIGHT, false));
			tSummary.addCell(createCell(formatTien(tongCuoiCung), fontBold, Element.ALIGN_RIGHT, false));

			tSummary.addCell(createCell("Đã cọc:", fontNormal, Element.ALIGN_RIGHT, false));
			tSummary.addCell(createCell("-" + formatTien(ban.tienCoc), fontNormal, Element.ALIGN_RIGHT, false));

			tSummary.addCell(createCell("CẦN THANH TOÁN:", fontBold, Element.ALIGN_RIGHT, false));
			tSummary.addCell(createCell(formatTien(soTienCanThu), fontBold, Element.ALIGN_RIGHT, false));

			document.add(tSummary);
			document.add(new Paragraph("-----------------------------------------------------------------------------------------", fontNormal));

			// 6. TIỀN KHÁCH ĐƯA
			PdfPTable tCus = new PdfPTable(new float[] { 3f, 2f });
			tCus.setWidthPercentage(100);
			tCus.addCell(createCell("Khách đưa:", fontNormal, Element.ALIGN_RIGHT, false));
			tCus.addCell(createCell(formatTien(khachDua), fontNormal, Element.ALIGN_RIGHT, false));
			tCus.addCell(createCell("Tiền thừa:", fontNormal, Element.ALIGN_RIGHT, false));
			tCus.addCell(createCell(formatTien(tienThua), fontNormal, Element.ALIGN_RIGHT, false));
			document.add(tCus);

			// 7. LỜI CẢM ƠN
			document.add(new Paragraph("\n"));
			Paragraph thanks = new Paragraph("Cảm ơn Quý khách và hẹn gặp lại!", fontItalic);
			thanks.setAlignment(Element.ALIGN_CENTER);
			document.add(thanks);

			JOptionPane.showMessageDialog(this, "Xuất PDF thành công:\n" + file.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Lỗi khi xuất PDF. Đảm bảo máy tính có font Arial.\nChi tiết: " + e.getMessage());
		} finally {
			if (document.isOpen()) document.close();
		}
	}

	// Hàm hỗ trợ vẽ ô bảng PDF tối giản (Bỏ viền)
	private PdfPCell createCell(String text, com.lowagie.text.Font font, int alignment, boolean isHeader) {
		PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font));
		cell.setHorizontalAlignment(alignment);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPadding(6);
		cell.setBorder(com.lowagie.text.Rectangle.NO_BORDER); // Xóa viền đen xấu xí
		if (isHeader) {
			cell.setBorder(com.lowagie.text.Rectangle.BOTTOM); // Chỉ để đường gạch ngang dưới header
			cell.setBorderWidthBottom(1f);
			cell.setPaddingBottom(8);
		}
		return cell;
	}

	private PdfPCell createBodyCell(String text, int align) {
		com.lowagie.text.Font font = FontFactory.getFont(FontFactory.HELVETICA, 11);
		PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font));
		cell.setHorizontalAlignment(align);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPadding(8);
		return cell;
	}

	private String formatTien(long so) {
		return String.format("%,d", so).replace(",", ".");
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			new FrmThuNgan("Quản trị viên").setVisible(true);
		});
	}
}
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
	private long tongCuoiCung = 0;
	private long tienGiamHienTai = 0;
	private long phiDichVuHienTai = 0;
	private long vatHienTai = 0;
	private long soTienCanThu = 0;

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

		JLabel btnBack = new JLabel("<html><font color='#DC2626'><b>←</b></font> Quay lại hệ thống</html>");
		btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnBack.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dispose();
			}
		});

		JLabel lbUser = new JLabel(
				"👤 " + tenNhanVien + " | " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		lbUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lbUser.setForeground(TEXT_DARK);

		bar.add(btnBack, BorderLayout.WEST);
		bar.add(lbUser, BorderLayout.EAST);
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
		paper.add(Box.createVerticalStrut(10));
		paper.add(createSummaryRow("Còn phải thanh toán:", lbConPhaiThanhToan));
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

		lbGiamGia.setText("-" + formatTien(tienGiamHienTai) + " đ");
		lbPhiDV.setText(formatTien(phiDichVuHienTai) + " đ");
		lbVAT.setText(formatTien(vatHienTai) + " đ");
		lbTongTien.setText(formatTien(tongCuoiCung) + " đ");
		lbTienCoc.setText("-" + formatTien(tienCoc) + " đ");
		lbConPhaiThanhToan.setText(formatTien(soTienCanThu) + " đ");

		tinhTienThua();
	}

	private void tinhTienThua() {
		try {
			String s = txtKhachDua.getText().trim().replace(".", "").replace(",", "");
			if (s.isEmpty()) {
				lbTienThua.setText("0 đ");
				lbTienThua.setForeground(new Color(0, 150, 0));
				return;
			}

			long khachDua = Long.parseLong(s);
			//long thua = khachDua - tongCuoiCung;
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
		String s = txtKhachDua.getText().trim().replace(".", "").replace(",", "");

		if (s.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền khách đưa.");
			txtKhachDua.requestFocus();
			return;
		}

		long khachDua;
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
					+ formatTien(khachDua) + " đ\n" + "Tổng cộng: " + formatTien(tongCuoiCung) + " đ");
			txtKhachDua.requestFocus();
			txtKhachDua.selectAll();
			return;
		}

		long tienThua = khachDua - tongCuoiCung;

		int opt = JOptionPane.showConfirmDialog(this,
				"Xác nhận khách đã trả tiền cho " + ban.tenBan + "?\n" + "Khách đưa: " + formatTien(khachDua) + " đ\n"
						+ "Tổng cộng: " + formatTien(tongCuoiCung) + " đ\n" + "Tiền thừa: " + formatTien(tienThua)
						+ " đ",
				"Thanh toán", JOptionPane.YES_NO_OPTION);

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

	private void xuatHoaDonPDF(BanAnModel ban, List<MonAnModel> dsMon, long khachDua, long tienThua) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Chọn nơi lưu hóa đơn PDF");
		chooser.setSelectedFile(new File("HoaDon_" + ban.tenBan.replaceAll("\\s+", "_") + "_" + ban.maHD + ".pdf"));

		int result = chooser.showSaveDialog(this);
		if (result != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File file = chooser.getSelectedFile();
		if (!file.getName().toLowerCase().endsWith(".pdf")) {
			file = new File(file.getAbsolutePath() + ".pdf");
		}

		Document document = new Document(PageSize.A4, 40, 40, 50, 40);

		try {
			PdfWriter.getInstance(document, new FileOutputStream(file));
			document.open();

			com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
			com.lowagie.text.Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
			com.lowagie.text.Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
			com.lowagie.text.Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

			Paragraph title = new Paragraph("NHA HANG NGOI DO", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);

			Paragraph sub = new Paragraph("Hoa don thanh toan", subFont);
			sub.setAlignment(Element.ALIGN_CENTER);
			sub.setSpacingAfter(15);
			document.add(sub);

			document.add(new Paragraph("Ma hoa don: " + ban.maHD, normalFont));
			document.add(new Paragraph("Ban: " + ban.tenBan, normalFont));
			document.add(
					new Paragraph("Khach hang: " + (ban.tenKH != null ? ban.tenKH : "Khach vang lai"), normalFont));
			document.add(new Paragraph("So dien thoai: " + (ban.sdt != null ? ban.sdt : ""), normalFont));
			document.add(new Paragraph("Gio vao: " + (ban.gioVao != null ? ban.gioVao : "--:--"), normalFont));
			document.add(new Paragraph("Gio ra: " + new SimpleDateFormat("HH:mm dd/MM/yyyy").format(new Date()),
					normalFont));
			document.add(new Paragraph("Thu ngan: " + tenNhanVien, normalFont));
			document.add(new Paragraph(" "));

			PdfPTable table = new PdfPTable(new float[] { 4f, 1.2f, 2f });
			table.setWidthPercentage(100);

			table.addCell(createHeaderCell("Mon an"));
			table.addCell(createHeaderCell("SL"));
			table.addCell(createHeaderCell("Thanh tien"));

			for (MonAnModel mon : dsMon) {
				table.addCell(createBodyCell(mon.tenMonAn));
				table.addCell(createBodyCell(String.valueOf(mon.soLuong), Element.ALIGN_CENTER));
				table.addCell(createBodyCell(formatTien(mon.thanhTien) + " d", Element.ALIGN_RIGHT));
			}

			document.add(table);
			document.add(new Paragraph(" "));

			String tenKM = cboKM_Current != null && cboKM_Current.getSelectedItem() != null
					? cboKM_Current.getSelectedItem().toString()
					: "Khong";

			document.add(new Paragraph("Tam tinh: " + formatTien(giaTriTamTinh) + " d", normalFont));
			document.add(new Paragraph("Khuyen mai: " + tenKM + " (" + lbGiamGia.getText() + ")", normalFont));
			document.add(new Paragraph("VAT: " + lbVAT.getText(), normalFont));
			document.add(new Paragraph("Tong cong: " + formatTien(tongCuoiCung) + " d", boldFont));
			document.add(new Paragraph("Khach dua: " + formatTien(khachDua) + " d", normalFont));
			document.add(new Paragraph("Tien thua: " + formatTien(tienThua) + " d", normalFont));

			document.add(new Paragraph(" "));
			Paragraph thanks = new Paragraph("Cam on quy khach va hen gap lai!", boldFont);
			thanks.setAlignment(Element.ALIGN_CENTER);
			document.add(thanks);

			JOptionPane.showMessageDialog(this, "Xuất PDF thành công:\n" + file.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Xuất PDF thất bại: " + e.getMessage());
		} finally {
			if (document.isOpen()) {
				document.close();
			}
		}
	}

	private PdfPCell createHeaderCell(String text) {
		com.lowagie.text.Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPadding(8);
		cell.setBackgroundColor(new Color(240, 240, 240));
		return cell;
	}

	private PdfPCell createBodyCell(String text) {
		return createBodyCell(text, Element.ALIGN_LEFT);
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
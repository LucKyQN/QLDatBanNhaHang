package GUI;

import DAO.HoaDonDAO;
import connectDatabase.ConnectDB;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;

public class FrmBaoCaoDoanhThu extends JPanel {

	private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
	private final String tenNhanVien;

	private static final Color RED_MAIN = new Color(220, 38, 38);
	private static final Color BG_MAIN = new Color(248, 248, 248);
	private static final Color TEXT_DARK = new Color(30, 30, 30);
	private static final Color TEXT_GRAY = new Color(110, 110, 110);
	private static final Color BORDER_CLR = new Color(230, 230, 230);

	// Stat labels
	private JLabel lbTongDT, lbSoDon, lbGiaTBDon, lbKhachHang;
	// Chart panels
	private LineChartPanel lineChart;
	private PieChartPanel pieChart;
	// Table
	private DefaultTableModel tblModel;
	// Period combo
	private JComboBox<String> cboPeriod;
	private long currentTongDT;
	private int currentSoDon;
	private int currentSoKH;
	private long currentGiaTB;

	private List<long[]> currentChartData = new ArrayList<>();
	private List<String[]> currentTopMon = new ArrayList<>();
	private List<String[]> currentPhanBo = new ArrayList<>();

	public FrmBaoCaoDoanhThu(String tenNhanVien) {
		this.tenNhanVien = tenNhanVien;

		initUI();
		loadData();
	}

	private void initUI() {
		setLayout(new BorderLayout(0, 0));
		setBackground(BG_MAIN);
		setBorder(new EmptyBorder(20, 24, 24, 24));

		JPanel north = new JPanel(new BorderLayout(0, 16));
		north.setOpaque(false);
		north.add(createTopActions(), BorderLayout.NORTH);
		north.add(createStatCards(), BorderLayout.SOUTH);

		add(north, BorderLayout.NORTH);

		JPanel center = new JPanel(new BorderLayout(0, 16));
		center.setOpaque(false);
		center.setBorder(new EmptyBorder(16, 0, 0, 0));
		center.add(createChartsRow(), BorderLayout.NORTH);
		center.add(createBottomTable(), BorderLayout.CENTER);

		JScrollPane scroll = new JScrollPane(center);
		scroll.setBorder(null);
		scroll.getViewport().setBackground(BG_MAIN);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		add(scroll, BorderLayout.CENTER);
	}

	// HEADER
	private JPanel createTopActions() {
		JPanel p = new JPanel(new BorderLayout());
		p.setOpaque(false);

		cboPeriod = new JComboBox<>(new String[] { "7 ngày qua", "30 ngày qua", "Tháng này", "Năm nay" });
		cboPeriod.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		cboPeriod.addActionListener(e -> loadData());

		JButton btnXuat = new JButton("⬇ Xuất báo cáo");
		btnXuat.setBackground(RED_MAIN);
		btnXuat.setForeground(Color.WHITE);
		btnXuat.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnXuat.setFocusPainted(false);
		btnXuat.setBorderPainted(false);
		btnXuat.setBorder(new EmptyBorder(9, 16, 9, 16));
		btnXuat.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnXuat.addActionListener(e -> xuatBaoCaoPDF());

		JPanel rightBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		rightBox.setOpaque(false);
		rightBox.add(cboPeriod);
		rightBox.add(btnXuat);

		p.add(rightBox, BorderLayout.EAST);
		return p;
	}

	// STAT CARDS
	private JPanel createStatCards() {
		JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
		row.setOpaque(false);

		lbTongDT = new JLabel("...");
		lbSoDon = new JLabel("...");
		lbGiaTBDon = new JLabel("...");
		lbKhachHang = new JLabel("...");

		row.add(createStatCard("💰", "Tổng doanh thu", lbTongDT, "", true, new Color(34, 197, 94)));
		row.add(createStatCard("🛒", "Số đơn hàng", lbSoDon, "", false, new Color(99, 102, 241)));
		row.add(createStatCard("📊", "Giá trị TB/đơn", lbGiaTBDon, "", true, new Color(168, 85, 247)));
		row.add(createStatCard("👥", "Khách hàng", lbKhachHang, "", false, new Color(251, 146, 60)));

		return row;
	}

	private JPanel createStatCard(String icon, String label, JLabel valueLabel, String badge, boolean positive,
			Color color) {
		JPanel card = new JPanel(new BorderLayout(0, 6));
		card.setBackground(Color.WHITE);
		card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true),
				new EmptyBorder(16, 18, 16, 18)));

		JPanel top = new JPanel(new BorderLayout());
		top.setOpaque(false);
		JPanel iconBox = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
				g2.dispose();
			}
		};
		iconBox.setPreferredSize(new Dimension(38, 38));
		iconBox.setOpaque(false);
		iconBox.setLayout(new GridBagLayout());
		JLabel lbIcon = new JLabel(icon);
		lbIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 17));
		iconBox.add(lbIcon);
		top.add(iconBox, BorderLayout.WEST);

		JLabel lbBadge = new JLabel(badge);
		lbBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
		lbBadge.setForeground(positive ? new Color(22, 163, 74) : RED_MAIN);
		JPanel bw = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
		bw.setOpaque(false);
		bw.add(lbBadge);
		top.add(bw, BorderLayout.EAST);

		JPanel bot = new JPanel();
		bot.setOpaque(false);
		bot.setLayout(new BoxLayout(bot, BoxLayout.Y_AXIS));
		JLabel lbLab = new JLabel(label);
		lbLab.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lbLab.setForeground(TEXT_GRAY);
		valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
		valueLabel.setForeground(TEXT_DARK);
		bot.add(lbLab);
		bot.add(Box.createVerticalStrut(4));
		bot.add(valueLabel);

		card.add(top, BorderLayout.NORTH);
		card.add(bot, BorderLayout.SOUTH);
		return card;
	}

	// CHARTS ROW
	private JPanel createChartsRow() {
		JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
		row.setOpaque(false);
		row.setPreferredSize(new Dimension(0, 320));

		// Line chart card
		JPanel lineCard = new JPanel(new BorderLayout());
		lineCard.setBackground(Color.WHITE);
		lineCard.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true),
				new EmptyBorder(16, 18, 16, 18)));
		JLabel lbLC = new JLabel("Xu hướng doanh thu");
		lbLC.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lbLC.setBorder(new EmptyBorder(0, 0, 10, 0));
		lineChart = new LineChartPanel();
		lineCard.add(lbLC, BorderLayout.NORTH);
		lineCard.add(lineChart, BorderLayout.CENTER);

		// Pie chart card
		JPanel pieCard = new JPanel(new BorderLayout());
		pieCard.setBackground(Color.WHITE);
		pieCard.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true),
				new EmptyBorder(16, 18, 16, 18)));
		JLabel lbPC = new JLabel("Phân bố theo danh mục");
		lbPC.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lbPC.setBorder(new EmptyBorder(0, 0, 10, 0));
		pieChart = new PieChartPanel();
		pieCard.add(lbPC, BorderLayout.NORTH);
		pieCard.add(pieChart, BorderLayout.CENTER);

		row.add(lineCard);
		row.add(pieCard);
		return row;
	}

	// BOTTOM TABLE
	private JPanel createBottomTable() {
		JPanel card = new JPanel(new BorderLayout(0, 12));
		card.setBackground(Color.WHITE);
		card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true),
				new EmptyBorder(16, 16, 16, 16)));

		JLabel lbTitle = new JLabel("Món ăn bán chạy nhất");
		lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
		lbTitle.setBorder(new EmptyBorder(0, 0, 10, 0));

		tblModel = new DefaultTableModel(
				new String[] { "Món ăn", "Danh mục", "Số lượng bán", "Doanh thu", "% Tổng ĐT" }, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		JTable tbl = new JTable(tblModel);
		tbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		tbl.setRowHeight(40);
		tbl.setShowGrid(false);
		tbl.setIntercellSpacing(new Dimension(0, 0));
		tbl.setBackground(Color.WHITE);
		tbl.setFocusable(false);
		tbl.setSelectionBackground(new Color(254, 242, 242));

		JTableHeader hdr = tbl.getTableHeader();
		hdr.setFont(new Font("Segoe UI", Font.BOLD, 12));
		hdr.setBackground(new Color(249, 250, 251));
		hdr.setForeground(TEXT_GRAY);
		hdr.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR));
		hdr.setPreferredSize(new Dimension(0, 40));
		((DefaultTableCellRenderer) hdr.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

		tbl.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row,
					int col) {
				super.getTableCellRendererComponent(t, val, sel, foc, row, col);
				setBackground(sel ? new Color(254, 242, 242) : Color.WHITE);
				setFont(new Font("Segoe UI", col == 0 ? Font.BOLD : Font.PLAIN, 13));
				setForeground(TEXT_DARK);
				setBorder(new EmptyBorder(0, 12, 0, 12));
				return this;
			}
		});

		JScrollPane sc = new JScrollPane(tbl);
		sc.setBorder(null);
		sc.getViewport().setBackground(Color.WHITE);

		card.add(lbTitle, BorderLayout.NORTH);
		card.add(sc, BorderLayout.CENTER);
		return card;
	}

	private String getTenKyBaoCao() {
		return switch (cboPeriod.getSelectedIndex()) {
		case 0 -> "7_ngay_qua";
		case 1 -> "30_ngay_qua";
		case 2 -> "thang_nay";
		default -> "nam_nay";
		};
	}

	private String formatTienPDF(long soTien) {
		return String.format("%,d", soTien).replace(",", ".");
	}

	private com.lowagie.text.Font taoFontUnicode(float size, int style) throws Exception {
		BaseFont bf = BaseFont.createFont("C:/Windows/Fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		return new com.lowagie.text.Font(bf, size, style);
	}

	private PdfPCell taoCell(String text, com.lowagie.text.Font font, int align) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setHorizontalAlignment(align);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPadding(8f);
		return cell;
	}

	private void xuatBaoCaoPDF() {
		if (currentTopMon == null) {
			JOptionPane.showMessageDialog(this, "Chưa có dữ liệu để xuất.");
			return;
		}

		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Chọn nơi lưu báo cáo PDF");
		chooser.setSelectedFile(new File("bao_cao_doanh_thu_" + getTenKyBaoCao() + ".pdf"));

		int result = chooser.showSaveDialog(this);
		if (result != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File file = chooser.getSelectedFile();
		if (!file.getName().toLowerCase().endsWith(".pdf")) {
			file = new File(file.getAbsolutePath() + ".pdf");
		}

		Document document = new Document(PageSize.A4, 36, 36, 36, 36);

		try {
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
			document.open();

			com.lowagie.text.Font fontTitle = taoFontUnicode(18, com.lowagie.text.Font.BOLD);
			com.lowagie.text.Font fontSub = taoFontUnicode(12, com.lowagie.text.Font.NORMAL);
			com.lowagie.text.Font fontBold = taoFontUnicode(12, com.lowagie.text.Font.BOLD);
			com.lowagie.text.Font fontNormal = taoFontUnicode(11, com.lowagie.text.Font.NORMAL);

			// --- HEADER BÁO CÁO ---
			Paragraph title = new Paragraph("BÁO CÁO DOANH THU NHÀ HÀNG NGÓI ĐỎ", fontTitle);
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);

			document.add(new Paragraph("Kỳ báo cáo: " + cboPeriod.getSelectedItem(), fontSub));
			document.add(new Paragraph("Ngày xuất báo cáo: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), fontSub));
			document.add(new Paragraph("Người xuất: " + tenNhanVien, fontSub));
			document.add(new Paragraph("---------------------------------------------------------------------------------------------------------", fontNormal));
			document.add(new Paragraph(" "));

			// --- PHẦN 1: THỐNG KÊ TỔNG QUAN ---
			document.add(new Paragraph("1. THỐNG KÊ TỔNG QUAN KINH DOANH", fontBold));
			document.add(new Paragraph(" "));

			PdfPTable tTongQuan = new PdfPTable(4);
			tTongQuan.setWidthPercentage(100);
			tTongQuan.addCell(taoCell("TỔNG DOANH THU", fontBold, Element.ALIGN_CENTER));
			tTongQuan.addCell(taoCell("SỐ ĐƠN HÀNG", fontBold, Element.ALIGN_CENTER));
			tTongQuan.addCell(taoCell("GIÁ TRỊ TRUNG BÌNH/ĐƠN", fontBold, Element.ALIGN_CENTER));
			tTongQuan.addCell(taoCell("SỐ KHÁCH HÀNG", fontBold, Element.ALIGN_CENTER));

			tTongQuan.addCell(taoCell(formatTienPDF(currentTongDT) + " đ", fontNormal, Element.ALIGN_CENTER));
			tTongQuan.addCell(taoCell(String.valueOf(currentSoDon), fontNormal, Element.ALIGN_CENTER));
			tTongQuan.addCell(taoCell(formatTienPDF(currentGiaTB) + " đ", fontNormal, Element.ALIGN_CENTER));
			tTongQuan.addCell(taoCell(String.valueOf(currentSoKH), fontNormal, Element.ALIGN_CENTER));
			document.add(tTongQuan);
			document.add(new Paragraph(" "));

			// --- PHẦN 2: CHỤP ẢNH BIỂU ĐỒ ---
			document.add(new Paragraph("2. BIỂU ĐỒ TRỰC QUAN", fontBold));
			document.add(new Paragraph(" "));

			PdfPTable tChart = new PdfPTable(2);
			tChart.setWidthPercentage(100);
			tChart.setWidths(new float[]{1f, 1f});

			try {
				// Chụp ảnh Line Chart
				int wLine = lineChart.getWidth();
				int hLine = lineChart.getHeight();
				java.awt.image.BufferedImage imgLine = new java.awt.image.BufferedImage(wLine, hLine, java.awt.image.BufferedImage.TYPE_INT_RGB);
				Graphics2D gLine = imgLine.createGraphics();
				lineChart.paint(gLine);
				gLine.dispose();
				com.lowagie.text.Image pdfImgLine = com.lowagie.text.Image.getInstance(imgLine, null);

				// Chụp ảnh Pie Chart
				int wPie = pieChart.getWidth();
				int hPie = pieChart.getHeight();
				java.awt.image.BufferedImage imgPie = new java.awt.image.BufferedImage(wPie, hPie, java.awt.image.BufferedImage.TYPE_INT_RGB);
				Graphics2D gPie = imgPie.createGraphics();
				pieChart.paint(gPie);
				gPie.dispose();
				com.lowagie.text.Image pdfImgPie = com.lowagie.text.Image.getInstance(imgPie, null);

				// Gắn ảnh vào bảng (để 2 ảnh nằm ngang nhau)
				PdfPCell cellLine = new PdfPCell(pdfImgLine, true);
				cellLine.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
				cellLine.setPadding(5);
				tChart.addCell(cellLine);

				PdfPCell cellPie = new PdfPCell(pdfImgPie, true);
				cellPie.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
				cellPie.setPadding(5);
				tChart.addCell(cellPie);

				document.add(tChart);
			} catch (Exception e) {
				System.err.println("Lỗi chụp biểu đồ: " + e.getMessage());
			}
			document.add(new Paragraph(" "));

			// --- PHẦN 3: CHI TIẾT DANH MỤC ---
			document.add(new Paragraph("3. PHÂN BỐ DOANH THU THEO DANH MỤC", fontBold));
			if (currentPhanBo.isEmpty()) {
				document.add(new Paragraph("Chưa có dữ liệu.", fontNormal));
			} else {
				for (String[] row : currentPhanBo) {
					document.add(new Paragraph("    • " + row[0] + ": " + formatTienPDF(Long.parseLong(row[1])) + " đ", fontNormal));
				}
			}
			document.add(new Paragraph(" "));

			// --- PHẦN 4: TOP MÓN BÁN CHẠY (Chuyển sang trang mới nếu cần) ---
			document.add(new Paragraph("4. BẢNG XẾP HẠNG TOP MÓN ĂN BÁN CHẠY NHẤT", fontBold));
			document.add(new Paragraph(" "));

			PdfPTable table = new PdfPTable(5);
			table.setWidthPercentage(100);
			table.setWidths(new float[] { 3.2f, 2.2f, 1.6f, 2.0f, 1.4f });

			table.addCell(taoCell("Món ăn", fontBold, Element.ALIGN_LEFT));
			table.addCell(taoCell("Danh mục", fontBold, Element.ALIGN_LEFT));
			table.addCell(taoCell("SL bán", fontBold, Element.ALIGN_CENTER));
			table.addCell(taoCell("Doanh thu", fontBold, Element.ALIGN_RIGHT));
			table.addCell(taoCell("% Tổng ĐT", fontBold, Element.ALIGN_CENTER));

			if (currentTopMon.isEmpty()) {
				PdfPCell empty = taoCell("Chưa có dữ liệu", fontNormal, Element.ALIGN_CENTER);
				empty.setColspan(5);
				table.addCell(empty);
			} else {
				for (String[] row : currentTopMon) {
					table.addCell(taoCell(row[0], fontNormal, Element.ALIGN_LEFT));
					table.addCell(taoCell(row[1], fontNormal, Element.ALIGN_LEFT));
					table.addCell(taoCell(row[2], fontNormal, Element.ALIGN_CENTER));
					table.addCell(taoCell(row[3], fontNormal, Element.ALIGN_RIGHT));
					table.addCell(taoCell(row[4], fontNormal, Element.ALIGN_CENTER));
				}
			}

			document.add(table);

			// --- KẾT THÚC ---
			document.add(new Paragraph("\n"));
			Paragraph signature = new Paragraph("Người lập báo cáo\n\n\n(Ký và ghi rõ họ tên)", fontNormal);
			signature.setAlignment(Element.ALIGN_RIGHT);
			document.add(signature);

			document.close();

			JOptionPane.showMessageDialog(this, "Xuất báo cáo PDF thành công!\nFile: " + file.getAbsolutePath(),
					"Thành công", JOptionPane.INFORMATION_MESSAGE);

			// Tự động mở file sau khi xuất
			java.awt.Desktop.getDesktop().open(file);

		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Lỗi khi xuất PDF!\n" + ex.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			if (document.isOpen()) {
				document.close();
			}
		}
	}

	// LOAD DATA FROM DB
	private void loadData() {
		SwingWorker<Void, Void> w = new SwingWorker<>() {
			long tongDT;
			int soDon, soKH;
			long giaTB;
			List<long[]> chartData = new ArrayList<>();
			List<String[]> topMon = new ArrayList<>();
			List<String[]> phanBo = new ArrayList<>();

			@Override
			protected Void doInBackground() {
				try {
					Connection con = ConnectDB.getInstance().getConnection();
					int days = switch (cboPeriod.getSelectedIndex()) {
					case 0 -> 7;
					case 1 -> 30;
					case 2 -> 30;
					default -> 365;
					};

					// Tổng doanh thu
					String sqlDT = "SELECT ISNULL(SUM(tongTien),0) FROM HoaDon "
							+ "WHERE trangThaiThanhToan=N'Đã thanh toán' "
							+ "AND ngayGioThanhToan >= DATEADD(DAY,-?,CAST(GETDATE() AS DATE))";
					PreparedStatement ps = con.prepareStatement(sqlDT);
					ps.setInt(1, days);
					ResultSet rs = ps.executeQuery();
					tongDT = rs.next() ? (long) rs.getDouble(1) : 0;
					rs.close();
					ps.close();

					// Số đơn
					String sqlDon = "SELECT COUNT(*) FROM HoaDon WHERE trangThaiThanhToan=N'Đã thanh toán' "
							+ "AND ngayGioThanhToan >= DATEADD(DAY,-?,CAST(GETDATE() AS DATE))";
					ps = con.prepareStatement(sqlDon);
					ps.setInt(1, days);
					rs = ps.executeQuery();
					soDon = rs.next() ? rs.getInt(1) : 0;
					rs.close();
					ps.close();
					giaTB = soDon > 0 ? tongDT / soDon : 0;

					// Khách hàng
					String sqlKH = "SELECT COUNT(DISTINCT ISNULL(tenKhachLe,'?')) FROM HoaDon "
							+ "WHERE trangThaiThanhToan=N'Đã thanh toán' "
							+ "AND ngayGioThanhToan >= DATEADD(DAY,-?,CAST(GETDATE() AS DATE))";
					ps = con.prepareStatement(sqlKH);
					ps.setInt(1, days);
					rs = ps.executeQuery();
					soKH = rs.next() ? rs.getInt(1) : 0;
					rs.close();
					ps.close();

					// Line chart: doanh thu theo ngày
					String sqlLine = "SELECT CAST(ngayGioThanhToan AS DATE) as ngay, ISNULL(SUM(tongTien),0) as dt "
							+ "FROM HoaDon WHERE trangThaiThanhToan=N'Đã thanh toán' "
							+ "AND ngayGioThanhToan >= DATEADD(DAY,-?,CAST(GETDATE() AS DATE)) "
							+ "GROUP BY CAST(ngayGioThanhToan AS DATE) ORDER BY ngay";
					ps = con.prepareStatement(sqlLine);
					ps.setInt(1, days);
					rs = ps.executeQuery();
					while (rs.next())
						chartData.add(new long[] { rs.getDate("ngay").getTime(), (long) rs.getDouble("dt") });
					rs.close();
					ps.close();

					// Top món bán chạy
					String sqlTop = "SELECT TOP 10 m.tenMonAn, dm.tenDM, "
							+ "SUM(c.soLuong) as sl, SUM(c.thanhTien) as dt "
							+ "FROM ChiTietHoaDon c JOIN MonAn m ON c.maMonAn=m.maMonAn "
							+ "JOIN DanhMucMonAn dm ON m.maDM=dm.maDM " + "JOIN HoaDon h ON c.maHD=h.maHD "
							+ "WHERE h.trangThaiThanhToan=N'Đã thanh toán' "
							+ "AND h.ngayGioThanhToan >= DATEADD(DAY,-?,CAST(GETDATE() AS DATE)) "
							+ "GROUP BY m.tenMonAn, dm.tenDM ORDER BY sl DESC";
					ps = con.prepareStatement(sqlTop);
					ps.setInt(1, days);
					rs = ps.executeQuery();
					while (rs.next())
						topMon.add(new String[] { rs.getString("tenMonAn"), rs.getString("tenDM"),
								String.valueOf(rs.getInt("sl")),
								String.format("%,.0fđ", rs.getDouble("dt")).replace(",", "."), "" });
					rs.close();
					ps.close();

					// Phân bố danh mục (pie chart)
					String sqlPie = "SELECT dm.tenDM, SUM(c.thanhTien) as dt "
							+ "FROM ChiTietHoaDon c JOIN MonAn m ON c.maMonAn=m.maMonAn "
							+ "JOIN DanhMucMonAn dm ON m.maDM=dm.maDM " + "JOIN HoaDon h ON c.maHD=h.maHD "
							+ "WHERE h.trangThaiThanhToan=N'Đã thanh toán' "
							+ "AND h.ngayGioThanhToan >= DATEADD(DAY,-?,CAST(GETDATE() AS DATE)) "
							+ "GROUP BY dm.tenDM ORDER BY dt DESC";
					ps = con.prepareStatement(sqlPie);
					ps.setInt(1, days);
					rs = ps.executeQuery();
					while (rs.next())
						phanBo.add(new String[] { rs.getString("tenDM"), String.valueOf((long) rs.getDouble("dt")) });
					rs.close();
					ps.close();

					// Tính % cho topMon
					if (!topMon.isEmpty() && tongDT > 0) {
						for (String[] row : topMon) {
							long dt = Long.parseLong(row[3].replaceAll("[^0-9]", ""));
							row[4] = String.format("%.1f%%", (double) dt / tongDT * 100);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void done() {
				// Stat cards
				lbTongDT.setText(tongDT == 0 ? "0đ" : String.format("%,.0fđ", (double) tongDT).replace(",", "."));
				lbSoDon.setText(String.valueOf(soDon));
				lbGiaTBDon.setText(giaTB == 0 ? "0đ" : String.format("%,.0fđ", (double) giaTB).replace(",", "."));
				lbKhachHang.setText(String.valueOf(soKH));

				// Charts
				lineChart.setData(chartData);
				pieChart.setData(phanBo);

				// Table
				tblModel.setRowCount(0);
				for (String[] r : topMon)
					tblModel.addRow(r);
				currentTongDT = tongDT;
				currentSoDon = soDon;
				currentSoKH = soKH;
				currentGiaTB = giaTB;

				currentChartData = new ArrayList<>(chartData);
				currentTopMon = new ArrayList<>(topMon);
				currentPhanBo = new ArrayList<>(phanBo);
			}
		};
		w.execute();
	}

	// LINE CHART (tự vẽ)
	static class LineChartPanel extends JPanel {
		private List<long[]> data = new ArrayList<>();
		private static final Color LINE_COLOR = new Color(220, 38, 38);

		public void setData(List<long[]> data) {
			this.data = data;
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(getBackground());
			g2.fillRect(0, 0, getWidth(), getHeight());

			if (data == null || data.isEmpty()) {
				g2.setColor(new Color(180, 180, 180));
				g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
				String msg = "Chưa có dữ liệu";
				FontMetrics fm = g2.getFontMetrics();
				g2.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
				g2.dispose();
				return;
			}

			int pad = 40, chartW = getWidth() - pad * 2, chartH = getHeight() - pad * 2;
			long maxDT = data.stream().mapToLong(d -> d[1]).max().orElse(1);
			if (maxDT == 0)
				maxDT = 1;

			// Grid lines
			g2.setColor(new Color(240, 240, 240));
			for (int i = 0; i <= 4; i++) {
				int y = pad + chartH * i / 4;
				g2.drawLine(pad, y, pad + chartW, y);
			}

			// Line
			g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2.setColor(LINE_COLOR);
			int n = data.size();
			int[] xs = new int[n], ys = new int[n];
			for (int i = 0; i < n; i++) {
				xs[i] = pad + chartW * i / Math.max(n - 1, 1);
				ys[i] = pad + chartH - (int) (chartH * data.get(i)[1] / maxDT);
			}
			for (int i = 0; i < n - 1; i++)
				g2.drawLine(xs[i], ys[i], xs[i + 1], ys[i + 1]);

			// Dots + labels x
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
			g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
			for (int i = 0; i < n; i++) {
				g2.setColor(LINE_COLOR);
				g2.fillOval(xs[i] - 4, ys[i] - 4, 8, 8);
				g2.setColor(new Color(120, 120, 120));
				String lbl = sdf.format(new Date(data.get(i)[0]));
				FontMetrics fm = g2.getFontMetrics();
				g2.drawString(lbl, xs[i] - fm.stringWidth(lbl) / 2, pad + chartH + 14);
			}

			// Y label
			g2.setColor(new Color(120, 120, 120));
			String maxStr = String.format("%.0fTr đ", (double) maxDT / 1_000_000);
			g2.drawString(maxStr, 2, pad + 8);
			g2.drawString("→ Doanh thu", pad + chartW / 2 - 30, pad + chartH + 28);
			g2.dispose();
		}
	}

	// PIE CHART (tự vẽ)
	static class PieChartPanel extends JPanel {
		private List<String[]> data = new ArrayList<>();
		private static final Color[] COLORS = { new Color(220, 38, 38), new Color(251, 146, 60), new Color(234, 179, 8),
				new Color(34, 197, 94), new Color(99, 102, 241) };

		public void setData(List<String[]> data) {
			this.data = data;
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(getBackground());
			g2.fillRect(0, 0, getWidth(), getHeight());

			if (data == null || data.isEmpty()) {
				g2.setColor(new Color(180, 180, 180));
				g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
				String msg = "Chưa có dữ liệu";
				FontMetrics fm = g2.getFontMetrics();
				g2.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
				g2.dispose();
				return;
			}

			long total = data.stream().mapToLong(d -> Long.parseLong(d[1])).sum();
			if (total == 0) {
				g2.dispose();
				return;
			}

			int sz = Math.min(getWidth() / 2, getHeight()) - 20;
			int cx = getWidth() / 4, cy = getHeight() / 2;
			int x = cx - sz / 2, y = cy - sz / 2;

			double angle = 0;
			for (int i = 0; i < data.size() && i < COLORS.length; i++) {
				double pct = (double) Long.parseLong(data.get(i)[1]) / total;
				double sweep = 360 * pct;
				g2.setColor(COLORS[i]);
				g2.fillArc(x, y, sz, sz, (int) angle, (int) sweep);
				angle += sweep;
			}

			// Legend
			g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
			int lx = getWidth() / 2 + 10, ly = cy - data.size() * 18 / 2;
			for (int i = 0; i < data.size() && i < COLORS.length; i++) {
				g2.setColor(COLORS[i]);
				g2.fillRoundRect(lx, ly + i * 22, 12, 12, 4, 4);
				g2.setColor(new Color(40, 40, 40));
				double pct = (double) Long.parseLong(data.get(i)[1]) / total * 100;
				g2.drawString(data.get(i)[0] + " (" + String.format("%.0f%%", pct) + ")", lx + 18, ly + i * 22 + 11);
			}
			g2.dispose();
		}
	}
}
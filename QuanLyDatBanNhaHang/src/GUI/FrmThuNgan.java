//package GUI;
//
//import DAO.HoaDonDAO;
//import Model.BanAnModel;
//import Model.MonAnModel;
//
//import javax.swing.*;
//import javax.swing.border.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.awt.print.*;
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//
//public class FrmThuNgan extends JFrame {
//
//    private final String tenNhanVien;
//    private final HoaDonDAO dao = new HoaDonDAO();
//
//    private static final Color RED_MAIN   = new Color(220, 38, 38);
//    private static final Color BG_MAIN    = new Color(248, 248, 248);
//    private static final Color TEXT_DARK  = new Color(20, 20, 20);
//    private static final Color TEXT_GRAY  = new Color(120, 120, 120);
//    private static final Color BORDER_CLR = new Color(225, 225, 225);
//
//    private List<BanAnModel> danhSachBan;
//    private BanAnModel banDangChon = null;
//    private JPanel pnlDanhSachBan;
//    private JPanel pnlHoaDon;
//
//    public FrmThuNgan(String tenNhanVien) {
//        this.tenNhanVien = tenNhanVien;
//        initUI();
//        taiDanhSachBan();
//    }
//
//    private void initUI() {
//        setTitle("Thanh toán & Hóa đơn - Nhà Hàng Ngói Đỏ");
//        setSize(1440, 860);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        JPanel root = new JPanel(new BorderLayout());
//        root.setBackground(BG_MAIN);
//        root.add(createTopBar(), BorderLayout.NORTH);
//        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
//                createLeftPanel(), createRightPanel());
//        split.setDividerLocation(290);
//        split.setDividerSize(1);
//        split.setBorder(null);
//        root.add(split, BorderLayout.CENTER);
//        setContentPane(root);
//    }
//
//    // ============================================================
//    //  TẢI DỮ LIỆU TỪ DB
//    // ============================================================
//    private void taiDanhSachBan() {
//        SwingWorker<List<BanAnModel>, Void> worker = new SwingWorker<>() {
//            @Override protected List<BanAnModel> doInBackground() {
//                return dao.getDanhSachBanChuaThanhToan();
//            }
//            @Override protected void done() {
//                try {
//                    danhSachBan = get();
//                    laBanCards();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    JOptionPane.showMessageDialog(FrmThuNgan.this,
//                            "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        };
//        worker.execute();
//    }
//
//    // ---- TOP BAR ----
//    private JPanel createTopBar() {
//        JPanel bar = new JPanel(new BorderLayout(16, 0));
//        bar.setBackground(Color.WHITE);
//        bar.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
//                new EmptyBorder(14, 20, 14, 24)));
//        JLabel btnBack = new JLabel("←");
//        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 22));
//        btnBack.setForeground(TEXT_DARK);
//        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        btnBack.addMouseListener(new MouseAdapter() {
//            @Override public void mouseClicked(MouseEvent e) { dispose(); }
//            @Override public void mouseEntered(MouseEvent e) { btnBack.setForeground(RED_MAIN); }
//            @Override public void mouseExited(MouseEvent e)  { btnBack.setForeground(TEXT_DARK); }
//        });
//        JPanel info = new JPanel();
//        info.setOpaque(false);
//        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
//        JLabel lbTitle = new JLabel("Thanh toán & Hóa đơn");
//        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
//        JLabel lbRole = new JLabel("Thu ngân  |  " + tenNhanVien);
//        lbRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
//        lbRole.setForeground(TEXT_GRAY);
//        info.add(lbTitle);
//        info.add(lbRole);
//        bar.add(btnBack, BorderLayout.WEST);
//        bar.add(info, BorderLayout.CENTER);
//        return bar;
//    }
//
//    // ---- PANEL TRÁI ----
//    private JPanel createLeftPanel() {
//        JPanel left = new JPanel(new BorderLayout());
//        left.setBackground(Color.WHITE);
//        left.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_CLR));
//        left.setPreferredSize(new Dimension(290, 0));
//        JLabel lbTitle = new JLabel("Bàn cần thanh toán");
//        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        lbTitle.setBorder(new EmptyBorder(16, 16, 12, 16));
//        pnlDanhSachBan = new JPanel();
//        pnlDanhSachBan.setBackground(Color.WHITE);
//        pnlDanhSachBan.setLayout(new BoxLayout(pnlDanhSachBan, BoxLayout.Y_AXIS));
//        pnlDanhSachBan.setBorder(new EmptyBorder(0, 12, 12, 12));
//        JScrollPane scroll = new JScrollPane(pnlDanhSachBan);
//        scroll.setBorder(null);
//        scroll.getVerticalScrollBar().setUnitIncrement(12);
//        left.add(lbTitle, BorderLayout.NORTH);
//        left.add(scroll, BorderLayout.CENTER);
//        return left;
//    }
//
//    private void laBanCards() {
//        pnlDanhSachBan.removeAll();
//        if (danhSachBan == null || danhSachBan.isEmpty()) {
//            JLabel lbEmpty = new JLabel("Không có bàn cần thanh toán");
//            lbEmpty.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//            lbEmpty.setForeground(TEXT_GRAY);
//            lbEmpty.setBorder(new EmptyBorder(20, 8, 0, 8));
//            pnlDanhSachBan.add(lbEmpty);
//        } else {
//            for (BanAnModel ban : danhSachBan) {
//                pnlDanhSachBan.add(createBanCard(ban));
//                pnlDanhSachBan.add(Box.createVerticalStrut(10));
//            }
//        }
//        pnlDanhSachBan.revalidate();
//        pnlDanhSachBan.repaint();
//    }
//
//    private JPanel createBanCard(BanAnModel ban) {
//        boolean selected = ban == banDangChon;
//        JPanel card = new JPanel(new BorderLayout(0, 4));
//        card.setBackground(selected ? new Color(254, 242, 242) : Color.WHITE);
//        card.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(selected ? RED_MAIN : BORDER_CLR, selected ? 2 : 1, true),
//                new EmptyBorder(12, 14, 12, 14)));
//        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
//        JPanel topRow = new JPanel(new BorderLayout());
//        topRow.setOpaque(false);
//        JLabel lbBan   = new JLabel(ban.tenBan);
//        lbBan.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        JLabel lbNguoi = new JLabel(ban.sucChua + " người");
//        lbNguoi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
//        lbNguoi.setForeground(TEXT_GRAY);
//        topRow.add(lbBan, BorderLayout.WEST);
//        topRow.add(lbNguoi, BorderLayout.EAST);
//        JLabel lbTong = new JLabel(formatTien(ban.tamTinh) + " đ");
//        lbTong.setFont(new Font("Segoe UI", Font.BOLD, 13));
//        lbTong.setForeground(RED_MAIN);
//        card.add(topRow, BorderLayout.NORTH);
//        card.add(lbTong, BorderLayout.CENTER);
//        card.addMouseListener(new MouseAdapter() {
//            @Override public void mouseClicked(MouseEvent e) {
//                banDangChon = ban;
//                laBanCards();
//                hienThiHoaDon(ban);
//            }
//            @Override public void mouseEntered(MouseEvent e) {
//                if (ban != banDangChon) card.setBackground(new Color(250, 250, 250));
//            }
//            @Override public void mouseExited(MouseEvent e) {
//                if (ban != banDangChon) card.setBackground(Color.WHITE);
//            }
//        });
//        return card;
//    }
//
//    // ---- PANEL PHẢI ----
//    private JPanel createRightPanel() {
//        pnlHoaDon = new JPanel(new BorderLayout());
//        pnlHoaDon.setBackground(BG_MAIN);
//        hienThiChoChon();
//        return pnlHoaDon;
//    }
//
//    private void hienThiChoChon() {
//        pnlHoaDon.removeAll();
//        JLabel lbHint = new JLabel("Vui lòng chọn bàn cần thanh toán");
//        lbHint.setFont(new Font("Segoe UI", Font.PLAIN, 15));
//        lbHint.setForeground(TEXT_GRAY);
//        lbHint.setHorizontalAlignment(SwingConstants.CENTER);
//        pnlHoaDon.add(lbHint, BorderLayout.CENTER);
//        pnlHoaDon.revalidate(); pnlHoaDon.repaint();
//    }
//
//    private void hienThiHoaDon(BanAnModel ban) {
//        pnlHoaDon.removeAll();
//        // Load chi tiết món từ DB
//        List<MonAnModel> dsMon = dao.getChiTietHoaDon(ban.maHD);
//        List<String[]>   dsKM  = dao.getKhuyenMaiHieuLuc();
//
//        JPanel wrapper = new JPanel(new BorderLayout());
//        wrapper.setBackground(BG_MAIN);
//        wrapper.setBorder(new EmptyBorder(20, 24, 20, 24));
//        JScrollPane scroll = new JScrollPane(buildHoaDonPanel(ban, dsMon, dsKM));
//        scroll.setBorder(null);
//        scroll.getVerticalScrollBar().setUnitIncrement(12);
//        wrapper.add(scroll, BorderLayout.CENTER);
//        wrapper.add(buildBottomBar(ban, dsKM), BorderLayout.SOUTH);
//        pnlHoaDon.add(wrapper, BorderLayout.CENTER);
//        pnlHoaDon.revalidate(); pnlHoaDon.repaint();
//    }
//
//    private JPanel buildHoaDonPanel(BanAnModel ban, List<MonAnModel> dsMon, List<String[]> dsKM) {
//        JPanel panel = new JPanel();
//        panel.setBackground(Color.WHITE);
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//        panel.setBorder(new EmptyBorder(20, 24, 20, 24));
//
//        // Header
//        JPanel header = new JPanel(new BorderLayout());
//        header.setOpaque(false);
//        JPanel left = new JPanel(); left.setOpaque(false);
//        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
//        JLabel lbHd = new JLabel("Hóa đơn");
//        lbHd.setFont(new Font("Segoe UI", Font.BOLD, 22));
//        JLabel lbBanInfo = new JLabel(ban.tenBan + " - Sức chứa: " + ban.sucChua + " người");
//        lbBanInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        lbBanInfo.setForeground(TEXT_GRAY);
//        left.add(lbHd); left.add(Box.createVerticalStrut(4)); left.add(lbBanInfo);
//        JPanel right = new JPanel(); right.setOpaque(false);
//        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
//        JLabel lbNgay = new JLabel("Ngày: " + LocalDate.now().format(DateTimeFormatter.ofPattern("d/M/yyyy")));
//        lbNgay.setFont(new Font("Segoe UI", Font.PLAIN, 12)); lbNgay.setForeground(TEXT_GRAY);
//        lbNgay.setAlignmentX(Component.RIGHT_ALIGNMENT);
//        JLabel lbGio = new JLabel("Giờ: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
//        lbGio.setFont(new Font("Segoe UI", Font.PLAIN, 12)); lbGio.setForeground(TEXT_GRAY);
//        lbGio.setAlignmentX(Component.RIGHT_ALIGNMENT);
//        right.add(lbNgay); right.add(lbGio);
//        header.add(left, BorderLayout.WEST); header.add(right, BorderLayout.EAST);
//        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
//        panel.add(header);
//        panel.add(Box.createVerticalStrut(18));
//        panel.add(createDivider());
//        panel.add(Box.createVerticalStrut(14));
//
//        JLabel lbChiTiet = new JLabel("Chi tiết đơn hàng");
//        lbChiTiet.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        lbChiTiet.setAlignmentX(Component.LEFT_ALIGNMENT);
//        panel.add(lbChiTiet);
//        panel.add(Box.createVerticalStrut(12));
//
//        long tamTinh = 0;
//        for (MonAnModel mon : dsMon) {
//            panel.add(createMonRow(mon));
//            panel.add(Box.createVerticalStrut(10));
//            tamTinh += mon.thanhTien;
//        }
//
//        panel.add(Box.createVerticalStrut(10));
//        panel.add(createDivider());
//        panel.add(Box.createVerticalStrut(14));
//
//        // Tính tiền
//        final long tamTinhFinal = tamTinh;
//        final long[] tongTien = {tamTinh};
//
//        // Dropdown khuyến mãi từ DB
//        String[] tenKM = dsKM.stream().map(k -> k[1]).toArray(String[]::new);
//        JComboBox<String> cboKM = new JComboBox<>(tenKM);
//        cboKM.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//
//        JLabel lbTamTinh  = createRowRight(formatTien(tamTinh) + " đ");
//        JLabel lbGiamGia  = createRowRight("0 đ");
//        JLabel lbPhiDV    = createRowRight(formatTien(tamTinh * 4 / 100) + " đ");
//        JLabel lbVAT      = createRowRight(formatTien(tamTinh * 6 / 100) + " đ");
//        JLabel lbTongTien = new JLabel(formatTien(tamTinh + tamTinh*4/100 + tamTinh*6/100) + " đ");
//        lbTongTien.setFont(new Font("Segoe UI", Font.BOLD, 18));
//        lbTongTien.setForeground(RED_MAIN);
//
//        JTextField txtTienCoc = new JTextField("0");
//        txtTienCoc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        txtTienCoc.setHorizontalAlignment(JTextField.RIGHT);
//        txtTienCoc.setPreferredSize(new Dimension(120, 30));
//        txtTienCoc.setMaximumSize(new Dimension(120, 30));
//
//        Runnable updateTong = () -> {
//            int idx = cboKM.getSelectedIndex();
//            double giaTriKM = idx >= 0 ? Double.parseDouble(dsKM.get(idx)[2]) : 0;
//            String loaiKM   = idx >= 0 ? dsKM.get(idx)[3] : "Phần trăm";
//            long giam = loaiKM.equals("Phần trăm")
//                    ? (long)(tamTinhFinal * giaTriKM / 100)
//                    : (long) giaTriKM;
//            long base = tamTinhFinal - giam;
//            long phiDV = base * 4 / 100;
//            long vat   = base * 6 / 100;
//            long coc;
//            try { coc = Long.parseLong(txtTienCoc.getText().replaceAll("[^0-9]","")); }
//            catch (Exception ex) { coc = 0; }
//            long tong = base + phiDV + vat - coc;
//            lbGiamGia.setText(giam > 0 ? "-" + formatTien(giam) + " đ" : "0 đ");
//            lbPhiDV.setText(formatTien(phiDV) + " đ");
//            lbVAT.setText(formatTien(vat) + " đ");
//            lbTongTien.setText(formatTien(Math.max(tong, 0)) + " đ");
//            tongTien[0] = tong;
//        };
//
//        cboKM.addActionListener(e -> updateTong.run());
//        txtTienCoc.addKeyListener(new KeyAdapter() {
//            @Override public void keyReleased(KeyEvent e) { updateTong.run(); }
//        });
//
//        panel.add(createSummaryRow("Tạm tính:",          lbTamTinh,  null));
//        panel.add(Box.createVerticalStrut(10));
//        panel.add(createSummaryRow("Khuyến mãi:",         lbGiamGia,  cboKM));
//        panel.add(Box.createVerticalStrut(10));
//        panel.add(createSummaryRow("Phí phục vụ (4%):",  lbPhiDV,    null));
//        panel.add(Box.createVerticalStrut(10));
//        panel.add(createSummaryRow("VAT (6%):",           lbVAT,      null));
//        panel.add(Box.createVerticalStrut(10));
//        panel.add(createSummaryRow("Tiền cọc:",           null,       txtTienCoc));
//        panel.add(Box.createVerticalStrut(14));
//        panel.add(createDivider());
//        panel.add(Box.createVerticalStrut(14));
//
//        JPanel rowTong = new JPanel(new BorderLayout());
//        rowTong.setOpaque(false);
//        rowTong.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
//        JLabel lbTongLabel = new JLabel("Tổng tiền:");
//        lbTongLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
//        rowTong.add(lbTongLabel, BorderLayout.WEST);
//        rowTong.add(lbTongTien,  BorderLayout.EAST);
//        panel.add(rowTong);
//        panel.add(Box.createVerticalStrut(20));
//
//        // Lưu tham chiếu tổng tiền + maKM để bottom bar dùng
//        panel.putClientProperty("tongTien", tongTien);
//        panel.putClientProperty("cboKM",    cboKM);
//        panel.putClientProperty("dsKM",     dsKM);
//        return panel;
//    }
//
//    private JPanel buildBottomBar(BanAnModel ban, List<String[]> dsKM) {
//        JPanel bar = new JPanel(new BorderLayout(12, 0));
//        bar.setBackground(BG_MAIN);
//        bar.setBorder(new EmptyBorder(12, 0, 0, 0));
//
//        RoundedButton btnThanhToan = new RoundedButton("Thanh toán", 10);
//        btnThanhToan.setBackground(RED_MAIN);
//        btnThanhToan.setForeground(Color.WHITE);
//        btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 16));
//        btnThanhToan.setFocusPainted(false);
//        btnThanhToan.setBorderPainted(false);
//        btnThanhToan.setPreferredSize(new Dimension(0, 52));
//        btnThanhToan.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        btnThanhToan.addActionListener(e -> xuLyThanhToan(ban, dsKM));
//
//        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
//        rightBtns.setOpaque(false);
//        JButton btnIn  = createOutlineButton("🖨  In Hóa Đơn", new Color(60,60,60));
//        JButton btnHuy = createOutlineButton("⊘  Hủy/Hoàn",   new Color(220,100,30));
//        btnIn.addActionListener(e  -> inHoaDon(ban));
//        btnHuy.addActionListener(e -> xacNhanHuy(ban));
//        rightBtns.add(btnIn); rightBtns.add(btnHuy);
//
//        bar.add(btnThanhToan, BorderLayout.CENTER);
//        bar.add(rightBtns,    BorderLayout.EAST);
//        return bar;
//    }
//
//    // ============================================================
//    //  XỬ LÝ SỰ KIỆN
//    // ============================================================
//    private void xuLyThanhToan(BanAnModel ban, List<String[]> dsKM) {
//        int opt = JOptionPane.showConfirmDialog(this,
//                "Xác nhận thanh toán " + ban.tenBan + "?",
//                "Thanh toán", JOptionPane.YES_NO_OPTION);
//        if (opt != JOptionPane.YES_OPTION) return;
//
//        // Lấy maKM đang chọn (nếu có)
//        // Để đơn giản, truyền maKM rỗng; nếu muốn lấy từ combobox cần truyền thêm ref
//        boolean ok = dao.thanhToan(ban.maHD, 0, "");
//        if (ok) {
//            danhSachBan.remove(ban);
//            banDangChon = null;
//            laBanCards();
//            hienThiChoChon();
//            JOptionPane.showMessageDialog(this,
//                    "✅ Thanh toán " + ban.tenBan + " thành công!",
//                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
//        } else {
//            JOptionPane.showMessageDialog(this, "Lỗi khi thanh toán!", "Lỗi", JOptionPane.ERROR_MESSAGE);
//        }
//    }
//
//    private void inHoaDon(BanAnModel ban) {
//        String email = JOptionPane.showInputDialog(this,
//                "Nhập địa chỉ Gmail để nhận hóa đơn PDF:",
//                "In Hóa Đơn", JOptionPane.PLAIN_MESSAGE);
//        if (email == null || email.trim().isEmpty()) return;
//        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
//            JOptionPane.showMessageDialog(this, "Email không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//        List<MonAnModel> dsMon = dao.getChiTietHoaDon(ban.maHD);
//        PrinterJob job = PrinterJob.getPrinterJob();
//        job.setJobName("HoaDon_" + ban.tenBan.replace(" ",""));
//        job.setPrintable((graphics, pageFormat, pageIndex) -> {
//            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
//            Graphics2D g2 = (Graphics2D) graphics;
//            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
//            g2.setFont(new Font("Serif", Font.BOLD, 16));
//            g2.drawString("NHA HANG NGOI DO - HOA DON", 60, 40);
//            g2.setFont(new Font("Serif", Font.PLAIN, 12));
//            g2.drawString(ban.tenBan + "  |  Ma HD: " + ban.maHD, 60, 60);
//            g2.drawString("Ngay: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
//                    + "  Gio: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")), 60, 80);
//            g2.drawLine(60, 92, 480, 92);
//            int y = 112;
//            long tong = 0;
//            for (MonAnModel mon : dsMon) {
//                g2.drawString(mon.tenMonAn + "  x" + mon.soLuong
//                        + "  =  " + formatTien(mon.thanhTien) + "d", 60, y);
//                y += 20; tong += mon.thanhTien;
//            }
//            g2.drawLine(60, y, 480, y); y += 20;
//            g2.setFont(new Font("Serif", Font.BOLD, 13));
//            g2.drawString("TONG TIEN: " + formatTien(tong) + "d", 60, y);
//            return Printable.PAGE_EXISTS;
//        });
//        if (job.printDialog()) {
//            try {
//                job.print();
//                JOptionPane.showMessageDialog(this,
//                        "✅ Đã gửi hóa đơn đến: " + email + "\n(Cần cấu hình JavaMail để gửi thật)",
//                        "Gửi thành công", JOptionPane.INFORMATION_MESSAGE);
//            } catch (PrinterException ex) {
//                JOptionPane.showMessageDialog(this, "Lỗi in: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
//            }
//        }
//    }
//
//    private void xacNhanHuy(BanAnModel ban) {
//        int opt = JOptionPane.showConfirmDialog(this,
//                "Hủy toàn bộ đơn hàng của " + ban.tenBan + "?\nHành động này không thể hoàn tác.",
//                "Hủy / Hoàn đơn", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
//        if (opt != JOptionPane.YES_OPTION) return;
//        boolean ok = dao.huyHoaDon(ban.maHD);
//        if (ok) {
//            danhSachBan.remove(ban);
//            banDangChon = null;
//            laBanCards();
//            hienThiChoChon();
//        } else {
//            JOptionPane.showMessageDialog(this, "Lỗi khi hủy đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
//        }
//    }
//
//    // ============================================================
//    //  HELPER UI
//    // ============================================================
//    private JPanel createMonRow(MonAnModel mon) {
//        JPanel row = new JPanel(new BorderLayout());
//        row.setOpaque(false);
//        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
//        JLabel lbTen = new JLabel(mon.tenMonAn);
//        lbTen.setFont(new Font("Segoe UI", Font.BOLD, 13));
//        JPanel rightCol = new JPanel(); rightCol.setOpaque(false);
//        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
//        JLabel lbQty = new JLabel(mon.soLuong + " x " + formatTien(mon.donGia) + " đ");
//        lbQty.setFont(new Font("Segoe UI", Font.PLAIN, 12)); lbQty.setForeground(TEXT_GRAY);
//        lbQty.setAlignmentX(Component.RIGHT_ALIGNMENT);
//        JLabel lbTT = new JLabel(formatTien(mon.thanhTien) + " đ");
//        lbTT.setFont(new Font("Segoe UI", Font.BOLD, 13)); lbTT.setForeground(RED_MAIN);
//        lbTT.setAlignmentX(Component.RIGHT_ALIGNMENT);
//        rightCol.add(lbQty); rightCol.add(lbTT);
//        row.add(lbTen, BorderLayout.WEST); row.add(rightCol, BorderLayout.EAST);
//        return row;
//    }
//
//    private JPanel createSummaryRow(String label, JLabel valueLabel, JComponent control) {
//        JPanel row = new JPanel(new BorderLayout(8, 0));
//        row.setOpaque(false);
//        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
//        JLabel lbLabel = new JLabel(label);
//        lbLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13)); lbLabel.setForeground(TEXT_GRAY);
//        row.add(lbLabel, BorderLayout.WEST);
//        JPanel rs = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 2)); rs.setOpaque(false);
//        if (control != null) rs.add(control);
//        if (valueLabel != null) rs.add(valueLabel);
//        row.add(rs, BorderLayout.EAST);
//        return row;
//    }
//
//    private JLabel createRowRight(String text) {
//        JLabel l = new JLabel(text);
//        l.setFont(new Font("Segoe UI", Font.PLAIN, 13)); l.setForeground(TEXT_DARK);
//        return l;
//    }
//
//    private JSeparator createDivider() {
//        JSeparator sep = new JSeparator();
//        sep.setForeground(BORDER_CLR);
//        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
//        return sep;
//    }
//
//    private JButton createOutlineButton(String text, Color borderColor) {
//        JButton btn = new JButton(text);
//        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        btn.setForeground(borderColor); btn.setBackground(Color.WHITE);
//        btn.setFocusPainted(false); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        btn.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(borderColor, 1, true),
//                new EmptyBorder(8, 16, 8, 16)));
//        btn.setPreferredSize(new Dimension(140, 52));
//        return btn;
//    }
//
//    private static String formatTien(long so) {
//        return String.format("%,d", so).replace(",", ".");
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new FrmThuNgan("Thu Ngân").setVisible(true));
//    }
//}
package GUI;

import DAO.HoaDonDAO;
import Model.BanAnModel;
import Model.MonAnModel;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class FrmThuNgan extends JFrame {

    private final String tenNhanVien;
    private final HoaDonDAO dao = new HoaDonDAO();

    private static final Color RED_MAIN   = new Color(220, 38, 38);
    private static final Color BG_MAIN    = new Color(248, 248, 248);
    private static final Color TEXT_DARK  = new Color(20, 20, 20);
    private static final Color TEXT_GRAY  = new Color(120, 120, 120);
    private static final Color BORDER_CLR = new Color(225, 225, 225);

    private List<BanAnModel> danhSachBan = new ArrayList<>();
    private BanAnModel banDangChon = null;
    private JPanel pnlDanhSachBan;
    private JPanel pnlHoaDon;
    
    // Components tính toán
    private JComboBox<String> cboKM_Current;
    private List<String[]> dsKM_Current = new ArrayList<>();
    private JLabel lbTamTinh, lbGiamGia, lbPhiDV, lbVAT, lbTongTien;
    private long giaTriTamTinh = 0;

    public FrmThuNgan(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
        initUI();
        taiDanhSachBan();
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

    // ============================================================
    // LOGIC DỮ LIỆU
    // ============================================================
    private void taiDanhSachBan() {
        new SwingWorker<List<BanAnModel>, Void>() {
            @Override protected List<BanAnModel> doInBackground() {
                return dao.getDanhSachBanChuaThanhToan();
            }
            @Override protected void done() {
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

    // ============================================================
    // UI COMPONENTS
    // ============================================================
    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
                new EmptyBorder(15, 25, 15, 25)));

        JLabel btnBack = new JLabel("<html><font color='#DC2626'><b>←</b></font> Quay lại hệ thống</html>");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { dispose(); }
        });

        JLabel lbUser = new JLabel("👤 " + tenNhanVien + " | " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
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
            @Override public void mouseClicked(MouseEvent e) {
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
        JLabel lbHint = new JLabel("<html><center><font size='5' color='#777777'>🏮</font><br>Vui lòng chọn bàn cần thanh toán bên trái</center></html>");
        lbHint.setHorizontalAlignment(SwingConstants.CENTER);
        pnlHoaDon.add(lbHint, BorderLayout.CENTER);
        pnlHoaDon.revalidate(); pnlHoaDon.repaint();
    }

    private void hienThiHoaDon(BanAnModel ban) {
        pnlHoaDon.removeAll();
        List<MonAnModel> dsMon = dao.getChiTietHoaDon(ban.maHD);
        dsKM_Current = dao.getKhuyenMaiHieuLuc();

        JPanel container = new JPanel(new BorderLayout(0, 25));
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(30, 50, 30, 50));

        container.add(buildBillPaper(ban, dsMon), BorderLayout.CENTER);
        container.add(buildFooterActions(ban), BorderLayout.SOUTH);

        pnlHoaDon.add(container, BorderLayout.CENTER);
        pnlHoaDon.revalidate(); pnlHoaDon.repaint();
        
        tinhToanLai(); // Tính tiền lần đầu
    }

    private JPanel buildBillPaper(BanAnModel ban, List<MonAnModel> dsMon) {
        JPanel paper = new JPanel();
        paper.setBackground(Color.WHITE);
        paper.setLayout(new BoxLayout(paper, BoxLayout.Y_AXIS));
        paper.setBorder(new CompoundBorder(
                new LineBorder(BORDER_CLR, 1),
                new EmptyBorder(35, 45, 35, 45)));

        // Header hóa đơn
        JLabel lbHdTitle = new JLabel("HÓA ĐƠN CHI TIẾT");
        lbHdTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        paper.add(lbHdTitle);
        paper.add(Box.createVerticalStrut(5));
        paper.add(new JLabel("Bàn: " + ban.tenBan + "  |  Mã HD: " + ban.maHD));
        paper.add(Box.createVerticalStrut(30));

        // Danh sách món ăn
        giaTriTamTinh = 0;
        for (MonAnModel mon : dsMon) {
            paper.add(createItemRow(mon));
            paper.add(Box.createVerticalStrut(12));
            giaTriTamTinh += mon.thanhTien;
        }

        paper.add(Box.createVerticalStrut(20));
        paper.add(new JSeparator());
        paper.add(Box.createVerticalStrut(25));

        // Phần tổng hợp tiền
        lbTamTinh = new JLabel(formatTien(giaTriTamTinh) + " đ");
        lbGiamGia = new JLabel("0 đ");
        lbPhiDV   = new JLabel("0 đ");
        lbVAT     = new JLabel("0 đ");
        lbTongTien = new JLabel("0 đ");
        lbTongTien.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lbTongTien.setForeground(RED_MAIN);

        // Khuyến mãi
        String[] tenKMs = dsKM_Current.stream().map(k -> k[1]).toArray(String[]::new);
        cboKM_Current = new JComboBox<>(tenKMs);
        cboKM_Current.setPreferredSize(new Dimension(200, 30));
        cboKM_Current.addActionListener(e -> tinhToanLai());

        paper.add(createSummaryRow("Tạm tính tổng món:", lbTamTinh));
        paper.add(createSummaryRow("Chương trình khuyến mãi:", cboKM_Current, lbGiamGia));
        paper.add(createSummaryRow("Phí dịch vụ (5%):", lbPhiDV));
        paper.add(createSummaryRow("Thuế VAT (10%):", lbVAT));
        paper.add(Box.createVerticalStrut(20));
        paper.add(createSummaryRow("TỔNG CỘNG THANH TOÁN:", lbTongTien));

        return paper;
    }

    private void tinhToanLai() {
        if (dsKM_Current == null || dsKM_Current.isEmpty() || cboKM_Current == null) return;
        
        int idx = cboKM_Current.getSelectedIndex();
        if (idx < 0) return;

        double giaTriKM = Double.parseDouble(dsKM_Current.get(idx)[2]);
        String loaiKM   = dsKM_Current.get(idx)[3];

        long tienGiam = loaiKM.equals("Phần trăm") ? (long)(giaTriTamTinh * giaTriKM / 100) : (long)giaTriKM;
        long sauGiam = giaTriTamTinh - tienGiam;
        long phiDV = (long)(sauGiam * 0.05);
        long vat = (long)(sauGiam * 0.1);
        long tongCuoi = sauGiam + phiDV + vat;

        lbGiamGia.setText("-" + formatTien(tienGiam) + " đ");
        lbPhiDV.setText(formatTien(phiDV) + " đ");
        lbVAT.setText(formatTien(vat) + " đ");
        lbTongTien.setText(formatTien(Math.max(0, tongCuoi)) + " đ");
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

    // ============================================================
    // SỰ KIỆN NÚT BẤM
    // ============================================================
    private void xuLyThanhToan(BanAnModel ban) {
        int opt = JOptionPane.showConfirmDialog(this, 
                "Xác nhận khách đã trả tiền cho " + ban.tenBan + "?", 
                "Thanh toán", JOptionPane.YES_NO_OPTION);
        
        if (opt == JOptionPane.YES_OPTION) {
            String maKM = dsKM_Current.get(cboKM_Current.getSelectedIndex())[0];
            // Thanh toán với chiết khấu bằng tay = 0 (vì đã có KM hệ thống)
            if (dao.thanhToan(ban.maHD, 0.0, maKM)) {
                JOptionPane.showMessageDialog(this, "✅ Đã lưu hóa đơn & giải phóng bàn!");
                banDangChon = null;
                taiDanhSachBan();
                hienThiChoChon();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Lỗi: Không thể kết nối cơ sở dữ liệu!");
            }
        }
    }

    private void xacNhanHuyDon(BanAnModel ban) {
        int opt = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc muốn HỦY đơn hàng này?\nBàn sẽ quay về trạng thái Trống.", 
                "Cảnh báo", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (opt == JOptionPane.YES_OPTION && dao.huyHoaDon(ban.maHD)) {
            banDangChon = null;
            taiDanhSachBan();
            hienThiChoChon();
        }
    }

    // ============================================================
    // HELPERS
    // ============================================================
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
        for (JComponent c : components) right.add(c);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    private String formatTien(long so) {
        return String.format("%,d", so).replace(",", ".");
    }

    public static void main(String[] args) {
        // Chạy thử với tên nhân viên giả lập
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {}
            new FrmThuNgan("Quản trị viên").setVisible(true);
        });
    }
}
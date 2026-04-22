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
            System.out.println("Đang cập nhật danh sách bàn");
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
                dispose();
                new FrmDangNhap().setVisible(true);
            }
        });

        // --- NÚT LỊCH SỬ HÓA ĐƠN ---
        JButton btnLichSu = new JButton("Lịch sử hóa đơn");
        btnLichSu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLichSu.setBackground(new Color(219, 234, 254));
        btnLichSu.setForeground(new Color(30, 58, 138));
        btnLichSu.setFocusPainted(false);
        btnLichSu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLichSu.addActionListener(e -> moLichSuHoaDon());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(lbUser);
        rightPanel.add(btnLichSu);
        rightPanel.add(btnCaiDat);
        rightPanel.add(btnLogout);

        bar.add(west, BorderLayout.WEST);
        bar.add(rightPanel, BorderLayout.EAST);
        return bar;
    }

    private void moLichSuHoaDon() {
        JDialog dialog = new JDialog(this, "Lịch sử Hóa Đơn", true);
        dialog.setSize(1050, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout(0, 15));
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Lịch sử giao dịch");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        // --- BỘ LỌC TÌM KIẾM THEO NGÀY ---
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlFilter.setOpaque(false);

        JLabel lblTuNgay = new JLabel("Từ ngày:");
        lblTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JSpinner spinTuNgay = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorTuNgay = new JSpinner.DateEditor(spinTuNgay, "dd/MM/yyyy");
        spinTuNgay.setEditor(editorTuNgay);
        spinTuNgay.setPreferredSize(new Dimension(120, 32));
        spinTuNgay.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel lblDenNgay = new JLabel("Đến ngày:");
        lblDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JSpinner spinDenNgay = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorDenNgay = new JSpinner.DateEditor(spinDenNgay, "dd/MM/yyyy");
        spinDenNgay.setEditor(editorDenNgay);
        spinDenNgay.setPreferredSize(new Dimension(120, 32));
        spinDenNgay.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JButton btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setBackground(RED_MAIN);
        btnTimKiem.setForeground(Color.WHITE);
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTimKiem.setFocusPainted(false);
        btnTimKiem.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnLamMoi = new JButton("Làm mới");
        btnLamMoi.setBackground(new Color(240, 240, 240));
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLamMoi.setFocusPainted(false);
        btnLamMoi.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlFilter.add(lblTuNgay);
        pnlFilter.add(spinTuNgay);
        pnlFilter.add(lblDenNgay);
        pnlFilter.add(spinDenNgay);
        pnlFilter.add(btnTimKiem);
        pnlFilter.add(btnLamMoi);

        header.add(title, BorderLayout.NORTH);
        header.add(pnlFilter, BorderLayout.CENTER);

        // --- BẢNG DỮ LIỆU ---
        String[] cols = {"Mã Hóa Đơn", "Bàn", "Khách hàng", "Thời gian thanh toán", "Tổng tiền", "Trạng thái"};
        javax.swing.table.DefaultTableModel modelList = new javax.swing.table.DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(modelList);
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(254, 242, 242));
        table.setSelectionForeground(TEXT_DARK);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(0, 20, 20, 20),
                BorderFactory.createLineBorder(BORDER_CLR)
        ));
        scroll.getViewport().setBackground(Color.WHITE);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        String maHD = table.getValueAt(row, 0).toString();
                        String tenBan = table.getValueAt(row, 1).toString();
                        String tenKhach = table.getValueAt(row, 2).toString();
                        String tongTien = table.getValueAt(row, 4).toString();

                        hienThiChiTietHoaDonLichSu(maHD, tenBan, tenKhach, tongTien);
                    }
                }
            }
        });

        Runnable loadData = () -> {
            modelList.setRowCount(0);
            List<String[]> data = dao.getLichSuHoaDonTheoNgay(null, null);
            for (String[] row : data) {
                modelList.addRow(row);
            }
        };

        // --- SỰ KIỆN NÚT BẤM ---
        btnTimKiem.addActionListener(e -> {
            java.util.Date tuNgay = (java.util.Date) spinTuNgay.getValue();
            java.util.Date denNgay = (java.util.Date) spinDenNgay.getValue();

            if (tuNgay.after(denNgay)) {
                JOptionPane.showMessageDialog(dialog, "'Từ ngày' không được lớn hơn 'Đến ngày'!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            modelList.setRowCount(0);
            List<String[]> data = dao.getLichSuHoaDonTheoNgay(tuNgay, denNgay);
            for (String[] row : data) {
                modelList.addRow(row);
            }

            if (data.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Không tìm thấy hóa đơn nào trong khoảng thời gian này.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnLamMoi.addActionListener(e -> {
            spinTuNgay.setValue(new java.util.Date());
            spinDenNgay.setValue(new java.util.Date());
            loadData.run();
        });


        loadData.run();

        dialog.add(header, BorderLayout.NORTH);
        dialog.add(scroll, BorderLayout.CENTER);
        dialog.setVisible(true);
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

        if (dao.thanhToan(ban.maHD, soTienCanThu, tienGiamHienTai, maKM, khachDua, tienThua)) {
            JOptionPane.showMessageDialog(this, "Đã lưu hóa đơn và giải phóng bàn!");

            int export = JOptionPane.showConfirmDialog(this, "Bạn có muốn xuất hóa đơn PDF không?",
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
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = new File(file.getAbsolutePath() + ".pdf");
        }

        Document document = new Document(PageSize.A5, 20, 20, 30, 30);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();


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
            PdfPTable table = new PdfPTable(new float[]{4f, 1f, 2f});
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
            PdfPTable tSummary = new PdfPTable(new float[]{3f, 2f});
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
            PdfPTable tCus = new PdfPTable(new float[]{3f, 2f});
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
        cell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
        if (isHeader) {
            cell.setBorder(com.lowagie.text.Rectangle.BOTTOM);
            cell.setBorderWidthBottom(1f);
            cell.setPaddingBottom(8);
        }
        return cell;
    }


    // HIỂN THỊ POPUP CHI TIẾT
    private void hienThiChiTietHoaDonLichSu(String maHD, String tenBan, String tenKhach, String tongTienStr) {
        // 1. Lấy thông tin phụ từ DAO
        Object[] info = dao.getThongTinChiTietHoaDonLichSu(maHD);
        String gioVao = info[0] != null ? info[0].toString() : "--:--";
        String gioRa = info[1] != null ? info[1].toString() : "--:--";
        String thuNgan = info[2] != null ? info[2].toString() : "---";
        long tienCoc = info[3] != null ? (long) info[3] : 0;
        long tienGiam = info[4] != null ? (long) info[4] : 0;
        long tienKhachDua = info[6] != null ? (long) info[6] : 0;
        long tienThuaTraKhach = info[7] != null ? (long) info[7] : 0;
        String tenKM = info[5] != null ? info[5].toString() : "Không";

        JDialog dialog = new JDialog(this, "Chi tiết Hóa Đơn: " + maHD, true);
        dialog.setSize(850, 750);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel paper = new JPanel();
        paper.setBackground(Color.WHITE);
        paper.setLayout(new BoxLayout(paper, BoxLayout.Y_AXIS));
        paper.setBorder(new EmptyBorder(30, 40, 30, 40));

        // --- HEADER ---
        JLabel lbTitle = new JLabel("HÓA ĐƠN CHI TIẾT");
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        paper.add(lbTitle);
        paper.add(Box.createVerticalStrut(20));

        JPanel pnlInfo = new JPanel(new GridLayout(3, 2, 20, 10));
        pnlInfo.setOpaque(false);
        pnlInfo.add(new JLabel("<html><span style='color:gray'>Mã Hóa Đơn:</span> <b>" + maHD + "</b></html>"));
        pnlInfo.add(new JLabel("<html><span style='color:gray'>Thu ngân:</span> <b>" + thuNgan + "</b></html>"));
        pnlInfo.add(new JLabel("<html><span style='color:gray'>Khách hàng:</span> <b>" + tenKhach + "</b></html>"));
        pnlInfo.add(new JLabel("<html><span style='color:gray'>Bàn:</span> <b>" + tenBan + "</b></html>"));
        pnlInfo.add(new JLabel("<html><span style='color:gray'>Giờ vào:</span> <b>" + gioVao + "</b></html>"));
        pnlInfo.add(new JLabel("<html><span style='color:gray'>Giờ ra:</span> <b>" + gioRa + "</b></html>"));
        paper.add(pnlInfo);

        paper.add(Box.createVerticalStrut(20));
        paper.add(new JSeparator());
        paper.add(Box.createVerticalStrut(20));

        long tamTinh = 0;
        List<MonAnModel> dsMon = dao.getChiTietHoaDon(maHD);

        JPanel pnlMonAn = new JPanel();
        pnlMonAn.setLayout(new BoxLayout(pnlMonAn, BoxLayout.Y_AXIS));
        pnlMonAn.setOpaque(false);

        for (MonAnModel m : dsMon) {
            JPanel pRow = new JPanel(new BorderLayout());
            pRow.setOpaque(false);
            JLabel lbTen = new JLabel(m.tenMonAn + " (x" + m.soLuong + ")");
            lbTen.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            JLabel lbGia = new JLabel(formatTien(m.thanhTien) + " đ");
            lbGia.setFont(new Font("Segoe UI", Font.BOLD, 15));
            pRow.add(lbTen, BorderLayout.WEST);
            pRow.add(lbGia, BorderLayout.EAST);

            pnlMonAn.add(pRow);
            pnlMonAn.add(Box.createVerticalStrut(10));
            tamTinh += m.thanhTien;
        }
        paper.add(pnlMonAn);

        paper.add(Box.createVerticalStrut(15));
        paper.add(new JSeparator());
        paper.add(Box.createVerticalStrut(15));

        long sauGiam = Math.max(0, tamTinh - tienGiam);
        long phiDV = (long) (sauGiam * 0.05);
        long vat = (long) (sauGiam * 0.10);
        long tongCong = sauGiam + phiDV + vat;

        long soTienCanThu = Math.max(0, tongCong - tienCoc);
        long soTienThucThu = Math.max(0, tienKhachDua - tienThuaTraKhach);
        long hoanCoc = Math.max(0, tienCoc - tongCong);

        paper.add(createSummaryRow("Tạm tính:", new JLabel(formatTien(tamTinh) + " đ")));
        paper.add(createSummaryRow("Khuyến mãi (" + tenKM + "):", new JLabel("-" + formatTien(tienGiam) + " đ")));
        paper.add(createSummaryRow("Phí dịch vụ (5%):", new JLabel(formatTien(phiDV) + " đ")));
        paper.add(createSummaryRow("VAT (10%):", new JLabel(formatTien(vat) + " đ")));

        JLabel lbTong = new JLabel(formatTien(tongCong) + " đ");
        lbTong.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbTong.setForeground(RED_MAIN);
        paper.add(createSummaryRow("TỔNG CỘNG:", lbTong));

        JLabel lbCoc = new JLabel("-" + formatTien(tienCoc) + " đ");
        lbCoc.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbCoc.setForeground(new Color(22, 163, 74));
        paper.add(createSummaryRow("Đã cọc:", lbCoc));

        paper.add(createSummaryRow("Cần thanh toán:", new JLabel(formatTien(soTienCanThu) + " đ")));
        paper.add(createSummaryRow("Khách đưa:", new JLabel(formatTien(tienKhachDua) + " đ")));
        paper.add(createSummaryRow("Tiền thừa trả khách:", new JLabel(formatTien(tienThuaTraKhach) + " đ")));

        if (hoanCoc > 0) {
            paper.add(createSummaryRow("Hoàn lại từ tiền cọc:", new JLabel(formatTien(hoanCoc) + " đ")));
        }

        JLabel lbDaThu = new JLabel(formatTien(soTienThucThu) + " đ");
        lbDaThu.setFont(new Font("Segoe UI", Font.BOLD, 18));
        paper.add(createSummaryRow("SỐ TIỀN THỰC THU:", lbDaThu));

        JScrollPane scroll = new JScrollPane(paper);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        dialog.add(scroll, BorderLayout.CENTER);
        dialog.setVisible(true);
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
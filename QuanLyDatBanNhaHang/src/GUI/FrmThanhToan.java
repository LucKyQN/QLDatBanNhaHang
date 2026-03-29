package GUI;

import java.awt.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import connectDatabase.ConnectDB;

public class FrmThanhToan extends JDialog {

    private static final Color RED_MAIN = new Color(220, 38, 38);
    private static final Color BORDER_CLR = new Color(230, 230, 230);
    private static final Color TEXT_DARK = new Color(40, 40, 40);

    private String tenBan;
    private double tongTienBill = 0; 
    
    private JLabel lblTotalDisplay; // Nhãn hiển thị tiền khách cần trả
    private JLabel lblTienThua;
    private JTextField txtKhachDua;
    private DefaultTableModel model;

    public FrmThanhToan(JFrame parent, String tenBan) {
        super(parent, true);
        this.tenBan = tenBan;
        
        setSize(950, 650);
        setLocationRelativeTo(parent);
        setUndecorated(true); 

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));

        root.add(createHeader(), BorderLayout.NORTH);
        
        JPanel body = new JPanel(new GridLayout(1, 2, 20, 0));
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        body.add(createBillArea());
        body.add(createPaymentArea());
        
        root.add(body, BorderLayout.CENTER);
        setContentPane(root);

        // GỌI HÀM LOAD DỮ LIỆU THẬT NGAY KHI MỞ FORM
        loadDataBill();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel title = new JLabel("Thanh toán - " + tenBan);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JButton btnClose = new JButton("✕");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> this.dispose()); 

        header.add(title, BorderLayout.WEST);
        header.add(btnClose, BorderLayout.EAST);
        
        JPanel bottomLine = new JPanel(new BorderLayout());
        bottomLine.add(header, BorderLayout.CENTER);
        bottomLine.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR));
        return bottomLine;
    }

    private JPanel createBillArea() {
        JPanel pnlBill = new JPanel(new BorderLayout(0, 10));
        pnlBill.setBackground(Color.WHITE);
        
        JLabel lblTitle = new JLabel("Chi tiết hóa đơn");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pnlBill.add(lblTitle, BorderLayout.NORTH);

        String[] columns = {"Tên món", "SL", "Đơn giá", "Thành tiền"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
        scroll.getViewport().setBackground(Color.WHITE);
        
        pnlBill.add(scroll, BorderLayout.CENTER);
        return pnlBill;
    }

    private void loadDataBill() {
        model.setRowCount(0);
        tongTienBill = 0;
        try {
            Connection con = connectDatabase.ConnectDB.getInstance().getConnection();
            
            // CẬP NHẬT: Sử dụng 'donGia' thay cho 'giaBan'
            String sql = "SELECT m.tenMonAn, ct.soLuong, ct.donGia " +
                         "FROM ChiTietHoaDon ct " +
                         "JOIN HoaDon h ON ct.maHD = h.maHD " +
                         "JOIN MonAn m ON ct.maMonAn = m.maMonAn " +
                         "WHERE h.maBan = ? AND h.trangThaiThanhToan = N'Chưa thanh toán'"; 
            
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, tenBan); 
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String ten = rs.getString("tenMonAn");
                int sl = rs.getInt("soLuong");
                double gia = rs.getDouble("donGia"); // Sửa ở đây
                double thanhTien = sl * gia;
                
                model.addRow(new Object[]{ten, sl, formatMoney((int)gia), formatMoney((int)thanhTien)});
                tongTienBill += thanhTien;
            }
            
            lblTotalDisplay.setText(formatMoney((int)tongTienBill));
            System.out.println("✅ Load thành công bill cho " + tenBan);

        } catch (Exception e) {
            System.err.println("❌ LỖI KHI LOAD BILL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JPanel createPaymentArea() {
        JPanel pnlPay = new JPanel();
        pnlPay.setLayout(new BoxLayout(pnlPay, BoxLayout.Y_AXIS));
        pnlPay.setBackground(Color.WHITE);
        pnlPay.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR),
            new EmptyBorder(20, 20, 20, 20)
        ));

        lblTotalDisplay = new JLabel("0 đ");
        lblTotalDisplay.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotalDisplay.setForeground(RED_MAIN);

        pnlPay.add(createRowItem("Khách cần trả:", "", new Font("Segoe UI", Font.BOLD, 22), RED_MAIN));
        // Ép nhãn tiền vào bên phải của dòng đầu tiên
        ((JPanel)pnlPay.getComponent(0)).add(lblTotalDisplay, BorderLayout.EAST);

        pnlPay.add(Box.createVerticalStrut(25));

        // Ô nhập tiền khách đưa
        JPanel pnlKhachDua = new JPanel(new BorderLayout());
        pnlKhachDua.setBackground(Color.WHITE);
        pnlKhachDua.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel lblKD = new JLabel("Khách thanh toán:");
        lblKD.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        txtKhachDua = new JTextField();
        txtKhachDua.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtKhachDua.setHorizontalAlignment(JTextField.RIGHT);
        txtKhachDua.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { tinhTienThua(); }
            public void removeUpdate(DocumentEvent e) { tinhTienThua(); }
            public void insertUpdate(DocumentEvent e) { tinhTienThua(); }
        });

        pnlKhachDua.add(lblKD, BorderLayout.WEST);
        pnlKhachDua.add(txtKhachDua, BorderLayout.CENTER);
        pnlPay.add(pnlKhachDua);
        pnlPay.add(Box.createVerticalStrut(25));

        lblTienThua = new JLabel("0 đ");
        lblTienThua.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pnlPay.add(createRowItem("Tiền thừa trả khách:", "", new Font("Segoe UI", Font.BOLD, 16), TEXT_DARK));
        ((JPanel)pnlPay.getComponent(4)).add(lblTienThua, BorderLayout.EAST);
        
        pnlPay.add(Box.createVerticalGlue());

        JButton btnThanhToan = new JButton("THANH TOÁN & IN BILL");
        btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnThanhToan.setBackground(RED_MAIN);
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        btnThanhToan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        btnThanhToan.addActionListener(e -> {
            // 1. Cập nhật trạng thái bàn về TRỐNG
            DAO.BanAnDAO daoBan = new DAO.BanAnDAO();
            boolean okBan = daoBan.capNhatTrangThaiBan(tenBan, "Trống");

            // 2. Đánh dấu hóa đơn đã thanh toán (trangThai = 1)
            boolean okBill = updateBillStatus();

            if(okBan && okBill) {
                JOptionPane.showMessageDialog(this, "✅ Thanh toán thành công cho " + tenBan);
                this.dispose();
            }
        });

        pnlPay.add(btnThanhToan);
        return pnlPay;
    }

    private boolean updateBillStatus() {
        try {
            Connection con = ConnectDB.getInstance().getConnection();
            String sql = "UPDATE ChiTietHoaDon SET trangThai = 1 WHERE tenBan = ? AND trangThai = 0";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, tenBan);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private JPanel createRowItem(String title, String value, Font font, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        panel.add(lblT, BorderLayout.WEST);
        return panel;
    }

    private void tinhTienThua() {
        try {
            String nhapVao = txtKhachDua.getText().replaceAll("[^\\d]", "");
            if (nhapVao.isEmpty()) { lblTienThua.setText("0 đ"); return; }
            double tienKhach = Double.parseDouble(nhapVao);
            double tienThua = tienKhach - tongTienBill;
            
            if (tienThua < 0) {
                lblTienThua.setText("Thiếu: " + formatMoney(Math.abs(tienThua)));
                lblTienThua.setForeground(Color.RED);
            } else {
                lblTienThua.setText(formatMoney(tienThua));
                lblTienThua.setForeground(new Color(34, 197, 94));
            }
        } catch (Exception ex) {
            lblTienThua.setText("Lỗi nhập!");
        }
    }

    private String formatMoney(double amount) {
        return String.format("%,.0f đ", amount).replace(',', '.');
    }
}
package GUI;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class FrmTaoDatCho extends JDialog {

    private static final Color RED_MAIN = new Color(220, 38, 38);
    private static final Color BORDER_CLR = new Color(230, 230, 230);
    private static final Color TEXT_DARK = new Color(40, 40, 40);
    private static final Color TEXT_GRAY = new Color(120, 120, 120);

    // Các biến toàn cục
    private DefaultTableModel tbModelDaChon;
    private JLabel lblTongTien;
    private int tongTien = 0;
    private JComboBox<String> cbBan; // <-- Đã đưa biến này ra ngoài để lấy được tên bàn

    public FrmTaoDatCho(JFrame parent) {
        super(parent, true); // Modal
        setUndecorated(true);
        setSize(950, 700); 
        setLocationRelativeTo(parent);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createBody(), BorderLayout.CENTER);
        root.add(createFooter(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    // --- 1. HEADER ---
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Tạo đặt chỗ mới");
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

    // --- 2. BODY ---
    private JPanel createBody() {
        JPanel body = new JPanel(new GridLayout(1, 2, 40, 0));
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(20, 30, 20, 30));

        // ==========================================
        // CỘT TRÁI: THÔNG TIN KHÁCH HÀNG
        // ==========================================
        JPanel pnlLeft = new JPanel();
        pnlLeft.setLayout(new BoxLayout(pnlLeft, BoxLayout.Y_AXIS));
        pnlLeft.setBackground(Color.WHITE);

        JLabel lblInfoTitle = new JLabel("Thông tin khách hàng");
        lblInfoTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlLeft.add(lblInfoTitle);
        pnlLeft.add(Box.createVerticalStrut(15));

        pnlLeft.add(createInputGroup("Tên khách hàng *", new JTextField()));
        pnlLeft.add(Box.createVerticalStrut(15));
        pnlLeft.add(createInputGroup("Số điện thoại *", new JTextField()));
        pnlLeft.add(Box.createVerticalStrut(15));

        JPanel rowTime = new JPanel(new GridLayout(1, 2, 15, 0));
        rowTime.setBackground(Color.WHITE);
        rowTime.add(createInputGroup("Số lượng khách *", new JTextField("2")));
        rowTime.add(createInputGroup("Thời gian *", new JTextField("--:-- --")));
        rowTime.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        pnlLeft.add(rowTime);
        pnlLeft.add(Box.createVerticalStrut(15));

        // Khởi tạo ComboBox chọn bàn (Sử dụng biến toàn cục)
        // MẸO: Tên bàn ở đây phải khớp chuẩn xác với tên bàn trong SQL của bạn
        cbBan = new JComboBox<>(new String[]{"Chọn bàn", "Bàn 1", "Bàn 2", "Bàn VIP 1"});
        pnlLeft.add(createInputGroup("Chọn bàn *", cbBan));
        pnlLeft.add(Box.createVerticalStrut(15));

        JTextArea txtNote = new JTextArea();
        txtNote.setLineWrap(true);
        txtNote.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
        JScrollPane scrollNote = new JScrollPane(txtNote);
        scrollNote.setPreferredSize(new Dimension(0, 80));
        pnlLeft.add(createInputGroup("Ghi chú", scrollNote));


        // ==========================================
        // CỘT PHẢI: CHIA LÀM 2 NỬA (MENU VÀ GIỎ HÀNG)
        // ==========================================
        JPanel pnlRight = new JPanel(new GridLayout(2, 1, 0, 15));
        pnlRight.setBackground(Color.WHITE);

        // NỬA TRÊN: Danh sách thực đơn
        JPanel pnlMenu = new JPanel(new BorderLayout(0, 5));
        pnlMenu.setBackground(Color.WHITE);
        JLabel lblMenuTitle = new JLabel("Thực đơn (Tùy chọn)");
        lblMenuTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlMenu.add(lblMenuTitle, BorderLayout.NORTH);

        JPanel listFood = new JPanel();
        listFood.setLayout(new BoxLayout(listFood, BoxLayout.Y_AXIS));
        listFood.setBackground(Color.WHITE);
        
        listFood.add(createFoodItem("🥩", "Bò Nướng Sả", 250000, "Món Nướng"));
        listFood.add(Box.createVerticalStrut(10));
        listFood.add(createFoodItem("🍗", "Gà Nướng Mật Ong", 180000, "Món Nướng"));
        listFood.add(Box.createVerticalStrut(10));
        listFood.add(createFoodItem("🍲", "Lẩu Thái Hải Sản", 350000, "Lẩu"));
        listFood.add(Box.createVerticalStrut(10));
        listFood.add(createFoodItem("🥘", "Lẩu Bò Nhúng Giấm", 320000, "Lẩu"));

        JScrollPane scrollFood = new JScrollPane(listFood);
        scrollFood.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
        pnlMenu.add(scrollFood, BorderLayout.CENTER);

        // NỬA DƯỚI: Bảng các món đã chọn (Giỏ hàng)
        JPanel pnlCart = new JPanel(new BorderLayout(0, 5));
        pnlCart.setBackground(Color.WHITE);
        
        JLabel lblCartTitle = new JLabel("Món đã chọn");
        lblCartTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlCart.add(lblCartTitle, BorderLayout.NORTH);

        String[] columns = {"Tên món", "SL", "Đơn giá"};
        tbModelDaChon = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        JTable tbDaChon = new JTable(tbModelDaChon);
        tbDaChon.setRowHeight(25);
        tbDaChon.getColumnModel().getColumn(1).setPreferredWidth(30); 
        JScrollPane scrollCart = new JScrollPane(tbDaChon);
        scrollCart.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
        pnlCart.add(scrollCart, BorderLayout.CENTER);

        lblTongTien = new JLabel("Tổng cộng: 0 đ");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongTien.setForeground(RED_MAIN);
        lblTongTien.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlCart.add(lblTongTien, BorderLayout.SOUTH);

        pnlRight.add(pnlMenu);
        pnlRight.add(pnlCart);

        body.add(pnlLeft);
        body.add(pnlRight);
        return body;
    }

    // --- 3. FOOTER ---
    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR));

        JButton btnHuy = new JButton("Hủy");
        btnHuy.setPreferredSize(new Dimension(150, 40));
        btnHuy.setBackground(Color.WHITE);
        btnHuy.setFocusPainted(false);
        btnHuy.addActionListener(e -> this.dispose());

        JButton btnXacNhan = new JButton("Xác nhận đặt chỗ");
        btnXacNhan.setPreferredSize(new Dimension(200, 40));
        btnXacNhan.setBackground(RED_MAIN);
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnXacNhan.setFocusPainted(false);
        btnXacNhan.setBorderPainted(false);
        
        // --- XỬ LÝ LƯU DATABASE & ĐỔI MÀU BÀN ---
        btnXacNhan.addActionListener(e -> {
            String tenBan = cbBan.getSelectedItem().toString();
            
            if (tenBan.equals("Chọn bàn")) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn để đặt!", "Lưu ý", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 1. Sau này bạn sẽ code phần lưu Khách Hàng, Hóa Đơn ở đây
            // System.out.println("Lưu các món: " + tbModelDaChon.getRowCount());

            // 2. Gọi DAO để đổi trạng thái bàn trong SQL Server
            DAO.BanAnDAO dao = new DAO.BanAnDAO();
            boolean thanhCong = dao.capNhatTrangThaiBan(tenBan, "Đã đặt");

            if (thanhCong) {
                JOptionPane.showMessageDialog(this, "✅ Đặt chỗ thành công cho " + tenBan + "!");
                
                // 3. Tự động F5 (Refresh) lại màn hình Lễ Tân bên dưới
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                if (parentFrame instanceof FrmLeTan) {
                    ((FrmLeTan) parentFrame).refreshSoDoBan();
                }
                
                // 4. Đóng popup
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Có lỗi xảy ra, không thể cập nhật trạng thái bàn!", "Lỗi DB", JOptionPane.ERROR_MESSAGE);
            }
        });

        footer.add(btnHuy);
        footer.add(btnXacNhan);
        return footer;
    }

    // --- HÀM HỖ TRỢ TẠO GIAO DIỆN ---
    private JPanel createInputGroup(String title, JComponent input) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        if(input instanceof JTextField) {
            ((JTextField) input).setPreferredSize(new Dimension(0, 35));
            input.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR),
                new EmptyBorder(0, 10, 0, 10)
            ));
        } else if (input instanceof JComboBox) {
            input.setPreferredSize(new Dimension(0, 35));
        }
        
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(input, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFoodItem(String emoji, String name, int price, String category) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setBackground(Color.WHITE);
        item.setBorder(new EmptyBorder(10, 10, 10, 10));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel lblImg = new JLabel(emoji, SwingConstants.CENTER);
        lblImg.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        lblImg.setPreferredSize(new Dimension(60, 60));
        lblImg.setBorder(BorderFactory.createLineBorder(BORDER_CLR));

        JPanel info = new JPanel(new GridLayout(3, 1));
        info.setBackground(Color.WHITE);
        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JLabel lblPrice = new JLabel(formatMoney(price));
        lblPrice.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPrice.setForeground(RED_MAIN);
        
        JLabel lblCat = new JLabel(category);
        lblCat.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCat.setForeground(TEXT_GRAY);
        info.add(lblName); info.add(lblPrice); info.add(lblCat);

        JButton btnAdd = new JButton("Thêm");
        btnAdd.setBackground(RED_MAIN);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setBorderPainted(false);
        
        btnAdd.addActionListener(e -> {
            boolean isExist = false;
            for (int i = 0; i < tbModelDaChon.getRowCount(); i++) {
                if (tbModelDaChon.getValueAt(i, 0).equals(name)) {
                    int slCu = (int) tbModelDaChon.getValueAt(i, 1);
                    tbModelDaChon.setValueAt(slCu + 1, i, 1);
                    isExist = true;
                    break;
                }
            }
            
            if (!isExist) {
                tbModelDaChon.addRow(new Object[]{name, 1, formatMoney(price)});
            }
            
            tongTien += price;
            lblTongTien.setText("Tổng cộng: " + formatMoney(tongTien));
        });

        item.add(lblImg, BorderLayout.WEST);
        item.add(info, BorderLayout.CENTER);
        item.add(btnAdd, BorderLayout.EAST);

        return item;
    }
    
    private String formatMoney(int amount) {
        return String.format("%,d đ", amount).replace(',', '.');
    }
}
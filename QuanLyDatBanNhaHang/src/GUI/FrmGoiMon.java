package GUI;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class FrmGoiMon extends JDialog {

    private static final Color RED_MAIN = new Color(220, 38, 38);
    private static final Color BG_MAIN = new Color(248, 248, 248);
    private static final Color BORDER_CLR = new Color(230, 230, 230);
    private static final Color TEXT_DARK = new Color(40, 40, 40);
    private static final Color TEXT_GRAY = new Color(120, 120, 120);

    private String tenBan;
    private int sucChua;
    
    // Biến cho giỏ hàng
    private DefaultTableModel cartModel;
    private JLabel lblTotal;
    private double totalAmount = 0; // Đổi sang kiểu double theo Entity
    private JButton btnGuiBep;

    public FrmGoiMon(JFrame parent, String tenBan, int sucChua) {
        super(parent, true);
        this.tenBan = tenBan;
        this.sucChua = sucChua;
        
        setSize(1200, 750);
        setLocationRelativeTo(parent);
        setUndecorated(true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));

        root.add(createTopBar(), BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(createMenuArea(), BorderLayout.CENTER);
        centerPanel.add(createCartArea(), BorderLayout.EAST);
        
        root.add(centerPanel, BorderLayout.CENTER);

        setContentPane(root);
    }

    // --- 1. TOP BAR ---
    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
                new EmptyBorder(10, 20, 10, 20)
        ));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        left.setOpaque(false);

        JButton btnBack = new JButton("←");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 22));
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> this.dispose());

        JPanel textWrap = new JPanel(new GridLayout(2, 1));
        textWrap.setOpaque(false);
        JLabel lblTitle = new JLabel("Gọi món - " + tenBan);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JLabel lblSub = new JLabel("Sức chứa: " + sucChua + " người");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(TEXT_GRAY);
        
        textWrap.add(lblTitle);
        textWrap.add(lblSub);

        left.add(btnBack);
        left.add(textWrap);

        bar.add(left, BorderLayout.WEST);
        return bar;
    }

    // --- 2. KHU VỰC CHỌN MÓN ---
    private JPanel createMenuArea() {
        JPanel menuArea = new JPanel(new BorderLayout());
        menuArea.setBackground(BG_MAIN);

        JPanel categoryTabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        categoryTabs.setBackground(BG_MAIN);
        categoryTabs.setBorder(new EmptyBorder(0, 15, 0, 15));
        
        String[] categories = {"Tất cả", "Món Nướng", "Lẩu", "Đồ Uống"};
        for (int i = 0; i < categories.length; i++) {
            JButton btnCat = new JButton(categories[i]);
            btnCat.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btnCat.setFocusPainted(false);
            btnCat.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnCat.setPreferredSize(new Dimension(100, 35));
            
            if (i == 0) {
                btnCat.setBackground(RED_MAIN);
                btnCat.setForeground(Color.WHITE);
                btnCat.setBorderPainted(false);
            } else {
                btnCat.setBackground(Color.WHITE);
                btnCat.setForeground(TEXT_DARK);
                btnCat.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
            }
            categoryTabs.add(btnCat);
        }

        JPanel gridFood = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        gridFood.setBackground(BG_MAIN);
        gridFood.setPreferredSize(new Dimension(800, 2000)); 

        // --- KẾT NỐI DB ĐỂ LẤY MÓN ĂN THỰC TẾ ---
        DAO.MonAnDAO monAnDAO = new DAO.MonAnDAO();
        List<Entity.MonAn> danhSachMon = monAnDAO.getAllMonAn();
        
        for (Entity.MonAn mon : danhSachMon) {
            String icon = "🍽️"; 
            String ten = mon.getTenMon().toLowerCase();
            
            if (ten.contains("bò") || ten.contains("heo") || ten.contains("nướng")) icon = "🥩";
            else if (ten.contains("gà") || ten.contains("vịt")) icon = "🍗";
            else if (ten.contains("lẩu") || ten.contains("canh")) icon = "🍲";
            else if (ten.contains("bia") || ten.contains("nước") || ten.contains("trà")) icon = "🥤";
            else if (ten.contains("salad") || ten.contains("rau")) icon = "🥗";

            // Truyền giá tiền kiểu double
            gridFood.add(createFoodCard(icon, mon.getTenMon(), mon.getGiaMon()));
        }

        JScrollPane scroll = new JScrollPane(gridFood);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_MAIN);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        menuArea.add(categoryTabs, BorderLayout.NORTH);
        menuArea.add(scroll, BorderLayout.CENTER);
        
        return menuArea;
    }

    // --- HÀM TẠO CARD MÓN ĂN (Đã sửa tham số price thành double) ---
    private JPanel createFoodCard(String emoji, String name, double price) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(180, 240));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JPanel imgBox = new JPanel(new BorderLayout());
        imgBox.setBackground(new Color(240, 240, 240));
        imgBox.setPreferredSize(new Dimension(160, 120));
        imgBox.setMaximumSize(new Dimension(160, 120));
        JLabel lblIcon = new JLabel(emoji, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
        imgBox.add(lblIcon, BorderLayout.CENTER);

        JLabel lblName = new JLabel(name, SwingConstants.CENTER);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPrice = new JLabel(formatMoney(price), SwingConstants.CENTER);
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPrice.setForeground(RED_MAIN);
        lblPrice.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnAdd = new JButton("+ Thêm");
        btnAdd.setBackground(RED_MAIN);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.setFocusPainted(false);
        btnAdd.setBorderPainted(false);
        btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAdd.setMaximumSize(new Dimension(150, 35));
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnAdd.addActionListener(e -> addToCart(name, price));

        card.add(imgBox);
        card.add(Box.createVerticalStrut(10));
        card.add(lblName);
        card.add(Box.createVerticalStrut(5));
        card.add(lblPrice);
        card.add(Box.createVerticalStrut(10));
        card.add(btnAdd);

        return card;
    }

    // --- 3. KHU VỰC HÓA ĐƠN ---
    private JPanel createCartArea() {
        JPanel cartArea = new JPanel(new BorderLayout());
        cartArea.setBackground(Color.WHITE);
        cartArea.setPreferredSize(new Dimension(350, 0));
        cartArea.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER_CLR));

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(20, 20, 10, 20));
        JLabel title = new JLabel("Đơn hàng hiện tại");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel sub = new JLabel(tenBan);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXT_GRAY);
        header.add(title);
        header.add(sub);

        String[] cols = {"Món", "SL", "Thành tiền"};
        cartModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(cartModel);
        table.setRowHeight(30);
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(40);
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, BORDER_CLR));
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel footer = new JPanel(new BorderLayout(0, 15));
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel pnlTotal = new JPanel(new BorderLayout());
        pnlTotal.setBackground(Color.WHITE);
        JLabel lblTotalText = new JLabel("Tạm tính:");
        lblTotalText.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblTotal = new JLabel("0 đ");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setForeground(RED_MAIN);
        pnlTotal.add(lblTotalText, BorderLayout.WEST);
        pnlTotal.add(lblTotal, BorderLayout.EAST);

        btnGuiBep = new JButton("Gửi Bếp");
        btnGuiBep.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnGuiBep.setPreferredSize(new Dimension(0, 45));
        btnGuiBep.setBackground(new Color(200, 200, 200)); 
        btnGuiBep.setForeground(Color.WHITE);
        btnGuiBep.setFocusPainted(false);
        btnGuiBep.setBorderPainted(false);
        btnGuiBep.setEnabled(false); 
        
        btnGuiBep.addActionListener(e -> {
            DAO.BanAnDAO dao = new DAO.BanAnDAO();
            boolean thanhCong = dao.capNhatTrangThaiBan(tenBan, "Có khách");
            
            if(thanhCong) {
                JOptionPane.showMessageDialog(this, "✅ Đã gửi đơn hàng xuống bếp thành công!");
                this.dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật bàn!");
            }
        });

        footer.add(pnlTotal, BorderLayout.NORTH);
        footer.add(btnGuiBep, BorderLayout.SOUTH);

        cartArea.add(header, BorderLayout.NORTH);
        cartArea.add(scroll, BorderLayout.CENTER);
        cartArea.add(footer, BorderLayout.SOUTH);

        return cartArea;
    }

    // --- HÀM XỬ LÝ LOGIC GIỎ HÀNG ---
    private void addToCart(String name, double price) {
        boolean exists = false;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            if (cartModel.getValueAt(i, 0).equals(name)) {
                int sl = (int) cartModel.getValueAt(i, 1) + 1;
                cartModel.setValueAt(sl, i, 1);
                cartModel.setValueAt(formatMoney(sl * price), i, 2);
                exists = true;
                break;
            }
        }
        
        if (!exists) {
            cartModel.addRow(new Object[]{name, 1, formatMoney(price)});
        }
        
        totalAmount += price;
        lblTotal.setText(formatMoney(totalAmount));
        
        btnGuiBep.setBackground(RED_MAIN);
        btnGuiBep.setEnabled(true);
        btnGuiBep.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Cập nhật hàm format để hỗ trợ số thập phân (double)
    private String formatMoney(double amount) {
        // Định dạng số không có phần thập phân dư thừa
        return String.format("%,.0f đ", amount).replace(',', '.');
    }
}
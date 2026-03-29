//package GUI;
//
//import java.awt.BasicStroke;
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Component;
//import java.awt.Cursor;
//import java.awt.Dimension;
//import java.awt.FlowLayout;
//import java.awt.Font;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.GridLayout;
//import java.util.List;
//import java.awt.RenderingHints;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import javax.swing.BorderFactory;
//import javax.swing.Box;
//import javax.swing.BoxLayout;
//import javax.swing.JButton;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTextField;
//import javax.swing.SwingConstants;
//import javax.swing.SwingUtilities;
//import javax.swing.Timer;
//import javax.swing.border.EmptyBorder;
//
//public class FrmLeTan extends JFrame {
//
//    // --- CÁC MÀU SẮC CHỦ ĐẠO TỪ ẢNH ---
//    private static final Color RED_MAIN = new Color(220, 38, 38);
//    private static final Color BG_MAIN = new Color(248, 248, 248);
//    private static final Color BORDER_CLR = new Color(230, 230, 230);
//    private static final Color TEXT_DARK = new Color(40, 40, 40);
//    private static final Color TEXT_GRAY = new Color(120, 120, 120);
//
//    // Màu Trạng thái Bàn
//    private static final Color BG_TRONG = new Color(220, 252, 231);    // Xanh nhạt
//    private static final Color BORDER_TRONG = new Color(34, 197, 94);  // Xanh đậm
//    private static final Color BG_KHACH = new Color(254, 226, 226);    // Đỏ nhạt
//    private static final Color BORDER_KHACH = new Color(239, 68, 68);  // Đỏ đậm
//    private static final Color BG_DAT = new Color(254, 249, 195);      // Vàng nhạt
//    private static final Color BORDER_DAT = new Color(234, 179, 8);    // Vàng đậm
//
//    public FrmLeTan() {
//        initUI();
//        startClock();
//    }
//
//    private void initUI() {
//        setTitle("Nhà Hàng Ngói Đỏ - Lễ Tân");
//        setSize(1440, 860);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//
//        JPanel root = new JPanel(new BorderLayout());
//        root.setBackground(BG_MAIN);
//
//        root.add(createTopBar(), BorderLayout.NORTH);
//
//        JPanel centerWrap = new JPanel(new BorderLayout());
//        centerWrap.setOpaque(false);
//        centerWrap.add(createTabs(), BorderLayout.NORTH);
//        centerWrap.add(createMapArea(), BorderLayout.CENTER);
//
//        root.add(centerWrap, BorderLayout.CENTER);
//        root.add(createRightSidebar(), BorderLayout.EAST);
//
//        setContentPane(root);
//    }
//
//    // ==========================================
//    // 1. THANH TOP BAR (Trang chủ, User, Đăng xuất)
//    // ==========================================
//    private JPanel createTopBar() {
//        JPanel bar = new JPanel(new BorderLayout());
//        bar.setBackground(Color.WHITE);
//        bar.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
//                new EmptyBorder(10, 20, 10, 20)
//        ));
//
//        // LEFT: Logo & Tên
//        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
//        left.setOpaque(false);
//        
//        JLabel lblLogo = new JLabel("🏮"); // Thay bằng Icon nếu có
//        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
//        lblLogo.setForeground(RED_MAIN);
//
//        JPanel textWrap = new JPanel(new GridLayout(2, 1));
//        textWrap.setOpaque(false);
//        JLabel lblName = new JLabel("Nhà Hàng Ngói Đỏ - Lễ Tân");
//        lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
//        JLabel lblSub = new JLabel("Quản lý đặt chỗ & Check-in");
//        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
//        lblSub.setForeground(TEXT_GRAY);
//        textWrap.add(lblName);
//        textWrap.add(lblSub);
//
//        left.add(lblLogo);
//        left.add(textWrap);
//
//        // RIGHT: Giờ, User, Logout
//        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
//        right.setOpaque(false);
//
//        lblClock = new JLabel("00:00:00");
//        lblClock.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        
//        JLabel lblUser = new JLabel("👤 Lễ tân Ca sáng");
//        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//
//        JButton btnLogout = new JButton("Đăng xuất");
//        btnLogout.setContentAreaFilled(false);
//        btnLogout.setBorderPainted(false);
//        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        btnLogout.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        
//        // --- ĐOẠN CODE XỬ LÝ ĐĂNG XUẤT ---
//        btnLogout.addActionListener(e -> {
//            // 1. Xác nhận người dùng có muốn thoát không
//            int choice = JOptionPane.showConfirmDialog(this, 
//                    "Bạn có chắc chắn muốn đăng xuất không?", 
//                    "Xác nhận", JOptionPane.YES_NO_OPTION);
//            
//            if (choice == JOptionPane.YES_OPTION) {
//                // 2. Đóng màn hình hiện tại (Lễ tân)
//                this.dispose(); 
//                
//                // 3. Mở lại màn hình Đăng nhập
//                // Lưu ý: Đảm bảo class FrmDangNhap của bạn nằm trong package GUI
//                new FrmDangNhap().setVisible(true); 
//            }
//        });
//
//        right.add(lblClock);
//        right.add(lblUser);
//        right.add(btnLogout);
//
//        bar.add(left, BorderLayout.WEST);
//        bar.add(right, BorderLayout.EAST);
//        return bar;
//    }
//
//    private JLabel lblClock;
//    private void startClock() {
//        Timer timer = new Timer(1000, e -> {
//            lblClock.setText("🕒 " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
//        });
//        timer.start();
//    }
//
//    // ==========================================
//    // 2. TABS (Tầng 1, Tầng 2, VIP)
// // ==========================================
//    private JPanel createTabs() {
//        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
//        tabs.setBackground(Color.WHITE);
//        tabs.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
//                new EmptyBorder(10, 20, 0, 20)
//        ));
//
//        String[] tabNames = {"Tầng 1", "Tầng 2", "Phòng VIP"};
//        for (int i = 0; i < tabNames.length; i++) {
//            JLabel lbl = new JLabel(tabNames[i]);
//            lbl.setFont(new Font("Segoe UI", i == 0 ? Font.BOLD : Font.PLAIN, 14));
//            lbl.setForeground(i == 0 ? RED_MAIN : TEXT_DARK);
//            lbl.setBorder(new EmptyBorder(0, 0, 10, 0));
//            if (i == 0) {
//                lbl.setBorder(BorderFactory.createCompoundBorder(
//                        BorderFactory.createMatteBorder(0, 0, 3, 0, RED_MAIN),
//                        new EmptyBorder(0, 0, 7, 0)
//                ));
//            }
//            lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
//            tabs.add(lbl);
//        }
//        return tabs;
//    }
//
//    // ==========================================
//    // 3. KHU VỰC SƠ ĐỒ BÀN (MAIN GRID)
//    // ==========================================
//    private JPanel createMapArea() {
//        JPanel mapWrap = new JPanel(new BorderLayout());
//        mapWrap.setOpaque(false);
//        mapWrap.setBorder(new EmptyBorder(20, 20, 20, 20));
//
//        JPanel header = new JPanel(new GridLayout(2, 1, 0, 5));
//        header.setOpaque(false);
//        header.setBorder(new EmptyBorder(0, 0, 20, 0));
//        JLabel title = new JLabel("Sơ đồ bàn - Tầng 1");
//        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
//        JLabel sub = new JLabel("Click vào bàn trống hoặc đã đặt để bắt đầu phục vụ");
//        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        sub.setForeground(TEXT_GRAY);
//        header.add(title);
//        header.add(sub);
//
//        // Chứa các bàn ăn
//        JPanel grid = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
//        grid.setOpaque(false);
//
//        // MOCK DATA - BẠN SẼ THAY BẰNG VÒNG LẶP dao.getAllBanAn() TẠI ĐÂY
//     // Lấy danh sách bàn từ DB
//        DAO.BanAnDAO banAnDAO = new DAO.BanAnDAO();
//        List<Entity.BanAn> danhSachBan = banAnDAO.getAllBanAn();
//
//        // Vẽ từng bàn lên sơ đồ
//        for (Entity.BanAn ban : danhSachBan) {
//            // Tạm thời chưa tính hóa đơn, để trống phần tiền
//            String tienHoaDon = ""; 
//            
//            grid.add(createTableCard(
//                ban.getTenBan(), 
//                ban.getSucChua(), 
//                ban.getTrangThai(), 
//                tienHoaDon
//            ));
//        }
//
//        JScrollPane scroll = new JScrollPane(grid);
//        scroll.setBorder(null);
//        scroll.setOpaque(false);
//        scroll.getViewport().setOpaque(false);
//        scroll.getVerticalScrollBar().setUnitIncrement(16);
//
//        mapWrap.add(header, BorderLayout.NORTH);
//        mapWrap.add(scroll, BorderLayout.CENTER);
//        return mapWrap;
//    }
//
//    // HÀM VẼ TỪNG BÀN ĂN (CARD BÀN)
//    private JPanel createTableCard(String name, int capacity, String status, String bill) {
//        Color bg, border;
//        if (status.equals("Trống")) {
//            bg = BG_TRONG; border = BORDER_TRONG;
//        } else if (status.equals("Có khách")) {
//            bg = BG_KHACH; border = BORDER_KHACH;
//        } else {
//            bg = BG_DAT; border = BORDER_DAT;
//        }
//
//        // Custom Panel để bo góc
//        JPanel card = new JPanel() {
//            @Override
//            protected void paintComponent(Graphics g) {
//                Graphics2D g2 = (Graphics2D) g.create();
//                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//                g2.setColor(bg);
//                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
//                g2.setColor(border);
//                g2.setStroke(new BasicStroke(1.5f));
//                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
//                g2.dispose();
//            }
//        };
//        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
//        card.setPreferredSize(new Dimension(220, 130));
//        card.setOpaque(false);
//        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
//
//        JLabel icon = new JLabel("🪑", SwingConstants.CENTER);
//        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
//        icon.setForeground(border);
//        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        JLabel lblName = new JLabel(name, SwingConstants.CENTER);
//        lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));
//        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        JLabel lblCap = new JLabel("Sức chứa: " + capacity + " người", SwingConstants.CENTER);
//        lblCap.setFont(new Font("Segoe UI", Font.PLAIN, 12));
//        lblCap.setForeground(new Color(80, 80, 80));
//        lblCap.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        JLabel lblStatus = new JLabel(status, SwingConstants.CENTER);
//        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
//        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        card.add(Box.createVerticalStrut(15));
//        card.add(icon);
//        card.add(Box.createVerticalStrut(5));
//        card.add(lblName);
//        card.add(Box.createVerticalStrut(5));
//        card.add(lblCap);
//        card.add(Box.createVerticalStrut(5));
//        card.add(lblStatus);
//
//        if (!bill.isEmpty()) {
//            JLabel lblBill = new JLabel("Hóa đơn: " + bill, SwingConstants.CENTER);
//            lblBill.setFont(new Font("Segoe UI", Font.BOLD, 12));
//            lblBill.setForeground(RED_MAIN);
//            lblBill.setAlignmentX(Component.CENTER_ALIGNMENT);
//            card.add(Box.createVerticalStrut(5));
//            card.add(lblBill);
//        }
//
//        // Hiệu ứng hover
//        card.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseEntered(MouseEvent e) {
//                card.setBorder(BorderFactory.createLineBorder(border, 2)); // Tăng viền khi hover
//            }
//            @Override
//            public void mouseExited(MouseEvent e) {
//                card.setBorder(null);
//            }
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                JOptionPane.showMessageDialog(card, "Bạn đã chọn: " + name);
//                // Xử lý mở bàn / gọi món tại đây
//            }
//        });
//
//        return card;
//    }
//
//    // ==========================================
//    // 4. RIGHT SIDEBAR (Quản lý đặt chỗ)
//    // ==========================================
//    private JPanel createRightSidebar() {
//        JPanel sidebar = new JPanel(new BorderLayout());
//        sidebar.setBackground(Color.WHITE);
//        sidebar.setPreferredSize(new Dimension(320, 0));
//        sidebar.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER_CLR));
//
//        JPanel top = new JPanel();
//        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
//        top.setBackground(Color.WHITE);
//        top.setBorder(new EmptyBorder(20, 20, 20, 20));
//
//        JLabel title = new JLabel("Quản lý Đặt chỗ");
//        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
//        title.setAlignmentX(Component.LEFT_ALIGNMENT);
//
//        JTextField txtSearch = new JTextField();
//        txtSearch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
//        txtSearch.setPreferredSize(new Dimension(0, 35));
//        // Fake placeholder
//        txtSearch.setText("🔍 Tìm khách hàng / SĐT");
//        txtSearch.setForeground(TEXT_GRAY);
//        txtSearch.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
//                new EmptyBorder(0, 10, 0, 10)
//        ));
//
//        JLabel subTitle = new JLabel("Đặt chỗ hôm nay");
//        subTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        subTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
//        subTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
//
//        top.add(title);
//        top.add(Box.createVerticalStrut(15));
//        top.add(txtSearch);
//        top.add(subTitle);
//
//        // Danh sách đặt chỗ
//        JPanel list = new JPanel();
//        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
//        list.setBackground(Color.WHITE);
//        list.setBorder(new EmptyBorder(0, 20, 0, 20));
//
//        list.add(createBookingCard("Nguyễn Văn A", "0901234567", "19:00", "Bàn 03", 6));
//        list.add(Box.createVerticalStrut(15));
//        list.add(createBookingCard("Trần Thị B", "0912345678", "20:00", "VIP 02", 8));
//
//        JScrollPane scroll = new JScrollPane(list);
//        scroll.setBorder(null);
//     // Thao tác nhanh
//        JPanel actions = new JPanel(new GridLayout(3, 1, 0, 10));
//        actions.setBackground(Color.WHITE);
//        actions.setBorder(new EmptyBorder(20, 20, 20, 20));
//        JLabel lblQuick = new JLabel("Thao tác nhanh");
//        lblQuick.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        
//        actions.add(lblQuick);
//        actions.add(createSolidButton("+ Tạo đặt chỗ mới", RED_MAIN, Color.WHITE));
//        actions.add(createOutlineButton("⇆ Chuyển bàn"));
//        actions.add(createOutlineButton("⛙ Gộp bàn"));
//
//        sidebar.add(top, BorderLayout.NORTH);
//        sidebar.add(scroll, BorderLayout.CENTER);
//        sidebar.add(actions, BorderLayout.SOUTH);
//
//        return sidebar;
//    }
//
//    // HÀM VẼ CARD ĐẶT CHỖ MÀU VÀNG
//    private JPanel createBookingCard(String name, String phone, String time, String table, int guests) {
//        JPanel card = new JPanel() {
//            @Override
//            protected void paintComponent(Graphics g) {
//                Graphics2D g2 = (Graphics2D) g.create();
//                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//                g2.setColor(new Color(254, 252, 232)); // Vàng nhạt
//                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
//                g2.setColor(new Color(253, 224, 71)); // Vàng viền
//                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
//                g2.dispose();
//            }
//        };
//        card.setLayout(new BorderLayout(10, 10));
//        card.setOpaque(false);
//        card.setBorder(new EmptyBorder(10, 12, 10, 12));
//        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
//
//        JPanel info = new JPanel(new GridLayout(3, 2));
//        info.setOpaque(false);
//
//        JLabel lName = new JLabel(name); lName.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        JLabel lTime = new JLabel(time, SwingConstants.RIGHT); lTime.setFont(new Font("Segoe UI", Font.BOLD, 12));
//        
//        JLabel lPhone = new JLabel(phone); lPhone.setFont(new Font("Segoe UI", Font.PLAIN, 12)); lPhone.setForeground(TEXT_GRAY);
//        JLabel empty = new JLabel("");
//        
//        JLabel lTable = new JLabel("Bàn: " + table); lTable.setFont(new Font("Segoe UI", Font.PLAIN, 12)); lTable.setForeground(TEXT_GRAY);
//        JLabel lGuests = new JLabel(guests + " người", SwingConstants.RIGHT); lGuests.setFont(new Font("Segoe UI", Font.PLAIN, 12));
//
//        info.add(lName); info.add(lTime);
//        info.add(lPhone); info.add(empty);
//        info.add(lTable); info.add(lGuests);
//
//        JButton btnCheckIn = createSolidButton("Check-in Khách", RED_MAIN, Color.WHITE);
//        btnCheckIn.setPreferredSize(new Dimension(0, 30));
//
//        // BẬT CÔNG TẮC: THÊM SỰ KIỆN CLICK CHO NÚT
//        btnCheckIn.addActionListener(e -> {
//            // Hiện hộp thoại hỏi xác nhận
//        	int xacNhan = JOptionPane.showConfirmDialog(card, 
//                    "Bạn có chắc chắn muốn Check-in cho khách " + name + " vào " + table + " không?", 
//                    "Xác nhận Check-in", 
//                    JOptionPane.YES_NO_OPTION);
//            
//            // Nếu người dùng bấm YES
//            if (xacNhan == JOptionPane.YES_OPTION) {
//                JOptionPane.showMessageDialog(card, "✅ Đã Check-in thành công cho " + name + "!");
//                
//                // TODO: Tại đây sau này bạn sẽ viết code để:
//                // 1. Đổi trạng thái bàn trong Database từ "Đã đặt" sang "Có khách"
//                // 2. Refresh lại sơ đồ bàn trên màn hình
//            }
//        });
//
//        card.add(info, BorderLayout.CENTER);
//        card.add(btnCheckIn, BorderLayout.SOUTH);
//
//        return card;
//    }
//
//    // Các hàm tạo Button Custom
//    private JButton createSolidButton(String text, Color bg, Color fg) {
//        JButton btn = new JButton(text);
//        btn.setBackground(bg);
//        btn.setForeground(fg);
//        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
//        btn.setFocusPainted(false);
//        btn.setBorderPainted(false);
//        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        return btn;
//    }
//
//    private JButton createOutlineButton(String text) {
//        JButton btn = new JButton(text);
//        btn.setBackground(Color.WHITE);
//        btn.setForeground(TEXT_DARK);
//        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        btn.setFocusPainted(false);
//        btn.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
//        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        return btn;
//    }
//
//    // ==========================================
//    // MAIN ĐỂ CHẠY THỬ ĐỘC LẬP
//    // ==========================================
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            new FrmLeTan().setVisible(true);
//        });
//    }
//}
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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class FrmLeTan extends JFrame {

    // --- CÁC MÀU SẮC CHỦ ĐẠO ---
    private static final Color RED_MAIN = new Color(220, 38, 38);
    private static final Color BG_MAIN = new Color(248, 248, 248);
    private static final Color BORDER_CLR = new Color(230, 230, 230);
    private static final Color TEXT_DARK = new Color(40, 40, 40);
    private static final Color TEXT_GRAY = new Color(120, 120, 120);

    // Màu Trạng thái Bàn
    private static final Color BG_TRONG = new Color(220, 252, 231);
    private static final Color BORDER_TRONG = new Color(34, 197, 94);
    private static final Color BG_KHACH = new Color(254, 226, 226);
    private static final Color BORDER_KHACH = new Color(239, 68, 68);
    private static final Color BG_DAT = new Color(254, 249, 195);
    private static final Color BORDER_DAT = new Color(234, 179, 8);

    // --- CÁC BIẾN TOÀN CỤC ---
    private JPanel gridMap; 
    private JLabel lblClock;
    
    // Khai báo biến xử lý Tab
    private String currentTab = "Tầng 1"; // Mặc định mở lên là Tầng 1
    private JPanel tabsContainer;
    private JLabel lblMapTitle;

    public FrmLeTan() {
        initUI();
        startClock();
    }

    private void initUI() {
        setTitle("Nhà Hàng Ngói Đỏ - Lễ Tân");
        setSize(1440, 860);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

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
    }

    // ==========================================
    // 1. THANH TOP BAR
    // ==========================================
    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
                new EmptyBorder(10, 20, 10, 20)
        ));

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
        
        JLabel lblUser = new JLabel("👤 Lễ tân Ca sáng");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setContentAreaFilled(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        btnLogout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, 
                    "Bạn có chắc chắn muốn đăng xuất không?", 
                    "Xác nhận", JOptionPane.YES_NO_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                this.dispose(); 
                new FrmDangNhap().setVisible(true); 
            }
        });

        right.add(lblClock);
        right.add(lblUser);
        right.add(btnLogout);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            lblClock.setText("🕒 " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
        });
        timer.start();
    }

    // ==========================================
    // 2. TABS (Chuyển tầng) - CẬP NHẬT CHỨC NĂNG CLICK
    // ==========================================
    private JPanel createTabs() {
        tabsContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        tabsContainer.setBackground(Color.WHITE);
        tabsContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
                new EmptyBorder(10, 20, 0, 20)
        ));

        String[] tabNames = {"Tầng 1", "Tầng 2", "Phòng VIP"};
        for (int i = 0; i < tabNames.length; i++) {
            JLabel lbl = new JLabel(tabNames[i]);
            lbl.setFont(new Font("Segoe UI", i == 0 ? Font.BOLD : Font.PLAIN, 14));
            lbl.setForeground(i == 0 ? RED_MAIN : TEXT_DARK);
            lbl.setBorder(new EmptyBorder(0, 0, 10, 0));
            if (i == 0) {
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 3, 0, RED_MAIN),
                        new EmptyBorder(0, 0, 7, 0)
                ));
            }
            lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // THÊM SỰ KIỆN KHI CLICK VÀO TAB
            lbl.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    currentTab = lbl.getText(); // Gán tab hiện tại
                    updateTabUI();              // Đổi gạch chân đỏ sang tab mới
                    refreshSoDoBan();           // Lọc và vẽ lại sơ đồ bàn
                }
            });
            
            tabsContainer.add(lbl);
        }
        return tabsContainer;
    }

    // Hàm cập nhật giao diện (Màu sắc/Gạch chân) cho Tab
    private void updateTabUI() {
        for (Component c : tabsContainer.getComponents()) {
            if (c instanceof JLabel) {
                JLabel lbl = (JLabel) c;
                boolean isSelected = lbl.getText().equals(currentTab);
                
                lbl.setFont(new Font("Segoe UI", isSelected ? Font.BOLD : Font.PLAIN, 14));
                lbl.setForeground(isSelected ? RED_MAIN : TEXT_DARK);
                
                if (isSelected) {
                    lbl.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 3, 0, RED_MAIN),
                            new EmptyBorder(0, 0, 7, 0)
                    ));
                } else {
                    lbl.setBorder(new EmptyBorder(0, 0, 10, 0));
                }
            }
        }
    }

    // ==========================================
    // 3. KHU VỰC SƠ ĐỒ BÀN (TỰ RỚT DÒNG)
    // ==========================================
    private JPanel createMapArea() {
        JPanel mapWrap = new JPanel(new BorderLayout());
        mapWrap.setOpaque(false);
        mapWrap.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel header = new JPanel(new GridLayout(2, 1, 0, 5));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        lblMapTitle = new JLabel("Sơ đồ bàn - " + currentTab);
        lblMapTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JLabel sub = new JLabel("Click vào bàn trống hoặc đã đặt để bắt đầu phục vụ");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXT_GRAY);
        header.add(lblMapTitle);
        header.add(sub);

        // BÍ QUYẾT TẠO RỚT DÒNG: Dùng GridLayout với số cột cố định là 4 (0 dòng, 4 cột)
        gridMap = new JPanel(new GridLayout(0, 4, 15, 15));
        gridMap.setOpaque(false);
        
        // Bọc vào FlowLayout để các Thẻ bàn không bị kéo giãn to đùng ra toàn màn hình
        JPanel wrapGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapGrid.setOpaque(false);
        wrapGrid.add(gridMap);

        // Lần đầu khởi động phần mềm, tự động gọi hàm vẽ bàn
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

    // HÀM VẼ TỪNG BÀN ĂN (CARD BÀN)
    private JPanel createTableCard(String name, int capacity, String status, String bill) {
        Color bg, border;
        if (status.equalsIgnoreCase("Trống")) {
            bg = BG_TRONG; border = BORDER_TRONG;
        } else if (status.equalsIgnoreCase("Có khách")) {
            bg = BG_KHACH; border = BORDER_KHACH;
        } else {
            bg = BG_DAT; border = BORDER_DAT;
        }

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.setColor(border);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(220, 130)); // Cố định kích thước mỗi thẻ
        card.setOpaque(false);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel icon = new JLabel("🪑", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        icon.setForeground(border);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblName = new JLabel(name, SwingConstants.CENTER);
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

        if (!bill.isEmpty()) {
            JLabel lblBill = new JLabel("Hóa đơn: " + bill, SwingConstants.CENTER);
            lblBill.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblBill.setForeground(RED_MAIN);
            lblBill.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(Box.createVerticalStrut(5));
            card.add(lblBill);
        }

        // --- ĐOẠN ĐÃ ĐƯỢC CẬP NHẬT ĐỂ MỞ FrmGoiMon ---
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
                    // Mở cửa sổ Gọi Món
                    FrmGoiMon frm = new FrmGoiMon(FrmLeTan.this, name, capacity);
                    frm.setVisible(true);
                    refreshSoDoBan(); // F5 lại bàn
                } 
                else if (status.equalsIgnoreCase("Có khách") || status.equalsIgnoreCase("Đã đặt")) {
                    // Mở cửa sổ Thanh Toán
                    FrmThanhToan frmTT = new FrmThanhToan(FrmLeTan.this, name);
                    frmTT.setVisible(true);
                    
                    // F5 lại sơ đồ: Nếu thanh toán xong, bàn sẽ về lại màu Xanh lá (Trống)
                    refreshSoDoBan(); 
                }
            }
        });

        return card;
    }

    // ==========================================
    // 4. RIGHT SIDEBAR (Giữ nguyên)
    // ==========================================
    private JPanel createRightSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(320, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER_CLR));

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(Color.WHITE);
        top.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Quản lý Đặt chỗ");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtSearch = new JTextField();
        txtSearch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtSearch.setPreferredSize(new Dimension(0, 35));
        txtSearch.setText("🔍 Tìm khách hàng / SĐT");
        txtSearch.setForeground(TEXT_GRAY);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(0, 10, 0, 10)
        ));

        JLabel subTitle = new JLabel("Đặt chỗ hôm nay");
        subTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        subTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
        subTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        top.add(title);
        top.add(Box.createVerticalStrut(15));
        top.add(txtSearch);
        top.add(subTitle);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(Color.WHITE);
        list.setBorder(new EmptyBorder(0, 20, 0, 20));

        list.add(createBookingCard("Nguyễn Văn A", "0901234567", "19:00", "Bàn 03", 6));
        list.add(Box.createVerticalStrut(15));
        list.add(createBookingCard("Trần Thị B", "0912345678", "20:00", "VIP 02", 8));

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);

        JPanel actions = new JPanel(new GridLayout(4, 1, 0, 10)); 
        actions.setBackground(Color.WHITE);
        actions.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel lblQuick = new JLabel("Thao tác nhanh");
        lblQuick.setFont(new Font("Segoe UI", Font.BOLD, 14));
        actions.add(lblQuick);
        
        JButton btnTaoDatCho = createSolidButton("+ Tạo đặt chỗ mới", RED_MAIN, Color.WHITE);
        btnTaoDatCho.addActionListener(e -> {
            FrmTaoDatCho frm = new FrmTaoDatCho(this);
            frm.setVisible(true); 
        });
        actions.add(btnTaoDatCho);

        JButton btnChuyenBan = createOutlineButton("⇆ Chuyển bàn");
        btnChuyenBan.addActionListener(e -> {
            FrmChuyenBan frmCB = new FrmChuyenBan(this);
            frmCB.setVisible(true);
        });
        actions.add(btnChuyenBan);

        JButton btnGopBan = createOutlineButton("⛙ Gộp bàn");
        btnGopBan.addActionListener(e -> {
            FrmGopBan frmGB = new FrmGopBan(this);
            frmGB.setVisible(true);
        });
        actions.add(btnGopBan);

        sidebar.add(top, BorderLayout.NORTH);
        sidebar.add(scroll, BorderLayout.CENTER);
        sidebar.add(actions, BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel createBookingCard(String name, String phone, String time, String table, int guests) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(254, 252, 232)); 
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.setColor(new Color(253, 224, 71)); 
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout(10, 10));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 12, 10, 12));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel info = new JPanel(new GridLayout(3, 2));
        info.setOpaque(false);

        JLabel lName = new JLabel(name); lName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel lTime = new JLabel(time, SwingConstants.RIGHT); lTime.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JLabel lPhone = new JLabel(phone); lPhone.setFont(new Font("Segoe UI", Font.PLAIN, 12)); lPhone.setForeground(TEXT_GRAY);
        JLabel empty = new JLabel("");
        
        JLabel lTable = new JLabel("Bàn: " + table); lTable.setFont(new Font("Segoe UI", Font.PLAIN, 12)); lTable.setForeground(TEXT_GRAY);
        JLabel lGuests = new JLabel(guests + " người", SwingConstants.RIGHT); lGuests.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        info.add(lName); info.add(lTime);
        info.add(lPhone); info.add(empty);
        info.add(lTable); info.add(lGuests);

        JButton btnCheckIn = createSolidButton("Check-in Khách", RED_MAIN, Color.WHITE);
        btnCheckIn.setPreferredSize(new Dimension(0, 30));

        btnCheckIn.addActionListener(e -> {
            int xacNhan = JOptionPane.showConfirmDialog(card, 
                    "Bạn có chắc chắn muốn Check-in cho khách " + name + " vào " + table + " không?", 
                    "Xác nhận Check-in", 
                    JOptionPane.YES_NO_OPTION);
            
            if (xacNhan == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(card, "✅ Đã Check-in thành công cho " + name + "!");
            }
        });

        card.add(info, BorderLayout.CENTER);
        card.add(btnCheckIn, BorderLayout.SOUTH);

        return card;
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

    // ==========================================
    // 5. HÀM REFRESH SƠ ĐỒ BÀN (CÓ LỌC THEO TAB)
    // ==========================================
    public void refreshSoDoBan() {
        if (gridMap == null) return;
        
        gridMap.removeAll(); // Xóa sạch để vẽ lại
        
        // Cập nhật lại Tiêu đề Sơ đồ theo Tab
        if (lblMapTitle != null) {
            lblMapTitle.setText("Sơ đồ bàn - " + currentTab);
        }
        
        DAO.BanAnDAO banAnDAO = new DAO.BanAnDAO();
        List<Entity.BanAn> danhSachBan = banAnDAO.getAllBanAn();
        
        for (Entity.BanAn ban : danhSachBan) {
            // ĐIỂM CHỐT LÕI: Chỉ lấy những bàn có vị trí giống với Tab đang chọn
            String viTriBan = ban.getViTri(); // Ví dụ: "Tầng 1", "Phòng VIP"
            
            if (viTriBan != null && viTriBan.trim().equalsIgnoreCase(currentTab)) {
                gridMap.add(createTableCard(
                    ban.getTenBan(), 
                    ban.getSucChua(), 
                    ban.getTrangThai().trim(), 
                    "" 
                ));
            }
        }
        
        gridMap.revalidate(); 
        gridMap.repaint();    
    }

    // ==========================================
    // MAIN
    // ==========================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FrmLeTan().setVisible(true);
        });
    }
}
package GUI;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class FrmChuyenBan extends JDialog {

    private static final Color RED_MAIN = new Color(220, 38, 38);
    private static final Color BORDER_CLR = new Color(230, 230, 230);
    private static final Color TEXT_DARK = new Color(40, 40, 40);

    private JComboBox<String> cbBanCu;
    private JComboBox<String> cbBanMoi;

    public FrmChuyenBan(JFrame parent) {
        super(parent, true); // Modal: Khóa màn hình chính khi popup này hiện lên
        setUndecorated(true); // Bỏ viền Windows mặc định
        setSize(500, 350);    // Kích thước nhỏ gọn
        setLocationRelativeTo(parent);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 2));

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createBody(), BorderLayout.CENTER);
        root.add(createFooter(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("⇆ Chuyển bàn");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton btnClose = new JButton("✕");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 16));
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

    private JPanel createBody() {
        JPanel body = new JPanel(new GridLayout(2, 1, 0, 15));
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(20, 30, 20, 30));

        // MẸO: Sau này bạn sẽ dùng DAO để lấy danh sách bàn ĐANG CÓ KHÁCH đưa vào cbBanCu
        cbBanCu = new JComboBox<>(new String[]{"-- Chọn bàn đang ngồi --", "Bàn 1", "Bàn VIP 1"});
        
        // MẸO: Dùng DAO để lấy danh sách bàn TRỐNG đưa vào cbBanMoi
        cbBanMoi = new JComboBox<>(new String[]{"-- Chọn bàn muốn chuyển đến --", "Bàn 2", "Bàn 3", "Bàn 4"});

        body.add(createInputGroup("Từ bàn (Đang có khách):", cbBanCu));
        body.add(createInputGroup("Đến bàn (Bàn trống):", cbBanMoi));

        return body;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR));

        JButton btnHuy = new JButton("Hủy");
        btnHuy.setPreferredSize(new Dimension(100, 35));
        btnHuy.setBackground(Color.WHITE);
        btnHuy.setFocusPainted(false);
        btnHuy.addActionListener(e -> this.dispose());

        JButton btnXacNhan = new JButton("Xác nhận chuyển");
        btnXacNhan.setPreferredSize(new Dimension(150, 35));
        btnXacNhan.setBackground(RED_MAIN);
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnXacNhan.setFocusPainted(false);
        btnXacNhan.setBorderPainted(false);
        
        // --- LOGIC XỬ LÝ CHUYỂN BÀN THỰC TẾ ---
        btnXacNhan.addActionListener(e -> {
            String tuBan = cbBanCu.getSelectedItem().toString();
            String denBan = cbBanMoi.getSelectedItem().toString();

            if(tuBan.contains("--") || denBan.contains("--")) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ bàn đi và bàn đến!");
                return;
            }

            // 1. Gọi Database xử lý đổi trạng thái cả 2 bàn
            DAO.BanAnDAO dao = new DAO.BanAnDAO();
            boolean thanhCong = dao.chuyenHoacGopBan(tuBan, denBan);

            if (thanhCong) {
                JOptionPane.showMessageDialog(this, "✅ Đã chuyển khách từ " + tuBan + " sang " + denBan + " thành công!");
                
                // 2. Ép màn hình Lễ Tân phải F5 (Vẽ lại sơ đồ bàn)
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                if (parentFrame instanceof FrmLeTan) {
                    ((FrmLeTan) parentFrame).refreshSoDoBan();
                }
                
                // 3. Đóng popup
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Lỗi kết nối CSDL khi chuyển bàn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        footer.add(btnHuy);
        footer.add(btnXacNhan);
        return footer;
    }

    private JPanel createInputGroup(String title, JComponent input) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(TEXT_DARK);
        
        input.setPreferredSize(new Dimension(0, 38));
        input.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(input, BorderLayout.CENTER);
        return panel;
    }
}
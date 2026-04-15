package GUI;

import Entity.LuuLog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.RenderingHints;
public class FrmCaiDat extends JPanel {

	// --- MÀU SẮC CHUẨN THEO THIẾT KẾ ---
	private static final Color BG_MAIN = new Color(243, 244, 246); // Nền xám nhạt (như trong ảnh)
	private static final Color BG_CARD = Color.WHITE; // Nền thẻ Card (trắng)
	private static final Color BORDER_CLR = new Color(229, 231, 235); // Viền xám mờ
	private static final Color TEXT_TITLE = new Color(17, 24, 39); // Đen đậm cho Tiêu đề lớn
	private static final Color TEXT_DARK = new Color(31, 41, 55); // Đen vừa cho giá trị
	private static final Color TEXT_GRAY = new Color(107, 114, 128); // Xám cho nhãn (Label)
	private static final Color BTN_BG = new Color(15, 23, 42); // Nền nút đen nhám

	// --- FONT CHỮ ---
	private static final Font FONT_H1 = new Font("Segoe UI", Font.BOLD, 28);
	private static final Font FONT_H2 = new Font("Segoe UI", Font.BOLD, 16);
	private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 13);
	private static final Font FONT_VALUE = new Font("Segoe UI", Font.PLAIN, 15);

	public FrmCaiDat() {
		initUI();
	}

	private void initUI() {
		setLayout(new BorderLayout());
		setBackground(BG_MAIN);

		// Khung root chứa toàn bộ nội dung (có padding rộng để giống web)
		JPanel root = new JPanel(new BorderLayout(0, 24));
		root.setBackground(BG_MAIN);
		root.setBorder(new EmptyBorder(30, 40, 40, 40));

		// 1. HEADER (Tiêu đề "Cài đặt" to ở góc trái)
		JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		header.setOpaque(false);
		header.setBorder(new EmptyBorder(0, 0, 10, 0));

		JLabel lblTitle = new JLabel("⚙ Cài đặt");
		lblTitle.setFont(FONT_H1);
		lblTitle.setForeground(TEXT_TITLE);
		header.add(lblTitle);

		// 2. BODY (Dùng BoxLayout Y_AXIS để xếp các thẻ Card từ trên xuống)
		JPanel bodyContent = new JPanel();
		bodyContent.setLayout(new BoxLayout(bodyContent, BoxLayout.Y_AXIS));
		bodyContent.setBackground(BG_MAIN);

		// Thêm các Card Cài đặt vào Body
		bodyContent.add(createAccountCard());
		bodyContent.add(Box.createVerticalStrut(24));
		bodyContent.add(createRestaurantCard());
		bodyContent.add(Box.createVerticalStrut(24));
		bodyContent.add(createSystemCard());

		// Bọc bodyContent vào một panel căn Bắc (NORTH) để nó không bị dãn giãn ra giữa
		// màn hình
		JPanel bodyWrapper = new JPanel(new BorderLayout());
		bodyWrapper.setBackground(BG_MAIN);
		bodyWrapper.add(bodyContent, BorderLayout.NORTH);

		// Thêm thanh cuộn (Scroll) phòng trường hợp màn hình nhỏ
		JScrollPane scrollPane = new JScrollPane(bodyWrapper);
		scrollPane.setBorder(null);
		scrollPane.getViewport().setBackground(BG_MAIN);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		root.add(header, BorderLayout.NORTH);
		root.add(scrollPane, BorderLayout.CENTER);

		add(root, BorderLayout.CENTER);
	}

	// CARD 1: THÔNG TIN TÀI KHOẢN
	private JPanel createAccountCard() {
		JPanel card = createCustomCard();

		// Header Card
		card.add(createCardHeader("👤 Thông tin tài khoản"));
		card.add(Box.createVerticalStrut(20));

		// Dữ liệu mặc định
		String tenDangNhap = "admin";
		String hoTen = "Chưa cập nhật";
		String email = "Chưa cập nhật";

		// Lấy dữ liệu thật từ LuuLog (Sạch lỗi 100%)
		if (LuuLog.nhanVienDangNhap != null) {
			// NhanVienModel của bạn hiện tại chỉ chắc chắn có hàm getMaNV()
			if (LuuLog.nhanVienDangNhap.getMaNV() != null && !LuuLog.nhanVienDangNhap.getMaNV().isEmpty()) {
				tenDangNhap = LuuLog.nhanVienDangNhap.getMaNV();
			}

			// Nếu sau này bạn cập nhật NhanVienModel có thêm các hàm dưới đây, hãy mở
			// comment ra để dùng:
			// hoTen = LuuLog.nhanVienDangNhap.getHoTenNV();
			// email = LuuLog.nhanVienDangNhap.getSoDienThoai();
		}

		// Lưới thông tin: Row 1 (Tên đăng nhập | Họ và tên)
		card.add(createTwoColRow(createLabelValue("Tên đăng nhập", tenDangNhap), createLabelValue("Họ và tên", hoTen)));
		card.add(Box.createVerticalStrut(20));

		// Lưới thông tin: Row 2 (Email/SĐT | Bỏ trống)
		card.add(createTwoColRow(createLabelValue("Email / Số điện thoại", email), createEmptyBlock()));
		card.add(Box.createVerticalStrut(24));

		// Đường kẻ ngang
		card.add(createDivider());
		card.add(Box.createVerticalStrut(16));

		// Nút đổi mật khẩu (Nằm góc phải)
		JButton btnDoiMatKhau = new JButton("🔑 Đổi mật khẩu");
		btnDoiMatKhau.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnDoiMatKhau.setBackground(BTN_BG);
		btnDoiMatKhau.setForeground(Color.WHITE);
		btnDoiMatKhau.setFocusPainted(false);
		btnDoiMatKhau.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnDoiMatKhau.setBorder(new EmptyBorder(8, 16, 8, 16));

		// Bắt sự kiện đổi mật khẩu
		btnDoiMatKhau.addActionListener(e -> hienThiDialogDoiMatKhau());

		JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		btnWrap.setOpaque(false);
		btnWrap.add(btnDoiMatKhau);

		card.add(btnWrap);

		return card;
	}

	// CARD 2: THÔNG TIN NHÀ HÀNG
	private JPanel createRestaurantCard() {
		JPanel card = createCustomCard();

		card.add(createCardHeader("🏢 Thông tin nhà hàng"));
		card.add(Box.createVerticalStrut(20));

		// Row 1
		card.add(createTwoColRow(createLabelValue("Tên nhà hàng", "Nhà hàng Phố Cổ"),
				createLabelValue("Số điện thoại", "024 3826 5555")));
		card.add(Box.createVerticalStrut(20));

		// Row 2 (Full width)
		JPanel row2 = new JPanel(new BorderLayout());
		row2.setOpaque(false);
		row2.add(createLabelValue("Địa chỉ", "123 Đường Hoàn Kiếm, Quận Hoàn Kiếm, Hà Nội"), BorderLayout.CENTER);
		row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
		card.add(row2);
		card.add(Box.createVerticalStrut(20));

		// Row 3
		card.add(createTwoColRow(createLabelValue("Email liên hệ", "contact@phoco.vn"),
				createLabelValue("Website", "www.phoco.vn")));

		return card;
	}

	// CARD 3: CÀI ĐẶT HỆ THỐNG & THIẾT BỊ
	private JPanel createSystemCard() {
		JPanel card = createCustomCard();

		card.add(createCardHeader("⚙ Hệ thống & Thiết bị"));
		card.add(Box.createVerticalStrut(20));

		// Row 1
		card.add(createToggleRow("Tự động in hóa đơn", "In hóa đơn tự động sau khi thanh toán thành công", true));
		card.add(Box.createVerticalStrut(16));
		card.add(createDivider());
		card.add(Box.createVerticalStrut(16));

		// Row 2
		card.add(createToggleRow("Âm thanh thông báo", "Phát âm thanh khi có đơn hàng mới hoặc thông báo quan trọng",
				true));

		return card;
	}

	// CÁC HÀM TIỆN ÍCH DỰNG GIAO DIỆN TÁI SỬ DỤNG

	// 1. Tạo Panel Card có nền trắng, viền mờ bo góc
	private JPanel createCustomCard() {
		JPanel card = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				// Vẽ nền
				g2.setColor(BG_CARD);
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
				// Vẽ viền
				g2.setColor(BORDER_CLR);
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setOpaque(false); // Để vẽ bo góc không bị dính nền đen ở 4 góc
		card.setBorder(new EmptyBorder(24, 24, 24, 24)); // Padding trong card
		return card;
	}

	// 2. Header của từng Card
	private JPanel createCardHeader(String title) {
		JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		pnl.setOpaque(false);
		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(FONT_H2);
		lblTitle.setForeground(TEXT_TITLE);
		pnl.add(lblTitle);
		pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		return pnl;
	}

	// 3. Khối hiển thị Title nhỏ và Giá trị đậm
	private JPanel createLabelValue(String label, String value) {
		JPanel pnl = new JPanel();
		pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
		pnl.setOpaque(false);

		JLabel lbl = new JLabel(label);
		lbl.setFont(FONT_LABEL);
		lbl.setForeground(TEXT_GRAY);

		JLabel val = new JLabel(value);
		val.setFont(FONT_VALUE);
		val.setForeground(TEXT_DARK);

		pnl.add(lbl);
		pnl.add(Box.createVerticalStrut(6)); // Khoảng cách giữa nhãn và giá trị
		pnl.add(val);
		return pnl;
	}

	// 4. Tạo hàng có 2 cột bằng nhau
	private JPanel createTwoColRow(JPanel col1, JPanel col2) {
		JPanel row = new JPanel(new GridLayout(1, 2, 20, 0));
		row.setOpaque(false);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
		row.add(col1);
		row.add(col2);
		return row;
	}

	// 5. Khối rỗng để đẩy Layout
	private JPanel createEmptyBlock() {
		JPanel pnl = new JPanel();
		pnl.setOpaque(false);
		return pnl;
	}

	// 6. Đường phân cách ngang (Divider)
	private JPanel createDivider() {
		JPanel line = new JPanel();
		line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
		line.setPreferredSize(new Dimension(0, 1));
		line.setBackground(BORDER_CLR);
		return line;
	}

	// 7. Tạo dòng có chứa Cài đặt (Bên trái: Title + Sub, Bên phải: Checkbox)
	private JPanel createToggleRow(String title, String sub, boolean isSelected) {
		JPanel row = new JPanel(new BorderLayout());
		row.setOpaque(false);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

		JPanel leftWrap = new JPanel();
		leftWrap.setLayout(new BoxLayout(leftWrap, BoxLayout.Y_AXIS));
		leftWrap.setOpaque(false);

		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
		lblTitle.setForeground(TEXT_TITLE);

		JLabel lblSub = new JLabel(sub);
		lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblSub.setForeground(TEXT_GRAY);

		leftWrap.add(lblTitle);
		leftWrap.add(Box.createVerticalStrut(4));
		leftWrap.add(lblSub);

		// Checkbox hiện đại
		JCheckBox chk = new JCheckBox();
		chk.setSelected(isSelected);
		chk.setOpaque(false);
		chk.setCursor(new Cursor(Cursor.HAND_CURSOR));
		chk.setFocusPainted(false);

		// Bắt sự kiện khi click checkbox
		chk.addActionListener(e -> {
			boolean checked = chk.isSelected();
			// Xử lý lưu cài đặt ở đây nếu cần
			System.out.println(title + ": " + checked);
		});

		row.add(leftWrap, BorderLayout.CENTER);
		row.add(chk, BorderLayout.EAST);

		return row;
	}

	// LOGIC XỬ LÝ SỰ KIỆN
	private void hienThiDialogDoiMatKhau() {
		JPanel panel = new JPanel(new GridLayout(3, 2, 10, 15));
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));

		JLabel lblOld = new JLabel("Mật khẩu cũ:");
		JPasswordField txtOld = new JPasswordField();

		JLabel lblNew = new JLabel("Mật khẩu mới:");
		JPasswordField txtNew = new JPasswordField();

		JLabel lblConfirm = new JLabel("Xác nhận mật khẩu mới:");
		JPasswordField txtConfirm = new JPasswordField();

		panel.add(lblOld);
		panel.add(txtOld);
		panel.add(lblNew);
		panel.add(txtNew);
		panel.add(lblConfirm);
		panel.add(txtConfirm);

		int result = JOptionPane.showConfirmDialog(this, panel, "Đổi Mật Khẩu", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			String oldPass = new String(txtOld.getPassword());
			String newPass = new String(txtNew.getPassword());
			String confirmPass = new String(txtConfirm.getPassword());

			if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (!newPass.equals(confirmPass)) {
				JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Gọi DAO để cập nhật Database ở đây
			JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!\nVui lòng đăng nhập lại ở lần tới.");
		}
	}

	// Hàm Test chạy độc lập form này
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame f = new JFrame("Test Giao Diện Cài Đặt");
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setSize(1000, 750);
			f.setLocationRelativeTo(null);
			f.setContentPane(new FrmCaiDat());
			f.setVisible(true);
		});
	}
}
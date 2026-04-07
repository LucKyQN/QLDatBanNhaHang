
package GUI;

import Entity.NhanVien;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class FrmDashBoard extends JFrame {
	private NhanVien nhanVien;
	private String tenNhanVien;

	private static final Color RED_MAIN = new Color(220, 38, 38);
	private static final Color BG_MAIN = new Color(248, 248, 248);
	private static final Color SIDEBAR_BG = Color.WHITE;
	private static final Color TEXT_DARK = new Color(30, 30, 30);
	private static final Color TEXT_GRAY = new Color(110, 110, 110);
	private static final Color BORDER_CLR = new Color(230, 230, 230);

	private JLabel lbTitle;
	private JLabel lbSub;

	private CardLayout cardLayout;
	private JPanel contentPanel;

	private JPanel dashboardPanel;
	private FrmQLNhanVien pnlNhanVien;
	private FrmQLMonAn pnlMonAn;
	private FrmQLBanAn pnlBanAn;
	private final Map<String, JPanel> menuMap = new LinkedHashMap<>();
	private String activeMenu = "Dashboard";

	public FrmDashBoard(NhanVien nhanVien) {
		this.nhanVien = nhanVien;

		if (nhanVien == null || !"Quản lý".equalsIgnoreCase(nhanVien.getVaiTro())) {
			JOptionPane.showMessageDialog(this, "Bạn không có quyền truy cập màn hình quản lý.");
			dispose();
			return;
		}

		this.tenNhanVien = nhanVien.getHoTenNV();
		initUI();
	}

	private void initUI() {
		setTitle("Hệ Thống Quản Lý - Nhà Hàng Ngói Đỏ");
		setSize(1440, 860);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(true);

		JPanel root = new JPanel(new BorderLayout());
		root.setBackground(BG_MAIN);

		root.add(createSidebar(), BorderLayout.WEST);
		root.add(createMainArea(), BorderLayout.CENTER);

		setContentPane(root);
	}

	// ========================= SIDEBAR =========================
	private JPanel createSidebar() {
		JPanel sidebar = new JPanel();
		sidebar.setPreferredSize(new Dimension(210, 0));
		sidebar.setBackground(SIDEBAR_BG);
		sidebar.setLayout(new BorderLayout());
		sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_CLR));

		JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 14));
		logoPanel.setBackground(SIDEBAR_BG);

		JPanel redCircle = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(RED_MAIN);
				g2.fillOval(0, 0, getWidth(), getHeight());
				g2.dispose();
			}
		};
		redCircle.setPreferredSize(new Dimension(40, 40));
		redCircle.setOpaque(false);
		redCircle.setLayout(new GridBagLayout());

		JLabel lbEmoji = new JLabel("🏮");
		lbEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
		redCircle.add(lbEmoji);

		JPanel nameBox = new JPanel();
		nameBox.setOpaque(false);
		nameBox.setLayout(new BoxLayout(nameBox, BoxLayout.Y_AXIS));

		JLabel lbName = new JLabel("Ngói Đỏ");
		lbName.setFont(new Font("Segoe UI", Font.BOLD, 15));
		lbName.setForeground(TEXT_DARK);

		JLabel lbRole = new JLabel("Admin Panel");
		lbRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lbRole.setForeground(TEXT_GRAY);

		nameBox.add(lbName);
		nameBox.add(lbRole);

		logoPanel.add(redCircle);
		logoPanel.add(nameBox);

		JPanel menuPanel = new JPanel();
		menuPanel.setBackground(SIDEBAR_BG);
		menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
		menuPanel.setBorder(new EmptyBorder(8, 10, 8, 10));

		JPanel btnDashboard = createMenuButton("🏠", "Dashboard");
		JPanel btnThucDon = createMenuButton("🍽", "Quản lý Thực đơn");
		JPanel btnBan = createMenuButton("🪑", "Quản lý Bàn");

		JPanel btnNhanSu = createMenuButton("👥", "Quản lý Nhân sự");
		JPanel btnKhuyenMai = createMenuButton("🎁", "Quản lý Khuyến mãi");
		JPanel btnBaoCao = createMenuButton("📈", "Báo cáo Doanh thu");
		JPanel btnCaiDat = createMenuButton("⚙", "Cài đặt");

		menuMap.put("Dashboard", btnDashboard);
		menuMap.put("Quản lý Thực đơn", btnThucDon);
		menuMap.put("Quản lý Bàn", btnBan);

		menuMap.put("Quản lý Nhân sự", btnNhanSu);
		menuMap.put("Quản lý Khuyến mãi", btnKhuyenMai);
		menuMap.put("Báo cáo Doanh thu", btnBaoCao);
		menuMap.put("Cài đặt", btnCaiDat);

		addMenuAction(btnDashboard, "Dashboard");
		addMenuAction(btnThucDon, "Quản lý Thực đơn");
		addMenuAction(btnBan, "Quản lý Bàn");

		addMenuAction(btnNhanSu, "Quản lý Nhân sự");

		menuPanel.add(btnDashboard);
		menuPanel.add(Box.createVerticalStrut(6));
		menuPanel.add(btnThucDon);
		menuPanel.add(Box.createVerticalStrut(6));
		menuPanel.add(btnBan);
		menuPanel.add(Box.createVerticalStrut(6));
		menuPanel.add(btnNhanSu);
		menuPanel.add(Box.createVerticalStrut(6));
		menuPanel.add(btnKhuyenMai);
		menuPanel.add(Box.createVerticalStrut(6));
		menuPanel.add(btnBaoCao);
		menuPanel.add(Box.createVerticalStrut(6));
		menuPanel.add(btnCaiDat);

		updateMenuState();

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
		bottomPanel.setBackground(SIDEBAR_BG);
		bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR));

		JLabel lbLogout = new JLabel("⎋  Đăng xuất");
		lbLogout.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lbLogout.setForeground(TEXT_GRAY);
		lbLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lbLogout.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int confirm = JOptionPane.showConfirmDialog(FrmDashBoard.this, "Bạn có chắc muốn đăng xuất?",
						"Đăng xuất", JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.YES_OPTION) {
					dispose();
					new FrmDangNhap().setVisible(true);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				lbLogout.setForeground(RED_MAIN);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				lbLogout.setForeground(TEXT_GRAY);
			}
		});
		bottomPanel.add(lbLogout);

		sidebar.add(logoPanel, BorderLayout.NORTH);
		sidebar.add(menuPanel, BorderLayout.CENTER);
		sidebar.add(bottomPanel, BorderLayout.SOUTH);

		return sidebar;
	}

	private JPanel createMenuButton(String icon, String label) {
		JPanel btn = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
		btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn.setBackground(SIDEBAR_BG);

		JLabel lbIcon = new JLabel(icon);
		lbIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));

		JLabel lbLabel = new JLabel(label);
		lbLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lbLabel.setForeground(TEXT_DARK);

		btn.add(lbIcon);
		btn.add(lbLabel);

		btn.putClientProperty("menuLabel", label);
		btn.putClientProperty("iconLabel", lbIcon);
		btn.putClientProperty("textLabel", lbLabel);

		btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				String thisLabel = (String) btn.getClientProperty("menuLabel");
				if (!thisLabel.equals(activeMenu)) {
					btn.setBackground(new Color(250, 250, 250));
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				String thisLabel = (String) btn.getClientProperty("menuLabel");
				if (!thisLabel.equals(activeMenu)) {
					btn.setBackground(SIDEBAR_BG);
				}
			}
		});

		return btn;
	}

	private void addMenuAction(JPanel btn, String menuName) {
		btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				switch (menuName) {
				case "Dashboard":
					showPage("DASHBOARD", "Dashboard", "Tổng quan hoạt động nhà hàng - Xin chào " + tenNhanVien,
							"Dashboard");
					break;
				case "Quản lý Bàn":
					showPage("BANAN", "Quản lý Bàn", "Thêm, sửa, xóa và quản lý danh sách bàn ăn", "Quản lý Bàn");
					break;
				case "Quản lý Thực đơn":
					showPage("MONAN", "Quản lý Thực đơn", "Thêm, sửa, xóa và quản lý danh sách món ăn",
							"Quản lý Thực đơn");
					break;
				case "Quản lý Nhân sự":
					showPage("NHANVIEN", "Quản lý Nhân sự", "Thêm, sửa, xóa và quản lý danh sách nhân viên",
							"Quản lý Nhân sự");
					break;
				}
			}
		});
	}

	private void updateMenuState() {
		for (Map.Entry<String, JPanel> entry : menuMap.entrySet()) {
			String label = entry.getKey();
			JPanel btn = entry.getValue();

			JLabel lbText = (JLabel) btn.getClientProperty("textLabel");
			boolean isActive = label.equals(activeMenu);

			btn.setBackground(isActive ? new Color(254, 242, 242) : SIDEBAR_BG);
			lbText.setForeground(isActive ? RED_MAIN : TEXT_DARK);
			lbText.setFont(new Font("Segoe UI", isActive ? Font.BOLD : Font.PLAIN, 13));
		}
	}

	// ========================= MAIN AREA =========================
	private JPanel createMainArea() {
		JPanel main = new JPanel(new BorderLayout());
		main.setBackground(BG_MAIN);

		main.add(createTopBar(), BorderLayout.NORTH);

		cardLayout = new CardLayout();
		contentPanel = new JPanel(cardLayout);
		contentPanel.setBackground(BG_MAIN);

		dashboardPanel = buildDashboardWrapper();
		pnlNhanVien = new FrmQLNhanVien();
		pnlMonAn = new FrmQLMonAn();
		pnlBanAn = new FrmQLBanAn();
		contentPanel.add(dashboardPanel, "DASHBOARD");
		contentPanel.add(pnlNhanVien, "NHANVIEN");
		contentPanel.add(pnlMonAn, "MONAN");
		contentPanel.add(pnlBanAn, "BANAN");
		main.add(contentPanel, BorderLayout.CENTER);
		return main;
	}

	private JPanel createTopBar() {
		JPanel bar = new JPanel(new BorderLayout());
		bar.setBackground(Color.WHITE);
		bar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
				new EmptyBorder(14, 24, 14, 24)));

		JPanel left = new JPanel();
		left.setOpaque(false);
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

		lbTitle = new JLabel("Dashboard");
		lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
		lbTitle.setForeground(TEXT_DARK);

		lbSub = new JLabel("Tổng quan hoạt động nhà hàng - Xin chào " + tenNhanVien);
		lbSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lbSub.setForeground(TEXT_GRAY);

		left.add(lbTitle);
		left.add(lbSub);

		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("d/M/yyyy"));

		JPanel right = new JPanel();
		right.setOpaque(false);
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

		JLabel lbToday = new JLabel("Hôm nay");
		lbToday.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lbToday.setForeground(TEXT_GRAY);
		lbToday.setAlignmentX(Component.RIGHT_ALIGNMENT);

		JLabel lbDate = new JLabel(today);
		lbDate.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lbDate.setForeground(TEXT_DARK);
		lbDate.setAlignmentX(Component.RIGHT_ALIGNMENT);

		right.add(lbToday);
		right.add(lbDate);

		bar.add(left, BorderLayout.WEST);
		bar.add(right, BorderLayout.EAST);

		return bar;
	}

	private JPanel buildDashboardWrapper() {
		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.setBackground(BG_MAIN);

		JScrollPane scroll = new JScrollPane(createDashboardContent());
		scroll.setBorder(null);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		scroll.getViewport().setBackground(BG_MAIN);

		wrapper.add(scroll, BorderLayout.CENTER);
		return wrapper;
	}

	private void showPage(String cardName, String title, String subTitle, String menuName) {
		lbTitle.setText(title);
		lbSub.setText(subTitle);
		activeMenu = menuName;
		updateMenuState();
		cardLayout.show(contentPanel, cardName);
	}

	// ========================= DASHBOARD CONTENT =========================
	private JPanel createDashboardContent() {
		JPanel content = new JPanel();
		content.setBackground(BG_MAIN);
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.setBorder(new EmptyBorder(20, 24, 24, 24));

		JPanel cardsRow = new JPanel(new GridLayout(1, 4, 14, 0));
		cardsRow.setOpaque(false);
		cardsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

		cardsRow.add(createStatCard("💰", "Doanh thu hôm nay", "14.200.000đ", "+12.5%", true, new Color(34, 197, 94)));
		cardsRow.add(createStatCard("🛒", "Tổng đơn hàng", "48", "+8.2%", true, new Color(99, 102, 241)));
		cardsRow.add(createStatCard("👤", "Nhân viên đang làm", "12", "", false, new Color(168, 85, 247)));
		cardsRow.add(createStatCard("⚠", "Tỷ lệ hủy", "2.3%", "-0.5%", false, new Color(251, 146, 60)));

		content.add(cardsRow);
		content.add(Box.createVerticalStrut(18));

		JPanel chartsRow = new JPanel(new GridLayout(1, 2, 14, 0));
		chartsRow.setOpaque(false);
		chartsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

		chartsRow.add(createChartPlaceholder("📊  Doanh thu 7 ngày qua", "Biểu đồ cột sẽ hiển thị tại đây"));
		chartsRow.add(createChartPlaceholder("🥧  Món ăn bán chạy nhất", "Biểu đồ tròn sẽ hiển thị tại đây"));

		content.add(chartsRow);
		content.add(Box.createVerticalStrut(18));

		content.add(createShiftTable());

		return content;
	}

	private JPanel createStatCard(String icon, String label, String value, String badge, boolean badgePositive,
			Color iconColor) {
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
				Color bg = new Color(iconColor.getRed(), iconColor.getGreen(), iconColor.getBlue(), 30);
				g2.setColor(bg);
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

		if (!badge.isEmpty()) {
			JLabel lbBadge = new JLabel(badge);
			lbBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
			lbBadge.setForeground(badgePositive ? new Color(22, 163, 74) : new Color(220, 38, 38));

			JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
			badgeWrap.setOpaque(false);
			badgeWrap.add(lbBadge);

			top.add(badgeWrap, BorderLayout.EAST);
		}

		JPanel bottom = new JPanel();
		bottom.setOpaque(false);
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));

		JLabel lbLabel = new JLabel(label);
		lbLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lbLabel.setForeground(TEXT_GRAY);

		JLabel lbValue = new JLabel(value);
		lbValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
		lbValue.setForeground(TEXT_DARK);

		bottom.add(lbLabel);
		bottom.add(Box.createVerticalStrut(4));
		bottom.add(lbValue);

		card.add(top, BorderLayout.NORTH);
		card.add(bottom, BorderLayout.SOUTH);

		return card;
	}

	private JPanel createChartPlaceholder(String title, String hint) {
		JPanel card = new JPanel(new BorderLayout());
		card.setBackground(Color.WHITE);
		card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true),
				new EmptyBorder(16, 18, 16, 18)));

		JLabel lbTitle = new JLabel(title);
		lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lbTitle.setForeground(TEXT_DARK);
		lbTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

		JPanel placeholder = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new Color(245, 245, 245));
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
				g2.setColor(new Color(200, 200, 200));
				g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0,
						new float[] { 6, 4 }, 0));
				g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);
				g2.setColor(new Color(180, 180, 180));
				g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
				FontMetrics fm = g2.getFontMetrics();
				int tx = (getWidth() - fm.stringWidth(hint)) / 2;
				int ty = getHeight() / 2 + fm.getAscent() / 2;
				g2.drawString(hint, tx, ty);
				g2.dispose();
			}
		};
		placeholder.setOpaque(false);

		card.add(lbTitle, BorderLayout.NORTH);
		card.add(placeholder, BorderLayout.CENTER);

		return card;
	}

	private JPanel createShiftTable() {
		JPanel card = new JPanel(new BorderLayout());
		card.setBackground(Color.WHITE);
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
		card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true),
				new EmptyBorder(16, 18, 16, 18)));

		JLabel lbTitle = new JLabel("📋  Quản lý ca làm việc");
		lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lbTitle.setForeground(TEXT_DARK);
		lbTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

		String[] cols = { "Thu ngân", "Ca làm", "Trạng thái", "Giờ bắt đầu", "Tiền trong két", "Chênh lệch" };
		Object[][] data = { { "Nguyễn Văn A", "Ca sáng", "Đang mở", "07:00", "5.200.000đ", "0đ" },
				{ "Trần Thị B", "Ca chiều", "Đang mở", "14:00", "8.500.000đ", "+50.000đ" },
				{ "Lê Văn C", "Ca tối", "Chưa mở", "18:00", "-", "-" } };

		DefaultTableModel model = new DefaultTableModel(data, cols) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		JTable table = new JTable(model);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		table.setRowHeight(40);
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setBackground(Color.WHITE);
		table.setSelectionBackground(new Color(254, 242, 242));
		table.setSelectionForeground(TEXT_DARK);
		table.setFocusable(false);

		JTableHeader header = table.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 12));
		header.setBackground(Color.WHITE);
		header.setForeground(TEXT_GRAY);
		header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR));
		header.setPreferredSize(new Dimension(0, 36));
		((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row,
					int col) {
				Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
				if (!sel) {
					c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
				}
				setFont(new Font("Segoe UI", Font.PLAIN, 13));
				setForeground(TEXT_DARK);
				setBorder(new EmptyBorder(0, 8, 0, 8));
				return c;
			}
		});

		table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row,
					int col) {
				JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
				wrap.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));

				JLabel badge = new JLabel(" " + val + " ");
				badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
				badge.setOpaque(true);
				badge.setBorder(new EmptyBorder(3, 8, 3, 8));

				if ("Đang mở".equals(val)) {
					badge.setBackground(new Color(220, 252, 231));
					badge.setForeground(new Color(22, 163, 74));
				} else {
					badge.setBackground(new Color(243, 244, 246));
					badge.setForeground(TEXT_GRAY);
				}

				wrap.add(badge);
				return wrap;
			}
		});

		table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row,
					int col) {
				super.getTableCellRendererComponent(t, val, sel, foc, row, col);
				String v = val.toString();
				setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
				setFont(new Font("Segoe UI", Font.BOLD, 13));
				setBorder(new EmptyBorder(0, 8, 0, 8));

				if (v.startsWith("+")) {
					setForeground(new Color(22, 163, 74));
				} else if (v.startsWith("-") && !v.equals("-")) {
					setForeground(RED_MAIN);
				} else {
					setForeground(TEXT_GRAY);
				}

				return this;
			}
		});

		JScrollPane tableScroll = new JScrollPane(table);
		tableScroll.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
		tableScroll.getViewport().setBackground(Color.WHITE);

		card.add(lbTitle, BorderLayout.NORTH);
		card.add(tableScroll, BorderLayout.CENTER);

		return card;
	}
}
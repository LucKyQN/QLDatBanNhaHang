
package GUI;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import DAO.HoaDonDAO;
import Model.MonAnModel;

public class FrmThanhToan extends JDialog {

	private static final Color RED_MAIN = new Color(220, 38, 38);
	private static final Color BORDER_CLR = new Color(230, 230, 230);
	private static final Color TEXT_DARK = new Color(40, 40, 40);

	private String maBan; // Đã thêm biến mã bàn
	private String tenBan;
	private String maHDHienTai = null; // Lưu mã hóa đơn đang cần thanh toán
	private double tongTienBill = 0;

	private final HoaDonDAO hoaDonDAO = new HoaDonDAO();

	private JLabel lblTotalDisplay;
	private JLabel lblTienThua;
	private JTextField txtKhachDua;
	private DefaultTableModel model;

	// HÀM KHỞI TẠO ĐÃ ĐƯỢC SỬA ĐỂ NHẬN 3 THAM SỐ (Hết báo lỗi đỏ bên FrmLeTan)
	public FrmThanhToan(JFrame parent, String maBan, String tenBan) {
		super(parent, true);
		this.maBan = maBan;
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

		String[] columns = { "Tên món", "SL", "Đơn giá", "Thành tiền" };
		model = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
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

		// 1. Lấy mã hóa đơn chưa thanh toán của cái bàn này thông qua DAO
		maHDHienTai = hoaDonDAO.getMaHoaDonChuaThanhToanCuaBan(this.maBan);

		if (maHDHienTai == null) {
			lblTotalDisplay.setText("0 đ");
			JOptionPane.showMessageDialog(this, "Bàn này hiện không có hóa đơn nào chưa thanh toán!", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		// 2. Lấy danh sách món ăn từ ChiTietHoaDon thông qua DAO
		List<MonAnModel> dsMon = hoaDonDAO.getChiTietHoaDon(maHDHienTai);

		for (MonAnModel mon : dsMon) {
			model.addRow(
					new Object[] { mon.tenMonAn, mon.soLuong, formatMoney(mon.giaBan), formatMoney(mon.thanhTien) });
			tongTienBill += mon.thanhTien;
		}

		lblTotalDisplay.setText(formatMoney(tongTienBill));
	}

	private JPanel createPaymentArea() {
		JPanel pnlPay = new JPanel();
		pnlPay.setLayout(new BoxLayout(pnlPay, BoxLayout.Y_AXIS));
		pnlPay.setBackground(Color.WHITE);
		pnlPay.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_CLR),
				new EmptyBorder(20, 20, 20, 20)));

		lblTotalDisplay = new JLabel("0 đ");
		lblTotalDisplay.setFont(new Font("Segoe UI", Font.BOLD, 22));
		lblTotalDisplay.setForeground(RED_MAIN);

		JPanel rowTienKhachTra = createRowItem("Khách cần trả:", "");
		rowTienKhachTra.add(lblTotalDisplay, BorderLayout.EAST);
		pnlPay.add(rowTienKhachTra);
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
			public void changedUpdate(DocumentEvent e) {
				tinhTienThua();
			}

			public void removeUpdate(DocumentEvent e) {
				tinhTienThua();
			}

			public void insertUpdate(DocumentEvent e) {
				tinhTienThua();
			}
		});

		pnlKhachDua.add(lblKD, BorderLayout.WEST);
		pnlKhachDua.add(txtKhachDua, BorderLayout.CENTER);
		pnlPay.add(pnlKhachDua);
		pnlPay.add(Box.createVerticalStrut(25));

		lblTienThua = new JLabel("0 đ");
		lblTienThua.setFont(new Font("Segoe UI", Font.BOLD, 16));
		JPanel rowTienThua = createRowItem("Tiền thừa trả khách:", "");
		rowTienThua.add(lblTienThua, BorderLayout.EAST);
		pnlPay.add(rowTienThua);

		pnlPay.add(Box.createVerticalGlue());

		JButton btnThanhToan = new JButton("THANH TOÁN & IN BILL");
		btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btnThanhToan.setBackground(RED_MAIN);
		btnThanhToan.setForeground(Color.WHITE);
		btnThanhToan.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
		btnThanhToan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

		btnThanhToan.addActionListener(e -> xuLyThanhToan());

		pnlPay.add(btnThanhToan);
		return pnlPay;
	}

	private void xuLyThanhToan() {
		if (maHDHienTai == null) {
			JOptionPane.showMessageDialog(this, "Không có hóa đơn để thanh toán!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (tongTienBill == 0) {
			JOptionPane.showMessageDialog(this, "Hóa đơn trống, không có món để thanh toán!", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Kiểm tra tiền khách đưa
		try {
			String nhapVao = txtKhachDua.getText().replaceAll("[^\\d]", "");
			double tienKhach = nhapVao.isEmpty() ? 0 : Double.parseDouble(nhapVao);
			if (tienKhach < tongTienBill) {
				JOptionPane.showMessageDialog(this, "Tiền khách đưa chưa đủ!", "Lưu ý", JOptionPane.WARNING_MESSAGE);
				return;
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Gọi hàm thanh toán từ DAO (Hàm này tự cập nhật Hóa Đơn và cập nhật luôn Bàn
		// về Trống)
		// Hiện tại giả định chietKhau = 0 và maKM = "NONE"
		boolean ok = hoaDonDAO.thanhToan(maHDHienTai, tongTienBill, 0, "NONE");

		if (ok) {
			JOptionPane.showMessageDialog(this, "✅ Thanh toán thành công cho hóa đơn: " + maHDHienTai);
			this.dispose();
		} else {
			JOptionPane.showMessageDialog(this, "❌ Lỗi khi lưu vào Database!", "Thất bại", JOptionPane.ERROR_MESSAGE);
		}
	}

	private JPanel createRowItem(String title, String value) {
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
			if (nhapVao.isEmpty()) {
				lblTienThua.setText("0 đ");
				return;
			}

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
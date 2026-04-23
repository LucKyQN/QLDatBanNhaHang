package Entity;

import DAO.KhachHangDAO;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DlgNhapThongTinKhach extends JDialog {
	private JTextField txtTen, txtSDT;
	private JSpinner spnSoNguoi;
	private JButton btnXacNhan;
	private boolean isSuccess = false;

	private final KhachHangDAO khachHangDAO = new KhachHangDAO();
	private boolean tenDuocTuDongDien = false;

	public DlgNhapThongTinKhach(JFrame parent) {
		super(parent, "Thông tin khách", true);
		setLayout(new GridLayout(4, 2, 10, 10));

		add(new JLabel(" Tên khách:"));
		txtTen = new JTextField();
		add(txtTen);

		add(new JLabel(" Số điện thoại:"));
		txtSDT = new JTextField();
		add(txtSDT);

		add(new JLabel(" Số người:"));
		spnSoNguoi = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
		add(spnSoNguoi);

		btnXacNhan = new JButton("Xác nhận mở bàn");
		add(new JLabel(""));
		add(btnXacNhan);

		txtSDT.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				tuDongDienTenTheoSDT();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				tuDongDienTenTheoSDT();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				tuDongDienTenTheoSDT();
			}
		});

		btnXacNhan.addActionListener(e -> {
			if (txtTen.getText().trim().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khách!");
				return;
			}

			isSuccess = true;
			dispose();
		});

		setSize(320, 200);
		setLocationRelativeTo(parent);
	}

	private void tuDongDienTenTheoSDT() {
		try {
			String sdt = txtSDT.getText().trim().replaceAll("[^0-9]", "");

			if (sdt.length() < 10) {
				if (tenDuocTuDongDien) {
					txtTen.setText("");
					tenDuocTuDongDien = false;
				}
				return;
			}

			String tenKhach = khachHangDAO.timTenKhachTheoSDT(sdt);

			if (tenKhach != null && !tenKhach.trim().isEmpty()) {
				txtTen.setText(tenKhach);
				tenDuocTuDongDien = true;
			} else {
				if (tenDuocTuDongDien) {
					txtTen.setText("");
					tenDuocTuDongDien = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getTen() {
		return txtTen.getText().trim();
	}

	public String getSDT() {
		return txtSDT.getText().trim();
	}

	public int getSoNguoi() {
		return (int) spnSoNguoi.getValue();
	}

	public boolean isConfirmed() {
		return isSuccess;
	}
}
package GUI;

import DAO.BanAnDAO;
import Entity.BanAn;
import Entity.LoaiBan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FrmQLBanAn extends JPanel {

	private final BanAnDAO dao = new BanAnDAO();

	private JTable tblBanAn;
	private DefaultTableModel model;
	private List<BanAn> dsBanAn = new ArrayList<>();

	private JLabel lblMaBan;
	private JLabel lblLoaiBan;
	private JLabel lblTenBan;
	private JLabel lblTrangThai;
	private JLabel lblTang;
	private JLabel lblViTri;
	private JLabel lblSucChua;
	private JTextArea txtMoTaChiTiet;

	public FrmQLBanAn() {
		initUI();
		loadTable();
	}

	private void initUI() {
		setLayout(new BorderLayout());
		setBackground(new Color(248, 248, 248));

		JPanel root = new JPanel(new BorderLayout(16, 16));
		root.setBackground(new Color(248, 248, 248));
		root.setBorder(new EmptyBorder(20, 24, 24, 24));

		root.add(createTopBar(), BorderLayout.NORTH);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createTablePanel(), createDetailPanel());
		splitPane.setResizeWeight(0.62);
		splitPane.setBorder(null);
		splitPane.setDividerSize(8);

		root.add(splitPane, BorderLayout.CENTER);

		add(root, BorderLayout.CENTER);
	}

	private JPanel createTopBar() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true), new EmptyBorder(16, 16, 16, 16)));

		JLabel lblTitle = new JLabel("Quản lý bàn");
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));

		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		btnPanel.setOpaque(false);

		JButton btnThem = new JButton("Thêm");
		JButton btnSua = new JButton("Sửa");
		JButton btnXoa = new JButton("Xóa mềm");
		JButton btnLamMoi = new JButton("Làm mới");

		btnThem.addActionListener(e -> moDialogThem());
		btnSua.addActionListener(e -> moDialogSua());
		btnXoa.addActionListener(e -> xoaMemBanAn());
		btnLamMoi.addActionListener(e -> loadTable());

		btnPanel.add(btnLamMoi);
		btnPanel.add(btnXoa);
		btnPanel.add(btnSua);
		btnPanel.add(btnThem);

		panel.add(lblTitle, BorderLayout.WEST);
		panel.add(btnPanel, BorderLayout.EAST);

		return panel;
	}

	private JPanel createTablePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true), new EmptyBorder(16, 16, 16, 16)));

		model = new DefaultTableModel(
				new String[] { "Mã bàn", "Tên bàn", "Loại bàn", "Vị trí", "Sức chứa", "Trạng thái" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tblBanAn = new JTable(model);
		tblBanAn.setRowHeight(30);
		tblBanAn.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblBanAn.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
		tblBanAn.setFont(new Font("Segoe UI", Font.PLAIN, 13));

		tblBanAn.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				BanAn ban = getBanDangChon();
				if (ban != null) {
					hienThiChiTietBan(ban);
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(tblBanAn);

		panel.add(scrollPane, BorderLayout.CENTER);
		return panel;
	}

	private JPanel createDetailPanel() {
		JPanel panel = new JPanel(new BorderLayout(16, 16));
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true), new EmptyBorder(16, 16, 16, 16)));

		JLabel lblTitle = new JLabel("Chi tiết bàn ăn");
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

		JPanel infoPanel = new JPanel(new GridLayout(3, 2, 12, 10));
		infoPanel.setOpaque(false);

		lblMaBan = new JLabel("-");
		lblTenBan = new JLabel("-");
		lblLoaiBan = new JLabel("-");
		lblTrangThai = new JLabel("-");
		lblTang = new JLabel("-");
		lblViTri = new JLabel("-");
		lblSucChua = new JLabel("-");

		infoPanel.add(createInfoItem("Mã bàn:", lblMaBan));
		infoPanel.add(createInfoItem("Loại bàn:", lblLoaiBan));
		infoPanel.add(createInfoItem("Trạng thái:", lblTrangThai));
		infoPanel.add(createInfoItem("Tên bàn:", lblTenBan));
		infoPanel.add(createInfoItem("Vị trí:", lblViTri));
		infoPanel.add(createInfoItem("Sức chứa:", lblSucChua));

		txtMoTaChiTiet = new JTextArea(4, 20);
		txtMoTaChiTiet.setLineWrap(true);
		txtMoTaChiTiet.setWrapStyleWord(true);
		txtMoTaChiTiet.setEditable(false);

		JPanel moTaPanel = new JPanel(new BorderLayout(4, 4));
		moTaPanel.setOpaque(false);
		JLabel lbMoTa = new JLabel("Mô tả:");
		lbMoTa.setFont(new Font("Segoe UI", Font.BOLD, 13));
		moTaPanel.add(lbMoTa, BorderLayout.NORTH);
		moTaPanel.add(new JScrollPane(txtMoTaChiTiet), BorderLayout.CENTER);

		panel.add(lblTitle, BorderLayout.NORTH);
		panel.add(infoPanel, BorderLayout.CENTER);
		panel.add(moTaPanel, BorderLayout.SOUTH);

		return panel;
	}

	private JPanel createInfoItem(String title, JLabel valueLabel) {
		JPanel p = new JPanel(new BorderLayout(4, 4));
		p.setOpaque(false);

		JLabel lb = new JLabel(title);
		lb.setFont(new Font("Segoe UI", Font.BOLD, 13));
		valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

		p.add(lb, BorderLayout.NORTH);
		p.add(valueLabel, BorderLayout.CENTER);
		return p;
	}

	private void loadTable() {
		model.setRowCount(0);
		dsBanAn = dao.getAllBanAn();

		for (BanAn ban : dsBanAn) {
			model.addRow(new Object[] { ban.getMaBan(), ban.getTenBan(),
					ban.getLoaiBan() != null ? ban.getLoaiBan().getTenLB() : "", ban.getViTri(), ban.getSucChua(),
					ban.getTrangThai() });
		}

		if (!dsBanAn.isEmpty()) {
			tblBanAn.setRowSelectionInterval(0, 0);
			hienThiChiTietBan(dsBanAn.get(0));
		}
	}

	private BanAn getBanDangChon() {
		int row = tblBanAn.getSelectedRow();
		if (row < 0 || row >= dsBanAn.size())
			return null;
		return dsBanAn.get(row);
	}

	private void hienThiChiTietBan(BanAn ban) {
		lblMaBan.setText(ban.getMaBan() != null ? ban.getMaBan() : "-");
		lblLoaiBan.setText(ban.getLoaiBan() != null ? ban.getLoaiBan().getTenLB() : "-");
		lblTrangThai.setText(ban.getTrangThai() != null ? ban.getTrangThai() : "-");
		lblTenBan.setText(ban.getTenBan() != null ? ban.getTenBan() : "-");
		lblViTri.setText(ban.getViTri() != null ? ban.getViTri() : "-");
		lblSucChua.setText(String.valueOf(ban.getSucChua()));
		txtMoTaChiTiet.setText(ban.getMoTa() != null ? ban.getMoTa() : "");
	}

	private void moDialogThem() {
		Window window = SwingUtilities.getWindowAncestor(this);
		Frame owner = window instanceof Frame ? (Frame) window : null;

		BanAnDialog dialog = new BanAnDialog(owner, dao, null);
		dialog.setVisible(true);

		if (dialog.isSucceeded()) {
			loadTable();
		}
	}

	private void moDialogSua() {
		BanAn ban = getBanDangChon();
		if (ban == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn cần sửa.");
			return;
		}

		Window window = SwingUtilities.getWindowAncestor(this);
		Frame owner = window instanceof Frame ? (Frame) window : null;

		BanAnDialog dialog = new BanAnDialog(owner, dao, ban);
		dialog.setVisible(true);

		if (dialog.isSucceeded()) {
			loadTable();
		}
	}

	private void xoaMemBanAn() {
		BanAn ban = getBanDangChon();
		if (ban == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn cần xóa.");
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận ngưng sử dụng bàn " + ban.getMaBan() + "?",
				"Xóa mềm bàn", JOptionPane.YES_NO_OPTION);

		if (confirm != JOptionPane.YES_OPTION)
			return;

		if (dao.xoaMemBanAn(ban.getMaBan())) {
			JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái ngưng sử dụng.");
			loadTable();
		} else {
			JOptionPane.showMessageDialog(this, "Xóa mềm thất bại.");
		}
	}

	static class BanAnDialog extends JDialog {

		private final BanAnDAO dao;
		private final BanAn banSua;
		private boolean succeeded = false;

		private JTextField txtMaBan;
		private JComboBox<LoaiBan> cboLoaiBan;
		private JComboBox<String> cboTrangThai;
		private JTextField txtTenBan;
		private JTextField txtViTri;
		private JTextField txtSucChua;
		private JTextArea txtMoTa;

		public BanAnDialog(Frame owner, BanAnDAO dao, BanAn banSua) {
			super(owner, true);
			this.dao = dao;
			this.banSua = banSua;

			setTitle(banSua == null ? "Thêm bàn ăn" : "Sửa bàn ăn");
			initUI();

			if (banSua != null) {
				doDuLieuLenForm();
			}

			pack();
			setSize(800, 420);
			setLocationRelativeTo(owner);
		}

		public boolean isSucceeded() {
			return succeeded;
		}

		private void initUI() {
			JPanel root = new JPanel(new BorderLayout(16, 16));
			root.setBorder(new EmptyBorder(20, 20, 20, 20));
			root.setBackground(Color.WHITE);

			JPanel fields = new JPanel(new GridLayout(3, 3, 12, 12));
			fields.setOpaque(false);

			txtMaBan = new JTextField();
			txtTenBan = new JTextField();
			cboLoaiBan = new JComboBox<>();
			loadLoaiBanToCombo();
			cboLoaiBan.setRenderer(new DefaultListCellRenderer() {
				@Override
				public Component getListCellRendererComponent(JList<?> list, Object value, int index,
						boolean isSelected, boolean cellHasFocus) {
					super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					if (value instanceof LoaiBan) {
						LoaiBan lb = (LoaiBan) value;
						setText(lb.getMaLB() + " - " + lb.getTenLB() + " (" + lb.getSoGhe() + " ghế)");
					} else {
						setText("");
					}
					return this;
				}
			});

			cboTrangThai = new JComboBox<>(new String[] { "Trống", "Đang phục vụ", "Đã đặt", "Ngưng sử dụng" });

			txtViTri = new JTextField();
			txtSucChua = new JTextField();
			txtMoTa = new JTextArea(4, 20);
			txtMoTa.setLineWrap(true);
			txtMoTa.setWrapStyleWord(true);

			fields.add(createField("Mã bàn", txtMaBan));
			fields.add(createField("Loại bàn", cboLoaiBan));
			fields.add(createField("Trạng thái", cboTrangThai));

			fields.add(createField("Tên bàn", txtTenBan));
			fields.add(createField("Vị trí", txtViTri));
			fields.add(createField("Sức chứa", txtSucChua));

			JPanel moTaPanel = new JPanel(new BorderLayout(6, 6));
			moTaPanel.setOpaque(false);
			JLabel lbMoTa = new JLabel("Mô tả");
			lbMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
			moTaPanel.add(lbMoTa, BorderLayout.NORTH);
			moTaPanel.add(new JScrollPane(txtMoTa), BorderLayout.CENTER);

			JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
			btnPanel.setOpaque(false);

			JButton btnHuy = new JButton("Hủy");
			JButton btnLuu = new JButton(banSua == null ? "Thêm" : "Lưu");

			btnHuy.addActionListener(e -> dispose());
			btnLuu.addActionListener(e -> luuBan());

			btnPanel.add(btnHuy);
			btnPanel.add(btnLuu);

			root.add(fields, BorderLayout.NORTH);
			root.add(moTaPanel, BorderLayout.CENTER);
			root.add(btnPanel, BorderLayout.SOUTH);

			setContentPane(root);
		}

		private JPanel createField(String label, JComponent comp) {
			JPanel p = new JPanel(new BorderLayout(6, 6));
			p.setOpaque(false);

			JLabel lb = new JLabel(label);
			lb.setFont(new Font("Segoe UI", Font.PLAIN, 13));

			p.add(lb, BorderLayout.NORTH);
			p.add(comp, BorderLayout.CENTER);

			return p;
		}

		private void loadLoaiBanToCombo() {
			List<LoaiBan> dsLoaiBan = dao.getAllLoaiBan();
			DefaultComboBoxModel<LoaiBan> model = new DefaultComboBoxModel<>();
			for (LoaiBan lb : dsLoaiBan) {
				model.addElement(lb);
			}
			cboLoaiBan.setModel(model);
		}

		private void doDuLieuLenForm() {
			txtMaBan.setText(banSua.getMaBan());
			txtMaBan.setEditable(false);

			txtTenBan.setText(banSua.getTenBan() != null ? banSua.getTenBan() : "");
			cboTrangThai.setSelectedItem(banSua.getTrangThai());
			txtViTri.setText(banSua.getViTri() != null ? banSua.getViTri() : "");
			txtSucChua.setText(String.valueOf(banSua.getSucChua()));
			txtMoTa.setText(banSua.getMoTa() != null ? banSua.getMoTa() : "");
			if (banSua.getLoaiBan() != null) {
				for (int i = 0; i < cboLoaiBan.getItemCount(); i++) {
					LoaiBan lb = cboLoaiBan.getItemAt(i);
					if (lb.getMaLB().equals(banSua.getLoaiBan().getMaLB())) {
						cboLoaiBan.setSelectedIndex(i);
						break;
					}
				}
			}
		}

		private void luuBan() {
			try {
				BanAn ban = layDuLieuForm();

				if (banSua == null) {
					if (dao.tonTaiMaBan(ban.getMaBan())) {
						JOptionPane.showMessageDialog(this, "Mã bàn đã tồn tại.");
						return;
					}

					if (dao.themBanAn(ban)) {
						JOptionPane.showMessageDialog(this, "Thêm bàn ăn thành công.");
						succeeded = true;
						dispose();
					} else {
						JOptionPane.showMessageDialog(this, "Thêm bàn ăn thất bại.");
					}
				} else {
					if (dao.suaBanAn(ban)) {
						JOptionPane.showMessageDialog(this, "Cập nhật bàn ăn thành công.");
						succeeded = true;
						dispose();
					} else {
						JOptionPane.showMessageDialog(this, "Cập nhật bàn ăn thất bại.");
					}
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
		}

		private BanAn layDuLieuForm() throws Exception {
		    String maBan = txtMaBan.getText().trim();
		    String tenBan = txtTenBan.getText().trim();
		    LoaiBan loaiBan = (LoaiBan) cboLoaiBan.getSelectedItem();
		    String trangThai = cboTrangThai.getSelectedItem().toString();
		    String viTri = txtViTri.getText().trim();
		    String sucChuaText = txtSucChua.getText().trim();

		    if (maBan.isEmpty() || tenBan.isEmpty() || loaiBan == null || viTri.isEmpty() || sucChuaText.isEmpty()) {
		        throw new Exception("Vui lòng nhập đầy đủ thông tin bắt buộc.");
		    }

		    int sucChua;
		    try {
		        sucChua = Integer.parseInt(sucChuaText);
		    } catch (Exception e) {
		        throw new Exception("Sức chứa phải là số nguyên.");
		    }

		    if (sucChua <= 0) {
		        throw new Exception("Sức chứa phải lớn hơn 0.");
		    }

		    BanAn ban = new BanAn();
		    ban.setMaBan(maBan);
		    ban.setTenBan(tenBan);
		    ban.setLoaiBan(loaiBan);
		    ban.setTrangThai(trangThai);
		    ban.setViTri(viTri);
		    ban.setSucChua(sucChua);
		    ban.setMoTa(txtMoTa.getText().trim());

		    return ban;
		}
	}
}
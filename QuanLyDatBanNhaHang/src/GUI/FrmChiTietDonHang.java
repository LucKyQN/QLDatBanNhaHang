package GUI;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import DAO.HoaDonDAO;
import Model.MonAnModel;

public class FrmChiTietDonHang extends JDialog {

    private static final Color RED_MAIN = new Color(220, 38, 38);
    private static final Color BORDER_CLR = new Color(230, 230, 230);
    private static final Color TEXT_DARK = new Color(40, 40, 40);

    private String maBan; 
    private String tenBan;
    private String maHDHienTai = null; 
    private double tongTienBill = 0; 
    
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    
    private JLabel lblTotalDisplay; 
    private DefaultTableModel model;
    private JTable table;

    public FrmChiTietDonHang(JFrame parent, String maBan, String tenBan) {
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
        
        JPanel body = new JPanel(new BorderLayout(20, 0));
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        body.add(createBillArea(), BorderLayout.CENTER);
        body.add(createActionArea(), BorderLayout.EAST);
        
        root.add(body, BorderLayout.CENTER);
        setContentPane(root);

        loadDataBill();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel title = new JLabel("Đơn Hàng" + tenBan);
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
        
        JLabel lblTitle = new JLabel("Chi tiết đơn hàng");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pnlBill.add(lblTitle, BorderLayout.NORTH);

        String[] columns = {"Tên món", "SL", "Đơn giá", "Thành tiền", "Trạng thái", "maMonAn_Hidden"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { 
                return column == 4;
            }
        };
        
        table = new JTable(model);
        table.setRowHeight(35);
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        
        // Ẩn cột maMonAn đi
        table.getColumnModel().getColumn(5).setMinWidth(0);
        table.getColumnModel().getColumn(5).setMaxWidth(0);
        table.getColumnModel().getColumn(5).setWidth(0);

        // Gắn ComboBox vào cột "Trạng thái"
        JComboBox<String> cboTrangThai = new JComboBox<>(new String[]{"Chưa lên", "Đã lên", "Mang về", "Hủy"});
        cboTrangThai.setFont(new Font("Segoe UI", Font.BOLD, 13));
        TableColumn statusColumn = table.getColumnModel().getColumn(4);
        statusColumn.setCellEditor(new DefaultCellEditor(cboTrangThai));

        model.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 4) {
                int row = e.getFirstRow();
                String trangThaiMoi = (String) model.getValueAt(row, 4);
                String maMonAn = (String) model.getValueAt(row, 5);
                

                if (maHDHienTai != null && maMonAn != null) {
                    hoaDonDAO.capNhatTrangThaiMon(maHDHienTai, maMonAn, trangThaiMoi);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
        scroll.getViewport().setBackground(Color.WHITE);
        
        pnlBill.add(scroll, BorderLayout.CENTER);
        return pnlBill;
    }

    private void loadDataBill() {
        model.setRowCount(0);
        tongTienBill = 0;
        
        maHDHienTai = hoaDonDAO.getMaHoaDonChuaThanhToanCuaBan(this.maBan);
        
        if (maHDHienTai == null) {
            lblTotalDisplay.setText("0 đ");
            return;
        }
        
        List<MonAnModel> dsMon = hoaDonDAO.getChiTietHoaDon(maHDHienTai);
        
        for (MonAnModel mon : dsMon) {
            model.addRow(new Object[]{
                mon.tenMonAn, 
                mon.soLuong, 
                formatMoney(mon.giaBan), 
                formatMoney(mon.thanhTien),
                mon.trangThaiPhucVu,
                mon.maMonAn
            });
            tongTienBill += mon.thanhTien;
        }
        
        lblTotalDisplay.setText(formatMoney(tongTienBill));
    }

    private JPanel createActionArea() {
        JPanel pnlAction = new JPanel();
        pnlAction.setLayout(new BoxLayout(pnlAction, BoxLayout.Y_AXIS));
        pnlAction.setBackground(Color.WHITE);
        pnlAction.setPreferredSize(new Dimension(300, 0));
        pnlAction.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Tạm tính
        JPanel rowTamTinh = new JPanel(new BorderLayout());
        rowTamTinh.setBackground(Color.WHITE);
        rowTamTinh.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel lblT = new JLabel("Tạm tính:");
        lblT.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        lblTotalDisplay = new JLabel("0 đ");
        lblTotalDisplay.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotalDisplay.setForeground(RED_MAIN);
        
        rowTamTinh.add(lblT, BorderLayout.WEST);
        rowTamTinh.add(lblTotalDisplay, BorderLayout.EAST);
        
        pnlAction.add(rowTamTinh);
        pnlAction.add(Box.createVerticalStrut(30));

        // Nút chức năng phụ
        JButton btnThemMon = createOutlineButton("Gọi thêm món");
        btnThemMon.addActionListener(e -> {
            new FrmGoiMon((JFrame)SwingUtilities.getWindowAncestor(this), maBan, tenBan, 0).setVisible(true);
            loadDataBill();
        });
        
        pnlAction.add(btnThemMon);
        pnlAction.add(Box.createVerticalStrut(15));
        
        pnlAction.add(createOutlineButton("Chuyển bàn"));
        pnlAction.add(Box.createVerticalStrut(15));
        
        pnlAction.add(createOutlineButton("Gộp bàn"));
        pnlAction.add(Box.createVerticalGlue());

        // Nút Yêu cầu thanh toán to đùng
        JButton btnYeuCauTT = new JButton("YÊU CẦU THANH TOÁN");
        btnYeuCauTT.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnYeuCauTT.setBackground(RED_MAIN);
        btnYeuCauTT.setForeground(Color.WHITE);
        btnYeuCauTT.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        btnYeuCauTT.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnYeuCauTT.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnYeuCauTT.setFocusPainted(false);
        
        btnYeuCauTT.addActionListener(e -> {
            if (maHDHienTai == null || tongTienBill == 0) {
                JOptionPane.showMessageDialog(this, "Bàn chưa có món nào để thanh toán!", "Lưu ý", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Hỏi xác nhận
            int c = JOptionPane.showConfirmDialog(this, "Gửi yêu cầu thanh toán cho " + tenBan + "?\nBàn sẽ chuyển sang trạng thái 'Chờ thanh toán'.", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                if (hoaDonDAO.yeuCauThanhToan(maHDHienTai, maBan)) {
                    JOptionPane.showMessageDialog(this, "Đã gửi yêu cầu tới Thu ngân!");
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi hệ thống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        pnlAction.add(btnYeuCauTT);
        return pnlAction;
    }
    
    private JButton createOutlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE);
        btn.setForeground(TEXT_DARK);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return btn;
    }

    private String formatMoney(double amount) {
        return String.format("%,.0f đ", amount).replace(',', '.');
    }
}
package GUI;

import DAO.PhucVuServiceMemory;

import javax.swing.*;


public final class FrmPhucVuDemo {

    private FrmPhucVuDemo() {
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() ->
                new FrmPhucVu("Demo (không DB)", PhucVuServiceMemory.demo()).setVisible(true));
    }
}

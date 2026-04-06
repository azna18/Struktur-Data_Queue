import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class AntrianKlinik {

    // ===== DATA =====
    private final LinkedList<String[]> queue = new LinkedList<>();
    private int nomorUrut = 1;

    // ===== KOMPONEN UI =====
    private final JFrame frame;
    private JLabel labelSekarang, labelTotal;
    private DefaultListModel<String> listModel;

    // ===== WARNA =====
    private static final Color NAVY  = new Color(47, 65, 86);
    private static final Color TEAL  = new Color(86, 124, 141);
    private static final Color SKY   = new Color(200, 217, 230);
    private static final Color BEIGE = new Color(245, 239, 235);

    public AntrianKlinik() {
        frame = buatFrame();
        frame.add(buatHeader(),  BorderLayout.NORTH);
        frame.add(buatListPanel(), BorderLayout.WEST);
        frame.add(buatCenter(),  BorderLayout.CENTER);
        frame.add(buatTombol(),  BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    // ===== BUILDER: FRAME =====
    private JFrame buatFrame() {
        JFrame f = new JFrame("Sistem Antrian Klinik");
        f.setSize(950, 550);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.setLayout(new BorderLayout(10, 10));
        return f;
    }

    // ===== BUILDER: HEADER =====
    private JPanel buatHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NAVY);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel judul = buatLabel("FARANNISA DENTAL CLINIC", 24, Font.BOLD, Color.WHITE);
        labelTotal   = buatLabel("Total: 0", 16, Font.PLAIN, Color.WHITE);

        header.add(judul,       BorderLayout.WEST);
        header.add(labelTotal,  BorderLayout.EAST);
        return header;
    }

    // ===== BUILDER: PANEL TENGAH (nomor dipanggil) =====
    private JPanel buatCenter() {
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(SKY);
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        labelSekarang = buatLabel("Belum ada panggilan", 32, Font.BOLD, new Color(40, 40, 40));
        center.add(labelSekarang);
        return center;
    }

    // ===== BUILDER: PANEL LIST =====
    private JPanel buatListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(BEIGE);
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel judulList = buatLabel("Daftar Antrian", 16, Font.BOLD, Color.BLACK);

        listModel = new DefaultListModel<>();
        JList<String> list = new JList<>(listModel);
        list.setFont(new Font("Arial", Font.PLAIN, 14));
        list.setSelectionBackground(TEAL);

        panel.add(judulList,           BorderLayout.NORTH);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        return panel;
    }

    // ===== BUILDER: TOMBOL =====
    private JPanel buatTombol() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBackground(BEIGE);

        JButton btnAmbil   = buatTombolGaya("Ambil Antrian");
        JButton btnPanggil = buatTombolGaya("Panggil");
        JButton btnReset   = buatTombolGaya("Reset");

        // --- Logika Ambil ---
        btnAmbil.addActionListener(e -> {
            String nama = JOptionPane.showInputDialog(frame, "Masukkan Nama:");
            if (nama != null && !nama.isBlank()) {
                queue.add(new String[]{ String.valueOf(nomorUrut), nama.trim() });
                nomorUrut++;
                refreshUI();
            }
        });

        // --- Logika Panggil ---
        btnPanggil.addActionListener(e -> {
            if (!queue.isEmpty()) {
                String[] data = queue.removeFirst();
                labelSekarang.setText("Nomor " + data[0] + " - " + data[1]);
                panggilTTS(data[0], data[1]);
                refreshUI();
            } else {
                JOptionPane.showMessageDialog(frame, "Antrian kosong!");
            }
        });

        // --- Logika Reset ---
        btnReset.addActionListener(e -> {
            int konfirmasi = JOptionPane.showConfirmDialog(
                frame, "Reset semua antrian?", "Konfirmasi", JOptionPane.YES_NO_OPTION
            );
            if (konfirmasi == JOptionPane.YES_OPTION) {
                queue.clear();
                nomorUrut = 1;
                labelSekarang.setText("Belum ada panggilan");
                refreshUI();
            }
        });

        panel.add(btnAmbil);
        panel.add(btnPanggil);
        panel.add(btnReset);
        return panel;
    }

    // ===== HELPER: TTS =====
    private void panggilTTS(String nomor, String nama) {
        String teks = "Nomor antrian " + nomor + " , atas nama " + nama
                    + " , silakan memasuki ruang pemeriksaan";
        try {
            new ProcessBuilder("python", "tts.py", teks)
                .redirectErrorStream(true)
                .start();
        } catch (Exception ex) {
            System.err.println("TTS gagal: " + ex.getMessage());
        }
    }

    // ===== HELPER: Refresh tampilan list & total =====
    private void refreshUI() {
        labelTotal.setText("Total: " + queue.size());
        listModel.clear();
        for (String[] d : queue) {
            listModel.addElement("Nomor " + d[0] + " - " + d[1]);
        }
    }

    // ===== HELPER: Buat JLabel seragam =====
    private JLabel buatLabel(String teks, int ukuran, int style, Color warna) {
        JLabel label = new JLabel(teks);
        label.setFont(new Font("Arial", style, ukuran));
        label.setForeground(warna);
        return label;
    }

    // ===== HELPER: Buat tombol bergaya =====
    private JButton buatTombolGaya(String teks) {
        JButton btn = new JButton(teks);
        btn.setBackground(TEAL);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(180, 45));
        return btn;
    }

    // ===== MAIN =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AntrianKlinik::new); // ✅ Diperbaiki
    }
}

import tkinter as tk
from tkinter import messagebox, simpledialog
from gtts import gTTS
import pygame
import tempfile
import threading

# ===== KONSTANTA =====
NAVY  = "#2F4156"
TEAL  = "#567C8D"
SKY   = "#C8D9E6"
BEIGE = "#F5EFEB"

FONT_JUDUL  = ("Arial", 22, "bold")
FONT_BESAR  = ("Arial", 34, "bold")
FONT_NORMAL = ("Arial", 14)
FONT_BOLD   = ("Arial", 14, "bold")
FONT_LIST   = ("Arial", 12)
FONT_TOMBOL = ("Arial", 11, "bold")


class AntrianKlinik:

    def __init__(self, root: tk.Tk):
        self.root  = root
        self.queue = []
        self.nomor = 1

        pygame.mixer.init()
        self._setup_window()
        self._buat_header()
        self._buat_main()
        self._buat_tombol()

    # ===== SETUP =====
    def _setup_window(self):
        self.root.title("Sistem Antrian Klinik")
        self.root.geometry("950x550")
        self.root.configure(bg=NAVY)

    # ===== HEADER =====
    def _buat_header(self):
        header = tk.Frame(self.root, bg=NAVY, padx=20, pady=15)
        header.pack(fill="x")

        tk.Label(header, text="FARANNISA DENTAL CLINIC",
                 bg=NAVY, fg="white", font=FONT_JUDUL).pack(side="left")

        self.label_total = tk.Label(header, text="Total: 0",
                                    bg=NAVY, fg="white", font=FONT_NORMAL)
        self.label_total.pack(side="right")

    # ===== MAIN CONTAINER =====
    def _buat_main(self):
        main = tk.Frame(self.root, bg=NAVY)
        main.pack(fill="both", expand=True, padx=10, pady=10)

        self._buat_panel_kiri(main)
        self._buat_panel_tengah(main)

    # ===== LEFT PANEL =====
    def _buat_panel_kiri(self, parent):
        left = tk.Frame(parent, bg=BEIGE, padx=15, pady=15)
        left.pack(side="left", fill="y")

        tk.Label(left, text="Daftar Antrian",
                 bg=BEIGE, font=FONT_BOLD).pack(anchor="w", pady=(0, 10))

        self.listbox = tk.Listbox(left, font=FONT_LIST, bd=0, highlightthickness=0)
        self.listbox.pack(fill="both", expand=True)

    # ===== CENTER PANEL =====
    def _buat_panel_tengah(self, parent):
        center = tk.Frame(parent, bg=SKY)
        center.pack(side="left", expand=True, fill="both", padx=10)

        self.label_sekarang = tk.Label(center, text="Belum ada panggilan",
                                       bg=SKY, fg="#333", font=FONT_BESAR)
        self.label_sekarang.place(relx=0.5, rely=0.5, anchor="center")

    # ===== TOMBOL =====
    def _buat_tombol(self):
        bottom = tk.Frame(self.root, bg=BEIGE, pady=15)
        bottom.pack(fill="x")

        tombol_config = [
            ("Ambil Antrian", self.aksi_ambil),
            ("Panggil",       self.aksi_panggil),
            ("Reset",         self.aksi_reset),
        ]

        for teks, perintah in tombol_config:
            btn = tk.Button(bottom, text=teks, command=perintah,
                            bg=TEAL, fg="white",
                            width=18, height=2,
                            font=FONT_TOMBOL,
                            relief="flat", bd=0)
            btn.pack(side="left", padx=20)

    # ===== AKSI =====
    def aksi_ambil(self):
        nama = simpledialog.askstring("Input", "Masukkan Nama:", parent=self.root)
        if nama and nama.strip():
            self.queue.append((self.nomor, nama.strip()))
            self.nomor += 1
            self._refresh_ui()

    def aksi_panggil(self):
        if not self.queue:
            messagebox.showinfo("Info", "Antrian kosong!", parent=self.root)
            return

        nomor_antri, nama = self.queue.pop(0)
        teks = f"Nomor antrian {nomor_antri}, atas nama {nama}, silakan masuk ke ruang pemeriksaan"

        self.label_sekarang.config(text=f"Nomor {nomor_antri} - {nama}")
        self._refresh_ui()

        threading.Thread(target=self._play_suara, args=(teks,), daemon=True).start()

    def aksi_reset(self):
        konfirmasi = messagebox.askyesno("Konfirmasi", "Reset semua antrian?", parent=self.root)
        if konfirmasi:
            self.queue.clear()
            self.nomor = 1
            self.label_sekarang.config(text="Belum ada panggilan")
            self._refresh_ui()

    # ===== HELPER =====
    def _refresh_ui(self):
        self.label_total.config(text=f"Total: {len(self.queue)}")
        self.listbox.delete(0, tk.END)
        for nomor_antri, nama in self.queue:
            self.listbox.insert(tk.END, f"Nomor {nomor_antri} - {nama}")

    def _play_suara(self, teks: str):
        try:
            tts = gTTS(text=teks, lang='id')
            file_path = tempfile.mktemp(suffix=".mp3")
            tts.save(file_path)
            pygame.mixer.music.load(file_path)
            pygame.mixer.music.play()
        except Exception as e:
            print(f"Error suara: {e}")


# ===== MAIN =====
if __name__ == "__main__":
    root = tk.Tk()
    AntrianKlinik(root)
    root.mainloop()

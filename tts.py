from gtts import gTTS
import sys
import pygame
import tempfile

pygame.mixer.init()

# ambil teks dari Java
text = " ".join(sys.argv[1:])

# buat suara
tts = gTTS(text=text, lang='id')
file_path = tempfile.mktemp(suffix=".mp3")
tts.save(file_path)

# play tanpa popup
pygame.mixer.music.load(file_path)
pygame.mixer.music.play()

# tunggu sampai selesai
while pygame.mixer.music.get_busy():
    continue
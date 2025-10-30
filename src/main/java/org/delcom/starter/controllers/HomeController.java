package org.delcom.starter.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Base64;
import java.util.ArrayList;



@RestController
public class HomeController {

    // ✅ Method bawaan dari starter
    @GetMapping("/")
    public String hello() {
        return "Hay Abdullah, selamat datang di pengembangan aplikasi dengan Spring Boot!";
    }

    @GetMapping("/hello/{name}")
    public String sayHello(@PathVariable String name) {
        return "Hello, " + name + "!";
    }

    // ✅ Method 1: Informasi NIM
    @GetMapping("/informasiNim/{nim}")
    public String informasiNim(@PathVariable String nim) {

        // Mapping prefix ke program studi
        HashMap<String, String> prodi = new HashMap<>();
        prodi.put("11S", "Sarjana Informatika");
        prodi.put("12S", "Sarjana Sistem Informasi");
        prodi.put("14S", "Sarjana Teknik Elektro");
        prodi.put("21S", "Sarjana Manajemen Rekayasa");
        prodi.put("22S", "Sarjana Teknik Metalurgi");
        prodi.put("31S", "Sarjana Teknik Bioproses");
        prodi.put("114", "Diploma 4 Teknologi Rekayasa Perangkat Lunak");
        prodi.put("113", "Diploma 3 Teknologi Informasi");
        prodi.put("133", "Diploma 3 Teknologi Komputer");

        // Ambil prefix pertama
        String prefix = nim.substring(0, 3);
        String angkatan = "20" + nim.substring(3, 5);

        // Buang nol di depan (leading zero)
        int urutInt = Integer.parseInt(nim.substring(nim.length() - 3));
        String urutan = String.valueOf(urutInt);

        // Ambil program studi dari HashMap
        String programStudi = prodi.getOrDefault(prefix, "Program Studi tidak diketahui");

        // Hasil dikembalikan ke browser
        return "<h3>Informasi NIM: " + nim + "</h3>" +
               "<ul>" +
               "<li>Program Studi: " + programStudi + "</li>" +
               "<li>Angkatan: " + angkatan + "</li>" +
               "<li>Urutan: " + urutan + "</li>" +
               "</ul>";
    }

    // ✅ Method 2: Perolehan Nilai (menggunakan input Base64)
    @GetMapping("/perolehanNilai/{strBase64}")
    public String perolehanNilai(@PathVariable String strBase64) {

        try {
            // Decode Base64 menjadi teks input asli
            byte[] decodedBytes = Base64.getDecoder().decode(strBase64);
            String decodedText = new String(decodedBytes);

            // Pisahkan baris-baris input
            String[] lines = decodedText.split("\\r?\\n");

            // Ambil bobot komponen dari 6 baris pertama
            int bobotPA = Integer.parseInt(lines[0].trim());
            int bobotT = Integer.parseInt(lines[1].trim());
            int bobotK = Integer.parseInt(lines[2].trim());
            int bobotP = Integer.parseInt(lines[3].trim());
            int bobotUTS = Integer.parseInt(lines[4].trim());
            int bobotUAS = Integer.parseInt(lines[5].trim());

            // Variabel total dan maksimum nilai untuk setiap kategori
            int totalPA = 0, maxPA = 0;
            int totalT = 0, maxT = 0;
            int totalK = 0, maxK = 0;
            int totalP = 0, maxP = 0;
            int totalUTS = 0, maxUTS = 0;
            int totalUAS = 0, maxUAS = 0;

            // Proses setiap baris nilai
            for (int i = 6; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.equals("---")) break;

                String[] parts = line.split("\\|");
                String simbol = parts[0];
                int maks = Integer.parseInt(parts[1]);
                int nilai = Integer.parseInt(parts[2]);

                switch (simbol) {
                    case "PA": maxPA += maks; totalPA += nilai; break;
                    case "T": maxT += maks; totalT += nilai; break;
                    case "K": maxK += maks; totalK += nilai; break;
                    case "P": maxP += maks; totalP += nilai; break;
                    case "UTS": maxUTS += maks; totalUTS += nilai; break;
                    case "UAS": maxUAS += maks; totalUAS += nilai; break;
                }
            }

            // Hitung rata-rata (dalam persen)
            double rataPA = (maxPA == 0) ? 0 : (totalPA * 100.0 / maxPA);
            double rataT = (maxT == 0) ? 0 : (totalT * 100.0 / maxT);
            double rataK = (maxK == 0) ? 0 : (totalK * 100.0 / maxK);
            double rataP = (maxP == 0) ? 0 : (totalP * 100.0 / maxP);
            double rataUTS = (maxUTS == 0) ? 0 : (totalUTS * 100.0 / maxUTS);
            double rataUAS = (maxUAS == 0) ? 0 : (totalUAS * 100.0 / maxUAS);

            // Pembulatan ke bawah
            int bulatPA = (int) Math.floor(rataPA);
            int bulatT = (int) Math.floor(rataT);
            int bulatK = (int) Math.floor(rataK);
            int bulatP = (int) Math.floor(rataP);
            int bulatUTS = (int) Math.floor(rataUTS);
            int bulatUAS = (int) Math.floor(rataUAS);

            // Hitung kontribusi dan total nilai akhir
            double nilaiPA = (bulatPA / 100.0) * bobotPA;
            double nilaiT = (bulatT / 100.0) * bobotT;
            double nilaiK = (bulatK / 100.0) * bobotK;
            double nilaiP = (bulatP / 100.0) * bobotP;
            double nilaiUTS = (bulatUTS / 100.0) * bobotUTS;
            double nilaiUAS = (bulatUAS / 100.0) * bobotUAS;

            double totalNilai = nilaiPA + nilaiT + nilaiK + nilaiP + nilaiUTS + nilaiUAS;
            String grade = getGrade(totalNilai);

            // Tampilkan hasil dalam HTML
            return "<h3>Perolehan Nilai:</h3>" +
                    "<ul>" +
                    "<li>Partisipatif: " + bulatPA + "/100 (" + String.format("%.2f", nilaiPA) + "/" + bobotPA + ")</li>" +
                    "<li>Tugas: " + bulatT + "/100 (" + String.format("%.2f", nilaiT) + "/" + bobotT + ")</li>" +
                    "<li>Kuis: " + bulatK + "/100 (" + String.format("%.2f", nilaiK) + "/" + bobotK + ")</li>" +
                    "<li>Proyek: " + bulatP + "/100 (" + String.format("%.2f", nilaiP) + "/" + bobotP + ")</li>" +
                    "<li>UTS: " + bulatUTS + "/100 (" + String.format("%.2f", nilaiUTS) + "/" + bobotUTS + ")</li>" +
                    "<li>UAS: " + bulatUAS + "/100 (" + String.format("%.2f", nilaiUAS) + "/" + bobotUAS + ")</li>" +
                    "</ul>" +
                    "<b>Nilai Akhir: " + String.format("%.2f", totalNilai) + "</b><br>" +
                    "<b>Grade: " + grade + "</b>";

        } catch (Exception e) {
            return "❌ Terjadi kesalahan: " + e.getMessage();
        }
    }

    // 🔹 Fungsi bantu konversi nilai ke huruf
    private String getGrade(double nilai) {
        if (nilai >= 79.5) return "A";
        else if (nilai >= 72) return "AB";
        else if (nilai >= 64.5) return "B";
        else if (nilai >= 57) return "BC";
        else if (nilai >= 49.5) return "C";
        else if (nilai >= 34) return "D";
        else return "E";
    }


    // ✅ Method 3: Perbedaan L dan Kebalikannya (dengan Base64)
    @GetMapping("/perbedaanL/{strBase64}")
    public String perbedaanL(@PathVariable String strBase64) {
        try {
            // Decode Base64 ke string asli
            byte[] decodedBytes = Base64.getDecoder().decode(strBase64);
            String decodedText = new String(decodedBytes).trim();

            // Split berdasarkan baris
            String[] lines = decodedText.split("\\r?\\n+");

            // Validasi jumlah baris minimal (harus ada ukuran matriks)
            if (lines.length < 2) {
                return "❌ Format input tidak valid. Pastikan baris pertama adalah ukuran matriks, diikuti data matriks.";
            }

            // Baris pertama = ukuran matriks
            String firstLine = lines[0].trim().replaceAll("[^0-9]", "");
            if (firstLine.isEmpty()) {
                return "❌ Ukuran matriks tidak ditemukan pada baris pertama.";
            }

            int n = Integer.parseInt(firstLine);

            // Buat matriks
            int[][] matrix = new int[n][n];
            int index = 1;

            for (int i = 0; i < n; i++) {
                if (index >= lines.length)
                    return "❌ Baris matriks kurang dari ukuran yang ditentukan.";

                String[] row = lines[index++].trim().split("\\s+");
                if (row.length != n)
                    return "❌ Setiap baris matriks harus memiliki " + n + " elemen.";

                for (int j = 0; j < n; j++) {
                    matrix[i][j] = Integer.parseInt(row[j]);
                }
            }

            // Hitung nilai
            int nilaiL = 0, nilaiKebalikanL = 0, nilaiTengah = 0;

            // Nilai L (kolom pertama + baris terakhir)
            for (int i = 0; i < n; i++) nilaiL += matrix[i][0];
            for (int j = 1; j < n - 1; j++) nilaiL += matrix[n - 1][j];

            // Nilai kebalikan L (baris pertama + kolom terakhir)
            for (int j = 1; j < n; j++) nilaiKebalikanL += matrix[0][j];
            for (int i = 1; i < n; i++) nilaiKebalikanL += matrix[i][n - 1];

            // Nilai tengah
            if (n % 2 == 1) {
                nilaiTengah = matrix[n / 2][n / 2];
            } else {
                int a = n / 2 - 1, b = n / 2;
                nilaiTengah = matrix[a][a] + matrix[a][b] + matrix[b][a] + matrix[b][b];
            }

            int perbedaan = Math.abs(nilaiL - nilaiKebalikanL);
            int dominan = (nilaiL == nilaiKebalikanL) ? nilaiTengah
                    : (nilaiL > nilaiKebalikanL ? nilaiL : nilaiKebalikanL);

            return "<h3>Hasil Perhitungan Matriks</h3>" +
                    "<ul>" +
                    "<li>Nilai L: " + nilaiL + "</li>" +
                    "<li>Nilai Kebalikan L: " + nilaiKebalikanL + "</li>" +
                    "<li>Nilai Tengah: " + nilaiTengah + "</li>" +
                    "<li>Perbedaan: " + perbedaan + "</li>" +
                    "<li>Dominan: " + dominan + "</li>" +
                    "</ul>";

        } catch (Exception e) {
            return "❌ Terjadi kesalahan: " + e.getMessage() +
                    "<br><br>Pastikan input Base64 benar-benar berasal dari file plain.txt berformat:<br>" +
                    "<pre>3\n1 2 3\n4 5 6\n7 8 9</pre>";
        }
    }

        // ✅ Method 4: PalingTer (menggunakan input Base64)
    @GetMapping("/palingTer/{strBase64}")
    public String palingTer(@PathVariable String strBase64) {
        try {
            // Decode Base64 → teks asli
            byte[] decodedBytes = Base64.getDecoder().decode(strBase64);
            String decodedText = new String(decodedBytes).trim();

            // Pisahkan baris input
            String[] lines = decodedText.split("\\r?\\n");
            ArrayList<Integer> daftarNilai = new ArrayList<>();

            // Kumpulkan nilai hingga tanda ---
            for (String line : lines) {
                line = line.trim();
                if (line.equals("---")) break;
                if (!line.isEmpty()) daftarNilai.add(Integer.parseInt(line));
            }

            if (daftarNilai.isEmpty()) {
                return "❌ Tidak ada data nilai yang valid.";
            }

            // Gunakan HashMap untuk menghitung frekuensi
            HashMap<Integer, Integer> frekuensi = new HashMap<>();
            for (int nilai : daftarNilai) {
                frekuensi.put(nilai, frekuensi.getOrDefault(nilai, 0) + 1);
            }

            // Cari nilai tertinggi & terendah
            int nilaiTertinggi = daftarNilai.get(0);
            int nilaiTerendah = daftarNilai.get(0);
            for (int nilai : daftarNilai) {
                if (nilai > nilaiTertinggi) nilaiTertinggi = nilai;
                if (nilai < nilaiTerendah) nilaiTerendah = nilai;
            }

            // Cari nilai dengan frekuensi terbanyak & tersedikit
            int nilaiTerbanyak = nilaiTertinggi;
            int nilaiTerdikit = nilaiTerendah;
            int freqTerbanyak = 0;
            int freqTerdikit = Integer.MAX_VALUE;

            for (var entry : frekuensi.entrySet()) {
                int nilai = entry.getKey();
                int count = entry.getValue();
                if (count > freqTerbanyak) {
                    freqTerbanyak = count;
                    nilaiTerbanyak = nilai;
                }
                if (count < freqTerdikit) {
                    freqTerdikit = count;
                    nilaiTerdikit = nilai;
                }
            }

            // Hitung jumlah tertinggi & terendah (nilai * frekuensi)
            int jumlahTertinggi = 0;
            int jumlahTerendah = Integer.MAX_VALUE;
            int nilaiJumlahTertinggi = 0;
            int nilaiJumlahTerendah = 0;

            for (var entry : frekuensi.entrySet()) {
                int nilai = entry.getKey();
                int jumlah = nilai * entry.getValue();
                if (jumlah > jumlahTertinggi) {
                    jumlahTertinggi = jumlah;
                    nilaiJumlahTertinggi = nilai;
                }
                if (jumlah < jumlahTerendah) {
                    jumlahTerendah = jumlah;
                    nilaiJumlahTerendah = nilai;
                }
            }

            // Hasil dalam format HTML
            return "<h3>📊 Hasil Analisis Nilai</h3>" +
                    "<ul>" +
                    "<li>Tertinggi: " + nilaiTertinggi + "</li>" +
                    "<li>Terendah: " + nilaiTerendah + "</li>" +
                    "<li>Terbanyak: " + nilaiTerbanyak + " (" + freqTerbanyak + "x)</li>" +
                    "<li>Tersedikit: " + nilaiTerdikit + " (" + freqTerdikit + "x)</li>" +
                    "<li>Jumlah Tertinggi: " + nilaiJumlahTertinggi + " * " + frekuensi.get(nilaiJumlahTertinggi) + " = " + jumlahTertinggi + "</li>" +
                    "<li>Jumlah Terendah: " + nilaiJumlahTerendah + " * " + frekuensi.get(nilaiJumlahTerendah) + " = " + jumlahTerendah + "</li>" +
                    "</ul>";

        } catch (Exception e) {
            return "❌ Terjadi kesalahan: " + e.getMessage();
        }
    }
    }
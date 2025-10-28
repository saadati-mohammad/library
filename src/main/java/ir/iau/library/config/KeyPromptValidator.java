package ir.iau.library.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Scanner;

public class KeyPromptValidator {

    public static void verify() {
        try {
            System.out.print("Enter key: ");
            Scanner sc = new Scanner(System.in);
            String provided = sc.nextLine().trim();
            if (provided.isEmpty()) {
//                System.out.println("❌ No key provided — exiting.");
                System.exit(1);
            }

            String serial = readBaseboardSerial().trim();
            if (serial.isEmpty()) {
//                System.out.println("❌ Couldn't read motherboard serial locally — exiting.");
                System.exit(1);
            }

            String expected = computeKeyFromSerial(serial);
            if (expected.equalsIgnoreCase(provided)) {
//                System.out.println("✅ Key valid — starting application.");
                return;
            } else {
//                System.out.println("❌ Invalid key — exiting.");
                System.exit(1);
            }
        } catch (Exception e) {
//            e.printStackTrace();
            System.exit(2);
        }
    }

    // same algorithm as the .bat generator
    private static String computeKeyFromSerial(String serial) throws Exception {
        // sha-256
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(serial.getBytes("UTF-8"));
        // base64
        String b64 = Base64.getEncoder().encodeToString(digest);
        // normalize: replace +/ and remove = and non-alphanumerics
        String k = b64.replace('+', 'X').replace('/', 'Y').replace("=", "");
        k = k.replaceAll("[^A-Za-z0-9]", "");
        if (k.length() >= 15) {
            k = k.substring(0, 15);
        }
        return k.toUpperCase();
    }

    // read baseboard serial via PowerShell CIM; fallback to WMIC
    private static String readBaseboardSerial() {
        try {
            Process p = Runtime.getRuntime().exec(new String[] {
                    "powershell", "-Command",
                    "(Get-CimInstance Win32_BaseBoard).SerialNumber"
            });
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"));
            String line = r.readLine();
            r.close();
            if (line != null) return line.trim();
        } catch (Exception ignored) {}

        try {
            Process p2 = Runtime.getRuntime().exec(new String[] {"cmd", "/c", "wmic baseboard get serialnumber"});
            BufferedReader r2 = new BufferedReader(new InputStreamReader(p2.getInputStream(), "UTF-8"));
            String l;
            while ((l = r2.readLine()) != null) {
                l = l.trim();
                if (l.isEmpty() || l.equalsIgnoreCase("SerialNumber")) continue;
                r2.close();
                return l;
            }
            r2.close();
        } catch (Exception ignored) {}

        return "";
    }
}

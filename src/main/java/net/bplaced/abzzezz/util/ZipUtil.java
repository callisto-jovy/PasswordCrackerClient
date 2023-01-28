package net.bplaced.abzzezz.util;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;

import java.io.*;
import java.util.Base64;
import java.util.List;

public class ZipUtil {

    public static File createTempZip(final String data) throws IOException {
        final byte[] bytes = Base64.getDecoder().decode(data);
        final File file = File.createTempFile("zipCrackerTemp", ".zip");
        final FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.close();
        return file;
    }

    public static boolean tryReadByteStream(final char[] password, final File file) {
        try (final ZipFile zipFile = new ZipFile(file)) {
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password);
            }

            List<FileHeader> fileHeaders = zipFile.getFileHeaders();

            for (FileHeader flHeader : fileHeaders) {

                final InputStream is = zipFile.getInputStream(flHeader);
                byte[] b = new byte[4 * 4096];
                while (is.read(b) != -1) {
                    // Verify password.
                }
                is.close();
            }


        } catch (IOException e) {
            //TODO: Remove in production.
           // e.printStackTrace();
            return false;
        }
        return true;
    }


}

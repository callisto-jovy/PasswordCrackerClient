package net.bplaced.abzzezz.util;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ZipCracker {

    //TODO: Move to input
    public static final String ZIP_FILE_PATH = "";

    public String bruteForcePassword(int length, char[] password, int position) {
        String testString;

        for (int i = 0; i < 36; i++) {
            password[position] = (char) ((i % 36) + 97); //Mutate the character at the position

            if (position == length - 1) {
                System.out.println(String.valueOf(password));
                //TODO: Maybe there's a more efficient method than trying to read the bytestream every time (first bytes should be the hash, right?)
                if (tryReadByteStream(password)) {
                    return String.valueOf(password);
                }
            } else {
                //Recursively call the function whilst increasing each position --> Moving forward in the string
                testString = bruteForcePassword(length, password, position + 1);
                if (testString != null) {
                    return testString;
                }
            }
        }
        //Return null
        return null;
    }

    public boolean tryReadByteStream(final char[] password) {
        try (final ZipFile zipFile = new ZipFile(new File(ZIP_FILE_PATH))) {
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
            e.printStackTrace();
            return false;
        }
        return true;
    }


}

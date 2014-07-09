package com.proptiger.data.util;

import java.io.File;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

public class FileUtil {

    @Value("${imageTempPath}")
    private static String tempDirPath;

    private static File   tempDir;

    public static final String TempFilePrefix = "tmp-";
    public static final String TempNameSeparator = "-";
    
    @PostConstruct
    private void init() {
        tempDir = new File(tempDirPath);
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }
    }

    /**
     * Converts a multi-part file to a regular file.
     * Conserves the file extension.
     * If a temp-file is created but conversion fails then it deletes the temp-file.
     * @param multipartFile
     * @return File object on success, 'null' on failure.
     */
    public static File createFileFromMultipartFile(MultipartFile multipartFile) {
        File file = null;
        try {
            file = File.createTempFile(TempFilePrefix, TempNameSeparator + multipartFile.getOriginalFilename(), tempDir);
            multipartFile.transferTo(file);
            return file;
        }
        catch (Exception e) {
            if (file != null) {
                file.delete();
            }
            return null;
        }
    }
}

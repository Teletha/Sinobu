/*
 * Copyright (C) 2015 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package kiss.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.CRC32;

import kiss.I;

/**
 * @version 2015/07/01 10:45:48
 */
class PathOperationTestHelper {

    /**
     * <p>
     * Helper method to check {@link Path} equality as file.
     * </p>
     * 
     * @param paths
     * @return
     */
    protected static boolean sameFile(Path one, Path other) {
        try {
            assert exist(one, other);
            assert file(one, other);
            assert sameLastModified(one, other);
            assert Files.size(one) == Files.size(other);
            assert checksum(one) == checksum(other);
        } catch (IOException e) {
            throw I.quiet(e);
        }
        return true;
    }

    /**
     * <p>
     * Helper method to check {@link Path} equality as directory.
     * </p>
     * 
     * @param paths
     * @return
     */
    protected static boolean sameDirectory(Path one, Path other) {
        try {
            assert exist(one, other);
            assert directory(one, other);
            assert sameLastModified(one, other);

            Iterator<Path> oneChildren = Files.newDirectoryStream(one).iterator();
            Iterator<Path> otherChildren = Files.newDirectoryStream(other).iterator();

            while (oneChildren.hasNext()) {
                assert otherChildren.hasNext();

                Path oneChild = oneChildren.next();
                Path otherChild = otherChildren.next();

                if (Files.isRegularFile(oneChild)) {
                    assert sameFile(oneChild, otherChild);
                } else if (Files.isDirectory(oneChild)) {
                    assert sameDirectory(oneChild, otherChild);
                }
            }
            assert!otherChildren.hasNext();
        } catch (IOException e) {
            throw I.quiet(e);
        }
        return true;
    }

    /**
     * <p>
     * Helper method to check {@link Path} attributes.
     * </p>
     * 
     * @param paths A path set to check.
     * @return A test result.
     */
    protected static boolean sameLastModified(Path one, Path other) {
        try {
            assert Files.getLastModifiedTime(one).toInstant().getEpochSecond() == Files.getLastModifiedTime(other)
                    .toInstant()
                    .getEpochSecond();
        } catch (Exception e) {
            throw I.quiet(e);
        }
        return true;
    }

    /**
     * <p>
     * Normalize the specified archive path.
     * </p>
     * 
     * @param path
     * @return
     */
    protected Path normalize(Path path) {
        if (path instanceof com.sun.nio.zipfs.ZipPath && path.toString().equals("/")) {
            return Paths.get(path.getFileSystem().toString());
        } else {
            return path;
        }
    }

    /**
     * <p>
     * Helper method to compute {@link Path} checksume.
     * </p>
     * 
     * @param paths
     * @return
     */
    protected static long checksum(Path path) {
        try {
            CRC32 crc = new CRC32();
            crc.update(Files.readAllBytes(path));

            return crc.getValue();
        } catch (IOException e) {
            throw I.quiet(e);
        }
    }

    /**
     * <p>
     * Helper method to check {@link Path} existence.
     * </p>
     * 
     * @param paths
     * @return
     */
    protected static boolean exist(Path... paths) {
        for (Path path : paths) {
            assert Files.exists(path);
        }
        return true;
    }

    /**
     * <p>
     * Helper method to check {@link Path} existence.
     * </p>
     * 
     * @param paths
     * @return
     */
    protected static boolean notExist(Path... paths) {
        for (Path path : paths) {
            assert Files.notExists(path);
        }
        return true;
    }

    /**
     * <p>
     * Helper method to check {@link Path} kind.
     * </p>
     * 
     * @param paths
     * @return
     */
    protected static boolean file(Path... paths) {
        for (Path path : paths) {
            if (path instanceof com.sun.nio.zipfs.ZipPath && path.toString().equals("/")) {
                // archive root directory is as archive file
            } else {
                assert Files.isRegularFile(path);
            }
        }
        return true;
    }

    /**
     * <p>
     * Helper method to check {@link Path} kind.
     * </p>
     * 
     * @param paths
     * @return
     */
    protected static boolean directory(Path... paths) {
        for (Path path : paths) {
            assert Files.isDirectory(path);
        }
        return true;
    }

    /**
     * <p>
     * Create copy of the specified path.
     * </p>
     * 
     * @param path
     * @return
     */
    protected static Path snapshot(Path path) {
        try {
            Path temp = I.locateTemporary();
            Files.createDirectories(temp);
            I.copy(path, temp);

            return temp.resolve(path.getFileName());
        } catch (IOException e) {
            throw I.quiet(e);
        }
    }

    /**
     * <p>
     * Collect children paths.
     * </p>
     * 
     * @param path
     * @return
     */
    protected static List<Path> children(Path path) {
        try {
            List<Path> list = new ArrayList();
            Files.newDirectoryStream(path).forEach(p -> list.add(p));
            return list;
        } catch (IOException e) {
            throw I.quiet(e);
        }
    }
}

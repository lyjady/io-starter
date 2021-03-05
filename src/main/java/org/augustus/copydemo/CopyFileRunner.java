package org.augustus.copydemo;

import java.io.File;

/**
 * @author: linyongjin
 * @create: 2021-03-04 21:30:48
 */
@FunctionalInterface
public interface CopyFileRunner {

    /**
     * 文件拷贝
     *
     * @param source 源文件
     * @param target 目标文件
     */
    void copyFile(File source, File target);
}

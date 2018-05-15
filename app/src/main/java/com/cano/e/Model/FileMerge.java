package com.cano.e.Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by Cano on 2018/5/3.
 */

public class FileMerge {

	File[] files = new File[8];
	File result;
	int sum = 0;

	public FileMerge(File result) {
		this.result = result;
	}

	// 利用NIO FileChannel合并多个文件
	//BufferedStream的合并操作是一个循环读取子文件内容然后复制写入最终文件的过程，此过程会从文件系统中读取数据到内存中，之后再写入文件系统，比较低效。
	//一种更高效的合并方式是利用Java nio库中FileChannel类的transferTo方法进行合并。此方法可以利用很多操作系统直接从文件缓存传输字节的能力来优化传输速度。

	synchronized public boolean merge(int item, File file) {
		files[item] = file;
		sum++;
		if (sum == 8) {
			try {
				FileChannel resultFileChannel = new FileOutputStream(result, true).getChannel();
				for (int i = 0; i < files.length; i++) {
					FileChannel blk = new FileInputStream(files[i]).getChannel();
					resultFileChannel.transferFrom(blk, resultFileChannel.size(), blk.size());
					blk.close();
				}
				resultFileChannel.close();
			} catch (IOException e) {}

			for (int i = 0; i < files.length; i++) {
				files[i].delete();
			}
			return true;
		}
		return false;
	}

}

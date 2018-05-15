package com.cano.e.Model;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import static org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE;

/**
 * Created by Cano on 2018/3/19.
 */

public class FtpUtil {

	Map<String, Object> ftpInfo;

	String path = "/";

	public void bind(Map<String, Object> ftpInfo) {
		this.ftpInfo = ftpInfo;
		ftpInfo.put("path", path);
	}

	public Map<String, Object> getFtpInfo() {
		return ftpInfo;
	}

	private void openConnect(FTPClient ftpClient) throws IOException {
		String name = ftpInfo.get("name").toString();
		String hostName = ftpInfo.get("site").toString();
		int port = (int) ftpInfo.get("port");
		String coding = ftpInfo.get("coding").toString();
		String userName = ftpInfo.get("user").toString();
		String password = ftpInfo.get("password").toString();

		// 中文转码
		ftpClient.setControlEncoding(coding);  // UTF-8, GBK
		// 连接至服务器
		ftpClient.connect(hostName, port);
		// 获取响应值
		int reply;
		reply = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			// 断开连接
			ftpClient.disconnect();
			throw new IOException("connect fail: " + reply);
		}

		// 登录到服务器
		ftpClient.login(userName, password);
		// 获取响应值
		reply = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			// 断开连接
			ftpClient.disconnect();
			throw new IOException("connect fail: " + reply);
		} else {
			// 获取登录信息
			FTPClientConfig config = new FTPClientConfig(ftpClient.getSystemType().split(" ")[0]);
			ftpClient.configure(config);
			// 使用被动模式设为默认
			ftpClient.enterLocalPassiveMode();
			// 设置以二进制方式传输
			ftpClient.setFileType(BINARY_FILE_TYPE);
		}
	}

	public FTPFile[] getList() throws IOException {
		FTPClient ftpClient = new FTPClient();
		openConnect(ftpClient);
		ftpClient.changeWorkingDirectory(path);
		FTPFile[] files = ftpClient.listFiles();
		ftpClient.logout();
		ftpClient.disconnect();
		return files;
	}

	public String getPath() {
		return path;
	}

	public String getFtpUrl() {
		return "远程文件" + path.replace("/", " > ");
	}

	public boolean changePath(String dir) {
		try {
			FTPClient ftpClient = new FTPClient();
			openConnect(ftpClient);
			ftpClient.changeWorkingDirectory(path);
			ftpClient.changeWorkingDirectory(dir);
			path = ftpClient.printWorkingDirectory();
			ftpInfo.put("path", path);
			ftpClient.logout();
			ftpClient.disconnect();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean createFolder(String folder) {
		boolean ret = false;
		try {
			FTPClient ftpClient = new FTPClient();
			openConnect(ftpClient);
			ftpClient.changeWorkingDirectory(path);
			ret = ftpClient.makeDirectory(folder);
			ftpClient.logout();
			ftpClient.disconnect();
		} catch (Exception e) {
		}
		return ret;
	}

	public boolean rename(String srcFname, String targetFname) {
		try {
			FTPClient ftpClient = new FTPClient();
			openConnect(ftpClient);
			ftpClient.changeWorkingDirectory(path);
			boolean ret = ftpClient.rename(srcFname, targetFname);
			ftpClient.logout();
			ftpClient.disconnect();
			return ret;
		} catch (IOException e) {
			return false;
		}
	}

	public boolean delete(FTPFile file) {
		try {
			FTPClient ftpClient = new FTPClient();
			openConnect(ftpClient);
			ftpClient.changeWorkingDirectory(path);
			boolean ret;
			if (file.isDirectory()) ret = ftpClient.removeDirectory(file.getName());
			else ret = ftpClient.deleteFile(file.getName());
			ftpClient.logout();
			ftpClient.disconnect();
			return ret;
		} catch (IOException e) {
			return false;
		}
	}

	public void multDownload(String remote, String local, Transfeedback r1) {
		try {
			FTPClient ftpClient = new FTPClient();
			openConnect(ftpClient);
			ftpClient.changeWorkingDirectory(path);

			FTPFile[] files = ftpClient.listFiles(remote);
			if (files.length != 1) {
				r1.run(-1);
				return;
			}
			long allSize = files[0].getSize();
			long piece = allSize / 1024 / 8;
			ftpClient.logout();
			ftpClient.disconnect();

			File target = new File(local);
			FileMerge fileMerge = new FileMerge(target);

			TransProgress transProgress = new TransProgress();

			for (int i = 0; i < 8; i++) {
				int item = i;
				long startLength = piece * i * 1024;
				long endLength = (i == 7) ? allSize : piece * (i + 1) * 1024;
				new Thread(() -> {
					partDownload(remote, local + ".part" + item, startLength, endLength, progress -> {
						if (progress < 0 || progress > 100) {
							r1.run(progress);
						} else if (progress == 100) {
							boolean merged = fileMerge.merge(item, new File(local + ".part" + item));
							if (merged) r1.run(100);
							else r1.run(-2);
						} else {
							transProgress.multDone[item] = progress;
							int sum = 0;
							for (int j = 0; j < 8; j++) {
								sum += transProgress.multDone[j];
							}
							if (transProgress.done < sum / 8) {
								transProgress.done = sum / 8;
								r1.run(transProgress.done);
							}
						}
						return true;
					});
				}).start();
			}
		} catch (Exception e) {
			r1.run(-1);
		}
	}

	public void partDownload(String remote, String local, long startLength, long endLength, Transfeedback r1) {
		try {
			FTPClient ftpClient = new FTPClient();
			openConnect(ftpClient);
			ftpClient.changeWorkingDirectory(path);

			long allSize = endLength - startLength;
			File f = new File(local);

			OutputStream out = new FileOutputStream(f);
			ftpClient.setRestartOffset(startLength);
			InputStream in = ftpClient.retrieveFileStream(remote);
			byte[] bytes = new byte[1024];
			long localSize = 0L;
			int c;
			TransProgress transProgress = new TransProgress();
			transProgress.done = 0;
			while ((c = in.read(bytes)) != -1) {
				localSize += c;

				if (localSize >= allSize) {
					out.write(bytes, 0, (int) (allSize + c - localSize));
					break;
				}
				out.write(bytes, 0, c);

				if (transProgress.done < (int) (localSize * 100 / allSize)) {
					transProgress.done = (int) (localSize * 100 / allSize);
					r1.run(transProgress.done);
				}
			}
			out.close();
			in.close();
			ftpClient.logout();
			ftpClient.disconnect();

			r1.run(100);
		} catch (Exception e) {
			r1.run(-1);
		}
	}


	public void download(String remote, String local, Transfeedback r1) {
		try {
			//检查远程文件是否存在
			FTPClient ftpClient = new FTPClient();
			openConnect(ftpClient);
			ftpClient.changeWorkingDirectory(path);

			FTPFile[] files = ftpClient.listFiles(remote);
			if (files.length != 1) {
				r1.run(-1);
				return;
			}

			long lRemoteSize = files[0].getSize();
			File f = new File(local);

			//本地存在文件，进行断点下载
//		if (f.exists()) {
//			long localSize = f.length();
//			//判断本地文件大小是否大于远程文件大小
//			if (localSize >= lRemoteSize) {
//				System.out.println("本地文件大于远程文件，下载中止");
//				return false;
//			}
//
//			//进行断点续传，并记录状态
//			FileOutputStream out = new FileOutputStream(f, true);
//			ftpClient.setRestartOffset(localSize);
//			InputStream in = ftpClient.retrieveFileStream(remote);
//
//			byte[] bytes = new byte[1024];
//			int c;
//			while ((c = in.read(bytes)) != -1) {
//				out.write(bytes, 0, c);
//				localSize += c;
//				int pg = (int) (localSize * 100 / lRemoteSize);
//				r1.run(pg);
//			}
//			in.close();
//			out.close();
//			return ftpClient.completePendingCommand();
//		} else {
			OutputStream out = new FileOutputStream(f);
			InputStream in = ftpClient.retrieveFileStream(remote);
			byte[] bytes = new byte[1024];
			long localSize = 0L;
			int c;
			TransProgress transProgress = new TransProgress();
			transProgress.done = 0;
			while ((c = in.read(bytes)) != -1) {
				out.write(bytes, 0, c);
				localSize += c;
				if (transProgress.done < (int) (localSize * 100 / lRemoteSize)) {
					transProgress.done = (int) (localSize * 100 / lRemoteSize);
					boolean continueDownload = r1.run(transProgress.done);
					if (!continueDownload) break;
				}
			}
			in.close();
			out.close();

			ftpClient.logout();
			ftpClient.disconnect();
//		}
		} catch (Exception e) {
			r1.run(-1);
		}
	}

	public boolean upload(File localFile, Transfeedback r1) {
		boolean flag = false;
		try {
			FTPClient ftpClient = new FTPClient();
			openConnect(ftpClient);
			ftpClient.changeWorkingDirectory(path);
			InputStream in = new FileInputStream(localFile);
			ftpClient.setRestartOffset(0);
			OutputStream out = ftpClient.appendFileStream(localFile.getName());
			byte[] bytes = new byte[1024];
			long allsize = localFile.length();
			long uploadedSize = 0L;
			int c;
			TransProgress transProgress = new TransProgress();
			transProgress.done = 0;
			while ((c = in.read(bytes)) != -1) {
				out.write(bytes, 0, c);
				uploadedSize += c;
				int done = (int) (uploadedSize * 100 / allsize);
				if (transProgress.done < done) {
					transProgress.done = done;
					r1.run(done);
				}
			}
			out.flush();
			in.close();
			out.close();
			flag = ftpClient.completePendingCommand();
			ftpClient.logout();
			ftpClient.disconnect();
		} catch (Exception e) {
		}
		return flag;
	}

	public interface Transfeedback {
		boolean run(int progress);
	}

	class TransProgress {
		int done;
		int[] multDone = new int[8];
	}

	// vultr ftp -- Net speed slow
	// 131B multDownload 11s singleDownload 4s
	//  3MB multDownload 25s singleDownload 38s
	// 76MB multDownload 2m28s singleDownload 10m15s

	// School ftp -- Net speed normal
	// 23MB multDownload 1s singleDownload 1s
	// 76MB multDownload 1s singleDownload 1s

}

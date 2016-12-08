import java.io.*;
import java.net.*;

/*
file:///E:/api/index.html
*/

class Tsocket {
	public static void main(String[] args) {
		try {
			// File[] fs = new File[] {new File("Sublime Text Build 3114 x64.zip"), new File("Sublime Text Build 3114 x64 (2).zip"), new File("npp.6.9.2.bin.zip"), new File("Sublime Text Build 3114.zip")};

			File f = new File("E:\\Android");
			File[] fs = f.listFiles() ;

			// UFTP u = new UFTP("xxxx.web.fc2.com", "xxxx", "xxxx");
			UFTP u = new UFTP("192.168.11.7", "guest", "guest");
			String code = u.FTPconnect();

			if (code.equals("ConnectFTPServer")) {
				u.upload(fs);
				u.close();
				System.out.println(code);
			} else {
				System.out.println(code);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}



class UFTP {
	private String USER, PASSWORD;
	private InetAddress FTPServerIP;
	private BufferedReader FTPCmdreader;
	private BufferedWriter FTPCmdwriter ;

	public UFTP(String FURL, String USER, String PASSWORD) throws Exception {
		this.FTPServerIP = InetAddress.getByName(FURL);
		this.USER = USER;
		this.PASSWORD = PASSWORD;
	}

	public  String FTPconnect()  {
		String errorlevel = "ConnectFTPServer";
		try {
			Socket socket21 = new Socket (FTPServerIP, 21);
			FTPCmdreader = new BufferedReader(new InputStreamReader(socket21.getInputStream()));
			FTPCmdwriter = new BufferedWriter(new OutputStreamWriter(socket21.getOutputStream()));
			FTPcommand(FTPCmdwriter, "USER " + USER);
			FTPcommand(FTPCmdwriter, "PASS " + PASSWORD);
		} catch (Exception e) {
			errorlevel = "ConnectFTPerror";
		}
		return errorlevel;
	}

	public void upload(File[] files) throws Exception {
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				FTPcommand(FTPCmdwriter, "PASV ");
				String[] temp = new String[] {};
				String recode = new String();
				while (true) {
					recode = FTPCmdreader.readLine();
					System.out.println(recode);
					if (recode.substring(0, 3).equals("227")) {
						temp = get227(recode);
						break;
					}
				}
				Socket socket20 = new Socket(temp[0], Integer.parseInt(temp[1]));
				OutputStream ftpOutputStream = socket20.getOutputStream();
				FileInputStream upfileInputStream = new FileInputStream(files[i]);
				FTPcommand(FTPCmdwriter, "TYPE " + "I");
				FTPcommand(FTPCmdwriter, "STOR " + files[i].getName());
				System.out.println("UPDATE Filename " + files[i].getName());
				byte[] buff = new byte[1024 * 1024];
				int len = 0;
				while ( (len = upfileInputStream.read(buff) ) != -1) {
					ftpOutputStream.write(buff, 0, len);
					ftpOutputStream.flush();
					System.out.print("*");
				}
				ftpOutputStream.close();
				upfileInputStream.close();
				System.out.println("");
				Thread.sleep(5 * 1000);
			}
		}
	}

	public void close() {
		try {
			FTPcommand(FTPCmdwriter, "QUIT ");
			String temp = null;
			while ( (temp = FTPCmdreader.readLine() ) != null) {
				System.out.println(temp);
			}
			System.out.println("");
			FTPCmdwriter.close();
			FTPCmdreader.close();
		} catch (Exception e) {

		}
	}
	private void FTPcommand(BufferedWriter FTPCmdwriter, String FTPcmd) throws Exception {
		FTPCmdwriter.write(FTPcmd +  "\r\n");
		FTPCmdwriter.flush();
	}

	protected String[] get227(String string) {
		int start = string.indexOf("(") + 1;
		int end = string.indexOf(")");
		String substring = string.substring(start, end);
		String[] temp = substring.split(",");
		String ip = temp[0] + "." + temp[1] + "." + temp[2] + "." + temp[3];
		int port = Integer.parseInt(temp[4]) * 256 + Integer.parseInt(temp[5]);
		String sport = String.valueOf(port);
		String[] res = {ip, sport};
		return res;
	}
}
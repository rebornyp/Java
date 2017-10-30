### 学习Java中尝试程序修改文件名称

* 代码如下

---java
package useful;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * 一个测试尝试在电脑本地修改文件名的小程序，将三国演义每一集都重新以集数+内容命名
 * 内容包括读取本地txt文件然后解析字符串，存入数组，然后将字符串数组内容写入要修改的文件夹的子文件数组中
 * @author Gastby
 *
 */
public class ThreeKindomChangeName {

	public static void main(String[] args) {
		String path1 = "C:\\Users\\Gastby\\Desktop\\a.txt"; //工具文件
		String path2 = "D:\\Garden\\四大名著.1986-2000.国语\\三国演义.1994.84集全"; //目标文件
		File file = new File(path1);
		File file2 = new File(path2);
		if(!file.exists() || !file2.exists())
			return;
		FileReader fr = null;
		BufferedReader br = null;
		String temp = null;
		String[] str = null;
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			StringBuilder sb = new StringBuilder();
			while((temp = br.readLine()) != null)
				sb.append(temp);
			temp = sb.toString().trim();
			str = temp.split("[0-9]+");
			for(int j=0; j<str.length; j++)
				str[j] = str[j].trim();
			br.close(); //即时关掉所有流的通道，以免占用内存以及无法修改被占用的文件
			fr.close(); //同上
			
			File[] list = file2.listFiles();
			temp = "D:\\Garden\\四大名著.1986-2000.国语\\三国演义.1994.84集全\\三国演义-第";
			int i = 1;
			System.out.println(list.length);
			for(File f : list) {
				String name = temp + i + "集-" + str[i-1] + ".mkv";
				if(f.exists())
					f.renameTo(new File(name)); //新文件必须和旧文件的父亲路径保持一致
				i ++;
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
  
}

---

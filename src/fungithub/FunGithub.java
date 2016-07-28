package fungithub;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunGithub {
	
	private static Map<String, Word> wordMap = new HashMap<String, Word>();
	private static int[][] picNumber = new int[1000][30];
	
	
	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("usage : <username> <words>");
			System.exit(1);
		}
		solve(args[0], args[1]);
	}
	
	public static void solve(String username, String words) {
		words = words.toUpperCase();
		try (
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(System.getProperty("user.dir") + "/picture-fonts.txt"), "utf-8"))
				) {
			int idx = 0;
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0) continue;
				int len = line.length();
				char[] ch = line.toCharArray();
				for (int i = 0; i < len; i ++) {
					char c = ch[i];
					picNumber[idx][i] = (int) c -  (int)'0';
				}
				idx ++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try (
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(System.getProperty("user.dir") + "/position.txt"), "utf-8"))
				) {
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0) continue;
				Word temp = new Word(line);
				wordMap.put(temp.getC(), temp);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		List<Integer> allWords = new ArrayList<Integer>();
		int i;
		for (i = 0; i < words.length(); i ++) {
			String oneWordString = String.format("%c", words.charAt(i));
			Word oneWord = wordMap.get(oneWordString);
			if (oneWord == null) {
				System.err.println(oneWordString + " not exists!");
				continue;
			}
			List<Integer> tmpWordsList = oneWord.getWordsList();
			if (allWords.size() + tmpWordsList.size() > 52 * 7)
				break;
			else {
				allWords.addAll(tmpWordsList);
				if (allWords.size() + 7 <= 52 * 7)
					for (int j = 0; j < 7; j ++)
						allWords.add(0);
			}
		}
		System.out.println("\"" + words.substring(0, i) +"\" solved!");
		int delta = 52 * 7 - allWords.size();
		for (int j = 0; j < delta; j ++) allWords.add(0);
		// allWords -- the list with 52 * 7 words generated.
		
		// download url content
		URL url = null;
		HttpURLConnection urlConnection = null;
		BufferedReader reader;
		String pageContent = "";
		try {
			url = new URL("https://github.com/" + username);
			urlConnection = (HttpURLConnection) url.openConnection();
			reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
			String line;
	        while ((line = reader.readLine()) != null){
	             pageContent += line + "\n";
	        }
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		pageContent = pageContent.replaceAll("fill=\"#[0-9a-e]{6}\"", "MoON1igHt");
		for (int word : allWords) {
			String fillStr = null;
			if (word == 1) {
				fillStr = "fill=\"#1e6823\"";
			} else {
				double r = Math.random();
				if (r <= 0.3) fillStr = "fill=\"#eeeeee\"";
				else if (r <= 0.9) fillStr = "fill=\"#d6e685\"";
				else if (r <= 0.96) fillStr = "fill=\"#8cc665\"";
				else fillStr = "fill=\"#44a340\"";
			}
			pageContent = pageContent.replaceFirst("MoON1igHt", fillStr);
		}
		pageContent = pageContent.replaceAll("MoON1igHt", "fill=\"#eeeeee\"");
		
		// write content to output html file, here I set it to Desktop, here I am the user "Administrator" on Windows7
		String outputFileName = "C:/Users/Administrator/Desktop/" + username + "-" + words + ".html";
		try (
				OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(outputFileName), "utf-8");
				) {
			osw.write(pageContent);
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(username + "-" + words + ".html successifully generated at desktop!");
	}
	
	static class Word {
		
		private String c;
		private int px;
		private int py;
		private int height;
		private int width;
		private List<Integer> wordsList;
		
		public Word(String line) {
			String[] arr = line.split(",");
			c = arr[0];
			px = Integer.parseInt(arr[1]);
			py = Integer.parseInt(arr[2]);
			height = Integer.parseInt(arr[3]);
			width = Integer.parseInt(arr[4]);
			
			// generate wordsList
			wordsList = new ArrayList<Integer>();
			for (int j = 0; j < width; j ++) 
				for (int i = 0; i < height; i ++)
					wordsList.add(picNumber[px+i][py+j]);
		}
		
		public String getC() { return c; }
		public int getPx() { return px; }
		public int getPy() { return py; }
		public int getHeight() { return height; }
		public int getWidth() { return width; }
		public List<Integer> getWordsList() { return wordsList; }
	}
}

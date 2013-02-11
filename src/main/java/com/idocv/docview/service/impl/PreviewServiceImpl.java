package com.idocv.docview.service.impl;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.service.PreviewService;
import com.idocv.docview.util.CmdUtil;
import com.idocv.docview.util.RcUtil;
import com.idocv.docview.vo.ExcelVo;
import com.idocv.docview.vo.PPTVo;
import com.idocv.docview.vo.PageVo;
import com.idocv.docview.vo.TxtVo;
import com.idocv.docview.vo.WordVo;

@Service
public class PreviewServiceImpl implements PreviewService, InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(PreviewServiceImpl.class);
	
	@Resource
	private RcUtil rcUtil;
	
	private @Value("${office.cmd.word2html}")
	String word2Html;

	private @Value("${office.cmd.excel2html}")
	String excel2Html;

	private @Value("${office.cmd.ppt2jpg}")
	String ppt2Jpg;

	private static String lineDilimeter = "``";
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO
	}

	@Override
	public PageVo<WordVo> convertWord2Html(String rid, int start, int limit) throws DocServiceException{
		try {
			convert(rid);
			File htmlFile = new File(rcUtil.getParsePathOfHtml(rid));
			
			// read body
			File bodyPath = new File(rcUtil.getParseDir(rid) + "body.html");
			String bodyRaw;
			if (!bodyPath.isFile()) {
				String contentWhole = FileUtils.readFileToString(htmlFile, "GBK");
				bodyRaw = contentWhole.replaceFirst("(?s)(?i).*?(<BODY[^>]*>)(.*?)</BODY>.*", "$2");
				FileUtils.writeStringToFile(bodyPath, bodyRaw, "UTF-8");
			} else {
				bodyRaw = FileUtils.readFileToString(bodyPath, "UTF-8");
			}

			String bodyString = bodyRaw;

			// modify picture path from RELATIVE to ABSOLUTE url.
			bodyString = processPictureUrl(rid, bodyString);
			
			// paging
			List<String> pages = new ArrayList<String>();
			while (bodyString.matches("(?s)(?i)(.+?)(<[^>]+style=\"[^>]*page-break-before[^>]+>.*)(?-i)")) {
				String page = bodyString.replaceFirst("(?s)(?i)(.+?)(<[^>]+style=\"[^>]*page-break-before[^>]+>.*)(?-i)", "$1");
				bodyString = bodyString.replaceFirst("(?s)(?i)(.+?)(<[^>]+style=\"[^>]*page-break-before[^>]+>.*)(?-i)", "$2");
				pages.add(page);
			}
			pages.add(bodyString);
			
			List<WordVo> data = new ArrayList<WordVo>();
			// construct vo
			for (String page : pages) {
				WordVo word = new WordVo();
				word.setContent(page);
				data.add(word);
			}
			PageVo<WordVo> page = new PageVo<WordVo>(data, 1);
			return page;
		} catch (Exception e) {
			logger.error("convertWord2Html error: ", e.fillInStackTrace());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	@Override
	public PageVo<ExcelVo> convertExcel2Html(String rid, int start, int limit) throws DocServiceException {
		try {
			convert(rid);
			File rawFilesDir = new File(rcUtil.getParseDir(rid) + "index.files");
			if (!rawFilesDir.isDirectory()) {
				throw new DocServiceException("Can't find parsed directory!");
			}

			File[] excelFiles = rawFilesDir.listFiles();
			List<File> sheetFiles = new ArrayList<File>();
			File tabstripFile = null;
			File sheetStyleFile = null;
			for (File excelFile : excelFiles) {
				if (excelFile.getName().matches("sheet\\d+\\.html")) {
					sheetFiles.add(excelFile);
				} else if (excelFile.getName().equalsIgnoreCase("tabstrip.html")) {
					tabstripFile = excelFile;
				} else if (excelFile.getName().endsWith(".css")) {
					sheetStyleFile = excelFile;
				}
			}
			if (CollectionUtils.isEmpty(sheetFiles) || null == tabstripFile) {
				throw new Exception("Excel parsed files NOT found!");
			}
			
			// get TITLE(s) and CONTENT(s)
			List<ExcelVo> VoList = new ArrayList<ExcelVo>();
			String titleFileContent = FileUtils.readFileToString(tabstripFile, "GBK");
			System.err.println("Processing excel file - " + rid);
			for (int i = 0; i < sheetFiles.size(); i++) {
				ExcelVo vo = new ExcelVo();

				// get title
				String title = titleFileContent.replaceFirst("(?s)(?i).+?" + sheetFiles.get(i).getName() + ".+?<font[^>]+>(.+?)</font>.*(?-i)", "$1");
				title = StringUtils.isBlank(title) ? ("表单" + (i + 1)) : title;
				// titleList.add(title);
				System.err.println("    title" + (i + 1) + " = " + title);

				// get content
				String sheetFileContent = FileUtils.readFileToString(sheetFiles.get(i), "GBK");
				String sheetContent = sheetFileContent.replaceFirst("(?s)(?i).+?<body.+?(<table[^>]+>.*?</table>).*(?-i)", "$1");
				// sheetContent = processPictureUrl(rid, sheetContent);
				// contentList.add(sheetContent);

				vo.setTitle(title);
				vo.setContent(sheetContent);
				VoList.add(vo);
			}
			PageVo<ExcelVo> page = new PageVo<ExcelVo>(VoList, sheetFiles.size());
			if (null != sheetStyleFile) {
				String styleString = FileUtils.readFileToString(sheetStyleFile, "GBK");
				File destStylePath = new File(rcUtil.getParseDir(rid) + "style.css");
				FileUtils.writeStringToFile(destStylePath, styleString, "UTF-8");
				page.setStyleUrl(rcUtil.getParseUrlDir(rid) + "style.css");
			}
			return page;
		} catch (Exception e) {
			logger.error("convertExcel2Html error: ", e.fillInStackTrace());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	@Override
	public PageVo<PPTVo> convertPPT2Html(String rid, int start, int limit) throws DocServiceException {
		try {
			// get page count
			File[] slideFiles = new File(rcUtil.getParseDir(rid)).listFiles();
			if (slideFiles.length <= 0) {
				convert(rid);
				slideFiles = new File(rcUtil.getParseDir(rid)).listFiles();
			}

			List<PPTVo> data = new ArrayList<PPTVo>();
			if (slideFiles.length > 0) {
				for (int i = 0; i < slideFiles.length; i++) {
					PPTVo ppt = new PPTVo();
					String url = rcUtil.getParseUrlDir(rid) + slideFiles[i].getName();
					ppt.setUrl(url);
					data.add(ppt);
				}
			}
			PageVo<PPTVo> page = new PageVo<PPTVo>(data, 1);
			return page;
		} catch (Exception e) {
			logger.error("convertPPT2Html error: ", e.fillInStackTrace());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	@Override
	public PageVo<TxtVo> getTxtContent(String rid) throws DocServiceException {
		try {
			File src = new File(rcUtil.getPath(rid));
			List<TxtVo> data = new ArrayList<TxtVo>();
			String content = FileUtils.readFileToString(src, getEncoding(src));
			String[] paragraphs = content.split("\n");
			for (String para : paragraphs) {
				TxtVo vo = new TxtVo();
				String c = "<P STYLE=\"margin-bottom: 0in\"><FONT FACE=\"微软雅黑, serif\">"
						+ para + "</FONT></P>";
				vo.setContent(c);
				data.add(vo);
			}
			PageVo<TxtVo> page = new PageVo<TxtVo>(data, 1);
			return page;
		} catch (IOException e) {
			logger.error("getTxtContent error: ", e.fillInStackTrace());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	@Override
	public String convertPdf2Swf(String rid) throws DocServiceException {
		try {
			String src = rcUtil.getPath(rid);
			String dest = rcUtil.getParseDir(rid) + RcUtil.getFileNameWithoutExt(RcUtil.getFileNameByRid(rid)) + ".swf";
			File destFile = new File(dest);
			if (!destFile.isFile()) {
				Runtime runtime = Runtime.getRuntime();
				runtime.exec("pdf2swf " + src + " -o " + dest + " -f -T 9 -t -s storeallcharacters").waitFor();
				if (!destFile.isFile()) {
					System.out.println("Can't convert \"" + src + "\" to \"" + dest + "\", start converting using poly2bitmap parameter...");
					runtime.exec("pdf2swf " + src + " -o " + dest + " -f -T 9 -t -s storeallcharacters -s poly2bitmap").waitFor();
				}
			}
			return rcUtil.getParseUrlDir(rid) + RcUtil.getFileNameWithoutExt(RcUtil.getFileNameByRid(rid)) + ".swf";
		} catch (Exception e) {
			logger.error("convertPdf2Swf error: ", e.fillInStackTrace());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	private boolean convert(String rid) throws DocServiceException {
		String src = rcUtil.getPath(rid);
		File srcFile = new File(src);
		String dest = rcUtil.getParsePathOfHtml(rid);
		File destFile = new File(dest);
		if (!srcFile.isFile()) {
			throw new DocServiceException("File NOT found, rid=" + rid);
		}
		String ext = RcUtil.getExt(rid);
		try {
			if ("doc".equalsIgnoreCase(ext) || "docx".equalsIgnoreCase(ext)) {
				if (!destFile.isFile()) {
					CmdUtil.runWindows(word2Html, src, dest);
				}
			} else if ("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext)) {
				if (!destFile.isFile()) {
					CmdUtil.runWindows(excel2Html, src, dest);
				}
			} else if ("ppt".equalsIgnoreCase(ext) || "pptx".equalsIgnoreCase(ext)) {
				dest = rcUtil.getParseDir(rid);
				destFile = new File(dest);
				if (destFile.listFiles().length <= 0) {
					CmdUtil.runWindows(ppt2Jpg, src, destFile.getAbsolutePath(), "save");
				}
			} else {
				throw new DocServiceException("Unsupported document type!");
			}
			return true;
		} catch (Exception e) {
			logger.error("convert error: ", e.fillInStackTrace());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	/**
	 * modify picture path from RELATIVE to ABSOLUTE url.
	 * @param content
	 * @return
	 */
	public String processPictureUrl(String rid, String content) throws DocServiceException {
		return content.replaceAll("(?s)(?i)(<img[^>]+?src=\")([^>]+?>)(?-i)", "$1" + rcUtil.getParseUrlDir(rid) + "$2");
	}
	
	public static String getEncoding(File file) {
		byte[] b = new byte[3];
		try {
			InputStream is = new FileInputStream(file);
			is.read(b);
			is.close();
		} catch (IOException e) {
			logger.error("Get encoding error: " + e.getMessage());
		}
		if (b[0] == -17 && b[1] == -69 && b[2] == -65) {
			return "UTF-8";
		} else if ((b[0] == -2 && b[1] == -1) || (b[0] == -1 && b[1] == -2)) {
			return "unicode";
		} else if (b[0] == -26 && b[1] == -75 && b[2] == -117) {	// apple
			return "UTF-8";
		} else if (b[0] == 97 && b[1] == 98 && b[2] == 99) {		// apple
			return "UTF-8";
		} else {
			return "GBK";
		}
	}

	@Override
	public boolean validateIp(String ip) throws DocServiceException {
		return true;
	}
}

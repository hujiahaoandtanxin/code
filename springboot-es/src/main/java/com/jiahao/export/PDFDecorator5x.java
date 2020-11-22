package com.jiahao.export;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jiahao.markdown.Block;
import com.jiahao.markdown.BlockType;
import com.jiahao.markdown.ValuePart;
import com.jiahao.util.ImgHelper;
import com.jiahao.util.MD2FileUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PDFDecorator5x implements Decorator {
	private Document doc;
	private static BaseFont bfChinese;
	static{
		try {
			bfChinese = BaseFont.createFont("MSYH.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		} catch (Exception e1) {
			try {
				bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);
			} catch (Exception e2) {
			}
			System.out.println("没有找到MSYH.TTF字体文件，使用itext自带中文字体。如果需要更好的显示效果，可以添加MSYH.TTF到src目录下");
		}
	}
    Font fontYHNormal = new Font(bfChinese, 12, Font.NORMAL); 
	
	public PDFDecorator5x(Document doc) {
		this.doc = doc;
	}
	
	public void beginWork(String outputFilePath) {
		try {
			PdfWriter.getInstance(doc, new FileOutputStream(outputFilePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		doc.open();
	}
	
	public void decorate(List<Block> list) {
		for (Block block : list) {
			try{
				switch (block.getType()) {
				case CODE:
					doc.add(codeParagraph(block.getValueParts()));
					break;
				case HEADLINE:
					doc.add(headerParagraph(block.getValueParts(), block.getLevel()));
					break;
				case QUOTE:
					List<Element> quotes = quoteParagraph(block.getListData());
					for (Element element : quotes) {
						doc.add(element);
					}
					break;
				case TABLE:
					doc.add(tableParagraph(block.getTableData()));
					break;
				case ORDERED_LIST:
					List<Element> els = listParagraph(block.getListData(), true);
					for (Element element : els) {
						doc.add(element);
					}
					break;
				case UNORDERED_LIST:
					els = listParagraph(block.getListData(), false);
					for (Element element : els) {
						doc.add(element);
					}
					break;
				default:
					doc.add(commonTextParagraph(block.getValueParts()));
					break;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void afterWork(String outputFilePath) {
		doc.close();
	}
	
	private List<Element> listParagraph(List<Block> listData, boolean isOrder){
		List<Element> list = new ArrayList<Element>();
		int j = 1;
		for (Block block : listData) {
			ValuePart[] vps = block.getValueParts();
			ValuePart[] newVps = new ValuePart[vps.length+1];
			if(isOrder){
				newVps[0] = new ValuePart(j+". ");
			}else{
				newVps[0] = new ValuePart("• ");
			}
			for (int i=1; i<newVps.length; i++) {
				newVps[i] = vps[i-1];
			}
			list.add(commonTextParagraph(newVps));
			j++;
		}
		return list;
	}
	
	private Element headerParagraph(ValuePart[] valueParts, int level){
		Paragraph paragraph = new Paragraph();
		for (ValuePart valuePart : valueParts) {
			BlockType[] types = valuePart.getTypes();
			Font font = new Font(bfChinese, 30-4*level);
			if(types!=null){
				for (BlockType type : types) {
					formatByType(font, type, valuePart.getLevel());
				}
			}
			Chunk chunk = new Chunk(valuePart.getValue(), font);
			paragraph.add(chunk);
		}
		paragraph.setSpacingBefore(20);
		paragraph.setSpacingAfter(10);
		return paragraph;
	}
	
	private Element tableParagraph(List<List<String>> tableData){
		
		int nRows = tableData.size();
    	int nCols = 0;
    	for (List<String> list : tableData) {
			int s = list.size();
			if(nCols<s){
				nCols = s;
			}
		}
    	
    	PdfPTable pdfPTable = new PdfPTable(nCols);
    	pdfPTable.setWidthPercentage(100);
    	
        Font font = new Font(bfChinese, 12);
        for (int i=0; i<nRows; i++) {
			List<String> colDatas = tableData.get(i);
			for(int j=0; j<nCols; j++){
				PdfPCell cell = new PdfPCell();
				if(i==0){
					cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				}
				cell.setPaddingBottom(12);
				cell.setPaddingLeft(12);
				cell.setPaddingRight(12);
				try {
					cell.addElement(new Chunk(colDatas.get(j), font));
				} catch (Exception e) {
					cell.addElement(new Chunk("", font));
				}
				pdfPTable.addCell(cell);
			}
		}
        
        pdfPTable.setSpacingAfter(5);
        return pdfPTable;
    }

	
	private Element imgParagraph(ValuePart valuePart){
		final String imgFile = valuePart.getValue();
		final PdfPTable pdfPTable = new PdfPTable(1);
		final PdfPCell cell = new PdfPCell();
		ImgHelper helper = new ImgHelper() {
			
			@Override
			public void setIntoFile(InputStream is) {

				if(is==null){
					return;
				}
				ByteArrayOutputStream baos;
				try {
					baos = MD2FileUtil.inputStream2ByteArrayOutputStream(is);
					byte[] bs = baos.toByteArray();
					Image image = Image.getInstance(bs);
					InputStream tmpIs = new ByteArrayInputStream(baos.toByteArray());
		        	int[] wh =getImgWidthHeight(tmpIs);
		        	int p = wh[0]*100/1000;
		        	p = p>100?100:p;
		        	pdfPTable.setWidthPercentage(p);
					cell.setBorder(PdfPCell.NO_BORDER);
					cell.addElement(image);
				} catch (Exception e) {
					System.out.println("[error] 无法生成图片："+imgFile+": "+e.getMessage());
					e.printStackTrace();
				}
			}
		};
		
		InputStream result = helper.setImgByUrl(imgFile);
		if(result == null) {
			ValuePart part = new ValuePart("图片地址："+imgFile);
			return commonTextParagraph(new ValuePart[]{part});
		}
		pdfPTable.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
		pdfPTable.addCell(cell);
		pdfPTable.setSpacingAfter(5);
		return pdfPTable;
	}
	
	private Element codeParagraph(ValuePart[] valueParts) throws DocumentException{

		String value = valueParts[0].getValue();
		Paragraph p = new Paragraph(value, new Font(bfChinese, 12));

		PdfPTable pdfPTable = new PdfPTable(1);
		pdfPTable.setWidthPercentage(100);
		
		PdfPCell cell = new PdfPCell();
		cell.addElement(p);
		cell.setBorder(Rectangle.BOX);
		cell.setPaddingBottom(12);
		cell.setPaddingLeft(12);
		cell.setPaddingRight(12);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		pdfPTable.addCell(cell);
		pdfPTable.setSpacingAfter(5);
		return pdfPTable;
	}
	
	private List<Element> quoteParagraph(List<Block> listData){
		boolean isFirst = true;
		List<Element> results = new ArrayList<Element>();
		for (int i = 0; i<listData.size(); i++) {
			Block block = listData.get(i);
			if(i>0){
				isFirst = false;
			}
			Element element = quoteParagraph(block.getValueParts(), isFirst);
			results.add(element);
		}
		return results;
	}
	
	private Element quoteParagraph(ValuePart[] valueParts, boolean isFirst){
		Paragraph p = new Paragraph();
		for (ValuePart valuePart : valueParts) {
			BlockType[] types = valuePart.getTypes();
			Font font = new Font(bfChinese);
			if(types!=null){
				for (BlockType type : types) {
					formatByType(font, type, valuePart.getLevel());
				}
			}
			Chunk chunk = new Chunk(valuePart.getValue(), font);
			p.add(chunk);
		}
		
		PdfPTable pdfPTable = new PdfPTable(2);

		try{
			pdfPTable.setWidthPercentage(100);
			pdfPTable.setWidths(new int[]{1, 20});
			
			try {
				PdfPCell cell = new PdfPCell();
				if(isFirst){
					InputStream is = PDFDecorator5x.class.getResourceAsStream("/quote_char.jpg");
					Image image = Image.getInstance(MD2FileUtil.inputStream2ByteArrayOutputStream(is).toByteArray());
					cell.addElement(image);
				}else{
					cell.addElement(new Chunk());
				}
				cell.setBorder(PdfPCell.NO_BORDER);
				pdfPTable.addCell(cell);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			PdfPCell cell = new PdfPCell();
			cell.addElement(p);
			cell.setBorder(PdfPCell.NO_BORDER);
			if(isFirst){
				cell.setPaddingTop(10);
			}
			cell.setPaddingBottom(5);
			pdfPTable.addCell(cell);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return pdfPTable;
	}
	
	private Paragraph commonTextParagraph(ValuePart[] valueParts){

		Paragraph p = new Paragraph();
		if(valueParts==null){
			return p;
		}
		for (ValuePart valuePart : valueParts) {
			BlockType[] types = valuePart.getTypes();
			Font font = new Font(bfChinese);
			boolean hasImg = false;
			if(types!=null){
				for (BlockType type : types) {
					if(type == BlockType.IMG){
						hasImg = true;
						break;
					}
					formatByType(font, type, valuePart.getLevel());
				}
			}
			if(hasImg){
				p.add(imgParagraph(valuePart));
			}else{
				Chunk chunk = new Chunk(valuePart.getValue(), font);
				p.add(chunk);
			}
		}

		p.setSpacingAfter(5);
		
		return p;
	}
	
	private void formatByType(Font font, BlockType type, int level){
		switch (type) {
			case BOLD_WORD:
				font.setStyle(Font.BOLD);
				break;
			case ITALIC_WORD:
				font.setStyle(Font.ITALIC);
				break;
			case STRIKE_WORD:
				font.setStyle(Font.STRIKETHRU);
				break;
			case CODE_WORD:
				font.setColor(BaseColor.RED);
				break;
			case HEADLINE:
				font.setSize(16);
				font.setStyle(Font.BOLD);
				break;
			default:
				break;
		}
	}
}

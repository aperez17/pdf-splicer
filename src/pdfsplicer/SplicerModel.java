/*
 * PDFSplicer is a way to create a custom PDF from selected pages from several different existing PDFs.
 * Copyright (C) 2016  Abeer Ahmed
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pdfsplicer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.multipdf.PDFCloneUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * This class acts as a model for the MVC pattern. All of the actual information about the PDFs
 * and selected page ranges are stored here, and when manipulated by a controller, notifies the view.
 * 
 * @author Abeer Ahmed
 */
public class SplicerModel extends Observable {
	
	// PDF List
	private Map<String, PDDocument> pdfList;
	private DefaultListModel<String> pdfListModel;
	private DefaultComboBoxModel<String> pdfComboBoxModel;
	private int numPDFs;

	// PDF List Viewer
	private int curLPage;
	private int lNumPages;
	private PDFRenderer loadedRenderer;
	private ImageIcon lPDFIcon;

	// Page Range List
	private List<String> pageEntryPDFList;
	private List<List<Integer>> pageRangeList;
	private DefaultListModel<String> pageRangeModel;
	private int numPageRanges;
	
	// Final PDF Viewer
	private int curFPage;
	private int fNumPages;
	private PDFRenderer finalRenderer;
	private ImageIcon fPDFIcon;
	
	/**
	 * Create a new model.
	 */
	public SplicerModel() {
		pdfList = new HashMap<String, PDDocument>();
		pdfListModel = new DefaultListModel<String>();
		pdfComboBoxModel = new DefaultComboBoxModel<String>();
		numPDFs = 0;

		pageEntryPDFList = new ArrayList<String>();
		pageRangeList = new ArrayList<List<Integer>>();
		pageRangeModel = new DefaultListModel<String>();
		numPageRanges = 0;
	}
	
	/**
	 * Add a PDF.
	 * 
	 * @param file the PDF file
	 * @throws IOException if the file cannot be read
	 */
	public void addPDF(File file) throws IOException {
		String name = file.getName();
		if (pdfList.containsKey(name)) {
			return;
		}
		pdfList.put(name, PDDocument.load(file));
		pdfListModel.addElement(name);
		pdfComboBoxModel.addElement(name);
		++numPDFs;
	}
	
	/**
	 * Remove a PDF.
	 * 
	 * @param index the index of the PDF in the list
	 * @param removeMulti if true, do not re-render the finalized PDF preview
	 * @throws IOException if the file cannot be closed
	 */
	public void removePDF(int index, boolean removeMulti) throws IOException {
		String name = pdfListModel.getElementAt(index);
		for (int i = pageEntryPDFList.size() - 1; i >= 0; --i) {
			if (pageEntryPDFList.get(i).equals(name)) {
				removePageRange(i, true);
			}
		}
		pdfList.get(name).close();
		pdfList.remove(name);
		pdfComboBoxModel.removeElementAt(index);
		pdfListModel.removeElementAt(index);
		--numPDFs;
		
		if (!removeMulti) showFinalPDFPreview();
	}
	
	/**
	 * Remove all PDFs.
	 * 
	 * @throws IOException if any of the files cannot be closed
	 */
	public void removeAllPDF() throws IOException {
		for (int i = numPDFs - 1; i >= 0; --i) {
			removePDF(i, true);
		}
		
		showFinalPDFPreview();
	}
	
	/**
	 * Add a range of pages from a loaded PDF to add to the new PDF.
	 * 
	 * @param pdfName the PDF from which the page range came from
	 * @param pages a string representing the range of pages to add
	 * @return {@code true} if the page range was successfully added
	 * @throws IOException if the finalized PDF preview cannot be rendered
	 */
	public boolean addPageRange(int pdfIndex, String pages) throws IOException {
		String pdfName = pdfListModel.get(pdfIndex);
		List<Integer> markedPages = new ArrayList<Integer>();
		int pageLimit = pdfList.get(pdfName).getNumberOfPages();
		
		if (pages.toLowerCase().equals("all")) { // Add all pages from the PDF in order
			for (int i = 1; i <= pageLimit; ++i) {
				markedPages.add(i);
				++fNumPages;
			}
			
			if (pageLimit == 1) {
				pageRangeModel.addElement(pdfName + ": " + 1);
			} else {
				pageRangeModel.addElement(pdfName + ": " + 1 + "-" + pageLimit);
			}
		} else {
			String[] pagenums = pages.split(","); // Separate pages or ranges of pages by comma, IE 1,5,6
			for (String pagenumsPart : pagenums) {
				pagenumsPart = pagenumsPart.trim();
				if (pagenumsPart.contains("-")) { // Dash designates a range of pages, IE 2-5 is pages 2 to 5
					String[] pageRange = pagenumsPart.split("-");
					if (pageRange.length != 2) {
						return false;
					}
					try {
						int begin = Integer.parseInt(pageRange[0].trim());
						int end = Integer.parseInt(pageRange[1].trim());
						for (int i = begin; i <= end; ++i) {
							if (pageLimit < i || i <= 0) {
								return false;
							}
							markedPages.add(i);
							++fNumPages;
						}
					} catch (NumberFormatException e) {
						System.err.println(e.getStackTrace());
					}
				} else { // If a single number was entered
					try {
						int pnum = Integer.parseInt(pagenumsPart);
						if (pageLimit < pnum || pnum <= 0) {
							return false;
						}
						markedPages.add(pnum);
						++fNumPages;
					} catch (NumberFormatException e) {
						return false;
					}
				}
			}
			
			pageRangeModel.addElement(pdfName + ": " + pages.replaceAll("\\s+", ""));
		}

		pageEntryPDFList.add(pdfName);
		pageRangeList.add(markedPages);
		++numPageRanges;
		
		showFinalPDFPreview();
		
		setChanged();
		notifyObservers();
		return true;
	}
	
	/**
	 * Remove a page range from the new PDF.
	 * 
	 * @param index the index of the page range to be removed
	 * @param removeMulti if true, do not re-render the finalized PDF preview
	 * @throws IOException if the finalized PDF preview cannot be rendered
	 */
	public void removePageRange(int index, boolean removeMulti) throws IOException {
		fNumPages -= pageRangeList.get(index).size();
		pageEntryPDFList.remove(index);
		pageRangeList.remove(index);
		pageRangeModel.remove(index);
		--numPageRanges;
		
		if (!removeMulti) showFinalPDFPreview();
	}
	
	/**
	 * Remove all page ranges from the new PDF.
	 * 
	 * @throws IOException if the finalized PDF preview cannot be rendered
	 */
	public void removeAllPageRange() throws IOException {
		for (int i = numPageRanges - 1; i >= 0; --i) {
			removePageRange(i, true);
		}
		
		showFinalPDFPreview();
	}
	
	/**
	 * Render an image for the first page of the selected loaded PDF.
	 * 
	 * @param selectedValue the name of the selected PDF
	 * @throws IOException if the selected PDF cannot be read
	 */
	public void showLoadedPDFPreview(String selectedValue) throws IOException {
		lPDFIcon = null;
		curLPage = -1;
		lNumPages = -1;
		
		if (selectedValue != null) {
			PDDocument pdf = pdfList.get(selectedValue);
			loadedRenderer = new PDFRenderer(pdf);
			lPDFIcon = new ImageIcon(loadedRenderer.renderImage(0));
			curLPage = 1;
			lNumPages = pdf.getNumberOfPages();
		}
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Render an image for the selected page of the selected loaded PDF.
	 * 
	 * @param selectedIndex the index of the selected PDF in the list
	 * @param pageNum the number of the page to be rendered
	 * @throws IOException if the selected PDF cannot be read
	 */
	public void changeLoadedPDFPreviewPage(int selectedIndex, int pageNum) throws IOException {
		if (pageNum < 1) {
			pageNum = 1;
		} else if (pageNum > lNumPages) {
			pageNum = lNumPages;
		}
		
		lPDFIcon = null;
		
		if (selectedIndex != -1) {
			lPDFIcon = new ImageIcon(loadedRenderer.renderImage(pageNum - 1));
			curLPage = pageNum;
		}
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Render an image for the first page of the finalized PDF.
	 * 
	 * @throws IOException if the loaded PDF from where the page came from cannot be read
	 */
	public void showFinalPDFPreview() throws IOException {
		fPDFIcon = null;
		curFPage = -1;
		
		if (numPageRanges > 0) {
			PDDocument pdf = pdfList.get(pageEntryPDFList.get(0));
			finalRenderer = new PDFRenderer(pdf);
			fPDFIcon = new ImageIcon(finalRenderer.renderImage(0));
			curFPage = 1;
		}
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Render an image for the selected page of the finalized PDF.
	 * 
	 * @param pageNum the number of the selected page to be rendered
	 * @throws IOException if the loaded PDF from where the page came from cannot be read
	 */
	public void changeFinalPDFPreviewPage(int pageNum) throws IOException {
		if (pageNum < 1) {
			pageNum = 1;
		} else if (pageNum > fNumPages) {
			pageNum = fNumPages;
		}
		
		fPDFIcon = null;
		
		int cPage = 1;
		int rangeNum = 0;
		int nextSize = pageRangeList.get(rangeNum).size();;
		
		while (cPage + nextSize <= pageNum) {
			cPage += nextSize;
			if (rangeNum + 1 < numPageRanges) {
				nextSize = pageRangeList.get(++rangeNum).size();
			}
		}
		
		PDDocument pdf = pdfList.get(pageEntryPDFList.get(rangeNum));
		int pageNumPos = pageRangeList.get(rangeNum).get(pageNum - cPage);
		finalRenderer = new PDFRenderer(pdf);
		fPDFIcon = new ImageIcon(finalRenderer.renderImage(pageNumPos - 1));
		curFPage = pageNum;
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Create the new PDF, and save it.
	 * 
	 * @param saveFile the file to save it as
	 * @throws IOException if it cannot save the file
	 */
	public void makeFinalizedPDF(File saveFile) throws IOException {

		PDDocument doc = null;
		PDDocument newdoc = new PDDocument();
		
		for (int i = 0; i < pageEntryPDFList.size(); ++i) {
			doc = pdfList.get(pageEntryPDFList.get(i));
			
			if (doc.isEncrypted()) {
				System.out.println("Error: Encrypted PDF");
				System.exit(1);
			}
			
			List<Integer> pRange = pageRangeList.get(i);
			PDFCloneUtility pdfCloner = new PDFCloneUtility(newdoc);
			for (int pNum : pRange) {
				PDPage page = doc.getPage(pNum - 1);
				COSDictionary clonedDict = (COSDictionary) pdfCloner.cloneForNewDocument(page);
				newdoc.addPage(new PDPage(clonedDict));
			}
		}
		
		newdoc.save(saveFile);
		if (newdoc != null) {
			newdoc.close();
		}
	}
	
	/**
	 * Returns the PDF list model.
	 * 
	 * @return the PDF list model, as a {@link DefaultListModel}
	 */
	public DefaultListModel<String> getPDFListModel() {
		return pdfListModel;
	}
	
	/**
	 * Returns the PDF ComboBoxModel.
	 * 
	 * @return the PDF ComboBoxModel, as a {@link DefaultComboBoxModel}
	 */
	public DefaultComboBoxModel<String> getPDFComboBoxModel() {
		return pdfComboBoxModel;
	}
	
	/**
	 * Returns the page range model.
	 * 
	 * @return the page range model, as a {@link DefaultListModel}
	 */
	public DefaultListModel<String> getPageRangeModel() {
		return pageRangeModel;
	}
	
	/**
	 * Returns the number of page ranges.
	 * 
	 * @return the number of page ranges
	 */
	public int getNumPageRanges() {
		return numPageRanges;
	}
	
	/**
	 * Returns the total number of pages in the selected loaded PDF.
	 * 
	 * @return the number of pages
	 */
	public int getLNumPages() {
		return lNumPages;
	}
	
	/**
	 * Returns the image for the selected loaded PDF page.
	 * 
	 * @return the image, as an {@link ImageIcon}
	 */
	public ImageIcon getLPDFImage() {
		return lPDFIcon;
	}
	
	/**
	 * Returns the current page number of the selected loaded PDF.
	 * 
	 * @return the page number
	 */
	public int getCurLPage() {
		return curLPage;
	}
	
	/**
	 * Returns the total number of pages in the finalized PDF.
	 * 
	 * @return the number of pages
	 */
	public int getFNumPages() {
		return fNumPages;
	}
	
	/**
	 * Returns the image for the selected finalized PDF page.
	 * 
	 * @return the image, as an {@link ImageIcon}
	 */
	public ImageIcon getFPDFImage() {
		return fPDFIcon;
	}
	
	/**
	 * Returns the current page number of the finalized PDF.
	 * 
	 * @return the page number
	 */
	public int getCurFPage() {
		return curFPage;
	}
	
}

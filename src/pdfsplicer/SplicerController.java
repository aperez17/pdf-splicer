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

/**
 * This class acts as the controller for the MVC pattern. 
 * It updates the model when notified by the view.
 * 
 * @author Abeer Ahmed
 */
public class SplicerController {
	
	private SplicerModel model;
	
	/**
	 * Create a new controller.
	 * 
	 * @param model the {@link SplicerModel}
	 */
	public SplicerController(SplicerModel model) {
		this.model = model;
	}
	
	/**
	 * Set the view.
	 * 
	 * @param view the {@link SplicerView}
	 */
	public void setView(SplicerView view) {
	}
	
	/**
	 * Tell the model to add a PDF.
	 * 
	 * @return a response error, as a {@link UIConstants}
	 */
	public UIConstants loadPDF(File[] selectedFiles) {
		if (selectedFiles == null) {
			return UIConstants.ERROR_NOERROR;
		}
		
		UIConstants error = UIConstants.ERROR_NOERROR;
		
		for (int i = 0; i < selectedFiles.length; ++i) {
			String fileName = selectedFiles[i].getName();
			if (fileName.substring(fileName.length() - 4).equals(".pdf")) {
				try {
					boolean success = model.addPDF(selectedFiles[i]);
					if (!success) {
						error = UIConstants.ERROR_ALREADY_LOADED;
					}
				} catch (IOException e) {
					error = UIConstants.ERROR_UNREADABLE;
				}
			} else {
				error = UIConstants.ERROR_NOTPDF;
			}
		}
		
		return error;
	}
	
	/**
	 * Tell the model to remove one or more PDFs.
	 * 
	 * @param selectedIndices the indices of the PDFs to remove
	 * @return a response error, as a {@link UIConstants}
	 */
	public UIConstants removePDF(int[] selectedIndices) {
		UIConstants error = UIConstants.ERROR_NOERROR;
		
		if (selectedIndices.length == 0) {
			return UIConstants.ERROR_NO_SELECTEDPDF;
		}
		
		try {
			boolean removeMulti = selectedIndices.length > 1;
			for (int i = selectedIndices.length - 1; i >= 0; --i) {
				model.removePDF(selectedIndices[i], removeMulti);
			}
			if (removeMulti) model.showFinalPDFPreview();
		} catch (IOException e) {
			error = UIConstants.ERROR_UNREADABLE;
		}
		
		return error;
	}
	
	/**
	 * Tell the model to remove all PDFs.
	 * 
	 * @return a response error, as a {@link UIConstants}
	 */
	public UIConstants removeAllPDF() {
		try {
			model.removeAllPDF();
		} catch (IOException e) {
			return UIConstants.ERROR_UNREADABLE;
		}
		
		return UIConstants.ERROR_NOERROR;
	}
	
	/**
	 * Tell the model to add a page range from a selected loaded PDF.
	 * 
	 * @param selectedIndex the index of the PDF on the list
	 * @param pageRange the range of pages as a string
	 * @return a response error, as a {@link UIConstants}
	 */
	public UIConstants addPageRange(int selectedIndex, String pageRange) {
		UIConstants error = UIConstants.ERROR_NOERROR;
		
		if (selectedIndex == -1) {
			error = UIConstants.ERROR_NO_SELECTEDPDF;
		} else {
			boolean validRange = false;
			try {
				validRange = model.addPageRange(selectedIndex, pageRange);
			} catch (IOException e) {
				error = UIConstants.ERROR_UNREADABLE;
			}
			if (!validRange) {
				error = UIConstants.ERROR_INVALID_PAGENUM;
			}
		}
		
		return error;
	}
	
	/**
	 * Tell the model to remove one or more page ranges.
	 * 
	 * @param selectedIndices the indices of the page ranges on the list
	 * @return a response error, as a {@link UIConstants}
	 */
	public UIConstants removePageRange(int[] selectedIndices) {
		UIConstants error = UIConstants.ERROR_NOERROR;
		
		if (selectedIndices.length == 0) {
			return UIConstants.ERROR_NO_SELECTEDPAGERANGE;
		}
		
		try {
			boolean removeMulti = selectedIndices.length > 1;
			for (int i = selectedIndices.length - 1; i >= 0; --i) {
				model.removePageRange(selectedIndices[i], removeMulti);
			}
			if (removeMulti) model.showFinalPDFPreview();
		} catch (IOException e) {
			error = UIConstants.ERROR_UNREADABLE;
		}
		
		return error;
	}
	
	/**
	 * Tell the model to remove all page ranges.
	 * 
	 * @return a response error, as a {@link UIConstants}
	 */
	public UIConstants removeAllPageRange() {
		UIConstants error = UIConstants.ERROR_NOERROR;
		
		try {
			model.removeAllPageRange();
		} catch (IOException e) {
			error = UIConstants.ERROR_UNREADABLE;
		}
		
		return error;
	}
	
	/**
	 * Tell the model to render the first page of the selected loaded PDF.
	 * 
	 * @param selectedValue the name of the selected PDF
	 * @return a response error, as a {@link UIConstants}
	 */
	public UIConstants showPDFPreview(String selectedValue) {
		UIConstants error = UIConstants.ERROR_NOERROR;
		
		try {
			model.showLoadedPDFPreview(selectedValue);
		} catch (IOException e) {
			error = UIConstants.ERROR_UNREADABLE;
		}
		
		return error;
	}
	
	/**
	 * tell the model to render a selected page of the selected loaded PDF, or finalized PDF.
	 * 
	 * @param pdfPreview which preview, determined by {@link UIConstants}
	 * @param selectedIndex the index of the selected PDF in the list, or -1 if either no PDF is selected, or the finalized PDF is chosen
	 * @param pageNum the number of the page to be rendered
	 * @return a response error, as a {@link UIConstants}
	 */
	public UIConstants changePDFPreviewPage(UIConstants pdfPreview, int selectedIndex, int pageNum) {
		UIConstants error = UIConstants.ERROR_NOERROR;
		
		try {
			if (pdfPreview == UIConstants.IMAGE_LOADEDPDF) {
				if (selectedIndex == -1) {
					error = UIConstants.ERROR_NO_SELECTEDPDF;
				}
				model.changeLoadedPDFPreviewPage(selectedIndex, pageNum);
			} else if (pdfPreview == UIConstants.IMAGE_FINALPDF) {
				if (model.getNumPageRanges() < 1) {
					error = UIConstants.ERROR_NO_SELECTEDPAGERANGE;
				}
				model.changeFinalPDFPreviewPage(pageNum);
			}
		} catch (IOException e) {
			error = UIConstants.ERROR_UNREADABLE;
		}
		
		return error;
	}

	/**
	 * Finalize and create the new PDF, and save it as a selected file.
	 * 
	 * @return a response error, as a {@link UIConstants}
	 */
	public UIConstants createPDF(File selectedFile) {
		try {
			model.makeFinalizedPDF(selectedFile);
		} catch (IOException e) {
			return UIConstants.ERROR_CANNOT_SAVE;
		}
		
		return UIConstants.ERROR_NOERROR;
	}
	
}

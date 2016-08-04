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

import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Font;
import java.awt.BorderLayout;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import java.awt.Component;

/**
 * This class acts as the view for the MVC pattern. It is responsible for displaying information
 * from the model on the UI using Swing, and using listeners to notify the controller about
 * changes to the model.
 * 
 * @author Abeer Ahmed
 */
public class SplicerView implements Observer {

	// MVC Layout
	private SplicerModel model;
	private SplicerController controller;
	
	// Main Frame
	private JFrame frame;
	private JFileChooser fileChooser;
	
	// Tabbed Pane and Panels
	private JTabbedPane pnlTabs;
	private JPanel pnlPDFLoader;
	private JPanel pnlPageRange;
	private JPanel pnlFinalize;
	
	// Loaded PDF List
	private JScrollPane pnlPDFList;
	private JList<String> lstPDFList;
	
	// Loaded PDF Preview
	private JScrollPane pnlLPDFPreview;
	private JLabel lblLNumPages;
	private JLabel lblLPDFPreview;
	
	// Loaded PDF Preview Navigation
	private JPanel pnlLPageSelect;
	private JButton btnFPrevLPage;
	private JButton btnPrevLPage;
	private JTextField txtfldLPage;
	private JButton btnNextLPage;
	private JButton btnFNextLPage;
	
	// PDF Loading / Removal Buttons
	private JPanel pnlLoadingOptions;
	private JButton btnLoadPDF;
	private JButton btnRemovePDF;
	private JButton btnRemoveAll;
	
	// Page Range List
	private JScrollPane pnlPageRangeList;
	private JList<String> lstPageRange;
	
	// Page Range Entry Options
	private JPanel pnlPageRangeSelection;
	private JButton btnRemovePageR;
	private JTextField txtfldPageRange;
	private JButton btnRemoveAllPageR;

	// Button to create the new PDF
	private JButton btnCreateButton;
	
	// Final PDF Preview
	private JScrollPane pnlFPDFPreview;
	private JLabel lblFNumPages;
	private JLabel lblFPDFPreview;
	
	// Final PDF Preview Navigation
	private JPanel pnlFPageSelect;
	private JButton btnFPrevFPage;
	private JButton btnPrevFPage;
	private JTextField txtfldFPage;
	private JButton btnNextFPage;
	private JButton btnFNextFPage;
	
	/**
	 * Create a new view.
	 * 
	 * @param model the {@link SplicerModel}
	 * @param controller the {@link SplicerController}
	 */
	public SplicerView(SplicerModel model, SplicerController controller) {
		this.model = model;
		this.controller = controller;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		
		// Main JFrame
		frame = new JFrame();
		frame.setBounds(100, 100, 750, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("PDFSplicer");
		frame.setVisible(true);
		
		// FileChooser dialog to open and save PDFs
		fileChooser = new JFileChooser(new File(System.getProperty("user.home")));
		fileChooser.setMultiSelectionEnabled(true);
		
		// Tabbed pane that sorts the different functionality
		pnlTabs = new JTabbedPane(JTabbedPane.TOP);
		
		// Panel for loading new PDFs
		pnlPDFLoader = new JPanel();
		pnlTabs.addTab("Loaded PDFs", null, pnlPDFLoader, null);
		GridBagLayout gbl_pnlPDFLoader = new GridBagLayout();
		gbl_pnlPDFLoader.columnWidths = new int[] {0, 0};
		gbl_pnlPDFLoader.rowHeights = new int[] {0, 0};
		gbl_pnlPDFLoader.columnWeights = new double[]{1.0, 1.0};
		gbl_pnlPDFLoader.rowWeights = new double[]{1.0, 0.0};
		pnlPDFLoader.setLayout(gbl_pnlPDFLoader);
		
		pnlPDFList = new JScrollPane();
		GridBagConstraints gbc_pnlPDFList = new GridBagConstraints();
		gbc_pnlPDFList.weightx = 0.5;
		gbc_pnlPDFList.weighty = 1.0;
		gbc_pnlPDFList.insets = new Insets(10, 10, 5, 10);
		gbc_pnlPDFList.fill = GridBagConstraints.BOTH;
		gbc_pnlPDFList.gridx = 0;
		gbc_pnlPDFList.gridy = 0;
		pnlPDFLoader.add(pnlPDFList, gbc_pnlPDFList);
		
		lstPDFList = new JList<String>();
		pnlPDFList.setViewportView(lstPDFList);
		lstPDFList.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lstPDFList.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		lstPDFList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				UIConstants error = controller.showPDFPreview(lstPDFList.getSelectedValue());
				errorMessage(error);
			}
		});
		lstPDFList.setModel(model.getPDFListModel());
		
		pnlLPDFPreview = new JScrollPane();
		pnlLPDFPreview.setViewportBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		lblLPDFPreview = new JLabel("");
		pnlLPDFPreview.setViewportView(lblLPDFPreview);
		GridBagConstraints gbc_pnlLPDFPreview = new GridBagConstraints();
		gbc_pnlLPDFPreview.fill = GridBagConstraints.BOTH;
		gbc_pnlLPDFPreview.insets = new Insets(10, 10, 5, 10);
		gbc_pnlLPDFPreview.gridx = 1;
		gbc_pnlLPDFPreview.gridy = 0;
		gbc_pnlLPDFPreview.weightx = 0.5;
		gbc_pnlLPDFPreview.weighty = 1.0;
		pnlPDFLoader.add(pnlLPDFPreview, gbc_pnlLPDFPreview);
		
		lblLNumPages = new JLabel("");
		lblLNumPages.setHorizontalAlignment(SwingConstants.CENTER);
		pnlLPDFPreview.setColumnHeaderView(lblLNumPages);
		
		// Panel for buttons related to loading / removing PDFs
		pnlLoadingOptions = new JPanel();
		GridBagLayout gbl_pnlLoadingOptions = new GridBagLayout();
		gbl_pnlLoadingOptions.columnWidths = new int[] {0, 0, 0};
		gbl_pnlLoadingOptions.rowHeights = new int[] {0};
		gbl_pnlLoadingOptions.columnWeights = new double[]{0.0, 0.0, 0.0};
		gbl_pnlLoadingOptions.rowWeights = new double[]{0.0};
		pnlLoadingOptions.setLayout(gbl_pnlLoadingOptions);
		GridBagConstraints gbc_pnlLoadingOptions = new GridBagConstraints();
		gbc_pnlLoadingOptions.fill = GridBagConstraints.HORIZONTAL;
		gbc_pnlLoadingOptions.insets = new Insets(0, 10, 10, 10);
		gbc_pnlLoadingOptions.gridx = 0;
		gbc_pnlLoadingOptions.gridy = 1;
		gbc_pnlLoadingOptions.weightx = 0.5;
		pnlPDFLoader.add(pnlLoadingOptions, gbc_pnlLoadingOptions);
		
		btnLoadPDF = new JButton("Load");
		GridBagConstraints gbc_btnLoadPDF = new GridBagConstraints();
		gbc_btnLoadPDF.weighty = 1.0;
		gbc_btnLoadPDF.weightx = 0.3;
		gbc_btnLoadPDF.fill = GridBagConstraints.BOTH;
		gbc_btnLoadPDF.insets = new Insets(0, 0, 0, 5);
		gbc_btnLoadPDF.gridx = 0;
		gbc_btnLoadPDF.gridy = 0;
		pnlLoadingOptions.add(btnLoadPDF, gbc_btnLoadPDF);
		
		// Button to load new PDF(s)
		btnLoadPDF.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fileChooser.setDialogTitle("Load PDF");
				if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
					UIConstants error = controller.loadPDF(fileChooser.getSelectedFiles());
					errorMessage(error);
					lstPDFList.setSelectedIndex(lstPDFList.getModel().getSize() - 1);
				}
			}
		});
		
		// Button to remove all loaded PDFs
		btnRemoveAll = new JButton("Remove All");
		btnRemoveAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int choice = JOptionPane.showConfirmDialog(frame, "Are you sure you want to remove all loaded PDFs?",
						"Remove All Confirmation", JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
					UIConstants error = controller.removeAllPDF();
					errorMessage(error);
				}
			}
		});
		
		// Button to remove loaded PDF(s)
		btnRemovePDF = new JButton("Remove");
		GridBagConstraints gbc_btnRemovePDF = new GridBagConstraints();
		gbc_btnRemovePDF.weightx = 0.3;
		gbc_btnRemovePDF.fill = GridBagConstraints.BOTH;
		gbc_btnRemovePDF.weighty = 1.0;
		gbc_btnRemovePDF.gridx = 1;
		gbc_btnRemovePDF.gridy = 0;
		pnlLoadingOptions.add(btnRemovePDF, gbc_btnRemovePDF);
		btnRemovePDF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selIndex = lstPDFList.getSelectedIndex();
				UIConstants error = controller.removePDF(lstPDFList.getSelectedIndices());
				errorMessage(error);
				
				if (lstPDFList.getModel().getSize() > 0 && selIndex != -1) {
					lstPDFList.setSelectedIndex(selIndex = selIndex == 0 ? 0 : selIndex - 1);
				}
			}
		});
		
		GridBagConstraints gbc_btnRemoveAll = new GridBagConstraints();
		gbc_btnRemoveAll.weighty = 1.0;
		gbc_btnRemoveAll.weightx = 0.3;
		gbc_btnRemoveAll.fill = GridBagConstraints.BOTH;
		gbc_btnRemoveAll.insets = new Insets(0, 5, 0, 0);
		gbc_btnRemoveAll.gridx = 2;
		gbc_btnRemoveAll.gridy = 0;
		pnlLoadingOptions.add(btnRemoveAll, gbc_btnRemoveAll);
		
		pnlLPageSelect = new JPanel();
		GridBagConstraints gbc_pnlLPageSelect = new GridBagConstraints();
		gbc_pnlLPageSelect.fill = GridBagConstraints.HORIZONTAL;
		gbc_pnlLPageSelect.insets = new Insets(0, 10, 10, 10);
		gbc_pnlLPageSelect.gridx = 1;
		gbc_pnlLPageSelect.gridy = 1;
		gbc_pnlLPageSelect.weightx = 0.5;
		pnlPDFLoader.add(pnlLPageSelect, gbc_pnlLPageSelect);
		GridBagLayout gbl_pnlLPageSelect = new GridBagLayout();
		gbl_pnlLPageSelect.columnWidths = new int[] {0, 0, 0, 0, 0};
		gbl_pnlLPageSelect.rowHeights = new int[] {0};
		gbl_pnlLPageSelect.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_pnlLPageSelect.rowWeights = new double[]{0.0};
		pnlLPageSelect.setLayout(gbl_pnlLPageSelect);
		
		// Go back a page
		btnPrevLPage = new JButton("<");
		btnPrevLPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				UIConstants error = controller.changePDFPreviewPage(UIConstants.IMAGE_LOADEDPDF, lstPDFList.getSelectedIndex(), model.getCurLPage() - 1);
				errorMessage(error);
			}
		});
		
		// Go back 10 pages
		btnFPrevLPage = new JButton("<<");
		btnFPrevLPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				UIConstants error = controller.changePDFPreviewPage(UIConstants.IMAGE_LOADEDPDF, lstPDFList.getSelectedIndex(), model.getCurLPage() - 10);
				errorMessage(error);
			}
		});
		
		GridBagConstraints gbc_btnFPrevLPage = new GridBagConstraints();
		gbc_btnFPrevLPage.fill = GridBagConstraints.BOTH;
		gbc_btnFPrevLPage.weightx = 0.2;
		gbc_btnFPrevLPage.insets = new Insets(0, 0, 0, 5);
		gbc_btnFPrevLPage.gridx = 0;
		gbc_btnFPrevLPage.gridy = 0;
		pnlLPageSelect.add(btnFPrevLPage, gbc_btnFPrevLPage);
		GridBagConstraints gbc_btnPrevLPage = new GridBagConstraints();
		gbc_btnPrevLPage.weighty = 1.0;
		gbc_btnPrevLPage.weightx = 0.1;
		gbc_btnPrevLPage.fill = GridBagConstraints.BOTH;
		gbc_btnPrevLPage.insets = new Insets(0, 0, 0, 5);
		gbc_btnPrevLPage.gridx = 1;
		gbc_btnPrevLPage.gridy = 0;
		pnlLPageSelect.add(btnPrevLPage, gbc_btnPrevLPage);
		
		// Type in a page number
		txtfldLPage = new JTextField();
		txtfldLPage.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_txtfldLPage = new GridBagConstraints();
		gbc_txtfldLPage.weightx = 0.4;
		gbc_txtfldLPage.weighty = 1.0;
		gbc_txtfldLPage.fill = GridBagConstraints.BOTH;
		gbc_txtfldLPage.gridx = 2;
		gbc_txtfldLPage.gridy = 0;
		pnlLPageSelect.add(txtfldLPage, gbc_txtfldLPage);
		txtfldLPage.setColumns(10);
		txtfldLPage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					int reqPageNum = Integer.parseInt(txtfldLPage.getText());
					UIConstants error = controller.changePDFPreviewPage(UIConstants.IMAGE_LOADEDPDF, lstPDFList.getSelectedIndex(), reqPageNum);
					errorMessage(error);
				} catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(frame, "Invalid Page Number.", "Error Displaying PDF", JOptionPane.ERROR_MESSAGE);
					int curPage = model.getCurLPage();
					txtfldLPage.setText(curPage == -1 ? "" : Integer.toString(curPage));
				}
			}
		});
		
		// Go forward a page
		btnNextLPage = new JButton(">");
		btnNextLPage.setAlignmentX(Component.RIGHT_ALIGNMENT);
		btnNextLPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UIConstants error = controller.changePDFPreviewPage(UIConstants.IMAGE_LOADEDPDF, lstPDFList.getSelectedIndex(), model.getCurLPage() + 1);
				errorMessage(error);
			}
		});
		
		GridBagConstraints gbc_btnNextLPage = new GridBagConstraints();
		gbc_btnNextLPage.insets = new Insets(0, 5, 0, 0);
		gbc_btnNextLPage.weightx = 0.1;
		gbc_btnNextLPage.weighty = 1.0;
		gbc_btnNextLPage.fill = GridBagConstraints.BOTH;
		gbc_btnNextLPage.gridx = 3;
		gbc_btnNextLPage.gridy = 0;
		pnlLPageSelect.add(btnNextLPage, gbc_btnNextLPage);
		
		// Go forward 10 pages
		btnFNextLPage = new JButton(">>");
		btnFNextLPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UIConstants error = controller.changePDFPreviewPage(UIConstants.IMAGE_LOADEDPDF, lstPDFList.getSelectedIndex(), model.getCurLPage() + 10);
				errorMessage(error);
			}
		});
		
		GridBagConstraints gbc_btnFNextLPage = new GridBagConstraints();
		gbc_btnFNextLPage.fill = GridBagConstraints.BOTH;
		gbc_btnFNextLPage.weightx = 0.2;
		gbc_btnFNextLPage.insets = new Insets(0, 5, 0, 0);
		gbc_btnFNextLPage.gridx = 4;
		gbc_btnFNextLPage.gridy = 0;
		pnlLPageSelect.add(btnFNextLPage, gbc_btnFNextLPage);
		
		// Panel where the label and text input for entering page ranges are
		pnlPageRange = new JPanel();
		pnlTabs.addTab("Page Selection", null, pnlPageRange, null);
		GridBagLayout gbl_pnlPageRange = new GridBagLayout();
		gbl_pnlPageRange.columnWidths = new int[] {0};
		gbl_pnlPageRange.rowHeights = new int[] {0, 0, 0};
		gbl_pnlPageRange.columnWeights = new double[]{1.0};
		gbl_pnlPageRange.rowWeights = new double[]{0.0, 1.0, 0.0};
		pnlPageRange.setLayout(gbl_pnlPageRange);
		
		// Dropdown menu to select the PDF for page ranges to be selected
		JComboBox<String> cmboBxPDFSelector1 = new JComboBox<String>();
		cmboBxPDFSelector1.setModel(model.getPDFComboBoxModel());
		
		GridBagConstraints gbc_cmboBxPDFSelector1 = new GridBagConstraints();
		gbc_cmboBxPDFSelector1.weightx = 1.0;
		gbc_cmboBxPDFSelector1.fill = GridBagConstraints.HORIZONTAL;
		gbc_cmboBxPDFSelector1.insets = new Insets(10, 10, 10, 10);
		gbc_cmboBxPDFSelector1.gridx = 0;
		gbc_cmboBxPDFSelector1.gridy = 0;
		pnlPageRange.add(cmboBxPDFSelector1, gbc_cmboBxPDFSelector1);
		
		pnlPageRangeList = new JScrollPane();
		pnlPageRangeList.setViewportBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_pnlPageRangeList = new GridBagConstraints();
		gbc_pnlPageRangeList.weighty = 1.0;
		gbc_pnlPageRangeList.weightx = 1.0;
		gbc_pnlPageRangeList.insets = new Insets(0, 10, 5, 10);
		gbc_pnlPageRangeList.fill = GridBagConstraints.BOTH;
		gbc_pnlPageRangeList.gridx = 0;
		gbc_pnlPageRangeList.gridy = 1;
		pnlPageRange.add(pnlPageRangeList, gbc_pnlPageRangeList);
		
		// List of page ranges displayed
		lstPageRange = new JList<String>();
		pnlPageRangeList.setViewportView(lstPageRange);
		lstPageRange.setBorder(null);
		
		lstPageRange.setModel(model.getPageRangeModel());
		
		pnlPageRangeSelection = new JPanel();
		GridBagConstraints gbc_pnlPageRangeSelection = new GridBagConstraints();
		gbc_pnlPageRangeSelection.fill = GridBagConstraints.HORIZONTAL;
		gbc_pnlPageRangeSelection.weightx = 1.0;
		gbc_pnlPageRangeSelection.insets = new Insets(0, 10, 10, 10);
		gbc_pnlPageRangeSelection.gridx = 0;
		gbc_pnlPageRangeSelection.gridy = 2;
		pnlPageRange.add(pnlPageRangeSelection, gbc_pnlPageRangeSelection);
		GridBagLayout gbl_pnlPageRangeSelection = new GridBagLayout();
		gbl_pnlPageRangeSelection.columnWidths = new int[] {0, 0, 0};
		gbl_pnlPageRangeSelection.rowHeights = new int[] {0};
		gbl_pnlPageRangeSelection.columnWeights = new double[]{0.0, 0.0, 0.0};
		gbl_pnlPageRangeSelection.rowWeights = new double[]{0.0};
		pnlPageRangeSelection.setLayout(gbl_pnlPageRangeSelection);
		
		// Remove page range(s)
		btnRemovePageR = new JButton("Remove");
		btnRemovePageR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selIndex = lstPageRange.getSelectedIndex();
				UIConstants error = controller.removePageRange(lstPageRange.getSelectedIndices());
				errorMessage(error);
				
				if (lstPageRange.getModel().getSize() > 0 && selIndex != -1) {
					lstPageRange.setSelectedIndex(selIndex = selIndex == 0 ? 0 : selIndex - 1);
				}
			}
		});
		
		GridBagConstraints gbc_btnRemovePageR = new GridBagConstraints();
		gbc_btnRemovePageR.fill = GridBagConstraints.BOTH;
		gbc_btnRemovePageR.weighty = 1.0;
		gbc_btnRemovePageR.weightx = 0.1;
		gbc_btnRemovePageR.gridx = 0;
		gbc_btnRemovePageR.gridy = 0;
		pnlPageRangeSelection.add(btnRemovePageR, gbc_btnRemovePageR);
		
		// User input for page ranges
		txtfldPageRange = new JTextField();
		txtfldPageRange.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_txtfldPageRange = new GridBagConstraints();
		gbc_txtfldPageRange.weightx = 0.8;
		gbc_txtfldPageRange.weighty = 1.0;
		gbc_txtfldPageRange.fill = GridBagConstraints.BOTH;
		gbc_txtfldPageRange.insets = new Insets(0, 5, 0, 5);
		gbc_txtfldPageRange.gridx = 1;
		gbc_txtfldPageRange.gridy = 0;
		pnlPageRangeSelection.add(txtfldPageRange, gbc_txtfldPageRange);
		txtfldPageRange.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				UIConstants error = controller.addPageRange(cmboBxPDFSelector1.getSelectedIndex(), txtfldPageRange.getText());
				errorMessage(error);
			}
		});
		txtfldPageRange.setColumns(10);
		
		// Remove all page ranges
		btnRemoveAllPageR = new JButton("Remove All");
		btnRemoveAllPageR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int choice = JOptionPane.showConfirmDialog(frame, "Are you sure you want to remove all entered page ranges?",
						"Remove All Confirmation", JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
					UIConstants error = controller.removeAllPageRange();
					errorMessage(error);
				}
			}
		});
		
		GridBagConstraints gbc_btnRemoveAllPageR = new GridBagConstraints();
		gbc_btnRemoveAllPageR.fill = GridBagConstraints.BOTH;
		gbc_btnRemoveAllPageR.weighty = 1.0;
		gbc_btnRemoveAllPageR.weightx = 0.1;
		gbc_btnRemoveAllPageR.gridx = 2;
		gbc_btnRemoveAllPageR.gridy = 0;
		pnlPageRangeSelection.add(btnRemoveAllPageR, gbc_btnRemoveAllPageR);
		
		// Panel for the final step in making the PDF
		pnlFinalize = new JPanel();
		pnlTabs.addTab("Finalize", null, pnlFinalize, null);
		GridBagLayout gbl_pnlFinalize = new GridBagLayout();
		gbl_pnlFinalize.columnWidths = new int[] {0};
		gbl_pnlFinalize.rowHeights = new int[] {0, 0, 0};
		gbl_pnlFinalize.columnWeights = new double[]{1.0};
		gbl_pnlFinalize.rowWeights = new double[]{0.0, 1.0, 0.0};
		pnlFinalize.setLayout(gbl_pnlFinalize);
		
		// Button to open a dialog to save the PDF
		btnCreateButton = new JButton("Create PDF");
		GridBagConstraints gbc_btnCreateButton = new GridBagConstraints();
		gbc_btnCreateButton.weightx = 1.0;
		gbc_btnCreateButton.insets = new Insets(10, 10, 10, 10);
		gbc_btnCreateButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnCreateButton.gridx = 0;
		gbc_btnCreateButton.gridy = 0;
		pnlFinalize.add(btnCreateButton, gbc_btnCreateButton);
		btnCreateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (model.getNumPageRanges() < 1) {
					JOptionPane.showMessageDialog(frame, "No Pages Selected.", "Error creating PDF", JOptionPane.ERROR_MESSAGE);
				} else {
					fileChooser.setDialogTitle("Save PDF");
					if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
						UIConstants error = controller.createPDF(fileChooser.getSelectedFile());
						errorMessage(error);
					}
				}
			}
		});
		
		// Panel Displaying Finalized PDF
		pnlFPDFPreview = new JScrollPane();
		GridBagConstraints gbc_pnlFPDFPreview = new GridBagConstraints();
		gbc_pnlFPDFPreview.weighty = 1.0;
		gbc_pnlFPDFPreview.weightx = 1.0;
		gbc_pnlFPDFPreview.insets = new Insets(0, 10, 5, 10);
		gbc_pnlFPDFPreview.fill = GridBagConstraints.BOTH;
		gbc_pnlFPDFPreview.gridx = 0;
		gbc_pnlFPDFPreview.gridy = 1;
		pnlFinalize.add(pnlFPDFPreview, gbc_pnlFPDFPreview);
		
		lblFPDFPreview = new JLabel("");
		pnlFPDFPreview.setViewportView(lblFPDFPreview);
		lblFPDFPreview.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		lblFNumPages = new JLabel("");
		lblFNumPages.setHorizontalAlignment(SwingConstants.CENTER);
		pnlFPDFPreview.setColumnHeaderView(lblFNumPages);
		
		pnlFPageSelect = new JPanel();
		GridBagConstraints gbc_pnlFPageSelect = new GridBagConstraints();
		gbc_pnlFPageSelect.fill = GridBagConstraints.HORIZONTAL;
		gbc_pnlFPageSelect.weightx = 1.0;
		gbc_pnlFPageSelect.insets = new Insets(0, 10, 10, 10);
		gbc_pnlFPageSelect.gridx = 0;
		gbc_pnlFPageSelect.gridy = 2;
		pnlFinalize.add(pnlFPageSelect, gbc_pnlFPageSelect);
		GridBagLayout gbl_pnlFPageSelect = new GridBagLayout();
		gbl_pnlFPageSelect.columnWidths = new int[] {0, 0, 0, 0, 0};
		gbl_pnlFPageSelect.rowHeights = new int[] {0};
		gbl_pnlFPageSelect.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_pnlFPageSelect.rowWeights = new double[]{0.0};
		pnlFPageSelect.setLayout(gbl_pnlFPageSelect);

		// Go back 10 pages
		btnFPrevFPage = new JButton("<<");
		btnFPrevFPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				UIConstants error = controller.changePDFPreviewPage(UIConstants.IMAGE_FINALPDF, -1, model.getCurFPage() - 10);
				errorMessage(error);
			}
		});
		
		GridBagConstraints gbc_btnFPrevFPage = new GridBagConstraints();
		gbc_btnFPrevFPage.weightx = 0.2;
		gbc_btnFPrevFPage.weighty = 1.0;
		gbc_btnFPrevFPage.fill = GridBagConstraints.BOTH;
		gbc_btnFPrevFPage.insets = new Insets(0, 0, 0, 5);
		gbc_btnFPrevFPage.gridx = 0;
		gbc_btnFPrevFPage.gridy = 0;
		pnlFPageSelect.add(btnFPrevFPage, gbc_btnFPrevFPage);

		// Go back a page
		btnPrevFPage = new JButton("<");
		btnPrevFPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				UIConstants error = controller.changePDFPreviewPage(UIConstants.IMAGE_FINALPDF, -1, model.getCurFPage() - 1);
				errorMessage(error);
			}
		});
		
		GridBagConstraints gbc_btnPrevFPage = new GridBagConstraints();
		gbc_btnPrevFPage.weightx = 0.1;
		gbc_btnPrevFPage.weighty = 1.0;
		gbc_btnPrevFPage.fill = GridBagConstraints.BOTH;
		gbc_btnPrevFPage.insets = new Insets(0, 0, 0, 5);
		gbc_btnPrevFPage.gridx = 1;
		gbc_btnPrevFPage.gridy = 0;
		pnlFPageSelect.add(btnPrevFPage, gbc_btnPrevFPage);
		
		// Enter a page number
		txtfldFPage = new JTextField();
		txtfldFPage.setHorizontalAlignment(SwingConstants.CENTER);
		txtfldFPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int reqPageNum = Integer.parseInt(txtfldFPage.getText());
					UIConstants error = controller.changePDFPreviewPage(UIConstants.IMAGE_FINALPDF, -1, reqPageNum);
					errorMessage(error);
				} catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(frame, "Invalid Page Number.", "Error Displaying PDF", JOptionPane.ERROR_MESSAGE);
					int curPage = model.getCurFPage();
					txtfldFPage.setText(curPage == -1 ? "" : Integer.toString(curPage));
				}
			}
		});
		
		GridBagConstraints gbc_txtfldFPage = new GridBagConstraints();
		gbc_txtfldFPage.weighty = 1.0;
		gbc_txtfldFPage.weightx = 0.4;
		gbc_txtfldFPage.fill = GridBagConstraints.BOTH;
		gbc_txtfldFPage.gridx = 2;
		gbc_txtfldFPage.gridy = 0;
		pnlFPageSelect.add(txtfldFPage, gbc_txtfldFPage);
		txtfldFPage.setColumns(10);
		
		// Go forward a page
		btnNextFPage = new JButton(">");
		btnNextFPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UIConstants error = controller.changePDFPreviewPage(UIConstants.IMAGE_FINALPDF, -1, model.getCurFPage() + 1);
				errorMessage(error);
			}
		});
		
		GridBagConstraints gbc_btnNextFPage = new GridBagConstraints();
		gbc_btnNextFPage.weightx = 0.1;
		gbc_btnNextFPage.weighty = 1.0;
		gbc_btnNextFPage.fill = GridBagConstraints.BOTH;
		gbc_btnNextFPage.insets = new Insets(0, 5, 0, 0);
		gbc_btnNextFPage.gridx = 3;
		gbc_btnNextFPage.gridy = 0;
		pnlFPageSelect.add(btnNextFPage, gbc_btnNextFPage);
		
		// Go forward 10 pages
		btnFNextFPage = new JButton(">>");
		btnFNextFPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UIConstants error = controller.changePDFPreviewPage(UIConstants.IMAGE_FINALPDF, -1, model.getCurFPage() + 10);
				errorMessage(error);
			}
		});
		
		GridBagConstraints gbc_btnFNextFPage = new GridBagConstraints();
		gbc_btnFNextFPage.weightx = 0.2;
		gbc_btnFNextFPage.weighty = 1.0;
		gbc_btnFNextFPage.insets = new Insets(0, 5, 0, 0);
		gbc_btnFNextFPage.fill = GridBagConstraints.BOTH;
		gbc_btnFNextFPage.gridx = 4;
		gbc_btnFNextFPage.gridy = 0;
		pnlFPageSelect.add(btnFNextFPage, gbc_btnFNextFPage);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(pnlTabs, BorderLayout.CENTER);
	}
	
	/**
	 * Display a message dialog with an error.
	 * 
	 * @param error the error type, represented by {@link UIConstants}
	 */
	private void errorMessage(UIConstants error) {
		switch (error) {
		case ERROR_ALREADY_LOADED:
			JOptionPane.showMessageDialog(frame, "File(s) already loaded.", "Error Loading PDF", JOptionPane.ERROR_MESSAGE);
			break;
		case ERROR_UNREADABLE:
			JOptionPane.showMessageDialog(frame, "File is not readable.", "Error Loading PDF", JOptionPane.ERROR_MESSAGE);
			break;
		case ERROR_NOTPDF:
			JOptionPane.showMessageDialog(frame, "File must be in PDF format.", "Error Loading PDF", JOptionPane.ERROR_MESSAGE);
			break;
		case ERROR_NO_SELECTEDPDF:
			JOptionPane.showMessageDialog(frame, "Please select a PDF.", "Error No PDF", JOptionPane.ERROR_MESSAGE);
			break;
		case ERROR_INVALID_PAGENUM:
			JOptionPane.showMessageDialog(frame, "Invalid Page Numbers.", "Error Entering Page Numbers", JOptionPane.ERROR_MESSAGE);
			break;
		case ERROR_NO_SELECTEDPAGERANGE:
			JOptionPane.showMessageDialog(frame, "No Page Range Selected.", "Error Removing Page Range", JOptionPane.ERROR_MESSAGE);
			break;
		case ERROR_CANNOT_SAVE:
			JOptionPane.showMessageDialog(frame, "Cannot save PDF.", "Error Saving PDF", JOptionPane.ERROR_MESSAGE);
			break;
		default:
			break;
		}
	}
	
	/**
	 * Update the PDF preview windows.
	 */
	private void updatePDFPreviews() {
		int curLPage = model.getCurLPage();
		int curFPage = model.getCurFPage();
		lblLPDFPreview.setIcon(model.getLPDFImage());
		lblFPDFPreview.setIcon(model.getFPDFImage());
		txtfldLPage.setText(curLPage == -1 ? "" : Integer.toString(curLPage));
		txtfldFPage.setText(curFPage == -1 ? "" : Integer.toString(curFPage));
	}
	
	/**
	 * Update the page counts.
	 */
	private void updatePageCounts() {
		int lNumPages = model.getLNumPages();
		int fNumPages = model.getFNumPages();
		lblLNumPages.setText(lNumPages >= 1 ? "Pages: " + Integer.toString(lNumPages) : "");
		lblFNumPages.setText(fNumPages >= 1 ? "Pages: " + Integer.toString(fNumPages) : "");
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		updatePDFPreviews();
		updatePageCounts();
		txtfldPageRange.setText("");
	}
}

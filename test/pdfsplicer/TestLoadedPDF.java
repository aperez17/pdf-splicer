package pdfsplicer;

import static org.junit.Assert.*;

import java.io.File;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnits testing PDF loading and removal.
 * 
 * @author Abeer Ahmed
 */
public class TestLoadedPDF {

	SplicerModel model;
	SplicerController controller;
	PDDocument doc;
	File[] files;
	
	@Before
	public void before() throws Exception {
		model = new SplicerModel();
		controller = new SplicerController(model);
		doc = new PDDocument();
		doc.addPage(new PDPage());
		files = new File[3];
		for (int i = 0; i < 3; ++i) {
			doc.save(files[i] = new File("test" + i + ".pdf"));
		}
	}
	
	@Test
	public void testLoadSinglePDF() {
		controller.loadPDF(new File[]{files[0]});
		
		assertEquals("List model size == 1", model.getPDFListModel().getSize(), 1);
		assertEquals("List model contains test0.pdf", model.getPDFListModel().getElementAt(0), "test0.pdf");
		
		assertEquals("Combobox model size == 1", model.getPDFComboBoxModel().getSize(), 1);
		assertEquals("Combobox model contains test0.pdf", model.getPDFComboBoxModel().getElementAt(0), "test0.pdf");
	}

	@Test
	public void testLoadMultiplePDF() {
		controller.loadPDF(files);
		
		assertEquals("List model size == 3", model.getPDFListModel().getSize(), 3);
		assertEquals("Combobox model size == 3", model.getPDFComboBoxModel().getSize(), 3);
		
		for (int i = 0; i < 3; ++i) {
			assertEquals("List model contains test" + i + ".pdf", model.getPDFListModel().getElementAt(i), "test" + i + ".pdf");
			assertEquals("Combobox model contains test" + i + ".pdf", model.getPDFListModel().getElementAt(i), "test" + i + ".pdf");
		}
	}
	
	@Test
	public void testRemoveSinglePDF() {
		controller.loadPDF(files);
		controller.removePDF(new int[]{0});

		assertEquals("List model size == 2", model.getPDFListModel().getSize(), 2);
		assertEquals("Combobox model size == 2", model.getPDFComboBoxModel().getSize(), 2);
		
		for (int i = 0; i < 2; ++i) {
			assertNotEquals("List model does not contain test0.pdf", model.getPDFListModel().getElementAt(i), "test0.pdf");
			assertNotEquals("Combobox model does not contain test0.pdf", model.getPDFComboBoxModel().getElementAt(i), "test0.pdf");
		}
	}
	
	@Test
	public void testRemoveMultiplePDF() {
		controller.loadPDF(files);
		controller.removePDF(new int[]{0, 2});

		assertEquals("List model size == 1", model.getPDFListModel().getSize(), 1);
		assertEquals("Combobox model size == 1", model.getPDFComboBoxModel().getSize(), 1);
		
		assertEquals("List model contains test1.pdf", model.getPDFListModel().getElementAt(0), "test1.pdf");
		assertEquals("Combobox model contains test1.pdf", model.getPDFComboBoxModel().getElementAt(0), "test1.pdf");
	}
	
	@Test
	public void testRemoveAllPDF() {
		controller.loadPDF(files);
		controller.removePDF(new int[]{0, 2});

		assertEquals("List model size == 1", model.getPDFListModel().getSize(), 1);
		assertEquals("Combobox model size == 1", model.getPDFComboBoxModel().getSize(), 1);
		
		assertEquals("List model contains test1.pdf", model.getPDFListModel().getElementAt(0), "test1.pdf");
		assertEquals("Combobox model contains test1.pdf", model.getPDFComboBoxModel().getElementAt(0), "test1.pdf");
	}
	
	@After
	public void tearDown() throws Exception {
		model = null;
		controller = null;
		doc.close();
		doc = null;
		files = null;
		assertNull(model);
		assertNull(controller);
		assertNull(doc);
		assertNull(files);
	}
}

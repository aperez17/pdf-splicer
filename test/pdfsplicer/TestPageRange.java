package pdfsplicer;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnits testing page range adding and removal.
 * 
 * @author Abeer Ahmed
 */
public class TestPageRange {

	SplicerModel model;
	SplicerController controller;
	PDDocument doc;
	File[] files;
	
	@Before
	public void before() throws Exception {
		model = new SplicerModel();
		controller = new SplicerController(model);
		doc = new PDDocument();
		for (int i = 0; i < 3; ++i) {
			doc.addPage(new PDPage());
		}
		files = new File[3];
		for (int i = 0; i < 3; ++i) {
			doc.save(files[i] = new File("test" + i + ".pdf"));
		}
		controller.loadPDF(files);
	}
	
	@Test
	public void testAddSinglePage() {
		controller.addPageRange(0, "1");
		assertEquals("List model size == 1", model.getPageRangeModel().size(), 1);
		assertEquals("List model contains page 1", model.getPageRangeModel().getElementAt(0), "test0.pdf: 1");
	}

	@Test
	public void testAddCommaSeparatedPages() {
		controller.addPageRange(0, "1,3");
		assertEquals("List model size == 1", model.getPageRangeModel().size(), 1);
		assertEquals("List model contains page 1, 3", model.getPageRangeModel().getElementAt(0), "test0.pdf: 1,3");
	}
	
	@Test
	public void testAddDashSeparatedPageRange() {
		controller.addPageRange(0, "1-3");
		assertEquals("List model size == 1", model.getPageRangeModel().size(), 1);
		assertEquals("List model contains page 1, 3", model.getPageRangeModel().getElementAt(0), "test0.pdf: 1-3");
	}
	
	@Test
	public void testRemoveSinglePageRange() {
		controller.addPageRange(0, "1");
		controller.addPageRange(1, "1,3");
		controller.addPageRange(2, "1-3");
		controller.removePageRange(new int[]{1});
		assertEquals("List model size == 2", model.getPageRangeModel().size(), 2);
		for (int i = 0; i < 2; ++i) {
			assertNotEquals("List model does not contain pages 1,3 from test1.pdf", model.getPageRangeModel().getElementAt(i), "test1.pdf: 1,3");
		}
	}
	
	@Test
	public void testRemoveMultiplePageRanges() {
		controller.addPageRange(0, "1");
		controller.addPageRange(1, "1,3");
		controller.addPageRange(2, "1-3");
		controller.removePageRange(new int[]{0, 2});
		assertEquals("List model size == 1", model.getPageRangeModel().size(), 1);
		assertEquals("List model contains pages 1,3 from test1.pdf", model.getPageRangeModel().getElementAt(0), "test1.pdf: 1,3");
	}
	
	@Test
	public void testRemoveAllPageRanges() {
		controller.addPageRange(0, "1");
		controller.addPageRange(1, "1,3");
		controller.addPageRange(2, "1-3");
		controller.removeAllPageRange();
		assertEquals("List model size == 0", model.getPageRangeModel().size(), 0);
	}
	
	@After
	public void after() throws Exception {
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

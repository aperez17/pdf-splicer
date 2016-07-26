import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitWidthDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

public class PDFParser {

	private static Set<Integer> getPageRanges(String pages) {
		Set<Integer> marked_pages = new LinkedHashSet<Integer>();
		
		String[] pagenums = pages.split(","); // Separate pages or ranges of pages by comma, IE 1, 5, 6
		for (String pagenums_section : pagenums) {
			pagenums_section = pagenums_section.trim();
			if (pagenums_section.contains("-")) { // Dash designates a range of pages, IE 2-5 is pages 2 to 5
				String[] pagerange = pagenums_section.split("-");
				if (pagerange.length != 2) {
					System.err.println("Invalid Page Range");
					System.exit(1);
				}
				try {
					int begin = Integer.parseInt(pagerange[0].trim());
					int end = Integer.parseInt(pagerange[1].trim());
					for (int i = begin; i <= end; ++i) {
						marked_pages.add(i);
					}
				} catch (NumberFormatException e) {
					System.err.println(e.getStackTrace());
				}
			} else {
				try {
					int pnum = Integer.parseInt(pagenums_section);
					marked_pages.add(pnum);
				} catch (NumberFormatException e) {
					System.err.println(e.getStackTrace());
				}
			}
		}
		
		return marked_pages;
	}
	
	private static void usage() {
		System.err.println("Usage: java " + PDFParser.class.getName() + " <input-pdf> <output-pdf>");
		System.exit(1);
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			usage();
		}
		
		Set<Integer> marked_pages = getPageRanges(args[0]);
		
		PDDocument doc = null;
		try {
			doc = PDDocument.load(new File("TestRTPDF.pdf"));
			if (doc.isEncrypted()) {
				System.out.println("Error: Encrypted PDF");
				System.exit(1);
			}
			PDDocumentOutline outline = new PDDocumentOutline();
			doc.getDocumentCatalog().setDocumentOutline(outline);
			PDOutlineItem pagesOutline = new PDOutlineItem();
			pagesOutline.setTitle("TITLE");
			outline.addLast(pagesOutline);
			Iterator<Integer> iter = marked_pages.iterator();
			while (iter.hasNext()) {
				int curpage = iter.next();
				PDPageFitWidthDestination dest = new PDPageFitWidthDestination();
				dest.setPage(doc.getPage(curpage - 1));
				PDOutlineItem bookmark = new PDOutlineItem();
				bookmark.setDestination(dest);
				bookmark.setTitle("BKMRK " + curpage);
				pagesOutline.addLast(bookmark);
			}
			pagesOutline.openNode();
			outline.openNode();
			
			doc.save("bkmarkedPDF.pdf");
		} finally {
			if (doc != null) {
				doc.close();
			}
		}
	}

}

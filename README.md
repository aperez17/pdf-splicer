[![Build Status](https://travis-ci.org/odvios/pdf-splicer.svg?branch=master)](https://travis-ci.org/odvios/pdf-splicer)

PDF-Splicer
===========

This program is meant to be a simple way to combine pages from different PDFs and create a new one
out of them, or create a PDF with a subset of pages from a single PDF.

### Prerequisites

* Java 8 or higher.

### Instructions

1. After launching the application, click `Load`, and use the file chooser to select a PDF.
To select multiple, hold Control (Command on OSX) or Shift and select more files. To remove 1 or
more PDF(s), simply select it in the list and click `Remove`, or `Remove All` to clear the list.
Selecting the loaded PDFs in the list will display a preview of them, which you can navigate via
the arrows or simply typing in a page number in the text field.
   
2. After loading all necessary PDFs, navigate to the `Page Selection` Tab at the top, and select
a PDF from the drop down menu. Now you can enter a page range in the text field at the bottom.
Enter separate pages with commas to separate them such as `1,4,9`, or type in a range with a dash
like in `2-10`. The word `all` can be typed in to enter in the whole PDF. Press Enter to add the
range to the list. The `Remove` and `Remove All` buttons work similarly to those in the loading tab.
   
3. After entering some page ranges, navigate to the `Finalize` tab. Here you can view and navigate
a preview of what the finished PDF will look like. When satisfied with the result, simply click
`Create PDF` to open a file chooser and select a save location for the new PDF.
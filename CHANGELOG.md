## 0.2.0 (April 8, 2018)

Bug Fixes:

- Fixed issue with larger PDFs not loading or saving due to running out of memory
- Fixed PDF preview not being centered in the view panel

Other:

- Updated PDFBox dependency to 2.0.9

## 0.1.4 (April 5, 2018)

Features:

- Added Drag and Drop functionality to loaded PDF list
- Made it so only PDF files can be seen when loading or saving using the file chooser
- When saving the finalized PDF, the utility now automatically adds the ".pdf" extension to the file if it does not already have it

## 0.1.3 (August 6, 2016)

Bug Fixes:

- Fixed bug where the "<<" and ">>" buttons in both previews do nothing
if there are fewer than 10 pages left in that direction in the PDF

## 0.1.2 (August 5, 2016)

Bug Fixes:

- Fixed bug where entering the name of the new save file, and then
backspacing and retyping the ".pdf" portion would cause a save error
- Fixed bug where you could only save once

## 0.1.1 (August 5, 2016)

Bug Fixes:

- Fixed Ant buildfile so it now builds the correct JAR with dependencies
- GUI window title changed from PDFSplicer to PDF-Splicer

## 0.1.0 (August 4, 2016)

Features:

- Program is now functional with a GUI
- Can now load multiple PDFs
- Added PDF display panels
- Can now add multiple page ranges (including duplicates)
- Properly saves the newly created PDF
- Added JUnits
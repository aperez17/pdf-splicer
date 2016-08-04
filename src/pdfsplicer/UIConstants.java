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

/**
 * An enum with different constants for the UI.
 * 
 * @author Abeer Ahmed
 */
public enum UIConstants {
	IMAGE_LOADEDPDF, IMAGE_FINALPDF,							  	  // Different preview images
	ERROR_NOERROR, ERROR_ALREADY_LOADED, ERROR_NOTPDF, ERROR_UNREADABLE, // Different error types
	ERROR_NO_SELECTEDPDF, ERROR_INVALID_PAGENUM, ERROR_NO_SELECTEDPAGERANGE,
	ERROR_CANNOT_SAVE
}

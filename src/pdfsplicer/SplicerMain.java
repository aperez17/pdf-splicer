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

import java.awt.EventQueue;

/**
 * The main class for the PDFSplicer, which initialized the model,
 * view, and controller for the MVC pattern.
 * 
 * @author Abeer Ahmed
 */
public class SplicerMain {

	/**
	 * The main method.
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
	    System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SplicerModel model = new SplicerModel();
					SplicerController controller = new SplicerController(model);
					SplicerView view = new SplicerView(model, controller);
					model.addObserver(view);
					controller.setView(view);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}

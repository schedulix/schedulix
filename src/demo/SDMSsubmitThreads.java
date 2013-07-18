/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

schedulix Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of schedulix

schedulix is free software:
you can redistribute it and/or modify it under the terms of the
GNU Affero General Public License as published by the
Free Software Foundation, either version 3 of the License,
or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package de.independit.scheduler.demo;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Text;
import de.independit.scheduler.server.output.SDMSOutput;
import de.independit.scheduler.server.output.SDMSOutputContainer;
import de.independit.scheduler.server.output.SDMSOutputError;
import de.independit.scheduler.shell.SDMSServerConnection;

public class SDMSsubmitThreads
{

	static String host = null;
	static String port = null;
	static String id = null;
	static String key = null;
	static String delay = "0";
	static String child = null;
	static String number = "3";

	static Text delay_field;
	static Text number_field;

	static Label status_line;

	public static void main(String[] args)
	{

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-h") || args[i].equals("--host")) {
				host = args[i + 1];
				i++;
			} else if (args[i].equals("-p") || args[i].equals("--port")) {
				port = args[i + 1];
				i++;
			} else if (args[i].equals("-i") || args[i].equals("--id")) {
				id = args[i + 1];
				i++;
			} else if (args[i].equals("-k") || args[i].equals("--key")) {
				key = args[i + 1];
				i++;
			} else if (args[i].equals("-c") || args[i].equals("--child")) {
				child = args[i + 1];
				i++;
			} else if (args[i].equals("-d") || args[i].equals("--delay")) {
				delay = args[i + 1];
				i++;
			} else if (args[i].equals("-n") || args[i].equals("--number")) {
				number = args[i + 1];
				i++;
			}
		}

		if (host == null || port == null || id == null || key == null
		    || child == null) {
			System.err
			.println("usage: SDMSsubmitThreads -h|--host host -p|--port port -i|--id jobid -k|--key jobkey -c|--child alias [-d|--delay delay] [-n|--number number]");
			System.exit(1);
		}

		final Display display = new Display();

		final Shell shell = new Shell(display);
		shell.setBackground(new Color(display, 255, 255, 255));
		shell.setText("SDMSsubmitThreads");
		final Image logo = new Image(display, "Images/independIT_logo_neu.png");
		final Image bullit = new Image(display,
		                               "Images/independIT_bullit_large.png");
		shell.setImage(bullit);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;
		shell.setLayout(gridLayout);

		GridData data;
		Composite compmain = new Composite(shell, SWT.NONE);
		compmain.setBackground(new Color(display, 255, 255, 255));
		data = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		compmain.setLayoutData(data);

		gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 15;
		gridLayout.marginWidth = 15;
		gridLayout.verticalSpacing = 15;
		compmain.setLayout(gridLayout);

		Canvas canvas = new Canvas(compmain, SWT.NONE);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(logo, 0, 0);
			}
		});
		data = new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1);
		data.widthHint = logo.getBounds().width;
		data.heightHint = logo.getBounds().height;
		canvas.setLayoutData(data);

		Composite entry = new Composite(compmain, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		entry.setLayoutData(data);
		entry.setBackground(new Color(display, 255, 255, 255));
		gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 5;
		gridLayout.marginWidth = 5;
		gridLayout.verticalSpacing = 5;
		entry.setLayout(gridLayout);

		Composite number_line = new Composite(entry, SWT.NONE);
		gridLayout = new GridLayout(2, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 5;
		number_line.setLayout(gridLayout);

		data = new GridData(GridData.FILL_HORIZONTAL);
		number_line.setLayoutData(data);
		number_line.setBackground(new Color(display, 255, 255, 255));

		final Label number_label = new Label(number_line, SWT.NONE);
		number_label.setBackground(new Color(display, 255, 255, 255));
		data = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		number_label.setText("Number of Threads to submit:");
		number_label.pack();
		number_label.setLayoutData(data);

		number_field = new Text(number_line, SWT.BORDER | SWT.RIGHT);
		data = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		number_field.setText("000");
		number_field.pack();
		number_field.setLayoutData(data);

		Composite delay_line = new Composite(entry, SWT.NONE);
		gridLayout = new GridLayout(2, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 5;
		delay_line.setLayout(gridLayout);

		data = new GridData(GridData.FILL_HORIZONTAL);
		delay_line.setLayoutData(data);
		delay_line.setBackground(new Color(display, 255, 255, 255));

		final Label delay_label = new Label(delay_line, SWT.NONE);
		delay_label.setBackground(new Color(display, 255, 255, 255));
		data = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		delay_label.setText("Delay between submits [secs]:");
		delay_label.pack();
		delay_label.setLayoutData(data);

		delay_field = new Text(delay_line, SWT.BORDER | SWT.RIGHT);
		data = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		delay_field.setText("000");
		delay_field.pack();
		delay_field.setLayoutData(data);

		Composite button_line = new Composite(compmain, SWT.NONE);
		gridLayout = new GridLayout(2, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 0;
		button_line.setLayout(gridLayout);

		data = new GridData(GridData.FILL_HORIZONTAL);
		button_line.setLayoutData(data);
		button_line.setBackground(new Color(display, 255, 255, 255));

		Button ok_button = new Button(button_line, SWT.NONE);
		ok_button.setText("Ok");
		data = new GridData(SWT.RIGHT, SWT.TOP, true, true, 1, 1);
		ok_button.setLayoutData(data);

		ok_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				submitThreads();
				System.exit(0);
			}
		});

		shell.setDefaultButton(ok_button);

		status_line = new Label(shell, SWT.BORDER);
		status_line.setBackground(new Color(display, 200, 200, 200));
		data = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		status_line.setText("Enter data and press Ok");
		status_line.setLayoutData(data);

		shell.pack();

		number_field.setText(number);
		number_field.selectAll();
		number_field.setFocus();

		delay_field.setText(delay);

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	static void submitThreads()
	{
		int n = 0, d = 0;
		try {
			n = new Integer(number_field.getText()).intValue();
		} catch (Exception evt) {
			System.err.println("invalid -n|--number option " + number_field.getText());
			System.exit(1);
		}
		try {
			d = new Integer(delay_field.getText()).intValue();
		} catch (Exception evt) {
			System.err.println("invalid -d|--delay option " + delay_field.getText());
			System.exit(1);
		}
		boolean failure = false;
		SDMSOutput output = null;
		SDMSServerConnection connection = new SDMSServerConnection(host, new Integer(port).intValue(),
		                id, key);
		try {
			output = connection.connect();
		} catch (IOException e) {

			e.printStackTrace();
			System.exit(1);
		}
		if (output.error != null) {
			System.err.println("Error Connection to BICsuite!server !");
			System.err.println("Error(" + output.error.code + "):" + output.error.message);
			System.exit(1);
		}
		for (int i = 0; i < n; i++) {
			String tag = new Integer(i+1).toString();
			status_line.setText("Submitting thread " + tag + "...");
			status_line.update();

			output = connection.execute("SUBMIT '" + child + "' WITH CHILDTAG = '" + tag + "'");
			if (output.error != null) {
				System.out.println("");
				System.err.println("Error submitting child !");
				System.err.println("Error(" + output.error.code + "):" + output.error.message);
				failure = true;
			}
			status_line.setText("Submitting thread " + tag + "...done");
			status_line.update();

			if (d > 0 && i+1 < n) {
				status_line.setText("Submitting thread " + tag + "...done, Waiting " + d + " seconds...");
				status_line.update();
				try {
					Thread.sleep(d*1000);
				} catch (InterruptedException e1) {

					e1.printStackTrace();
				}
			}
		}

		try {
			connection.finish();
		} catch (IOException e) {

			e.printStackTrace();
		}
		if (failure) {
			System.err.println("One or more child submit failed !");
			System.exit(1);
		}
	}
}

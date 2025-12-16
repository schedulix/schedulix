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
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Event;

import de.independit.scheduler.server.output.SDMSOutput;
import de.independit.scheduler.server.output.SDMSOutputContainer;
import de.independit.scheduler.server.output.SDMSOutputError;
import de.independit.scheduler.shell.SDMSServerConnection;

public class SDMSsubmitThreadsGuiThread extends Thread
{

	static Text delay_field = null;
	static Text number_field = null;
	static Label status = null;
	static Button ok_button = null;
	static boolean done = false;
	static long startTime = 0;
	static Display display = null;
	static boolean submitting = false;
	static int cntSubmitted = 0;

	public void run()
	{
		try {
			startTime = System.currentTimeMillis();
			try {
				display = new Display();
			} catch (Exception e) {
				System.err.println("Exception in SDMSsubmitThreadsGuiThread (" + e.toString() + ") !");
				SDMSsubmitThreads.gui_failure = true;
				return;
			}

			final Shell shell = new Shell(display);
			shell.setBackground(new Color(display, 255, 255, 255));
			shell.setText("SDMSsubmitThreads");
			final Image logo = new Image(display, "Images/Logo.png");
			final Image bullit = new Image(display,
			                               "Images/Bullit.png");
			shell.setImage(bullit);
			shell.addListener(SWT.Close, new Listener() {
				public void handleEvent(Event event) {
					if (submitting) {
						event.doit = false;
						return;
					}
					System.err.println ("User closed window, no children submitted");
					System.exit(0);
				}
			});
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
			number_field.setText("0000000000000000");
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
			delay_field.setText("0000000000000000");
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

			ok_button = new Button(button_line, SWT.NONE);
			ok_button.setText("Ok");
			data = new GridData(SWT.RIGHT, SWT.TOP, true, true, 1, 1);
			ok_button.setLayoutData(data);

			ok_button.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					submitThreads();
				}
			});

			shell.setDefaultButton(ok_button);

			status = new Label(shell, SWT.BORDER);
			status.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
			status.setText("Enter data and press Ok");

			shell.pack();

			number_field.setText(Integer.valueOf(SDMSsubmitThreads.number).toString());
			number_field.selectAll();
			number_field.setFocus();

			delay_field.setText(SDMSsubmitThreads.delay);

			shell.open();

			if (SDMSsubmitThreads.waitTime != null ) {
				Runnable updateTimerLabel = new Runnable() {
					public void run() {
						if (submitting)
							return;
						long now = System.currentTimeMillis();
						long secsRunning = (now - startTime) / 1000;
						long secsToRun = SDMSsubmitThreads.waitTime.intValue() - secsRunning;
						if (secsToRun > 0) {
							int interval = 1000;
							String text = "Starting to submit threads in " + secsToRun + " seconds";
							if (secsToRun > 120) {
								interval *= 60;
								text = "Starting to submit threads in " + secsToRun/60 + " minutes";
							}
							try {
								status.setText(text);
								status.update();
								display.timerExec(interval, this);
							} catch (Exception e) {  }
						} else {
							SDMSsubmitThreadsGuiThread.submitThreads();
						}
					}
				};
				updateTimerLabel.run();
			}

			while (!shell.isDisposed() && !done) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			display.dispose();
		} catch (Throwable t) {
			System.err.println("Unecpected Throwable (" + t.toString() + ") in SDMSsubmitThreadsGuiThread.java !");
			t.printStackTrace();
			System.exit(1);
		}
	}

	static void submitThreads()
	{
		submitting = true;
		ok_button.setEnabled(false);

		try {
			SDMSsubmitThreads.number = Integer.valueOf(number_field.getText()).intValue();
		} catch (Exception evt) {
			System.err.println("invalid -n|--number option " + number_field.getText());
			System.exit(1);
		}
		try {
			SDMSsubmitThreads.delay = delay_field.getText();
		} catch (Exception evt) {
			System.err.println("invalid -d|--delay option " + delay_field.getText());
			System.exit(1);
		}
		Runnable submitThread = new Runnable() {
			public void run() {
				cntSubmitted ++;
				if (cntSubmitted > SDMSsubmitThreads.number) {
					done = true;
					return;
				}
				SDMSsubmitThreads.submitThread(cntSubmitted);
				if (cntSubmitted == SDMSsubmitThreads.number) {
					status.setText("Thread " + cntSubmitted + " submitted");
					status.update();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) { }
					done = true;
				} else {
					int d;
					if (SDMSsubmitThreads.delay.equals(""))
						d = 0;
					else
						d = SDMSpopup.rndConf(SDMSsubmitThreads.delay);
					if (d > 0) {
						status.setText("Thread " + cntSubmitted + " submitted, Waiting " + d + " seconds...");
						display.timerExec(d * 1000, this);
					} else {
						status.setText("Thread " + cntSubmitted + " submitted");
						display.timerExec(0, this);
					}
					status.update();
				}
			}
		};
		submitThread.run();
	}
}

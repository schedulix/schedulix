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

import java.util.Vector;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Event;

public class SDMSpopupGuiThread extends Thread
{
	Vector<String> displayArgs = null;
	String config = null;
	boolean ignoreGuiFailure = false;
	Integer runTimeSecs = 0;
	int exitCode = 0;
	long startTime = 0;
	static Label status = null;
	static Control focusControl = null;
	static boolean hold = false;
	static HashMap<Integer, String> exitButtons = new HashMap<Integer, String>();

	SDMSpopupGuiThread(Vector<String> displayArgs, String config, boolean ignoreGuiFailure, Integer runTimeSecs, int exitCode)
	{
		this.config = config;
		this.ignoreGuiFailure = ignoreGuiFailure;
		this.runTimeSecs = runTimeSecs;
		this.exitCode = exitCode;
		this.displayArgs = (Vector<String>)(displayArgs);
		startTime = System.currentTimeMillis();
	}

	public void run()
	{
		try {
			final Display display = new Display();
			String fontName = System.getenv("SDMSFONT");
			if (fontName == null)
				fontName = "FreeMono";
			final Font argsFont = new Font(display, fontName, 10, SWT.NORMAL );
			final Image logo = new Image(display, "Images/Logo.png");
			final Image bullit = new Image(display,	"Images/Bullit.png");

			Shell shell = new Shell(display);
			shell.setText("SDMSpopup");
			shell.setImage(bullit);
			shell.addListener(SWT.Close, new Listener() {
				public void handleEvent(Event event) {
					System.err.println ("User closed window , exiting with exit code " + exitCode);
					System.exit(exitCode);
				}
			});

			GridLayout gridLayout = new GridLayout(1, false);
			gridLayout.marginHeight = 0;
			gridLayout.marginWidth = 0;
			gridLayout.verticalSpacing = 0;
			shell.setLayout(gridLayout);

			Composite form = new Composite(shell, SWT.NONE);
			form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			form.setBackground(new Color(display, 255, 255, 255));

			gridLayout = new GridLayout(1, false);
			gridLayout.marginHeight = 15;
			gridLayout.marginWidth = 15;
			gridLayout.verticalSpacing = 15;
			form.setLayout(gridLayout);

			Canvas canvas = new Canvas(form, SWT.NONE);
			canvas.addPaintListener(new PaintListener() {
				public void paintControl(PaintEvent e) {
					e.gc.drawImage(logo, 0, 0);
				}
			});
			GridData data;
			data = new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1);
			data.widthHint = logo.getBounds().width;
			data.heightHint = logo.getBounds().height;
			canvas.setLayoutData(data);

			Table table = new Table(form, SWT.BORDER);
			table.setLinesVisible(true);
			table.setHeaderVisible(true);
			TableColumn argv_column = new TableColumn(table, SWT.NONE);
			argv_column.setText("Command Line Arguments");

			TableItem item;
			for (int i = 0; i < displayArgs.size(); i++) {
				item = new TableItem(table, SWT.NONE);
				item.setFont(argsFont);
				item.setText(0, displayArgs.elementAt(i));
				item.setBackground(new Color(display, 255, 255, 255));
			}

			argv_column.pack();

			data = new GridData(SWT.FILL, SWT.FILL, true, true);
			table.setLayoutData(data);
			table.pack();
			data.widthHint = table.getBounds().width;
			data.heightHint = table.getBounds().height;

			Composite bottom = new Composite(form, SWT.NONE);
			data = new GridData(GridData.FILL_HORIZONTAL);
			bottom.setLayoutData(data);
			bottom.setBackground(new Color(display, 255, 255, 255));

			String[] buttons = config.split(":");
			int cols = buttons.length;
			if (runTimeSecs != null )
				cols += 1;
			gridLayout = new GridLayout(cols, false);
			gridLayout.marginHeight = 0;
			gridLayout.marginWidth = 0;
			gridLayout.horizontalSpacing = 15;
			bottom.setLayout(gridLayout);

			Button  defaultButton = null;
			Button  exitCodeButton = null;
			boolean expand = true;
			Text exit_code_field = null;

			if (runTimeSecs != null ) {
				final Button hold_button = new Button(bottom, SWT.NONE);
				data = new GridData(SWT.LEFT, SWT.FILL, expand, false, 1, 1);
				hold_button.setLayoutData(data);
				hold_button.setText("Hold");
				hold_button.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						hold = true;
						status.setText("Auto close has been disabled by Hold");
						hold_button.setEnabled(false);
						if (focusControl != null) focusControl.setFocus();
					}
				});
			}

			for (int i = 0; i < buttons.length; i ++) {
				final String[] button = buttons[i].split("=");
				if (button[0].equals("?")) {
					Composite exit_code_button = new Composite(bottom, SWT.NONE);
					exit_code_button.setBackground(new Color(display, 255, 255, 255));

					data = new GridData(SWT.RIGHT, SWT.FILL, expand, false, 1, 1);
					exit_code_button.setLayoutData(data);
					gridLayout = new GridLayout(2, false);
					gridLayout.marginHeight = 0;
					gridLayout.marginWidth = 0;
					gridLayout.horizontalSpacing = 5;
					exit_code_button.setLayout(gridLayout);

					Button exit_button = new Button(exit_code_button, SWT.NONE);
					exit_button.setText("Exit with Code:");
					data = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
					exit_button.setLayoutData(data);

					final Text exit_code = new Text(exit_code_button, SWT.BORDER | SWT.RIGHT);
					exit_code_field = exit_code;
					data = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
					exit_code.setText("000");
					exit_code.pack();
					exit_code.setLayoutData(data);

					exit_button.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							try {
								int ret;
								ret = new Integer(exit_code.getText()).intValue();
								System.err.println ("User requested exit with exit code " + ret + " (Exit Code Field)");
								System.exit(ret);
							} catch (Exception evt) {
							}
						}
					});

					defaultButton = exit_button;
					exitCodeButton = exit_button;
				} else {
					Button exit_button = new Button(bottom, SWT.NONE);
					data = new GridData(SWT.RIGHT, SWT.FILL, expand, false, 1, 1);
					exit_button.setLayoutData(data);
					exit_button.setText(button[1]);
					exitButtons.put (new Integer(button[0]), button[1]);
					exit_button.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							int ret;
							ret = new Integer(button[0]).intValue();
							System.err.println ("User requested exit with exit code " + ret + " (" + button[1] + " Button)");
							System.exit(ret);
						}
					});
					if (defaultButton == null || new Integer(button[0]).intValue() == 0) defaultButton = exit_button;
					if (focusControl == null || new Integer(button[0]).intValue() == 0) focusControl = exit_button;
				}
				expand = false;
			}

			if (runTimeSecs != null ) {
				status = new Label(shell, SWT.BORDER);
				status.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
				status.setText("I am Label");
				Runnable updateTimerLabel = new Runnable() {
					public void run() {
						if (hold)
							return;
						long now = System.currentTimeMillis();
						long secsRunning = (now - startTime) / 1000;
						long secsToRun = runTimeSecs.intValue() - secsRunning;
						if (secsToRun > 0) {
							String exitStr = exitButtons.get(new Integer(exitCode));
							if (exitStr == null)
								exitStr = " with exit code " + exitCode;
							int interval = 1000;
							String text = "Exiting with " + exitStr + " in " + secsToRun + " seconds";
							if (secsToRun > 120) {
								interval *= 60;
								text = "Exiting with " + exitStr + " in " + secsToRun/60 + " minutes";
							}
							try {
								status.setText(text);
								display.timerExec(interval, this);
							} catch (Exception e) {  }
						} else
							System.exit(exitCode);
					}
				};
				updateTimerLabel.run();
			}

			form.pack();
			shell.pack();

			if (exit_code_field != null) {
				exit_code_field.setText("" + exitCode);
				exit_code_field.selectAll();
				focusControl = exit_code_field;
				defaultButton = exitCodeButton;
			}

			if (focusControl != null) focusControl.setFocus();
			if (defaultButton != null) shell.setDefaultButton(defaultButton);

			shell.open();

			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			display.dispose();
		} catch (org.eclipse.swt.SWTError e) {
			System.err.println("Cannot open GUI (Exception:" + e.toString() + ")");
			if (ignoreGuiFailure) {
				System.err.println("ignored (-I flag is set) , running in silent mode");
				if (runTimeSecs == null) {
					System.exit (exitCode);
				}
				SDMSpopup.silent = true;
			} else {
				System.err.println("exit (" + SDMSpopup.ERRORCODE + ")");
				System.exit(SDMSpopup.ERRORCODE);
			}
		}
	}
}

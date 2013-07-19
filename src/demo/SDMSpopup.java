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

public class SDMSpopup
{

	public static void main(String[] args)
	{
		final Display display = new Display();
		final Shell shell = new Shell(display);

		final Font argsFont = new Font(display, "Courier", 10, SWT.NORMAL );

		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 15;
		gridLayout.marginWidth = 15;
		gridLayout.verticalSpacing = 15;
		shell.setLayout(gridLayout);

		shell.setBackground(new Color(display, 255, 255, 255));
		shell.setText("SDMSpopup");

		final Image logo = new Image(display, "Images/Logo.png");
		final Image bullit = new Image(display,
		                               "Images/Bullit.png");

		shell.setImage(bullit);

		Canvas canvas = new Canvas(shell, SWT.NONE);
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

		Table table = new Table(shell, SWT.BORDER);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableColumn argv_column = new TableColumn(table, SWT.NONE);
		argv_column.setText("Command Line Arguments");

		String config = null;
		TableItem item;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-c") || args[i].equals("-c")) {
				config = args[i+1];
				i++;
			} else {
				item = new TableItem(table, SWT.NONE);

				item.setFont(argsFont);
				item.setText(0, args[i]);
				item.setBackground(new Color(display, 255, 255, 255));
			}
		}
		if (config == null) {
			config = "?:1=FAILURE:0=SUCCESS";
		}

		argv_column.pack();

		data = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(data);

		Composite bottom = new Composite(shell, SWT.NONE);
		data = new GridData(GridData.FILL_HORIZONTAL);
		bottom.setLayoutData(data);
		bottom.setBackground(new Color(display, 255, 255, 255));

		String[] buttons = config.split(":");
		int cols = buttons.length;
		gridLayout = new GridLayout(cols, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 15;
		bottom.setLayout(gridLayout);

		Control focusControl = null;
		Button  defaultButton = null;
		Button  exitCodeButton = null;
		boolean expand = true;
		Text exit_code_field = null;
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
				exit_button.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						System.exit(new Integer(button[0]).intValue());
					}
				});
				if (defaultButton == null || new Integer(button[0]).intValue() == 0) defaultButton = exit_button;
				if (focusControl == null || new Integer(button[0]).intValue() == 0) focusControl = exit_button;
			}
			expand = false;
		}

		shell.pack();

		if (exit_code_field != null) {
			exit_code_field.setText("0");
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
	}
}

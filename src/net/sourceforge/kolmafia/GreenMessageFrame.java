/**
 * Copyright (c) 2005, KoLmafia development team
 * http://kolmafia.sourceforge.net/
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  [1] Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *  [2] Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in
 *      the documentation and/or other materials provided with the
 *      distribution.
 *  [3] Neither the name "KoLmafia development team" nor the names of
 *      its contributors may be used to endorse or promote products
 *      derived from this software without specific prior written
 *      permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package net.sourceforge.kolmafia;

// layout
import java.awt.Color;
import java.awt.CardLayout;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;

// event listeners
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

// containers
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

// other imports
import java.util.List;
import java.util.ArrayList;
import net.java.dev.spellcast.utilities.LockableListModel;

public class GreenMessageFrame extends KoLFrame
{
	private static final int COLS = 32;
	private static final int ROWS = 8;

	private JTextField recipientEntry;
	private JTextArea messageEntry;

	private List attachedItems;
	private JLabel attachedItemsDisplay;

	public GreenMessageFrame( KoLmafia client )
	{	this( client, "" );
	}

	public GreenMessageFrame( KoLmafia client, String recipient )
	{
		super( "KoLmafia: Send a Green Message", client );

		this.attachedItems = new ArrayList();

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout( new BoxLayout( centerPanel, BoxLayout.Y_AXIS ) );

		this.recipientEntry = new JTextField( recipient );
		JPanel recipientPanel = new JPanel();
		recipientPanel.setLayout( new BoxLayout( recipientPanel, BoxLayout.X_AXIS ) );
		recipientPanel.add( new JLabel( "To:  ", JLabel.LEFT ), "" );
		recipientPanel.add( recipientEntry, "" );

		this.attachedItemsDisplay = new JLabel( "(none)" );
		JPanel attachmentPanel = new JPanel();
		attachmentPanel.setLayout( new BorderLayout() );
		JLabel attachLabel = new JLabel( "Attachments:  ", JLabel.LEFT );
		attachLabel.setForeground( new Color( 0, 0, 128 ) );
		attachmentPanel.add( attachLabel, BorderLayout.WEST );
		attachmentPanel.add( attachedItemsDisplay, BorderLayout.CENTER );

		this.messageEntry = new JTextArea( ROWS, COLS );
		messageEntry.setLineWrap( true );
		messageEntry.setWrapStyleWord( true );
		JScrollPane scrollArea = new JScrollPane( messageEntry,
			JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );

		centerPanel.add( recipientPanel, "" );
		centerPanel.add( Box.createVerticalStrut( 16 ) );
		centerPanel.add( attachmentPanel, "" );
		centerPanel.add( scrollArea, "" );

		addMenuBar();

		this.getContentPane().setLayout( new CardLayout( 10, 10 ) );
		this.getContentPane().add( centerPanel, "" );
		setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		setResizable( false );
	}

	/**
	 * Utility method used to add a menu bar to the <code>GreenMessageFrame</code>.
	 * The menu bar contains the general license information associated with
	 * <code>KoLmafia</code> and the ability to attach items to the green message.
	 * This also contains the option to send the message.
	 */

	private void addMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar( menuBar );

		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic( KeyEvent.VK_F );
		menuBar.add( fileMenu );

		JMenuItem sendItem = new JMenuItem( "Send Message", KeyEvent.VK_S );
		sendItem.addActionListener( new SendGreenMessageListener() );
		fileMenu.add( sendItem );

		JMenuItem attachItem = new JMenuItem( "Attach Item...", KeyEvent.VK_A );
		fileMenu.add( attachItem );

		addHelpMenu( menuBar );
	}

	/**
	 * Sets all of the internal panels to a disabled or enabled state; this
	 * prevents the user from modifying the data as it's getting sent, leading
	 * to uncertainty and generally bad things.
	 */

	public void setEnabled( boolean isEnabled )
	{
		recipientEntry.setEnabled( isEnabled );
		messageEntry.setEnabled( isEnabled );
	}

	/**
	 * Internal class used to handle sending a green message to the server.
	 */

	private class SendGreenMessageListener implements ActionListener
	{
		public void actionPerformed( ActionEvent e )
		{	(new SendGreenMessageThread()).start();
		}

		private class SendGreenMessageThread extends Thread
		{
			public SendGreenMessageThread()
			{
				super( "Green-Message-Thread" );
				setDaemon( true );
			}

			public void run()
			{
				GreenMessageFrame.this.setEnabled( false );
				(new GreenMessageRequest( client, recipientEntry.getText(), messageEntry.getText(), attachedItems.toArray() )).run();
				GreenMessageFrame.this.setEnabled( true );
			}
		}
	}

	/**
	 * Main class used to view the user interface without having to actually
	 * start the program.  Used primarily for user interface testing.
	 */

	public static void main( String [] args )
	{
		JFrame test = new GreenMessageFrame( null );
		test.pack();  test.setVisible( true );  test.requestFocus();
	}
}
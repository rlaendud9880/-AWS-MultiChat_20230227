package entity;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;

import dto.request.RequestDto;
import views.ClientRecive;

import java.awt.CardLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JOptionPane;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ClientApplication extends JFrame {

	private static final long serialVersionUID = 4726952957598445730L;

	private Gson gson; // 클릭시 일어나는 행동
	private Socket socket;

	private JPanel mainPanel;
	private CardLayout mainCard;

	private JTextField usernameField;

	private JTextField sendMessageField;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientApplication frame = new ClientApplication();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ClientApplication() {

		/* ========================<< init >> ======================== */
		gson = new Gson();
		try {
			socket = new Socket("127.0.0.1", 9090);
			ClientRecive clientRecive = new ClientRecive(socket);
			clientRecive.start();

		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (ConnectException e1) {
			JOptionPane.showMessageDialog(this, "서버에 접속할 수 없습니다.", "접속오류", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		} catch (IOException e1) {
			e1.printStackTrace();
		} 

		/* ========================<< frame set >> ======================== */

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(600, 150, 480, 800);

		/* ========================<< panels >> ======================== */

		mainPanel = new JPanel(); // 각각 모아둔다.
		JPanel loginPanel = new JPanel();
		JPanel roomListPanel = new JPanel();
		JPanel roomPanel = new JPanel();

		/* ========================<< layout >> ======================== */

		mainCard = new CardLayout();

		mainPanel.setLayout(mainCard);
		loginPanel.setLayout(null);
		roomListPanel.setLayout(null);
		roomPanel.setLayout(null);

//		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5)); // 지워도 상관 없다.

		/* ========================<< panel set >> ======================== */
		setContentPane(mainPanel);
		mainPanel.add(loginPanel, "loginPanel");
		mainPanel.add(roomListPanel, "roomListPanel");
		mainPanel.add(roomPanel, "roomPanel");

		/* ========================<< login panel >> ======================== */

		JButton enterButton = new JButton("접속하기");

		usernameField = new JTextField();
		usernameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					enterButton.doClick();
			}
		});

		usernameField.setBounds(76, 456, 301, 59);
		loginPanel.add(usernameField);
		usernameField.setColumns(10);

		enterButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				RequestDto<String> usernameCheckReqDto = new RequestDto<String>("usernameCheck",
						usernameField.getText());
				sendRequest(usernameCheckReqDto);
			}
		});
		enterButton.setBounds(76, 539, 301, 49);
		loginPanel.add(enterButton);

		/* ========================<< roomList panel >> ======================== */

		JScrollPane roomListScroll = new JScrollPane();
		roomListScroll.setBounds(125, 0, 329, 751);
		roomListPanel.add(roomListScroll);

		JList roomList = new JList();
		roomListScroll.setViewportView(roomList);

		JButton createRoomButton = new JButton("방생성");
		createRoomButton.setBounds(12, 10, 97, 78);
		roomListPanel.add(createRoomButton);

		/* ========================<< room panel >> ======================== */

		JScrollPane joinUserListScroll = new JScrollPane();
		joinUserListScroll.setBounds(0, 0, 349, 105);
		roomPanel.add(joinUserListScroll);

		JList joinUserList = new JList();
		joinUserListScroll.setViewportView(joinUserList);

		JButton roomExitButton = new JButton("나가기");
		roomExitButton.setBounds(349, 0, 105, 105);
		roomPanel.add(roomExitButton);

		JScrollPane chattingContentScroll = new JScrollPane();
		chattingContentScroll.setBounds(0, 115, 454, 547);
		roomPanel.add(chattingContentScroll);

		JList chattingContent = new JList();
		chattingContentScroll.setViewportView(chattingContent);

		sendMessageField = new JTextField();
		sendMessageField.setBounds(0, 672, 349, 79);
		roomPanel.add(sendMessageField);
		sendMessageField.setColumns(10);

		JButton sendButtion = new JButton("전송");
		sendButtion.setBounds(349, 672, 105, 79);
		roomPanel.add(sendButtion);
	}

	private void sendRequest(RequestDto<?> requestDto) {
		String reqJson = gson.toJson(requestDto);
		OutputStream outputStream = null;
		PrintWriter printWriter = null;
		try {
			outputStream = socket.getOutputStream();
			printWriter = new PrintWriter(socket.getOutputStream(), true);
			printWriter.println(reqJson);
			System.out.println("클라이언트 -> 서버 : " + reqJson);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

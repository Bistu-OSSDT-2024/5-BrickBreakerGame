package ww;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class ww extends JPanel implements ActionListener, KeyListener
{
	 // ��Ϸ����
	   private final int WIDTH = 600, HEIGHT = 400;
	   private final int BRICK_COLUMNS = 10, BRICK_ROWS = 5;
	   private final int BRICK_WIDTH = WIDTH / BRICK_COLUMNS, BRICK_HEIGHT = 20;
	   private Timer timer;
	   private boolean running = true;
	   private boolean gameWon = false;
	   private boolean gameOver = false;
	   private int ballX = WIDTH / 2, ballY = HEIGHT / 2;
	   private int paddleX = WIDTH / 2;
	   private final int BALL_SIZE = 20, PADDLE_WIDTH = 100, PADDLE_HEIGHT = 10;
	   private int ballDeltaX = -2, ballDeltaY = -2; // ����ƶ��ٶ�
	   private boolean[][] bricks = new boolean[BRICK_ROWS][BRICK_COLUMNS];
	   private JButton startButton;
	   private boolean ballMoving = false;  
	   private Color ballColor; // ���ڴ洢�û�ѡ��������ɫ
	   private Color[][] brickColors; // �洢ש����ɫ�Ķ�ά����
	   private int score = 0;
	   public ww() 
	   {
		   //��ʼ��ש������
		   score = 0; 
		   for (int i = 0; i < BRICK_ROWS; i++) 
		   {
	           for (int j = 0; j < BRICK_COLUMNS; j++) 
	           {
	               bricks[i][j] = true;
	           }
	       }
		   // ��ʼ��ש����ɫ����
	        brickColors = new Color[BRICK_ROWS][BRICK_COLUMNS];
	        Random random = new Random();
	        for (int i = 0; i < BRICK_ROWS; i++) {
	            for (int j = 0; j < BRICK_COLUMNS; j++) {
	                brickColors[i][j] = getRandomColor(); // Ϊÿ��ש�������ɫ���洢
	            }
	        }
	       setPreferredSize(new Dimension(WIDTH, HEIGHT));
	       setBackground(Color.BLACK);
	       setFocusable(true);
	       addKeyListener(this);
	       // ������Ϸѭ���Ķ�ʱ��
	       timer = new Timer(28, this);
	       timer.start();
	       
	    // ������ɫѡ��Ի���
	        JColorChooser colorChooser = new JColorChooser();
	        int result = JOptionPane.showConfirmDialog(this, colorChooser, "ѡ�������ɫ", JOptionPane.OK_CANCEL_OPTION);
	        if (result == JOptionPane.OK_OPTION) {
	            ballColor = colorChooser.getColor();
	        } else {
	            ballColor = Color.WHITE; // Ĭ����ɫΪ��ɫ
	        }
	        
	       startButton = new JButton("��ʼ��Ϸ");
	       startButton.addActionListener(new ActionListener()
	       {
	           @Override
	           public void actionPerformed(ActionEvent e) 
	           {
	        	   int selectedSpeed = showSpeedSelectionDialog();
	               if (selectedSpeed != JOptionPane.CLOSED_OPTION) {
	                   adjustBallSpeed(selectedSpeed);
	                   timer.start(); // �û�ѡ���ٶȺ�������Ϸѭ����ʱ��
	                   startGame(); // �����ٶȺ�ʼ��Ϸ
	               }
	           }
	       });
	       add(startButton); // ����ť��ӵ������
	   }
		// �����ٶ�ѡ��Ի���
	    private int showSpeedSelectionDialog() {
	        String[] options = {"��", "�е�", "��"};
	        return JOptionPane.showOptionDialog(
	                null,
	                "��ѡ��С���ٶ�",
	                "�ٶ�ѡ��",
	                JOptionPane.DEFAULT_OPTION,
	                JOptionPane.PLAIN_MESSAGE,
	                null,
	                options,
	                options[0]
	        );
	    }
	    // �����û�ѡ�����С���ٶ�
	    private void adjustBallSpeed(int speedLevel) {
	        switch (speedLevel) {
	            case 0: // ��
	                ballDeltaX = -2;
	                ballDeltaY = -2;
	                break;
	            case 1: // �е�
	                ballDeltaX = -3;
	                ballDeltaY = -3;
	                break;
	            case 2: // ��
	                ballDeltaX = -4;
	                ballDeltaY = -4;
	                break;
	            default:
	                throw new IllegalArgumentException("Invalid speed level");
	        }
	    }
	   private Random random = new Random(); // ����һ�������������ʵ��
		// ���Ʋ�ɫש��
		   private Color getRandomColor() 
		   {
		       // ���������ɫ
		       return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
		   }	 
	   // ��Ϸ��ѭ��
	   public void actionPerformed(ActionEvent e) 
	   {
	       if (running && !gameOver && !gameWon) 
	       {
	           ballX += ballDeltaX;
	           ballY += ballDeltaY; 
	           if (ballMoving) 
	           {
	               // �ƶ�С��
	               ballX += ballDeltaX;
	               ballY += ballDeltaY;
	              // ���ǽ����ײ
	               if (ballX < 0 || ballX > WIDTH - BALL_SIZE) 
	               {
	            	   ballDeltaX *= -1;
	               }
	               if (ballY < 0) 
	               {
	            	   ballDeltaY *= -1;
	               }
	               // ��⵲����ײ
	              if (ballY > HEIGHT - PADDLE_HEIGHT - BALL_SIZE) 
	              {
	            	  if (ballX > paddleX && ballX < paddleX + PADDLE_WIDTH) 
	            	  {
	            		  ballDeltaY *= -1;
	                  }	               
	                  else 
	                 {
	                	  gameOver = true;
	                 }	               
	              }	           
	              // ���ש����ײ
	             collisionWithBricks();
	           }	           
	         repaint();
	       }
	   }	   
	   // �������ש�����ײ
	   private void checkScore() {
		    int brickCount = 0;
		    for (boolean[] row : bricks) {
		        for (boolean brick : row) {
		            if (!brick) {
		                brickCount++; // ͳ�Ʊ����е�ש������
		            }
		        }
		    }
		    score = brickCount * 2; // ÿ��ש���2��
		}
	   private void collisionWithBricks() 
	   {
	       for (int i = 0; i < BRICK_ROWS; i++) 
	       {
	           for (int j = 0; j < BRICK_COLUMNS; j++) 
	           {
	               if (bricks[i][j]) 
	               {
	                   if (ballX > j * BRICK_WIDTH && ballX < j * BRICK_WIDTH + BRICK_WIDTH &&
	                       ballY > i * BRICK_HEIGHT && ballY < i * BRICK_HEIGHT + BRICK_HEIGHT) 
	                   {
	                       bricks[i][j] = false; // �Ƴ�ש��
	                       ballDeltaY *= -1; // ��ת��ķ���
	                       score += 2;
	                       if (score >= 100)
	                       {
	                           gameWon = true; // ������Ϸʤ����־
	                           running = false; // ֹͣ��Ϸѭ��
	                           timer.stop(); 
	                       }
	                       break; // �˳��ڲ�ѭ��
	                   }
	               }
	           }
	       }
	   }	   
	   // ����Ƿ�����ש�鶼��ʧ
	   private void checkBricks() 
	   {
	        boolean allDestroyed = true;
	        for (int i = 0; i < BRICK_ROWS; i++) 
	        {
	            for (int j = 0; j < BRICK_COLUMNS; j++) 
	            {
	                if (bricks[i][j]) 
	                {
	                    allDestroyed = false;
	                    break;
	                }
	            }
	            if (!allDestroyed) 
	            {
	                break;
	            }
	        }	        
	        if (allDestroyed) 
	        {
	            gameWon = true;
	            running = false;
	            timer.stop(); // ֹͣ��ʱ��
	            repaint(); // ˢ�½�������ʾʤ����Ϣ
	        }
	   }	   
	   // ������ϷԪ��
	   @Override
	   protected void paintComponent(Graphics g) 
	   {
	       super.paintComponent(g);
	       g.setColor(ballColor);
	       g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE); // ������
	       g.fillRect(paddleX, HEIGHT - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT); // ���Ƶ���
	       Font scoreFont = new Font("Arial", Font.BOLD, 20); // ����ש��
	       g.setFont(scoreFont);
	       String scoreText = "Score: " + score;
	       FontMetrics metrics = g.getFontMetrics();
	       int textWidth = metrics.stringWidth(scoreText);
	       g.setColor(Color.WHITE);
	       g.drawString(scoreText, (WIDTH - textWidth) / 2, HEIGHT - 200);
	        // ����ש��
	       for (int i = 0; i < BRICK_ROWS; i++) 
	       {
	    	   for (int j = 0; j < BRICK_COLUMNS; j++) 
	    	   {
	    		   if (bricks[i][j]) 
	    		   {
	    			   g.setColor(brickColors[i][j]); // ��֮ǰ���ɵ���ɫ�����л�ȡ��ɫ
	    			   g.fillRect(j * BRICK_WIDTH, i * BRICK_HEIGHT, BRICK_WIDTH, BRICK_HEIGHT);
                   }
               }
           }     
            // ������Ϸ������ʤ����Ϣ
	       g.setFont(new Font("Arial", Font.BOLD, 30));
           g.setColor(Color.WHITE);
           if (gameOver) 
           {
        	   g.setFont(new Font("Arial", Font.BOLD, 36));
               g.setColor(Color.RED);
               String message = "Game Over";
               g.drawString(message, (WIDTH - g.getFontMetrics().stringWidth(message)) / 2, HEIGHT / 2 - 50);
           } 
           else if (gameWon) 
           {
       	       g.setFont(new Font("Arial", Font.BOLD, 36));
               g.setColor(Color.GREEN);
           	   String successMessage = "Congratulations! You won!";
               g.drawString(successMessage, (WIDTH - g.getFontMetrics().stringWidth(successMessage)) / 2, HEIGHT / 2 - 50);
               g.setFont(new Font("Arial", Font.BOLD, 20));
               g.drawString("Your final score: " + score, (WIDTH - metrics.stringWidth("Your final score: " + score)) / 2, HEIGHT / 2 + 50);
               
           }
       }
	   // �����¼�����
	   @Override
	   public void keyPressed(KeyEvent e) 
	   {
	       if (e.getKeyCode() == KeyEvent.VK_LEFT) 
	       {
	           paddleX -= 20;
	       }
	       if (e.getKeyCode() == KeyEvent.VK_RIGHT) 
	       {
	           paddleX += 20;
	       }
	       // ��ֹ�����Ƴ��߽�
	       paddleX = Math.max(paddleX, 0);
	       paddleX = Math.min(paddleX, WIDTH - PADDLE_WIDTH);
	   }	   
	   @Override
	   public void keyReleased(KeyEvent e) 
	   {
		   
	   } 
	   @Override
	   public void keyTyped(KeyEvent e) 
	   {
		   
	   } 
	   public void startGame() 
	   {
	       running = true;// ������һ������ running ������Ϸ����
	       startButton.setVisible(false);// ��Ϸ��ʼ�����ذ�ť
	       // ����С���λ�õ����崦
	       ballX = paddleX + PADDLE_WIDTH / 2 - BALL_SIZE / 2;
	       ballY = HEIGHT - PADDLE_HEIGHT - BALL_SIZE;      
	       ballMoving = true; // ����С���ƶ��ı�־Ϊtrue       
	       // �����û�ѡ���speedLevel����С��ĳ�ʼ�ٶ�
	   }	   
	   public static void main(String[] args) 
	   {
	       JFrame frame = new JFrame("��ש����Ϸ");
	       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	       ww game = new ww();
           frame.getContentPane().add(game);
	       frame.pack();
	       frame.setLocationRelativeTo(null);
	       frame.setVisible(true);
	    }
}

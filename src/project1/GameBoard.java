package project1;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;

public class GameBoard {

    int n;
    private final JPanel main = new JPanel();
    private final ScrollPane scroll = new ScrollPane();
    private final JPanel board = new JPanel(new BorderLayout(3, 3));
    public JButton[][] c1squares;
    private JPanel c1Board, c2Board;
    private JLabel messagec1 = new JLabel("N Queen Board with size "+n+" X "+n);
    JToolBar tool = new JToolBar();
    Insets Margin = new Insets(0, 0, 0, 0);
    int squares;
    int space = 100;
    boolean f;

    static ImageIcon icon;
    static ImageIcon icon1;

    GameBoard(int n, boolean f) {
        this.n = n;
        c1squares = new JButton[n][n];
        squares = n;
        this.f = f;
        messagec1 = new JLabel(n+" Queen Board ");
        initializeGui();
        icon = new ImageIcon(new BufferedImage(space, space, BufferedImage.TYPE_INT_ARGB));
        icon1=new javax.swing.ImageIcon(getClass().getResource("/Images/Black_Queen.png"));
    }

    public final void initializeGui() {

        main.add(board);
        scroll.add(board);
        board.setBorder(new EmptyBorder(5, 5, 5, 5));
        tool.setFloatable(false);
        board.add(tool, BorderLayout.PAGE_START);
        tool.add(messagec1);
        messagec1.setFont(new java.awt.Font("Tahoma", 1, 14));
        c1Board = new JPanel(new GridLayout(0, n));
        c1Board.setBorder(new LineBorder(Color.BLACK));
        board.add(c1Board);

        for (int i = 0; i < c1squares.length; i++) {
            for (int j = 0; j < c1squares[i].length; j++) {
                JButton b = new JButton();
                b.setMargin(Margin);
                b.setIcon(icon);
                b.setBackground(Color.WHITE);
                if (f) {
                    b.addActionListener(e -> {
                        if (b.getBackground()==Color.WHITE) {
                                b.setIcon(icon1);
                                b.setBackground(Color.lightGray);
                        }
                        else
                        { 
                            b.setIcon(icon);
                            b.setBackground(Color.WHITE);
                        }
                    });
                }
                c1squares[j][i] = b;
            }
        }
        for (int i = 0; i < squares; i++) {
            for (int j = 0; j < squares; j++) {
                c1Board.add(c1squares[j][i]);
            }
        }

    }

    public final JComponent getGui() {
        return board;
    }

}

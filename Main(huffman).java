import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main {
    public static void main(String[] args) {
        HuffmanFrame huffmanFrame = new HuffmanFrame();
    }
}

class HuffmanFrame extends JFrame {
    public HuffmanFrame() {
        Mainpanel mainpanel = new Mainpanel();
        add(mainpanel);
        pack();
        setTitle("Huffman");
        setSize(640, 240);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

}

class SubFrame extends JFrame {
    private int num, level;
    private int side = 20;
    private int height = 50;
    private int width = 40;
    private int[] weight;
    private char[] key;
    private String[] code;

    private JPanel kwc = new JPanel();
    private JLabel[] title = new JLabel[3];
    private JLabel[] keyLable, weightLable, codeLable;
    private paintPanel paintpanel;

    public SubFrame(int n, int[] w, char[] k, String[] c) {
        num = n;
        weight = new int[2 * num - 1];
        key = new char[num];
        code = new String[2 * num - 1];
        level = c[0].length();
        for (int i = 0; i < num; ++i) {
            weight[i] = w[i];
            key[i] = k[i];
            code[i] = c[i];
            if (level < code[i].length())
                level = code[i].length();
        }

        for (int i = num; i < 2 * num - 1; ++i) {
            weight[i] = w[i];
            code[i] = c[i];
        }

        paintpanel = new paintPanel(num, level, side, width, height, weight, key, code);
        pack();
        setTitle("Huffman Tree");
        setSize(width * ((int) (Math.pow(2, level)) - 1) + (level + 2) * side, level * height + 20 * (num + 1) + (level + 2) * side);
        setLayout(new BorderLayout(side, side));

        add(paintpanel, BorderLayout.CENTER);
        keyLable = new JLabel[num];
        weightLable = new JLabel[num];
        codeLable = new JLabel[num];
        kwc.setLayout(new GridLayout(num + 1, 3));
        kwc.add(title[0] = new JLabel("key"));
        kwc.add(title[1] = new JLabel("weight"));
        kwc.add(title[2] = new JLabel("code"));
        for (int i = 0; i < num; ++i) {
            kwc.add(keyLable[i] = new JLabel(Character.toString(key[i])));
            kwc.add(weightLable[i] = new JLabel(Integer.toString(weight[i])));
            kwc.add(codeLable[i] = new JLabel(code[i]));
        }
        add(kwc,BorderLayout.SOUTH);
        //setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}

class paintPanel extends JPanel {
    private int num, level, side, width, height;
    private int[] weight;
    private char[] key;
    private String[] code;

    public paintPanel(int n, int l, int s, int wid, int h, int[] w, char[] k, String[] c) {
        num = n;
        level = l;
        side = s;
        width = wid;
        height = h;
        weight = new int[2 * num - 1];
        key = new char[num];
        code = new String[2 * num - 1];
        for (int i = 0; i < num; ++i) {
            weight[i] = w[i];
            key[i] = k[i];
            code[i] = c[i];
        }
        for (int i = num; i < 2 * num - 1; ++i) {
            weight[i] = w[i];
            code[i] = c[i];
        }
        setLayout(new BorderLayout(5,5));

    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int[] lineWidth = new int[level + 1];
        lineWidth[level] = width;
        for (int i = level - 1; i >= 0; --i)
            lineWidth[i] = lineWidth[i + 1] * 2;
        int westStart = side + lineWidth[0] / 2;
        int southStart = side;
        g.drawOval(westStart, southStart, 20, 20);
        int x, y;
        for (int i = 0; i < 2 * num - 1; ++i) {
            southStart = side;
            westStart = side + lineWidth[0] / 2;
            for (int j = 0; j < code[i].length(); ++j) {
                x = westStart;
                y = southStart;
                if (code[i].charAt(j) == '1')
                    westStart += lineWidth[j + 1] / 2;
                else
                    westStart -= lineWidth[j + 1] / 2;
                southStart += height;
                g.drawLine(x + 10, y + 20, westStart + 10, southStart);
                g.drawString(Character.toString(code[i].charAt(j)), (x + westStart) / 2 + 10, (y + southStart) / 2 + 10);
            }
            g.drawString(Integer.toString(weight[i]), westStart + 5, southStart + 15);
            if (i < num) {
                g.setColor(Color.RED);
                g.drawString(Character.toString(key[i]), westStart + 8, southStart + 30);
            } else
                g.setColor(Color.BLUE);
            g.drawOval(westStart, southStart, 20, 20);
            g.setColor(Color.BLACK);
        }
    }
}

class Mainpanel extends JPanel {
    private Huffman huffman;
    private int num;
    private int[] weight;
    private char[] key;
    private String str;

    private JRadioButton path1, path2, path3;
    private JLabel dpath1, dpath2, dpath3, content;
    private JPanel[] subPanel = new JPanel[2];
    private JTextField jtf = new JTextField("input your text here");
    private JButton start = new JButton("生成");

    public Mainpanel() {
        setLayout(new GridLayout(2, 1));
        for (int i = 0; i < 2; ++i)
            subPanel[i] = new JPanel();

        subPanel[0].setLayout(new GridLayout(3, 2));
        subPanel[0].add(path1 = new JRadioButton("方式一:"));
        subPanel[0].add(dpath1 = new JLabel("给定一组关键值"));
        subPanel[0].add(path2 = new JRadioButton("方式二:"));
        subPanel[0].add(dpath2 = new JLabel("给定一段文本"));
        subPanel[0].add(path3 = new JRadioButton("方式三:"));
        subPanel[0].add(dpath3 = new JLabel("随机输入的文本"));
        subPanel[1].setLayout(new BorderLayout());
        content = new JLabel("未指定内容");
        content.setFont(new Font("Serif,", Font.BOLD, 24));
        subPanel[1].add(content, BorderLayout.NORTH);
        subPanel[1].add(jtf, BorderLayout.CENTER);
        subPanel[1].add(start, BorderLayout.SOUTH);

        for (int i = 0; i < 2; ++i)
            add(subPanel[i]);

        ButtonGroup group = new ButtonGroup();
        group.add(path1);
        group.add(path2);
        group.add(path3);

        path1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                num = 7;
                weight = new int[]{5, 4, 12, 3, 19, 8, 21};
                key = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g'};
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < num; ++i)
                    sb.append(key[i] + "-" + weight[i] + " ");
                String s = sb.toString();
                content.setText(s);
            }
        });

        path2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                str = "↑↑↓↓←←→→→AABB↓→A↑↓A↓↓A";
                content.setText(str);
                extract(str);
            }
        });

        jtf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (path3.isSelected()) {
                    str = jtf.getText();
                    content.setText(str);
                    extract(str);
                }
            }
        });

        path3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                str = jtf.getText();
                content.setText(str);
                extract(str);
            }
        });

        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                huffman = new Huffman(num, weight);
                SubFrame huffmanTree = new SubFrame(num, huffman.getW(), key, huffman.HuffmanEncoding());
            }
        });
    }

    private void extract(String s) {
        int cnt = 0;
        boolean flag;
        StringBuilder sbt = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            flag = true;
            for (int j = 0; j < i; ++j)
                if (s.charAt(i) == s.charAt(j)) {
                    flag = false;
                    break;
                }
            if (flag) {
                ++cnt;
                sbt.append(s.charAt(i));
            }
        }
        String st = sbt.toString();
        num = cnt;
        key = st.toCharArray();
        weight = new int[cnt];
        for (int i = 0; i < s.length(); ++i)
            if (st.indexOf(s.charAt(i)) >= 0)
                ++weight[st.indexOf(s.charAt(i))];
    }
}

class HuffmanNode {
    int weight;
    int parent, leftChild, rightChild;

    public HuffmanNode() {
        weight = 0;
        parent = -1;
        leftChild = -1;
        rightChild = -1;
    }
}

class Huffman {
    int n;
    int[] w;
    HuffmanNode[] HT;

    public Huffman(int num, int[] weight) {
        n = num;
        int m = 2 * n - 1;
        w = new int[m];
        for (int i = 0; i < n; ++i)
            w[i] = weight[i];
        HT = new HuffmanNode[m];
        for (int i = 0; i < m; ++i)
            HT[i] = new HuffmanNode();

        for (int i = 0; i < n; ++i)
            HT[i].weight = w[i];

        for (int i = n; i < m; ++i) {
            int[] s = Select(i);
            HT[s[0]].parent = i;
            HT[s[1]].parent = i;
            HT[i].leftChild = s[0];
            HT[i].rightChild = s[1];
            HT[i].weight = HT[s[0]].weight + HT[s[1]].weight;
        }
    }

    public String[] HuffmanEncoding() {
        if (n <= 1)
            return null;

        String[] HuffmanCode = new String[2 * n - 1];
        int c, f;   //child, father
        for (int i = 0; i < 2 * n - 1; ++i) {
            StringBuilder sb = new StringBuilder();
            for (c = i, f = HT[i].parent; f != -1; c = f, f = HT[f].parent) {
                if (HT[f].leftChild == c)
                    sb.append('0');
                else
                    sb.append('1');
            }
            sb.reverse();
            HuffmanCode[i] = sb.toString();
        }
        return HuffmanCode;
    }

    public int[] getW() {
        if (n <= 1)
            return null;

        int[] weight = new int[2 * n - 1];
        for (int i = 0; i < 2 * n - 1; ++i)
            weight[i] = HT[i].weight;

        return weight;
    }

    public int[] Select(int i) {
        int[] s = new int[2];
        for (int j = 0; j < i; ++j)
            if (HT[j].parent == -1) {
                s[0] = j;
                break;
            }

        for (int j = 0; j < i; ++j)
            if (HT[j].parent == -1 && HT[j].weight < HT[s[0]].weight)
                s[0] = j;

        for (int j = 0; j < i; ++j) {
            if (HT[j].parent == -1 && j != s[0]) {
                s[1] = j;
                break;
            }
        }

        for (int j = 0; j < i; ++j)
            if (HT[j].parent == -1 && j != s[0] && HT[j].weight < HT[s[1]].weight)
                s[1] = j;
        return s;
    }
}
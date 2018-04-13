import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;

public class Graph {

    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame();
    }
}

class MainFrame extends JFrame {
    private paintPanel paintPanel = new paintPanel();

    public MainFrame() {
        setLayout(new BorderLayout());
        add(paintPanel, BorderLayout.CENTER);
        pack();
        setTitle("AOE");
        setSize(640, 480);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class paintPanel extends JPanel {
    private JLabel vertexnum = new JLabel("顶点数:");
    private JLabel fromPoint = new JLabel("from:");
    private JLabel toPoint = new JLabel("to:");
    private JLabel weightLabel = new JLabel("weight:");
    private JLabel status = new JLabel("请先输入点的个数");
    private JTextField jtfVer = new JTextField(10);
    private JTextField jtfFrom = new JTextField(5);
    private JTextField jtfTo = new JTextField(5);
    private JTextField jtfWeight = new JTextField(5);
    private JButton confirm = new JButton("确认");
    private JButton addEdge = new JButton("添加边");
    private JButton generate = new JButton("求解AOE活动网络");
    private JPanel jp1 = new JPanel();
    private JPanel jp2 = new JPanel();
    private JPanel jp3 = new JPanel();
    private JPanel controlPanel = new JPanel();
    public Graph graph;
    private boolean solved;
    private int count;

    public paintPanel() {
        count = 0;
        jp1.setLayout(new FlowLayout());
        jp1.add(vertexnum);
        jp1.add(jtfVer);
        jp1.add(confirm);
        jp2.setLayout(new FlowLayout());
        jp2.add(fromPoint);
        jp2.add(jtfFrom);
        jp2.add(toPoint);
        jp2.add(jtfTo);
        jp2.add(weightLabel);
        jp2.add(jtfWeight);
        jp2.add(addEdge);
        jp3.setLayout(new FlowLayout());
        jp3.add(status);
        jp3.add(generate);
        controlPanel.setLayout(new GridLayout(3, 1));
        controlPanel.add(jp1);
        controlPanel.add(jp2);
        controlPanel.add(jp3);

        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int num = Integer.parseInt(jtfVer.getText());
                graph = new Graph(num);
                status.setText("请点击窗口空白出" + num + "次");
            }
        });

        addEdge.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int vfrom, vto, value;
                vfrom = Integer.parseInt(jtfFrom.getText());
                vto = Integer.parseInt(jtfTo.getText());
                value = Integer.parseInt(jtfWeight.getText());
                graph.addEdge(vfrom, vto, value);
                status.setText("");
                repaint();
            }
        });

        generate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (graph.ToplogicalSort()) {
                    graph.CriticalPath();
                    status.setText("全工程完成最早时间为:" + graph.minimumTime);
                    solved = true;
                    repaint();
                } else
                    status.setText("无解");
            }
        });
        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.SOUTH);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (count < graph.vernum) {
                    graph.x[count] = e.getX();
                    graph.y[count] = e.getY();
                    ++count;
                    if (graph.vernum == count)
                        status.setText("请添加边");
                    else
                        status.setText("请点击窗口空白出" + (graph.vernum - count) + "次");
                    repaint();
                }
            }
        });
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (count != 0) {
            if (count < graph.vernum) {
                for (int i = 0; i < graph.vernum; ++i) {
                    if (graph.x[i] != 0 && graph.y[i] != 0) {
                        g.drawOval(graph.x[i], graph.y[i], 30, 30);
                        g.drawString(i + "", graph.x[i] + 10, graph.y[i] + 20);
                    }
                }
            } else {
                Graphics2D g2 = (Graphics2D) g;
                GraphNode p;
                int j;
                for (int i = 0; i < graph.vernum; ++i) {
                    p = graph.vertex[i].first.nextnode;
                    while (p != null) {
                        j = p.info;
                        if (solved && graph.e[i][j] == graph.l[i][j])
                            g.setColor(Color.RED);
                        drawAL(graph.x[i] + 30, graph.y[i] + 15, graph.x[j], graph.y[j] + 15, g2);
                        if (solved)
                            g.drawString("e=" + graph.e[i][j] + ",l=" + graph.l[i][j] + ",w=" + graph.weight[i][j], (graph.x[i] + graph.x[j]) / 2, (graph.y[i] + graph.y[j]) / 2);
                            //g.drawString("e(" + i + "," + j + ")=" + graph.e[i][j] + ",l(" + i + "," + j + ")=" + graph.l[i][j] + "w(" + i + "," + j + ")=" + graph.weight[i][j], (graph.x[i] + graph.x[j]) / 2, (graph.y[i] + graph.y[j]) / 2);
                        else
                            g.drawString("w(" + i + "," + j + ")=" + graph.weight[i][j], (graph.x[i] + graph.x[j]) / 2, (graph.y[i] + graph.y[j]) / 2);
                        p = p.nextnode;
                        g.setColor(Color.black);
                    }
                    g.drawOval(graph.x[i], graph.y[i], 30, 30);
                    g.drawString(i + " ", graph.x[i] + 10, graph.y[i] + 20);
                }
            }
        }
    }

    public static void drawAL(int sx, int sy, int ex, int ey, Graphics2D g2) {

        double H = 10; // 箭头高度
        double L = 4; // 底边的一半
        int x3 = 0;
        int y3 = 0;
        int x4 = 0;
        int y4 = 0;
        double awrad = Math.atan(L / H); // 箭头角度
        double arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度
        double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
        double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
        double x_3 = ex - arrXY_1[0]; // (x3,y3)是第一端点
        double y_3 = ey - arrXY_1[1];
        double x_4 = ex - arrXY_2[0]; // (x4,y4)是第二端点
        double y_4 = ey - arrXY_2[1];

        Double X3 = new Double(x_3);
        x3 = X3.intValue();
        Double Y3 = new Double(y_3);
        y3 = Y3.intValue();
        Double X4 = new Double(x_4);
        x4 = X4.intValue();
        Double Y4 = new Double(y_4);
        y4 = Y4.intValue();
        // 画线
        g2.drawLine(sx, sy, ex, ey);
        //
        GeneralPath triangle = new GeneralPath();
        triangle.moveTo(ex, ey);
        triangle.lineTo(x3, y3);
        triangle.lineTo(x4, y4);
        triangle.closePath();
        //实心箭头
        g2.fill(triangle);
        //非实心箭头
        //g2.draw(triangle);

    }

    // 计算
    public static double[] rotateVec(int px, int py, double ang,
                                     boolean isChLen, double newLen) {

        double mathstr[] = new double[2];
        // 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度
        double vx = px * Math.cos(ang) - py * Math.sin(ang);
        double vy = px * Math.sin(ang) + py * Math.cos(ang);
        if (isChLen) {
            double d = Math.sqrt(vx * vx + vy * vy);
            vx = vx / d * newLen;
            vy = vy / d * newLen;
            mathstr[0] = vx;
            mathstr[1] = vy;
        }
        return mathstr;
    }
}

//图的顶点
class GraphNode {
    public GraphNode nextnode;
    public int info;
}

//图的邻接表
class GraphList {
    public int indagree;
    public GraphNode first, last;

    public void addNode(int v) {
        GraphNode node = new GraphNode();
        node.info = v;
        if (first == null) {
            first = node;
            last = node;
        } else {
            last.nextnode = node;
            last = node;
        }
    }
}

//图
class Graph {
    public int vernum, edgenum;
    public GraphList[] vertex;
    public int[][] weight;

    public int[][] e;
    public int[][] l;
    public int minimumTime;
    public int[] x, y;

    public Graph(int vernum) {
        this.vernum = vernum;
        edgenum = 0;
        vertex = new GraphList[vernum];
        weight = new int[vernum][vernum];
        e = new int[vernum][vernum];
        l = new int[vernum][vernum];
        minimumTime = 0;
        x = new int[vernum];
        y = new int[vernum];
        for (int i = 0; i < vernum; i++)
            addGraph(i);
    }

    //添加节点
    public void addGraph(int info) {
        for (int i = 0; i < vernum; ++i) {
            if (vertex[i] == null) {
                vertex[i] = new GraphList();
                vertex[i].addNode(info);
                break;
            }
        }
    }

    //添加边
    public void addEdge(int vfrom, int vto, int value) {
        vertex[vfrom].addNode(vto);
        weight[vfrom][vto] = value;
        weight[vto][vfrom] = value;
        ++edgenum;
    }

    public void print() {
        for (int i = 0; i < vernum; i++) {
            GraphNode current = vertex[i].first;
            while (current != null) {
                System.out.print(current.info + " ");
                current = current.nextnode;
            }
            System.out.println("");
        }

    }

    //计算各顶点入度
    void calcIndegree() {
        GraphNode current;
        for (int i = 0; i < vernum; ++i) {
            for (current = vertex[i].first.nextnode; current != null; current = current.nextnode) {
                vertex[current.info].indagree++;
            }
        }
    }


    //拓扑排序
    boolean ToplogicalSort() {
        GraphNode p;
        int i, k;
        calcIndegree();
        int top = -1;
        for (i = 0; i < vernum; ++i) {
            if (vertex[i].indagree == 0) {
                vertex[i].indagree = top;
                top = i;  //入度为零的顶点进栈
            }
        }
        int count = 0;
        while (top + 1 != 0) {
            i = top;
            top = vertex[top].indagree;
            ++count;
            for (p = vertex[i].first.nextnode; p != null; p = p.nextnode) {
                k = p.info;
                vertex[k].indagree--;
                if (vertex[k].indagree == 0) {
                    vertex[k].indagree = top;
                    top = k;
                }
            }
        }
        if (count < vernum)
            return false;
        else
            return true;
    }

    //关键路径
    void CriticalPath() {
        int i, j;
        int[] ve = new int[vernum];
        int[] vl = new int[vernum];
        GraphNode p;
        //求ve[i]
        for (i = 0; i < vernum; ++i) {
            p = vertex[i].first.nextnode;
            while (p != null) {
                j = p.info;
                if (ve[i] + weight[i][j] > ve[j])
                    ve[j] = ve[i] + weight[i][j];
                p = p.nextnode;
            }
        }
        //求vl[i]
        for (i = 0; i < vernum; ++i)
            if (minimumTime < ve[i])
                minimumTime = ve[i];
        for (i = 0; i < vernum; ++i)
            vl[i] = minimumTime;
        for (i = vernum - 1; i >= 0; --i) {
            p = vertex[i].first.nextnode;
            while (p != null) {
                j = p.info;
                if (vl[j] - weight[i][j] < vl[i])
                    vl[i] = vl[j] - weight[i][j];
                p = p.nextnode;
            }
        }

        for (i = 0; i < vernum; ++i) {
            p = vertex[i].first.nextnode;
            while (p != null) {
                j = p.info;
                e[i][j] = ve[i];
                l[i][j] = vl[j] - weight[i][j];
                p = p.nextnode;
            }
        }

        for (j = 0; j < vernum; ++j) {
            for (i = 0; i < j; ++i) {
                if (weight[i][j] != 0) {
                    System.out.println("(" + i + "," + j + ")" + " " + e[i][j] + " " + l[i][j]);
                }
            }
        }
        System.out.println("minimum time=" + minimumTime);
    }
}

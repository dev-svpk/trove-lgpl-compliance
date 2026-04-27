package solver;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipFile;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public final class GameTree extends JTree {
   ArrayList a = new ArrayList();
   PokerTreeNode b;
   FilterSettings c;
   JButton d = new JButton("Undo");
   JButton e;

   public GameTree(PokerTreeNode var1) {
      super(var1);

      this.b = var1;

      this.d.addActionListener((var1x) -> {
         this.d();
      });
      this.e = new JButton("Redo");
      this.e.addActionListener((var1x) -> {
         this.e();
      });
      this.d.setEnabled(false);
      this.e.setEnabled(false);
      AnalysisPanel.l = Math.round((float)this.getFont().getSize() * 1.8F);
      this.setRowHeight(AnalysisPanel.l);
      JPopupMenu var11 = new JPopupMenu();
      JMenuItem var2;
      (var2 = new JMenuItem("Remove")).addActionListener(new SaveRangeListener(this));
      JMenuItem var3;
      (var3 = new JMenuItem("Remove all")).addActionListener((var0) -> {
         if (AnalysisPanel.b != null) {
            ArrayList var10 = new ArrayList(AnalysisPanel.g.c);
            if (AnalysisPanel.g.customBetCheckBox.isSelected() && AnalysisPanel.g.d.a() > 0) {
               var10.add(AnalysisPanel.g.d);
            }

            AnalysisPanel.b.a(var10);
         }

      });
      JMenu var4 = new JMenu("Add to all");
      JMenu var5 = new JMenu("Saved filters");
      JMenuItem var6;
      (var6 = new JMenuItem("Custom filter")).addActionListener((var1x) -> {
         AnalysisPanel.p = new FilterSettings();
         this.f();
      });
      JMenuItem var7;
      (var7 = new JMenuItem("Postflop nodes")).addActionListener((var1x) -> {
         if (AnalysisPanel.b != null) {
            AnalysisPanel.q.p = new ArrayList(AnalysisPanel.g.c);
            if (AnalysisPanel.g.customBetCheckBox.isSelected() && AnalysisPanel.g.d.a() > 0) {
               AnalysisPanel.q.p.add(AnalysisPanel.g.d);
            }

            AnalysisPanel.q.a = false;
            AnalysisPanel.q.b = true;
            this.a(AnalysisPanel.b, AnalysisPanel.q);
         }

      });
      JMenuItem var8;
      (var8 = new JMenuItem("Postflop nodes w/o donk")).addActionListener((var1x) -> {
         if (AnalysisPanel.b != null) {
            AnalysisPanel.q.p = new ArrayList(AnalysisPanel.g.c);
            if (AnalysisPanel.g.customBetCheckBox.isSelected() && AnalysisPanel.g.d.a() > 0) {
               AnalysisPanel.q.p.add(AnalysisPanel.g.d);
            }

            AnalysisPanel.q.a = true;
            this.a(AnalysisPanel.b, AnalysisPanel.q);
         }

      });
      JMenuItem var9;
      (var9 = new JMenuItem("Preflop nodes")).addActionListener(new ClearRangeListener(this));
      var4.add(var9);
      var4.add(var7);
      var4.add(var8);
      var4.addSeparator();
      var4.add(var6);
      util.AppFile var10 = new util.AppFile("Filters");
      var11.addPopupMenuListener(new SelectAllRangeListener(this, var4, var9, var7, var8, var6, var10, var5));
      var11.add(var4);
      JMenuItem var13;
      (var13 = new JMenuItem("Edit")).addActionListener(new DeselectAllRangeListener(this));
      var11.add(var13);
      var11.add(var2);
      var11.add(var3);
      (new JMenuItem("Expand all")).addActionListener((var1x) -> {
         EventQueue.invokeLater(() -> {
            this.a(AnalysisPanel.b, true);
         });
      });
      (new JMenuItem("Collapse all")).addActionListener((var1x) -> {
         EventQueue.invokeLater(() -> {
            this.a(AnalysisPanel.b, false);
         });
      });
      InvertRangeListener var12 = new InvertRangeListener(this, var11);
      this.setCellRenderer(AnalysisPanel.m);
      this.addTreeSelectionListener(AnalysisPanel.o);
      this.addMouseListener(var12);
      this.setUI(new PokerTree$6(this));
      this.addKeyListener(new CopyRangeListener(this));
      this.getInputMap(1).clear();
      this.getInputMap().clear();
   }

   private synchronized void d() {
      Iterator var2 = this.a.iterator();

      while(var2.hasNext()) {
         ((PokerTreeNode)var2.next()).e();
      }

      this.a.clear();
      this.e.setEnabled(true);
      this.d.setEnabled(false);
   }

   private synchronized void e() {
      if (this.b != null && this.c != null) {
         this.a(this.b, this.c);
      }

      this.e.setEnabled(false);
   }

   public static void a(PokerTreeNode var0, ArrayList var1) {
      // This one for just adding one node
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         BetType var3;
         if ((var3 = (BetType)var2.next()).a() == -1) {
            var0.a(solver.BetType.a(var0.c).a(), false);
         } else {
            var0.a(var3.a(), false);
         }
      }

      if (AnalysisPanel.g.customBetCheckBox.isSelected() && AnalysisPanel.g.d.a() > 0) {
         var0.a(AnalysisPanel.g.d.a(), false);
      }

   }

   public static void a(PokerTreeNode var0) {
      var0.removeFromParent();
   }

   private void a(PokerTreeNode var1, FilterSettings var2) {
      this.a.clear();
      var1.a(var2, this.a);
      this.c = new FilterSettings(var2);
      this.b = var1;
      this.d.setEnabled(true);
      this.e.setEnabled(false);
   }

   private void f() {
      if (AnalysisPanel.b != null) {
         JDialog var1 = new JDialog(MainTabbedPane.k, false);
         JPanel var2;
         (var2 = new JPanel()).setLayout(new BorderLayout());
         JPanel var3 = AnalysisPanel.a(var1, new JPanel(), AnalysisPanel.p, true);
         var2.add(var3, "Center");
         JButton var4;
         (var4 = new JButton("Apply")).addActionListener((var2x) -> {
            this.a(AnalysisPanel.b, AnalysisPanel.p);
            var1.setVisible(false);
            var1.dispose();
         });
         JButton var5;
         (var5 = new JButton("Cancel")).addActionListener((var1x) -> {
            var1.setVisible(false);
            var1.dispose();
         });
         JButton var6;
         (var6 = new JButton("Save")).addActionListener((var0) -> {
            String var33;
            if ((var33 = JOptionPane.showInputDialog((Component)null, solver.HashUtil.j(new char[0]), "Save as", -1)) != null) {
               String var11 = var33;
               FilterSettings var44 = AnalysisPanel.p;

               try {
                  Equity.a((File)(new util.AppFile("Filters")), (String)var11, (Object)var44.a());
                  return;
               } catch (Exception var22) {
                  var22.printStackTrace();
               } catch (Throwable ex) {
                  Logger.getLogger(GameTree.class.getName()).log(Level.SEVERE, null, ex);
               }
            }

         });
         var3.setBorder(new EmptyBorder(0, 0, 18, 0));
         (var3 = new JPanel()).add(var4);
         var3.add(var5);
         var3.add(var6);
         var2.add(var3, "South");
         var1.setContentPane(var2);
         var1.pack();
         var1.setVisible(true);
      }

   }

   private void a(JMenu var1, File var2) {
      File[] var5;
      int var4 = (var5 = var2.listFiles()).length;

      for(int var3 = 0; var3 < var4; ++var3) {
         if ((var2 = var5[var3]).isDirectory()) {
            JMenu var6 = new JMenu(var2.getName());
            this.a(var6, var2);
         } else {
            JMenuItem var7 = new JMenuItem(var2.getName());
            var1.add(var7);
            File var15 = var2;
            var7.addActionListener((var2x) -> {
               AnalysisPanel.p = FilterSettings.a((Properties)Equity.a(var15));
               this.f();
            });
         }
      }

   }

   public final GameState a() {
      return ((PokerTreeNode)this.getModel().getRoot()).c;
   }

   public final int b() {
      return ((PokerTreeNode)this.getModel().getRoot()).c.nWay;
   }

   public final void c() {
      ((DefaultTreeModel)this.getModel()).reload();
   }

   private void a(PokerTreeNode var1, boolean var2) {
      TreeExpansionListener[] var3;
      TreeExpansionListener[] var7;
      int var6 = (var7 = var3 = this.getTreeExpansionListeners()).length;

      for(int var5 = 0; var5 < var6; ++var5) {
         TreeExpansionListener var4 = var7[var5];
         this.removeTreeExpansionListener(var4);
      }

      TreePath var9 = new TreePath(var1.getPath());
      a(this, var9, var2);
      TreeExpansionListener[] var8 = var3;
      int var11 = var3.length;

      for(var6 = 0; var6 < var11; ++var6) {
         TreeExpansionListener var10 = var8[var6];
         this.addTreeExpansionListener(var10);
      }

      this.collapsePath(var9);
      if (var2) {
         this.expandPath(var9);
      }

   }

   private static void a(JTree var0, TreePath var1, boolean var2) {
      TreeModel var3 = var0.getModel();
      Object var4 = var1.getLastPathComponent();
      int var5;
      if ((var5 = var3.getChildCount(var4)) != 0) {
         if (var2) {
            var0.expandPath(var1);
         } else {
            var0.collapsePath(var1);
         }

         for(int var6 = 0; var6 < var5; ++var6) {
            Object var7 = var3.getChild(var4, var6);
            if (var3.getChildCount(var7) > 0) {
               LoadRangeListener var9 = new LoadRangeListener(var1, var7);
               a(var0, var9, var2);
            }
         }

      }
   }

   private static void a(BufferedWriter var0, int var1) throws IOException {
      var0.write(var1 >> 16);
      var0.write(var1 & '\uffff');
   }

   private static int a(BufferedReader var0) throws IOException {
      int var1 = var0.read() << 16;
      return var0.read() | var1;
   }

   public final void writeToFile(BufferedWriter var1, boolean var2) throws IOException {
      PokerTreeNode var3 = (PokerTreeNode)this.getModel().getRoot();
      var1.write(33485);
      var1.write(var3.c.nWay);
      var1.write(var3.c.firstPlayerToAct);
      var1.write(var3.c.gameStage);
      int var4;
      if (var3.c.gameStage == 0) {
         for(var4 = 0; var4 < var3.c.nWay; ++var4) {
            a(var1, var3.c.bets[var4]);
         }
      }

      a(var1, var3.c.deadMoney[0]);

      for(var4 = 0; var4 < var3.c.nWay; ++var4) {
         var1.write(var3.c.stacks[var4] / 1000);
      }

      this.a(var1, var3);

      if (var2) {
         for(var4 = 0; var4 < var3.c.nWay; ++var4) {
            double[] var9 = AnalysisPanel.a(var4).a;

            for(int var5 = 0; var5 < var9.length; ++var5) {
               int tmp = 0;

               if ((var9[var5] >= 0.84D) && (var9[var5] < 0.855D) ) {
                  tmp = (int)Math.round(0.84D * 65535.0D);
               }
               if ((var9[var5] >= 0.855D) && (var9[var5] <= 0.88D) ) {
                  tmp = (int)Math.round(0.88D * 65535.0D);
               }

            	/*if (var9[var5] == 0.85D) {
            		tmp = (int)Math.round(0.84D * 65535.0D);
            	}
            	if (var9[var5] == 0.858D) {
            		tmp = (int)Math.round(0.84D * 65535.0D);
            	}
            	if (var9[var5] == 0.86D) {
            		tmp = (int)Math.round(0.84D * 65535.0D);
            	}
            	if (var9[var5] == 0.866D) {
            		tmp = (int)Math.round(0.88D * 65535.0D);
            	}
            	if (var9[var5] == 0.87D) {
            		tmp = (int)Math.round(0.88D * 65535.0D);
            	}*/

               if (tmp == 0) {
                  tmp = (int)Math.round(var9[var5] * 65535.0D);
               }

               char var6 = (char)(tmp);
               var1.write(var6 + "");
            }
         }
      }

   }

   private void a(BufferedWriter var1, PokerTreeNode var2) throws IOException {
      Enumeration var3 = var2.children();
      if (var2.nodeType >= 0) {
         var1.write(var2.nodeType);
      }

      var1.write(var2.answers.length);

      while(var3.hasMoreElements()) {
         this.a(var1, (PokerTreeNode)var3.nextElement());
      }

   }

   public static GameState a(File var0) throws Throwable {
      if (var0.getName().endsWith(".mkr") && !var0.isDirectory()) {
         try {
            Throwable var1 = null;

            try {
               ZipFile var11 = new ZipFile(var0, StandardCharsets.UTF_16);

               GameState var10000;
               try {
                  var10000 = b(var11.getInputStream(var11.getEntry("tree")));
               } finally {
                  var11.close();
               }

               return var10000;
            } catch (Throwable var9) {
               if (var1 == null) {
                  var1 = var9;
               } else if (var1 != var9) {
                  var1.addSuppressed(var9);
               }

               throw var1;
            }
         } catch (Exception var10) {
            var10.printStackTrace();
            return null;
         }
      } else {
         return b(new File(var0, "tree"));
      }
   }

   private static GameState readNodeFromFile(BufferedReader var0) throws IOException {
      int nWay = var0.read();
      byte version = 0;
      if (nWay > 120) {
         version = 1;
         if (nWay == 33482) {
            version = 2;
         } else if (nWay == 33483) {
            version = 3;
         } else if (nWay == 33484) {
            version = 4;
         } else if (nWay == 33485) {
            version = 5;
         }

         nWay = var0.read();
      }

      int firstPlayerToAct = -1;
      if (version >= 5) {
         firstPlayerToAct = var0.read();
      }

      int gameStage = var0.read();
      int[] bets = null;
      int var8;
      if (gameStage == 0 && version >= 3) {
         ArrayList var6 = new ArrayList();

         int var7;
         for(var7 = 0; var7 < nWay; ++var7) {
            if (version >= 4) {
               var8 = a(var0);
            } else {
               var8 = var0.read();
            }

            if (var8 > 0) {
               var6.add(var8);
            }
         }

         Collections.sort(var6);
         bets = new int[var6.size()];

         for(var7 = 0; var7 < bets.length; ++var7) {
            bets[var7] = (Integer)var6.get(var7) / 100;
         }
      }

      int deadMoney;
      if (version >= 2) {
         deadMoney = a(var0);
      } else {
         deadMoney = var0.read();
      }

      if (version == 0) {
         deadMoney *= 1000;
      }

      GameState var10 =
              bets == null ?
                      new GameState(nWay, gameStage, deadMoney, 0, -1) :
                      new GameState(bets, nWay, gameStage, deadMoney, firstPlayerToAct, 0, -1);

      for(var8 = 0; var8 < var10.nWay; ++var8) {
         var10.stacks[var8] = var0.read() * 1000;
      }

      return var10;
   }

   private static GameTree readTreeFromFile(BufferedReader inputBuffer) throws IOException {
      int nWay = inputBuffer.read();
      byte version = 0;
      if (nWay > 120) {
         version = 1;
         if (nWay == 33482) {
            version = 2;
         } else if (nWay == 33483) {
            version = 3;
         } else if (nWay == 33484) {
            version = 4;
         } else if (nWay == 33485) {
            version = 5;
         }

         nWay = inputBuffer.read();
      }

      int firstPlayerToAct = -1;
      if (version >= 5) {
         firstPlayerToAct = inputBuffer.read();
      }

      int gameStage = inputBuffer.read();
      int[] bets = null;
      int var7;
      int var10;
      if (gameStage == 0 && version >= 3) {
         ArrayList var6 = new ArrayList();

         for(var7 = 0; var7 < nWay; ++var7) {
            if (version >= 4) {
               var10 = a(inputBuffer);
            } else {
               var10 = inputBuffer.read();
            }

            var6.add(var10);
         }

         if (nWay > 2) {
            var7 = (Integer)var6.get(0);
            var6.remove(0);
            var6.add(var7);
         }

         bets = new int[var6.size()];

         for(var7 = 0; var7 < bets.length; ++var7) {
            bets[var7] = (Integer)var6.get(var7) / 100;
         }
      }

      int deadMoney;
      if (version >= 2) {
         deadMoney = a(inputBuffer);
      } else {
         deadMoney = inputBuffer.read();
      }

      if (version == 0) {
         deadMoney *= 1000;
      }

      GameState rootNode =
              bets == null ?
                      new GameState(nWay, gameStage, deadMoney, 0, -1) :
                      new GameState(bets, nWay, gameStage, deadMoney, firstPlayerToAct, 0, -1);

      for(var10 = 0; var10 < rootNode.nWay; ++var10) {
         rootNode.stacks[var10] = inputBuffer.read() * 1000;
      }

      PokerTreeNode.d.clear();
      PokerTreeNode var12 = new PokerTreeNode(rootNode);

      GameTree var9 = new GameTree(var12);
      DefaultTreeModel var8 = (DefaultTreeModel)var9.getModel();
      a(var8, inputBuffer, var12, inputBuffer.read());

      AnalysisPanel.b(nWay, gameStage);

      int holeCards = AnalysisPanel.isHoldem() ? 1326 : 270725;
      //int holeCards = AnalysisPanel.isHoldem() ? 630 : 270725;

      if (holeCards == 630) {
         for(var10 = 0; var10 < nWay; ++var10) {
            for(int var15 = 0; var15 < 1326; ++var15) {
               AnalysisPanel.a(var10).a[var15] = 0.0D;
            }
         }
      }


      label81:
      for(var10 = 0; var10 < nWay; ++var10) {
         for(int var15 = 0; var15 < holeCards; ++var15) {
            if ((var7 = inputBuffer.read()) < 0) {
               System.lineSeparator();
               break label81;
            }
            AnalysisPanel.a(var10).a[var15] = 0.0D;

            if (holeCards == 630) {
               int card1 = AnalysisPanel.a(var10).l[var15];
               int card2 = AnalysisPanel.a(var10).m[var15];

               int card1FD = ((card1 % 9) + 4) + (card1 / 9)*13;
               int card2FD = ((card2 % 9) + 4) + (card2 / 9)*13;

               int k = 0;
               while(k < 1326) {
                  if ( (AnalysisPanel.a(var10).l[k] == card1FD) && (AnalysisPanel.a(var10).m[k] == card2FD) ) {
                     break;
                  }

                  k++;
               }

               if (k < 1326) {
                  AnalysisPanel.a(var10).a[k] = (double)var7 / 65535.0D;
               }
            } else {
               AnalysisPanel.a(var10).a[var15] = (double)var7 / 65535.0D;
            }
         }
      }

      var8.reload();
      return var9;
   }

   private static GameState b(InputStream var0) throws Throwable {
      try {
         Throwable var17 = null;

         try {
            BufferedReader var16 = new BufferedReader(new InputStreamReader(var0, StandardCharsets.UTF_16));

            GameState var10000;
            try {
               var10000 = readNodeFromFile(var16);
            } finally {
               var16.close();
            }

            return var10000;
         } catch (Throwable var12) {
            if (var17 == null) {
               var17 = var12;
            } else if (var17 != var12) {
               var17.addSuppressed(var12);
            }

            throw var17;
         }
      } catch (Exception var13) {
         Exception var1 = var13;
         File var14 = new File("test2.log");

         try {
            PrintStream var15 = new PrintStream(var14);
            var1.printStackTrace(var15);
         } catch (FileNotFoundException var10) {
         }

         return null;
      }
   }

   public static GameState b(File var0) throws Throwable {
      try {
         Throwable var1 = null;

         try {
            BufferedReader var11 = Files.newBufferedReader(var0.toPath());

            GameState var10000;
            try {
               var10000 = readNodeFromFile(var11);
            } finally {
               if (var11 != null) {
                  var11.close();
               }

            }

            return var10000;
         } catch (Throwable var9) {
            if (var1 == null) {
               var1 = var9;
            } else if (var1 != var9) {
               var1.addSuppressed(var9);
            }

            throw var1;
         }
      } catch (Exception var10) {
         return null;
      }
   }

   public static GameTree a(InputStream var0) throws Throwable {
      try {
         Throwable var1 = null;

         try {
            BufferedReader var11 = new BufferedReader(new InputStreamReader(var0, StandardCharsets.UTF_16));

            GameTree var10000;
            try {
               var10000 = readTreeFromFile(var11);
            } finally {
               var11.close();
            }

            return var10000;
         } catch (Throwable var9) {
            if (var1 == null) {
               var1 = var9;
            } else if (var1 != var9) {
               var1.addSuppressed(var9);
            }

            throw var1;
         }
      } catch (Exception var10) {
         return null;
      }
   }

   public static GameTree c(File var0) throws Throwable {
      try {
         Throwable var1 = null;

         try {
            BufferedReader var11 = new BufferedReader(new InputStreamReader(new FileInputStream(var0), StandardCharsets.UTF_8));

            GameTree var10000;
            try {
               var10000 = readTreeFromFile(var11);
            } finally {
               var11.close();
            }

            return var10000;
         } catch (Throwable var9) {
            if (var1 == null) {
               var1 = var9;
            } else if (var1 != var9) {
               var1.addSuppressed(var9);
            }

            throw var1;
         }
      } catch (Exception var10) {
         var10.printStackTrace();
         return null;
      }
   }

   private static void a(DefaultTreeModel var0, BufferedReader var1, PokerTreeNode var2, int var3) throws IOException {
      var2.c();

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var1.read();
         PokerTreeNode var6 = new PokerTreeNode(var2, var5, false);
         a(var0, var1, var6, var1.read());
         int[] var7;
         (var7 = new int[var2.answers.length + 1])[0] = var5;

         for(var5 = 0; var5 < var2.answers.length; ++var5) {
            var7[var5 + 1] = var2.answers[var5];
         }

         var2.answers = var7;
         var0.insertNodeInto(var6, var2, var4);
      }

   }

   static void createAdvancedTree(DefaultTreeModel treeModel, PokerTreeNode tree, AdvTreeSituation state) {
/*
	betType - 40000
		= % from POT
	betType - 11
		= X chips
	0	= FOLD
	1	= CHECK / CALL
	2	= POT
	3	= ALLIN
	4	= 1/2 POT
	5	= MIN RAISE
	6	= BET
	7	= 1/4 POT
	8	= 2 POT
	9	= 3/4 POT


	0 - FOLD
	1 - CHECK/CALL
	3 - ALLIN
	11+X - X chips

	[1..39989]	chips
*/
      GameState treeSettings = tree.c;

      if (treeSettings.gameStage == 4) {
         return;
      }

      if (treeSettings.parentNode != null) {
         if (treeSettings.gameStage > treeSettings.parentNode.gameStage) {
            state.getPostflopType();
         }
      }

      List<Double> children = AdvTreeSettings.getPossibleAnswers(treeSettings, state);
      int childrenCount = children.size();

      for(int var4 = 0; var4 < childrenCount; ++var4) {
         double var5 = children.get(var4);
         int var5Int = (int)var5;

         PokerTreeNode var6 = new PokerTreeNode(tree, var5, false);
         createAdvancedTree(treeModel, var6, new AdvTreeSituation(state, treeSettings, var5));
         int[] var7;
         (var7 = new int[tree.answers.length + 1])[0] = var5Int;

         for(int i = 0; i < tree.answers.length; ++i) {
            var7[i + 1] = tree.answers[i];
         }

         tree.answers = var7;
         treeModel.insertNodeInto(var6, tree, var4);
      }
   }

   // $FF: synthetic method
   static void a(GameTree var0, PokerTreeNode var1, FilterSettings var2) {
      var0.a(var1, var2);
   }

   // $FF: synthetic method
   static void a(GameTree var0, JMenu var1, File var2) {
      var0.a(var1, var2);
   }
}

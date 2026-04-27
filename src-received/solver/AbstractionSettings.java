package solver;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public final class AbstractionSettings extends JPanel {
   private JPanel b = new JPanel();
   private JPanel c = new JPanel();
   private JPanel d = new JPanel();
   private TitledBorder e;
   static AbstractionSettings a = new AbstractionSettings();
   private static volatile long[] f = new long[4];
   private static String g;

   public AbstractionSettings() {
      this.setLayout(new BoxLayout(this, 1));
      this.setBackground(ThemeManager.BACKGROUND_DARK);
      this.e = BorderFactory.createTitledBorder("Abstraction");
      this.e.setTitleColor(ThemeManager.TEXT_PRIMARY);
      this.setBorder(this.e);
   }   

   public final void a() {
      this.removeAll();
      if (AnalysisPanel.getTreeStage() > 0) {
         this.m();
      } else {
         this.l();
      }

      MainTabbedPane.a(this.isEnabled());
   }

   public static String a(long var0) {
      return var0 < 200000L ? "0GB" : FlopNE.a((double)var0 / 1.073741824E9D, 3) + "GB";
   }

   public static String b(long var0) {
      return (double)var0 < 1048576.0D ? "0MB" : FlopNE.a((double)var0 / 1048576.0D, 3) + "MB";
   }

   private static long c() {
      return f != null ? f[3] : TextureAbstractionLookup.b(FlopNE.riverTextureType) * (long)FlopNE.riverBuckets;
   }

   private static long d() {
      return f != null ? f[2] : TextureAbstractionLookup.a(FlopNE.turnTextureType) * (long)FlopNE.turnBuckets << 2;
   }

   private static long e() {
      if (f != null) {
         return f[1];
      } else if (FlopNE.flopBuckets == 0) {
         return AnalysisPanel.isHoldem() ? 881374L : 79791556L;
      } else {
    	  if (AnalysisPanel.gameType == 0) {
    		  return 1755L * (long)FlopNE.flopBuckets << 2;
    	  } else {
    		  return 573L * (long)FlopNE.flopBuckets << 2;
    	  }
      }
   }

   private static boolean a(int var0) {
      return FlopNE.avgstreets + AnalysisPanel.getTreeStage() > var0;
   }

   private static boolean b(int var0) {
      return FlopNE.evstreets + AnalysisPanel.getTreeStage() > var0;
   }

   public final void b() {
      this.a(false);
   }

   private void a(boolean var1) {
      Thread var2;
      (var2 = new Thread(() -> {
         long var02 = this.f();
         long var5 = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax();
         if (var02 > var5) {
            this.e.setTitleColor(Color.red);
         } else {
            this.e.setTitleColor(Color.black);
         }

         g = var02 < 0L ? "Specify board cards." : a(var02) + " / " + a(var5);
         EventQueue.invokeLater(() -> {
            this.e.setTitle("Abstraction (" + g + ")");
            this.j().repaint();
            this.i().repaint();
            this.k().repaint();
            this.revalidate();
            this.repaint();
         });
      })).start();
      if (var1) {
         try {
            var2.join();
            return;
         } catch (InterruptedException var3) {
            var3.printStackTrace();
         }
      }

   }

   private static long b(boolean var0) {
      GameState var1 = AnalysisPanel.e();
      byte var2 = 3;
      if (var1.gameStage == 2) {
         var2 = 4;
      } else if (var1.gameStage == 3) {
         var2 = 5;
      }

      card[] var15;
      if ((var15 = card.b(MainTabbedPane.k())).length < var2) {
         return -1L;
      } else {
         final card[] var3 = new card[var2];

         for(int var4 = 0; var4 < var2; ++var4) {
            var3[var4] = var15[var4];
         }

         final GameState var19 = AnalysisPanel.e();
         final long[][] var16 = new long[var19.nWay][4];
         ArrayList var17 = new ArrayList();
         if (f == null) {
            f = new long[4];
         }

         Arrays.fill(f, 0L);

         int var8 = 0;
         //for(int var8 = 0; var8 < var19.b; ++var8) {
         while (var8 < var19.nWay){
            int n3 = var8++; 
            Thread var5;
            var5 = new Thread(() -> AbstractionSettings.a(var0, n3, var19, var3, (long[][])var16));
            var5.start();
            /*(var5 = new Thread(() -> {
               try {
                  int tmp = var8; 
                  int[] var70;
                  if (var0) {
                     var70 = FlopNE.a(GameSettings.d, (OmahaHandRange)AnalysisPanel.a(var8), var19.h, var3, GameSettings.c);
                  } else {
                     var70 = FlopNE.a(AnalysisPanel.a(var8).d(), var19.h, var3, new collections.LongIntHashMap());
                  }

                  var16[var8][1] = (long)var70[0];
                  var16[var8][2] = (long)var70[1];
                  var16[var8][3] = (long)var70[2];
                  synchronized(f) {
                     long[] var10000 = f;
                     var10000[1] += (long)(var70[0] / var19.b);
                     var10000 = f;
                     var10000[2] += (long)(var70[1] / var19.b);
                     var10000 = f;
                     var10000[3] += (long)(var70[2] / var19.b);
                  }
               } catch (InterruptedException var6) {
               }
            })).start();*/
            var17.add(var5);
         }

         Iterator var9 = var17.iterator();

         while(var9.hasNext()) {
            Thread var21 = (Thread)var9.next();

            try {
               var21.join();
            } catch (InterruptedException var14) {
               var14.printStackTrace();
            }
         }

         long var22 = 0L;

         for(int var20 = 0; var20 < var19.nWay; ++var20) {
            for(int var18 = 1; var18 < 4; ++var18) {
               int var12 = b(var18) ? 2 : 0;
               var22 += AnalysisPanel.a(var18, var20, var12) * var16[var20][var18];
            }
         }

         long var23 = var22 << 3;
         if (var0) {
            var23 = (var23 += 23762752L) + 157286400L * (long)var19.nWay;
         }

         return var23 + 1205862400L * (long)var19.nWay + h();
      }
   }

   private synchronized long f() {
      if (AnalysisPanel.getTreeStage() > 0) {
         return b(!AnalysisPanel.isHoldem());
      } else {
         f = null;
         long var3 = AnalysisPanel.b(b(1) ? 2 : 0) + 2L;
         long var1 = 0L + var3 * e();
         if (a(1)) {
            var1 += AnalysisPanel.b(0) * e();
         }

         long var5 = AnalysisPanel.c(b(2) ? 2 : 0) + 2L;
         long var7 = d();
         var1 += var5 * var7;
         if (a(2)) {
            var1 += AnalysisPanel.c(0) * var7;
         }

         long var9 = AnalysisPanel.d(b(3) ? 2 : 0) + 2L;
         long var11 = c();
         var1 += var9 * var11;
         if (a(3)) {
            var1 += AnalysisPanel.d(0) * var11;
         }

         long var13 = (var13 = var1 << 3) + 524288000L;
         if (AnalysisPanel.isHoldem()) {
            var13 = (var13 += 503316480L) + 262144000L;
         } else {
            var13 = (var13 += 4215275520L) + 23762752L;
         }

         return (var13 += 1048576000L) + h();
      }
   }

   private static long g() {
      int var0 = AnalysisPanel.e().nWay;
      if (!FlopNE.b()) {
         int var1 = FlopNE.icm != null ? 0 : 1;
         return (long) FilterButtonListener.a[var1][var0];
      } else {
         return 0L;
      }
   }

   private static long h() {
      int var0 = AnalysisPanel.e().nWay;
      return !FlopNE.b() ? AnalysisPanel.d() * (long)var0 * g() << 3 : 0L;
   }

   private JPanel i() {
      this.c.removeAll();
      long var1 = AnalysisPanel.c(0);
      this.c.add(new JLabel("Turn nodes: " + var1));
      long var3 = d();
      JLabel var5 = new JLabel("Buckets/node: " + NumberFormat.getNumberInstance(Locale.US).format(var3));
      this.c.add(var5);
      JLabel var6 = new JLabel("Total: " + NumberFormat.getNumberInstance(Locale.US).format(var1 * var3));
      this.c.add(var6);
      return this.c;
   }

   private JPanel j() {
      this.b.removeAll();
      long var1 = AnalysisPanel.b(0);
      long var3 = e();
      this.b.add(new JLabel("Flop nodes: " + var1));
      JLabel var5 = new JLabel("Buckets/node: " + NumberFormat.getNumberInstance(Locale.US).format(var3));
      this.b.add(var5);
      long var6 = var1 * var3;
      JLabel var8 = new JLabel("Total: " + NumberFormat.getNumberInstance(Locale.US).format(var6));
      this.b.add(var8);
      return this.b;
   }

   private JPanel k() {
      this.d.removeAll();
      long var1 = AnalysisPanel.d(0);
      this.d.add(new JLabel("River nodes: " + var1));
      long var3 = c();
      JLabel var5 = new JLabel("Buckets/node: " + NumberFormat.getNumberInstance(Locale.US).format(var3));
      this.d.add(var5);
      JLabel var6 = new JLabel("Total: " + NumberFormat.getNumberInstance(Locale.US).format(var1 * var3));
      this.d.add(var6);
      return this.d;
   }

   private void l() {
      f = null;
      JPanel var1;
      (var1 = new JPanel(new FlowLayout(0))).setBackground(ThemeManager.BACKGROUND_DARK);
      javax.swing.border.TitledBorder flopBorder = BorderFactory.createTitledBorder("Flop");
      flopBorder.setTitleColor(ThemeManager.TEXT_PRIMARY);
      var1.setBorder(flopBorder);

      JPanel var2;
      (var2 = new JPanel()).setBackground(ThemeManager.BACKGROUND_DARK);
      javax.swing.border.TitledBorder strengthBorder = BorderFactory.createTitledBorder("Strength");
      strengthBorder.setTitleColor(ThemeManager.TEXT_PRIMARY);
      var2.setBorder(strengthBorder);

      JTextField var3;
      (var3 = new JTextField(solver.HashUtil.o(new char[0]) + FlopNE.flopBuckets)).setColumns(2);
      var3.getDocument().addDocumentListener(new FlopBucketListener(this, var3));
      var1.add(this.b);
      var2.add(new JLabel("Buckets: "));
      var2.add(var3);
      var1.add(var2);

      (var2 = new JPanel()).setBackground(ThemeManager.BACKGROUND_DARK);
      javax.swing.border.TitledBorder textureBorder = BorderFactory.createTitledBorder("Texture");
      textureBorder.setTitleColor(ThemeManager.TEXT_PRIMARY);
      var2.setBorder(textureBorder);
      JRadioButton var11 = new JRadioButton("Perfect");
      var2.add(var11);
      (new ButtonGroup()).add(var11);
      var11.setSelected(true);
      var1.add(var2);

      (var2 = new JPanel(new FlowLayout(0))).setBackground(ThemeManager.BACKGROUND_DARK);
      javax.swing.border.TitledBorder turnBorder = BorderFactory.createTitledBorder("Turn");
      turnBorder.setTitleColor(ThemeManager.TEXT_PRIMARY);
      var2.setBorder(turnBorder);

      JPanel var12;
      (var12 = new JPanel()).setBackground(ThemeManager.BACKGROUND_DARK);
      javax.swing.border.TitledBorder turnStrengthBorder = BorderFactory.createTitledBorder("Strength");
      turnStrengthBorder.setTitleColor(ThemeManager.TEXT_PRIMARY);
      var12.setBorder(turnStrengthBorder);

      JTextField var4;
      (var4 = new JTextField(solver.HashUtil.u(new char[0]) + FlopNE.turnBuckets)).getDocument().addDocumentListener(new TurnBucketsListener(this, var4));
      var4.setColumns(2);
      var12.add(new JLabel("Buckets: "));
      var12.add(var4);

      JPanel var13;
      (var13 = new JPanel()).setBackground(ThemeManager.BACKGROUND_DARK);
      javax.swing.border.TitledBorder turnTextureBorder = BorderFactory.createTitledBorder("Texture");
      turnTextureBorder.setTitleColor(ThemeManager.TEXT_PRIMARY);
      var13.setBorder(turnTextureBorder);
      JRadioButton var5 = new JRadioButton("None");
      JRadioButton var6 = new JRadioButton("Small");
      JRadioButton var7 = new JRadioButton("Medium");
      JRadioButton var8 = new JRadioButton("Large");
      JRadioButton var9 = new JRadioButton("Perfect");
      var5.addItemListener((var1x) -> {
         if (var1x.getStateChange() == 1) {
            FlopNE.turnTextureType = 0;
            this.a(false);
         }

      });
      var6.addItemListener((var1x) -> {
         if (var1x.getStateChange() == 1) {
            FlopNE.turnTextureType = 3;
            this.a(false);
         }

      });
      var7.addItemListener((var1x) -> {
         if (var1x.getStateChange() == 1) {
            FlopNE.turnTextureType = 4;
            this.a(false);
         }

      });
      var8.addItemListener((var1x) -> {
         if (var1x.getStateChange() == 1) {
            FlopNE.turnTextureType = 1;
            this.a(false);
         }

      });
      var9.addItemListener((var1x) -> {
         if (var1x.getStateChange() == 1) {
            FlopNE.turnTextureType = 2;
            this.a(false);
         }

      });
      ButtonGroup var10;
      (var10 = new ButtonGroup()).add(var5);
      var10.add(var6);
      var10.add(var7);
      var10.add(var8);
      var10.add(var9);
      var13.add(var5);
      var13.add(var6);
      var13.add(var7);
      var13.add(var8);
      var13.add(var9);
      if (FlopNE.turnTextureType == 0) {
         var5.setSelected(true);
      } else if (FlopNE.turnTextureType == 1) {
         var8.setSelected(true);
      } else if (FlopNE.turnTextureType == 3) {
         var6.setSelected(true);
      } else if (FlopNE.turnTextureType == 4) {
         var7.setSelected(true);
      } else {
         var9.setSelected(true);
      }

      var2.add(this.c);
      var2.add(var12);
      var2.add(var13);

      (var12 = new JPanel(new FlowLayout(0))).setBackground(ThemeManager.BACKGROUND_DARK);
      javax.swing.border.TitledBorder riverBorder = BorderFactory.createTitledBorder("River");
      riverBorder.setTitleColor(ThemeManager.TEXT_PRIMARY);
      var12.setBorder(riverBorder);

      (var13 = new JPanel()).setBackground(ThemeManager.BACKGROUND_DARK);
      javax.swing.border.TitledBorder riverStrengthBorder = BorderFactory.createTitledBorder("Strength");
      riverStrengthBorder.setTitleColor(ThemeManager.TEXT_PRIMARY);
      var13.setBorder(riverStrengthBorder);

      JTextField var15;
      (var15 = new JTextField(solver.HashUtil.F(new char[0]) + FlopNE.riverBuckets)).getDocument().addDocumentListener(new RiverBucketsListener(this, var15));
      var15.setColumns(2);
      var13.add(new JLabel("Buckets: "));
      var13.add(var15);

      JPanel var16;
      (var16 = new JPanel()).setBackground(ThemeManager.BACKGROUND_DARK);
      javax.swing.border.TitledBorder riverTextureBorder = BorderFactory.createTitledBorder("Texture");
      riverTextureBorder.setTitleColor(ThemeManager.TEXT_PRIMARY);
      var16.setBorder(riverTextureBorder);
      (var6 = new JRadioButton("None")).addItemListener(new RiverTextureNoneListener(this));
      (var7 = new JRadioButton("Small")).addItemListener(new RiverTexturePerfectListener(this));
      (var8 = new JRadioButton("Large")).addItemListener(new RiverTextureLargeListener(this));
      ButtonGroup var17;
      (var17 = new ButtonGroup()).add(var6);
      var17.add(var7);
      var17.add(var8);
      var16.add(var6);
      var16.add(var7);
      var16.add(var8);
      if (FlopNE.riverTextureType == 0) {
         var6.setSelected(true);
      } else if (FlopNE.riverTextureType == 2) {
         var7.setSelected(true);
      } else {
         var8.setSelected(true);
      }

      var12.add(this.d);
      var12.add(var13);
      var12.add(var16);
      this.b.setLayout(new BoxLayout(this.b, 1));
      this.c.setLayout(new BoxLayout(this.c, 1));
      this.d.setLayout(new BoxLayout(this.d, 1));
      Dimension var14 = new Dimension((int)(150.0F * PokerSolverMain.c), (int)(60.0F * PokerSolverMain.c));
      this.b.setPreferredSize(var14);
      this.d.setPreferredSize(var14);
      this.c.setPreferredSize(var14);
      this.add(var1);
      this.add(var2);
      this.add(var12);
      this.a(false);
   }
   
   //Hold'em

   private void m() {
      JRadioButton var1;
      (var1 = new JRadioButton("Small")).addActionListener((var0) -> {
         GameSettings.c = 0;
      });
      JRadioButton var2;
      (var2 = new JRadioButton("Large")).addActionListener((var0) -> {
         GameSettings.c = 1;
      });
      JPanel var3;
      if (!AnalysisPanel.isHoldem()) {
         var3 = new JPanel();
         JButton var4;
         (var4 = new JButton("Estimate RAM")).addActionListener((var2x) -> {
            var4.setIcon(MainTabbedPane.d);
            (new Thread(() -> {
               this.a(true);
               var4.setIcon((Icon)null);
            })).start();
         });
         var3.add(var4);
         String var5 = "Custom abstraction";
         JButton var6;
         (var6 = new JButton(solver.HashUtil.Q(new char[0]))).setText(GameSettings.d == null ? var5 : GameSettings.d.d());
         var6.addActionListener((var2x) -> {
            JFileChooser var04;
            (var04 = new JFileChooser()).setCurrentDirectory(new util.AppFile("Views"));
            var04.setDialogTitle("Select view file");
            var04.setFileFilter(new FileNameExtensionFilter("Text files", new String[]{"txt"}));
            if (var04.showOpenDialog(MainTabbedPane.k) == 0) {
               File var5x;
               if ((var5x = var04.getSelectedFile()) != null && var5x.exists()) {
                  GameSettings.d = new ViewSettingsManager(var5x);
               } else {
                  GameSettings.d = null;
               }
            } else {
               GameSettings.d = null;
            }

            if (GameSettings.d == null) {
               var6.setText(var5);
            } else {
               var6.setText(GameSettings.d.d());
            }
         });
         if (AnalysisPanel.gameType == 1) {
            var3.add(var6);
         }

         this.add(var3);
      }

      if (f == null) {
         f = new long[4];
         this.e.setTitle("Abstraction");
      }

      (var3 = new JPanel(new FlowLayout(0))).setMaximumSize(new Dimension(1231231, 120));
      var3.setBackground(ThemeManager.BACKGROUND_DARK);
      javax.swing.border.TitledBorder flopBorder2 = BorderFactory.createTitledBorder("Flop");
      flopBorder2.setTitleColor(ThemeManager.TEXT_PRIMARY);
      var3.setBorder(flopBorder2);
      var3.add(this.j());
      JPanel var8;
      (var8 = new JPanel(new FlowLayout(0))).setMaximumSize(new Dimension(1231231, 120));
      var8.setBackground(ThemeManager.BACKGROUND_DARK);
      javax.swing.border.TitledBorder turnBorder2 = BorderFactory.createTitledBorder("Turn");
      turnBorder2.setTitleColor(ThemeManager.TEXT_PRIMARY);
      var8.setBorder(turnBorder2);
      var8.add(this.i());
      JPanel var9;
      (var9 = new JPanel(new FlowLayout(0))).setMaximumSize(new Dimension(1231231, 120));
      var9.setBackground(ThemeManager.BACKGROUND_DARK);
      javax.swing.border.TitledBorder riverBorder2 = BorderFactory.createTitledBorder("River");
      riverBorder2.setTitleColor(ThemeManager.TEXT_PRIMARY);
      var9.setBorder(riverBorder2);
      var9.add(this.k());
      JPanel var10 = new JPanel();
      var1.setToolTipText("Only suits with 3 or more cards on the river are considered.");
      var2.setToolTipText("Suits which had at least 2 cards by the turn are considered.");
      if (GameSettings.c == 0) {
         var1.setSelected(true);
      } else {
         var2.setSelected(true);
      }

      ButtonGroup var7;
      (var7 = new ButtonGroup()).add(var1);
      var7.add(var2);
      var10.add(var1);
      var10.add(var2);
      if (!AnalysisPanel.isHoldem()) {
         var9.add(var10);
      }

      this.add(var3);
      this.add(var8);
      this.add(var9);
      if ( (AnalysisPanel.gameType == 0) || (AnalysisPanel.gameType == 3) ) {
         this.a(false);
      }

      this.revalidate();
      this.repaint();
   }
   
   private static /* synthetic */ void a(boolean PokerTreeNode, int n, GameState cc2, card[] arrh, long[][] arrl) {
        try {
            int[] arrn = PokerTreeNode ? FlopNE.a((ViewSettingsManager)GameSettings.d, (OmahaHandRange)((OmahaHandRange)AnalysisPanel.a((int)n)), (int)cc2.gameStage, (card[])arrh, (int)GameSettings.c) :
                    FlopNE.a((card[][])AnalysisPanel.a((int)n).d(), (int)cc2.gameStage, (card[])arrh, (collections.LongIntHashMap)new collections.LongIntHashMap());
            arrl[n][1] = arrn[0];
            arrl[n][2] = arrn[1];
            arrl[n][3] = arrn[2];
            long[] arrl2 = f;
            synchronized (arrl2) {
                long[] arrl3 = f;
                arrl3[1] = arrl3[1] + (long)(arrn[0] / cc2.nWay);
                long[] arrl4 = f;
                arrl4[2] = arrl4[2] + (long)(arrn[1] / cc2.nWay);
                long[] arrl5 = f;
                arrl5[3] = arrl5[3] + (long)(arrn[2] / cc2.nWay);
                return;
            }
        }
        catch (InterruptedException interruptedException) {
            return;
        }
    }
   
}

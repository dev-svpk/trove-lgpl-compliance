package solver;

import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

final class GameStateRefreshTask implements Runnable {
   private JTable b;
   boolean a = false;
   private static GameStateRefreshTask c;
   private static GameState d;

   public GameStateRefreshTask(JTable var1) {
      this.b = var1;
      if (c != null) {
         c.a = true;
      }

      c = this;
   }

   public final void run() {
      if (FlopNE.G != null) {
         GameState var2 = MainTabbedPane.e();
         if (d != null && !d.a(var2)) {
            SwingUtilities.invokeLater(() -> {
               this.b.setModel(new DynamicTableModel());
            });
         }

         d = var2;
         double bb = Arrays.stream(FlopNE.G.bets).max().getAsInt();
         if ((var2.gameStage != 1 || MainTabbedPane.enteredBoard.size() >= 3) && (var2.gameStage != 2 || MainTabbedPane.enteredBoard.size() >= 4) && (var2.gameStage != 3 || MainTabbedPane.enteredBoard.size() >= 5)) {
            BucketGenerator var3 = FlopNE.a(this, var2, MainTabbedPane.enteredBoard);
            if (!this.a) {
               int[] var4 = FlopNE.a(var2);
               ArrayList var5 = new ArrayList();
               int var6 = 0;
               if (var3 != null) {
                  int[] var9 = var4;
                  int var8 = var4.length;

                  for(int var7 = 0; var7 < var8; ++var7) {
                     int var11 = var9[var7];

                     var5.add(solver.HashUtil.K(new char[0]) + solver.BetType.getBetCaption(var2, var11) + " (" + var3.a[var6] + ")");

                     ++var6;
                  }

                  for(var6 = 0; var6 < var3.b.length; ++var6) {
                     if (var3.b[var6].length == 0) {
                        var3.b[var6] = new String[]{solver.HashUtil.L(new char[0])};
                     }
                  }

                  String[] var12 = (String[])var5.toArray(new String[0]);
                  DynamicTableModel var14 = new DynamicTableModel();
                  Integer[] var13;
                  int var16 = (var13 = EquitySortComparator.a(FlopNE.a(var2))).length;

                  for(int var15 = 0; var15 < var16; ++var15) {
                     var8 = var13[var15];
                     var6 = EquitySortComparator.c(var2, var8);
                     var14.a(var12[var6], var3.b[var6]);
                  }

                  SwingUtilities.invokeLater(() -> {
                     this.b.setModel(var14);
                  });
               }
            }
         }
      }

   }
}

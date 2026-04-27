package solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public final class PokerTreeNode extends DefaultMutableTreeNode {
   static final String[] a = new String[]{"PREFLOP", "FLOP", "TURN", "RIVER", "SHOWDOWN"};
   private static final long serialVersionUID = -3926025528308114435L;
   int nodeType = -1;
   private PokerTreeNode parentNode;
   GameState c;
   static HashMap d = new HashMap();
   static int[] e = new int[]{41, 51, 61, 71, 90, 90, 90, 90, 35, 45, 55, 65, 90, 90, 90, 90};
   static int[] f = new int[]{70, 70, 80, 100, 60, 60, 70, 100, 70, 70, 80, 100, 70, 70, 80, 100};
   static int[] g = new int[]{80, 80, 80, 100, 75, 80, 90, 100, 90, 90, 90, 100, 75, 80, 90, 100};
   static int[] h = new int[]{1300, 800, 700, 700, 0, 0, 0, 0, 2600, 1300, 1100, 900, 0, 0, 0, 0};
   int[] answers = new int[0];
   private int l = -1;
   public static String j = "Default";
   private static FilterSettings m;

   public PokerTreeNode(GameState var1) {
      super("ROOT");
      this.c = var1;
      this.c();
   }

   public static final void a(String var0, ArrayList var1) {
      j = var0;
      h = (int[])((int[])var1.get(0)).clone();
      e = (int[])((int[])var1.get(1)).clone();
      f = (int[])((int[])var1.get(2)).clone();
      g = (int[])((int[])var1.get(3)).clone();
   }

   public PokerTreeNode getParentNode(){
      return this.parentNode;
   }
   public static final ArrayList a() {
      ArrayList var0;
      ArrayList var10000 = var0 = new ArrayList();
      int[] var10001 = h;
      var10000.add(Arrays.copyOf(var10001, var10001.length));
      var10001 = e;
      var0.add(Arrays.copyOf(var10001, var10001.length));
      var10001 = f;
      var0.add(Arrays.copyOf(var10001, var10001.length));
      var10001 = g;
      var0.add(Arrays.copyOf(var10001, var10001.length));
      return var0;
   }

   public PokerTreeNode(PokerTreeNode var1, int var2, boolean var3) {
      super(solver.BetType.c(var2));
      
      this.c = new GameState(var1.c, var2);
      if (var2 > 80000){
         this.c.noAnte = true;
         int prevPlayer = this.c.parentNode.k;
         int newNodeType = (this.c.bets[this.c.k] - this.c.parentNode.getMaxBet())/1000;

         this.c.nodeType = 11 + (newNodeType);
         this.nodeType = 11 + newNodeType;
      }else{
         this.nodeType = var2;
      }

      this.parentNode = var1;
      if (var3) {
         this.d();
      }

   }
   
   public PokerTreeNode(PokerTreeNode var1, double betType, boolean var3) {	  	 
      super(solver.BetType.c(betType));
      
      int var2 = (int)betType;
      
      this.nodeType = var2;
      this.c = new GameState(var1.c, betType);
      this.parentNode = var1;
      if (var3) {
         this.d();
      }

   }

   public final boolean a(int var1) {
      if (this.parentNode != null && this.parentNode.c != null) {
         if (this.parentNode.c.gameStage >= 4 || !this.parentNode.c.d(var1)) {
            return false;
         }

         int var2 = 0;
         int[] var6;
         int var5 = (var6 = this.parentNode.answers).length;

         for(int var4 = 0; var4 < var5 && var6[var4] != this.nodeType; ++var4) {
            ++var2;
         }

         this.parentNode.answers[var2] = var1;
         this.c = new GameState(this.parentNode.c, var1);
         this.nodeType = var1;
      }

      Enumeration var7 = this.children();
      ArrayList var3 = new ArrayList();

      while(var7.hasMoreElements()) {
         PokerTreeNode var8;
         if (!(var8 = (PokerTreeNode)var7.nextElement()).b()) {
            var3.add(var8);
         }
      }

      Iterator var9 = var3.iterator();

      while(var9.hasNext()) {
         ((PokerTreeNode)var9.next()).e();
      }

      if (this.c.gameStage < 4 && this.answers.length == 0) {
         this.d();
      }

      this.c();
      return true;
   }

   private boolean f(int var1) {
      int[] var5;
      int var4 = (var5 = this.answers).length;

      for(int var3 = 0; var3 < var4; ++var3) {
         int var2;
         if ((var2 = var5[var3]) != var1 && this.c.a(var2, var1)) {
            return true;
         }
      }

      return false;
   }

   public final PokerTreeNode b(int var1) {
      Enumeration var2 = this.children();

      while(var2.hasMoreElements()) {
         PokerTreeNode var3;
         if ((var3 = (PokerTreeNode)var2.nextElement()).nodeType == var1) {
            return var3;
         }
      }

      return null;
   }

   public final boolean b() {
      if (this.parentNode != null && this.parentNode.c != null) {
         if (this.parentNode.f(this.nodeType)) {
            return false;
         }

         if (this.parentNode.c.gameStage >= 4 || !this.parentNode.c.d(this.nodeType)) {
            return false;
         }

         this.c = new GameState(this.parentNode.c, this.nodeType);
      }

      Enumeration var1 = this.children();
      ArrayList var2 = new ArrayList();

      while(var1.hasMoreElements()) {
         PokerTreeNode var3;
         if (!(var3 = (PokerTreeNode)var1.nextElement()).b()) {
            var2.add(var3);
         }
      }

      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         ((PokerTreeNode)var4.next()).e();
      }

      if (var2.size() > 0 || this.c.gameStage < 4 && this.answers.length == 0) {
         this.d();
      }

      this.c();
      return true;
   }

   public final void c() {
      synchronized(d) {
         d.put(this.c, this);
      }
   }

   public final String toString() {
      if (this.nodeType < 0) {
         return this.l >= 0 ? this.c.nWay + "-WAY, " + a[this.c.gameStage] + ", " + (AnalysisPanel.i == 0 ? "NO LIMIT" : "POT LIMIT " + this.l) : this.c.nWay + "-WAY, " + a[this.c.gameStage] + ", " + (AnalysisPanel.i == 0 ? "NO LIMIT" : "POT LIMIT");
      } else {
         return this.l >= 0 ? solver.BetType.getBetCaption(this.parentNode.c, this.nodeType) + " (" + (int)(this.c.getTotalPot() / 1000.0D) + ") " + this.l : (solver.BetType.getBetCaption(this.parentNode.c, this.nodeType)) + " (" + (int)(this.c.getTotalPot() / 1000.0D) + ")";
      }
   }

   private int a(int[] var1, int var2, int var3) {
      return var1[((var2 << 1) + var3 << 2) + this.c.gameStage];
   }

   public final void d() {
      if (this.c.gameStage < 4) {
    	  
         int var1 = this.c.a(this.c.c()) ? 0 : 1;
         
         boolean var2 = true;
         if (AnalysisPanel.i != 0) {
            var2 = this.a(3, true) != null;
         }

         if (!var2 || this.g(var1)) {
            this.a(1, false);
         }

         if (!var2 || this.h(var1)) {
            this.a(0, true);
         }

         if (AnalysisPanel.i == 0 && this.i(var1)) {
            this.a(3, true);
         }

      }
   }

   private boolean g(int var1) {
      return !this.c.a((double)this.a(f, var1, AnalysisPanel.i) / 100.0D, 1);
   }

   private boolean h(int var1) {
      return !this.c.a((double)this.a(g, var1, AnalysisPanel.i) / 100.0D, 0);
   }

   private boolean i(int var1) {
      var1 = h[(var1 << 3) + this.c.gameStage];
      GameState var3 = new GameState(this.c, var1 + 40000);
      return (new GameState(this.c, 3)).bets[this.c.firstPlayerToAct] <= var3.bets[this.c.firstPlayerToAct];
   }

   public final PokerTreeNode a(int var1, boolean var2) {
      while(true) {
         int[] var6 = this.answers;
         int var5 = var6.length;

         for(int var4 = 0; var4 < var5; ++var4) {
            int var3 = var6[var4];
            if (this.c.a(var1, var3)) {
               return null;
            }
         }

         if (!this.c.d(var1)) {
            if (var1 != 6) {
               return null;
            }

            var2 = true;
            var1 = 1;
         } else {
            if (!var2 && m != null && m.a(this.c, var1)) {
               if (var1 == 6) {
                  var2 = true;
                  var1 = 1;
                  continue;
               }

               return null;
            }

            PokerTreeNode var7 = new PokerTreeNode(this, var1, true);
            this.j(var7.nodeType);
            ((DefaultTreeModel)AnalysisPanel.g.a.getModel()).insertNodeInto(var7, this, 0);

            var7.c();
            return var7;
         }
      }
   }

   private final void j(int var1) {
      int[] var2 = new int[this.answers.length + 1];
      System.arraycopy(this.answers, 0, var2, 0, this.answers.length);
      if (var1 > 80000) {
         var1 = (this.c.bets[this.c.k] - this.c.getMaxBet()) / 1000;
      }
      var2[this.answers.length] = var1;
      this.answers = var2;
   }

   public final void e() {
      PokerTreeNode var1 = this;
      synchronized(d) {
         d.remove(var1.c);
      }

      int[] var5 = new int[this.parentNode.answers.length - 1];
      int var2 = 0;

      for(int var3 = 0; var3 < this.parentNode.answers.length; ++var3) {
         if (this.parentNode.answers[var3] != this.nodeType) {
            var5[var2] = this.parentNode.answers[var3];
            ++var2;
         }
      }

      this.parentNode.answers = var5;
      ((DefaultTreeModel)AnalysisPanel.g.a.getModel()).removeNodeFromParent(this);
   }

   public final void a(List var1) {
      ArrayList var2 = new ArrayList();
      Enumeration var3 = this.children();

      while(var3.hasMoreElements()) {
         PokerTreeNode var4 = (PokerTreeNode)var3.nextElement();
         Iterator var6 = var1.iterator();

         while(var6.hasNext()) {
            BetType var5 = (BetType)var6.next();
            if (var4.nodeType == var5.a()) {
               var2.add(var4);
            }
         }
      }

      Iterator var7 = var2.iterator();

      while(var7.hasNext()) {
         ((PokerTreeNode)var7.next()).e();
      }

      var3 = this.children();

      while(var3.hasMoreElements()) {
         ((PokerTreeNode)var3.nextElement()).a(var1);
      }

   }

   public final void a(FilterSettings var1, ArrayList var2) {
      if (this.c.gameStage < 4) {
         int var3 = this.c.a(this.c.c()) ? 0 : 1;
         double var4 = (double)this.a(e, var3, AnalysisPanel.i) / 100.0D;
         FilterSettings var12 = var1;

         gnu.trove.fa.c var6;
         label97:
         for(var6 = var2 == null ? null : new gnu.trove.fa.c(5); var12 != null; var12 = var12.l) {
            m = var12;
            int var8 = this.c.l;
            if (var12.o == null && var12.p == null) {
               String[] var9;
               ArrayList[] var10 = new ArrayList[(var9 = var12.q.a.getText().trim().split(";")).length];

               for(int var11 = 0; var11 < var10.length; ++var11) {
                  var10[var11] = BetSizingPanel.a(var9[var11]);
               }

               var12.o = var10;
               String var16;
               int var17 = (var16 = var12.q.a.getText()).lastIndexOf(59) + 1;
               var12.p = (List)((var16 = var16.substring(var17).trim()).length() > 0 ? BetSizingPanel.a(var16) : new ArrayList());
            }

            Iterator var14 = (var12.o != null && var8 < var12.o.length ? var12.o[var8] : var12.p).iterator();

            while(true) {
               BetType var7;
               do {
                  if (!var14.hasNext()) {
                     continue label97;
                  }

                  if ((var7 = (BetType)var14.next()).a() == -1) {
                     var7 = solver.BetType.a(this.c);
                  }
               } while(var7.a() != 3 && this.c.a(var4, var7));

               if (this.a(var7.a(), false) != null && var6 != null) {
                  var6.a(var7.a());
               }
            }
         }

         m = null;
         Enumeration var13 = this.children();

         while(true) {
            while(var13.hasMoreElements()) {
               PokerTreeNode var15 = (PokerTreeNode)var13.nextElement();
               if (var2 != null && var6.contains(var15.nodeType)) {
                  var15.a((FilterSettings)var1, (ArrayList)null);
                  var2.add(var15);
               } else {
                  var15.a(var1, var2);
               }
            }

            return;
         }
      }
   }

   public final int a(int var1, int var2, int var3) {
      if (this.c.gameStage > var1) {
         return 0;
      } else {
         int var4 = 0;

         PokerTreeNode var6;
         for(Enumeration var5 = this.children(); var5.hasMoreElements(); var4 += var6.a(var1, var2, var3)) {
            var6 = (PokerTreeNode)var5.nextElement();
         }

         return this.c.gameStage == var1 && this.c.firstPlayerToAct == var2 ? var4 + this.children.size() + var3 : var4;
      }
   }

   public final long c(int var1) {
      if (this.c.gameStage > 1) {
         return 0L;
      } else {
         long var2 = 0L;

         PokerTreeNode var5;
         for(Enumeration var4 = this.children(); var4.hasMoreElements(); var2 += var5.c(var1)) {
            var5 = (PokerTreeNode)var4.nextElement();
         }

         return this.c.gameStage == 1 ? var2 + (long)this.children.size() + (long)var1 : var2;
      }
   }

   public final long d(int var1) {
      if (this.c.gameStage > 2) {
         return 0L;
      } else {
         long var2 = 0L;

         PokerTreeNode var5;
         for(Enumeration var4 = this.children(); var4.hasMoreElements(); var2 += var5.d(var1)) {
            var5 = (PokerTreeNode)var4.nextElement();
         }

         return this.c.gameStage == 2 ? var2 + (long)this.answers.length + (long)var1 : var2;
      }
   }

   public final long f() {
      if (this.c.gameStage >= 4) {
         return 1L;
      } else {
         long var1 = 0L;

         PokerTreeNode var4;
         for(Enumeration var3 = this.children(); var3.hasMoreElements(); var1 += var4.f()) {
            var4 = (PokerTreeNode)var3.nextElement();
         }

         return var1;
      }
   }

   public final long e(int var1) {
      if (this.c.gameStage > 3) {
         return 0L;
      } else {
         long var2 = 0L;

         PokerTreeNode var5;
         for(Enumeration var4 = this.children(); var4.hasMoreElements(); var2 += var5.e(var1)) {
            var5 = (PokerTreeNode)var4.nextElement();
         }

         return this.c.gameStage == 3 ? var2 + (long)this.children.size() + (long)var1 : var2;
      }
   }

   public final int g() {
      if (this.c.gameStage >= 4) {
         return 0;
      } else {
         int var1 = 0;
         if (this.children != null) {
            for(Enumeration var2 = this.children(); var2.hasMoreElements(); ++var1) {
               PokerTreeNode var3 = (PokerTreeNode)var2.nextElement();
               var1 += var3.g();
            }
         }

         this.l = var1;
         return var1;
      }
   }
}

package solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public final class GameState {
   static int a;
   int nWay;
   int[] stacks;
   int[] deadMoney;

   int nodeType;
   int[] bets;
   private int n;
   int g;
   int gameStage; //0 - preflop; 1 - flop; 2 - turn; 3 - river;
   private long o;
   GameState parentNode;
   int firstPlayerToAct;
   int k;
   int l;
   int m;
   private int p;
   boolean noAnte;
   private double totalPot = -1;
   private double totalPotWithSquid = -1;
   private GameState rootNodeCache;
   boolean splitPayment;

   static {
      String[] var10000 = new String[]{"PRE", "FLOP", "TURN", "RIVER"};
      a = 100000;
   }

   public GameState(int[] blinds, int playerCount, int stage, int deadMoney, int firstPlayerToAct, int newAnte, int whoStarts) {
      this.nWay = 2;
      this.gameStage = 0;
      this.o = 274572457L;
      this.k = -1;
      this.deadMoney = new int[]{deadMoney};
      this.nWay = playerCount;
      this.bets = new int[playerCount];
      this.g = 0;
      this.n = 0;
      this.stacks = new int[playerCount];

      int tmp;

      if (a != -1) {
         a(this.stacks, a);
      }

      if ( (whoStarts >= playerCount) || (whoStarts == -1) ) {
         whoStarts = 1;
      }

      System.lineSeparator();
      if (firstPlayerToAct < 0) {
         if (stage > 0) {
            this.firstPlayerToAct = 1;
         } else if (playerCount == 2) {
            this.bets[0] = blinds[0] * 100;
            this.bets[1] = blinds[1] * 100;
            if (this.bets[1] > this.bets[0]) {
               this.firstPlayerToAct = 0;
            } else {
               this.firstPlayerToAct = 1;
            }
         } else {
            for(tmp = 0; tmp < blinds.length; ++tmp) {
               this.bets[(tmp + 1) % playerCount] = blinds[tmp] * 100;
               if (blinds[tmp] != 0) {
                  this.firstPlayerToAct = (tmp + 2) % playerCount;
               }
            }
         }
      } else {
         this.firstPlayerToAct = firstPlayerToAct;
         if (playerCount == 2) {
            this.bets[0] = blinds[0] * 100;
            this.bets[1] = blinds[1] * 100;
         } else {

            for(tmp = 0; tmp < blinds.length; ++tmp) {
               this.bets[(tmp + whoStarts) % playerCount] = blinds[tmp] * 100;
            }
         }
      }

      for (int i = 0; i < this.bets.length; i++) {
         this.bets[i] += newAnte * 100;
      }

      this.nodeType = -1;
      this.gameStage = stage;
   }

   public GameState(int var1, int var2, int var3, int newAnte, int whoStarts) {
      this.nWay = 2;
      this.gameStage = 0;
      this.o = 274572457L;
      this.k = -1;
      this.deadMoney = new int[]{var3};
      this.nWay = var1;
      this.bets = new int[var1];
      this.g = 0;
      this.n = 0;
      this.stacks = new int[var1];
      this.splitPayment = splitPayment;

      if (a != -1) {
         a(this.stacks, a);
      }

      if (whoStarts >= var1) {
         whoStarts = -1;
      }

      if (var2 == 0) {
         if (var1 > 2) {
            this.bets[2] = 2000;
            this.bets[1] = 1000;
         } else {
            this.bets[1] = 2000;
            this.bets[0] = 1000;
         }
      }

      this.firstPlayerToAct = var1 == 2 ? 0 : 3 % var1;
      if (var2 > 0) {
         this.firstPlayerToAct = 1;
      }

      for (int i = 0; i < this.bets.length; i++) {
         this.bets[i] += newAnte * 100;
      }

      this.nodeType = -1;
      this.gameStage = var2;
   }

   public GameState(GameState var1) {
      this.nWay = 2;
      this.gameStage = 0;
      this.o = 274572457L;
      this.k = -1;
      this.stacks = var1.stacks;
      this.p = var1.p;
      this.deadMoney = var1.deadMoney;
      this.nodeType = var1.nodeType;
      this.gameStage = var1.gameStage;
      this.splitPayment = var1.splitPayment;

      if (var1.parentNode != null) {
         this.parentNode = new GameState(var1.parentNode);
      }

      this.o = var1.o;
      this.bets = a(var1.bets);
      this.l = var1.l;
      this.nWay = var1.nWay;
      this.firstPlayerToAct = var1.firstPlayerToAct;
      this.g = var1.g;
      this.n = var1.n;
      this.k = var1.k;
   }

   public final boolean checkedAroundPreviously() {
      // Must have a parent node
      if (this.parentNode == null) {
         return false;
      }

      // Must be at the start of a new game stage
      if (this.gameStage <= this.parentNode.gameStage) {
         return false;
      }

      // Start from the parent node (last action of previous street)
      GameState node = this.parentNode;
      int prevGameStage = node.gameStage;

      // Traverse back through all actions in the previous game stage
      while (node != null && node.gameStage == prevGameStage) {
         // If we find any action that's not a check/call (nodeType 1) or fold (nodeType 0)
         // and not the initial node (nodeType -1), then it wasn't checked around
         if (node.nodeType != 1 && node.nodeType != 0 && node.nodeType != -1) {
            return false;
         }
         node = node.parentNode;
      }

      return true;
   }

   public GameState(GameState var1, BetType var2) {
      this(var1, var2.a());
   }

   public GameState(GameState var1, int var2) {
      this.nWay = 2;
      this.gameStage = 0;
      this.o = 274572457L;
      this.k = -1;
      this.deadMoney = var1.deadMoney;
      this.splitPayment = var1.splitPayment;
      this.p = var1.p;
      this.stacks = var1.stacks;
      this.nWay = var1.nWay;
      this.parentNode = var1;
      this.o = var1.o;
      this.k = var1.firstPlayerToAct;
      this.gameStage = var1.gameStage;
      this.l = var1.l;
      this.firstPlayerToAct = var1.firstPlayerToAct;
      this.bets = a(var1.bets);
      this.g = var1.g;
      this.n = var1.n;
      this.k = this.firstPlayerToAct;
      int var3 = this.firstPlayerToAct;
      this.n = solver.Equity.a(this.n, var3);
      this.nodeType = var2;
      this.o = this.o * 27001L + 1L + (long)var2 ^ 120007L * (long)(var2 + 1);
      int[] var10000;
      int var4;
      switch(var2) {
         case 0:
            this.g = solver.Equity.a(this.g, var3);
            if (!this.l() && !this.s()) {
               if (this.u()) {
                  this.q();
                  ++this.gameStage;
                  this.n = 0;
                  this.l = 0;
               } else {
                  this.p();
               }
            } else {
               this.gameStage = 4;
            }
            break;
         case 1:
            this.bets[var3] = Math.min(this.stacks[var3], this.getMaxBet());
            if (this.s()) {
               this.gameStage = 4;
            } else if (this.u()) {
               this.q();
               ++this.gameStage;
               this.n = 0;
               this.l = 0;
            } else {
               this.p();
            }
            break;
         case 2:
            this.bets[var3] = this.getMaxBet();
            var10000 = this.bets;
            var10000[var3] = (int)((double)var10000[var3] + this.getTotalPot());
            this.p();
            break;
         case 3:
            this.bets[var3] = this.stacks[var3];
            if (this.s()) {
               this.gameStage = 4;
            } else {
               this.p();
            }
            break;
         case 4:
            this.bets[var3] = this.getMaxBet();
            var10000 = this.bets;
            var10000[var3] = (int)((double)var10000[var3] + this.getTotalPot() / 2.0D);
            this.p();
            break;
         case 5:
            if (this.nodeType != 1 && this.nodeType != -1) {
               var4 = this.getMaxBet() - this.bets[var3];
               if (this.gameStage == 0) {
                  var4 = Math.max(var4, this.n());
               }

               this.bets[var3] = this.getMaxBet();
               var10000 = this.bets;
               var10000[var3] += var4;
            } else {
               this.bets[var3] = this.getMaxBet();
               var10000 = this.bets;
               var10000[var3] += this.n();
            }

            this.p();
            break;
         case 6:
            this.bets[var3] = this.getMaxBet();
            if (this.gameStage <= 1) {
               var10000 = this.bets;
               var10000[var3] += this.n();
            } else {
               var10000 = this.bets;
               var10000[var3] += this.n() << 1;
            }

            this.p();
            break;
         case 7:
            this.bets[var3] = this.getMaxBet();
            var10000 = this.bets;
            var10000[var3] = (int)((double)var10000[var3] + this.getTotalPot() / 4.0D);
            this.p();
            break;
         case 8:
            this.bets[var3] = this.getMaxBet();
            var10000 = this.bets;
            var10000[var3] = (int)((double)var10000[var3] + 2.0D * this.getTotalPot());
            this.p();
            break;
         case 9:
            this.bets[var3] = this.getMaxBet();
            var10000 = this.bets;
            var10000[var3] = (int)((double)var10000[var3] + 3.0D * this.getTotalPot() / 4.0D);
            this.p();
            break;
         case 10:
            if (this.nodeType != 1 && this.nodeType != -1) {
               var4 = this.getMaxBet() - this.bets[var3];
               if (this.gameStage == 0) {
                  var4 = Math.max(var4, this.n());
               }

               this.bets[var3] = this.getMaxBet();
               var10000 = this.bets;
               var10000[var3] += var4;
            } else {
               this.bets[var3] = this.getMaxBet();
               var10000 = this.bets;
               var10000[var3] += this.n();
            }

            this.p();
            break;
         default:
            if (var2 > 80000) {
               var4 = var2 - 80000;
               this.bets[var3] = this.getMaxBet();
               var10000 = this.bets;
               var10000[var3] = (int)((double)var10000[var3] + this.getTotalPotWithSquid() * (double)var4 / 100.0D);
               this.p();
            } else if (var2 > 40000) {
               var4 = var2 - 40000;
               this.bets[var3] = this.getMaxBet();
               var10000 = this.bets;
               var10000[var3] = (int)((double)var10000[var3] + this.getTotalPot() * (double)var4 / 100.0D);
               this.p();
            } else {
               var4 = var2 - solver.BetType.a;
               this.bets[var3] = this.getMaxBet();
               var10000 = this.bets;
               var10000[var3] += var4 * 2000 / 2;
               this.p();
            }
      }

      var4 = this.c(var3);
      if (this.bets[var3] > var4) {
         this.bets[var3] = var4;
      }

      if (this.bets[var3] > this.stacks[var3]) {
         this.bets[var3] = this.stacks[var3];
      }

      if (var2 != 0 && var2 != 1 && var2 != 3) {
         ++this.l;
      }

   }

   public GameState(GameState var1, double betType) {
      int var2 = (int)betType;

      this.nWay = 2;
      this.gameStage = 0;
      this.o = 274572457L;
      this.k = -1;
      this.deadMoney = var1.deadMoney;
      this.splitPayment = var1.splitPayment;
      this.p = var1.p;
      this.stacks = var1.stacks;
      this.nWay = var1.nWay;
      this.parentNode = var1;
      this.o = var1.o;
      this.k = var1.firstPlayerToAct;
      this.gameStage = var1.gameStage;
      this.l = var1.l;
      this.firstPlayerToAct = var1.firstPlayerToAct;
      this.bets = a(var1.bets);
      this.g = var1.g;
      this.n = var1.n;
      this.k = this.firstPlayerToAct;
      int var3 = this.firstPlayerToAct;
      this.n = solver.Equity.a(this.n, var3);
      this.nodeType = var2;
      this.o = this.o * 27001L + 1L + (long)var2 ^ 120007L * (long)(var2 + 1);
      int[] var10000;
      int var4;
      switch(var2) {
         case 0:
            this.g = solver.Equity.a(this.g, var3);
            if (!this.l() && !this.s()) {
               if (this.u()) {
                  this.q();
                  ++this.gameStage;
                  this.n = 0;
                  this.l = 0;
               } else {
                  this.p();
               }
            } else {
               this.gameStage = 4;
            }
            break;
         case 1:
            this.bets[var3] = Math.min(this.stacks[var3], this.getMaxBet());
            if (this.s()) {
               this.gameStage = 4;
            } else if (this.u()) {
               this.q();
               ++this.gameStage;
               this.n = 0;
               this.l = 0;
            } else {
               this.p();
            }
            break;
         case 2:
            this.bets[var3] = this.getMaxBet();
            var10000 = this.bets;
            var10000[var3] = (int)((double)var10000[var3] + this.getTotalPot());
            this.p();
            break;
         case 3:
            this.bets[var3] = this.stacks[var3];
            if (this.s()) {
               this.gameStage = 4;
            } else {
               this.p();
            }
            break;
         case 4:
            this.bets[var3] = this.getMaxBet();
            var10000 = this.bets;
            var10000[var3] = (int)((double)var10000[var3] + this.getTotalPot() / 2.0D);
            this.p();
            break;
         case 5:
            if (this.nodeType != 1 && this.nodeType != -1) {
               var4 = this.getMaxBet() - this.bets[var3];
               if (this.gameStage == 0) {
                  var4 = Math.max(var4, this.n());
               }

               this.bets[var3] = this.getMaxBet();
               var10000 = this.bets;
               var10000[var3] += var4;
            } else {
               this.bets[var3] = this.getMaxBet();
               var10000 = this.bets;
               var10000[var3] += this.n();
            }

            this.p();
            break;
         case 6:
            this.bets[var3] = this.getMaxBet();
            if (this.gameStage <= 1) {
               var10000 = this.bets;
               var10000[var3] += this.n();
            } else {
               var10000 = this.bets;
               var10000[var3] += this.n() << 1;
            }

            this.p();
            break;
         case 7:
            this.bets[var3] = this.getMaxBet();
            var10000 = this.bets;
            var10000[var3] = (int)((double)var10000[var3] + this.getTotalPot() / 4.0D);
            this.p();
            break;
         case 8:
            this.bets[var3] = this.getMaxBet();
            var10000 = this.bets;
            var10000[var3] = (int)((double)var10000[var3] + 2.0D * this.getTotalPot());
            this.p();
            break;
         case 9:
            this.bets[var3] = this.getMaxBet();
            var10000 = this.bets;
            var10000[var3] = (int)((double)var10000[var3] + 3.0D * this.getTotalPot() / 4.0D);
            this.p();
            break;
         case 10:
            if (this.nodeType != 1 && this.nodeType != -1) {
               var4 = this.getMaxBet() - this.bets[var3];
               if (this.gameStage == 0) {
                  var4 = Math.max(var4, this.n());
               }

               this.bets[var3] = this.getMaxBet();
               var10000 = this.bets;
               var10000[var3] += var4;
            } else {
               this.bets[var3] = this.getMaxBet();
               var10000 = this.bets;
               var10000[var3] += this.n();
            }

            this.p();
            break;
         default:
            if (var2 > 40000) {
               var4 = var2 - 40000;
               this.bets[var3] = this.getMaxBet();
               var10000 = this.bets;
               var10000[var3] = (int)((double)var10000[var3] + this.getTotalPot() * (double)var4 / 100.0D);
               this.p();
            } else {
               double tmp = betType - solver.BetType.a;
               //var4 = var2 - solver.BetType.a;
               this.bets[var3] = this.getMaxBet();
               var10000 = this.bets;
               var10000[var3] += (int)(tmp * 2000 / 2);
               this.p();
            }
      }

      var4 = this.c(var3);
      if (this.bets[var3] > var4) {
         this.bets[var3] = var4;
      }

      if (this.bets[var3] > this.stacks[var3]) {
         this.bets[var3] = this.stacks[var3];
      }

      if (var2 != 0 && var2 != 1 && var2 != 3) {
         ++this.l;
      }

   }

   public final double getTotalPot() {
      if (totalPot < 0) {
         totalPot = Arrays.stream(this.bets).sum() + this.deadMoney[0];
      }
      return totalPot;
   }

   public final double getTotalPotWithSquid() {
      if (totalPotWithSquid < 0) {
         totalPotWithSquid = Arrays.stream(this.bets).sum() + FlopNE.squidPrice * this.getMaxBet();
      }
      return totalPotWithSquid;
   }

   private double o() {
      int var1 = -1;
      int var2 = -1;
      int[] var6;
      int var5 = (var6 = this.bets).length;

      for(int var4 = 0; var4 < var5; ++var4) {
         int var3;
         if ((var3 = var6[var4]) > var1) {
            var2 = var1;
            var1 = var3;
         } else if (var3 > var2) {
            var2 = var3;
         }
      }

      if (var2 < 0) {
         System.exit(0);
      }

      return this.getTotalPot() - (double)(var1 - var2);
   }

   public final double b() {
      if (this.parentNode.gameStage == 0 && this.l() && !solver.Equity.b(GameSettings.rakeFlags, 0)) {
         return 0.0D;
      } else if (solver.Equity.b(GameSettings.rakeFlags, 1)) {
         return this.getTotalPot();
      } else if (solver.Equity.b(GameSettings.rakeFlags, 2)) {
         return this.o();
      } else {
         double var1 = (double)this.f().getMaxBet();
         double var3 = 0.0D;
         int[] var8;
         int var7 = (var8 = this.bets).length;

         for(int var6 = 0; var6 < var7; ++var6) {
            int var5;
            if ((double)(var5 = var8[var6]) > var1) {
               var3 += var1;
            } else {
               var3 += (double)var5;
            }
         }

         return var3 + (double)this.deadMoney[0];
      }
   }

   public final boolean a(int var1) {
      if (var1 == 0) {
         return false;
      } else {
         return this.firstPlayerToAct == 0 || this.firstPlayerToAct > var1;
      }
   }

   public final int c() {
      for(GameState var1 = this; var1 != null; var1 = var1.parentNode) {
         if (!var1.g() && !var1.h()) {
            return var1.k;
         }
      }

      if (this.nWay == 2) {
         return 0;
      } else {
         return 3 % this.nWay;
      }
   }

   public final int b(int var1) {
      return this.bets[var1] - this.p;
   }

   public final int d() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.nWay; ++var2) {
         var1 += Math.min(this.p, this.bets[var2]);
      }

      return var1 + this.deadMoney[0];
   }

   private static void a(int[] var0, int var1) {
      for(int var2 = 0; var2 < var0.length; ++var2) {
         var0[var2] = var1;
      }

   }

   private void p() {
      if (this.s()) {
         this.gameStage = 4;
      } else {
         this.firstPlayerToAct = (this.firstPlayerToAct + 1) % this.nWay;

         while(solver.Equity.b(this.g, this.firstPlayerToAct) || this.bets[this.firstPlayerToAct] >= this.stacks[this.firstPlayerToAct]) {
            this.p();
         }

      }
   }

   private void q() {
      for(this.firstPlayerToAct = 1; solver.Equity.b(this.g, this.firstPlayerToAct) || this.bets[this.firstPlayerToAct] >= this.stacks[this.firstPlayerToAct]; this.firstPlayerToAct = (this.firstPlayerToAct + 1) % this.nWay) {
      }

      this.p = this.getMaxBet();
   }

   public final int getMaxBet() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.bets.length; ++var2) {
         if (this.bets[var2] > var1) {
            var1 = this.bets[var2];
         }
      }

      return var1;
   }

   public final GameState f() {
      GameState varthis = this;


      while(varthis.nodeType != 1) {
         if (varthis.parentNode == null) {
            return varthis;
         }

         varthis = varthis.parentNode;
      }

      return varthis;
   }

   public final boolean g() {
      return this.nodeType == 0;
   }

   public final boolean h() {
      return this.nodeType == 1;
   }

   public final int c(int var1) {
      int var2 = 0;

      for(int var3 = 0; var3 < this.nWay; ++var3) {
         if (var3 != var1 && !solver.Equity.b(this.g, var3) && this.stacks[var3] > var2) {
            var2 = this.stacks[var3];
         }
      }

      return var2;
   }

   public final int i() {
      int var1 = this.nWay;

      for(int var2 = 0; var2 < this.nWay; ++var2) {
         if (solver.Equity.b(this.g, var2)) {
            --var1;
         }
      }

      return var1;
   }

   public final int j() {
      for(int var1 = 0; var1 < this.nWay; ++var1) {
         if (!solver.Equity.b(this.g, var1)) {
            return var1;
         }
      }

      return -1;
   }

   public final int[] k() {
      int[] var1 = new int[this.i()];
      int var2 = 0;

      for(int var3 = 0; var3 < this.nWay; ++var3) {
         if (!solver.Equity.b(this.g, var3)) {
            var1[var2++] = var3;
         }
      }

      return var1;
   }

   public final boolean d(int var1) {
      if (this.gameStage == 4) {
         return false;
      } else if (var1 == 1) {
         return true;
      } else {
         int var2 = this.getMaxBet();
         if (var1 == 0 && this.bets[this.firstPlayerToAct] < var2 && this.bets[this.firstPlayerToAct] < this.stacks[this.firstPlayerToAct]) {
            return true;
         } else if (var1 == 0) {
            return false;
         } else {
            GameState var5;
            if (!((var5 = this.r()).k < 0 ? true : (var5.parentNode.gameStage < this.gameStage ? true : var5.bets[var5.k] > this.bets[this.firstPlayerToAct]))) {
               return false;
            } else {
               GameState var4;
               int var6;
               int var7;
               GameState var8;
               int var11;
               if (var1 == 3) {
                  if (AnalysisPanel.i == 0) {
                     return this.getMaxBet() < this.stacks[this.firstPlayerToAct];
                  } else {
                     GameState var9 = new GameState(this, var1);
                     var8 = new GameState(this, solver.BetType.b);
                     if (var9.bets[this.firstPlayerToAct] <= var8.bets[this.firstPlayerToAct] && this.getMaxBet() < this.stacks[this.firstPlayerToAct]) {
                        var4 = this;
                        var11 = 0;
                        var6 = this.getMaxBet();

                        for(var7 = 0; var7 < var4.nWay; ++var7) {
                           if (!solver.Equity.b(var4.g, var7) && var4.stacks[var7] > var6) {
                              ++var11;
                           }
                        }

                        if (var11 > 1) {
                           return true;
                        }
                     }

                     return false;
                  }
               } else if (var1 == 6) {
                  return this.l < 4;
               } else {
                  int var3 = this.firstPlayerToAct;
                  var8 = new GameState(this, var1);
                  var4 = this;
                  var11 = 0;
                  var6 = 0;

                  for(var7 = 0; var7 < var4.bets.length; ++var7) {
                     if (var4.bets[var7] > var11) {
                        var6 = var11;
                        var11 = var4.bets[var7];
                     } else if (var4.bets[var7] > var6) {
                        var6 = var4.bets[var7];
                     }
                  }

                  int var10 = var11 - var6;
                  if (var8.bets[this.firstPlayerToAct] - var2 < var10) {
                     return false;
                  } else if (var8.bets[var3] >= this.stacks[var3]) {
                     return false;
                  } else {
                     return true;
                  }
               }
            }
         }
      }
   }

   private GameState e(int var1) {
      GameState varthis = this;

      while(varthis.nodeType >= 0) {
         if (varthis.parentNode.gameStage != var1) {
            return varthis;
         }

         if (varthis.nodeType != 0 && varthis.nodeType != 1) {
            return varthis;
         }

         varthis = varthis.parentNode;
      }

      return varthis;
   }

   private GameState r() {
      GameState varthis = this;


      GameState var1;
      while((var1 = varthis.e(varthis.gameStage)).parentNode != null) {
         int var2;
         if ((var2 = var1.bets[var1.parentNode.firstPlayerToAct] - var1.parentNode.getMaxBet()) <= 0) {
            return var1;
         }

         GameState var3;
         int var4;
         if ((var3 = var1.parentNode.e(varthis.gameStage)).parentNode == null) {
            var4 = varthis.n();
         } else {
            var4 = var3.bets[var3.parentNode.firstPlayerToAct] - var3.parentNode.getMaxBet();
         }

         if (var4 <= var2) {
            return var1;
         }

         varthis = var3;
      }

      return var1;
   }

   public final boolean a(double var1, int var3) {
      int var5 = (int)((double)this.stacks[this.firstPlayerToAct] * var1);
      int var2 = this.firstPlayerToAct;
      GameState var4 = new GameState(this, var3);
      if (var3 == 1 && var4.bets[var2] == var4.stacks[var2]) {
         return false;
      } else if (var4.bets[var2] >= var5) {
         return AnalysisPanel.i == 0 || !solver.BetType.b(var3) || this.d(3);
      } else {
         return false;
      }
   }

   public final boolean a(double var1, BetType var3) {
      return this.a(var1, var3.a());
   }

   private boolean s() {
      int var1 = this.nWay;
      int var2 = 0;
      int var3 = 0;
      int var4 = -1;

      for(int var5 = 0; var5 < this.nWay; ++var5) {
         if (solver.Equity.b(this.g, var5)) {
            --var1;
         } else {
            ++var2;
            if (this.bets[var5] >= this.stacks[var5]) {
               ++var3;
            } else {
               var4 = var5;
            }
         }
      }

      if (var1 <= 0) {
         return true;
      } else if (var4 == -1) {
         return true;
      } else if (var2 > 1 && var2 - var3 == 1 && this.bets[var4] >= this.getMaxBet()) {
         return true;
      } else {
         return false;
      }
   }

   private boolean t() {
      for(int var1 = 0; var1 < this.nWay; ++var1) {
         if (!solver.Equity.b(this.g, var1) && this.bets[var1] != this.stacks[var1] && !solver.Equity.b(this.n, var1)) {
            return false;
         }
      }

      return true;
   }

   private boolean u() {
      if (!this.t()) {
         return false;
      } else {
         int var1 = this.getMaxBet();

         for(int var2 = 0; var2 < this.bets.length; ++var2) {
            if (!solver.Equity.b(this.g, var2) && this.bets[var2] != this.stacks[var2] && this.bets[var2] != var1) {
               return false;
            }
         }

         return true;
      }
   }

   public final boolean l() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.nWay; ++var2) {
         if (!solver.Equity.b(this.g, var2)) {
            ++var1;
         }
      }

      if (var1 <= 1) {
         return true;
      } else {
         return false;
      }
   }

   private static int[] a(int[] var0) {
      int[] var1 = new int[var0.length];

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1[var2] = var0[var2];
      }

      return var1;
   }

   public final int m() {
      return -this.bets[this.k];
   }

   public static int[] eligiblePlayers(int[] arr) {
      int count = 0;

      // Count the number of elements less than 99999
      for (int i = 0; i < arr.length; i++) {
         if (arr[i] < 99999) {
            count++;
         }
      }

      // Create an array to store the indices
      int[] indices = new int[count];
      int index = 0;

      // Fill the array with indices of elements less than 99999
      for (int i = 0; i < arr.length; i++) {
         if (arr[i] < 99999) {
            indices[index] = i;
            index++;
         }
      }

      return indices;
   }

   public static List<Integer> findLowestIndexes(int[] arr) {
      int minValue = arr[0];
      List<Integer> indexes = new ArrayList<>();

      for (int i = 0; i < arr.length; ++i) {
         if (arr[i] < minValue) {
            minValue = arr[i];
            indexes.clear();
            indexes.add(i);
         } else if (arr[i] == minValue) {
            indexes.add(i);
         }
      }
      return indexes;
   }

   public final double[] a(int var1, double var2, double[] var4, int[] var5, int[] var6, boolean[] var7) {
      int var8;

      for(var8 = 0; var8 < var5.length; ++var8) {
         var7[var8] = false;
         var4[var8] = (double)this.bets[var8];
         if (solver.Equity.b(this.g, var8)) {
            var5[var8] = 999999;
         } else {
            var5[var8] = var6[var8];
         }
      }

      double var10 = (double)this.deadMoney[0];

      for(int var9 = this.a(var5, var7); var9 != -1; var9 = this.a(var5, var7)) {
         int var18 = b(var5, var7);
         var8 = a(var5, var7, var18);
         var7[var9] = true;
         double var13 = 0.0D;

         for(int var12 = 0; var12 < var5.length; ++var12) {
            if (!var7[var12] && var5[var12] != var18) {
               double var16 = Math.min(var4[var9], var4[var12]) / (double)var8;
               var4[var12] -= var16;
               var13 += var16;
            }
         }

         var13 += var10 / (double)var8;
         var10 -= var10 / (double)var8;
         var4[var9] += var13;
      }
//
      for(int var19 = 0; var19 < var4.length; ++var19) {
         if (this.bets[var19] > var1) {
            var4[var19] -= (double)(this.bets[var19] - var1);
            var4[var19] -= var4[var19] * var2;
            var4[var19] -= (double)var1;
         } else {
            var4[var19] -= var4[var19] * var2;
            var4[var19] -= (double)this.bets[var19];
         }
      }

      return var4;
   }

   final double[] a(double[] var1, int[] var2, int[] var3, boolean[] var4) {
      int var5;
      for(var5 = 0; var5 < var2.length; ++var5) {
         var4[var5] = false;
         var1[var5] = (double)this.bets[var5];
         if (solver.Equity.b(this.g, var5)) {
            var2[var5] = 9999;
         } else {
            var2[var5] = var3[var5];
         }
      }

      double var8 = (double)this.deadMoney[0];

      for(var5 = this.a(var2, var4); var5 != -1; var5 = this.a(var2, var4)) {
         int var6 = b(var2, var4);
         int var15 = a(var2, var4, var6);
         var4[var5] = true;
         double var10 = 0.0D;

         for(int var7 = 0; var7 < var2.length; ++var7) {
            if (!var4[var7] && var2[var7] != var6) {
               double var13 = Math.min(var1[var5], var1[var7]) / (double)var15;
               var1[var7] -= var13;
               var10 += var13;
            }
         }

         var10 += var8 / (double)var15;
         var8 -= var8 / (double)var15;
         var1[var5] += var10;
      }

      for(int var16 = 0; var16 < var1.length; ++var16) {
         var1[var16] += (double)(this.stacks[var16] - this.bets[var16]);
      }

      return var1;
   }

   public final double a(double var1, double var3, double var5) {
      if (this.parentNode.gameStage == 0 && this.l() && !solver.Equity.b(GameSettings.rakeFlags, 0)) {
         return 0.0D;
      } else {
         double var7;
         if ((var7 = var1 * var3) > var5) {
            var7 = var5;
         }

         return var7;
      }
   }

   public static double b(double var0, double var2, double var4) {
      if (var0 < 1.0E-8D) {
         return 0.0D;
      } else {
         return var0 * var2 > var4 ? var4 / var0 : var2;
      }
   }

   private int a(int[] var1, boolean[] var2) {
      int var3 = -1;

      for (int var4 = 0; var4 < var1.length; ++var4) {
         if (!var2[var4]) {
            if (var3 == -1 || var1[var4] < var1[var3] || (var1[var4] == var1[var3] && this.bets[var4] < this.bets[var3])) {
               var3 = var4;
            }
         }
      }

      return var3;
   }

   private static int b(int[] var0, boolean[] var1) {
      int var2 = 9999990;

      for(int var3 = 0; var3 < var0.length; ++var3) {
         if (!var1[var3] && var0[var3] < var2) {
            var2 = var0[var3];
         }
      }

      return var2;
   }

   private static int a(int[] var0, boolean[] var1, int var2) {
      int var3 = 0;

      for(int var4 = 0; var4 < var0.length; ++var4) {
         if (!var1[var4] && var0[var4] == var2) {
            ++var3;
         }
      }

      return var3;
   }

   public final boolean a(int var1, int var2) {
      if (var1 != 0 && var2 != 0) {
         if (var1 == var2) {
            return true;
         } else {
            GameState var3 = new GameState(this, var1);
            GameState var4 = new GameState(this, var2);
            return var3.bets[this.firstPlayerToAct] == var4.bets[this.firstPlayerToAct];
         }
      } else {
         return var1 == var2;
      }
   }

   public final boolean equals(Object var1) {
      return this.a((GameState)var1);
   }

   public final boolean a(GameState var1) {
      GameState var100 = this;

      while(var100 != var1) {
         if (var1 != null && var100.nodeType == var1.nodeType) {
            if (var100.o == var1.o) {
               if (var100.parentNode != null && var1.parentNode != null) {
                  var1 = var1.parentNode;
                  var100 = var100.parentNode;
                  continue;
               }

               if (var100.parentNode == var1.parentNode) {
                  return true;
               }

               return false;
            }

            return false;
         }

         return false;
      }

      return true;
   }

   public final int hashCode() {
      return (int)(this.o ^ this.o >>> 32);
   }

   public final void a(Stack var1) {
      GameState var100 = this;

      while(var100.parentNode != null) {
         var1.push(var100.nodeType);
         var100 = var100.parentNode;
      }

   }

   public final int n() {
      GameState rootNode = this.getRootNode();

      if (rootNode.gameStage > 0) {
         return 2000;
      } else {
         int var1 = Integer.MAX_VALUE;
         int var2 = Integer.MAX_VALUE;
         int[] var6;
         int var5 = (var6 = rootNode.bets).length;

         for (int var4 = 0; var4 < var5; ++var4) {
            int var3;
            if ((var3 = var6[var4]) != 0) {
               if (var3 < var1) {
                  var2 = var1;
                  var1 = var3;
               } else if (var3 < var2) {
                  var2 = var3;
               }
            }
         }

         return (var2 == Integer.MAX_VALUE) ? var1 : var2;
      }
   }

   public final int playersVpipedAtNode() {
      GameState rootNode = this.getRootNode();
      int[] rootBets = rootNode.bets;
      int count = 0;

      // Compare each player's current bet with their root bet
      for (int i = 0; i < this.bets.length; i++) {
         if (this.bets[i] > rootBets[i]) {
            count++;
         }
      }

      return count;
   }

   private GameState getRootNode() {
      if (rootNodeCache == null) {
         GameState node = this;
         while (node.parentNode != null) {
            node = node.parentNode;
         }
         rootNodeCache = node;
      }
      return rootNodeCache;
   }

   public int countZeros(int[] array) {
      return (int) Arrays.stream(array).filter(value -> value == 0).count();
   }
}

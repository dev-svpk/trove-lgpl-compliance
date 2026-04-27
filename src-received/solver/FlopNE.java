package solver;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TLongDoubleHashMap;
import gnu.trove.map.hash.TLongIntHashMap;
import java.io.File;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.TableRowSorter;

public final class FlopNE {
   static HandRangeReader a;
   static long version;
   private static HashMap M;
   private static card[][][] N;
   static byte[][] c;
   private static double[][] O;
   private static double[][] P;
   static card[] boardCardsArray;
   private static int Q;
   private static int R;
   static int isoLevel;
   static ViewSettingsManager f;
   static boolean g;
   static File h;
   static collections.LongIntHashMap i;
   static double[] icm;
   private static collections.LongIntHashMap[] S;
   static collections.IntIntHashMap flopTexture;
   static collections.IntIntHashMap turnTexture;
   static collections.IntIntHashMap riverTexture;
   private static collections.LongIntHashMap T;
   private static TIntIntHashMap[] U;
   private static HashMap V;
   static collections.LongIntHashMap[] n;
   static int flopBuckets;
   static int turnBuckets;
   static int riverBuckets;
   static int turnTextureType; //0 - None; 1 - Large; 2 - Perfect; 3 - Small; 4 - Medium
   static int riverTextureType; //0 - None; 1 - Large; 2 - Small;
   static boolean isShortDeck;
   static boolean t;
   static double[][][] cfrTables;
   static double[][] avg;
   static boolean[] hasEV;
   static double[] evs;
   public static int[][] cachedEVs;

   public static int[] noAdjustment;
   public static double bbAmount;
   static long[] eviters;
   static int[] z;
   static FilterButtonListener A;
   private static int[] W;
   private static int X;
   static int[] buckets;
   static long iterations;
   static boolean D;
   static long iscount;
   static long F;
   static GameState G;
   static int H;
   static int avgstreets;
   static int evstreets;
   static int K;
   private static int Y;
   private static int[] Z;
   static long L;
   private static int[] aa;
   private static int[] ab;
   private static int[] ac;
   private static card[] ad;
   private static TLongDoubleHashMap ae;
   private static TLongDoubleHashMap af;
   private static gnu.trove.fa.e ag;
   private static File ah;

   public static int[] squidMarkers;
   public static double squidPrice;
   public static int squidTotalAmount;
   public static final int MAX_MULTIPLIERS = 10;
   public static int[] multiplierQualifiers = new int[MAX_MULTIPLIERS];
   public static int[] multiplierAmounts = new int[MAX_MULTIPLIERS];
   public static int firstRoundAmount = 1;

   static {
      version = solver.MainTabbedPane.a;
      M = new HashMap();
      g = false;
      V = new HashMap();
      flopBuckets = 30;
      turnBuckets = 30;
      riverBuckets = 30;
      turnTextureType = 1;
      riverTextureType = 1;
      t = true;
      iterations = 0L;
      D = false;
      iscount = 0L;
      H = Math.min(20, Math.max(Runtime.getRuntime().availableProcessors() - 1, 2));
      avgstreets = 1;
      evstreets = 1;
      K = 5;
      Y = 0;
      L = 0L;
      aa = new int[4];
      ab = new int[9];
      ac = new int[9];
      ad = solver.card.c(9);
      ae = new TLongDoubleHashMap(1000, 0.75F, -1L, -1.0D);
      af = new TLongDoubleHashMap();
      ag = new gnu.trove.fa.e();

      squidMarkers = new int[20];
      Arrays.fill(squidMarkers, 0);
      squidPrice = 0;
      squidTotalAmount = 0;
      multiplierQualifiers = new int[MAX_MULTIPLIERS];
      multiplierAmounts = new int[MAX_MULTIPLIERS];
      Arrays.fill(multiplierQualifiers, 0);
      Arrays.fill(multiplierAmounts, 0);
   }

   private static card[] addCardToArray(card[] var0, card var1) {
      card[] var2 = new card[var0.length + 1];

      for(int var3 = 0; var3 < var0.length; ++var3) {
         var2[var3] = var0[var3];
      }

      var2[var0.length] = var1;
      return var2;
   }

   private static card[] combine2CardsWithArray(card[] var0, card[] var1) {
      card[] var2;
      (var2 = new card[var1.length + 2])[0] = var0[0];
      var2[1] = var0[1];

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2[var3 + 2] = var1[var3];
      }

      return var2;
   }

   public static int[] a(ViewSettingsManager var0, OmahaHandRange var1, int var2, card[] var3, int var4) throws InterruptedException {
      (var1 = new OmahaHandRange(var1)).a(var3);
      return a(var0 == null ? null : var0.e(), var1.e(), var1.l(), var2, new collections.LongIntHashMap(), var3, var0 == null ? var4 : 1);
   }

   public static int[] a(ViewSettingsManager var0, byte[] var1, int[] var2, int var3, collections.LongIntHashMap var4, card[] var5, int var6) throws InterruptedException {
      // Use 5-card logic if gameType == 4
      if (AnalysisPanel.is5Card()) {
         return a5c(var0, var1, var2, var3, var4, var5, var6);
      }

      int var7 = 0;
      int var8 = 0;
      int var9 = 0;
      int[] var10 = new int[9];
      int[] var11 = new int[9];
      int[] var12 = new int[4];
      HandFilterParser var13 = null;
      HashUtil var14 = null;
      if (var0 != null) {
         var13 = new HandFilterParser();
         var0.a(var13, var5);
         var14 = ViewSettingsManager.b();
      }

      int var15 = var0 == null ? 0 : var0.c(var3);

      int var16;
      for(var16 = 0; var16 < var5.length; ++var16) {
         var11[var16 + 4] = var5[var16].b();
      }

      int var19;
      for(var16 = 0; var16 < var1.length; var16 += 4) {
         var11[0] = var1[var16];
         var11[1] = var1[var16 + 1];
         var11[2] = var1[var16 + 2];
         var11[3] = var1[var16 + 3];
         long var17 = OmahaHandNormalizer.b(var11, var10, var5.length + 4, var12, var6);
         if (!var4.b(var17)) {
            if (var15 > 0) {
               var19 = var0.a(var2[var16 / 4], var3, var14);
               var4.a(var17, var19);
               var7 = Math.max(var19 + 1, var7);
            } else {
               var4.a(var17, var7++);
            }
         }
      }

      var16 = 4 - var3;
      if (K < var16) {
         var16 = K;
      }

      if (var16 >= 2) {
         int var33 = var5.length + 4;
         List var18;
         if (AnalysisPanel.gameType == 3) {
            var18 = solver.CardArrays.getRestCardsShortdeck(var5);
         } else {
            var18 = solver.CardArrays.getRestCardsFullDeck(var5);
         }
         W = new int[var18.size()];
         var19 = 0;
         int var20 = var3 + 1;
         var15 = 0;
         if (var0 != null) {
            var14 = ViewSettingsManager.b();
            var15 = var0.c(var20);
         }

         card[] var21 = (card[])Arrays.copyOf(var5, var5.length + 1);
         Iterator var22 = var18.iterator();

         card var34;
         while(var22.hasNext()) {
            var34 = (card)var22.next();
            var21[var5.length] = var34;
            W[var19++] = var8;
            var11[var33] = var34.b();
            boolean var23 = true;

            for(int var36 = 0; var36 < var1.length; var36 += 4) {
               if (var1[var36] != var11[var33] && var1[var36 + 1] != var11[var33] && var1[var36 + 2] != var11[var33] && var1[var36 + 3] != var11[var33]) {
                  var11[0] = var1[var36];
                  var11[1] = var1[var36 + 1];
                  var11[2] = var1[var36 + 2];
                  var11[3] = var1[var36 + 3];
                  long var26 = OmahaHandNormalizer.b(var11, var10, var33 + 1, var12, var6);
                  if (!var4.b(var26)) {
                     if (var15 > 0) {
                        if (var23) {
                           var0.a(var13, var21);
                           ViewSettingsManager.a(var14);
                        }

                        int var28 = var0.a(var2[var36 / 4], var20, var14);
                        var4.a(var26, var28);
                        var8 = Math.max(var28 + 1, var8);
                        var23 = false;
                     } else {
                        var4.a(var26, var8++);
                     }
                  }
               }
            }
         }

         if (var16 >= 3) {
            var21 = new card[]{var5[0], var5[1], var5[2], null, null};
            var15 = 0;
            if (var0 != null) {
               var15 = var0.c(var3 + 2);
               var14 = ViewSettingsManager.b();
            }

            if (AnalysisPanel.gameType == 3) {
               var22 = solver.CardArrays.getRestCardsShortdeck(var5).iterator();
            } else {
               var22 = solver.CardArrays.getRestCardsFullDeck(var5).iterator();
            }


            while(var22.hasNext()) {
               var34 = (card)var22.next();
               card[] var37 = addCardToArray(var5, var34);
               var11[7] = var34.b();
               var21[3] = var34;
               Iterator var38;

               if (AnalysisPanel.gameType == 3) {
                  var38 = solver.CardArrays.getRestCardsShortdeck(var37).iterator();
               } else {
                  var38 = solver.CardArrays.getRestCardsFullDeck(var37).iterator();
               }



               while(var38.hasNext()) {
                  var34 = (card)var38.next();
                  if (solver.SolverRunner.b) {
                     throw new InterruptedException();
                  }

                  var21[4] = var34;
                  var11[8] = var34.b();
                  long var27 = OmahaHandNormalizer.b(var11, var10, var12, var6);
                  boolean var35 = true;

                  for(var33 = 0; var33 < var1.length; var33 += 4) {
                     if (var1[var33] != var11[7] && var1[var33] != var11[8] && var1[var33 + 1] != var11[7] && var1[var33 + 1] != var11[8] && var1[var33 + 2] != var11[7] && var1[var33 + 2] != var11[8] && var1[var33 + 3] != var11[7] && var1[var33 + 3] != var11[8]) {
                        var11[0] = var1[var33];
                        var11[1] = var1[var33 + 1];
                        var11[2] = var1[var33 + 2];
                        var11[3] = var1[var33 + 3];
                        long var31 = OmahaHandNormalizer.a(var11, var10, var12, var27, var6);
                        if (!var4.b(var31)) {
                           if (var15 > 0) {
                              if (var35) {
                                 var0.a(var13, var21);
                                 ViewSettingsManager.a(var14);
                              }

                              var16 = var0.a(var2[var33 / 4], 3, var14);
                              var4.a(var31, var16);
                              var9 = Math.max(var16 + 1, var9);
                              var35 = false;
                           } else {
                              var4.a(var31, var9++);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      System.lineSeparator();
      var4.b();
      if (var3 == 1) {
         return new int[]{var7, var8, var9};
      } else {
         return var3 == 2 ? new int[]{0, var7, var8} : new int[]{0, 0, var7};
      }
   }

   // 5-card version of the method above
   public static int[] a5c(ViewSettingsManager var0, byte[] var1, int[] var2, int var3, collections.LongIntHashMap var4, card[] var5, int var6) throws InterruptedException {
      int var7 = 0;
      int var8 = 0;
      int var9 = 0;
      int[] var10 = new int[10];
      int[] var11 = new int[10];
      int[] var12 = new int[4];
      HandFilterParser var13 = null;
      HashUtil var14 = null;
      if (var0 != null) {
         var13 = new HandFilterParser();
         var0.a(var13, var5);
         var14 = ViewSettingsManager.b();
      }

      int var15 = var0 == null ? 0 : var0.c(var3);

      int var16;
      for(var16 = 0; var16 < var5.length; ++var16) {
         var11[var16 + 5] = var5[var16].b();
      }

      int var19;
      for(var16 = 0; var16 < var1.length; var16 += 5) {
         var11[0] = var1[var16];
         var11[1] = var1[var16 + 1];
         var11[2] = var1[var16 + 2];
         var11[3] = var1[var16 + 3];
         var11[4] = var1[var16 + 4];
         long var17 = OmahaHandNormalizer.b5c(var11, var10, var5.length + 5, var12, var6);
         if (!var4.b(var17)) {
            if (var15 > 0) {
               var19 = var0.a(var2[var16 / 5], var3, var14);
               var4.a(var17, var19);
               var7 = Math.max(var19 + 1, var7);
            } else {
               var4.a(var17, var7++);
            }
         }
      }

      var16 = 4 - var3;
      if (K < var16) {
         var16 = K;
      }

      if (var16 >= 2) {
         int var33 = var5.length + 5;
         List var18;
         var18 = solver.CardArrays.getRestCardsFullDeck(var5);
         W = new int[var18.size()];
         var19 = 0;
         int var20 = var3 + 1;
         var15 = 0;
         if (var0 != null) {
            var14 = ViewSettingsManager.b();
            var15 = var0.c(var20);
         }

         card[] var21 = (card[])Arrays.copyOf(var5, var5.length + 1);
         Iterator var22 = var18.iterator();

         card var34;
         while(var22.hasNext()) {
            var34 = (card)var22.next();
            var21[var5.length] = var34;
            W[var19++] = var8;
            var11[var33] = var34.b();
            boolean var23 = true;

            for(int var36 = 0; var36 < var1.length; var36 += 5) {
               if (var1[var36] != var11[var33] && var1[var36 + 1] != var11[var33] && var1[var36 + 2] != var11[var33] && var1[var36 + 3] != var11[var33] && var1[var36 + 4] != var11[var33]) {
                  var11[0] = var1[var36];
                  var11[1] = var1[var36 + 1];
                  var11[2] = var1[var36 + 2];
                  var11[3] = var1[var36 + 3];
                  var11[4] = var1[var36 + 4];
                  long var26 = OmahaHandNormalizer.b5c(var11, var10, var33 + 1, var12, var6);
                  if (!var4.b(var26)) {
                     if (var15 > 0) {
                        if (var23) {
                           var0.a(var13, var21);
                           ViewSettingsManager.a(var14);
                        }

                        int var28 = var0.a(var2[var36 / 5], var20, var14);
                        var4.a(var26, var28);
                        var8 = Math.max(var28 + 1, var8);
                        var23 = false;
                     } else {
                        var4.a(var26, var8++);
                     }
                  }
               }
            }
         }

         if (var16 >= 3) {
            var21 = new card[]{var5[0], var5[1], var5[2], null, null};
            var15 = 0;
            if (var0 != null) {
               var15 = var0.c(var3 + 2);
               var14 = ViewSettingsManager.b();
            }

            var22 = solver.CardArrays.getRestCardsFullDeck(var5).iterator();

            while(var22.hasNext()) {
               var34 = (card)var22.next();
               card[] var37 = addCardToArray(var5, var34);
               var11[8] = var34.b();
               var21[3] = var34;
               Iterator var38;

               var38 = solver.CardArrays.getRestCardsFullDeck(var37).iterator();

               while(var38.hasNext()) {
                  var34 = (card)var38.next();
                  if (solver.SolverRunner.b) {
                     throw new InterruptedException();
                  }

                  var21[4] = var34;
                  var11[9] = var34.b();
                  long var27 = OmahaHandNormalizer.b5c(var11, var10, var12, var6);
                  boolean var35 = true;

                  for(var33 = 0; var33 < var1.length; var33 += 5) {
                     if (var1[var33]     != var11[8] && var1[var33]     != var11[9] &&
                    	 var1[var33 + 1] != var11[8] && var1[var33 + 1] != var11[9] &&
                    	 var1[var33 + 2] != var11[8] && var1[var33 + 2] != var11[9] &&
                    	 var1[var33 + 3] != var11[8] && var1[var33 + 3] != var11[9] &&
                    	 var1[var33 + 4] != var11[8] && var1[var33 + 4] != var11[9])
                     {
                    	var11[0] = var1[var33];
                        var11[1] = var1[var33 + 1];
                        var11[2] = var1[var33 + 2];
                        var11[3] = var1[var33 + 3];
                        var11[4] = var1[var33 + 4];
                        long var31 = OmahaHandNormalizer.a5c(var11, var10, var12, var27, var6);
                        if (!var4.b(var31)) {
                           if (var15 > 0) {
                              if (var35) {
                                 var0.a(var13, var21);
                                 ViewSettingsManager.a(var14);
                              }

                              var16 = var0.a(var2[var33 / 5], 3, var14);
                              var4.a(var31, var16);
                              var9 = Math.max(var16 + 1, var9);
                              var35 = false;
                           } else {
                              var4.a(var31, var9++);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      System.lineSeparator();
      var4.b();
      if (var3 == 1) {
         return new int[]{var7, var8, var9};
      } else {
         return var3 == 2 ? new int[]{0, var7, var8} : new int[]{0, 0, var7};
      }
   }

   public static int[] a(card[][] var0, int var1, card[] var2, collections.LongIntHashMap var3) {
      int var4 = 0;
      int var5 = 0;
      int var6 = 0;
      card[] var7 = solver.card.c(7);
      card[][] var11 = var0;
      int var10 = var0.length;

      for(int var9 = 0; var9 < var10; ++var9) {
         long var12 = CardCombinations.a(combine2CardsWithArray(var11[var9], var2), var7, new int[4]);
         if (!var3.b(var12)) {
            var3.a(var12, var4);
            ++var4;
         }
      }

      int var8 = 4 - var1;
      if (K < var8) {
         var8 = K;
      }

      if (var8 >= 2) {
         int[] var22 = new int[4];
         Iterator var25;

         if (AnalysisPanel.gameType == 3) {
            var25 = solver.CardArrays.getRestCardsShortdeck(var2).iterator();
         } else {
            var25 = solver.CardArrays.getRestCardsFullDeck(var2).iterator();

         }

         card[] var16;
         while(var25.hasNext()) {
            card var23 = (card)var25.next();
            card[][] var15 = var0;
            int var14 = var0.length;

            for(int var13 = 0; var13 < var14; ++var13) {
               card[] var27 = var15[var13];
               if (!var23.cardEquals(var27[0]) && !var23.cardEquals(var27[1])) {
                  var16 = var2;
                  card[] var18;
                  (var18 = new card[var2.length + 1 + 2])[0] = var27[0];
                  var18[1] = var27[1];

                  for(int var29 = 0; var29 < var16.length; ++var29) {
                     var18[var29 + 2] = var16[var29];
                  }

                  var18[var16.length + 2] = var23;
                  long var34 = CardCombinations.a(var18, var7, var22);
                  if (!var3.b(var34)) {
                     var3.a(var34, var5);
                     ++var5;
                  }
               }
            }
         }

         card[] var24 = new card[7];

         for(int var26 = 0; var26 < var2.length; ++var26) {
            var24[var26 + 2] = var2[var26];
         }

         if (var8 >= 3) {
            Iterator var30;// = solver.CardArrays.getRestCardsFullDeck(var2).iterator();

            if (AnalysisPanel.gameType == 3) {
               var30 = solver.CardArrays.getRestCardsShortdeck(var2).iterator();
            } else {
               var30 = solver.CardArrays.getRestCardsFullDeck(var2).iterator();
            }

            while(var30.hasNext()) {
               card var28 = (card)var30.next();
               var24[5] = var28;
               Iterator var33;// = solver.CardArrays.getRestCardsFullDeck(addCardToArray(var2, var28)).iterator();
               if (AnalysisPanel.gameType == 3) {
                  var33 = solver.CardArrays.getRestCardsShortdeck(addCardToArray(var2, var28)).iterator();
               } else {
                  var33 = solver.CardArrays.getRestCardsFullDeck(addCardToArray(var2, var28)).iterator();
               }

               while(var33.hasNext()) {
                  card var32 = (card)var33.next();
                  var24[6] = var32;
                  card[][] var31 = var0;
                  var8 = var0.length;

                  for(int var17 = 0; var17 < var8; ++var17) {
                     var16 = var31[var17];
                     if (!var28.cardEquals(var16[0]) && !var28.cardEquals(var16[1]) && !var32.cardEquals(var16[0]) && !var32.cardEquals(var16[1])) {
                        var24[0] = var16[0];
                        var24[1] = var16[1];
                        long var20 = CardCombinations.a(var24, var7, var22);
                        if (!var3.b(var20)) {
                           var3.a(var20, var6);
                           ++var6;
                        }
                     }
                  }
               }
            }
         }
      }

      if (var1 == 1) {
         return new int[]{var4, var5, var6};
      } else {
         return var1 == 2 ? new int[]{0, var4, var5} : new int[]{0, 0, var4};
      }
   }

   private static void d() {
      hasEV = new boolean[G.nWay << 2];


      for(int var0 = 0; var0 < G.nWay; ++var0) {
         for(int var1 = 0; var1 < 4; ++var1) {
            if (evstreets > var1 - G.gameStage) {
               hasEV[(var0 << 2) + var1] = true;
            }
         }
      }

   }

   public static boolean a(long var0) {
      long var2 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

      /*long totalM = Runtime.getRuntime().totalMemory();
      long freeM = Runtime.getRuntime().freeMemory();
      long maxM = Runtime.getRuntime().maxMemory();

      double freeMGb = freeM / (1024L * 1024L * 1024L);*/

      return Runtime.getRuntime().maxMemory() - var2 < 1048576L * var0;
   }

   private static void e() throws InterruptedException {
      X = 0;
      Y = 0;
      d(new GameState(G));
      R = Y;
      Q = X + 1;
      UnsafeMemoryStorage.a = new boolean[X + 1];
      avg = new double[X + 1][];
      z = new int[X + 1];
      GameState[] gameScenarios = null;
      if (K >= 4) {
         gameScenarios = new GameState[R];
      }

      X = 0;
      Y = 0;
      Arrays.fill(Z = new int[4 * G.nWay], 0);
      System.lineSeparator();
      UnsafeMemoryStorage.b();
      UnsafeMemoryStorage.c(Q);
      iscount = 0L;
      UnsafeMemoryStorage.initPackedNodeInfoStorage(R, G.nWay);
      a(G, Z, gameScenarios);

      System.gc();
      Thread.sleep(10L);
      int var2;
      int var3;
      if (!g) {
         cfrTables = new double[4 * G.nWay][][];

         for(var2 = 0; var2 < 4; ++var2) {
            Thread.sleep(10L);

            for(var3 = 0; var3 < G.nWay; ++var3) {
               cfrTables[(var3 << 2) + var2] = new double[buckets[(var3 << 2) + var2]][Z[(var3 << 2) + var2]];
            }
         }

         a();
      }

      System.lineSeparator();
      if (K >= 4) {
         UnsafeMemoryStorage.d(R);

         for(int scenarioIndex = 0; scenarioIndex < gameScenarios.length; ++scenarioIndex) {
            GameState currentScenario = gameScenarios[scenarioIndex];

            // Calculate pot and rake
            double totalPot = currentScenario.getTotalPot();
            double rakeAmount = currentScenario.a(currentScenario.b(), GameSettings.rakePercent, (double)GameSettings.rakeCap);

            // Store net pot (after rake) for this scenario
            UnsafeMemoryStorage.a(scenarioIndex, totalPot - rakeAmount);

            // Get indices of all active (non-folded) players
            int[] activePlayers = currentScenario.k();

            // Store the negative bet amount of the first active player to remove from final pot
            UnsafeMemoryStorage.c(scenarioIndex, -currentScenario.bets[activePlayers[0]]);

            // NEW: Compute and store closest node ID for each player

            GameState node = currentScenario;
            while (node != null) {
               int nodeId = node.m;
//                  System.out.println("Node " + nodeId + ", gameStage=" + node.gameStage +
//                          ", nodeType=" + node.nodeType +
//                          ", parent=" + (node.parentNode != null ? node.parentNode.m : "null") +
//                          ", parentFirstToAct=" + (node.parentNode != null ? node.parentNode.firstPlayerToAct : "null"));

               if (nodeId > 0 && node.parentNode != null) {
                  int actingPlayer = node.parentNode.firstPlayerToAct;
                  //System.out.println("  -> Processing actingPlayer=" + actingPlayer);

                  if (actingPlayer >= 0 && actingPlayer < G.nWay) {
                     // CHANGE: Check for -1 instead of 0
                     if (UnsafeMemoryStorage.getPackedNodeInfo(scenarioIndex, actingPlayer) == -1) {
                        int[] parentActions = a(node.parentNode);
//                           System.out.println("  -> Parent actions: " + Arrays.toString(parentActions));

                        for (int i = 0; i < parentActions.length; i++) {
                           if (parentActions[i] == node.nodeType && node.nodeType == 0) {
//                                 int parentNodeM = node.parentNode.m;
//                                 parentNodeM = parentNodeM == 0 ? 1 : parentNodeM;
//                                 System.out.println("  -> Storing Player" + actingPlayer + " -> Parent" + parentNodeM + ", Pos" + i);
                              UnsafeMemoryStorage.setPackedNodeInfo(scenarioIndex, actingPlayer, node.m, i);
                              break;
                           }
                        }
                     } else {
                        int currentPacked = UnsafeMemoryStorage.getPackedNodeInfo(scenarioIndex, actingPlayer);
                        //System.out.println("  -> Already stored for player " + actingPlayer + " (value: " + currentPacked + ")");
                     }
                  }
               } else {
//                     System.out.println("  -> Skipped: nodeId=" + nodeId + ", gameStage=" + node.gameStage +
//                             ", hasParent=" + (node.parentNode != null));
               }
               node = node.parentNode;
            }
         }
         if (FlopNE.G.gameStage == 0){
            bbAmount = Arrays.stream(FlopNE.G.bets).max().getAsInt();
         } else{
            bbAmount = 2000;
         }

         if (t) {
            A = new FilterButtonListener(1, 1);
            A.setAdjustmentEvs();
         } else {
            (A = new FilterButtonListener(G, icm, G.nWay)).a(gameScenarios);
            A.setAdjustmentEvs();
         }
      }
//      debugDisplayCompactNodeInfo_AwRef(gameScenarios);
//      System.exit(0);
      System.lineSeparator();
   }

   // Debug display for the original UnsafeMemoryStorage implementation
   public static void debugDisplayCompactNodeInfo_AwRef(GameState[] scenarios) {
      System.out.println("=== COMPACT NODE INFO VIEW (UnsafeMemoryStorage Original) ===");

      int totalPlayers = G.nWay; // Use G.nWay for the original implementation

      // Header row
      System.out.printf("Scenario");
      for (int playerId = 0; playerId < totalPlayers; playerId++) {
         System.out.printf(" | P%d ", playerId);
      }
      System.out.println();

      // Separator
      System.out.printf("--------");
      for (int playerId = 0; playerId < totalPlayers; playerId++) {
         System.out.printf("-+----");
      }
      System.out.println();

      // Data rows
      for (int scenarioIndex = 0; scenarioIndex < Math.min(20, scenarios.length); scenarioIndex++) {
         System.out.printf("%8d", scenarioIndex);

         for (int playerId = 0; playerId < totalPlayers; playerId++) {
            // Get packed value from UnsafeMemoryStorage
            int packedValue = UnsafeMemoryStorage.getPackedNodeInfo(scenarioIndex, playerId);

            if (packedValue == -1) {
               System.out.printf(" |  - ");
            } else {
               // Extract parent node ID from packed value
               int nodeId = UnsafeMemoryStorage.getParentNodeId(packedValue);
               System.out.printf(" |%3d ", nodeId);
            }
         }
         System.out.println();
      }

      if (scenarios.length > 20) {
         System.out.printf("... (%d more scenarios not shown)%n", scenarios.length - 20);
      }
   }


   private static final int a(GameState var0, int[] var1, GameState[] var2) throws InterruptedException {
      if (X % 100 == 0 && a(400L)) {
         System.lineSeparator();
         throw new OutOfMemoryError();
      } else if (solver.SolverRunner.b) {
         throw new InterruptedException();
      } else {
         ++X;
         var0.m = X;
         int currentNodeId = X; // Store the current node ID for consistent debugging
         int var3 = X;
         int var4 = (var0.firstPlayerToAct << 2) + var0.gameStage;
         UnsafeMemoryStorage.a((long)X, var4);

         if (K >= 4 && icm == null && var0.g()) {
            UnsafeMemoryStorage.d((long)X, var0.m());
         }

         // Compute active players bitmask (for other uses)
         int[] activePlayers = var0.k();
         int activePlayersBitmask = 0;
         for(int i = 0; i < activePlayers.length; ++i) {
            activePlayersBitmask |= (1 << activePlayers[i]);
         }

         if (var0.gameStage >= 4) {
            // Terminal node: winnable players are those still active
            UnsafeMemoryStorage.setWinnablePlayers(X, activePlayersBitmask);
//            System.out.println("Branch " + currentNodeId + ": with bets " + Arrays.toString(var0.bets) + " " + formatWinnablePlayers(activePlayersBitmask));
            UnsafeMemoryStorage.a((long)X, -1);
            //payoff node
            UnsafeMemoryStorage.c((long)X, X);
            if (K >= 4) {
               UnsafeMemoryStorage.b((long)X, Y);
               var2[Y] = var0;
               ++Y;
            }
            return 1;
         } else {
            List var5 = e(var0);
            z[X] = var5.size();
            UnsafeMemoryStorage.b((long)X, var1[var4]);
            var1[var4] += z[X];
            if (hasEV[var4]) {
               var1[var4] += 2;
            }

            if (K > var0.gameStage - G.gameStage) {
               var4 = buckets[var4];
               if (var0.gameStage < avgstreets + G.gameStage && !g) {
                  avg[X] = new double[var4 * z[X]];
               }
               iscount += (long)var4;
            }

            // Debug: Show current node info
            int[] availableActions = a(var0);

            // Initialize winnable players mask
            int winnablePlayersMask = 0;

            var4 = 0;
            int var6;
            int childIndex = 0;
            for(Iterator var7 = var5.iterator(); var7.hasNext(); var4 += var6) {
               GameState childState = (GameState)var7.next();
               var6 = a(childState, var1, var2); // Recursive call processes child completely

               // Now childState.m should be set to the actual child node ID
               int childNodeId = childState.m;

               // After child is processed, get its winnable players
               int childWinnablePlayers = UnsafeMemoryStorage.getWinnablePlayers(childNodeId);

               // Add child's winnable players to current node
               winnablePlayersMask |= childWinnablePlayers;

               childIndex++;
            }

            // Store the computed winnable players mask
            UnsafeMemoryStorage.setWinnablePlayers(currentNodeId, winnablePlayersMask);

//            System.out.println("Branch " + currentNodeId + ": with bets " + Arrays.toString(var0.bets) + " " + formatWinnablePlayers(winnablePlayersMask));
            // payoff node
            UnsafeMemoryStorage.c((long)var3, var3 + var4);
            return var4 + 1;
         }
      }
   }

   public static String formatWinnablePlayers(int bitmask) {
      if (bitmask == 0) {
         return "0 = 000₂ = No players";
      }

      StringBuilder binary = new StringBuilder();
      StringBuilder players = new StringBuilder();

      // Build binary representation (reverse order for readability)
      int temp = bitmask;
      int bitCount = 0;
      while (temp > 0 || bitCount < 3) { // At least 3 bits for clarity
         binary.insert(0, (temp & 1));
         temp >>= 1;
         bitCount++;
         if (bitCount >= 8) break; // Reasonable limit
      }

      // Build player list
      boolean first = true;
      for (int i = 0; i < 8; i++) { // Check up to 8 players
         if ((bitmask & (1 << i)) != 0) {
            if (!first) players.append(", ");
            players.append("Player ").append(i);
            first = false;
         }
      }

      if (players.length() == 0) {
         players.append("No players");
      }

      return String.format("%d = %s₂ = %s", bitmask, binary.toString(), players.toString());
   }
   static final boolean a(int var0, int var1) {
      if (var1 == var0) {
         UnsafeMemoryStorage.a[var1] = true;
         return true;
      } else {
         int var2 = z[var1];
         int var3 = var1;

         for(int var4 = 0; var4 < var2; ++var4) {
            ++var1;
            if (a(var0, var1)) {
               UnsafeMemoryStorage.a[var3] = true;
               return true;
            }

            var1 = UnsafeMemoryStorage.c((long)var1);
         }

         return false;
      }
   }

   public static void a() {
      L = 0L;
      if (cfrTables != null) {
         for(int var0 = 0; var0 < cfrTables.length; ++var0) {
            if (cfrTables[var0] != null) {
               for(int var1 = 0; var1 < cfrTables[var0].length; ++var1) {
                  if (cfrTables[var0][var1] != null) {
                     L += 8L * (long)cfrTables[var0][var1].length;
                  }
               }
            }
         }
      }

   }

   private static boolean hasNonFoldAction(GameState gameState, int player) {
      if (gameState.firstPlayerToAct != player) {
         return false; // Not this player's turn
      }

      int[] actions = a(gameState); // Get available actions
      for (int action : actions) {
         if (!isFoldAction(action)) {
            return true;
         }
      }
      return false;
   }

   private static boolean isFoldAction(int action) {
      // nodeType == 0 means fold action
      return action == 0;
   }

   private static boolean isAllinAction(int action) {
      // nodeType == 0 means fold action
      return action == 3;
   }

   private static boolean isAllInPlayer(GameState gameState) {
      // Check if player has already committed all their chips

      int[] actions = a(gameState); // Get available actions
      for (int action : actions) {
         if (isAllinAction(action)) {
            return true;
         }
      }
      return false;
   }

   private static void d(GameState var0) {
      ++X;
      if (var0.gameStage >= 4) {
         ++Y;
      } else {
         Iterator var1 = e(var0).iterator();

         while(var1.hasNext()) {
            d((GameState)var1.next());
         }

      }
   }

   public static void b(int var0, int var1) {
      double[] var2;
      if ((var2 = UnsafeMemoryStorage.a(var0)) != null) {
         int var3 = UnsafeMemoryStorage.a((long)var0);
         double[] var4 = cfrTables[var3][var1];
         int var5 = z[var0];
         int var6 = UnsafeMemoryStorage.b((long)var0);
         synchronized(var4) {
            boolean var8 = false;

            int var9;
            for(var9 = 0; var9 < z[var0]; ++var9) {
               if (var2[var1 * var5 + var9] >= 0.0D) {
                  var8 = true;
                  var2[var1 * var5 + var9] = -1.0D;
               }
            }

            if (var8) {
               for(var9 = 0; var9 < var5; ++var9) {
                  if (avg[var0] != null) {
                     avg[var0][var1 * var5 + var9] = 0.0D;
                  }

                  var4[var6 + var9] = 0.0D;
               }

               if (hasEV[var3]) {
                  var4[var6 + var5] = 0.0D;
                  var4[var6 + var5 + 1] = 0.0D;
               }
            }

         }
      }
   }

   public static double a(int var0, int var1, int var2) {
      if (UnsafeMemoryStorage.a(var0) == null) {
         return -1.0D;
      } else {
         int var3 = z[var0];
         return UnsafeMemoryStorage.a(var0)[var1 * var3 + var2];
      }
   }

   public static void a(int var0, HandRange var1, card[] var2, int var3, boolean var4) {
      if (var3 < 0) {
         var4 = true;
      }

      int var5;
      card[] var6 = solver.card.c((var5 = AnalysisPanel.o()) + var2.length);

      int var7;
      for(var7 = 0; var7 < var2.length; ++var7) {
         var6[var5 + var7] = var2[var7];
      }

      for(var7 = 0; var7 < var1.a.length; ++var7) {
         double var8 = var1.a[var7];
         if (!var4 || var8 > 1.0E-7D) {
            var1.a(var7, var6);
            int var10;
            if ((var10 = a(UnsafeMemoryStorage.a((long)var0) / 4, var6, UnsafeMemoryStorage.a((long)var0) % 4)) >= 0) {
               if (var3 >= 0) {
                  a(var0, var10, var3, var1.a[var7]);
               } else {
                  b(var0, var10);
               }
            }
         }
      }

   }

   public static void a(int var0, int var1, int var2, double var3) {
      double[] var5 = cfrTables[UnsafeMemoryStorage.a((long)var0)][var1];
      int var6 = z[var0];
      int var7 = UnsafeMemoryStorage.b((long)var0);
      int var8 = UnsafeMemoryStorage.a((long)var0);
      double[] var9 = UnsafeMemoryStorage.b(var0, buckets[var8] * z[var0]);
      double[] var16 = avg[var0];
      var1 *= var6;
      synchronized(var5) {
         var9[var1 + var2] = var3;
         double var13 = 0.0D;
         boolean var17 = false;

         int var18;
         for(var18 = 0; var18 < var6; ++var18) {
            var5[var7 + var18] = 0.0D;
            if (var16 != null) {
               var16[var1 + var18] = 0.0D;
            }

            if (var9[var1 + var18] >= 0.0D) {
               var13 += var9[var1 + var18];
            } else {
               var17 = true;
            }
         }

         if (hasEV[var8]) {
            var5[var7 + var6] = 0.0D;
            var5[var7 + var6 + 1] = 0.0D;
         }

         if (var13 > 1.0D || !var17) {
            for(var18 = 0; var18 < var6; ++var18) {
               if (var9[var1 + var18] > 0.0D) {
                  var9[var1 + var18] /= var13;
               }
            }
         }

      }
   }

   public static void a(GameState var0, int var1, double var2) {
      if (var2 != 0.0D) {
         double var6 = var0.getTotalPot() + var2;
         int var8 = (Integer)V.get(var0);

         UnsafeMemoryStorage.a(var8, z[var8])[var1] = var6;
      }
   }

   public static void applyAdjustment(GameState var0, int var1, double penalty) {

         int var8 = (Integer)V.get(var0);

         UnsafeMemoryStorage.a(var8, z[var8])[var1] = penalty;

   }

   public static GameState findGameStateByBranchID(int branchID) {
      // Search through the V map to find the GameState object with this branchID
      for (Object key : V.keySet()) {
         Object value = V.get(key);
         if (value instanceof Integer && ((Integer)value).intValue() == branchID) {
            if (key instanceof GameState) {
               return (GameState)key;
            }
         }
      }
      return null; // Not found
   }

   public static void b(GameState var0, int var1, double var2) {
      if (var2 != 0.0D) {
         double var4 = var0.getTotalPot();
         double var6;
         if (icm == null) {
            var6 = var4 * var2;
         } else {
            double var10001;
            if (icm == null) {
               var10001 = 1.0D;
            } else {
               double var10 = 0.0D;
               double var12 = 0.0D;

               for(int var15 = 0; var15 < G.stacks.length; ++var15) {
                  var10 += (double)G.stacks[var15];
               }

               for(int var41 = 0; var41 < icm.length; ++var41) {
                  var12 += icm[var41];
               }

               var10001 = var12 * 1000.0D / var10;
            }

            var6 = var4 * var10001 * var2;
         }

         int var14;
         var14 = (Integer)V.get(var0);
         double[] var10000 = UnsafeMemoryStorage.a(var14, z[var14]);
         var10000[var1] += var6;
      }
   }

   public static void a(GameState var0, int var1) {
      int var2;

      if (UnsafeMemoryStorage.b(var2 = (Integer)V.get(var0)) != null) {
         UnsafeMemoryStorage.b(var2)[var1] = 0.0D;
      }
   }

   public static boolean c(int var0, int var1) {
      if (UnsafeMemoryStorage.a(var0) == null) {
         return false;
      } else {
         double[] var2 = UnsafeMemoryStorage.a(var0);
         var1 *= z[var0];

         for(int var3 = 0; var3 < z[var0]; ++var3) {
            if (var2[var1 + var3] >= 0.0D) {
               return true;
            }
         }

         return false;
      }
   }

   public static int[] a(card[] var0, card[] var1) {
      return a(var0, var1, new int[4], new int[4]);
   }

   private static int[] a(card[] var0, int[] var1) {
      int[] var3;
      int[] var2 = var3 = new int[4];
      int var4 = i.a(CardCombinations.a(var0));
      var3[0] = var4;
      if (var0.length > 2) {
         var4 = (var1[0] * flopBuckets << 2) + S[1].a(CardCombinations.c(var0, var2));
         var3[1] = var4;
         if (var0.length > 5) {
            var4 = (var1[1] * turnBuckets << 2) + S[2].a(CardCombinations.b(var0, var2));
            var3[2] = var4;
            if (var0.length > 6) {
               var4 = var1[2] * riverBuckets + S[3].a(CardCombinations.a(var0, var2));
               var3[3] = var4;
            }
         }
      }

      return var3;
   }

   private static int[] a(collections.LongIntHashMap var0, card[] var1, card[] var2, int[] var3, int[] var4) {
      long var5 = CardCombinations.a(var1, var2, var3);
      var4[3] = var0.a(var5);
      var5 /= 100L;
      var4[2] = var0.a(var5);
      var5 /= 100L;
      var4[1] = var0.a(var5);
      return var4;
   }

   private static int[] a(card[] var0, card[] var1, int[] var2, int[] var3) {
      var3[0] = i.a(CardCombinations.a(var0, var1, var2, 2));
      if (var0.length > 2) {
         card[] var4;
         int var5 = CardCombinations.getCardArrayNumValue(var4 = CardCombinations.a((card[])Arrays.copyOfRange(var0, 2, var0.length), var1), 3);
         int var6 = CardCombinations.getCardArrayNumValue(var4, 4);
         int var17 = CardCombinations.getCardArrayNumValue(var4, 5);
         long var9 = CardCombinations.a(var0, var1, var2, 5);
         var3[1] = (flopTexture.a(var5) * flopBuckets << 2) + S[1].a(var9);
         if (var0.length > 5) {
            long var12 = CardCombinations.a(var0, var1, var2, 6);
            var3[2] = (turnTexture.a(var6) * turnBuckets << 2) + S[2].a(var12);
            if (var0.length > 6) {
               long var15 = CardCombinations.a(var0, var1, var2, 7);
               var3[3] = riverTexture.a(var17) * riverBuckets + S[3].a(var15);
            }
         }
      }

      return var3;
   }

   private static int[] a(int[] var0, int[] var1, int[] var2, int[] var3, int[] var4, int var5, int var6) {
      // Use 5-card bucket calculation if gameType == 4
      if (AnalysisPanel.is5Card()) {
         return a5c_bucket(var0, var1, var2, var3, var4, var5, var6);
      }

      var0[0] = i.a(OmahaHandNormalizer.d(var2, var3, var4));
      if (var6 > 4 && var1 != null) {
         var0[1] = (var1[0] * flopBuckets << 2) + S[1].a(OmahaHandNormalizer.c(var2, var3, var4));
         if (var6 > 7) {
            var0[2] = (var1[1] * turnBuckets << 2) + S[2].a(OmahaHandNormalizer.b(var2, var3, var4));
            if (var6 > 8) {
               int var7 = T.a(OmahaHandNormalizer.c(var2, var3, var4, 9));
               var0[3] = var1[2] * riverBuckets + U[var7].get(var5);
            }
         }
      }

      return var0;
   }

   // 5-card version of bucket calculation
   private static int[] a5c_bucket(int[] var0, int[] var1, int[] var2, int[] var3, int[] var4, int var5, int var6) {
      var0[0] = i.a(OmahaHandNormalizer.d5c(var2, var3, var4));
      if (var6 > 5 && var1 != null) {
         var0[1] = (var1[0] * flopBuckets << 2) + S[1].a(OmahaHandNormalizer.c5c(var2, var3, var4));
         if (var6 > 8) {
            var0[2] = (var1[1] * turnBuckets << 2) + S[2].a(OmahaHandNormalizer.b5c(var2, var3, var4));
            if (var6 > 9) {
               int var7 = T.a(OmahaHandNormalizer.c5c(var2, var3, var4, 10));
               var0[3] = var1[2] * riverBuckets + U[var7].get(var5);
            }
         }
      }

      return var0;
   }

   public static int a(int var0, card[] var1, int var2) {
      synchronized(ad) {
         if (AnalysisPanel.isHoldem()) {
            if (G.gameStage == 0) {
               return a(var1, ad, aa, new int[4])[var2];
            } else {
               long var12 = CardCombinations.a(var1, ad, aa);
               int var9;
               if ((var9 = var2 - G.gameStage) == 0) {
                  return n[var0].a(var12);
               } else {
                  return var9 == 1 ? n[var0].a(var12 / 100L) : n[var0].a(var12 / 100L / 100L);
               }
            }
         } else if (AnalysisPanel.is5Card()) {
            // 5-card Omaha logic
            for(int var4 = 0; var4 < var1.length; ++var4) {
               ab[var4] = var1[var4].b();
            }

            if (G.gameStage == 0) {
               int[] var5 = aa;
               int[] var11 = ac;
               int[] var10 = ab;
               int[] var6;
               if (var1.length > 5) {
                  var6 = new int[3];
                  card[] var7 = CardCombinations.optimizeSuits((card[])Arrays.copyOfRange(var1, 5, var1.length));
                  var6[0] = flopTexture.a(CardCombinations.getCardArrayNumValue(var7, 3));
                  var6[1] = var1.length > 8 ? turnTexture.a(CardCombinations.getCardArrayNumValue(var7, 4)) : 0;
                  var6[2] = var1.length > 9 ? riverTexture.a(CardCombinations.getCardArrayNumValue(var7, 5)) : 0;
               } else {
                  var6 = null;
               }

               int var10000;
               if ((var0 = var2) == 0) {
                  var10000 = i.a(OmahaHandNormalizer.d5c(var10, var11, var5));
               } else if (var0 == 1) {
                  var10000 = (var6[0] * flopBuckets << 2) + S[1].a(OmahaHandNormalizer.c5c(var10, var11, var5));
               } else if (var0 == 2) {
                  var10000 = (var6[1] * turnBuckets << 2) + S[2].a(OmahaHandNormalizer.b5c(var10, var11, var5));
               } else {
                  var0 = T.a(OmahaHandNormalizer.c5c(var10, var11, var5, 10));
                  // TODO: 5-card hand evaluator needed - using 4-card evaluator as placeholder
                  int var13 = handeval.PloHandEvaluator.b5c(var10);
                  var10000 = var6[2] * riverBuckets + U[var0].get(var13);
               }

               return var10000;
            } else {
               return n[var0].a(OmahaHandNormalizer.b5c(ab, ac, var1.length, aa, isoLevel));
            }
         } else {
            // 4-card Omaha logic
            for(int var4 = 0; var4 < var1.length; ++var4) {
               ab[var4] = var1[var4].b();
            }

            if (G.gameStage == 0) {
               int[] var5 = aa;
               int[] var11 = ac;
               int[] var10 = ab;
               int[] var6;
               if (var1.length > 4) {
                  var6 = new int[3];
                  card[] var7 = CardCombinations.optimizeSuits((card[])Arrays.copyOfRange(var1, 4, var1.length));
                  var6[0] = flopTexture.a(CardCombinations.getCardArrayNumValue(var7, 3));
                  var6[1] = var1.length > 7 ? turnTexture.a(CardCombinations.getCardArrayNumValue(var7, 4)) : 0;
                  var6[2] = var1.length > 8 ? riverTexture.a(CardCombinations.getCardArrayNumValue(var7, 5)) : 0;
               } else {
                  var6 = null;
               }

               int var10000;
               if ((var0 = var2) == 0) {
                  var10000 = i.a(OmahaHandNormalizer.d(var10, var11, var5));
               } else if (var0 == 1) {
                  var10000 = (var6[0] * flopBuckets << 2) + S[1].a(OmahaHandNormalizer.c(var10, var11, var5));
               } else if (var0 == 2) {
                  var10000 = (var6[1] * turnBuckets << 2) + S[2].a(OmahaHandNormalizer.b(var10, var11, var5));
               } else {
                  var0 = T.a(OmahaHandNormalizer.c(var10, var11, var5, 9));
                  int var13 = AnalysisPanel.is5Card() ? handeval.PloHandEvaluator.b5c(var10) : handeval.PloHandEvaluator.b(var10);
                  if (AnalysisPanel.gameType == 2) {
                     var13 |= handeval.PloHandEvaluator.a(var10, var11) << 16;
                  }

                  var10000 = var6[2] * riverBuckets + U[var0].get(var13);
               }

               return var10000;
            } else {
               return n[var0].a(OmahaHandNormalizer.b(ab, ac, var1.length, aa, isoLevel));
            }
         }
      }
   }

   private static void init() throws InterruptedException, Throwable {
      d();
      if (!AnalysisPanel.isHoldem()) {
         h();
      } else {
         X = 0;
         int[] var0 = new int[5];
         i = new collections.LongIntHashMap();

         Iterator var2;

         //var2 = solver.CardArrays.h().iterator();
         if (AnalysisPanel.gameType == 0) {
            var2 = solver.CardArrays.h().iterator();
         } else {
            var2 = solver.CardArrays.hShortdeck().iterator();
         }

         while(var2.hasNext()) {
            CardCombinations var1 = (CardCombinations)var2.next();
            i.a(var1.a(), i.a());
         }

         TLongIntHashMap var8 = null;
         TLongIntHashMap var9 = null;
         TLongIntHashMap var3 = null;

         /*collections.IntIntHashMap turnTextureShortDeck = null;
         collections.IntIntHashMap var15;

         collections.IntIntHashMap riverTextureShortDeck = null;
         collections.IntIntHashMap var16;*/

         AnalysisPanel.p();
         String var4 = "";
         String sd = ""; if (AnalysisPanel.gameType == 3) { sd = "SD"; }
         if (K > 1) {
            var8 = (TLongIntHashMap)solver.Equity.a(var4 + "mmnestedflop"+sd+"." + flopBuckets + ".4");
            if (solver.SolverRunner.b) {
               throw new InterruptedException();
            }

            if (var8 == null) {
               AnalysisPanel.p();
               solver.Equity.generateFlopBuckets(flopBuckets, 4);
               var8 = (TLongIntHashMap)solver.Equity.a(var4 + "mmnestedflop"+sd+"." + flopBuckets + ".4");
            }

            Iterator var6;
            if (AnalysisPanel.gameType == 0) {
               flopTexture = new collections.IntIntHashMap(1755);
               List fullDeckFlops = solver.CardArrays.generateFullDeckFlops();
               var6 = fullDeckFlops.iterator();
            } else {
               flopTexture = new collections.IntIntHashMap(573);
               List shortDeckFlops = solver.CardArrays.generateShortDeckFlops();
               var6 = shortDeckFlops.iterator();
            }

            //flopTexture = new collections.IntIntHashMap(573);
            //List shortDeckFlops = solver.CardArrays.generateShortDeckFlops();

            while(var6.hasNext()) {
               card[] var5 = (card[])var6.next();
               flopTexture.a(CardCombinations.getCardArrayValue(var5), flopTexture.c());
            }
         }

         if (solver.SolverRunner.b) {
            throw new InterruptedException();
         } else {
            if (K > 2) {
               var9 = (TLongIntHashMap)solver.Equity.a(var4 + "mmnestedturn"+sd+"." + turnBuckets + ".4");
               if (solver.SolverRunner.b) {
                  throw new InterruptedException();
               }

               if (var9 == null) {
                  solver.Equity.generateTurnBuckets(turnBuckets, 4);
                  var9 = (TLongIntHashMap)solver.Equity.a(var4 + "mmnestedturn"+sd+"." + turnBuckets + ".4");
               }

               AnalysisPanel.p();
               turnTexture = TextureAbstractionLookup.generateTurnTexture(turnTextureType);
            }

            if (K > 3) {
               var3 = (TLongIntHashMap)solver.Equity.a(var4 + "mmnestedriver"+sd+"." + riverBuckets);
               if (solver.SolverRunner.b) {
                  throw new InterruptedException();
               }

               if (var3 == null) {
                  solver.Equity.generateRiverBuckets(riverBuckets);
                  var3 = (TLongIntHashMap)solver.Equity.a(var4 + "mmnestedriver"+sd+"." + riverBuckets);
               }

               AnalysisPanel.p();
               riverTexture = TextureAbstractionLookup.generateRiverTexture(riverTextureType);
            }

            (S = new collections.LongIntHashMap[4])[3] = solver.Equity.a((gnu.trove.map.S_ref)var3);
            S[2] = solver.Equity.a((gnu.trove.map.S_ref)var9);
            S[1] = solver.Equity.a((gnu.trove.map.S_ref)var8);
            X = 0;
            a(new GameState(G), var0);
            X = 0;
            int var11 = flopTexture == null ? 0 : flopTexture.a() + 1;
            int var12 = turnTexture == null ? 0 : turnTexture.a() + 1;
            int var7 = riverTexture == null ? 0 : riverTexture.a() + 1;
            System.lineSeparator();
            buckets = new int[G.nWay << 2];

            for(int var10 = 0; var10 < G.nWay; ++var10) {
               buckets[var10 << 2] = i.a();
               buckets[(var10 << 2) + 1] = var11 * flopBuckets << 2;
               if (flopBuckets == 0) {
                  buckets[(var10 << 2) + 1] = 881374;
               }

               buckets[(var10 << 2) + 2] = var12 * turnBuckets << 2;
               buckets[(var10 << 2) + 3] = var7 * riverBuckets;
            }

            EquityTableCache.b();
            System.lineSeparator();
            e();
            X = 0;
         }
      }
   }

   private static void g() throws InterruptedException {
      d();
      byte var0;
      int var1;
      if (AnalysisPanel.isHoldem()) {
         if (G.gameStage == 1) {
            var0 = 3;
         } else if (G.gameStage == 2) {
            var0 = 4;
         } else {
            var0 = 5;
         }

         boardCardsArray = new card[var0];

         for(var1 = 0; var1 < var0; ++var1) {
            boardCardsArray[var1] = solver.card.a((String)solver.MainTabbedPane.enteredBoard.get(var1));
         }

         N = new card[G.nWay][][];
         c = new byte[G.nWay][];
         O = new double[G.nWay][];
         P = new double[G.nWay][];

         for(var1 = 0; var1 < G.nWay; ++var1) {
            HandRange var10 = AnalysisPanel.a(var1);
            HandRange var11;
            (var11 = new HandRange(var10)).a(boardCardsArray);
            AnalysisPanel.p();
            N[var1] = var11.d();
            O[var1] = var11.f();
            P[var1] = var11.a;
            c[var1] = new byte[2 * O[var1].length];

            for(int var12 = 0; var12 < N[var1].length; ++var12) {
               c[var1][var12 << 1] = (byte)N[var1][var12][0].getFullDeckIndex();
               c[var1][(var12 << 1) + 1] = (byte)N[var1][var12][1].getFullDeckIndex();
            }
         }

         X = 0;
         n = new collections.LongIntHashMap[G.nWay];
         buckets = new int[G.nWay << 2];

         for(var1 = 0; var1 < G.nWay; ++var1) {
            n[var1] = new collections.LongIntHashMap();
            int[] var13 = a(N[var1], G.gameStage, boardCardsArray, n[var1]);
            buckets[(var1 << 2) + 1] = var13[0];
            buckets[(var1 << 2) + 2] = var13[1];
            buckets[(var1 << 2) + 3] = var13[2];
         }

         a(G, new int[5]);
         X = 0;
         e();
         X = 0;
      } else {
         handeval.PloHandEvaluator.a();
         if (G.gameStage == 1) {
            var0 = 3;
         } else if (G.gameStage == 2) {
            var0 = 4;
         } else {
            var0 = 5;
         }

         boardCardsArray = new card[var0];

         for(var1 = 0; var1 < var0; ++var1) {
            boardCardsArray[var1] = solver.card.a((String)solver.MainTabbedPane.enteredBoard.get(var1));
         }

         c = new byte[G.nWay][];
         O = new double[G.nWay][];
         P = new double[G.nWay][];
         int[][] var5 = new int[G.nWay][];

         for(int var4 = 0; var4 < G.nWay; ++var4) {
            OmahaHandRange var2 = (OmahaHandRange)AnalysisPanel.a(var4);
            (var2 = new OmahaHandRange(var2)).a(boardCardsArray);
            P[var4] = var2.a;
            c[var4] = var2.e();
            O[var4] = var2.f();
            var5[var4] = var2.l();
         }

         X = 0;
         Test.a();
         if (!D) {
            n = new collections.LongIntHashMap[G.nWay];
            buckets = new int[G.nWay << 2];
            ArrayList var6 = new ArrayList();

            for(int var7 = 0; var7 < G.nWay; ++var7) {
               Thread var3;
               (var3 = new Thread(new CFRBucketingTask(var7, var5))).start();
               var6.add(var3);
            }

            Iterator var8 = var6.iterator();

            while(var8.hasNext()) {
               Thread var9 = (Thread)var8.next();

               while(var9.isAlive()) {
                  Thread.sleep(20L);
               }
            }
         }

         Test.b();
         System.lineSeparator();
         if (solver.SolverRunner.b) {
            throw new InterruptedException();
         } else {
            a(G, new int[5]);
            X = 0;
            e();
            X = 0;
            X = 0;
         }
      }
   }

   private static void h() throws InterruptedException, Throwable {
      X = 0;
      // 5-card has more unique hands than 4-card
      int numHands = AnalysisPanel.is5Card() ? 134459 : 16432;  // 5-card ~134k unique hands vs 4-card ~16k
      i = new collections.LongIntHashMap(numHands);
      Iterator var2 = AnalysisPanel.is5Card() ? OmahaHandNormalizer.b5c().iterator() : OmahaHandNormalizer.b().iterator();

      while(var2.hasNext()) {
         long var0 = (Long)var2.next();
         i.a(var0, i.a());
      }

      handeval.PloHandEvaluator.a();
      int[] var3 = new int[5];
      a(new GameState(G), var3);
      X = 0;
      gnu.trove.map.S_ref var1 = null;
      gnu.trove.map.S_ref var7 = null;
      S = new collections.LongIntHashMap[4];
      // Use separate folders for 5-card PLO (Omaha5) vs 4-card PLO (Omaha/Omaha8)
      String var4;
      if (AnalysisPanel.is5Card()) {
         var4 = "Omaha5" + File.separator;
      } else {
         var4 = AnalysisPanel.gameType == 2 ? "Omaha8" + File.separator : "Omaha" + File.separator;
      }
      // Use separate file prefixes: omaha5 for 5-card, omaha for 4-card
      String filePrefix = AnalysisPanel.is5Card() ? "omaha5" : "omaha";
      // River index file: PLO5 uses "Omaha5/omaha5riverindex", PLO4 uses "omahariverindex" (in root)
      String riverIndexFile = AnalysisPanel.is5Card() ? var4 + "omaha5riverindex" : "omahariverindex";

      if (K > 2) {
         var7 = (gnu.trove.map.S_ref)solver.Equity.a(var4 + filePrefix + "mmnestedturn." + turnBuckets + ".4");
         if (solver.SolverRunner.b) {
            throw new InterruptedException();
         }

         if (var7 == null) {
            solver.MainTabbedPane.a("Generating turn buckets.", 0, 1);
            OmahaEquityGenerator.a(turnBuckets, 4, AnalysisPanel.gameType == 2);
            var7 = (gnu.trove.map.S_ref)solver.Equity.a(var4 + filePrefix + "mmnestedturn." + turnBuckets + ".4");
         }

         turnTexture = TextureAbstractionLookup.generateTurnTexture(turnTextureType);
      }

      if (solver.SolverRunner.b) {
         throw new InterruptedException();
      } else {
         if (K > 3) {
            T = solver.Equity.a((gnu.trove.map.S_ref)((TLongIntHashMap)solver.Equity.a(riverIndexFile)));
            if (version >= 10097L) {
               U = (TIntIntHashMap[])solver.Equity.a(var4 + "nriver." + riverBuckets);
               if (solver.SolverRunner.b) {
                  throw new InterruptedException();
               }

               if (U == null) {
                  solver.MainTabbedPane.a("Generating river buckets.", 0, 1);
                  OmahaEquityGenerator.a(riverBuckets, AnalysisPanel.gameType == 2);
                  U = (TIntIntHashMap[])solver.Equity.a(var4 + "nriver." + riverBuckets);
               }
            } else {
               U = (TIntIntHashMap[])solver.Equity.a(var4 + filePrefix + "mmnestedriver." + riverBuckets);
               if (solver.SolverRunner.b) {
                  throw new InterruptedException();
               }

               if (U == null) {
                  solver.MainTabbedPane.a("Generating river buckets.");
                  OmahaEquityGenerator.b(riverBuckets, AnalysisPanel.gameType == 2);
                  U = (TIntIntHashMap[])solver.Equity.a(var4 + filePrefix + "mmnestedriver." + riverBuckets);
               }
            }

            riverTexture = TextureAbstractionLookup.generateRiverTexture(riverTextureType);
         }

         EquityTableCache.b();
         if (K > 1) {
            var1 = (gnu.trove.map.S_ref)solver.Equity.a(var4 + filePrefix + "mmnestedflop." + flopBuckets + ".4");
            if (solver.SolverRunner.b) {
               throw new InterruptedException();
            }

            if (var1 == null) {
               solver.MainTabbedPane.a("Generating flop buckets.", 0, 1);
               OmahaEquityGenerator.b(flopBuckets, 4, AnalysisPanel.gameType == 2);
               var1 = (gnu.trove.map.S_ref)solver.Equity.a(var4 + filePrefix + "mmnestedflop." + flopBuckets + ".4");
            }

            System.lineSeparator();
         }

         if (solver.SolverRunner.b) {
            throw new InterruptedException();
         } else {
            S[1] = solver.Equity.a(var1);
            var1 = null;
            System.gc();
            S[2] = solver.Equity.a(var7);
            var7 = null;
            System.gc();
            X = 0;
            flopTexture = new collections.IntIntHashMap(1755);
            Iterator var8 = solver.CardArrays.generateFullDeckFlops().iterator();

            while(var8.hasNext()) {
               card[] var5 = (card[])var8.next();
               flopTexture.a(CardCombinations.getCardArrayValue(var5), flopTexture.c());
            }

            int var6 = turnTexture == null ? 0 : turnTexture.a() + 1;
            int var9 = riverTexture == null ? 0 : riverTexture.a() + 1;
            buckets = new int[G.nWay << 2];

            // 5-card has more unique hole card combinations
            int numPreflopBuckets = AnalysisPanel.is5Card() ? 134459 : 16432;
            // 5-card has more possible flop+hand combinations
            int flopBucketMultiplier = AnalysisPanel.is5Card() ? 498409379 : 79791556;

            for(int var10 = 0; var10 < G.nWay; ++var10) {
               buckets[var10 << 2] = numPreflopBuckets;
               buckets[(var10 << 2) + 1] = 1755 * flopBuckets << 2;
               if (flopBuckets == 0) {
                  buckets[(var10 << 2) + 1] = flopBucketMultiplier;
               }

               buckets[(var10 << 2) + 2] = var6 * turnBuckets << 2;
               buckets[(var10 << 2) + 3] = var9 * riverBuckets;
            }

            System.lineSeparator();
            e();
            X = 0;
         }
      }
   }

   public static final boolean b() {
      boolean var0;
      if (AnalysisPanel.gameType == 2 || icm == null && AnalysisPanel.e().nWay > 3 && AnalysisPanel.n()) {
         var0 = true;
      } else {
         var0 = false;
      }

      return var0;
   }

   public static void b(long var0) throws InterruptedException, Throwable {
      solver.SolverRunner.c = false;
      solver.SolverRunner.b = false;
      if (AnalysisPanel.gameType == 2 && !AnalysisPanel.n()) {
         JOptionPane.showMessageDialog(solver.MainTabbedPane.j, "Only symmetrical stacksizes can be used for Omaha Hi/Lo.");
         throw new InterruptedException();
      } else {
         if (!g) {
            b((File)null);
         }

         version = var0;
         h = null;
         G = new GameState(AnalysisPanel.e());
         t = b();
         solver.MainTabbedPane.h.clear();
         evs = new double[G.nWay];
         eviters = new long[G.nWay];
         handeval.tables.HandRankEvaluator.a();
         AnalysisPanel.p();
         HandStatisticCollection.c();
         M.clear();
         GameTree var2;
         (var2 = AnalysisPanel.g.a).a.clear();
         var2.b = null;
         var2.c = null;
         var2.d.setEnabled(false);
         var2.e.setEnabled(false);
         if (!g) {
            a = null;
         }

         new CardArrays();
         if (GameSettings.d != null) {
            f = GameSettings.d;
            isoLevel = 1;
         } else {
            f = null;
            isoLevel = GameSettings.c;
         }

         iterations = 0L;
         A = null;
         cfrTables = null;
         avg = null;
         hasEV = null;
         System.gc();
         if (G.gameStage == 0) {
            init();
         } else {
            g();
         }

         F = System.currentTimeMillis();
         System.gc();
         solver.MainTabbedPane.c();
      }
   }

   public static int[] a(GameState var0) {
      int[] var1;
      if ((var1 = (int[])M.get(var0)) != null) {
         return var1;
      } else {
         var1 = AnalysisPanel.a(var0);
         var0 = ((PokerTreeNode)PokerTreeNode.d.get(var0)).c;
         M.put(var0, var1);
         return var1;
      }
   }

   private static List e(GameState var0) {
      ArrayList var1 = new ArrayList();
      int[] var5;
      int var4 = (var5 = a(var0)).length;

      for(int var3 = 0; var3 < var4; ++var3) {
         int var2 = var5[var3];
         var1.add(b(var0, var2));
      }

      return var1;
   }

   public static GameState b(GameState var0, int var1) {
      if (var0 == null) {
         return null;
      } else {
         PokerTreeNode var3;
         if ((var3 = (PokerTreeNode)PokerTreeNode.d.get(var0)) == null) {
            return null;
         } else {
            Enumeration var4 = var3.children();

            while(var4.hasMoreElements()) {
               PokerTreeNode var2;
               if ((var2 = (PokerTreeNode)var4.nextElement()).nodeType == var1) {
                  return var2.c;
               }
            }

            return null;
         }
      }
   }

   private static void a(GameState var0, int[] var1) {
      ++X;
      if (var0.gameStage < 4) {
         ++var1[var0.gameStage];
         V.put(var0, X);
         Iterator var2 = e(var0).iterator();

         while(var2.hasNext()) {
            a((GameState)var2.next(), var1);
         }

      }
   }

   public static final void a(CFRIterator var0) {
      int[] var1;
      int[] var4;
      int[] var5;
      int[] var6;
      int var10;
      int[] var12;
      int var13;
      int var14;
      int var15;
      int var18;
      int var20;
      int[] var22;
      int[] var23;
      int[] var24;
      int var29;
      int[] var59;
      card[] var60;
      card[] var64;
      int var80;
      int var80tmp;
      int[] var82;
      int var83;
      int var86;
      if (AnalysisPanel.isHoldem()) {
         card[] var54;
         card[] var55;
         int var56;
         if (G.gameStage == 0) {
            var0 = var0;
            var1 = new int[7];
            var54 = new card[7];
            var55 = new card[7];

            for(var56 = 0; var56 < 7; ++var56) {
               var55[var56] = new card(0, 0);
            }

            var5 = new int[(var4 = new int[G.nWay]).length];
            var59 = new int[4];
            int[][] var10000 = new int[G.nWay][4];
            var60 = new card[5];
            int[] var65 = new int[3];
            var12 = new int[G.nWay];

            for(var13 = 0; var13 < var12.length; var12[var13] = var13++) {
            }

            CardArrays var70 = new CardArrays();
            AnalysisPanel.p();

            for(var14 = 0; var14 < 150000 && !var0.a() && !var0.c(); ++var14) {
               int[] var10002 = new int[5];
               int[] var93 = new int[7];
               var24 = var59;
               var23 = var5;
               var22 = var4;
               card[] var77 = var54;
               CardArrays var63 = var70;
               CFRIterator var73 = var0;
               var70.a((G.nWay << 1) + 5, var0.h());
               byte var87 = 0;
               int var88 = var87 + 1;
               var60[0] = var70.b[0];
               ++var88;
               var60[1] = var70.b[1];
               ++var88;
               var60[2] = var70.b[2];
               ++var88;
               var60[3] = var70.b[3];
               ++var88;
               var60[4] = var70.b[4];
               var54[2] = var60[0];
               var54[3] = var60[1];
               var54[4] = var60[2];
               var54[5] = var60[3];
               var54[6] = var60[4];
               var1[2] = var60[0].getFullDeckIndex();
               var1[3] = var60[1].getFullDeckIndex();
               var1[4] = var60[2].getFullDeckIndex();
               var1[5] = var60[3].getFullDeckIndex();
               var1[6] = var60[4].getFullDeckIndex();
               CardCombinations.a(var60, var55);
               var82 = CardCombinations.getBoardTexturesValues(var55, var65);

               int var28;
               int var28tmp = 0;
               for(var28 = 0; var28 < G.nWay; ++var28) {
                  var77[0] = var63.b[var88++];
                  var77[1] = var63.b[var88++];
                  var18 = var28 << 2;
                  var73.a(var18, i.a(CardCombinations.a(var77)));
                  long var91 = CardCombinations.c(var77, var24);
                  long var92 = CardCombinations.b(var77, var24);
                  long var44 = CardCombinations.a(var77, var24);

                  final int var18_1 = var18;
                  final int[] var82_1 = var82;
                  CFRLoaderInterface var89 = (var8x) -> {
                     var8x.a(var18_1 + 1, (var82_1[0] * flopBuckets << 2) + S[1].a(var91));
                     var8x.a(var18_1 + 2, (var82_1[1] * turnBuckets << 2) + S[2].a(var92));
                     var8x.a(var18_1 + 3, var82_1[2] * riverBuckets + S[3].a(var44));
                  };
                  var73.a(var18 + 1, var89);
                  var73.a(var18 + 2, var89);
                  var73.a(var18 + 3, var89);
               }

               var88 -= G.nWay << 1;
               AnalysisPanel.p();
               var28 = handeval.tables.HandRankEvaluator.b(var1);

               boolean acesOnBoard = false;
               if (AnalysisPanel.gameType == 3) {
                  for (int i = 2; i<7; i++) {
                     if (var1[i] > 47) {
                        acesOnBoard = true;
                        break;
                     }
                  }

                  if (acesOnBoard) {
                     var28tmp = handeval.tables.HandRankEvaluator.bA5swap(var1);
                  }
               }

               int var58;
               int tmp1;
               int tmp2;
               for(var58 = 0; var58 < G.nWay; ++var58) {
                  var77[0] = var63.b[var88++];
                  var77[1] = var63.b[var88++];

                  int var770index = var77[0].getFullDeckIndex();
                  int var771index = var77[1].getFullDeckIndex();

                  tmp1 = handeval.tables.HandRankEvaluator.a(var28, var770index, var771index);

                  if (AnalysisPanel.gameType == 3) {
                     if (acesOnBoard) {
                        if ( (var770index > 47) || (var771index > 47)) {
                           tmp2 = handeval.tables.HandRankEvaluator.aA5swap(var28tmp, var770index, var771index);
                        } else {
                           tmp2 = handeval.tables.HandRankEvaluator.a(var28tmp, var770index, var771index);
                        }
                     } else {
                        if ( (var770index > 47) || (var771index > 47)) {
                           tmp2 = handeval.tables.HandRankEvaluator.aA5swap(var28, var770index, var771index);
                        } else {
                           tmp2 = handeval.tables.HandRankEvaluator.a(var28, var770index, var771index);
                        }
                     }

                     tmp1 = Math.max(tmp1, tmp2);

                     if ( ((tmp1 >= 5863) && (tmp1 < 7140)) || (tmp1 >= 7296) ) {
                        tmp1 += 2000;
                     }
                  }

                  var22[var58] = tmp1;
               }

               var73.g();
               int[] var90;
               //var29 = player count
               var29 = (var90 = var12).length;
               var73.setHandStrengths(var22);

               for(var86 = 0; var86 < var29; ++var86) {
                  var58 = var90[var86];  // Current player

                  if (t) {
                     int[] payoutIdxs = var73.b(var22, var23, var58);
                     var73.a(payoutIdxs);
                  } else {
                     var73.a(A.a(var28, var22, var58));
                  }
                  var73.c(var58);
               }

               if (var73.h().nextDouble() < 1.0D / (2.0D * (double)(G.nWay * G.nWay))) {
                  for(var58 = 0; var58 < G.nWay; ++var58) {
                     var73.d(var58);
                  }
               }
            }

         } else {
            AnalysisPanel.p();
            var1 = new int[7];
            var54 = new card[7];
            var55 = new card[7];

            for(var56 = 0; var56 < 7; ++var56) {
               var55[var56] = new card(0, 0);
            }

            var5 = new int[(var4 = new int[G.nWay]).length];
            var6 = new int[4];
            var59 = new int[4];
            SplittableRandom var61 = var0.h();
            var60 = new card[G.nWay];
            var64 = new card[G.nWay];
            var12 = new int[G.nWay];
            long var67 = 0L;

            for(var15 = 0; var15 < boardCardsArray.length; ++var15) {
               var54[var15 + 2] = boardCardsArray[var15];
               var1[var15 + 2] = boardCardsArray[var15].getFullDeckIndex();
               var67 = solver.Equity.a(var67, var1[var15 + 2]);
            }

            int[] var72 = new int[G.nWay];

            for(var10 = 0; var10 < var72.length; var72[var10] = var10++) {
            }

            for(var10 = 0; var10 < 145000 && !var0.a() && !var0.c(); ++var10) {
               long var78 = a(var67, var60, var64, var12, var61);
               if (AnalysisPanel.gameType == 0) {
                  if (G.gameStage == 1) {
                     var54[5] = solver.CardArrays.a(var78, var61);
                     var1[5] = var54[5].getFullDeckIndex();
                     var78 = solver.Equity.a(var78, var1[5]);
                     var54[6] = solver.CardArrays.a(var78, var61);
                     var1[6] = var54[6].getFullDeckIndex();
                  } else if (G.gameStage == 2) {
                     var54[6] = solver.CardArrays.a(var78, var61);
                     var1[6] = var54[6].getFullDeckIndex();
                  }
               } else {
                  if (G.gameStage == 1) {
                     var54[5] = solver.CardArrays.aShortdeck(var78, var61);
                     var1[5] = var54[5].getFullDeckIndex();
                     var78 = solver.Equity.a(var78, var1[5]);
                     var54[6] = solver.CardArrays.aShortdeck(var78, var61);
                     var1[6] = var54[6].getFullDeckIndex();
                  } else if (G.gameStage == 2) {
                     var54[6] = solver.CardArrays.aShortdeck(var78, var61);
                     var1[6] = var54[6].getFullDeckIndex();
                  }
               }

               var80 = handeval.tables.HandRankEvaluator.b(var1);
               var80tmp = 0;

               boolean acesOnBoard = false;
               if (AnalysisPanel.gameType == 3) {
                  for (int i = 2; i<7; i++) {
                     if (var1[i] > 47) {
                        acesOnBoard = true;
                        break;
                     }
                  }

                  if (acesOnBoard) {
                     var80tmp = handeval.tables.HandRankEvaluator.bA5swap(var1);
                  }
               }

               int tmp1;
               int tmp2;
               for(var20 = 0; var20 < G.nWay; ++var20) {
                  var54[0] = var60[var20];
                  var54[1] = var64[var20];

                  int var54_0 = var54[0].getFullDeckIndex();
                  int var54_1 = var54[1].getFullDeckIndex();

                  int[] var76 = a(n[var20], var54, var55, var59, var6);

                  for(var83 = 1; var83 < var76.length; ++var83) {
                     if (var76[var83] >= 0) {
                        var0.a((var20 << 2) + var83, var76[var83]);
                     }
                  }

                  tmp1 = handeval.tables.HandRankEvaluator.a(var80, var54_0, var54_1);

                  if (AnalysisPanel.gameType == 3) {
                     if (acesOnBoard) {
                        if ( (var54_0 > 47) || (var54_1 > 47)) {
                           tmp2 = handeval.tables.HandRankEvaluator.aA5swap(var80tmp, var54_0, var54_1);
                        } else {
                           tmp2 = handeval.tables.HandRankEvaluator.a(var80tmp, var54_0, var54_1);
                        }
                     } else {
                        if ( (var54_0 > 47) || (var54_1 > 47)) {
                           tmp2 = handeval.tables.HandRankEvaluator.aA5swap(var80, var54_0, var54_1);
                        } else {
                           tmp2 = handeval.tables.HandRankEvaluator.a(var80, var54_0, var54_1);
                        }
                     }

                     tmp1 = Math.max(tmp1, tmp2);

                     if ( ((tmp1 >= 5863) && (tmp1 < 7140)) || (tmp1 >= 7296) ) {
                        tmp1 += 2000;
                     }
                  }

                  var4[var20] = tmp1;
               }

               var0.g();
               var24 = var72;
               int var85 = var72.length;
               var0.setHandStrengths(var4);
               for(int var84 = 0; var84 < var85; ++var84) {
                  var83 = var24[var84];
                  if (t) {
                     int[] payoutIdxs = var0.b(var4, var5, var83);
                     var0.a(payoutIdxs);
                  } else {
                     var0.a(A.a(var20, var4, var83));
                  }
                  var0.c(var83);
               }

               if (var0.h().nextInt(4 * G.nWay * G.nWay) == 1) {
                  for(var83 = 0; var83 < G.nWay; ++var83) {
                     var0.d(var83);
                  }
               }
            }

         }
      } else {
         int[] var2;
         int[] var3;
         int[] var8;
         int[] var66;
         if (G.gameStage == 0) {
            var0 = var0;
            int holeCards0 = AnalysisPanel.is5Card() ? 5 : 4;
            var1 = new int[holeCards0 + 5];  // 5-card: 10, 4-card: 9
            var3 = new int[(var2 = new int[G.nWay]).length];
            var4 = new int[var2.length];
            SplittableRandom var57 = var0.h();
            var59 = new int[holeCards0 + 5];  // 5-card: 10, 4-card: 9
            var8 = new int[13];
            var60 = new card[5];

            for(int var62 = 0; var62 < 5; ++var62) {
               var60[var62] = new card(0, 0);
            }

            var64 = new card[5];
            var12 = new int[4];
            var66 = new int[4];
            int[] var69 = new int[G.nWay];

            for(var15 = 0; var15 < var69.length; var69[var15] = var15++) {
            }

            CardArrays var71 = new CardArrays();

            for(var10 = 0; var10 < 145000 && !var0.a() && !var0.c(); ++var10) {
               int[] var27 = var66;
               var5 = var59;
               int[] var25 = var4;
               var24 = var8;
               var23 = var3;
               var22 = var2;
               int[] var81 = var12;
               var82 = var1;
               CardArrays var79 = var71;
               CFRIterator var75 = var0;
               // 5-card: (G.nWay * 5) + 5, 4-card: (G.nWay * 4) + 5
               var71.a(AnalysisPanel.is5Card() ? (G.nWay * 5) + 5 : (G.nWay << 2) + 5, var57);
               byte var26 = 0;
               var86 = var26 + 1;
               var64[0] = var71.b[0];
               ++var86;
               var64[1] = var71.b[1];
               ++var86;
               var64[2] = var71.b[2];
               ++var86;
               var64[3] = var71.b[3];
               ++var86;
               var64[4] = var71.b[4];
               // Board cards: 5-card uses indices 5-9, 4-card uses indices 4-8
               var1[holeCards0] = var64[0].b();
               var1[holeCards0 + 1] = var64[1].b();
               var1[holeCards0 + 2] = var64[2].b();
               var1[holeCards0 + 3] = var64[3].b();
               var1[holeCards0 + 4] = var64[4].b();
               var29 = AnalysisPanel.is5Card() ? handeval.PloHandEvaluator.a5c(var1) : handeval.PloHandEvaluator.a(var1);
               CardCombinations.a(var64, var60);
               CardCombinations.getBoardTexturesValues(var60, var12);
               // 5-card: c5c with 10 cards, 4-card: c with 9 cards
               final long var34 = AnalysisPanel.is5Card() ? OmahaHandNormalizer.c5c(var1, var59, var66, 10) : OmahaHandNormalizer.c(var1, var59, var66, 9);

               int var30;
               int var31;
               for(var83 = 0; var83 < G.nWay; ++var83) {
                  var82[0] = var79.b[var86++].b();
                  var82[1] = var79.b[var86++].b();
                  var82[2] = var79.b[var86++].b();
                  var82[3] = var79.b[var86++].b();
                  if (AnalysisPanel.is5Card()) {
                     var82[4] = var79.b[var86++].b();
                  }
                  var30 = AnalysisPanel.is5Card() ? handeval.PloHandEvaluator.a5c(var82, var29) : handeval.PloHandEvaluator.a(var82, var29);
                  var22[var83] = -var30;
                  if (AnalysisPanel.gameType == 2) {
                     var31 = handeval.PloHandEvaluator.a(var82, var24);
                     var23[var83] = var31;
                     var30 |= var31 << 16;
                  }

                  int var42 = var83 << 2;
                  // 5-card: use d5c, c5c, b5c; 4-card: use d, c, b
                  var75.a(var42, i.a(AnalysisPanel.is5Card() ? OmahaHandNormalizer.d5c(var82, var5, var27) : OmahaHandNormalizer.d(var82, var5, var27)));
                  long var50 = AnalysisPanel.is5Card() ? OmahaHandNormalizer.c5c(var82, var5, var27) : OmahaHandNormalizer.c(var82, var5, var27);
                  long var52 = AnalysisPanel.is5Card() ? OmahaHandNormalizer.b5c(var82, var5, var27) : OmahaHandNormalizer.b(var82, var5, var27);

                  int var30_1 = var30;
                  CFRLoaderInterface var74 = (var9x) -> {
                     var9x.a(var42 + 1, (var81[0] * flopBuckets << 2) + S[1].a(var50));
                     var9x.a(var42 + 2, (var81[1] * turnBuckets << 2) + S[2].a(var52));
                     var9x.a(var42 + 3, var81[2] * riverBuckets + U[T.a(var34)].get(var30_1));
                  };
                  var75.a(var42 + 1, var74);
                  var75.a(var42 + 2, var74);
                  var75.a(var42 + 3, var74);
               }

               var83 = A.a(var22, var4);
               var75.g();
               int[] var40;
               var18 = (var40 = var69).length;
               var75.setHandStrengths(var22);

               for(var31 = 0; var31 < var18; ++var31) {
                  var30 = var40[var31];
                  if (t) {
                     int[] payoutIdxs = var75.b(var22, var25, var30);  // CHANGED
                     var75.a(payoutIdxs);  // CHANGED
                     if (AnalysisPanel.gameType == 2) {
                        var75.b(a(var23, var25, var30));
                     }
                  } else {
                     var75.a(A.a(var83, var22, var30));
                  }

                  var75.c(var30);
               }

               if (var75.h().nextInt(2 * G.nWay * G.nWay) == 1) {
                  for(var30 = 0; var30 < G.nWay; ++var30) {
                     var75.d(var30);
                  }
               }
            }

         } else {
            var0 = var0;
            int holeCards = AnalysisPanel.is5Card() ? 5 : 4;
            var1 = new int[holeCards + 5];  // hole cards + 5 board cards
            var2 = new int[holeCards + 5];
            var3 = new int[4];
            var4 = new int[G.nWay];
            var5 = null;
            var6 = new int[var4.length];
            SplittableRandom var7 = var0.h();
            var8 = null;
            if (AnalysisPanel.gameType == 2) {
               var8 = new int[13];
               var5 = new int[var4.length];
            }

            int[] var9 = new int[G.nWay * holeCards];  // nWay * holeCards for indexing
            long var11 = 0L;

            for(var13 = 0; var13 < boardCardsArray.length; ++var13) {
               var1[var13 + holeCards] = boardCardsArray[var13].b();
               var11 = solver.Equity.a(var11, var1[var13 + holeCards]);
            }

            var66 = new int[G.nWay];

            for(var14 = 0; var14 < var66.length; var66[var14] = var14++) {
            }

            long var68 = var11;

            for(var10 = 0; var10 < 155000 && !var0.a() && !var0.c(); ++var10) {
               var11 = AnalysisPanel.is5Card() ? a5c(var68, var9, var7) : a(var68, var9, var7);
               if (G.gameStage == 1) {
                  do {
                     var1[holeCards + 3] = var7.nextInt(52);
                  } while(solver.Equity.b(var11, var1[holeCards + 3]));

                  var11 = solver.Equity.a(var11, var1[holeCards + 3]);

                  do {
                     var1[holeCards + 4] = var7.nextInt(52);
                  } while(solver.Equity.b(var11, var1[holeCards + 4]));
               } else if (G.gameStage == 2) {
                  do {
                     var1[holeCards + 4] = var7.nextInt(52);
                  } while(solver.Equity.b(var11, var1[holeCards + 4]));
               }

               int var17 = AnalysisPanel.is5Card() ? handeval.PloHandEvaluator.a5c(var1) : handeval.PloHandEvaluator.a(var1);

               int var16;
               for(var18 = 0; var18 < G.nWay; ++var18) {
                  var1[0] = var9[var18 * holeCards];
                  var1[1] = var9[var18 * holeCards + 1];
                  var1[2] = var9[var18 * holeCards + 2];
                  var1[3] = var9[var18 * holeCards + 3];
                  if (AnalysisPanel.is5Card()) {
                     var1[4] = var9[var18 * holeCards + 4];
                  }
                  if (G.gameStage < 3) {
                     // For 5-card: use 3-param a5c for turn bucket (matches reference)
                     // Turn bucket - 5-card uses a5c(var1, var2, var3), 4-card uses a(var1, var2, var3)
                     long var19 = AnalysisPanel.is5Card() ? OmahaHandNormalizer.a5c(var1, var2, var3) : OmahaHandNormalizer.a(var1, var2, var3);
                     var16 = n[var18].a(var19);
                     var0.a((var18 << 2) + 2, var16);
                     if (G.gameStage < 2) {
                        // Flop bucket - divide by 100L (same as reference)
                        var16 = n[var18].a(var19 / 100L);
                        var0.a((var18 << 2) + 1, var16);
                     }
                  }

                  // River bucket - 5-card uses 4-param a5c, 4-card uses 4-param a
                  var80 = n[var18].a(AnalysisPanel.is5Card() ? OmahaHandNormalizer.a5c(var1, var2, var3, isoLevel) : OmahaHandNormalizer.a(var1, var2, var3, isoLevel));
                  var0.a((var18 << 2) + 3, var80);
                  System.lineSeparator();
                  var4[var18] = AnalysisPanel.is5Card() ? -handeval.PloHandEvaluator.a5c(var1, var17) : -handeval.PloHandEvaluator.a(var1, var17);
                  if (AnalysisPanel.gameType == 2) {
                     var5[var18] = handeval.PloHandEvaluator.a(var1, var8);
                  }
               }

               var18 = A.a(var4, var6);
               var0.g();
               int[] var21 = var66;
               var16 = var66.length;
               var0.setHandStrengths(var4);

               for(var20 = 0; var20 < var16; ++var20) {
                  var80 = var21[var20];
                  if (t) {
                     int[] payoutIdxs = var0.b(var4, var6, var80);  // CHANGED
                     var0.a(payoutIdxs);  // CHANGED
                     if (AnalysisPanel.gameType == 2) {
                        var0.b(a(var5, var6, var80));
                     }
                  } else {
                     var0.a(A.a(var18, var4, var80));
                  }

                  var0.c(var80);
               }

               if (var0.h().nextDouble() < 1.0D / (double)(2 * G.nWay * G.nWay)) {
                  for(var80 = 0; var80 < G.nWay; ++var80) {
                     var0.d(var80);
                  }
               }
            }

         }
      }

   }

   public static void main(String[] var0) {
      solver.Equity.a(0L, 2);
      solver.Equity.a(0L, 21);
      solver.Equity.a(0L, 51);
      SplittableRandom var6 = new SplittableRandom(23626227L);
      (new OmahaHandRange()).h();
      HandRange var1 = ResultsReader.a.c("15%", new card[0], 1);
      c = new byte[5][];
      O = new double[5][];
      long[][] var2 = new long[5][];

      for(int var3 = 0; var3 < 5; ++var3) {
         c[var3] = var1.e();
         O[var3] = var1.f();
         var2[var3] = new long[O[var3].length];

         for(int var4 = 0; var4 < O[var3].length; ++var4) {
            int var5 = var4 << 2;
            var2[var3][var4] = solver.Equity.a(var2[var3][var4], c[var3][var5]);
            var2[var3][var4] = solver.Equity.a(var2[var3][var4], c[var3][var5 + 1]);
            var2[var3][var4] = solver.Equity.a(var2[var3][var4], c[var3][var5 + 2]);
            var2[var3][var4] = solver.Equity.a(var2[var3][var4], c[var3][var5 + 3]);
         }
      }

      int[] var8 = new int[20];

      int var7;
      for(var7 = 0; var7 < 100000; ++var7) {
         a(0L, var8, var6);
      }

      for(var7 = 0; var7 < 200000; ++var7) {
         a(0L, var8, var6);
      }

      System.lineSeparator();
      System.lineSeparator();
   }

   private static long a(long var0, int[] var2, SplittableRandom var3) {
      label45:
      while(true) {
         long var4 = var0;

         int var6;
         int var8;
         for(var6 = 0; var6 < c.length; ++var6) {
            double[] var7 = O[var6];

            for(var8 = var3.nextInt(var7.length); var7[var8] < 0.9999999999D && var3.nextDouble() > var7[var8]; var8 = var3.nextInt(var7.length)) {
            }

            int var9 = var8 << 2;
            byte[] var11 = c[var6];
            if (solver.Equity.b(var4, var11[var9]) || solver.Equity.b(var4, var11[var9 + 1]) || solver.Equity.b(var4, var11[var9 + 2]) || solver.Equity.b(var4, var11[var9 + 3])) {
               continue label45;
            }

            var4 = solver.Equity.a(solver.Equity.a(solver.Equity.a(solver.Equity.a(var4, var11[var9]), var11[var9 + 1]), var11[var9 + 2]), var11[var9 + 3]);
            var2[var6 << 2] = var9;
         }

         for(var6 = 0; var6 < c.length; ++var6) {
            byte[] var10 = c[var6];
            var8 = var2[var6 << 2];
            var2[var6 << 2] = var10[var8];
            var2[(var6 << 2) + 1] = var10[var8 + 1];
            var2[(var6 << 2) + 2] = var10[var8 + 2];
            var2[(var6 << 2) + 3] = var10[var8 + 3];
         }

         return var4;
      }
   }

   // 5-card version of card dealing method
   private static long a5c(long var0, int[] var2, SplittableRandom var3) {
      label45:
      while(true) {
         long var4 = var0;

         int var6;
         int var8;
         for(var6 = 0; var6 < c.length; ++var6) {
            double[] var7 = O[var6];

            for(var8 = var3.nextInt(var7.length); var7[var8] < 0.9999999999D && var3.nextDouble() > var7[var8]; var8 = var3.nextInt(var7.length)) {
            }

            int var9 = var8 * 5;
            byte[] var11 = c[var6];
            if (solver.Equity.b(var4, var11[var9]) || solver.Equity.b(var4, var11[var9 + 1]) || solver.Equity.b(var4, var11[var9 + 2]) || solver.Equity.b(var4, var11[var9 + 3]) || solver.Equity.b(var4, var11[var9 + 4])) {
               continue label45;
            }

            var4 = solver.Equity.a(solver.Equity.a(solver.Equity.a(solver.Equity.a(solver.Equity.a(var4, var11[var9]), var11[var9 + 1]), var11[var9 + 2]), var11[var9 + 3]), var11[var9 + 4]);
            var2[var6 * 5] = var9;
         }

         for(var6 = 0; var6 < c.length; ++var6) {
            byte[] var10 = c[var6];
            var8 = var2[var6 * 5];
            var2[var6 * 5] = var10[var8];
            var2[var6 * 5 + 1] = var10[var8 + 1];
            var2[var6 * 5 + 2] = var10[var8 + 2];
            var2[var6 * 5 + 3] = var10[var8 + 3];
            var2[var6 * 5 + 4] = var10[var8 + 4];
         }

         return var4;
      }
   }

   private static final long a(long var0, card[] var2, card[] var3, int[] var4, SplittableRandom var5) {
      label39:
      while(true) {
         long var6 = var0;

         int var8;
         for(var8 = 0; var8 < G.nWay; ++var8) {
            double[] var9 = O[var8];

            int var10;
            for(var10 = var5.nextInt(var9.length); var9[var10] < 1.0D && var5.nextDouble() > var9[var10]; var10 = var5.nextInt(var9.length)) {
            }

            byte[] var12 = c[var8];
            int var11 = var10 << 1;
            if (solver.Equity.b(var6, var12[var11]) || solver.Equity.b(var6, var12[var11 + 1])) {
               continue label39;
            }

            var6 = solver.Equity.a(solver.Equity.a(var6, var12[var11]), var12[var11 + 1]);
            var4[var8] = var10;
         }

         for(var8 = 0; var8 < G.nWay; ++var8) {
            var2[var8] = N[var8][var4[var8]][0];
            var3[var8] = N[var8][var4[var8]][1];
         }

         return var6;
      }
   }

   private static int[] a(int[] var0, int[] var1, int var2) {
      var1[0] = 0;
      var1[1] = 0;

      for(int var3 = 0; var3 < var0.length; ++var3) {
         if (var0[var3] != 32767 && var0[var2] >= var0[var3]) {
            if (var0[var2] == var0[var3]) {
               var1[0] |= 1 << var3;
            } else {
               var1[1] |= 1 << var3;
            }
         }
      }

      return var1;
   }

   public static boolean b(card[] var0, card[] var1) {
      card[] var4 = var0;
      int var3 = var0.length;

      for(int var2 = 0; var2 < var3; ++var2) {
         card var10 = var4[var2];
         card[] var8 = var1;
         int var7 = var1.length;

         for(int var6 = 0; var6 < var7; ++var6) {
            card var5 = var8[var6];
            if (var10.rank == var5.rank && var10.suit == var5.suit) {
               return true;
            }
         }
      }

      return false;
   }

   private static double[] a(double[] var0) {
      double var1 = 0.0D;

      int var3;
      for(var3 = 0; var3 < var0.length; ++var3) {
         var1 += var0[var3];
      }

      if (var1 > 0.0D) {
         for(var3 = 0; var3 < var0.length; ++var3) {
            var0[var3] /= var1;
         }
      } else {
         for(var3 = 0; var3 < var0.length; ++var3) {
            var0[var3] = 1.0D / (double)var0.length;
         }
      }

      return var0;
   }

   private static int a(int[] var0, int[] var1, int[] var2, int var3) {
      GameState var4 = G;
      int var5 = 0;
      int[] var8 = var2;
      int var7 = var2.length;

      for(int var6 = 0; var6 < var7; ++var6) {
         int var14 = var8[var6];
         if (var4.firstPlayerToAct != var3) {
            var4 = b(var4, var14);
         } else {
            int var9 = 0;
            int[] var13;
            int var12 = (var13 = a(var4)).length;

            for(int var11 = 0; var11 < var12; ++var11) {
               int var10 = var13[var11];
               if (var14 == var10) {
                  break;
               }

               ++var9;
            }

            var0[var5] = (Integer)V.get(var4);
            var1[var5] = var9;
            ++var5;
            var4 = b(var4, var14);
         }
      }

      return var5;
   }

   private static double a(int[] var0, double var1, int var3, int[] var4) {
      double var5 = var1;
      GameState var16 = G;
      int[] var8 = var0;
      int var7 = var0.length;

      for(int var2 = 0; var2 < var7; ++var2) {
         int var15 = var8[var2];
         if (var16.firstPlayerToAct == var3) {
            int var9 = 0;
            int[] var11;
            int var10 = (var11 = a(var16)).length;

            for(int var14 = 0; var14 < var10; ++var14) {
               int var13 = var11[var14];
               if (var15 == var13) {
                  break;
               }

               ++var9;
            }

            var10 = var4[var16.gameStage];
            double var17 = c((Integer)V.get(var16), var10, var9);
            if ((var5 *= var17) <= 1.0E-7D) {
               return 0.0D;
            }
         }

         var16 = b(var16, var15);
      }

      return var5;
   }

   private static double a(int[] var0, int[] var1, double var2, int[] var4) {
      double var5 = var2;

      for(int var11 = 0; var11 < var0.length; ++var11) {
         int var3;
         double var9 = c(var3 = var0[var11], var4[UnsafeMemoryStorage.a((long)var3) % 4], var1[var11]);
         if ((var5 *= var9) <= 1.0E-7D) {
            return 0.0D;
         }
      }

      return var5;
   }

   public static HandRange[] a(GameState var0, ArrayList var1, int var2) {
      HandRange[] var3;
      (var3 = new HandRange[3])[0] = new HandRange();
      var3[1] = new HandRange();
      int var4;
      int var6;
      int var7;
      if (var0 == null) {
         var3[1] = var3[0];
         if (G.gameStage > 0) {
            var4 = 0;
            card[][] var15;
            var7 = (var15 = N[0]).length;

            for(var6 = 0; var6 < var7; ++var6) {
               card[] var5 = var15[var6];
               var3[0].a(var5[0] + solver.HashUtil.q(new char[0]) + var5[1], O[0][var4]);
               ++var4;
            }
         } else {
            for(var4 = 0; var4 < 1326; ++var4) {
               HandRange var10000 = var3[0];
               double var11 = 1.0D;
               var10000.a[var4] = var11;
            }
         }

         return var3;
      } else {
         var4 = 0;
         int[] var8;
         var7 = (var8 = a(var0)).length;

         for(var6 = 0; var6 < var7 && var8[var6] != var2; ++var6) {
            ++var4;
         }

         Iterator var14 = a(var0, var1).iterator();

         while(var14.hasNext()) {
            PlayerHandStatistic[] var13;
            if ((var13 = (PlayerHandStatistic[])var14.next()) != null && var13[var4] != null) {
               var3[1].a(var13[var4].g, var13[var4].a() * var13[var4].b);
               var3[0].a(var13[var4].g, var13[var4].a());
            }
         }

         var3[2] = new HandRange(var3[1]);
         var3[2].b();
         return var3;
      }
   }

   private static HandRange i() {
      return (HandRange)(AnalysisPanel.isHoldem() ? new HandRange() : new OmahaHandRange());
   }

   public static HandRange a(HandRange var0) {
      return (HandRange)(AnalysisPanel.isHoldem() ? new HandRange(var0) : new OmahaHandRange(var0));
   }

   private static HandRange b(double[] var0) {
      return (HandRange)(AnalysisPanel.isHoldem() ? new HandRange(var0) : new OmahaHandRange(var0));
   }

   public static HandRange a(int var0, GameState var1, card[] var2, HandRange var3, boolean var4) {
      GameState var5 = new GameState(G);
      HandRange var6;
      if (AnalysisPanel.getTreeStage() == 0) {
         (var6 = i()).h();
      } else {
         var6 = b(P[var0]);
      }

      var6.a(var2);
      if (var3 != null) {
         var6 = var6.b(var3);
      }

      Stack var22 = new Stack();
      var1.a(var22);
      int var7 = AnalysisPanel.o();
      card[] var8;
      if (var2 == null) {
         var8 = new card[var7];
      } else {
         var8 = new card[var2.length + var7];

         for(int var9 = 0; var9 < var2.length; ++var9) {
            var8[var9 + var7] = var2[var9];
         }
      }

      card[] var28 = new card[var7];

      int var21;
      for(var21 = 0; var21 < var28.length; ++var21) {
         var28[var21] = new card(1, 1);
      }

      a(var0, var22, var6, var5, var8, var28);
      if (!var4) {
         return var6;
      } else {
         var6.b = new double[var6.a.length];
         var21 = var7;
         if (var1.parentNode.gameStage == 1) {
            var21 = var7 + 3;
         } else if (var1.parentNode.gameStage == 2) {
            var21 = var7 + 4;
         } else if (var1.parentNode.gameStage == 3) {
            var21 = var7 + 5;
         }

         card[] var23 = new card[var21];

         int var24;
         for(var24 = var7; var24 < var21; ++var24) {
            var23[var24] = var8[var24];
         }

         int[] var25 = a(var1.parentNode);
         var21 = -1;
         int var26 = 0;
         int[] var19 = var25;
         int var10 = var25.length;

         for(int var27 = 0; var27 < var10; ++var27) {
            if (var19[var27] == var1.nodeType) {
               var21 = var26;
               break;
            }

            ++var26;
         }

         for(var24 = 0; var24 < var6.b.length; ++var24) {
            var8 = var6.a(var24, var28);

            for(var10 = 0; var10 < var7; ++var10) {
               var23[var10] = var8[var10];
            }

            if ((var10 = a(var0, var23, var1.parentNode.gameStage)) >= 0) {
               double var29 = b((Integer)V.get(var1.parentNode), var10, var21);
               var6.b[var24] = var29;
            }
         }

         return var6;
      }
   }

   public static HandRange a(int var0, GameState var1, card[] var2, HandRange var3) {
      new GameState(G);
      HandRange var4;
      if (AnalysisPanel.getTreeStage() == 0) {
         (var4 = i()).h();
      } else {
         var4 = b(P[var0]);
      }

      var4.a(var2);
      if (var3 != null) {
         var4 = var4.b(var3);
      }

      int var21;
      int var5 = var21 = AnalysisPanel.o();
      if (var1.parentNode.gameStage == 1) {
         var5 += 3;
      } else if (var1.parentNode.gameStage == 2) {
         var5 += 4;
      } else if (var1.parentNode.gameStage == 3) {
         var5 += 5;
      }

      card[] var6 = new card[var21];

      for(int var7 = 0; var7 < var6.length; ++var7) {
         var6[var7] = new card(1, 1);
      }

      card[] var22 = new card[var5];

      int var8;
      for(var8 = var21; var8 < var5; ++var8) {
         var22[var8] = var2[var8 - var21];
      }

      var8 = c(var1.parentNode);
      int[] var19 = a(var1.parentNode);
      var5 = -1;
      int var9 = var1.nodeType;
      int var10 = 0;
      int[] var17 = var19;
      int var12 = var19.length;

      for(int var11 = 0; var11 < var12; ++var11) {
         if (var17[var11] == var9) {
            var5 = var10;
            break;
         }

         ++var10;
      }

      for(int var20 = 0; var20 < var4.a.length; ++var20) {
         if (var4.a[var20] > 0.0D) {
            card[] var23 = var4.a(var20, var6);

            for(var12 = 0; var12 < var21; ++var12) {
               var22[var12] = var23[var12];
            }

            if ((var12 = a(var0, var22, var1.parentNode.gameStage)) < 0) {
               var4.a[var20] = 0.0D;
            } else {
               double var24 = c(var8, var12, var5);
               var4.a[var20] = var24;
            }
         }
      }

      return var4;
   }

   private static HandRange a(int var0, Stack var1, HandRange var2, GameState var3, card[] var4, card[] var5) {
      while(!var1.isEmpty()) {
         int var6 = (Integer)var1.pop();
         int var7 = AnalysisPanel.o();
         if (var3.firstPlayerToAct == var0) {
            int[] var8 = a(var3);
            int var9 = -1;
            int var10 = 0;
            int[] var13 = var8;
            int var12 = var8.length;

            for(int var11 = 0; var11 < var12; ++var11) {
               if (var13[var11] == var6) {
                  var9 = var10;
                  break;
               }

               ++var10;
            }

            int var19 = var7;
            if (var3.gameStage == 1) {
               var19 = var7 + 3;
            } else if (var3.gameStage == 2) {
               var19 = var7 + 4;
            } else if (var3.gameStage == 3) {
               var19 = var7 + 5;
            }

            card[] var21 = new card[var19];

            for(var12 = var7; var12 < var19 && var12 < var4.length; ++var12) {
               var21[var12] = var4[var12];
            }

            var12 = c(var3);

            for(int var22 = 0; var22 < var2.a.length; ++var22) {
               if (var2.a[var22] > 0.0D) {
                  card[] var20 = var2.a(var22, var5);

                  for(var10 = 0; var10 < var7; ++var10) {
                     var21[var10] = var20[var10];
                  }

                  if ((var10 = a(var0, var21, var3.gameStage)) < 0) {
                     var2.a[var22] = 0.0D;
                  } else {
                     double var17 = c(var12, var10, var9);
                     double[] var10002 = var2.a;
                     var10002[var22] *= var17;
                  }
               }
            }
         }

         var3 = b(var3, var6);
         var4 = var4;
         var3 = var3;
         var2 = var2;
         var1 = var1;
         var0 = var0;
      }

      return var2;
   }

   public static double d(int var0, int var1) {
      double[] var2 = avg[var0];
      var0 = z[var0];
      double var4 = 0.0D;

      for(int var6 = var1; var6 < var2.length; var6 += var0) {
         var4 += var2[var6];
      }

      double var8 = 0.0D;

      for(var1 = var1; var1 < var2.length; ++var1) {
         var8 += var2[var1];
      }

      return var8 > 0.0D ? var4 / var8 : 1.0D / (double)var0;
   }

   public static JTable b(final GameState var0) {
      int[] var1 = a(var0);
      int var2 = c(var0);
      String[] var3;
      (var3 = new String[var1.length + 1])[0] = "Board";

      for(int var4 = 1; var4 < var3.length; ++var4) {
         var3[var4] = solver.BetType.c(var1[var4 - 1]);
      }

      final CustomTableModel var8 = new CustomTableModel(var3, 0);
      (new Thread(() -> {
         int[] var22 = solver.MainTabbedPane.f();
         solver.card.b(solver.MainTabbedPane.k());
         if (var0.gameStage == 1) {
            List var24;
            if (AnalysisPanel.gameType == 3) {
               var24 = solver.CardArrays.dShortdeck(new card[0]);
            } else {
               var24 = solver.CardArrays.d(new card[0]);
            }

            int var27 = 0;
            Object[][] var28 = new Object[var24.size()][var3.length];
            double[] var23 = new double[var1.length];

            int[] var29;
            if (AnalysisPanel.gameType == 3) {
               var29 = solver.CardArrays.eShortdeck();
            } else {
               var29 = solver.CardArrays.e();
            }

            ArrayList var30 = new ArrayList();

            Iterator var16;
            int var25;
            List tmp;
            if (AnalysisPanel.gameType == 3) {
               tmp = solver.CardArrays.generateShortDeckFlops();
            } else {
               tmp = solver.CardArrays.generateFullDeckFlops();
            }

            for(var16 = tmp.iterator(); var16.hasNext(); ++var27) {
               card[] var15 = (card[])var16.next();
               var28[var27][0] = new ComparableCardArray(var15);
               var25 = var29[var27];
               String[] var10 = new String[]{var15[0].toString(), var15[1].toString(), var15[2].toString()};
               if (var30.size() >= 4) {
                  label70:
                  while(true) {
                     Iterator var12 = var30.iterator();

                     while(var12.hasNext()) {
                        Thread var11;
                        if (!(var11 = (Thread)var12.next()).isAlive()) {
                           var30.remove(var11);
                           break label70;
                        }
                     }

                     Thread.yield();
                  }
               }

               Object[] var13 = var28[var27];

               double var251 = (double)var25;

               Thread var26;
               (var26 = new Thread(() -> {
                  int[] var10x;
                  int var14;
                  int var151;
                  double[] var10000;
                  int[] var19;
                  card[] var20;
                  int var21;
                  int var48;
                  card[] var60;
                  if (AnalysisPanel.isHoldem()) {
                     /*var10 = var10;
                     var0 = var0;
                     var22 = var22;*/
                     int var7;
                     double[] var8x = new double[var7 = a(var0).length];
                     double[] var9 = new double[var7];
                     var10x = new int[4];
                     collections.LongIntHashMap var12 = null;
                     HandRange var11;
                     if (G.gameStage > 0) {
                        var12 = n[var0.firstPlayerToAct];
                        var11 = new HandRange(P[var0.firstPlayerToAct]);
                     } else {
                        (var11 = new HandRange()).h();
                     }

                     int var13x = c(var0);
                     if ((var14 = var0.gameStage) == 0) {
                        var151 = 2;
                     } else if (var14 == 1) {
                        var151 = 5;
                     } else if (var14 == 2) {
                        var151 = 6;
                     } else {
                        var151 = 7;
                     }

                     card[] var161 = new card[var151 - 2];

                     for(int var17 = 0; var17 < var151 - 2; ++var17) {
                        var161[var17] = solver.card.a(var10[var17]);
                     }

                     var60 = new card[7];

                     for(int var18 = 0; var18 < 7; ++var18) {
                        var60[var18] = new card(1, 1);
                     }

                     card[] var61 = combine2CardsWithArray(new card[]{new card(1, 1), new card(1, 1)}, var161);
                     var19 = new int[3];
                     if (G.gameStage == 0 && var161.length > 0) {
                        var20 = CardCombinations.optimizeSuits(var161);
                        var19[0] = var61.length > 2 ? flopTexture.a(CardCombinations.getCardArrayNumValue(var20, 3)) : 0;
                        var19[1] = var61.length > 5 ? turnTexture.a(CardCombinations.getCardArrayNumValue(var20, 4)) : 0;
                        var19[2] = var61.length > 6 ? riverTexture.a(CardCombinations.getCardArrayNumValue(var20, 5)) : 0;
                     }

                     double[] var63 = null;
                     if (EquitySortComparator.activeFilter != null) {
                        System.lineSeparator();
                        var63 = EquitySortComparator.activeFilter.a;
                     }

                     for(var21 = 0; var21 < 1326; ++var21) {
                        if (var11.a[var21] > 1.0E-7D && (var63 == null || var63[var21] > 1.0E-7D)) {
                           var11.a(var21, var61);
                           if (!solver.Equity.cardInArray(var61[0], var161) && !solver.Equity.cardInArray(var61[1], var161)) {
                              var48 = a(var22, var9, var11.a[var21], var0, var61, var60, var10x, var12, var19);
                              double var34 = var9[0];
                              b(var13x, var48, var9);

                              for(int var22x = 0; var22x < var7; ++var22x) {
                                 double var37 = var34 * var9[var22x];
                                 var8x[var22x] += var37;
                              }
                           }
                        }
                     }

                     var10000 = var8x;
                  } else {
                     //var10 = var10;
                     //var0 = var0;
                     int var55 = a(var0).length;
                     var10x = new int[4];
                     int[] var56 = new int[4];
                     int[] var57 = new int[13];
                     double[] var58 = OmahaHandRange.getDefaultRange().a;
                     collections.LongIntHashMap var51 = null;
                     if (G.gameStage > 0) {
                        var51 = n[var0.firstPlayerToAct];
                        var58 = P[var0.firstPlayerToAct];
                     }

                     int var52 = c(var0);
                     int numHoleCards4 = AnalysisPanel.is5Card() ? 5 : 4;
                     if ((var14 = var0.gameStage) == 0) {
                        var151 = numHoleCards4;
                     } else if (var14 == 1) {
                        var151 = numHoleCards4 + 3;
                     } else if (var14 == 2) {
                        var151 = numHoleCards4 + 4;
                     } else {
                        var151 = numHoleCards4 + 5;
                     }

                     int var59;
                     if (G.gameStage == 0) {
                        var59 = numHoleCards4;
                     } else if (G.gameStage == 1) {
                        var59 = numHoleCards4 + 3;
                     } else if (G.gameStage == 2) {
                        var59 = numHoleCards4 + 4;
                     } else {
                        var59 = numHoleCards4 + 5;
                     }

                     var60 = new card[var151];
                     int[] var62 = new int[10];
                     var19 = new int[var151];

                     int var64;
                     for(var64 = 0; var64 < var19.length; ++var64) {
                        var19[var64] = -1;
                     }

                     for(var64 = 0; var64 < numHoleCards4; ++var64) {
                        var60[var64] = new card(1, 1);
                     }

                     var20 = new card[var151 - numHoleCards4];

                     for(var21 = 0; var21 < var151 - numHoleCards4; ++var21) {
                        var60[var21 + numHoleCards4] = solver.card.a(var10[var21]);
                        var20[var21] = solver.card.a(var10[var21]);
                        var19[var21 + numHoleCards4] = var60[var21 + numHoleCards4].b();
                     }

                     int[] var65 = new int[3];
                     if (G.gameStage == 0 && var20.length > 0) {
                        card[] var49 = CardCombinations.optimizeSuits(var20);
                        var65[0] = var20.length > 0 ? flopTexture.a(CardCombinations.getCardArrayNumValue(var49, 3)) : 0;
                        var65[1] = var20.length > 3 ? turnTexture.a(CardCombinations.getCardArrayNumValue(var49, 4)) : 0;
                        var65[2] = var20.length > 4 ? riverTexture.a(CardCombinations.getCardArrayNumValue(var49, 5)) : 0;
                     }

                     double[] var50 = null;
                     if (EquitySortComparator.activeFilter != null) {
                        System.lineSeparator();
                        var50 = EquitySortComparator.activeFilter.a;
                     }

                     double[] var67 = new double[var55];
                     double[] var35 = new double[var55];
                     int[] var66;
                     int[] var68 = new int[(var66 = new int[var22.length]).length];
                     int var38 = a(var66, var68, var22, var0.firstPlayerToAct);
                     var66 = Arrays.copyOf(var66, var38);
                     int numHoleCards2 = AnalysisPanel.is5Card() ? 5 : 4;
                     int comboCount2 = AnalysisPanel.is5Card() ? 2598960 : 270725;

                     label195:
                     for(int var45 = 0; var45 < comboCount2; ++var45) {
                        if (var58[var45] > 1.0E-7D && (var50 == null || var50[var45] > 1.0E-7D)) {
                           var64 = AnalysisPanel.is5Card() ? OmahaHandRange.i5c[var45] : OmahaHandRange.i[var45];

                           int var23x;
                           for(var23x = 0; var23x < numHoleCards2; ++var23x) {
                              var19[var23x] = var64 % 52;

                              for(int var42 = var59; var42 < var151; ++var42) {
                                 if (var19[var23x] == var19[var42]) {
                                    continue label195;
                                 }
                              }

                              var64 /= 52;
                           }

                           var23x = a(var66, var68, var67, var58[var45], var0, var19, var60, var65, var51, var14, var62, var57, var56, var10x);
                           double var69 = var67[0];
                           b(var52, var23x, var67);

                           for(var64 = 0; var64 < var55; ++var64) {
                              var35[var64] += var69 * var67[var64];
                           }
                        }
                     }

                     var10000 = var35;
                  }

                  double[] var46 = var10000;
                  double var54 = 0.0D;

                  for(int var47 = 0; var47 < var46.length; ++var47) {
                     var54 += var46[var47];
                  }

                  synchronized(var23) {
                     var48 = 1;

                     while(true) {
                        if (var48 >= var13.length) {
                           break;
                        }

                        String var53 = a(var46[var48 - 1] / var54);
                        var13[var48] = new RangeButtonListener(var53);
                        var23[var48 - 1] += var251 * var46[var48 - 1];
                        ++var48;
                     }
                  }

                  SwingUtilities.invokeLater(() -> {
                     var8.addRow(var13);
                  });
               })).start();

               var30.add(var26);
            }

            var16 = var30.iterator();

            while(var16.hasNext()) {
               Thread var31 = (Thread)var16.next();

               try {
                  var31.join();
               } catch (InterruptedException var17) {
                  var17.printStackTrace();
               }
            }

            double var32 = 0.0D;

            for(var25 = 0; var25 < var23.length; ++var25) {
               var32 += var23[var25];
            }

            for(var25 = 1; var25 < var3.length; ++var25) {
               var3[var25] = var3[var25] + " (" + a(var23[var25 - 1] / var32) + ")";
            }

            var8.setColumnIdentifiers(var3);
         } else {
            card[] var19;

            List var20;
            if (AnalysisPanel.gameType == 3) {
               var20 = solver.CardArrays.getAllPossibleArraysPlus1ShortDeck((card[])Arrays.copyOfRange(var19 = solver.card.b(solver.MainTabbedPane.k()), 0, var19.length - 1));
            } else {
               var20 = solver.CardArrays.getAllPossibleArraysPlus1FullDeck((card[])Arrays.copyOfRange(var19 = solver.card.b(solver.MainTabbedPane.k()), 0, var19.length - 1));
            }


            ArrayList var5;
            (var5 = new ArrayList()).addAll(solver.MainTabbedPane.enteredBoard);
            Object[][] var6 = new Object[var20.size()][var3.length];
            int var7 = 0;

            for(Iterator var8x = var20.iterator(); var8x.hasNext(); ++var7) {
               card var21 = (card)var8x.next();
               var5.remove(var5.size() - 1);
               var5.add(var21.toString());
               Object[] var9 = a((GameStateRefreshTask)null, (GameState)var0, (ArrayList)var5).a;
               var6[var7][0] = new ComparableCardArray(new card[]{var21});

               for(int var18 = 1; var18 < var6[var7].length; ++var18) {
                  var6[var7][var18] = new RangeButtonListener((String)var9[var18 - 1]);
               }

               int var7_1 = var7;
               SwingUtilities.invokeLater(() -> {
                  var8.addRow(var6[var7_1]);
               });
            }

         }
      })).start();
      JTable var5;
      (var5 = new JTable(var8)).setDefaultRenderer(ComparableCardArray.class, new CustomCellRenderer());
      var5.getTableHeader().setDefaultRenderer(ThemeManager.createTableHeaderRenderer());
      var5.getTableHeader().setOpaque(true);
      var5.getTableHeader().setBackground(ThemeManager.TABLE_HEADER_BG);
      var5.getTableHeader().setForeground(ThemeManager.TEXT_PRIMARY);
      var5.setRowHeight((int)(25.0F * solver.PokerSolverMain.c));
      TableRowSorter var6 = new TableRowSorter(var8);
      var5.setRowSorter(var6);
      var5.setRowSelectionAllowed(true);
      ArrayList var7;
      (var7 = new ArrayList()).add(new SortKey(0, SortOrder.ASCENDING));
      var6.setSortKeys(var7);
      var6.sort();
      return var5;
   }

   private static ArrayList a(GameState var0, ArrayList var1) {
      ArrayList var2 = new ArrayList();
      double[] var3 = null;
      collections.LongIntHashMap var5 = null;
      card[][] var4;
      if (G.gameStage > 0) {
         var3 = O[var0.firstPlayerToAct];
         var4 = N[var0.firstPlayerToAct];
         var5 = n[var0.firstPlayerToAct];
      } else {
         List var6;
         if (AnalysisPanel.isHoldem()) {
            if (AnalysisPanel.gameType == 0) {
               var6 = solver.CardArrays.getStartingHandsListHoldem();
            } else {
               var6 = solver.CardArrays.getStartingHandsListHoldemShortdeck();
            }
         } else {
            var6 = solver.CardArrays.getStartingHandsListOmaha();
         }

         var4 = new card[var6.size()][];
         var4 = (card[][])var6.toArray(var4);
      }

      card[] var29 = new card[var1.size()];
      int var7 = 0;

      for(Iterator var8 = var1.iterator(); var8.hasNext(); ++var7) {
         String var25 = (String)var8.next();
         var29[var7] = solver.card.a(var25);
      }

      if (G.gameStage == 0 && var29.length > 2) {
         CardCombinations.b(var29);
      }

      int var26 = a(var0).length;
      int var31 = -1;
      card[] var30 = new card[7];

      for(int var9 = 0; var9 < 7; ++var9) {
         var30[var9] = new card(0, 0);
      }

      int[] var32 = new int[4];
      int[] var10 = solver.MainTabbedPane.f();
      card[][] var13 = var4;
      int var12 = var4.length;

      for(int var11 = 0; var11 < var12; ++var11) {
         card[] var27 = var13[var11];
         ++var31;
         card[] var14;
         (var14 = new card[2])[0] = new card(var27[0]);
         var14[1] = new card(var27[1]);
         double var18 = 0.0D;
         double[] var15 = null;
         if (var0.gameStage == 0) {
            var32 = a(new card[]{var27[0], var27[1]}, var30);
            var18 = a(var10, 1.0D, var0.firstPlayerToAct, var32);
            var15 = c(new GameState(var0), var32[0]);
         } else {
            long var23;
            if (var0.gameStage == 1) {
               if (var27[0].cardEquals(var29[0]) || var27[0].cardEquals(var29[1]) || var27[0].cardEquals(var29[2]) || var27[1].cardEquals(var29[0]) || var27[1].cardEquals(var29[1]) || var27[1].cardEquals(var29[2])) {
                  continue;
               }

               var27 = new card[]{var27[0], var27[1], var29[0], var29[1], var29[2]};
               if (G.gameStage > 0) {
                  var23 = (new CardCombinations(var27)).a();
                  var32[1] = var5.a(var23);
                  var18 = a(var10, var3[var31], var0.firstPlayerToAct, var32);
                  var15 = c(new GameState(var0), var5.a(var23));
               } else {
                  var32 = a(var27, var30);
                  var18 = a(var10, 1.0D, var0.firstPlayerToAct, var32);
                  var15 = c(new GameState(var0), var32[1]);
               }
            } else if (var0.gameStage == 2) {
               if (var27[0].cardEquals(var29[0]) || var27[0].cardEquals(var29[1]) || var27[0].cardEquals(var29[2]) || var27[0].cardEquals(var29[3]) || var27[1].cardEquals(var29[0]) || var27[1].cardEquals(var29[1]) || var27[1].cardEquals(var29[2]) || var27[1].cardEquals(var29[3])) {
                  continue;
               }

               var27 = new card[]{var27[0], var27[1], var29[0], var29[1], var29[2], var29[3]};
               if (G.gameStage > 0) {
                  var23 = (new CardCombinations(var27)).a();
                  var32[1] = var5.a(var23 / 100L);
                  var32[2] = var5.a(var23);
                  var18 = a(var10, var3[var31], var0.firstPlayerToAct, var32);
                  var15 = c(new GameState(var0), var5.a(var23));
               } else {
                  var32 = a(var27, var30);
                  var18 = a(var10, 1.0D, var0.firstPlayerToAct, var32);
                  var15 = c(new GameState(var0), var32[2]);
               }
            } else if (var0.gameStage == 3) {
               if (var27[0].cardEquals(var29[0]) || var27[0].cardEquals(var29[1]) || var27[0].cardEquals(var29[2]) || var27[0].cardEquals(var29[3]) || var27[0].cardEquals(var29[4]) || var27[1].cardEquals(var29[0]) || var27[1].cardEquals(var29[1]) || var27[1].cardEquals(var29[2]) || var27[1].cardEquals(var29[3]) || var27[1].cardEquals(var29[4])) {
                  continue;
               }

               var27 = new card[]{var27[0], var27[1], var29[0], var29[1], var29[2], var29[3], var29[4]};
               if (G.gameStage > 0) {
                  var23 = (new CardCombinations(var27)).a();
                  var32[1] = var5.a(var23 / 100L / 100L);
                  var32[2] = var5.a(var23 / 100L);
                  var32[3] = var5.a(var23);
                  var18 = a(var10, var3[var31], var0.firstPlayerToAct, var32);
                  var15 = c(new GameState(var0), var5.a(var23));
               } else {
                  var32 = a(var27, var30);
                  var18 = a(var10, 1.0D, var0.firstPlayerToAct, var32);
                  var15 = c(new GameState(var0), var32[3]);
               }
            }
         }

         if (var18 >= 1.0E-4D) {
            PlayerHandStatistic[] var28 = new PlayerHandStatistic[var26];

            for(int var33 = 0; var33 < var26; ++var33) {
               if (var15[var33] > 0.001D) {
                  var28[var33] = new PlayerHandStatistic(var18, var14, (int)(10000.0D * var15[var33]), 0);
               }
            }

            var2.add(var28);
         }
      }

      return var2;
   }

   public static BucketGenerator a(GameStateRefreshTask var0, GameState var1, ArrayList var2) {
      PlayerHandStatistic.a = var1;
      if (!AnalysisPanel.isHoldem()) {
         return b(var0, var1, var2);
      } else {
         int[] var3;
         int var8;
         int var80;
         double[] var98;

         int startingHandsNum = 1326;
         //if (AnalysisPanel.gameType == 3) { startingHandsNum = 630; }

         if (EquitySortComparator.d != null && EquitySortComparator.d.c(var1.gameStage) > 0) {
            ArrayList var78 = var2;
            GameState var77 = var1;
            GameStateRefreshTask var105 = var0;
            var80 = a(var1).length;
            var3 = solver.MainTabbedPane.f();
            int[] var83 = new int[4];
            collections.LongIntHashMap var85 = null;
            HandRange var84;
            if (G.gameStage > 0) {
               var85 = n[var1.firstPlayerToAct];
               var84 = new HandRange(P[var1.firstPlayerToAct]);
            } else {
               (var84 = new HandRange()).h();
            }

            int var86 = c(var1);
            double var50 = 0.0D;
            byte var87;
            if ((var8 = var1.gameStage) == 0) {
               var87 = 2;
            } else if (var8 == 1) {
               var87 = 5;
            } else if (var8 == 2) {
               var87 = 6;
            } else {
               var87 = 7;
            }

            card[] var88 = new card[var87 - 2];

            for(int var10 = 0; var10 < var87 - 2; ++var10) {
               var88[var10] = solver.card.a((String)var78.get(var10));
            }

            card[] var90 = new card[7];

            for(int var79 = 0; var79 < 7; ++var79) {
               var90[var79] = new card(1, 1);
            }

            card[] var81 = combine2CardsWithArray(new card[]{new card(1, 1), new card(1, 1)}, var88);
            int[] var89 = new int[3];
            if (G.gameStage == 0 && var88.length > 0) {
               card[] var91 = CardCombinations.optimizeSuits(var88);
               var89[0] = var81.length > 2 ? flopTexture.a(CardCombinations.getCardArrayNumValue(var91, 3)) : 0;
               var89[1] = var81.length > 5 ? turnTexture.a(CardCombinations.getCardArrayNumValue(var91, 4)) : 0;
               var89[2] = var81.length > 6 ? riverTexture.a(CardCombinations.getCardArrayNumValue(var91, 5)) : 0;
            }

            double[] var92 = null;
            if (EquitySortComparator.activeFilter != null) {
               System.lineSeparator();
               var92 = EquitySortComparator.activeFilter.a;
            }

            double[] var95 = new double[var80];
            double[] var97 = new double[var80];
            var98 = new double[var80];
            double var63 = 0.0D;
            ViewSettingsManager var65;
            (var65 = EquitySortComparator.d).a(var80);

            for(int var66 = 0; var66 < startingHandsNum; ++var66) {
               boolean var93 = false;
               if (var84.a[var66] > 1.0E-7D) {
                  if (var92 != null && var92[var66] <= 1.0E-7D) {
                     var93 = true;
                  }

                  if (var105 != null && var105.a) {
                     return null;
                  }

                  var84.a(var66, var81);
                  if (!solver.Equity.cardInArray(var81[0], var88) && !solver.Equity.cardInArray(var81[1], var88)) {
                     if (var105 != null && var105.a) {
                        return null;
                     }

                     HandFilterQuery var67 = var65.a(HandFilterParser.m, var66, var88);
                     int var68 = a(var3, var95, var84.a[var66], var77, var81, var90, var83, var85, var89);
                     double var69 = var95[0];
                     b(var86, var68, var95);
                     if (!var93) {
                        var67.a += var69;
                        var50 += var69;

                        for(int var71 = 0; var71 < var80; ++var71) {
                           double var72;
                           if ((var72 = var69 * var95[var71]) > 0.0D) {
                              var67.a(var71, var72);
                              if (a(var86, var68, var98)) {
                                 double var74 = var72 + 1.0E-10D;
                                 var67.b(var71, var98[var71] * var74);
                                 var67.c(var71, var74);
                              }

                              var97[var71] += var72;
                           }
                        }
                     }

                     var63 += var69;
                  }
               }
            }

            solver.MainTabbedPane.a(var50 / var63);
            return a(var105, var65, var97);
         } else {
            var3 = solver.MainTabbedPane.f();
            int var4 = a(var1).length;
            int[] var5 = new int[4];
            collections.LongIntHashMap var7 = null;
            HandRange var6;
            if (G.gameStage > 0) {
               var7 = n[var1.firstPlayerToAct];
               var6 = new HandRange(P[var1.firstPlayerToAct]);
            } else {
               (var6 = new HandRange()).h();
            }

            var8 = c(var1);
            double var9 = 0.0D;
            int var11;
            byte var12;
            if ((var11 = var1.gameStage) == 0) {
               var12 = 2;
            } else if (var11 == 1) {
               var12 = 5;
            } else if (var11 == 2) {
               var12 = 6;
            } else {
               var12 = 7;
            }

            int var13Size = Math.min(var12 - 2, var2.size());
            card[] var13 = new card[var13Size];

            for(int var14 = 0; var14 < var13Size; ++var14) {
               var13[var14] = solver.card.a((String)var2.get(var14));
            }

            card[] var96 = new card[7];

            for(var80 = 0; var80 < 7; ++var80) {
               var96[var80] = new card(1, 1);
            }

            card[] var82 = combine2CardsWithArray(new card[]{new card(1, 1), new card(1, 1)}, var13);
            int[] var94 = new int[3];
            if (G.gameStage == 0 && var13.length > 0) {
               card[] var15 = CardCombinations.optimizeSuits(var13);
               var94[0] = var82.length > 2 ? flopTexture.a(CardCombinations.getCardArrayNumValue(var15, 3)) : 0;
               var94[1] = var82.length > 5 ? turnTexture.a(CardCombinations.getCardArrayNumValue(var15, 4)) : 0;
               var94[2] = var82.length > 6 ? riverTexture.a(CardCombinations.getCardArrayNumValue(var15, 5)) : 0;
            }

            var98 = null;
            if (EquitySortComparator.activeFilter != null) {
               System.lineSeparator();
               var98 = EquitySortComparator.activeFilter.a;
            }

            double[] var16 = new double[var4];
            double[] var17 = new double[var4];
            double[] var19 = new double[var4];
            double var22 = 0.0D;
            ArrayList[] var20 = new ArrayList[var4];

            for(int var21 = 0; var21 < var4; ++var21) {
               var20[var21] = new ArrayList();
            }

            HashMap var100 = new HashMap();

            for(int var24 = 0; var24 < startingHandsNum; ++var24) {
               boolean var18 = false;
               if (var6.a[var24] > 1.0E-7D) {
                  if (var98 != null && var98[var24] <= 1.0E-7D) {
                     var18 = true;
                  }

                  if (var0 != null && var0.a) {
                     return null;
                  }

                  var6.a(var24, var82);
                  if (!solver.Equity.cardInArray(var82[0], var13) && !solver.Equity.cardInArray(var82[1], var13)) {
                     if (var0 != null && var0.a) {
                        return null;
                     }

                     CardCombinations var25;
                     long var28 = (var25 = new CardCombinations(var82)).a();
                     card[] var27 = solver.card.b(var11 == 0 ? var25.toString() : var25.b);
                     int var102 = a(var3, var16, var6.a[var24], var1, var82, var96, var5, var7, var94);
                     double var33 = var16[0];
                     b(var8, var102, var16);
                     if (!var18) {
                        var9 += var33;
                        if (!var100.containsKey(var28)) {
                           var100.put(var28, new OmahaEquityGenerator(var4));
                        }

                        OmahaEquityGenerator var99 = (OmahaEquityGenerator)var100.get(var28);

                        for(int var26 = 0; var26 < var4; ++var26) {
                           double var37 = var33 * var16[var26];
                           if (var99.a[var26] == null) {
                              var99.a[var26] = new PlayerHandStatistic(0.0D, var27, 0, var102);
                              var20[var26].add(var99.a[var26]);
                           }

                           if (var37 > 0.0D) {
                              var99.a(var26, var37);
                              var19[var26] += var37;
                           }

                           if (a(var8, var102, var17)) {
                              double var39 = var37 + 1.0E-10D;
                              var99.b(var26, var17[var26] * var39);
                              var99.c(var26, var39);
                           }
                        }
                     }

                     var22 += var33;
                  }
               }
            }

            solver.MainTabbedPane.a(var9 / var22);
            Iterator var103 = var100.values().iterator();

            while(var103.hasNext()) {
               ((OmahaEquityGenerator)var103.next()).a();
            }

            a(var19);
            Object[][] var101 = new Object[var4][];
            String[] var104 = new String[var4];

            for(int var108 = 0; var108 < var4; ++var108) {
               var104[var108] = a(var19[var108]);
               if (GameSettings.l) {
                  var20[var108].removeIf((var0x) -> {
                     return (double)((PlayerHandStatistic)var0x).c < 10000.0D * GameSettings.k;
                  });
               }

               var101[var108] = new Object[var20[var108].size()];
               Collections.sort(var20[var108], PlayerHandStatistic.c());
               int var29 = 0;

               PlayerHandStatistic var106;
               for(Iterator var107 = var20[var108].iterator(); var107.hasNext(); var101[var108][var29++] = var106) {
                  var106 = (PlayerHandStatistic)var107.next();
               }
            }

            return new BucketGenerator(var104, var101);
         }
      }
   }

   private static BucketGenerator a(GameStateRefreshTask var0, ViewSettingsManager var1, double[] var2) {
      int var3 = var2.length;
      double var4 = 0.0D;
      double[] var10 = var2;
      int var9 = var2.length;

      int var8;
      for(var8 = 0; var8 < var9; ++var8) {
         double var6 = var10[var8];
         var4 += var6;
      }

      var1.c();
      Object[][] var13 = new Object[var3][];
      String[] var7 = new String[var3];

      label53:
      for(var8 = 0; var8 < var3; ++var8) {
         if (var0 != null && var0.a) {
            return null;
         }

         var7[var8] = a(var2[var8] / var4);
         ArrayList var14 = var1.b(var8);
         int var15 = 0;
         Iterator var12 = var14.iterator();

         while(true) {
            HandFilterQuery var11;
            do {
               if (!var12.hasNext()) {
                  var13[var8] = new Object[var15];
                  var15 = 0;
                  var12 = var14.iterator();

                  while(true) {
                     do {
                        if (!var12.hasNext()) {
                           continue label53;
                        }

                        var11 = (HandFilterQuery)var12.next();
                     } while(GameSettings.l && var11.b[var8] <= 0.01D);

                     var13[var8][var15] = var11;
                     ++var15;
                  }
               }

               var11 = (HandFilterQuery)var12.next();
            } while(GameSettings.l && var11.b[var8] <= 0.01D);

            ++var15;
         }
      }

      return new BucketGenerator(var7, var13);
   }

   private static int a(int[] var0, int[] var1, double[] var2, double var3, GameState var5, int[] var6, card[] var7, int[] var8, collections.LongIntHashMap var9, int var10, int[] var11, int[] var12, int[] var13, int[] var14) {
      if (G.gameStage == 0) {
         int var17;
         int numHoleCards = AnalysisPanel.is5Card() ? 5 : 4;
         for(var17 = 0; var17 < numHoleCards; ++var17) {
            var7[var17].rank = var6[var17] % 13 + 2;
            if (var7[var17].rank == 14) {
               var7[var17].rank = 1;
            }

            var7[var17].suit = var6[var17] / 13;
         }

         if (var10 >= 3) {
            var17 = AnalysisPanel.is5Card() ? handeval.PloHandEvaluator.b5c(var6) : handeval.PloHandEvaluator.b(var6);
            if (AnalysisPanel.gameType == 2) {
               var17 |= handeval.PloHandEvaluator.a(var6, var12) << 16;
            }
         } else {
            var17 = 0;
         }

         var14 = a(var14, var8, var6, var11, var13, var17, var7.length);
         var2[0] = a(var0, var1, 1.0D, var14);
         return var14[var10];
      } else {
         int numHoleCards = AnalysisPanel.is5Card() ? 5 : 4;
         if (var10 == 1) {
            int flopLen = numHoleCards + 3;
            var14[1] = var9.a(AnalysisPanel.is5Card() ? OmahaHandNormalizer.b5c(var6, var11, flopLen, var13, isoLevel) : OmahaHandNormalizer.b(var6, var11, 7, var13, isoLevel));
         } else {
            long var15;
            int turnLen = numHoleCards + 4;
            int riverLen = numHoleCards + 5;
            if (var10 == 2) {
               var15 = AnalysisPanel.is5Card() ? OmahaHandNormalizer.b5c(var6, var11, turnLen, var13, isoLevel) : OmahaHandNormalizer.b(var6, var11, 8, var13, isoLevel);
               var14[2] = var9.a(var15);
               var14[1] = var9.a(var15 / 100L);
            } else if (var10 == 3) {
               var15 = AnalysisPanel.is5Card() ? OmahaHandNormalizer.b5c(var6, var11, turnLen, var13, isoLevel) : OmahaHandNormalizer.b(var6, var11, 8, var13, isoLevel);
               var14[2] = var9.a(var15);
               var14[1] = var9.a(var15 / 100L);
               var14[3] = var9.a(AnalysisPanel.is5Card() ? OmahaHandNormalizer.b5c(var6, var11, riverLen, var13, isoLevel) : OmahaHandNormalizer.b(var6, var11, 9, var13, isoLevel));
            }
         }

         var2[0] = a(var0, var1, var3, var14);
         return var14[var10];
      }
   }

   private static int a(int[] var0, double[] var1, double var2, GameState var4, card[] var5, card[] var6, int[] var7, collections.LongIntHashMap var8, int[] var9) {
      int var14 = var4.gameStage;
      if (G.gameStage == 0) {
         var7 = a(var5, var9);
      } else {
         long var12 = (new CardCombinations(var5)).a();
         if (var14 == 1) {
            var7[1] = var8.a(var12);
         } else if (var14 == 2) {
            var7[1] = var8.a(var12 / 100L);
            var7[2] = var8.a(var12);
         } else {
            var7[1] = var8.a(var12 / 100L / 100L);
            var7[2] = var8.a(var12 / 100L);
            var7[3] = var8.a(var12);
         }
      }

      var1[0] = a(var0, G.gameStage == 0 ? 1.0D : var2, var4.firstPlayerToAct, var7);
      return var7[var14];
   }

   private static synchronized BucketGenerator b(GameStateRefreshTask var0, GameState var1, ArrayList var2) {
      int[] var6;
      int var10;
      double[] var85;
      int var86;
      if (EquitySortComparator.d != null && EquitySortComparator.d.c(var1.gameStage) > 0) {
         var2 = var2;
         var1 = var1;
         var0 = var0;
         var86 = a(var1).length;
         int[] var88 = new int[4];
         int numHoleCards5 = AnalysisPanel.is5Card() ? 5 : 4;
         int[] var89 = new int[numHoleCards5];
         var6 = new int[13];
         double[] var90 = OmahaHandRange.getDefaultRange().a;
         collections.LongIntHashMap var91 = null;
         if (G.gameStage > 0) {
            var91 = n[var1.firstPlayerToAct];
            var90 = P[var1.firstPlayerToAct];
         }

         int var92 = c(var1);
         double var50 = 0.0D;
         int var95;
         if ((var10 = var1.gameStage) == 0) {
            var95 = numHoleCards5;
         } else if (var10 == 1) {
            var95 = numHoleCards5 + 3;
         } else if (var10 == 2) {
            var95 = numHoleCards5 + 4;
         } else {
            var95 = numHoleCards5 + 5;
         }

         int var94;
         if (G.gameStage == 0) {
            var94 = numHoleCards5;
         } else if (G.gameStage == 1) {
            var94 = numHoleCards5 + 3;
         } else if (G.gameStage == 2) {
            var94 = numHoleCards5 + 4;
         } else {
            var94 = numHoleCards5 + 5;
         }

         card[] var96 = new card[var95];
         int[] var14 = new int[10];
         int[] var97 = new int[var95];

         int var98;
         for(var98 = 0; var98 < var97.length; ++var98) {
            var97[var98] = -1;
         }

         for(var98 = 0; var98 < numHoleCards5; ++var98) {
            var96[var98] = new card(1, 1);
         }

         card[] var100 = new card[var95 - numHoleCards5];

         for(int var101 = 0; var101 < var95 - numHoleCards5; ++var101) {
            var96[var101 + numHoleCards5] = solver.card.a((String)var2.get(var101));
            var100[var101] = solver.card.a((String)var2.get(var101));
            var97[var101 + numHoleCards5] = var96[var101 + numHoleCards5].b();
         }

         int[] var103 = new int[3];
         if (G.gameStage == 0 && var100.length > 0) {
            card[] var87 = CardCombinations.optimizeSuits(var100);
            var103[0] = var100.length > 0 ? flopTexture.a(CardCombinations.getCardArrayNumValue(var87, 3)) : 0;
            var103[1] = var100.length > 3 ? turnTexture.a(CardCombinations.getCardArrayNumValue(var87, 4)) : 0;
            var103[2] = var100.length > 4 ? riverTexture.a(CardCombinations.getCardArrayNumValue(var87, 5)) : 0;
         }

         var85 = null;
         if (EquitySortComparator.activeFilter != null) {
            System.lineSeparator();
            var85 = EquitySortComparator.activeFilter.a;
         }

         double[] var99 = new double[var86];
         double[] var102 = new double[var86];
         double[] var64 = new double[var86];
         int[] var65 = solver.MainTabbedPane.f();
         double var66 = 0.0D;
         ViewSettingsManager var68 = EquitySortComparator.d;
         int[] var69;
         int[] var70 = new int[(var69 = new int[var65.length]).length];
         int var71 = a(var69, var70, var65, var1.firstPlayerToAct);
         var69 = Arrays.copyOf(var69, var71);
         var68.a(var86);
         int numHoleCards3 = AnalysisPanel.is5Card() ? 5 : 4;
         int comboCount3 = AnalysisPanel.is5Card() ? 2598960 : 270725;

         label327:
         for(int var72 = 0; var72 < comboCount3; ++var72) {
            boolean var104 = false;
            if (var90[var72] > 1.0E-7D) {
               if (var85 != null && var85[var72] <= 1.0E-7D) {
                  var104 = true;
               }

               if (var0 != null && var0.a) {
                  return null;
               }

               int var73 = AnalysisPanel.is5Card() ? OmahaHandRange.i5c[var72] : OmahaHandRange.i[var72];

               int var75;
               for(int var74 = 0; var74 < numHoleCards3; ++var74) {
                  var97[var74] = var73 % 52;

                  for(var75 = var94; var75 < var95; ++var75) {
                     if (var97[var74] == var97[var75]) {
                        continue label327;
                     }
                  }

                  var73 /= 52;
               }

               HandFilterQuery var120 = var68.a(HandFilterParser.m, var72, var100);
               if ((var75 = a(var69, var70, var99, var90[var72], var1, var97, var96, var103, var91, var10, var14, var6, var89, var88)) < 0) {
                  System.lineSeparator();
               } else {
                  double var76 = var99[0];
                  b(var92, var75, var99);
                  if (!var104) {
                     var120.a += var76;
                     var50 += var76;

                     for(int var78 = 0; var78 < var86; ++var78) {
                        double var79;
                        if ((var79 = var76 * var99[var78]) > 0.0D) {
                           var120.a(var78, var79);
                           var64[var78] += var79;
                        }

                        if (a(var92, var75, var102)) {
                           double var81 = var79 + 1.0E-10D;
                           var120.b(var78, var102[var78] * var81);
                           var120.c(var78, var81);
                        }
                     }
                  }

                  var66 += var76;
               }
            }
         }

         solver.MainTabbedPane.a(var50 / var66);
         return a(var0, var68, var64);
      } else {
         int[] var3 = solver.MainTabbedPane.f();
         int var4 = isoLevel;
         int var5 = a(var1).length;
         int numHoleCards = AnalysisPanel.is5Card() ? 5 : 4;
         int comboCount = AnalysisPanel.is5Card() ? 2598960 : 270725;
         var6 = new int[numHoleCards];
         int[] var7 = new int[numHoleCards];
         int[] var8 = new int[13];
         HandStatisticCollection.d();
         HandStatisticCollection[] var9 = new HandStatisticCollection[var5];

         for(var10 = 0; var10 < var5; ++var10) {
            var9[var10] = new HandStatisticCollection();
         }

         double[] var93 = OmahaHandRange.getDefaultRange().a;
         collections.LongIntHashMap var11 = null;
         if (G.gameStage > 0) {
            var11 = n[var1.firstPlayerToAct];
            var93 = P[var1.firstPlayerToAct];
         }

         ae.clear();
         af.clear();
         int var12 = c(var1);
         double var13 = 0.0D;
         int var15;
         int var16;
         if ((var15 = var1.gameStage) == 0) {
            var16 = numHoleCards;
         } else if (var15 == 1) {
            var16 = numHoleCards + 3;
         } else if (var15 == 2) {
            var16 = numHoleCards + 4;
         } else {
            var16 = numHoleCards + 5;
         }

         int var17;
         if (G.gameStage == 0) {
            var17 = numHoleCards;
         } else if (G.gameStage == 1) {
            var17 = numHoleCards + 3;
         } else if (G.gameStage == 2) {
            var17 = numHoleCards + 4;
         } else {
            var17 = numHoleCards + 5;
         }

         card[] var18 = new card[var16];
         int[] var19 = new int[10];
         int[] var20 = new int[var16];

         int var21;
         for(var21 = 0; var21 < var20.length; ++var21) {
            var20[var21] = -1;
         }

         for(var21 = 0; var21 < numHoleCards; ++var21) {
            var18[var21] = new card(1, 1);
         }

         card[] var105 = new card[var16 - numHoleCards];

         for(int var22 = 0; var22 < var16 - numHoleCards; ++var22) {
            var18[var22 + numHoleCards] = solver.card.a((String)var2.get(var22));
            var105[var22] = solver.card.a((String)var2.get(var22));
            var20[var22 + numHoleCards] = var18[var22 + numHoleCards].b();
         }

         int[] var106 = new int[3];
         if (G.gameStage == 0 && var105.length > 0) {
            var105 = CardCombinations.optimizeSuits(var105);
            var106[0] = var18.length > 4 ? flopTexture.a(CardCombinations.getCardArrayNumValue(var105, 3)) : 0;
            var106[1] = var18.length > 7 ? turnTexture.a(CardCombinations.getCardArrayNumValue(var105, 4)) : 0;
            var106[2] = var18.length > 8 ? riverTexture.a(CardCombinations.getCardArrayNumValue(var105, 5)) : 0;
         }

         var85 = null;
         if (EquitySortComparator.activeFilter != null) {
            System.lineSeparator();
            var85 = EquitySortComparator.activeFilter.a;
         }

         double[] var107 = new double[var5];
         double[] var24 = new double[var5];
         double var27 = 0.0D;

         int var29;
         int var30;
         int var31;
         int var32;
         int var33;
         double var109;
         double var115;
         label449:
         for(var29 = 0; var29 < comboCount; ++var29) {
            boolean var23 = false;
            if (var93[var29] > 1.0E-7D) {
               if (var85 != null && var85[var29] <= 1.0E-7D) {
                  var23 = true;
               }

               if (var0 != null && var0.a) {
                  return null;
               }

               var30 = AnalysisPanel.is5Card() ? OmahaHandRange.i5c[var29] : OmahaHandRange.i[var29];

               for(var31 = 0; var31 < numHoleCards; ++var31) {
                  var20[var31] = var30 % 52;

                  for(var32 = var17; var32 < var16; ++var32) {
                     if (var20[var31] == var20[var32]) {
                        continue label449;
                     }
                  }

                  var30 /= 52;
               }

               if (G.gameStage == 0) {
                  for(var33 = 0; var33 < numHoleCards; ++var33) {
                     var18[var33].rank = var20[var33] % 13 + 2;
                     if (var18[var33].rank == 14) {
                        var18[var33].rank = 1;
                     }

                     var18[var33].suit = var20[var33] / 13;
                  }

                  if (var15 >= 3) {
                     var33 = AnalysisPanel.is5Card() ? handeval.PloHandEvaluator.b5c(var20) : handeval.PloHandEvaluator.b(var20);
                     if (AnalysisPanel.gameType == 2) {
                        var33 |= handeval.PloHandEvaluator.a(var20, var8) << 16;
                     }
                  } else {
                     var33 = 0;
                  }

                  var6 = a(var6, var106, var20, var19, var7, var33, var18.length);
               } else if (var15 == 1) {
                  int flopLen = numHoleCards + 3;
                  var6[1] = var11.a(AnalysisPanel.is5Card() ? OmahaHandNormalizer.b5c(var20, var19, flopLen, var7, isoLevel) : OmahaHandNormalizer.b(var20, var19, flopLen, var7, isoLevel));
               } else {
                  long var113;
                  int turnLen = numHoleCards + 4;
                  int riverLen = numHoleCards + 5;
                  if (var15 == 2) {
                     var113 = AnalysisPanel.is5Card() ? OmahaHandNormalizer.b5c(var20, var19, turnLen, var7, isoLevel) : OmahaHandNormalizer.b(var20, var19, turnLen, var7, isoLevel);
                     var6[2] = var11.a(var113);
                     var6[1] = var11.a(var113 / 100L);
                  } else if (var15 == 3) {
                     var113 = AnalysisPanel.is5Card() ? OmahaHandNormalizer.b5c(var20, var19, turnLen, var7, isoLevel) : OmahaHandNormalizer.b(var20, var19, turnLen, var7, isoLevel);
                     var6[2] = var11.a(var113);
                     var6[1] = var11.a(var113 / 100L);
                     var6[3] = var11.a(AnalysisPanel.is5Card() ? OmahaHandNormalizer.b5c(var20, var19, riverLen, var7, isoLevel) : OmahaHandNormalizer.b(var20, var19, riverLen, var7, isoLevel));
                  }
               }

               var109 = a(var3, 1.0D, var1.firstPlayerToAct, var6);
               var115 = GameSettings.b ? 1.0D : var93[var29];
               if (!var23) {
                  var13 += var109 * var115;
                  long var35 = AnalysisPanel.is5Card() ? OmahaHandNormalizer.a5c(var20, var19, var16, var7, var4) : OmahaHandNormalizer.a(var20, var19, var16, var7, var4);
                  af.adjustOrPutValue(var35, var109 * var115, var109 * var115);
               }

               var27 += var109 * var115;
            }
         }

         System.lineSeparator();
         ag.clear();

         label421:
         for(var29 = 0; var29 < comboCount; ++var29) {
            if (var93[var29] > 1.0E-7D && (var85 == null || var85[var29] > 1.0E-7D)) {
               if (var0 != null && var0.a) {
                  return null;
               }

               var30 = AnalysisPanel.is5Card() ? OmahaHandRange.i5c[var29] : OmahaHandRange.i[var29];

               for(var31 = 0; var31 < numHoleCards; ++var31) {
                  var20[var31] = var30 % 52;

                  for(var32 = var17; var32 < var16; ++var32) {
                     if (var20[var31] == var20[var32]) {
                        continue label421;
                     }
                  }

                  var30 /= 52;
               }

               long var110 = AnalysisPanel.is5Card() ? OmahaHandNormalizer.a5c(var20, var19, var16, var7, var4) : OmahaHandNormalizer.a(var20, var19, var16, var7, var4);
               if (ag.a(var110) && (var115 = af.get(var110)) / var13 >= GameSettings.j) {
                  int var36;
                  int var114;
                  if (G.gameStage == 0) {
                     for(var36 = 0; var36 < numHoleCards; ++var36) {
                        var18[var36].rank = var20[var36] % 13 + 2;
                        if (var18[var36].rank == 14) {
                           var18[var36].rank = 1;
                        }

                        var18[var36].suit = var20[var36] / 13;
                     }

                     if (var15 >= 3) {
                        var36 = AnalysisPanel.is5Card() ? handeval.PloHandEvaluator.b5c(var20) : handeval.PloHandEvaluator.b(var20);
                        if (AnalysisPanel.gameType == 2) {
                           var36 |= handeval.PloHandEvaluator.a(var20, var8) << 16;
                        }
                     } else {
                        var36 = 0;
                     }

                     var114 = a(var6, var106, var20, var19, var7, var36, var18.length)[var15];
                  } else {
                     var114 = var11.a(AnalysisPanel.is5Card() ? OmahaHandNormalizer.b5c(var20, var19, var16, var7, isoLevel) : OmahaHandNormalizer.b(var20, var19, var16, var7, isoLevel));
                  }

                  if (var114 >= 0) {
                     b(var12, var114, var107);

                     for(var36 = 0; var36 < var5; ++var36) {
                        var24[var36] += var107[var36] * var115;
                     }

                     if (var15 > 0) {
                        long var118 = var110;

                        for(int var84 = numHoleCards - 1; var84 >= 0; --var84) {
                           var86 = (int)(var118 % 100L) - 1;
                           var18[var84].rank = var86 % 13 + 2;
                           if (var18[var84].rank == 14) {
                              var18[var84].rank = 1;
                           }

                           var18[var84].suit = var86 / 13;
                           var118 /= 100L;
                        }
                     }

                     for(var36 = 0; var36 < var5; ++var36) {
                        if (!GameSettings.l || var107[var36] >= GameSettings.k) {
                           var9[var36].a(var115, var18, (int)(10000.0D * var107[var36]), var114, b(var12, var114, var36));
                        }
                     }
                  }
               }
            }
         }

         System.lineSeparator();
         solver.MainTabbedPane.a(var13 / var27);
         double var108 = 0.0D;
         double[] var116 = var24;

         for(var33 = 0; var33 < var5; ++var33) {
            var109 = var116[var33];
            var108 += var109;
         }

         Object[][] var111 = new Object[var5][];
         String[] var112 = new String[var5];

         for(var33 = 0; var33 < var5; ++var33) {
            if (var0 != null && var0.a) {
               return null;
            }

            var112[var33] = a(var24[var33] / var108);
            var111[var33] = new Object[var9[var33].a()];
            var9[var33].b();
            int var34 = 0;

            PlayerHandStatistic var117;
            for(Iterator var119 = var9[var33].iterator(); var119.hasNext(); var111[var33][var34++] = var117) {
               var117 = (PlayerHandStatistic)var119.next();
            }
         }

         System.lineSeparator();
         return new BucketGenerator(var112, var111);
      }
   }

   public static String a(double var0) {
      if (var0 < 0.0D) {
         return solver.HashUtil.u(new char[0]);
      } else {
         String var2;
         if ((var2 = solver.HashUtil.v(new char[0]) + Math.round(var0 * 1000.0D)).length() == 1) {
            var2 = "0" + var2;
         }

         return var2.substring(0, var2.length() - 1) + "." + var2.charAt(var2.length() - 1) + "%";
      }
   }

   public static String a(double var0, int var2) {
      return String.format("%." + var2 + "g%n", var0);
   }

   public static int c(GameState var0) {
      return (Integer)V.get(var0);
   }

   public static double[] c(GameState var0, int var1) {
      return e((Integer)V.get(var0), var1);
   }

   private static double c(int var0, int var1, int var2) {
      if (avg[var0] == null) {
         return d(var0, var1, var2);
      } else {
         double[] var3 = avg[var0];
         var0 = z[var0];
         if (var1 < 0 || var2 < 0) {
            return 1.0D / (double)var0;
         }
         var1 *= var0;
         double var5 = 0.0D;

         for(int var4 = 0; var4 < var0; ++var4) {
            if (var1 + var4 >= 0 && var1 + var4 < var3.length) {
               var5 += var3[var1 + var4];
            }
         }

         return var5 > 0.0D && var1 + var2 >= 0 && var1 + var2 < var3.length ? var3[var1 + var2] / var5 : 1.0D / (double)var0;
      }
   }

   private static double[] e(int var0, int var1) {
      if (avg[var0] == null) {
         return f(var0, var1);
      } else {
         int var2;
         double[] var3 = new double[var2 = z[var0]];
         double var4;
         if ((var4 = d(var0, var1, var3)) <= 0.0D) {
            for(var0 = 0; var0 < var2; ++var0) {
               var3[var0] = 1.0D / (double)var2;
            }

            return var3;
         } else {
            for(var0 = 0; var0 < var2; ++var0) {
               var3[var0] /= var4;
            }

            return var3;
         }
      }
   }

   public static double b(int var0, int var1, int var2) {
      if (a != null) {
         return a.b(var0, var1, z[var0], var2);
      } else if (hasEV[UnsafeMemoryStorage.a((long)var0)]) {
         double[] var12 = cfrTables[UnsafeMemoryStorage.a((long)var0)][var1];
         int var3 = z[var0];
         var0 = UnsafeMemoryStorage.b((long)var0);
         double var6 = var12[var0 + var2];
         double var8;
         if ((var8 = var12[var0 + var3]) == 0.0D) {
            return Double.NEGATIVE_INFINITY;
         } else {
            double var10 = var12[var0 + var3 + 1];
            return (var6 + var10) / var8;
         }
      } else {
         return Double.NEGATIVE_INFINITY;
      }
   }

   public static boolean a(int var0, int var1, double[] var2) {
      if (a != null) {
         return a.b(var0, var1, var2);
      } else if (!hasEV[UnsafeMemoryStorage.a((long)var0)]) {
         return false;
      } else {
         double[] var10 = cfrTables[UnsafeMemoryStorage.a((long)var0)][var1];
         int var3 = z[var0];
         var0 = UnsafeMemoryStorage.b((long)var0);
         double var6;
         if ((var6 = var10[var0 + var3]) < 1.0E-11D) {
            return false;
         } else {
            double var8 = var10[var0 + var3 + 1];

            for(int var4 = 0; var4 < var3; ++var4) {
               var2[var4] = (var10[var0 + var4] + var8) / var6;
            }

            return true;
         }
      }
   }

   public static double[] b(int var0, int var1, double[] var2) {
      if (avg[var0] == null) {
         c(var0, var1, var2);
         return var2;
      } else {
         double var3 = d(var0, var1, var2);
         var0 = z[var0];
         if (var3 <= 0.0D) {
            double var8 = 1.0D / (double)var0;

            for(var1 = 0; var1 < var0; ++var1) {
               var2[var1] = var8;
            }

            return var2;
         } else {
            for(int var6 = 0; var6 < var0; ++var6) {
               var2[var6] /= var3;
            }

            return var2;
         }
      }
   }

   private static double[] f(int var0, int var1) {
      double[] var2 = new double[z[var0]];
      c(var0, var1, var2);
      return var2;
   }

   private static double d(int var0, int var1, int var2) {
      if (a != null) {
         return a.a(var0, var1, z[var0], var2);
      } else {
         double[] var3 = cfrTables[UnsafeMemoryStorage.a((long)var0)][var1];
         int var4 = z[var0];
         int var5 = UnsafeMemoryStorage.b((long)var0);
         var1 *= var4;
         double[] var14;
         if ((var14 = UnsafeMemoryStorage.a(var0)) != null && var14[var1 + var2] >= 0.0D) {
            return var14[var1 + var2];
         } else {
            double var8 = 0.0D;
            double var10 = 1.0D;
            int var6 = var4;
            synchronized(var3) {
               for(int var12 = 0; var12 < var4; ++var12) {
                  if (var14 != null && var14[var1 + var12] >= 0.0D) {
                     --var6;
                     var10 -= var14[var1 + var12];
                  } else if (var3[var5 + var12] > 0.0D) {
                     var8 += var3[var5 + var12];
                  }
               }

               if (var8 > 0.0D) {
                  if (var3[var5 + var2] > 0.0D) {
                     return var3[var5 + var2] * var10 / var8;
                  } else {
                     return 0.0D;
                  }
               } else {
                  return var10 / (double)var6;
               }
            }
         }
      }
   }

   public static final void c(int var0, int var1, double[] var2) {
      if (a != null) {
         a.a(var0, var1, var2);
      } else {
         int var3 = var2.length;
         int var4 = UnsafeMemoryStorage.b((long)var0);
         double var5 = 0.0D;
         double var7 = 1.0D;
         double[] var9 = cfrTables[UnsafeMemoryStorage.a((long)var0)][var1];
         double[] var15 = UnsafeMemoryStorage.a(var0);
         int var10 = var3;
         var1 *= var3;

         for(int var13 = 0; var13 < var3; ++var13) {
            if (var15 != null && var15[var1 + var13] >= 0.0D) {
               var7 -= var15[var1 + var13];
               --var10;
               var2[var13] = Double.NEGATIVE_INFINITY;
            } else {
               var2[var13] = var9[var4 + var13];
            }

            if (var2[var13] > 0.0D) {
               var5 += var2[var13];
            }
         }

         double var16;
         if (var5 > 0.0D) {
            var16 = var7 / var5;

            for(var4 = 0; var4 < var3; ++var4) {
               if (var2[var4] > 0.0D) {
                  var2[var4] *= var16;
               } else if (var2[var4] != Double.NEGATIVE_INFINITY) {
                  var2[var4] = 0.0D;
               } else {
                  var2[var4] = var15[var1 + var4];
               }
            }

         } else {
            var16 = var7 / (double)var10;

            for(var4 = 0; var4 < var3; ++var4) {
               if (var2[var4] != Double.NEGATIVE_INFINITY) {
                  var2[var4] = var16;
               } else {
                  var2[var4] = var15[var1 + var4];
               }
            }

         }
      }
   }

   private static final double d(int var0, int var1, double[] var2) {
      double[] var6 = avg[var0];
      double var4 = 0.0D;

      for(int var3 = 0; var3 < var2.length; ++var3) {
         var2[var3] = var6[var1 * var2.length + var3];
         var4 += var2[var3];
      }

      return var4;
   }

   public static File c() {
      if (ah == null) {
         ah = new util.AppFile("savedRuns");
      }

      if (!ah.exists()) {
         ah.mkdirs();
      }

      return ah;
   }

   public static void a(File var0) {
      ah = var0;
   }

   public static Thread a(File var0, int var1) {
      solver.MainTabbedPane.m.setIcon(solver.MainTabbedPane.d);
      boolean var2 = solver.SolverRunner.stopCalculation();
      Thread var3;
      (var3 = new Thread(new CalcSaver(var0, var1, var2))).start();
      return var3;
   }

   public static void b(File var0) {
      h = var0;
      if (!solver.MainTabbedPane.b) {
         if (var0 == null) {
            solver.MainTabbedPane.k.setTitle("Squid v2 [New run]");
            return;
         }

         solver.MainTabbedPane.k.setTitle("Squid v2 [" + var0.getAbsolutePath() + "]");
      }

   }

   public static void a(double[][] var0) {
      try {
         for(int var1 = 0; var1 < var0.length; ++var1) {
            if (var0[var1] != null) {
               int var2 = z[var1];
               double[] var3 = var0[var1];

               for(int var4 = 0; var4 < var3.length; ++var4) {
                  boolean var10000;
                  label33: {
                     int var5 = var4 / var2;
                     int var7 = var4 % var2;
                     if (cfrTables != null) {
                        double[] var6 = cfrTables[UnsafeMemoryStorage.a((long)var1)][var5];
                        var5 = UnsafeMemoryStorage.b((long)var1);
                        if (var6[var5 + var7] == Double.NEGATIVE_INFINITY) {
                           var10000 = true;
                           break label33;
                        }
                     }

                     var10000 = false;
                  }

                  if (!var10000) {
                     var3[var4] = -1.0D;
                  }
               }

               UnsafeMemoryStorage.a(var1, var3);
            }
         }

      } catch (Exception var8) {
      }
   }

   public static Thread b(File var0, int var1) {
      if (!var0.exists()) {
         return null;
      } else {
         if (solver.MainTabbedPane.l != null) {
            solver.MainTabbedPane.l.setIcon(solver.MainTabbedPane.d);
         }

         Thread var2;
         (var2 = new Thread(new CalcReader(var0, var1))).start();
         return var2;
      }
   }
}

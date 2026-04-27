package solver;

import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TLongDoubleHashMap;
import gnu.trove.map.hash.TLongIntHashMap;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipFile;

public final class Equity {
   private static volatile long a;

   static {
      new Random();
   }

   public static collections.LongIntHashMap a(gnu.trove.map.S_ref var0) {
      if (var0 == null) {
         return null;
      } else {
         collections.LongIntHashMap var1 = new collections.LongIntHashMap(var0.size());
         gnu.trove.c_ref.Z_ref var2 = var0.iterator();

         while(var2.hasNext()) {
            var2.advance();
            var1.a(var2.key(), var2.value());
         }

         return var1;
      }
   }

   public static final int a(int var0, int var1) {
      return var0 | 1 << var1;
   }

   public static final boolean b(int var0, int var1) {
      return (var0 & 1 << var1) != 0;
   }

   public static final long a(long var0, int var2) {
      return var0 | 1L << var2;
   }

   public static final boolean b(long var0, int var2) {
      return (var0 & 1L << var2) != 0L;
   }

   public static void a(EquityChartPanel var0, List var1, card[] var2) {
      int var3 = var1.size();
      ArrayList var4 = new ArrayList();
      ArrayList var5 = new ArrayList();
      ArrayList var6 = new ArrayList();
      ArrayList var7 = new ArrayList();
      ArrayList var8 = new ArrayList();

      int var9;
      for(var9 = 0; var9 < var1.size(); ++var9) {
         int[][] var10;
         if ((var10 = ((HandRange)var1.get(var9)).c()).length == 0) {
            return;
         }

         var5.add(var10);
         var4.add(((HandRange)var1.get(var9)).e());
         var6.add(((HandRange)var1.get(var9)).f());
         var7.add(new double[((int[][])var5.get(var9)).length]);
         var8.add(new double[((int[][])var5.get(var9)).length]);
      }

      int[] var42 = new int[(var9 = AnalysisPanel.o()) + 5];
      long var11 = 0L;
      int[] var13 = new int[var2.length];

      int var43;
      for(int var14 = 0; var14 < var2.length; ++var14) {
         var13[var14] = var9 == 2 ? var2[var14].getFullDeckIndex() : var2[var14].b();
         var43 = var13[var14];
         var11 |= 1L << var43;
         var42[var14 + var9] = var13[var14];
      }

      XorShiftRandomGenerator var41 = new XorShiftRandomGenerator();
      int var44 = 5 - var13.length;
      int[][] var45 = new int[var1.size()][var9];
      int[] var15 = new int[var1.size()];
      int[] var16 = new int[11];
      int[] var17 = new int[var1.size()];
      int[] var18 = null;
      if (AnalysisPanel.gameType == 2) {
         var18 = new int[var17.length];
      }

      for(int var22 = 0; var22 < var1.size(); ++var22) {
         double[] var23 = (double[])var6.get(var22);
         ArrayList var24 = new ArrayList();

         for(int var25 = 0; var25 < var23.length; ++var25) {
            var24.add(new EquityDataPoint(((int[][])var5.get(var22))[var25], 1.0D / (double)var3, var23[var25]));
         }

         ((HandRange)var1.get(var22)).e = var24;
      }

      long var48 = (var48 = (long)(var9 == 2 ? 500 : 1000)) * (long)var1.size();
      long var49 = (long)(var9 == 2 ? 1000 * var1.size() : 15000 * var1.size());
      a = System.currentTimeMillis();
      long var26 = var11;

      for(var9 = 0; var9 < 500000000 && !var0.d; ++var9) {
         if (System.currentTimeMillis() - a > var48) {
            if ((var48 += var48 / 2L) > var49) {
               var48 = var49;
            }

            int var12;
            double[] var46;
            for(int var29 = 0; var29 < var1.size(); ++var29) {
               double[] var30 = (double[])var6.get(var29);
               double[] var31 = (double[])var7.get(var29);
               double[] var32 = (double[])var8.get(var29);
               synchronized(((HandRange)var1.get(var29)).e) {
                  var0.b[var29] = 0.0D;

                  for(var12 = 0; var12 < var30.length; ++var12) {
                     EquityDataPoint var19;
                     (var19 = (EquityDataPoint)((HandRange)var1.get(var29)).e.get(var12)).a = ((int[][])var5.get(var29))[var12];
                     if (var32[var12] > 0.0D) {
                        double var36 = var31[var12] / var32[var12];
                        var19.b = var36;
                        var19.c = var32[var12];
                     } else {
                        var19.c = var32[var12];
                        var19.b = 1.0D / (double)var3;
                     }

                     var46 = var0.b;
                     var46[var29] += var19.c;
                     var46 = var0.c;
                     var46[var29] += var19.b * var19.c;
                  }

                  Collections.sort(((HandRange)var1.get(var29)).e);
               }
            }

            double var50 = 0.0D;
            double[] var47;
            var12 = (var47 = var0.c).length;

            for(var43 = 0; var43 < var12; ++var43) {
               double var51 = var47[var43];
               var50 += var51;
            }

            for(int var52 = 0; var52 < var3; ++var52) {
               var46 = var0.c;
               var46[var52] /= var50;
            }

            a = Long.MAX_VALUE;
            (new Thread(() -> {
               var0.k = false;
               a = var0.a();
            })).start();
         }

         if (AnalysisPanel.gameType == 1 || AnalysisPanel.gameType == 4) {
            // Use b5c for 5-card PLO (gameType == 4), b for 4-card PLO (gameType == 1)
            if (AnalysisPanel.is5Card()) {
               b5c(var0, var7, var8, var3, var26, var42, var17, var4, var6, var41, var15, var45, var44);
            } else {
               b(var0, var7, var8, var3, var26, var42, var17, var4, var6, var41, var15, var45, var44);
            }
         } else if (AnalysisPanel.gameType == 2) {
            a(var0, var7, var8, var3, var26, var42, var16, var17, var18, var4, var6, var41, var15, var45, var44);
         } else {
            AnalysisPanel.p();
            a(var0, var7, var8, var3, var26, var42, var17, var4, var6, var41, var15, var45, var44);
         }
      }

   }

   private static void a(EquityChartPanel var0, List var1, List var2, int var3, long var4, int[] var6, int[] var7, List var8, List var9, XorShiftRandomGenerator var10, int[] var11, int[][] var12, int var13) {
      label76:
      while(!var0.d) {
         long var14 = var4;

         int var16;
         int var19;
         int var19tmp = 0;
         int var20;
         int[] var21;
         int var25;
         int tmp1;
         int tmp2;
         boolean acesOnBoard;
         for(var16 = 0; var16 < var3; ++var16) {
            byte[] var17 = (byte[])var8.get(var16);
            double[] var18 = (double[])var9.get(var16);

            for(var19 = var10.nextInt(var18.length); var18[var19] < 1.0D && var10.nextDouble() > var18[var19]; var19 = var10.nextInt(var18.length)) {
            }

            var20 = var19 << 1;
            if (b(var14, var17[var20]) || b(var14, var17[var20 + 1])) {
               continue label76;
            }

            var11[var16] = var19;
            (var21 = var12[var16])[0] = var17[var20];
            var21[1] = var17[var20 + 1];
            var25 = var21[0];
            long var10000 = var14 | 1L << var25;
            var25 = var21[1];
            var14 = var10000 | 1L << var25;
         }

         int var27;
         int var28;
         for(var16 = var13 - 1; var16 >= 0; --var16) {
        	 
        	if (AnalysisPanel.gameType == 0) { 
	            for(var27 = var10.nextInt(52); b(var14, var27); var27 = var10.nextInt(52)) {
	            }
        	} else {
        		for(var27 = 16 + var10.nextInt(36); b(var14, var27); var27 = 16 + var10.nextInt(36)) {
	            }
        	}

            var28 = 2 + (5 - var13) + var16;
            var6[var28] = var27;
            var25 = var6[var28];
            var14 |= 1L << var25;
         }

         var27 = Integer.MIN_VALUE;
         var28 = 1;
         var19 = handeval.tables.HandRankEvaluator.b(var6);
         
         var19tmp = 0;
         acesOnBoard = false;
         if (AnalysisPanel.gameType == 3) {
      	   for (int i = 2; i<7; i++) {
      		   if (var6[i] > 47) {
      			   acesOnBoard = true;
      			   break;            			   
      		   }
      	   }
      	   
      	   if (acesOnBoard) {
      		 var19tmp = handeval.tables.HandRankEvaluator.bA5swap(var6);
      	   }
         }

         for(var20 = 0; var20 < var3; ++var20) {
            var21 = var12[var20];
            //var7[var20] = handeval.tables.HandRankEvaluator.a(var19, var21[0], var21[1]);
            tmp1 = handeval.tables.HandRankEvaluator.a(var19, var21[0], var21[1]);
            
            if (AnalysisPanel.gameType == 3) {
          	  if (acesOnBoard) {
          		  if ( (var21[0] > 47) || (var21[1] > 47)) {
          			  tmp2 = handeval.tables.HandRankEvaluator.aA5swap(var19tmp, var21[0], var21[1]);
          		  } else {
          			  tmp2 = handeval.tables.HandRankEvaluator.a(var19tmp, var21[0], var21[1]);
          		  }                		                  		 
          	  } else {
          		  if ( (var21[0] > 47) || (var21[1] > 47)) {
          			  tmp2 = handeval.tables.HandRankEvaluator.aA5swap(var19, var21[0], var21[1]);
          		  } else {
          			  tmp2 = handeval.tables.HandRankEvaluator.a(var19, var21[0], var21[1]);
          		  }
          	  }
                
                tmp1 = Math.max(tmp1, tmp2);
                
                if ( ((tmp1 >= 5863) && (tmp1 < 7140)) || (tmp1 >= 7296) ) {
              	  tmp1 += 2000;                	  
                }
            }
            
            var7[var20] = tmp1;                                               
            
            if (var7[var20] > var27) {
               var27 = var7[var20];
               var28 = 1;
            } else if (var7[var20] == var27) {
               ++var28;
            }
         }

         for(var20 = 0; var20 < var7.length; ++var20) {
            double var29 = var7[var20] == var27 ? 1.0D / (double)var28 : 0.0D;
            double[] var26 = (double[])var1.get(var20);
            var26[var11[var20]] += var29;
            int var10002 = (int) ((double[])var2.get(var20))[var11[var20]]++;
         }

         ++var0.e;
         return;
      }

   }

   private static void a(EquityChartPanel var0, List var1, List var2, int var3, long var4, int[] var6, int[] var7, int[] var8, int[] var9, List var10, List var11, XorShiftRandomGenerator var12, int[] var13, int[][] var14, int var15) {
      label105:
      while(!var0.d) {
         long var16 = var4;

         int var18;
         int var21;
         int var22;
         int var30;
         for(var18 = 0; var18 < var3; ++var18) {
            byte[] var19 = (byte[])var10.get(var18);
            double[] var20 = (double[])var11.get(var18);

            for(var21 = var12.nextInt(var20.length); var20[var21] < 1.0D && var12.nextDouble() > var20[var21]; var21 = var12.nextInt(var20.length)) {
            }

            var22 = var21 << 2;
            if (b(var16, var19[var22]) || b(var16, var19[var22 + 1]) || b(var16, var19[var22 + 2]) || b(var16, var19[var22 + 3])) {
               continue label105;
            }

            var13[var18] = var21;
            int[] var23;
            (var23 = var14[var18])[0] = var19[var22];
            var23[1] = var19[var22 + 1];
            var23[2] = var19[var22 + 2];
            var23[3] = var19[var22 + 3];
            var30 = var23[0];
            long var10000 = var16 | 1L << var30;
            var30 = var23[1];
            var10000 |= 1L << var30;
            var30 = var23[2];
            var10000 |= 1L << var30;
            var30 = var23[3];
            var16 = var10000 | 1L << var30;
         }

         int var33;
         int var34;
         for(var18 = var15 - 1; var18 >= 0; --var18) {
            for(var33 = var12.nextInt(52); b(var16, var33); var33 = var12.nextInt(52)) {
            }

            var34 = 4 + (5 - var15) + var18;
            var6[var34] = var33;
            var30 = var6[var34];
            var16 |= 1L << var30;
         }

         var33 = 32767;
         var34 = 0;
         var21 = Integer.MIN_VALUE;
         var22 = 1;

         int var35;
         for(var35 = 0; var35 < var3; ++var35) {
            int[] var24 = var14[var35];
            var6[0] = var24[0];
            var6[1] = var24[1];
            var6[2] = var24[2];
            var6[3] = var24[3];
            var8[var35] = var6.length == 7 ? handeval.tables.HandRankEvaluator.a(var6) : -handeval.PloHandEvaluator.b(var6);
            if (var8[var35] > var21) {
               var21 = var8[var35];
               var22 = 1;
            } else if (var8[var35] == var21) {
               ++var22;
            }

            var9[var35] = handeval.PloHandEvaluator.a(var6, var7);
            if (var9[var35] < var33) {
               var33 = var9[var35];
               var34 = 1;
            } else if (var9[var35] == var33 && var33 != 32767) {
               ++var34;
            }
         }

         ++var0.e;

         for(var35 = 0; var35 < var8.length; ++var35) {
            double var36 = var8[var35] == var21 ? 1.0D / (double)var22 : 0.0D;
            if (var34 > 0) {
               double var10001 = var9[var35] == var33 ? 1.0D / (double)var34 : 0.0D;
               double var31 = var36 + (var9[var35] == var33 ? 1.0D / (double)var34 : 0.0D);
               var10001 += var36;
               var36 = var31 / 2.0D;
            }

            double[] var32 = (double[])var1.get(var35);
            var32[var13[var35]] += var36;
            int var10002 = (int) ((double[])var2.get(var35))[var13[var35]]++;
         }

         return;
      }

   }

   private static void b(EquityChartPanel var0, List var1, List var2, int var3, long var4, int[] var6, int[] var7, List var8, List var9, XorShiftRandomGenerator var10, int[] var11, int[][] var12, int var13) {
      label81:
      while(!var0.d) {
         long var14 = var4;

         int var16;
         int var19;
         int var20;
         int[] var21;
         int var24;
         for(var16 = 0; var16 < var3; ++var16) {
            byte[] var17 = (byte[])var8.get(var16);
            double[] var18 = (double[])var9.get(var16);

            for(var19 = var10.nextInt(var18.length); var18[var19] < 1.0D && var10.nextDouble() > var18[var19]; var19 = var10.nextInt(var18.length)) {
            }

            var20 = var19 << 2;
            if (b(var14, var17[var20]) || b(var14, var17[var20 + 1]) || b(var14, var17[var20 + 2]) || b(var14, var17[var20 + 3])) {
               continue label81;
            }

            var11[var16] = var19;
            (var21 = var12[var16])[0] = var17[var20];
            var21[1] = var17[var20 + 1];
            var21[2] = var17[var20 + 2];
            var21[3] = var17[var20 + 3];
            var24 = var21[0];
            long var10000 = var14 | 1L << var24;
            var24 = var21[1];
            var10000 |= 1L << var24;
            var24 = var21[2];
            var10000 |= 1L << var24;
            var24 = var21[3];
            var14 = var10000 | 1L << var24;
         }

         int var25;
         int var27;
         for(var16 = var13 - 1; var16 >= 0; --var16) {
            for(var25 = var10.nextInt(52); b(var14, var25); var25 = var10.nextInt(52)) {
            }

            var27 = 4 + (5 - var13) + var16;
            var6[var27] = var25;
            var24 = var6[var27];
            var14 |= 1L << var24;
         }

         var25 = Integer.MIN_VALUE;
         var27 = 1;
         var19 = handeval.PloHandEvaluator.a(var6);

         for(var20 = 0; var20 < var3; ++var20) {
            var21 = var12[var20];
            var6[0] = var21[0];
            var6[1] = var21[1];
            var6[2] = var21[2];
            var6[3] = var21[3];
            var7[var20] = -handeval.PloHandEvaluator.a(var6, var19);
            if (var7[var20] > var25) {
               var25 = var7[var20];
               var27 = 1;
            } else if (var7[var20] == var25) {
               ++var27;
            }
         }

         ++var0.e;

         for(var20 = 0; var20 < var7.length; ++var20) {
            if (var7[var20] == var25) {
               double[] var26 = (double[])var1.get(var20);
               var26[var11[var20]] += 1.0D / (double)var27;
            }

            int var10002 = (int) ((double[])var2.get(var20))[var11[var20]]++;
         }

         System.lineSeparator();
         return;
      }

   }

   // 5-card version of b() for 5-card PLO equity calculations
   private static void b5c(EquityChartPanel var0, List var1, List var2, int var3, long var4, int[] var6, int[] var7, List var8, List var9, XorShiftRandomGenerator var10, int[] var11, int[][] var12, int var13) {
      label81:
      while(!var0.d) {
         long var14 = var4;

         int var16;
         int var19;
         int var20;
         int[] var21;
         int var24;
         for(var16 = 0; var16 < var3; ++var16) {
            byte[] var17 = (byte[])var8.get(var16);
            double[] var18 = (double[])var9.get(var16);

            for(var19 = var10.nextInt(var18.length); var18[var19] < 1.0D && var10.nextDouble() > var18[var19]; var19 = var10.nextInt(var18.length)) {
            }

            var20 = var19 * 5;
            if (b(var14, var17[var20]) || b(var14, var17[var20 + 1]) || b(var14, var17[var20 + 2]) || b(var14, var17[var20 + 3]) || b(var14, var17[var20 + 4])) {
               continue label81;
            }

            var11[var16] = var19;
            var21 = var12[var16];
            var21[0] = var17[var20];
            var21[1] = var17[var20 + 1];
            var21[2] = var17[var20 + 2];
            var21[3] = var17[var20 + 3];
            var21[4] = var17[var20 + 4];

            var24 = var21[0];
            long var10000 = var14 | 1L << var24;
            var24 = var21[1];
            var10000 |= 1L << var24;
            var24 = var21[2];
            var10000 |= 1L << var24;
            var24 = var21[3];
            var10000 |= 1L << var24;
            var24 = var21[4];
            var14 = var10000 | 1L << var24;
         }

         int var25;
         int var27;
         for(var16 = var13 - 1; var16 >= 0; --var16) {
            for(var25 = var10.nextInt(52); b(var14, var25); var25 = var10.nextInt(52)) {
            }

            var27 = 5 + (5 - var13) + var16;
            var6[var27] = var25;
            var24 = var6[var27];
            var14 |= 1L << var24;
         }

         var25 = Integer.MIN_VALUE;
         var27 = 1;
         var19 = handeval.PloHandEvaluator.a5c(var6);

         for(var20 = 0; var20 < var3; ++var20) {
            var21 = var12[var20];
            var6[0] = var21[0];
            var6[1] = var21[1];
            var6[2] = var21[2];
            var6[3] = var21[3];
            var6[4] = var21[4];
            var7[var20] = -handeval.PloHandEvaluator.a5c(var6, var19);
            if (var7[var20] > var25) {
               var25 = var7[var20];
               var27 = 1;
            } else if (var7[var20] == var25) {
               ++var27;
            }
         }

         ++var0.e;

         for(var20 = 0; var20 < var7.length; ++var20) {
            if (var7[var20] == var25) {
               double[] var26 = (double[])var1.get(var20);
               var26[var11[var20]] += 1.0D / (double)var27;
            }

            int var10002 = (int) ((double[])var2.get(var20))[var11[var20]]++;
         }

         System.lineSeparator();
         return;
      }

   }

   public static boolean cardInArray(card var0, card[] var1) {
      card[] var4 = var1;
      int var3 = var1.length;

      for(int var2 = 0; var2 < var3; ++var2) {
         card var5 = var4[var2];
         if (var0.cardEquals(var5)) {
            return true;
         }
      }

      return false;
   }

   public static void generate2EquityShortdeck(boolean isShortDeck) throws Throwable {
      EquityTableCache.a();
      TLongDoubleHashMap var1 = new TLongDoubleHashMap(14661258, 1.0F);
      TLongDoubleHashMap var2 = new TLongDoubleHashMap(3635295, 1.0F);
      TLongDoubleHashMap var3 = new TLongDoubleHashMap(418133, 1.0F);
      TLongDoubleHashMap var4 = new TLongDoubleHashMap(170, 1.0F);
      card[] var5 = new card[7];

      for(int var6 = 0; var6 < 7; ++var6) {
         var5[var6] = new card(0, 0);
      }

      int[] var42 = new int[4];
      card[] var7 = new card[7];
      Iterator var9 = CardArrays.getStartingHandsListHoldemShortdeck().iterator();

      int count = 0;
      while(var9.hasNext()) {
         card[] var8 = (card[])var9.next();
         System.lineSeparator();
         Long var10 = (new CardCombinations(var8, (byte)0)).a();
         double var11 = 0.0D;
         double var13 = 0.0D;
         var7[0] = var8[0];
         var7[1] = var8[1];

         double var19;
         for(Iterator var16 = CardArrays.generateWholeFlopsListShortdeck(var8).iterator(); var16.hasNext(); var11 += var19 * var19) {
            card[] var15 = (card[])var16.next();
            ++var13;
            Arrays.fill(var42, 0);
            long var17 = CardCombinations.a(new card[]{var8[0], var8[1], var15[0], var15[1], var15[2]}, var5, var42, 5);
            var19 = 0.0D;
            double var21 = 0.0D;
            double var23 = 0.0D;
            var7[2] = var15[0];
            var7[3] = var15[1];
            var7[4] = var15[2];

            double var29;
            for(Iterator var26 = CardArrays.getRestCardsShortdeck(new card[]{var8[0], var8[1], var15[0], var15[1], var15[2]}).iterator(); var26.hasNext(); var21 += var29 * var29) {
               card var25 = (card)var26.next();
               ++var23;
               Arrays.fill(var42, 0);
               long var27 = CardCombinations.a(new card[]{var8[0], var8[1], var15[0], var15[1], var15[2], var25}, var5, var42, 6);
               var29 = 0.0D;
               double var31 = 0.0D;
               double var33 = 0.0D;
               var7[5] = var25;

               double var35;
               for(Iterator var37 = CardArrays.getRestCardsShortdeck(new card[]{var8[0], var8[1], var15[0], var15[1], var15[2], var25}).iterator(); var37.hasNext(); var31 += var35 * var35) {
                  var25 = (card)var37.next();
                  ++var33;
                  var7[6] = var25;
                  long var39 = CardCombinations.a(var7, var42);
                  var35 = EquityTableCache.e.get(var39);
                  var29 += var35;
               }

               var29 /= var33;
               var31 /= var33;
               var2.put(var27, var31);
               var19 += var29;
            }

            var19 /= var23;
            var21 /= var23;
            var3.put(var17, var21);
         }

         var11 /= var13;
         var4.put(var10, var11);

         count++;
         //System.out.println(count);
         MainTabbedPane.a("Generating equity2SD",count,630);
      }

      util.AppFile var43 = isShortDeck ? new util.AppFile("HoldemSixPlus") : new util.AppFile();

      try {
         a((File)(new util.AppFile(var43, "riverequity2SD")), (Object)var1);
         a((File)(new util.AppFile(var43, "turnequity2SD")), (Object)var2);
         a((File)(new util.AppFile(var43, "flopequity2SD")), (Object)var3);
         a((File)(new util.AppFile(var43, "preflopequity2SD")), (Object)var4);
      } catch (Exception var41) {
      }
   }

   public static void generate2Equity(boolean isShortDeck) throws Throwable {
      EquityTableCache.a();
      TLongDoubleHashMap var1 = new TLongDoubleHashMap(14661258, 1.0F);
      TLongDoubleHashMap var2 = new TLongDoubleHashMap(3635295, 1.0F);
      TLongDoubleHashMap var3 = new TLongDoubleHashMap(418133, 1.0F);
      TLongDoubleHashMap var4 = new TLongDoubleHashMap(170, 1.0F);
      card[] var5 = new card[7];

      for(int var6 = 0; var6 < 7; ++var6) {
         var5[var6] = new card(0, 0);
      }

      int[] var42 = new int[4];
      card[] var7 = new card[7];
      Iterator var9 = CardArrays.getStartingHandsListHoldem().iterator();

      int count = 0;
      while(var9.hasNext()) {
         card[] var8 = (card[])var9.next();
         System.lineSeparator();
         Long var10 = (new CardCombinations(var8, (byte)0)).a();
         double var11 = 0.0D;
         double var13 = 0.0D;
         var7[0] = var8[0];
         var7[1] = var8[1];

         double var19;
         for(Iterator var16 = CardArrays.generateWholeFlopsList(var8).iterator(); var16.hasNext(); var11 += var19 * var19) {
            card[] var15 = (card[])var16.next();
            ++var13;
            Arrays.fill(var42, 0);
            long var17 = CardCombinations.a(new card[]{var8[0], var8[1], var15[0], var15[1], var15[2]}, var5, var42, 5);
            var19 = 0.0D;
            double var21 = 0.0D;
            double var23 = 0.0D;
            var7[2] = var15[0];
            var7[3] = var15[1];
            var7[4] = var15[2];

            double var29;
            for(Iterator var26 = CardArrays.getRestCardsFullDeck(new card[]{var8[0], var8[1], var15[0], var15[1], var15[2]}).iterator(); var26.hasNext(); var21 += var29 * var29) {
               card var25 = (card)var26.next();
               ++var23;
               Arrays.fill(var42, 0);
               long var27 = CardCombinations.a(new card[]{var8[0], var8[1], var15[0], var15[1], var15[2], var25}, var5, var42, 6);
               var29 = 0.0D;
               double var31 = 0.0D;
               double var33 = 0.0D;
               var7[5] = var25;

               double var35;
               for(Iterator var37 = CardArrays.getRestCardsFullDeck(new card[]{var8[0], var8[1], var15[0], var15[1], var15[2], var25}).iterator(); var37.hasNext(); var31 += var35 * var35) {
                  var25 = (card)var37.next();
                  ++var33;
                  var7[6] = var25;
                  long var39 = CardCombinations.a(var7, var42);
                  var35 = EquityTableCache.e.get(var39);
                  var29 += var35;
               }

               var29 /= var33;
               var31 /= var33;
               var2.put(var27, var31);
               var19 += var29;
            }

            var19 /= var23;
            var21 /= var23;
            var3.put(var17, var21);
         }

         var11 /= var13;
         var4.put(var10, var11);
         
         count++;
         MainTabbedPane.a("Generating equity2",count,1326);
      }

      util.AppFile var43 = isShortDeck ? new util.AppFile("HoldemSixPlus") : new util.AppFile();

      try {
         a((File)(new util.AppFile(var43, "riverequity2")), (Object)var1);
         a((File)(new util.AppFile(var43, "turnequity2")), (Object)var2);
         a((File)(new util.AppFile(var43, "flopequity2")), (Object)var3);
         a((File)(new util.AppFile(var43, "preflopequity2")), (Object)var4);
      } catch (Exception var41) {
      }
   }

   public static void a_test(boolean var0) throws Throwable {
      //e.a();
      TLongDoubleHashMap var1 = new TLongDoubleHashMap(14661258, 1.0F);
      TLongDoubleHashMap var2 = new TLongDoubleHashMap(3635295, 1.0F);
      TLongDoubleHashMap var3 = new TLongDoubleHashMap(418133, 1.0F);
      TLongDoubleHashMap var4 = new TLongDoubleHashMap(170, 1.0F);
      card[] var5 = new card[7];

      for(int var6 = 0; var6 < 7; ++var6) {
         var5[var6] = new card(0, 0);
      }

      int[] var42 = new int[4];
      card[] var7 = new card[7];
      Iterator var9 = CardArrays.getStartingHandsListHoldem().iterator();

      int curNum = 0;      
      while(var9.hasNext()) {
         card[] var8 = (card[])var9.next();
         System.lineSeparator();
         Long var10 = (new CardCombinations(var8, (byte)0)).a();
         double var11 = 0.0D;
         double var13 = 0.0D;
         var7[0] = var8[0];
         var7[1] = var8[1];

         double var19;
         for(Iterator var16 = CardArrays.generateWholeFlopsList(var8).iterator(); var16.hasNext(); var11 += var19 * var19) {
            card[] var15 = (card[])var16.next();
            ++var13;
            Arrays.fill(var42, 0);
            long var17 = CardCombinations.a(new card[]{var8[0], var8[1], var15[0], var15[1], var15[2]}, var5, var42, 5);
            var19 = 0.0D;
            double var21 = 0.0D;
            double var23 = 0.0D;
            var7[2] = var15[0];
            var7[3] = var15[1];
            var7[4] = var15[2];

            double var29;
            for(Iterator var26 = CardArrays.getRestCardsFullDeck(new card[]{var8[0], var8[1], var15[0], var15[1], var15[2]}).iterator(); var26.hasNext(); var21 += var29 * var29) {
               card var25 = (card)var26.next();
               ++var23;
               Arrays.fill(var42, 0);
               long var27 = CardCombinations.a(new card[]{var8[0], var8[1], var15[0], var15[1], var15[2], var25}, var5, var42, 6);
               var29 = 0.0D;
               double var31 = 0.0D;
               double var33 = 0.0D;
               var7[5] = var25;

               double var35;
               for(Iterator var37 = CardArrays.getRestCardsFullDeck(new card[]{var8[0], var8[1], var15[0], var15[1], var15[2], var25}).iterator(); var37.hasNext(); var31 += var35 * var35) {
                  var25 = (card)var37.next();
                  ++var33;
                  var7[6] = var25;
                  long var39 = CardCombinations.a(var7, var42);
                  var35 = EquityTableCache.e.get(var39);
                  var29 += var35;
               }

               var29 /= var33;
               var31 /= var33;
               var2.put(var27, var31);
               var19 += var29;
            }

            var19 /= var23;
            var21 /= var23;
            var3.put(var17, var21);
         }

         var11 /= var13;
         var4.put(var10, var11);
         
         
         MainTabbedPane.k.setTitle( Integer.toString(curNum) + " from 1326");
         curNum++;
      }

      util.AppFile var43 = var0 ? new util.AppFile("HoldemSixPlus") : new util.AppFile();

      try {
         a((File)(new util.AppFile(var43, "riverequity2")), (Object)var1);
         a((File)(new util.AppFile(var43, "turnequity2")), (Object)var2);
         a((File)(new util.AppFile(var43, "flopequity2")), (Object)var3);
         a((File)(new util.AppFile(var43, "preflopequity2")), (Object)var4);
      } catch (Exception var41) {
      }
   }

   public static void generate1Equity(boolean isShortDeck) throws Throwable {
      TLongDoubleHashMap var1 = new TLongDoubleHashMap();
      TLongDoubleHashMap var2 = new TLongDoubleHashMap();
      TLongDoubleHashMap var3 = new TLongDoubleHashMap();
      TLongDoubleHashMap var4 = new TLongDoubleHashMap();
      handeval.tables.HandRankEvaluator.a();
      card[] var5 = new card[7];

      for(int var6 = 0; var6 < 7; ++var6) {
         var5[var6] = new card(0, 0);
      }

      int[] var49 = new int[4];
      int[] var7 = new int[5];
      int[] var8 = new int[7];
      int[] var9 = new int[7];
      int[] var10 = new int[7];
      card[] var11 = new card[7];
      Iterator var13 = CardArrays.getStartingHandsListHoldem().iterator();

      card[] var12;
      int count = 0;
      while(var13.hasNext()) {
         var12 = (card[])var13.next();
         System.lineSeparator();
         long var14 = (new CardCombinations(var12, (byte)0)).a();
         double var16 = 0.0D;
         double var18 = 0.0D;
         var11[0] = var12[0];
         var11[1] = var12[1];

         double var24;
         for(Iterator var21 = CardArrays.generateWholeFlopsList(var12).iterator(); var21.hasNext(); var16 += var24) {
            card[] var20 = (card[])var21.next();
            ++var18;
            Arrays.fill(var49, 0);
            long var22 = CardCombinations.a(new card[]{var12[0], var12[1], var20[0], var20[1], var20[2]}, var5, var49, 5);
            var11[2] = var20[0];
            var11[3] = var20[1];
            var11[4] = var20[2];
            var24 = 0.0D;
            double var26 = 0.0D;

            double var32;
            for(Iterator var29 = CardArrays.getRestCardsFullDeck(new card[]{var12[0], var12[1], var20[0], var20[1], var20[2]}).iterator(); var29.hasNext(); var24 += var32) {
               card var28 = (card)var29.next();
               ++var26;
               Arrays.fill(var49, 0);
               long var30 = CardCombinations.a(new card[]{var12[0], var12[1], var20[0], var20[1], var20[2], var28}, var5, var49, 6);
               var11[5] = var28;
               var32 = 0.0D;
               double var34 = 0.0D;

               double var53;
               for(Iterator var37 = CardArrays.getRestCardsFullDeck(new card[]{var12[0], var12[1], var20[0], var20[1], var20[2], var28}).iterator(); var37.hasNext(); var32 += var53) {
                  card var36 = (card)var37.next();
                  ++var34;
                  var11[6] = var36;
                  long var38 = CardCombinations.a(var11, var49);
                  if (var1.containsKey(var38)) {
                     var53 = var1.get(var38);
                  } else {
                     var10[2] = var20[0].getFullDeckIndex();
                     var10[3] = var20[1].getFullDeckIndex();
                     var10[4] = var20[2].getFullDeckIndex();
                     var10[5] = var28.getFullDeckIndex();
                     var10[6] = var36.getFullDeckIndex();
                     var9[2] = var20[0].getShortdeckIndex();
                     var9[3] = var20[1].getShortdeckIndex();
                     var9[4] = var20[2].getShortdeckIndex();
                     var9[5] = var28.getShortdeckIndex();
                     var9[6] = var36.getShortdeckIndex();
                     double var42 = 0.0D;
                     double var44 = 0.0D;
                     double var46 = 0.0D;
                     Iterator var40 = CardArrays.e(new card[]{var12[0], var12[1], var20[0], var20[1], var20[2], var28, var36}).iterator();

                     while(var40.hasNext()) {
                        card[] var51 = (card[])var40.next();
                        int var41;
                        int var52;
                        if (isShortDeck) {
                           var9[0] = var51[0].getShortdeckIndex();
                           var9[1] = var51[1].getShortdeckIndex();
                           var52 = handeval.HandRankHelper.a(var9, var7, var8);
                           var9[0] = var12[0].getShortdeckIndex();
                           var9[1] = var12[1].getShortdeckIndex();
                           var41 = handeval.HandRankHelper.a(var9, var7, var8);
                        } else {
                           var10[0] = var51[0].getFullDeckIndex();
                           var10[1] = var51[1].getFullDeckIndex();
                           var52 = handeval.tables.HandRankEvaluator.a(var10);
                           var10[0] = var12[0].getFullDeckIndex();
                           var10[1] = var12[1].getFullDeckIndex();
                           var41 = handeval.tables.HandRankEvaluator.a(var10);
                        }

                        if (var41 > var52) {
                           ++var42;
                        } else if (var52 > var41) {
                           ++var44;
                        } else {
                           ++var46;
                        }
                     }

                     var53 = (var42 + var46 / 2.0D) / (var42 + var44 + var46);
                     var1.put(var38, var53);
                  }
               }

               var32 /= var34;
               if (!var2.containsKey(var30)) {
                  var2.put(var30, var32);
               }
            }

            var24 /= var26;
            if (!var3.containsKey(var22)) {
               var3.put(var22, var24);
            }
         }

         var16 /= var18;
         if (!var4.containsKey(var14)) {
            var4.put(var14, var16);
         }
         
         count++;
         MainTabbedPane.a("Generating equity",count,1326);
      }

      var13 = CardArrays.getStartingHandsListHoldem().iterator();

      while(var13.hasNext()) {
         var12 = (card[])var13.next();
         new CardCombinations(var12, (byte)0);
         System.lineSeparator();
      }

      util.AppFile var50 = isShortDeck ? new util.AppFile("HoldemSixPlus") : new util.AppFile();

      try {
         a((File)(new util.AppFile(var50, "riverequity")), (Object)var1);
         a((File)(new util.AppFile(var50, "turnequity")), (Object)var2);
         a((File)(new util.AppFile(var50, "flopequity")), (Object)var3);
         a((File)(new util.AppFile(var50, "preflopequity")), (Object)var4);
      } catch (Exception var48) {
      }
   }   
   
   public static void generate1EquityShortdeck(boolean isShortDeck) throws Throwable {
      TLongDoubleHashMap var1 = new TLongDoubleHashMap();
      TLongDoubleHashMap var2 = new TLongDoubleHashMap();
      TLongDoubleHashMap var3 = new TLongDoubleHashMap();
      TLongDoubleHashMap var4 = new TLongDoubleHashMap();
      handeval.tables.HandRankEvaluator.a();
      card[] var5 = new card[7];

      for(int var6 = 0; var6 < 7; ++var6) {
         var5[var6] = new card(0, 0);
      }

      int[] var49 = new int[4];
      int[] var7 = new int[5];
      int[] var8 = new int[7];
      int[] var9 = new int[7];
      int[] var10 = new int[7];
      int[] var10tmp = new int[7];
      card[] var11 = new card[7];
      Iterator var13 = CardArrays.getStartingHandsListHoldemShortdeck().iterator();

      card[] var12;
      int count = 0;
      while(var13.hasNext()) {
         var12 = (card[])var13.next();
         System.lineSeparator();
         long var14 = (new CardCombinations(var12, (byte)0)).a();
         double var16 = 0.0D;
         double var18 = 0.0D;
         var11[0] = var12[0];
         var11[1] = var12[1];

         double var24;
         for(Iterator var21 = CardArrays.generateWholeFlopsListShortdeck(var12).iterator(); var21.hasNext(); var16 += var24) {
            card[] var20 = (card[])var21.next();
            ++var18;
            Arrays.fill(var49, 0);
            long var22 = CardCombinations.a(new card[]{var12[0], var12[1], var20[0], var20[1], var20[2]}, var5, var49, 5);
            var11[2] = var20[0];
            var11[3] = var20[1];
            var11[4] = var20[2];
            var24 = 0.0D;
            double var26 = 0.0D;

            double var32;
            for(Iterator var29 = CardArrays.getRestCardsShortdeck(new card[]{var12[0], var12[1], var20[0], var20[1], var20[2]}).iterator(); var29.hasNext(); var24 += var32) {
               card var28 = (card)var29.next();
               ++var26;
               Arrays.fill(var49, 0);
               long var30 = CardCombinations.a(new card[]{var12[0], var12[1], var20[0], var20[1], var20[2], var28}, var5, var49, 6);
               var11[5] = var28;
               var32 = 0.0D;
               double var34 = 0.0D;

               double var53;
               for(Iterator var37 = CardArrays.getRestCardsShortdeck(new card[]{var12[0], var12[1], var20[0], var20[1], var20[2], var28}).iterator(); var37.hasNext(); var32 += var53) {
                  card var36 = (card)var37.next();
                  ++var34;
                  var11[6] = var36;
                  long var38 = CardCombinations.a(var11, var49);
                  if (var1.containsKey(var38)) {
                     var53 = var1.get(var38);
                  } else {
                     var10[2] = var20[0].getFullDeckIndex();
                     var10[3] = var20[1].getFullDeckIndex();
                     var10[4] = var20[2].getFullDeckIndex();
                     var10[5] = var28.getFullDeckIndex();
                     var10[6] = var36.getFullDeckIndex();
                     var9[2] = var20[0].getShortdeckIndex();
                     var9[3] = var20[1].getShortdeckIndex();
                     var9[4] = var20[2].getShortdeckIndex();
                     var9[5] = var28.getShortdeckIndex();
                     var9[6] = var36.getShortdeckIndex();
                     double var42 = 0.0D;
                     double var44 = 0.0D;
                     double var46 = 0.0D;
                     Iterator var40 = CardArrays.eShortdeck(new card[]{var12[0], var12[1], var20[0], var20[1], var20[2], var28, var36}).iterator();
                     

                     while(var40.hasNext()) {
                        card[] var51 = (card[])var40.next();
                        int var41;
                        int var52;
                        int tmp;
                        
                        var10[0] = var51[0].getFullDeckIndex();
                        var10[1] = var51[1].getFullDeckIndex();
                        var52 = handeval.tables.HandRankEvaluator.a(var10);                        
                        if ( ((var52 >= 5863) && (var52 < 7140)) || (var52 >= 7296) ) {
                        	var52 += 2000;                	  
		                }

                        var10[0] = var12[0].getFullDeckIndex();
                        var10[1] = var12[1].getFullDeckIndex();
                        var41 = handeval.tables.HandRankEvaluator.a(var10);
                        if ( ((var41 >= 5863) && (var41 < 7140)) || (var41 >= 7296) ) {
                        	var41 += 2000;                	  
		                }
                        
                        /*if ( (( var52 >= 5863 ) && ( var52 < 7140)) && (( var41 >= 7140 ) && ( var41 < 7296)) ) {
                        	tmp = var52;
                        	var52 = var41;
                        	var41 = tmp;
                        } else {
                        	if ( (( var41 >= 5863 ) && ( var41 < 7140)) && (( var52 >= 7140 ) && ( var52 < 7296)) ) {
                            	tmp = var52;
                            	var52 = var41;
                            	var41 = tmp;
                            }
                        }*/

                        if (var41 > var52) {
                           ++var42;
                        } else if (var52 > var41) {
                           ++var44;
                        } else {
                           ++var46;
                        }
                     }

                     var53 = (var42 + var46 / 2.0D) / (var42 + var44 + var46);
                     var1.put(var38, var53);
                  }
               }

               var32 /= var34;
               if (!var2.containsKey(var30)) {
                  var2.put(var30, var32);
               }
            }

            var24 /= var26;
            if (!var3.containsKey(var22)) {
               var3.put(var22, var24);
            }
         }

         var16 /= var18;
         if (!var4.containsKey(var14)) {
            var4.put(var14, var16);
         }
         
         count++;
         //System.out.println(count);
         MainTabbedPane.a("Generating equitySD",count,630);
      }

      var13 = CardArrays.getStartingHandsListHoldemShortdeck().iterator();

      while(var13.hasNext()) {
         var12 = (card[])var13.next();
         new CardCombinations(var12, (byte)0);
         System.lineSeparator();
      }

      util.AppFile var50 = /*isShortDeck ? new util.AppFile("HoldemSixPlus") :*/ new util.AppFile();

      try {
         a((File)(new util.AppFile(var50, "riverequitySD")), (Object)var1);
         a((File)(new util.AppFile(var50, "turnequitySD")), (Object)var2);
         a((File)(new util.AppFile(var50, "flopequitySD")), (Object)var3);
         a((File)(new util.AppFile(var50, "preflopequitySD")), (Object)var4);
      } catch (Exception var48) {
      }
   }

   public static void generateRiverBuckets(int var0) throws Throwable {
      EquityTableCache.a();
      TLongIntHashMap var1 = new TLongIntHashMap(EquityTableCache.e.size());
      int[] var2 = new int[4];
      card[] var3 = new card[7];

      int var4;
      for(var4 = 0; var4 < 7; ++var4) {
         var3[var4] = new card(0, 0);
      }

      var4 = 0;
      var3 = new card[7];

      for(int var5 = 0; var5 < 7; ++var5) {
         var3[var5] = new card(0, 0);
      }

      AnalysisPanel.p();
      List var21;
      if (AnalysisPanel.gameType == 3) {
    	  var21 = CardArrays.bShortdeck();
      } else {
    	  var21 = CardArrays.b();
      }
      
      Iterator var7 = var21.iterator();

      label62:
      while(var7.hasNext()) {
         card[] var6 = (card[])var7.next();
         if (var4 % 100 == 0) {
            System.lineSeparator();
            MainTabbedPane.a("Generating river buckets.", var4, var21.size());
         }

         ++var4;

         for(int var8 = 0; var8 < 5; ++var8) {
            var3[var8 + 2] = var6[var8];
         }

         Iterator var25;
         
         if (AnalysisPanel.gameType == 3) {
        	 var25 = CardArrays.eShortdeck(var6).iterator(); 
         } else {
        	 var25 = CardArrays.e(var6).iterator();
         }                  

         while(true) {
            long var13;
            do {
               if (!var25.hasNext()) {
                  continue label62;
               }

               var6 = (card[])var25.next();
               var3[0] = var6[0];
               var3[1] = var6[1];
               var13 = CardCombinations.a(var3, var2);
            } while(var1.containsKey(var13));

            double var10000 = (double)var0;
            double var18 = EquityTableCache.e.get(var13);
            double var16 = var10000;

            int var22;
            int var26;
            label57: {
               for(var22 = 1; (double)var22 <= var16; ++var22) {
                  if (var18 <= 0.0D + 1.0D * ((double)var22 / var16)) {
                     var26 = var22 - 1;
                     break label57;
                  }
               }

               System.lineSeparator();
               var26 = -1;
            }

            var22 = var26;
            var1.put(var13, var22);
         }
      }

      try {
         util.AppFile var24 = new util.AppFile();
         ObjectOutputStream var23;
         String sd = ""; if (AnalysisPanel.gameType == 3) { sd = "SD"; }
         (var23 = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new util.AppFile(var24, "mmnestedriver"+sd+"." + var0))))).writeObject(var1);
         var23.close();
      } catch (Exception var20) {
      }
   }

   public static void generateTurnBuckets(int var0, int var1) throws Throwable {
      System.lineSeparator();
      EquityTableCache.a();
      EquityTableCache.c();
      List var28;
      
      if (AnalysisPanel.gameType == 3) {
    	  var28 = CardArrays.getAllTurnsShortdeck();
      } else {
    	  var28 = CardArrays.getAllTurns();
      }      
      TLongIntHashMap var2 = new TLongIntHashMap(EquityTableCache.c.size());
      TIntDoubleHashMap var3 = new TIntDoubleHashMap();
      TIntDoubleHashMap var4 = new TIntDoubleHashMap();
      gnu.trove.fa.e var5 = new gnu.trove.fa.e();
      gnu.trove.da.e var6 = new gnu.trove.da.e();
      int var7 = 0;
      card[] var8 = new card[7];
      AnalysisPanel.p();
      String var9 = "";
      card[] var10 = new card[7];
      int[] var11 = new int[4];

      int var12;
      for(var12 = 0; var12 < 7; ++var12) {
         var10[var12] = new card(0, 0);
      }

      Iterator var29 = var28.iterator();

      while(var29.hasNext()) {
         card[] var30 = (card[])var29.next();
         if (var7 % 100 == 0) {
            MainTabbedPane.a("Generating turn buckets.", var7, var28.size());
         }

         ++var7;
         List var13;
         if (AnalysisPanel.gameType == 3) {
        	 var13 = CardArrays.eShortdeck(var30);
         } else {
        	 var13 = CardArrays.e(var30);
         }
         
         double var16 = 0.0D;
         double var18 = 1.0D;
         var6.b();
         var8[2] = var30[0];
         var8[3] = var30[1];
         var8[4] = var30[2];
         var8[5] = var30[3];

         double var24;
         for(Iterator var21 = var13.iterator(); var21.hasNext(); var18 = Math.min(var18, var24)) {
            var30 = (card[])var21.next();
            var8[0] = var30[0];
            var8[1] = var30[1];
            long var22 = CardCombinations.b(var8, var11);
            var6.a(var22);
            var24 = EquityTableCache.c.get(var22);
            var16 = Math.max(var16, var24);
         }

         var3.clear();
         var4.clear();

         long var33;
         for(var12 = 0; var12 < var6.a(); ++var12) {
            var33 = var6.a(var12);
            if (!var2.containsKey(var33)) {
               double var23 = EquityTableCache.c.get(var33);
               double var25 = EquityTableCache.d.get(var33);
               int var31 = a(var23, (double)var0, var18, var16);
               var2.put(var33, var31);
               if (!var3.containsKey(var31) || var25 > var3.get(var31)) {
                  var3.put(var31, var25);
               }

               if (!var4.containsKey(var31) || var25 < var4.get(var31)) {
                  var4.put(var31, var25);
               }
            }
         }

         for(var12 = 0; var12 < var6.a(); ++var12) {
            var33 = var6.a(var12);
            if (!var5.contains(var33)) {
               var5.a(var33);
               int var34 = var2.get(var33);
               int var26 = a(EquityTableCache.d.get(var33), 4.0D, var4.get(var34), var3.get(var34));
               var2.put(var33, (var34 << 2) + var26);
            }
         }
      }

      try {
         ObjectOutputStream var32;
         String sd = ""; if (AnalysisPanel.gameType == 3) {sd = "SD";}
         (var32 = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new util.AppFile(var9 + "mmnestedturn"+sd+"." + var0 + ".4"))))).writeObject(var2);
         var32.close();
      } catch (Exception var27) {
      }
   }

   private static int a(double var0, double var2, double var4, double var6) {
      double var8 = var6 - var4 + 1.0E-8D;

      for(int var10 = 1; (double)var10 <= var2; ++var10) {
         if (var0 <= var4 + var8 * ((double)var10 / var2)) {
            return var10 - 1;
         }
      }

      System.lineSeparator();
      System.lineSeparator();
      System.exit(0);
      return -1;
   }

   public static void generateFlopBuckets(int var0, int var1) throws Throwable {
      List var68;
      if (var0 == 0) {
         TLongIntHashMap var67 = new TLongIntHashMap();
         
         if (AnalysisPanel.gameType == 3) {
        	 var68 = CardArrays.dShortdeck(new card[0]);        	 
         } else {
        	 var68 = CardArrays.d(new card[0]);        	 
         }         
         
         int[] var70 = new int[4];
         card[] var72 = new card[5];

         for(int var73 = 0; var73 < 5; ++var73) {
            var72[var73] = new card(0, 0);
         }

         Iterator var69 = var68.iterator();

         while(var69.hasNext()) {
            card[] var74;
            Iterator var76;
            
            if (AnalysisPanel.gameType == 3) {
            	var76 = CardArrays.eShortdeck(var74 = (card[])var69.next()).iterator();
            } else {
            	var76 = CardArrays.e(var74 = (card[])var69.next()).iterator();
            }            	                        

            while(var76.hasNext()) {
               var72 = (card[])var76.next();
               long var35 = CardCombinations.c(new card[]{var72[0], var72[1], var74[0], var74[1], var74[2]}, var70);
               if (!var67.containsKey(var35)) {
                  var67.put(var35, var67.size());
               }
            }
         }

         AnalysisPanel.p();
         String var75 = "";
         System.lineSeparator();

         try {
            Throwable var71 = null;

            try {
            	String sd = ""; if (AnalysisPanel.gameType == 3) { sd = "SD"; }
               ObjectOutputStream var77 = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new util.AppFile(var75 + "mmnestedflop"+sd+"." + 0 + ".4"))));

               try {
                  var77.writeObject(var67);
               } finally {
                  var77.close();
               }

            } catch (Throwable var65) {
               if (var71 == null) {
                  var71 = var65;
               } else if (var71 != var65) {
                  var71.addSuppressed(var65);
               }

               throw var71;
            }
         } catch (Throwable var66) {
         }
      } else {
         EquityTableCache.a();
         EquityTableCache.c();
         if (AnalysisPanel.gameType == 3) {
        	 var68 = CardArrays.dShortdeck(new card[0]);
         } else {
        	 var68 = CardArrays.d(new card[0]);
         }
         TLongIntHashMap var2 = new TLongIntHashMap(EquityTableCache.a.size());
         TIntDoubleHashMap var3 = new TIntDoubleHashMap();
         TIntDoubleHashMap var4 = new TIntDoubleHashMap();
         gnu.trove.fa.e var5 = new gnu.trove.fa.e();
         ArrayList var6 = new ArrayList();
         int var7 = 0;
         card[] var8 = new card[7];
         int[] var9 = new int[4];

         int var10;
         for(var10 = 0; var10 < 7; ++var10) {
            var8[var10] = new card(0, 0);
         }

         Iterator var11 = var68.iterator();

         label763:
         while(var11.hasNext()) {
            card[] var78 = (card[])var11.next();
            if (var7 % 10 == 0) {
               MainTabbedPane.a("Generating flop buckets.", var7, var68.size());
            }

            ++var7;
            List var12;
            if (AnalysisPanel.gameType == 0) {
            	var12 = CardArrays.e(var78);
            } else {
            	var12 = CardArrays.eShortdeck(var78);
            }
            double var14 = 0.0D;
            double var16 = 1.0D;
            var6.clear();

            double var22;
            for(Iterator var19 = var12.iterator(); var19.hasNext(); var16 = Math.min(var16, var22)) {
               card[] var18 = (card[])var19.next();
               long var20 = CardCombinations.a(new card[]{var18[0], var18[1], var78[0], var78[1], var78[2]}, var8, var9, 5);
               var6.add(var20);
               var22 = EquityTableCache.a.get(var20);
               var14 = Math.max(var14, var22);
            }

            var3.clear();
            var4.clear();
            Iterator var83 = var6.iterator();

            while(true) {
               double var23;
               do {
                  long var82;
                  do {
                     if (!var83.hasNext()) {
                        var83 = var6.iterator();

                        while(var83.hasNext()) {
                           var82 = (Long)var83.next();
                           if (!var5.contains(var82)) {
                              var5.a(var82);
                              int var84 = var2.get(var82);
                              int var24 = a(EquityTableCache.b.get(var82), 4.0D, var4.get(var84), var3.get(var84));
                              var2.put(var82, (var2.get(var82) << 2) + var24);
                           }
                        }
                        continue label763;
                     }

                     var82 = (Long)var83.next();
                  } while(var2.containsKey(var82));

                  double var21 = EquityTableCache.a.get(var82);
                  var23 = EquityTableCache.b.get(var82);
                  var10 = a(var21, (double)var0, var16, var14);
                  var2.put(var82, var10);
                  if (!var3.containsKey(var10) || var23 > var3.get(var10)) {
                     var3.put(var10, var23);
                  }
               } while(var4.containsKey(var10) && var23 >= var4.get(var10));

               var4.put(var10, var23);
            }
         }

         String sd = ""; if (AnalysisPanel.gameType == 3) { sd = "SD";}
         String var79 = "mmnestedflop"+sd+"." + var0 + ".4";
         AnalysisPanel.p();
         util.AppFile var80 = new util.AppFile(var79);

         try {
            Throwable var81 = null;

            try {
               ObjectOutputStream var15 = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(var80)));

               try {
                  var15.writeObject(var2);
               } finally {
                  var15.close();
               }

            } catch (Throwable var63) {
               if (var81 == null) {
                  var81 = var63;
               } else if (var81 != var63) {
                  var81.addSuppressed(var63);
               }

               throw var81;
            }
         } catch (Throwable var64) {
         }
      }
   }

   public static final Object a(String var0) {
      return a((File)(new util.AppFile(var0)));
   }

   public static final Object a(File var0, String var1) {
      return a(new File(var0, var1));
   }

   public static final Object a(ZipFile var0, String var1, String var2) throws IOException {
      return a((InputStream)(new ZipEntryInputStream(var0, var1, var2)));
   }

   public static final Object a(InputStream var0) {
      try {
         Throwable var1 = null;

         try {
            ObjectInputStream var12 = new ObjectInputStream(new BufferedInputStream(var0, 65536));

            Object var10000;
            try {
               var10000 = var12.readObject();
            } finally {
               var12.close();
            }

            return var10000;
         } catch (Throwable var10) {
            if (var1 == null) {
               var1 = var10;
            } else if (var1 != var10) {
               var1.addSuppressed(var10);
            }

            throw var1;
         }
      } catch (Throwable var11) {
         return null;
      }
   }

   public static final Object a(InputStream var0, String var1, long var2) {
      try {
         Throwable var4 = null;

         try {
            ObjectInputStream var14 = new ObjectInputStream(new ZipEntryInputStream(var0, var1, var2));

            Object var10000;
            try {
               var10000 = var14.readObject();
            } finally {
               var14.close();
            }

            return var10000;
         } catch (Throwable var12) {
            if (var4 == null) {
               var4 = var12;
            } else if (var4 != var12) {
               var4.addSuppressed(var12);
            }

            throw var4;
         }
      } catch (Throwable var13) {
         return null;
      }
   }

   public static final Object a(File var0) {
      Object var1;
      if ((var1 = b(var0)) != null) {
         return var1;
      } else {
         try {
            Throwable var13 = null;

            try {
               ObjectInputStream var12 = new ObjectInputStream(new BufferedInputStream(new HandRangeWriter(var0), 65536));

               Object var10000;
               try {
                  Object var2 = var12.readObject();
                  System.lineSeparator();
                  var10000 = var2;
               } finally {
                  var12.close();
               }

               return var10000;
            } catch (Throwable var10) {
               if (var13 == null) {
                  var13 = var10;
               } else if (var13 != var10) {
                  var13.addSuppressed(var10);
               }

               throw var13;
            }
         } catch (Throwable var11) {
            return null;
         }
      }
   }

   public static final void a(File var0, String var1, Object var2) throws Throwable {
      a(new File(var0, var1), var2);
   }

   public static final void a(OutputStream var0, Object var1) throws Throwable {
      try {
         Throwable var2 = null;

         try {
            ObjectOutputStream var12 = new ObjectOutputStream(new BufferedOutputStream(var0, 65536));

            try {
               var12.writeObject(var1);
            } finally {
               var12.close();
            }

         } catch (Throwable var10) {
            if (var2 == null) {
               var2 = var10;
            } else if (var2 != var10) {
               var2.addSuppressed(var10);
            }

            throw var2;
         }
      } catch (Throwable var11) {
         throw var11;
      }
   }

   private static double[] a(DataInputStream var0) throws IOException {
      int var1;
      if ((var1 = var0.readInt()) < 0) {
         return null;
      } else {
         double[] var2 = new double[var1];

         for(int var3 = 0; var3 < var1; ++var3) {
            var2[var3] = var0.readDouble();
         }

         return var2;
      }
   }

   public static final double[][] b(InputStream var0) {
      try {
         Throwable var1 = null;

         try {
            DataInputStream var13 = new DataInputStream(new BufferedInputStream(var0, 65536));

            try {
               double[][] var2 = new double[var13.readInt()][];

               for(int var3 = 0; var3 < var2.length; ++var3) {
                  var2[var3] = a(var13);
               }

               double[][] var10000 = var2;
               return var10000;
            } finally {
               var13.close();
            }
         } catch (Throwable var11) {
            if (var1 == null) {
               var1 = var11;
            } else if (var1 != var11) {
               var1.addSuppressed(var11);
            }

            throw var1;
         }
      } catch (Throwable var12) {
         var12.printStackTrace();
         return null;
      }
   }

   public static final void a(File var0, Object var1) throws Throwable {
      if (var0.getParentFile() != null) {
         var0.getParentFile().mkdirs();
      }

      try {
         Throwable var2 = null;

         try {
            ObjectOutputStream var12 = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(var0), 65536));

            try {
               var12.writeObject(var1);
            } finally {
               var12.close();
            }

         } catch (Throwable var10) {
            if (var2 == null) {
               var2 = var10;
            } else if (var2 != var10) {
               var2.addSuppressed(var10);
            }

            throw var2;
         }
      } catch (Throwable var11) {
         throw var11;
      }
   }

   public static final void b(File var0, Object var1) {
      if (var0.getParentFile() != null) {
         var0.getParentFile().mkdirs();
      }

      try {
         Throwable var2 = null;

         try {
            ObjectOutputStream var12 = new ObjectOutputStream(new DeflaterOutputStream(new FileOutputStream(var0), new Deflater(1), 65536));

            try {
               var12.writeObject(var1);
               var12.close();
            } finally {
               var12.close();
            }

         } catch (Throwable var10) {
            if (var2 == null) {
               var2 = var10;
            } else if (var2 != var10) {
               var2.addSuppressed(var10);
            }

            throw var2;
         }
      } catch (Throwable var11) {
         var11.printStackTrace();
      }
   }

   public static final Object c(InputStream var0) {
      try {
         Throwable var1 = null;

         try {
            ObjectInputStream var11 = new ObjectInputStream(new InflaterInputStream(var0));

            Object var10000;
            try {
               var10000 = var11.readObject();
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
      } catch (Throwable var10) {
         return null;
      }
   }

   private static Object b(File var0) {
      try {
         Throwable var1 = null;

         try {
            ObjectInputStream var11 = new ObjectInputStream(new InflaterInputStream(new HandRangeWriter(var0)));

            Object var10000;
            try {
               var10000 = var11.readObject();
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
      } catch (Throwable var10) {
         return null;
      }
   }

   public static int[] a(int[][] var0) {
      int[] var1 = new int[var0.length * var0[0].length];

      for(int var2 = 0; var2 < var0.length; ++var2) {
         for(int var3 = 0; var3 < var0[0].length; ++var3) {
            var1[(var2 << 2) + var3] = var0[var2][var3];
         }
      }

      return var1;
   }
}

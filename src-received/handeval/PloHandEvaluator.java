package handeval;

import solver.AnalysisPanel;
import java.util.Arrays;

public final class PloHandEvaluator {
   private static short[] a = new short[1149876];
   private static short[] b;  // Sized dynamically: 828100 for 4-card, 2815540 for 5-card
   private static short[] b5c;  // 5-card specific array (2815540 entries)
   private static boolean c = false;      // 4-card initialization flag
   private static boolean c5c = false;    // 5-card initialization flag
   private static short[] d;
   private static final int[] e = new int[91];      // 4-card offsets
   private static final int[] e5c = new int[91];    // 5-card combination counts per pair
   private static final int[] e5c_off = new int[91]; // 5-card offsets (like e[] but for 5-card)

   private static final int a(int var0, int var1) {
      return var0 % 13 + var1 * 13;
   }

   public static void a() {
      if (AnalysisPanel.gameType == 2) {
         b();
      }

      // Initialize 4-card tables (once)
      if (!c) {
         c = true;

         b = new short[828100];  // 4-card Omaha (always needed for flush lookups)

         int var0 = 0;

         int var3;
         // Initialize e[] for 4-card Omaha (original 4-level loop)
         for(int var1 = 0; var1 < 13; ++var1) {
            for(int var2 = var1; var2 < 13; ++var2) {
               e[b(var1, var2)] = var0;

               for(var3 = var2; var3 < 13; ++var3) {
                  for(int var4 = var3; var4 < 13; ++var4) {
                     ++var0;
                  }
               }
            }
         }

         // Initialize e5c[] and e5c_off[] for 5-card Omaha (5-level loop)
         // e5c_off[] stores the running offset (like e[] does for 4-card)
         // e5c[] stores the count of combinations for each pair
         int var0_5c = 0;
         for(int var1 = 0; var1 < 13; ++var1) {
            for(int var2 = var1; var2 < 13; ++var2) {
               e5c_off[b(var1, var2)] = var0_5c;  // Set offset BEFORE counting
               int varNew = 0;
               for(var3 = var2; var3 < 13; ++var3) {
                  for(int var4 = var3; var4 < 13; ++var4) {
                     for (int varN = var4; varN < 13; ++varN) {
                        ++var0_5c;
                        ++varNew;
                     }
                  }
               }
               e5c[b(var1,var2)] = varNew;
            }
         }

         FiveCardEvaluator.a();
         int[] var5 = FiveCardEvaluator.a4;  // Use 4-card array (52 elements) for 4-card initialization
         int[] var6 = new int[7];
         int[] var7 = new int[7];

         int var10002;
         short var8;
         for(var6[0] = 0; var6[0] < 26; var10002 = var6[0]++) {
            for(var6[1] = var6[0]; var6[1] < 26; var10002 = var6[1]++) {
               for(var6[2] = 0; var6[2] < 26; var10002 = var6[2]++) {
                  for(var6[3] = var6[2]; var6[3] < 26; var10002 = var6[3]++) {
                     for(var6[4] = var6[3]; var6[4] < 26; var10002 = var6[4]++) {
                        for(var3 = 0; var3 < 5; ++var3) {
                           if (var6[var3] / 13 == 1) {
                              var7[var3] = var6[var3] % 13;
                           } else {
                              var7[var3] = a(var6[var3], 1 + var3 % 3);
                           }
                        }

                        if ((var8 = FiveCardEvaluator.a(var5[var7[0]], var5[var7[1]], var5[var7[2]], var5[var7[3]], var5[var7[4]])) > 0) {
                           a[c(var6[0], var6[1]) * 3276 + a(var6[2], var6[3], var6[4])] = var8;
                        }
                     }
                  }
               }
            }
         }

         for(var6[0] = 0; var6[0] < 13; var10002 = var6[0]++) {
            for(var6[1] = var6[0]; var6[1] < 13; var10002 = var6[1]++) {
               for(var6[2] = var6[1]; var6[2] < 13; var10002 = var6[2]++) {
                  for(var6[3] = var6[2]; var6[3] < 13; var10002 = var6[3]++) {
                     for(var6[4] = 0; var6[4] < 13; var10002 = var6[4]++) {
                        for(var6[5] = var6[4]; var6[5] < 13; var10002 = var6[5]++) {
                           for(var6[6] = var6[5]; var6[6] < 13; var10002 = var6[6]++) {
                              for(var3 = 0; var3 < 7; ++var3) {
                                 var7[var3] = a(var6[var3], 1 + var3 % 3);
                              }

                              short var9 = FiveCardEvaluator.a(var5[var7[0]], var5[var7[1]], var5[var7[4]], var5[var7[5]], var5[var7[6]]);
                              if ((var8 = FiveCardEvaluator.a(var5[var7[0]], var5[var7[2]], var5[var7[4]], var5[var7[5]], var5[var7[6]])) < var9) {
                                 var9 = var8;
                              }

                              if ((var8 = FiveCardEvaluator.a(var5[var7[0]], var5[var7[3]], var5[var7[4]], var5[var7[5]], var5[var7[6]])) < var9) {
                                 var9 = var8;
                              }

                              if ((var8 = FiveCardEvaluator.a(var5[var7[1]], var5[var7[2]], var5[var7[4]], var5[var7[5]], var5[var7[6]])) < var9) {
                                 var9 = var8;
                              }

                              if ((var8 = FiveCardEvaluator.a(var5[var7[1]], var5[var7[3]], var5[var7[4]], var5[var7[5]], var5[var7[6]])) < var9) {
                                 var9 = var8;
                              }

                              if ((var8 = FiveCardEvaluator.a(var5[var7[2]], var5[var7[3]], var5[var7[4]], var5[var7[5]], var5[var7[6]])) < var9) {
                                 var9 = var8;
                              }

                              if (var8 > 0) {
                                 b[a(var6[0], var6[1], var6[2], var6[3]) * 455 + b(var6[4], var6[5], var6[6])] = var9;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      // Initialize 5-card tables (only when needed, can be triggered after 4-card init)
      if (AnalysisPanel.is5Card() && !c5c) {
         c5c = true;
         b5c = new short[2815540];  // 5-card Omaha

         FiveCardEvaluator.a();  // Ensure FiveCardEvaluator is initialized
         int[] var5_5c = FiveCardEvaluator.a5;  // Use 5-card array (65 elements) for 5-card initialization
         int[] var6_5c = new int[8];
         int[] var7_5c = new int[8];
         int var3;
         for(var6_5c[0] = 0; var6_5c[0] < 13; var6_5c[0]++) {
            for(var6_5c[1] = var6_5c[0]; var6_5c[1] < 13; var6_5c[1]++) {
               for(var6_5c[2] = var6_5c[1]; var6_5c[2] < 13; var6_5c[2]++) {
                  for(var6_5c[3] = var6_5c[2]; var6_5c[3] < 13; var6_5c[3]++) {
                     for(var6_5c[4] = var6_5c[3]; var6_5c[4] < 13; var6_5c[4]++) {
                        // board
                        for(var6_5c[5] = 0; var6_5c[5] < 13; var6_5c[5]++) {
                           for(var6_5c[6] = var6_5c[5]; var6_5c[6] < 13; var6_5c[6]++) {
                              for(var6_5c[7] = var6_5c[6]; var6_5c[7] < 13; var6_5c[7]++) {
                                 for(var3 = 0; var3 < 8; ++var3) {
                                    var7_5c[var3] = a(var6_5c[var3], 1 + var3 % 3);
                                 }

                                 short var9_5c = FiveCardEvaluator.a(var5_5c[var7_5c[0]], var5_5c[var7_5c[1]], var5_5c[var7_5c[5]], var5_5c[var7_5c[6]], var5_5c[var7_5c[7]]);
                                 short var8_5c;
                                 if ((var8_5c = FiveCardEvaluator.a(var5_5c[var7_5c[0]], var5_5c[var7_5c[2]], var5_5c[var7_5c[5]], var5_5c[var7_5c[6]], var5_5c[var7_5c[7]])) < var9_5c) {
                                    var9_5c = var8_5c;
                                 }

                                 if ((var8_5c = FiveCardEvaluator.a(var5_5c[var7_5c[0]], var5_5c[var7_5c[3]], var5_5c[var7_5c[5]], var5_5c[var7_5c[6]], var5_5c[var7_5c[7]])) < var9_5c) {
                                    var9_5c = var8_5c;
                                 }
                                 if ((var8_5c = FiveCardEvaluator.a(var5_5c[var7_5c[0]], var5_5c[var7_5c[4]], var5_5c[var7_5c[5]], var5_5c[var7_5c[6]], var5_5c[var7_5c[7]])) < var9_5c) {
                                    var9_5c = var8_5c;
                                 }

                                 if ((var8_5c = FiveCardEvaluator.a(var5_5c[var7_5c[1]], var5_5c[var7_5c[2]], var5_5c[var7_5c[5]], var5_5c[var7_5c[6]], var5_5c[var7_5c[7]])) < var9_5c) {
                                    var9_5c = var8_5c;
                                 }

                                 if ((var8_5c = FiveCardEvaluator.a(var5_5c[var7_5c[1]], var5_5c[var7_5c[3]], var5_5c[var7_5c[5]], var5_5c[var7_5c[6]], var5_5c[var7_5c[7]])) < var9_5c) {
                                    var9_5c = var8_5c;
                                 }
                                 if ((var8_5c = FiveCardEvaluator.a(var5_5c[var7_5c[1]], var5_5c[var7_5c[4]], var5_5c[var7_5c[5]], var5_5c[var7_5c[6]], var5_5c[var7_5c[7]])) < var9_5c) {
                                    var9_5c = var8_5c;
                                 }

                                 if ((var8_5c = FiveCardEvaluator.a(var5_5c[var7_5c[2]], var5_5c[var7_5c[3]], var5_5c[var7_5c[5]], var5_5c[var7_5c[6]], var5_5c[var7_5c[7]])) < var9_5c) {
                                    var9_5c = var8_5c;
                                 }
                                 if ((var8_5c = FiveCardEvaluator.a(var5_5c[var7_5c[2]], var5_5c[var7_5c[4]], var5_5c[var7_5c[5]], var5_5c[var7_5c[6]], var5_5c[var7_5c[7]])) < var9_5c) {
                                    var9_5c = var8_5c;
                                 }

                                 if ((var8_5c = FiveCardEvaluator.a(var5_5c[var7_5c[3]], var5_5c[var7_5c[4]], var5_5c[var7_5c[5]], var5_5c[var7_5c[6]], var5_5c[var7_5c[7]])) < var9_5c) {
                                    var9_5c = var8_5c;
                                 }

                                 if (var8_5c > 0) {
                                    b5c[a5c(var6_5c[0], var6_5c[1], var6_5c[2], var6_5c[3], var6_5c[4]) * 455 + b(var6_5c[5], var6_5c[6], var6_5c[7])] = var9_5c;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void b() {
      if (d == null) {
         d = new short[32768];
         int[] var0 = new int[5];
         boolean[] var1 = new boolean[8];
         int[] var2 = new int[8];

         int var10002;
         for(var0[0] = 0; var0[0] < 8; var10002 = var0[0]++) {
            for(var0[1] = 0; var0[1] < 8; var10002 = var0[1]++) {
               for(var0[2] = 0; var0[2] < 8; var10002 = var0[2]++) {
                  for(var0[3] = 0; var0[3] < 8; var10002 = var0[3]++) {
                     for(var0[4] = 0; var0[4] < 8; var10002 = var0[4]++) {
                        System.arraycopy(var0, 0, var2, 0, 5);
                        d[var0[0] + (var0[1] << 3) + (var0[2] << 6) + (var0[3] << 9) + (var0[4] << 12)] = a(var0, var1, var2);
                     }
                  }
               }
            }
         }

      }
   }

   private static short a(int[] var0, boolean[] var1, int[] var2) {
      Arrays.fill(var1, false);

      for(int var3 = 0; var3 < 5; ++var3) {
         if (var1[var0[var3]]) {
            return 32767;
         }

         var1[var0[var3]] = true;
         var2[var3] = var0[var3];
      }

      Arrays.sort(var2);
      return (short)((var2[4] << 12) + (var2[3] << 9) + (var2[2] << 6) + (var2[1] << 3) + var2[0]);
   }

   public static final int a(int[] var0, int var1, int[] var2) {
      var2[0] = 0;
      var2[1] = 0;
      var2[2] = 0;
      var2[3] = 0;

      for(var1 = var1; var1 < var0.length; ++var1) {
         if (var2[var0[var1] / 13] >= 2) {
            return var0[var1] / 13;
         }

         ++var2[var0[var1] / 13];
      }

      return -1;
   }

   // 5-card PLO version - same logic as a(int[], int, int[]) but for API compatibility
   public static final int a5c(int[] var0, int var1, int[] var2) {
      var2[0] = 0;
      var2[1] = 0;
      var2[2] = 0;
      var2[3] = 0;

      for(int var10 = var1; var10 < var0.length; ++var10) {
         if (var2[var0[var10] / 13] >= 2) {
            return var0[var10] / 13;
         }

         ++var2[var0[var10] / 13];
      }

      return -1;
   }

   public static final int a(int[] var0) {
      long var1;
      if (Long.bitCount((var1 = 1L << var0[4] | 1L << var0[5] | 1L << var0[6] | 1L << var0[7] | 1L << var0[8]) & 4503049871556608L) >= 3) {
         return 3;
      } else if (Long.bitCount(var1 & 549688705024L) >= 3) {
         return 2;
      } else if (Long.bitCount(var1 & 67100672L) >= 3) {
         return 1;
      } else {
         return Long.bitCount(var1 & 8191L) >= 3 ? 0 : -1;
      }
   }

   public static int b(int[] var0) {
      return a(var0[0], var0[1], var0[2], var0[3], var0[4], var0[5], var0[6], var0[7], var0[8], a(var0));
   }

   // 5-card Omaha flush detection
   public static final int a5c(int[] var0) {
      long var1 = 1L << var0[5] | 1L << var0[6] | 1L << var0[7] | 1L << var0[8] | 1L << var0[9];

      if (Long.bitCount(var1 & 4503049871556608L) >= 3) {
         return 3;
      } else if (Long.bitCount(var1 & 549688705024L) >= 3) {
         return 2;
      } else if (Long.bitCount(var1 & 67100672L) >= 3) {
         return 1;
      } else {
         return Long.bitCount(var1 & 8191L) >= 3 ? 0 : -1;
      }
   }

   // 5-card Omaha hand evaluation
   public static int b5c(int[] var0) {
      return a5c(var0[0], var0[1], var0[2], var0[3], var0[4], var0[5], var0[6], var0[7], var0[8], var0[9], a5c(var0));
   }

   private static final int b(int[] var0, int var1) {
      if (var1 == 5) {
         return d[var0[0] + (var0[1] << 3) + (var0[2] << 6) + (var0[3] << 9) + (var0[4] << 12)];
      } else {
         short var2;
         short var3;
         if (var1 == 6) {
            var3 = d[var0[0] + (var0[1] << 3) + (var0[2] << 6) + (var0[3] << 9) + (var0[4] << 12)];
            if ((var2 = d[var0[0] + (var0[1] << 3) + (var0[2] << 6) + (var0[3] << 9) + (var0[5] << 12)]) < var3) {
               var3 = var2;
            }

            if ((var2 = d[var0[0] + (var0[1] << 3) + (var0[2] << 6) + (var0[4] << 9) + (var0[5] << 12)]) < var3) {
               var3 = var2;
            }

            if ((var2 = d[var0[0] + (var0[1] << 3) + (var0[3] << 6) + (var0[4] << 9) + (var0[5] << 12)]) < var3) {
               var3 = var2;
            }

            return var3;
         } else {
            var3 = d[var0[0] + (var0[1] << 3) + (var0[2] << 6) + (var0[3] << 9) + (var0[4] << 12)];
            if ((var2 = d[var0[0] + (var0[1] << 3) + (var0[2] << 6) + (var0[3] << 9) + (var0[5] << 12)]) < var3) {
               var3 = var2;
            }

            if ((var2 = d[var0[0] + (var0[1] << 3) + (var0[2] << 6) + (var0[3] << 9) + (var0[6] << 12)]) < var3) {
               var3 = var2;
            }

            if ((var2 = d[var0[0] + (var0[1] << 3) + (var0[2] << 6) + (var0[4] << 9) + (var0[5] << 12)]) < var3) {
               var3 = var2;
            }

            if ((var2 = d[var0[0] + (var0[1] << 3) + (var0[2] << 6) + (var0[4] << 9) + (var0[6] << 12)]) < var3) {
               var3 = var2;
            }

            if ((var2 = d[var0[0] + (var0[1] << 3) + (var0[2] << 6) + (var0[5] << 9) + (var0[6] << 12)]) < var3) {
               var3 = var2;
            }

            if ((var2 = d[var0[0] + (var0[1] << 3) + (var0[3] << 6) + (var0[4] << 9) + (var0[5] << 12)]) < var3) {
               var3 = var2;
            }

            if ((var2 = d[var0[0] + (var0[1] << 3) + (var0[3] << 6) + (var0[4] << 9) + (var0[6] << 12)]) < var3) {
               var3 = var2;
            }

            if ((var2 = d[var0[0] + (var0[1] << 3) + (var0[3] << 6) + (var0[5] << 9) + (var0[6] << 12)]) < var3) {
               var3 = var2;
            }

            if ((var2 = d[var0[0] + (var0[1] << 3) + (var0[4] << 6) + (var0[5] << 9) + (var0[6] << 12)]) < var3) {
               var3 = var2;
            }

            return var3;
         }
      }
   }

   private static final int a(int var0) {
      if ((var0 %= 13) == 12) {
         return 0;
      } else {
         return var0 < 7 ? var0 + 1 : 8;
      }
   }

   public static final int c(int[] var0) {
      int var1 = 2;

      for(int var2 = 4; var2 < 9; ++var2) {
         if (a(var0[var2]) < 8) {
            ++var1;
         }
      }

      return var1;
   }

   public static final int a(int[] var0, int[] var1, int var2) {
      int var3 = 0;

      int var4;
      int var5;
      for(var4 = 0; var4 < 4; ++var4) {
         if ((var5 = a(var0[var4])) < 8) {
            var1[var2 + var3] = var5;
            ++var3;
         }
      }

      if (var3 < 2) {
         return 32767;
      } else if (var3 == 2) {
         var1[0] = var1[var2];
         var1[1] = var1[var2 + 1];
         return b(var1, var2);
      } else if (var3 == 3) {
         var1[0] = var1[var2];
         var1[1] = var1[var2 + 1];
         var4 = b(var1, var2);
         var1[0] = var1[var2];
         var1[1] = var1[var2 + 2];
         if ((var5 = b(var1, var2)) < var4) {
            var4 = var5;
         }

         var1[0] = var1[var2 + 1];
         var1[1] = var1[var2 + 2];
         if ((var5 = b(var1, var2)) < var4) {
            var4 = var5;
         }

         return var4;
      } else {
         var1[0] = var1[var2];
         var1[1] = var1[var2 + 1];
         var4 = b(var1, var2);
         var1[0] = var1[var2];
         var1[1] = var1[var2 + 2];
         if ((var5 = b(var1, var2)) < var4) {
            var4 = var5;
         }

         var1[0] = var1[var2];
         var1[1] = var1[var2 + 3];
         if ((var5 = b(var1, var2)) < var4) {
            var4 = var5;
         }

         var1[0] = var1[var2 + 1];
         var1[1] = var1[var2 + 2];
         if ((var5 = b(var1, var2)) < var4) {
            var4 = var5;
         }

         var1[0] = var1[var2 + 1];
         var1[1] = var1[var2 + 3];
         if ((var5 = b(var1, var2)) < var4) {
            var4 = var5;
         }

         var1[0] = var1[var2 + 2];
         var1[1] = var1[var2 + 3];
         if ((var5 = b(var1, var2)) < var4) {
            var4 = var5;
         }

         return var4;
      }
   }

   public static final int a(int[] var0, int[] var1) {
      b();
      int var2 = 2;

      for(int var3 = 4; var3 < var0.length; ++var3) {
         int var4;
         if ((var4 = a(var0[var3])) < 8) {
            var1[var2] = var4;
            ++var2;
         }
      }

      if (var2 < 5) {
         return 32767;
      } else {
         return a(var0, var1, var2);
      }
   }

   private static final int b(int var0, int var1) {
      return (25 - var0) * var0 / 2 + var1;
   }

   private static final int c(int var0, int var1) {
      return (51 - var0) * var0 / 2 + var1;
   }

   private static final int a(int var0, int var1, int var2) {
      return var0 * (var0 * (var0 - 81) + 2180) / 6 + (51 - var0 - var1) * (var1 - var0) / 2 + var2;
   }

   private static final int a(int var0, int var1, int var2, int var3) {
      return e[b(var0, var1)] + (25 - var1 - var2) * (var2 - var1) / 2 + var3 - var1;
   }

   private static final int a5c(int var0, int var1, int var2, int var3, int varN) {
      int test1 = b(var2, var3, varN);
      int test2 = e5c[b(var0,var1)];
      int test3 = test1 - (455 - test2);

      // Use e5c_off for 5-card offsets instead of e[] which has 4-card offsets
      return e5c_off[b(var0, var1)] + test3;
   }

   // 5-card Omaha: check if at least 2 hole cards match the flush suit
   private static final boolean a5c(int var0, int var1, int var2, int var3, int varN, int var4) {
      if (var0 / 13 == var4) {
         return var1 / 13 == var4 || var2 / 13 == var4 || var3 / 13 == var4 || varN / 13 == var4;
      } else if (var1 / 13 == var4) {
         return var2 / 13 == var4 || var3 / 13 == var4 || varN / 13 == var4;
      } else if (var2 / 13 == var4) {
         return var3 / 13 == var4 || varN / 13 == var4;
      } else {
         return var3 / 13 == var4 && varN / 13 == var4;
      }
   }

   // 5-card Omaha: 11-parameter hand evaluation with flush suit
   private static int a5c(int var0, int var1, int var2, int var3, int varN, int var4, int var5, int var6, int var7, int var8, int var9) {
      if (var9 >= 0 && a5c(var0, var1, var2, var3, varN, var9)) {
         var0 = var0 / 13 == var9 ? a(var0, 1) : var0 % 13;
         var1 = var1 / 13 == var9 ? a(var1, 1) : var1 % 13;
         var2 = var2 / 13 == var9 ? a(var2, 1) : var2 % 13;
         var3 = var3 / 13 == var9 ? a(var3, 1) : var3 % 13;
         varN = varN / 13 == var9 ? a(varN, 1) : varN % 13;

         var4 = var4 / 13 == var9 ? a(var4, 1) : var4 % 13;
         var5 = var5 / 13 == var9 ? a(var5, 1) : var5 % 13;
         var6 = var6 / 13 == var9 ? a(var6, 1) : var6 % 13;
         var7 = var7 / 13 == var9 ? a(var7, 1) : var7 % 13;
         var8 = var8 / 13 == var9 ? a(var8, 1) : var8 % 13;

         // Sort hole cards (5 cards)
         if (var0 > var1) {
            var9 = var0;
            var0 = var1;
            var1 = var9;
         }
         if (var2 > var3) {
            var9 = var2;
            var2 = var3;
            var3 = var9;
         }
         if (var0 > var2) {
            var9 = var0;
            var0 = var2;
            var2 = var9;
         }
         if (var1 > var3) {
            var9 = var1;
            var1 = var3;
            var3 = var9;
         }
         if (var1 > var2) {
            var9 = var1;
            var1 = var2;
            var2 = var9;
         }
         if (var3 > varN) {
            var9 = var3;
            var3 = varN;
            varN = var9;
            if (var2 > var3) {
               var9 = var2;
               var2 = var3;
               var3 = var9;
               if (var1 > var2) {
                  var9 = var1;
                  var1 = var2;
                  var2 = var9;
                  if (var0 > var1) {
                     var9 = var0;
                     var0 = var1;
                     var1 = var9;
                  }
               }
            }
         }

         // Sort board cards (5 cards)
         if (var4 > var5) {
            var9 = var4;
            var4 = var5;
            var5 = var9;
         }
         if (var7 > var8) {
            var9 = var7;
            var7 = var8;
            var8 = var9;
         }
         if (var6 > var8) {
            var9 = var6;
            var6 = var8;
            var8 = var9;
         }
         if (var6 > var7) {
            var9 = var6;
            var6 = var7;
            var7 = var9;
         }
         if (var4 > var7) {
            var9 = var4;
            var4 = var7;
            var7 = var9;
         }
         if (var4 > var6) {
            var9 = var4;
            var4 = var6;
            var6 = var9;
         }
         if (var5 > var8) {
            var9 = var5;
            var5 = var8;
            var8 = var9;
         }
         if (var5 > var7) {
            var9 = var5;
            var5 = var7;
            var7 = var9;
         }
         if (var5 > var6) {
            var9 = var5;
            var5 = var6;
            var6 = var9;
         }

         var9 = a(var4, var5, var6);
         int var10 = a(var4, var5, var7);
         int var11 = a(var4, var5, var8);
         int var12 = a(var4, var6, var7);
         int var13 = a(var4, var6, var8);
         var4 = a(var4, var7, var8);
         int var14 = a(var5, var6, var7);
         int var15 = a(var5, var6, var8);
         var5 = a(var5, var7, var8);
         var6 = a(var6, var7, var8);

         short var17;
         short var16;

         // All 10 combinations of 2 hole cards from 5
         var8 = c(var3, varN) * 3276;
         var16 = a[var8 + var6];
         if ((var17 = a[var8 + var5]) < var16) var16 = var17;
         if ((var17 = a[var8 + var15]) < var16) var16 = var17;
         if ((var17 = a[var8 + var14]) < var16) var16 = var17;
         if ((var17 = a[var8 + var4]) < var16) var16 = var17;
         if ((var17 = a[var8 + var13]) < var16) var16 = var17;
         if ((var17 = a[var8 + var12]) < var16) var16 = var17;
         if ((var17 = a[var8 + var11]) < var16) var16 = var17;
         if ((var17 = a[var8 + var10]) < var16) var16 = var17;
         if ((var17 = a[var8 + var9]) < var16) var16 = var17;

         var8 = c(var2, varN) * 3276;
         if ((var17 = a[var8 + var6]) < var16) var16 = var17;
         if ((var17 = a[var8 + var5]) < var16) var16 = var17;
         if ((var17 = a[var8 + var15]) < var16) var16 = var17;
         if ((var17 = a[var8 + var14]) < var16) var16 = var17;
         if ((var17 = a[var8 + var4]) < var16) var16 = var17;
         if ((var17 = a[var8 + var13]) < var16) var16 = var17;
         if ((var17 = a[var8 + var12]) < var16) var16 = var17;
         if ((var17 = a[var8 + var11]) < var16) var16 = var17;
         if ((var17 = a[var8 + var10]) < var16) var16 = var17;
         if ((var17 = a[var8 + var9]) < var16) var16 = var17;

         var8 = c(var1, varN) * 3276;
         if ((var17 = a[var8 + var6]) < var16) var16 = var17;
         if ((var17 = a[var8 + var5]) < var16) var16 = var17;
         if ((var17 = a[var8 + var15]) < var16) var16 = var17;
         if ((var17 = a[var8 + var14]) < var16) var16 = var17;
         if ((var17 = a[var8 + var4]) < var16) var16 = var17;
         if ((var17 = a[var8 + var13]) < var16) var16 = var17;
         if ((var17 = a[var8 + var12]) < var16) var16 = var17;
         if ((var17 = a[var8 + var11]) < var16) var16 = var17;
         if ((var17 = a[var8 + var10]) < var16) var16 = var17;
         if ((var17 = a[var8 + var9]) < var16) var16 = var17;

         var8 = c(var0, varN) * 3276;
         if ((var17 = a[var8 + var6]) < var16) var16 = var17;
         if ((var17 = a[var8 + var5]) < var16) var16 = var17;
         if ((var17 = a[var8 + var15]) < var16) var16 = var17;
         if ((var17 = a[var8 + var14]) < var16) var16 = var17;
         if ((var17 = a[var8 + var4]) < var16) var16 = var17;
         if ((var17 = a[var8 + var13]) < var16) var16 = var17;
         if ((var17 = a[var8 + var12]) < var16) var16 = var17;
         if ((var17 = a[var8 + var11]) < var16) var16 = var17;
         if ((var17 = a[var8 + var10]) < var16) var16 = var17;
         if ((var17 = a[var8 + var9]) < var16) var16 = var17;

         var8 = c(var2, var3) * 3276;
         if ((var17 = a[var8 + var6]) < var16) var16 = var17;
         if ((var17 = a[var8 + var5]) < var16) var16 = var17;
         if ((var17 = a[var8 + var15]) < var16) var16 = var17;
         if ((var17 = a[var8 + var14]) < var16) var16 = var17;
         if ((var17 = a[var8 + var4]) < var16) var16 = var17;
         if ((var17 = a[var8 + var13]) < var16) var16 = var17;
         if ((var17 = a[var8 + var12]) < var16) var16 = var17;
         if ((var17 = a[var8 + var11]) < var16) var16 = var17;
         if ((var17 = a[var8 + var10]) < var16) var16 = var17;
         if ((var17 = a[var8 + var9]) < var16) var16 = var17;

         var8 = c(var1, var3) * 3276;
         if ((var17 = a[var8 + var6]) < var16) var16 = var17;
         if ((var17 = a[var8 + var5]) < var16) var16 = var17;
         if ((var17 = a[var8 + var15]) < var16) var16 = var17;
         if ((var17 = a[var8 + var14]) < var16) var16 = var17;
         if ((var17 = a[var8 + var4]) < var16) var16 = var17;
         if ((var17 = a[var8 + var13]) < var16) var16 = var17;
         if ((var17 = a[var8 + var12]) < var16) var16 = var17;
         if ((var17 = a[var8 + var11]) < var16) var16 = var17;
         if ((var17 = a[var8 + var10]) < var16) var16 = var17;
         if ((var17 = a[var8 + var9]) < var16) var16 = var17;

         var8 = c(var0, var3) * 3276;
         if ((var17 = a[var8 + var6]) < var16) var16 = var17;
         if ((var17 = a[var8 + var5]) < var16) var16 = var17;
         if ((var17 = a[var8 + var15]) < var16) var16 = var17;
         if ((var17 = a[var8 + var14]) < var16) var16 = var17;
         if ((var17 = a[var8 + var4]) < var16) var16 = var17;
         if ((var17 = a[var8 + var13]) < var16) var16 = var17;
         if ((var17 = a[var8 + var12]) < var16) var16 = var17;
         if ((var17 = a[var8 + var11]) < var16) var16 = var17;
         if ((var17 = a[var8 + var10]) < var16) var16 = var17;
         if ((var17 = a[var8 + var9]) < var16) var16 = var17;

         var8 = c(var1, var2) * 3276;
         if ((var17 = a[var8 + var6]) < var16) var16 = var17;
         if ((var17 = a[var8 + var5]) < var16) var16 = var17;
         if ((var17 = a[var8 + var15]) < var16) var16 = var17;
         if ((var17 = a[var8 + var14]) < var16) var16 = var17;
         if ((var17 = a[var8 + var4]) < var16) var16 = var17;
         if ((var17 = a[var8 + var13]) < var16) var16 = var17;
         if ((var17 = a[var8 + var12]) < var16) var16 = var17;
         if ((var17 = a[var8 + var11]) < var16) var16 = var17;
         if ((var17 = a[var8 + var10]) < var16) var16 = var17;
         if ((var17 = a[var8 + var9]) < var16) var16 = var17;

         var8 = c(var0, var2) * 3276;
         if ((var17 = a[var8 + var6]) < var16) var16 = var17;
         if ((var17 = a[var8 + var5]) < var16) var16 = var17;
         if ((var17 = a[var8 + var15]) < var16) var16 = var17;
         if ((var17 = a[var8 + var14]) < var16) var16 = var17;
         if ((var17 = a[var8 + var4]) < var16) var16 = var17;
         if ((var17 = a[var8 + var13]) < var16) var16 = var17;
         if ((var17 = a[var8 + var12]) < var16) var16 = var17;
         if ((var17 = a[var8 + var11]) < var16) var16 = var17;
         if ((var17 = a[var8 + var10]) < var16) var16 = var17;
         if ((var17 = a[var8 + var9]) < var16) var16 = var17;

         var8 = c(var0, var1) * 3276;
         if ((var17 = a[var8 + var6]) < var16) var16 = var17;
         if ((var17 = a[var8 + var5]) < var16) var16 = var17;
         if ((var17 = a[var8 + var15]) < var16) var16 = var17;
         if ((var17 = a[var8 + var14]) < var16) var16 = var17;
         if ((var17 = a[var8 + var4]) < var16) var16 = var17;
         if ((var17 = a[var8 + var13]) < var16) var16 = var17;
         if ((var17 = a[var8 + var12]) < var16) var16 = var17;
         if ((var17 = a[var8 + var11]) < var16) var16 = var17;
         if ((var17 = a[var8 + var10]) < var16) var16 = var17;
         if ((var17 = a[var8 + var9]) < var16) var16 = var17;

         return var16;
      } else {
         return a5c(var0, var1, var2, var3, varN, var4, var5, var6, var7, var8);
      }
   }

   // 5-card Omaha: 10-parameter no-flush hand evaluation
   private static int a5c(int var0, int var1, int var2, int var3, int varN, int var4, int var5, int var6, int var7, int var8) {
      var0 %= 13;
      var1 %= 13;
      var2 %= 13;
      var3 %= 13;
      varN %= 13;
      var4 %= 13;
      var5 %= 13;
      var6 %= 13;
      var7 %= 13;
      var8 %= 13;
      int var9;
      // Sort hole cards (5 cards)
      if (var0 > var1) {
         var9 = var0;
         var0 = var1;
         var1 = var9;
      }
      if (var2 > var3) {
         var9 = var2;
         var2 = var3;
         var3 = var9;
      }
      if (var0 > var2) {
         var9 = var0;
         var0 = var2;
         var2 = var9;
      }
      if (var1 > var3) {
         var9 = var1;
         var1 = var3;
         var3 = var9;
      }
      if (var1 > var2) {
         var9 = var1;
         var1 = var2;
         var2 = var9;
      }
      if (var3 > varN) {
         var9 = var3;
         var3 = varN;
         varN = var9;
         if (var2 > var3) {
            var9 = var2;
            var2 = var3;
            var3 = var9;
            if (var1 > var2) {
               var9 = var1;
               var1 = var2;
               var2 = var9;
               if (var0 > var1) {
                  var9 = var0;
                  var0 = var1;
                  var1 = var9;
               }
            }
         }
      }

      var0 = a5c(var0, var1, var2, var3, varN) * 455;

      // Sort board cards (5 cards)
      if (var4 > var5) {
         var9 = var4;
         var4 = var5;
         var5 = var9;
      }
      if (var7 > var8) {
         var9 = var7;
         var7 = var8;
         var8 = var9;
      }
      if (var6 > var8) {
         var9 = var6;
         var6 = var8;
         var8 = var9;
      }
      if (var6 > var7) {
         var9 = var6;
         var6 = var7;
         var7 = var9;
      }
      if (var4 > var7) {
         var9 = var4;
         var4 = var7;
         var7 = var9;
      }
      if (var4 > var6) {
         var9 = var4;
         var4 = var6;
         var6 = var9;
      }
      if (var5 > var8) {
         var9 = var5;
         var5 = var8;
         var8 = var9;
      }
      if (var5 > var7) {
         var9 = var5;
         var5 = var7;
         var7 = var9;
      }
      if (var5 > var6) {
         var9 = var5;
         var5 = var6;
         var6 = var9;
      }

      var1 = var0 + b(var4, var5, var6);
      var2 = var0 + b(var4, var5, var7);
      var3 = var0 + b(var4, var5, var8);
      var9 = var0 + b(var4, var6, var7);
      int var10 = var0 + b(var4, var6, var8);
      var4 = var0 + b(var4, var7, var8);
      int var11 = var0 + b(var5, var6, var7);
      int var12 = var0 + b(var5, var6, var8);
      var5 = var0 + b(var5, var7, var8);
      var0 += b(var6, var7, var8);
      short var13 = b5c[var0];
      var0 = Math.min(b5c[var5], var13);
      var0 = Math.min(b5c[var12], var0);
      var0 = Math.min(b5c[var11], var0);
      var0 = Math.min(b5c[var4], var0);
      short var14 = b5c[var10];
      var4 = Math.min(b5c[var9], var14);
      var4 = Math.min(b5c[var3], var4);
      var4 = Math.min(b5c[var2], var4);
      var4 = Math.min(b5c[var1], var4);
      return Math.min(var0, var4);
   }

   private static final int b(int var0, int var1, int var2) {
      return var0 * (var0 * (var0 - 39) + 506) / 6 + var1 * (25 - var1) / 2 + var2;
   }

   private static final int a(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      var0 %= 13;
      var1 %= 13;
      var2 %= 13;
      var3 %= 13;
      var4 %= 13;
      var5 %= 13;
      var6 %= 13;
      var7 %= 13;
      var8 %= 13;
      int var9;
      if (var0 > var1) {
         var9 = var0;
         var0 = var1;
         var1 = var9;
      }

      if (var2 > var3) {
         var9 = var2;
         var2 = var3;
         var3 = var9;
      }

      if (var0 > var2) {
         var9 = var0;
         var0 = var2;
         var2 = var9;
      }

      if (var1 > var3) {
         var9 = var1;
         var1 = var3;
         var3 = var9;
      }

      if (var1 > var2) {
         var9 = var1;
         var1 = var2;
         var2 = var9;
      }

      var0 = a(var0, var1, var2, var3) * 455;
      if (var4 > var5) {
         var9 = var4;
         var4 = var5;
         var5 = var9;
      }

      if (var7 > var8) {
         var9 = var7;
         var7 = var8;
         var8 = var9;
      }

      if (var6 > var8) {
         var9 = var6;
         var6 = var8;
         var8 = var9;
      }

      if (var6 > var7) {
         var9 = var6;
         var6 = var7;
         var7 = var9;
      }

      if (var4 > var7) {
         var9 = var4;
         var4 = var7;
         var7 = var9;
      }

      if (var4 > var6) {
         var9 = var4;
         var4 = var6;
         var6 = var9;
      }

      if (var5 > var8) {
         var9 = var5;
         var5 = var8;
         var8 = var9;
      }

      if (var5 > var7) {
         var9 = var5;
         var5 = var7;
         var7 = var9;
      }

      if (var5 > var6) {
         var9 = var5;
         var5 = var6;
         var6 = var9;
      }

      var1 = var0 + b(var4, var5, var6);
      var2 = var0 + b(var4, var5, var7);
      var3 = var0 + b(var4, var5, var8);
      var9 = var0 + b(var4, var6, var7);
      int var10 = var0 + b(var4, var6, var8);
      var4 = var0 + b(var4, var7, var8);
      int var11 = var0 + b(var5, var6, var7);
      int var12 = var0 + b(var5, var6, var8);
      var5 = var0 + b(var5, var7, var8);
      var0 += b(var6, var7, var8);
      short var13 = b[var0];
      var0 = Math.min(b[var5], var13);
      var0 = Math.min(b[var12], var0);
      var0 = Math.min(b[var11], var0);
      var0 = Math.min(b[var4], var0);
      short var14 = b[var10];
      var4 = Math.min(b[var9], var14);
      var4 = Math.min(b[var3], var4);
      var4 = Math.min(b[var2], var4);
      var4 = Math.min(b[var1], var4);
      return Math.min(var0, var4);
   }

   private static final boolean a(int var0, int var1, int var2, int var3, int var4) {
      if (var0 / 13 == var4) {
         return var1 / 13 == var4 || var2 / 13 == var4 || var3 / 13 == var4;
      } else if (var1 / 13 == var4) {
         return var2 / 13 == var4 || var3 / 13 == var4;
      } else {
         return var2 / 13 == var4 && var3 / 13 == var4;
      }
   }

   public static final int a(int[] var0, int var1) {
      return a(var0[0], var0[1], var0[2], var0[3], var0[4], var0[5], var0[6], var0[7], var0[8], var1);
   }

   // 5-card Omaha version of hand evaluation with flush suit
   public static final int a5c(int[] var0, int var1) {
      return a5c(var0[0], var0[1], var0[2], var0[3], var0[4], var0[5], var0[6], var0[7], var0[8], var0[9], var1);
   }

   // 5-card Omaha partial board evaluation (flop: varCardNum=8, turn: varCardNum=9)
   public static int a5c(int[] varCards, int var9, int varCardNum) {
      int var0;
      int var1;
      int var2;
      int var3;
      int varN;

      int var4;
      int var5;
      int var6;
      int var7;

      if (varCardNum == 9) {
    	 var0 = varCards[0] / 13 == var9 ? a(varCards[0], 1) : varCards[0] % 13;
         var1 = varCards[1] / 13 == var9 ? a(varCards[1], 1) : varCards[1] % 13;
         var2 = varCards[2] / 13 == var9 ? a(varCards[2], 1) : varCards[2] % 13;
         var3 = varCards[3] / 13 == var9 ? a(varCards[3], 1) : varCards[3] % 13;
         varN = varCards[4] / 13 == var9 ? a(varCards[4], 1) : varCards[4] % 13;

         var4 = varCards[5] / 13 == var9 ? a(varCards[5], 1) : varCards[5] % 13;
         var5 = varCards[6] / 13 == var9 ? a(varCards[6], 1) : varCards[6] % 13;
         var6 = varCards[7] / 13 == var9 ? a(varCards[7], 1) : varCards[7] % 13;
         var7 = varCards[8] / 13 == var9 ? a(varCards[8], 1) : varCards[8] % 13;


         if (var0 > var1) {
            var9 = var0;
            var0 = var1;
            var1 = var9;
         }

         if (var2 > var3) {
            var9 = var2;
            var2 = var3;
            var3 = var9;
         }

         if (var0 > var2) {
            var9 = var0;
            var0 = var2;
            var2 = var9;
         }

         if (var1 > var3) {
            var9 = var1;
            var1 = var3;
            var3 = var9;
         }

         if (var1 > var2) {
            var9 = var1;
            var1 = var2;
            var2 = var9;
         }

         if (var3 > varN) {
	        var9 = var3;
	        var3 = varN;
	        varN = var9;

	        if (var2 > var3) {
	        	var9 = var2;
	        	var2 = var3;
	        	var3 = var9;

	        	if (var1 > var2) {
	        		var9 = var1;
	        		var1 = var2;
	        		var2 = var9;

	        		if (var0 > var1) {
	        			var9 = var0;
	        			var0 = var1;
	        			var1 = var9;
	        		}
	        	}
	        }
	     }

         //board

         if (var4 > var5) {
            var9 = var4;
            var4 = var5;
            var5 = var9;
         }

         if (var6 > var7) {
            var9 = var6;
            var6 = var7;
            var7 = var9;
         }

         if (var4 > var7) {
            var9 = var4;
            var4 = var7;
            var7 = var9;
         }

         if (var4 > var6) {
            var9 = var4;
            var4 = var6;
            var6 = var9;
         }

         if (var5 > var7) {
            var9 = var5;
            var5 = var7;
            var7 = var9;
         }

         if (var5 > var6) {
            var9 = var5;
            var5 = var6;
            var6 = var9;
         }

         var9 = a(var4, var5, var6);
         int var10 = a(var4, var5, var7);
         int var12 = a(var4, var6, var7);
         int var14 = a(var5, var6, var7);

         short var17;
         short var16;

         int var8;


         var8 = c(var3, varN) * 3276;
         var16 = a[var8 + var14];


         if ((var17 = a[var8 + var14]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var12]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var10]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var9]) < var16) {
            var16 = var17;
         }

         var8 = c(var2, varN) * 3276;
         if ((var17 = a[var8 + var14]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var12]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var10]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var9]) < var16) {
            var16 = var17;
         }

         var8 = c(var1, varN) * 3276;
         if ((var17 = a[var8 + var14]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var12]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var10]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var9]) < var16) {
            var16 = var17;
         }

         var8 = c(var0, varN) * 3276;
         if ((var17 = a[var8 + var14]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var12]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var10]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var9]) < var16) {
            var16 = var17;
         }

         var8 = c(var2, var3) * 3276;
         if ((var17 = a[var8 + var14]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var12]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var10]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var9]) < var16) {
            var16 = var17;
         }

         var8 = c(var1, var3) * 3276;
         if ((var17 = a[var8 + var14]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var12]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var10]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var9]) < var16) {
            var16 = var17;
         }

         var8 = c(var0, var3) * 3276;
         if ((var17 = a[var8 + var14]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var12]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var10]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var9]) < var16) {
            var16 = var17;
         }

         var8 = c(var1, var2) * 3276;
         if ((var17 = a[var8 + var14]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var12]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var10]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var9]) < var16) {
            var16 = var17;
         }

         var8 = c(var0, var2) * 3276;
         if ((var17 = a[var8 + var14]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var12]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var10]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var9]) < var16) {
            var16 = var17;
         }

         var8 = c(var0, var1) * 3276;
         if ((var17 = a[var8 + var14]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var12]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var10]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var9]) < var16) {
            var16 = var17;
         }

         return var16;
      } else if (varCardNum == 8) {
    	  var0 = varCards[0] / 13 == var9 ? a(varCards[0], 1) : varCards[0] % 13;
          var1 = varCards[1] / 13 == var9 ? a(varCards[1], 1) : varCards[1] % 13;
          var2 = varCards[2] / 13 == var9 ? a(varCards[2], 1) : varCards[2] % 13;
          var3 = varCards[3] / 13 == var9 ? a(varCards[3], 1) : varCards[3] % 13;
          varN = varCards[4] / 13 == var9 ? a(varCards[4], 1) : varCards[4] % 13;

          var4 = varCards[5] / 13 == var9 ? a(varCards[5], 1) : varCards[5] % 13;
          var5 = varCards[6] / 13 == var9 ? a(varCards[6], 1) : varCards[6] % 13;
          var6 = varCards[7] / 13 == var9 ? a(varCards[7], 1) : varCards[7] % 13;


          if (var0 > var1) {
             var9 = var0;
             var0 = var1;
             var1 = var9;
          }

          if (var2 > var3) {
             var9 = var2;
             var2 = var3;
             var3 = var9;
          }

          if (var0 > var2) {
             var9 = var0;
             var0 = var2;
             var2 = var9;
          }

          if (var1 > var3) {
             var9 = var1;
             var1 = var3;
             var3 = var9;
          }

          if (var1 > var2) {
             var9 = var1;
             var1 = var2;
             var2 = var9;
          }

          if (var3 > varN) {
	        var9 = var3;
	        var3 = varN;
	        varN = var9;

	        if (var2 > var3) {
	        	var9 = var2;
	        	var2 = var3;
	        	var3 = var9;

	        	if (var1 > var2) {
	        		var9 = var1;
	        		var1 = var2;
	        		var2 = var9;

	        		if (var0 > var1) {
	        			var9 = var0;
	        			var0 = var1;
	        			var1 = var9;
	        		}
	        	}
	        }
	     }

          //board

          if (var4 > var5) {
             var9 = var4;
             var4 = var5;
             var5 = var9;
          }

          if (var4 > var6) {
             var9 = var4;
             var4 = var6;
             var6 = var9;
          }

          if (var5 > var6) {
             var9 = var5;
             var5 = var6;
             var6 = var9;
          }

          var9 = a(var4, var5, var6);

          short var17;
          short var16;

          int var8;


          var8 = c(var3, varN) * 3276;
          var16 = a[var8 + var9];

          if ((var17 = a[var8 + var9]) < var16) {
             var16 = var17;
          }

          var8 = c(var2, varN) * 3276;
          if ((var17 = a[var8 + var9]) < var16) {
             var16 = var17;
          }

          var8 = c(var1, varN) * 3276;
          if ((var17 = a[var8 + var9]) < var16) {
             var16 = var17;
          }

          var8 = c(var0, varN) * 3276;
          if ((var17 = a[var8 + var9]) < var16) {
             var16 = var17;
          }

          var8 = c(var2, var3) * 3276;
          if ((var17 = a[var8 + var9]) < var16) {
             var16 = var17;
          }

          var8 = c(var1, var3) * 3276;
          if ((var17 = a[var8 + var9]) < var16) {
             var16 = var17;
          }

          var8 = c(var0, var3) * 3276;
          if ((var17 = a[var8 + var9]) < var16) {
             var16 = var17;
          }

          var8 = c(var1, var2) * 3276;
          if ((var17 = a[var8 + var9]) < var16) {
             var16 = var17;
          }

          var8 = c(var0, var2) * 3276;
          if ((var17 = a[var8 + var9]) < var16) {
             var16 = var17;
          }

          var8 = c(var0, var1) * 3276;
          if ((var17 = a[var8 + var9]) < var16) {
             var16 = var17;
          }

          return var16;
      } else {
         return a5c(varCards[0], varCards[1], varCards[2], varCards[3], varCards[4], varCards[5], varCards[6], varCards[7], varCards[8], varCards[9], var9);
      }
   }

   private static int a(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      if (var9 >= 0 && a(var0, var1, var2, var3, var9)) {
         var0 = var0 / 13 == var9 ? a(var0, 1) : var0 % 13;
         var1 = var1 / 13 == var9 ? a(var1, 1) : var1 % 13;
         var2 = var2 / 13 == var9 ? a(var2, 1) : var2 % 13;
         var3 = var3 / 13 == var9 ? a(var3, 1) : var3 % 13;
         var4 = var4 / 13 == var9 ? a(var4, 1) : var4 % 13;
         var5 = var5 / 13 == var9 ? a(var5, 1) : var5 % 13;
         var6 = var6 / 13 == var9 ? a(var6, 1) : var6 % 13;
         var7 = var7 / 13 == var9 ? a(var7, 1) : var7 % 13;
         var8 = var8 / 13 == var9 ? a(var8, 1) : var8 % 13;
         if (var0 > var1) {
            var9 = var0;
            var0 = var1;
            var1 = var9;
         }

         if (var2 > var3) {
            var9 = var2;
            var2 = var3;
            var3 = var9;
         }

         if (var0 > var2) {
            var9 = var0;
            var0 = var2;
            var2 = var9;
         }

         if (var1 > var3) {
            var9 = var1;
            var1 = var3;
            var3 = var9;
         }

         if (var1 > var2) {
            var9 = var1;
            var1 = var2;
            var2 = var9;
         }

         if (var4 > var5) {
            var9 = var4;
            var4 = var5;
            var5 = var9;
         }

         if (var7 > var8) {
            var9 = var7;
            var7 = var8;
            var8 = var9;
         }

         if (var6 > var8) {
            var9 = var6;
            var6 = var8;
            var8 = var9;
         }

         if (var6 > var7) {
            var9 = var6;
            var6 = var7;
            var7 = var9;
         }

         if (var4 > var7) {
            var9 = var4;
            var4 = var7;
            var7 = var9;
         }

         if (var4 > var6) {
            var9 = var4;
            var4 = var6;
            var6 = var9;
         }

         if (var5 > var8) {
            var9 = var5;
            var5 = var8;
            var8 = var9;
         }

         if (var5 > var7) {
            var9 = var5;
            var5 = var7;
            var7 = var9;
         }

         if (var5 > var6) {
            var9 = var5;
            var5 = var6;
            var6 = var9;
         }

         var9 = a(var4, var5, var6);
         int var10 = a(var4, var5, var7);
         int var11 = a(var4, var5, var8);
         int var12 = a(var4, var6, var7);
         int var13 = a(var4, var6, var8);
         var4 = a(var4, var7, var8);
         int var14 = a(var5, var6, var7);
         int var15 = a(var5, var6, var8);
         var5 = a(var5, var7, var8);
         var6 = a(var6, var7, var8);
         var8 = c(var2, var3) * 3276;
         short var16 = a[var8 + var6];
         short var17;
         if ((var17 = a[var8 + var5]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var15]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var14]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var4]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var13]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var12]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var11]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var10]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var9]) < var16) {
            var16 = var17;
         }

         var8 = c(var1, var3) * 3276;
         if ((var17 = a[var8 + var6]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var5]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var15]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var14]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var4]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var13]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var12]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var11]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var10]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var9]) < var16) {
            var16 = var17;
         }

         var8 = c(var0, var3) * 3276;
         if ((var17 = a[var8 + var6]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var5]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var15]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var14]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var4]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var13]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var12]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var11]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var10]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var9]) < var16) {
            var16 = var17;
         }

         var8 = c(var1, var2) * 3276;
         if ((var17 = a[var8 + var6]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var5]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var15]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var14]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var4]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var13]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var12]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var11]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var10]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var9]) < var16) {
            var16 = var17;
         }

         var8 = c(var0, var2) * 3276;
         if ((var17 = a[var8 + var6]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var5]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var15]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var14]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var4]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var13]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var12]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var11]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var10]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var9]) < var16) {
            var16 = var17;
         }

         var8 = c(var0, var1) * 3276;
         if ((var17 = a[var8 + var6]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var5]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var15]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var14]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var4]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var13]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var12]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var11]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var10]) < var16) {
            var16 = var17;
         }

         if ((var17 = a[var8 + var9]) < var16) {
            var16 = var17;
         }

         return var16;
      } else {
         return a(var0, var1, var2, var3, var4, var5, var6, var7, var8);
      }
   }

   public static int b(int[] var0, int[] var1, int var2) {
      int var3;
      int var4;
      int var5;
      int var6;
      int var7;
      int var8;
      int var10000;
      int var16;
      int var10001;
      int var17;
      int var10002;
      int var10003;
      int var10004;
      if (var1.length == 3) {
         var10000 = var0[0];
         var10001 = var0[1];
         var10002 = var1[0];
         var10003 = var1[1];
         var10004 = var1[2];
         var5 = var2;
         var4 = var10004;
         var3 = var10003;
         var2 = var10002;
         var17 = var10001;
         var16 = var10000;
         var16 = var10000 / 13 == var5 ? a(var16, 1) : var16 % 13;
         var17 = var17 / 13 == var5 ? a(var17, 1) : var17 % 13;
         var2 = var2 / 13 == var5 ? a(var2, 1) : var2 % 13;
         var3 = var3 / 13 == var5 ? a(var3, 1) : var3 % 13;
         var4 = var4 / 13 == var5 ? a(var4, 1) : var4 % 13;
         if (var16 > var17) {
            var6 = var16;
            var16 = var17;
            var17 = var6;
         }

         if (var2 > var3) {
            var6 = var2;
            var2 = var3;
            var3 = var6;
         }

         if (var2 > var4) {
            var6 = var2;
            var2 = var4;
            var4 = var6;
         }

         if (var3 > var4) {
            var6 = var3;
            var3 = var4;
            var4 = var6;
         }

         var7 = a(var2, var3, var4);
         var8 = c(var16, var17) * 3276;
         return a[var8 + var7];
      } else {
         int var9;
         int var10005;
         if (var1.length == 4) {
            var10000 = var0[0];
            var10001 = var0[1];
            var10002 = var1[0];
            var10003 = var1[1];
            var10004 = var1[2];
            var10005 = var1[3];
            var6 = var2;
            var5 = var10005;
            var4 = var10004;
            var3 = var10003;
            var2 = var10002;
            var17 = var10001;
            var16 = var10000;
            var16 = var10000 / 13 == var6 ? a(var16, 1) : var16 % 13;
            var17 = var17 / 13 == var6 ? a(var17, 1) : var17 % 13;
            var2 = var2 / 13 == var6 ? a(var2, 1) : var2 % 13;
            var3 = var3 / 13 == var6 ? a(var3, 1) : var3 % 13;
            var4 = var4 / 13 == var6 ? a(var4, 1) : var4 % 13;
            var5 = var5 / 13 == var6 ? a(var5, 1) : var5 % 13;
            if (var16 > var17) {
               var7 = var16;
               var16 = var17;
               var17 = var7;
            }

            if (var2 > var3) {
               var7 = var2;
               var2 = var3;
               var3 = var7;
            }

            if (var4 > var5) {
               var7 = var4;
               var4 = var5;
               var5 = var7;
            }

            if (var2 > var5) {
               var7 = var2;
               var2 = var5;
               var5 = var7;
            }

            if (var2 > var4) {
               var7 = var2;
               var2 = var4;
               var4 = var7;
            }

            if (var3 > var5) {
               var7 = var3;
               var3 = var5;
               var5 = var7;
            }

            if (var3 > var4) {
               var7 = var3;
               var3 = var4;
               var4 = var7;
            }

            var6 = a(var2, var3, var4);
            var7 = a(var2, var3, var5);
            var2 = a(var2, var4, var5);
            var9 = a(var3, var4, var5);
            var8 = c(var16, var17) * 3276;
            short var20 = a[var8 + var9];
            short var21;
            if ((var21 = a[var8 + var2]) < var20) {
               var20 = var21;
            }

            if ((var21 = a[var8 + var7]) < var20) {
               var20 = var21;
            }

            if ((var21 = a[var8 + var6]) < var20) {
               var20 = var21;
            }

            return var20;
         } else {
            var10000 = var0[0];
            var10001 = var0[1];
            var10002 = var1[0];
            var10003 = var1[1];
            var10004 = var1[2];
            var10005 = var1[3];
            int var10006 = var1[4];
            var7 = var2;
            var6 = var10006;
            var5 = var10005;
            var4 = var10004;
            var3 = var10003;
            var2 = var10002;
            var17 = var10001;
            var16 = var10000;
            var16 = var10000 / 13 == var7 ? a(var16, 1) : var16 % 13;
            var17 = var17 / 13 == var7 ? a(var17, 1) : var17 % 13;
            var2 = var2 / 13 == var7 ? a(var2, 1) : var2 % 13;
            var3 = var3 / 13 == var7 ? a(var3, 1) : var3 % 13;
            var4 = var4 / 13 == var7 ? a(var4, 1) : var4 % 13;
            var5 = var5 / 13 == var7 ? a(var5, 1) : var5 % 13;
            var6 = var6 / 13 == var7 ? a(var6, 1) : var6 % 13;
            if (var16 > var17) {
               var8 = var16;
               var16 = var17;
               var17 = var8;
            }

            if (var2 > var3) {
               var8 = var2;
               var2 = var3;
               var3 = var8;
            }

            if (var5 > var6) {
               var8 = var5;
               var5 = var6;
               var6 = var8;
            }

            if (var4 > var6) {
               var8 = var4;
               var4 = var6;
               var6 = var8;
            }

            if (var4 > var5) {
               var8 = var4;
               var4 = var5;
               var5 = var8;
            }

            if (var2 > var5) {
               var8 = var2;
               var2 = var5;
               var5 = var8;
            }

            if (var2 > var4) {
               var8 = var2;
               var2 = var4;
               var4 = var8;
            }

            if (var3 > var6) {
               var8 = var3;
               var3 = var6;
               var6 = var8;
            }

            if (var3 > var5) {
               var8 = var3;
               var3 = var5;
               var5 = var8;
            }

            if (var3 > var4) {
               var8 = var3;
               var3 = var4;
               var4 = var8;
            }

            var9 = a(var2, var3, var4);
            int var10 = a(var2, var3, var5);
            int var11 = a(var2, var3, var6);
            var8 = a(var2, var4, var5);
            int var12 = a(var2, var4, var6);
            int var13 = a(var2, var5, var6);
            int var14 = a(var3, var4, var5);
            int var15 = a(var3, var4, var6);
            var3 = a(var3, var5, var6);
            var2 = a(var4, var5, var6);
            var6 = c(var16, var17) * 3276;
            short var18 = a[var6 + var2];
            short var19;
            if ((var19 = a[var6 + var3]) < var18) {
               var18 = var19;
            }

            if ((var19 = a[var6 + var15]) < var18) {
               var18 = var19;
            }

            if ((var19 = a[var6 + var14]) < var18) {
               var18 = var19;
            }

            if ((var19 = a[var6 + var13]) < var18) {
               var18 = var19;
            }

            if ((var19 = a[var6 + var12]) < var18) {
               var18 = var19;
            }

            if ((var19 = a[var6 + var8]) < var18) {
               var18 = var19;
            }

            if ((var19 = a[var6 + var11]) < var18) {
               var18 = var19;
            }

            if ((var19 = a[var6 + var10]) < var18) {
               var18 = var19;
            }

            if ((var19 = a[var6 + var9]) < var18) {
               var18 = var19;
            }

            return var18;
         }
      }
   }

   public static int a(int[] var0, int var1, int var2) {
      int var3;
      int var4;
      int var5;
      int var6;
      int var7;
      int var8;
      int var11;
      if (var2 == 8) {
         var2 = (var0 = var0)[0] / 13 == var1 ? a(var0[0], 1) : var0[0] % 13;
         var3 = var0[1] / 13 == var1 ? a(var0[1], 1) : var0[1] % 13;
         var4 = var0[2] / 13 == var1 ? a(var0[2], 1) : var0[2] % 13;
         var5 = var0[3] / 13 == var1 ? a(var0[3], 1) : var0[3] % 13;
         var6 = var0[4] / 13 == var1 ? a(var0[4], 1) : var0[4] % 13;
         var7 = var0[5] / 13 == var1 ? a(var0[5], 1) : var0[5] % 13;
         var8 = var0[6] / 13 == var1 ? a(var0[6], 1) : var0[6] % 13;
         var11 = var0[7] / 13 == var1 ? a(var0[7], 1) : var0[7] % 13;
         if (var2 > var3) {
            var1 = var2;
            var2 = var3;
            var3 = var1;
         }

         if (var4 > var5) {
            var1 = var4;
            var4 = var5;
            var5 = var1;
         }

         if (var2 > var4) {
            var1 = var2;
            var2 = var4;
            var4 = var1;
         }

         if (var3 > var5) {
            var1 = var3;
            var3 = var5;
            var5 = var1;
         }

         if (var3 > var4) {
            var1 = var3;
            var3 = var4;
            var4 = var1;
         }

         if (var6 > var7) {
            var1 = var6;
            var6 = var7;
            var7 = var1;
         }

         if (var8 > var11) {
            var1 = var8;
            var8 = var11;
            var11 = var1;
         }

         if (var6 > var11) {
            var1 = var6;
            var6 = var11;
            var11 = var1;
         }

         if (var6 > var8) {
            var1 = var6;
            var6 = var8;
            var8 = var1;
         }

         if (var7 > var11) {
            var1 = var7;
            var7 = var11;
            var11 = var1;
         }

         if (var7 > var8) {
            var1 = var7;
            var7 = var8;
            var8 = var1;
         }

         var1 = a(var6, var7, var8);
         int var14 = a(var6, var7, var11);
         var6 = a(var6, var8, var11);
         var7 = a(var7, var8, var11);
         var11 = c(var4, var5) * 3276;
         short var13;
         short var10 = var13 = a[var11 + var7];
         if (var13 < var10) {
            var10 = var13;
         }

         if ((var13 = a[var11 + var6]) < var10) {
            var10 = var13;
         }

         if ((var13 = a[var11 + var14]) < var10) {
            var10 = var13;
         }

         if ((var13 = a[var11 + var1]) < var10) {
            var10 = var13;
         }

         var11 = c(var3, var5) * 3276;
         if ((var13 = a[var11 + var7]) < var10) {
            var10 = var13;
         }

         if ((var13 = a[var11 + var6]) < var10) {
            var10 = var13;
         }

         if ((var13 = a[var11 + var14]) < var10) {
            var10 = var13;
         }

         if ((var13 = a[var11 + var1]) < var10) {
            var10 = var13;
         }

         var11 = c(var2, var5) * 3276;
         if ((var13 = a[var11 + var7]) < var10) {
            var10 = var13;
         }

         if ((var13 = a[var11 + var6]) < var10) {
            var10 = var13;
         }

         if ((var13 = a[var11 + var14]) < var10) {
            var10 = var13;
         }

         if ((var13 = a[var11 + var1]) < var10) {
            var10 = var13;
         }

         var11 = c(var3, var4) * 3276;
         if ((var13 = a[var11 + var7]) < var10) {
            var10 = var13;
         }

         if ((var13 = a[var11 + var6]) < var10) {
            var10 = var13;
         }

         if ((var13 = a[var11 + var14]) < var10) {
            var10 = var13;
         }

         if ((var13 = a[var11 + var1]) < var10) {
            var10 = var13;
         }

         var11 = c(var2, var4) * 3276;
         if ((var13 = a[var11 + var7]) < var10) {
            var10 = var13;
         }

         if ((var13 = a[var11 + var6]) < var10) {
            var10 = var13;
         }

         if ((var13 = a[var11 + var14]) < var10) {
            var10 = var13;
         }

         if ((var13 = a[var11 + var1]) < var10) {
            var10 = var13;
         }

         var11 = c(var2, var3) * 3276;
         if ((var13 = a[var11 + var7]) < var10) {
            var10 = var13;
         }

         if ((var13 = a[var11 + var6]) < var10) {
            var10 = var13;
         }

         if ((var13 = a[var11 + var14]) < var10) {
            var10 = var13;
         }

         if ((var13 = a[var11 + var1]) < var10) {
            var10 = var13;
         }

         return var10;
      } else if (var2 == 7) {
         var2 = (var0 = var0)[0] / 13 == var1 ? a(var0[0], 1) : var0[0] % 13;
         var3 = var0[1] / 13 == var1 ? a(var0[1], 1) : var0[1] % 13;
         var4 = var0[2] / 13 == var1 ? a(var0[2], 1) : var0[2] % 13;
         var5 = var0[3] / 13 == var1 ? a(var0[3], 1) : var0[3] % 13;
         var6 = var0[4] / 13 == var1 ? a(var0[4], 1) : var0[4] % 13;
         var7 = var0[5] / 13 == var1 ? a(var0[5], 1) : var0[5] % 13;
         var8 = var0[6] / 13 == var1 ? a(var0[6], 1) : var0[6] % 13;
         if (var2 > var3) {
            var11 = var2;
            var2 = var3;
            var3 = var11;
         }

         if (var4 > var5) {
            var11 = var4;
            var4 = var5;
            var5 = var11;
         }

         if (var2 > var4) {
            var11 = var2;
            var2 = var4;
            var4 = var11;
         }

         if (var3 > var5) {
            var11 = var3;
            var3 = var5;
            var5 = var11;
         }

         if (var3 > var4) {
            var11 = var3;
            var3 = var4;
            var4 = var11;
         }

         if (var6 > var7) {
            var11 = var6;
            var6 = var7;
            var7 = var11;
         }

         if (var6 > var8) {
            var11 = var6;
            var6 = var8;
            var8 = var11;
         }

         if (var7 > var8) {
            var11 = var7;
            var7 = var8;
            var8 = var11;
         }

         var11 = a(var6, var7, var8);
         var1 = c(var4, var5) * 3276;
         short var12;
         short var9 = var12 = a[var1 + var11];
         if (var12 < var9) {
            var9 = var12;
         }

         var1 = c(var3, var5) * 3276;
         if ((var12 = a[var1 + var11]) < var9) {
            var9 = var12;
         }

         var1 = c(var2, var5) * 3276;
         if ((var12 = a[var1 + var11]) < var9) {
            var9 = var12;
         }

         var1 = c(var3, var4) * 3276;
         if ((var12 = a[var1 + var11]) < var9) {
            var9 = var12;
         }

         var1 = c(var2, var4) * 3276;
         if ((var12 = a[var1 + var11]) < var9) {
            var9 = var12;
         }

         var1 = c(var2, var3) * 3276;
         if ((var12 = a[var1 + var11]) < var9) {
            var9 = var12;
         }

         return var9;
      } else {
         return a(var0[0], var0[1], var0[2], var0[3], var0[4], var0[5], var0[6], var0[7], var0[8], var1);
      }
   }

   public static int b(int[] var0, int[] var1) {
      int var15 = a(var0, 4, var1);
      int var2 = var0[0] / 13 == var15 ? a(var0[0], 1) : var0[0] % 13;
      int var3 = var0[1] / 13 == var15 ? a(var0[1], 1) : var0[1] % 13;
      int var4 = var0[4] / 13 == var15 ? a(var0[4], 1) : var0[4] % 13;
      int var5 = var0[5] / 13 == var15 ? a(var0[5], 1) : var0[5] % 13;
      int var6 = var0[6] / 13 == var15 ? a(var0[6], 1) : var0[6] % 13;
      int var7 = var0[7] / 13 == var15 ? a(var0[7], 1) : var0[7] % 13;
      int var14 = var0[8] / 13 == var15 ? a(var0[8], 1) : var0[8] % 13;
      if (var2 > var3) {
         var15 = var2;
         var2 = var3;
         var3 = var15;
      }

      if (var4 > var5) {
         var15 = var4;
         var4 = var5;
         var5 = var15;
      }

      if (var7 > var14) {
         var15 = var7;
         var7 = var14;
         var14 = var15;
      }

      if (var6 > var14) {
         var15 = var6;
         var6 = var14;
         var14 = var15;
      }

      if (var6 > var7) {
         var15 = var6;
         var6 = var7;
         var7 = var15;
      }

      if (var4 > var7) {
         var15 = var4;
         var4 = var7;
         var7 = var15;
      }

      if (var4 > var6) {
         var15 = var4;
         var4 = var6;
         var6 = var15;
      }

      if (var5 > var14) {
         var15 = var5;
         var5 = var14;
         var14 = var15;
      }

      if (var5 > var7) {
         var15 = var5;
         var5 = var7;
         var7 = var15;
      }

      if (var5 > var6) {
         var15 = var5;
         var5 = var6;
         var6 = var15;
      }

      var15 = a(var4, var5, var6);
      int var8 = a(var4, var5, var7);
      int var9 = a(var4, var5, var14);
      int var10 = a(var4, var6, var7);
      int var11 = a(var4, var6, var14);
      var4 = a(var4, var7, var14);
      int var12 = a(var5, var6, var7);
      int var13 = a(var5, var6, var14);
      var5 = a(var5, var7, var14);
      var6 = a(var6, var7, var14);
      var14 = c(var2, var3) * 3276;
      short var17 = a[var14 + var6];
      short var16;
      if ((var16 = a[var14 + var5]) < var17) {
         var17 = var16;
      }

      if ((var16 = a[var14 + var13]) < var17) {
         var17 = var16;
      }

      if ((var16 = a[var14 + var12]) < var17) {
         var17 = var16;
      }

      if ((var16 = a[var14 + var4]) < var17) {
         var17 = var16;
      }

      if ((var16 = a[var14 + var11]) < var17) {
         var17 = var16;
      }

      if ((var16 = a[var14 + var10]) < var17) {
         var17 = var16;
      }

      if ((var16 = a[var14 + var9]) < var17) {
         var17 = var16;
      }

      if ((var16 = a[var14 + var8]) < var17) {
         var17 = var16;
      }

      if ((var16 = a[var14 + var15]) < var17) {
         var17 = var16;
      }

      return var17;
   }
}

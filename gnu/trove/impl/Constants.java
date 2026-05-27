package gnu.trove.impl;

public final class Constants {
   private static final boolean h = System.getProperty("gnu.trove.verbose", (String)null) != null;
   public static final byte DEFAULT_BYTE_NO_ENTRY_VALUE;
   public static final short DEFAULT_SHORT_NO_ENTRY_VALUE;
   public static final char DEFAULT_CHAR_NO_ENTRY_VALUE;
   public static final int d;
   public static final long DEFAULT_LONG_NO_ENTRY_VALUE;
   public static final float f;
   public static final double DEFAULT_DOUBLE_NO_ENTRY_VALUE;

   static {
      String var1 = System.getProperty("gnu.trove.no_entry.byte", "0");
      byte var0;
      if ("MAX_VALUE".equalsIgnoreCase(var1)) {
         var0 = 127;
      } else if ("MIN_VALUE".equalsIgnoreCase(var1)) {
         var0 = -128;
      } else {
         var0 = Byte.valueOf(var1);
      }

      if (var0 > 127) {
         var0 = 127;
      } else if (var0 < -128) {
         var0 = -128;
      }

      DEFAULT_BYTE_NO_ENTRY_VALUE = var0;
      if (h) {
         System.out.println("DEFAULT_BYTE_NO_ENTRY_VALUE: " + DEFAULT_BYTE_NO_ENTRY_VALUE);
      }

      var1 = System.getProperty("gnu.trove.no_entry.short", "0");
      short var2;
      if ("MAX_VALUE".equalsIgnoreCase(var1)) {
         var2 = 32767;
      } else if ("MIN_VALUE".equalsIgnoreCase(var1)) {
         var2 = -32768;
      } else {
         var2 = Short.valueOf(var1);
      }

      if (var2 > 32767) {
         var2 = 32767;
      } else if (var2 < -32768) {
         var2 = -32768;
      }

      DEFAULT_SHORT_NO_ENTRY_VALUE = var2;
      if (h) {
         System.out.println("DEFAULT_SHORT_NO_ENTRY_VALUE: " + DEFAULT_SHORT_NO_ENTRY_VALUE);
      }

      var1 = System.getProperty("gnu.trove.no_entry.char", "\u0000");
      char var3;
      if ("MAX_VALUE".equalsIgnoreCase(var1)) {
         var3 = '\uffff';
      } else if ("MIN_VALUE".equalsIgnoreCase(var1)) {
         var3 = 0;
      } else {
         var3 = var1.toCharArray()[0];
      }

      if (var3 > '\uffff') {
         var3 = '\uffff';
      } else if (var3 < 0) {
         var3 = 0;
      }

      DEFAULT_CHAR_NO_ENTRY_VALUE = var3;
      if (h) {
         System.out.println("DEFAULT_CHAR_NO_ENTRY_VALUE: " + Integer.valueOf(var3));
      }

      var1 = System.getProperty("gnu.trove.no_entry.int", "0");
      int var4;
      if ("MAX_VALUE".equalsIgnoreCase(var1)) {
         var4 = Integer.MAX_VALUE;
      } else if ("MIN_VALUE".equalsIgnoreCase(var1)) {
         var4 = Integer.MIN_VALUE;
      } else {
         var4 = Integer.valueOf(var1);
      }

      d = var4;
      if (h) {
         System.out.println("DEFAULT_INT_NO_ENTRY_VALUE: " + d);
      }

      String var5 = System.getProperty("gnu.trove.no_entry.long", "0");
      long var6;
      if ("MAX_VALUE".equalsIgnoreCase(var5)) {
         var6 = Long.MAX_VALUE;
      } else if ("MIN_VALUE".equalsIgnoreCase(var5)) {
         var6 = Long.MIN_VALUE;
      } else {
         var6 = Long.valueOf(var5);
      }

      DEFAULT_LONG_NO_ENTRY_VALUE = var6;
      if (h) {
         System.out.println("DEFAULT_LONG_NO_ENTRY_VALUE: " + DEFAULT_LONG_NO_ENTRY_VALUE);
      }

      var1 = System.getProperty("gnu.trove.no_entry.float", "0");
      float var7;
      if ("MAX_VALUE".equalsIgnoreCase(var1)) {
         var7 = Float.MAX_VALUE;
      } else if ("MIN_VALUE".equalsIgnoreCase(var1)) {
         var7 = Float.MIN_VALUE;
      } else if ("MIN_NORMAL".equalsIgnoreCase(var1)) {
         var7 = 1.17549435E-38F;
      } else if ("NEGATIVE_INFINITY".equalsIgnoreCase(var1)) {
         var7 = Float.NEGATIVE_INFINITY;
      } else if ("POSITIVE_INFINITY".equalsIgnoreCase(var1)) {
         var7 = Float.POSITIVE_INFINITY;
      } else {
         var7 = Float.valueOf(var1);
      }

      f = var7;
      if (h) {
         System.out.println("DEFAULT_FLOAT_NO_ENTRY_VALUE: " + f);
      }

      var5 = System.getProperty("gnu.trove.no_entry.double", "0");
      double var8;
      if ("MAX_VALUE".equalsIgnoreCase(var5)) {
         var8 = Double.MAX_VALUE;
      } else if ("MIN_VALUE".equalsIgnoreCase(var5)) {
         var8 = Double.MIN_VALUE;
      } else if ("MIN_NORMAL".equalsIgnoreCase(var5)) {
         var8 = 2.2250738585072014E-308D;
      } else if ("NEGATIVE_INFINITY".equalsIgnoreCase(var5)) {
         var8 = Double.NEGATIVE_INFINITY;
      } else if ("POSITIVE_INFINITY".equalsIgnoreCase(var5)) {
         var8 = Double.POSITIVE_INFINITY;
      } else {
         var8 = Double.valueOf(var5);
      }

      DEFAULT_DOUBLE_NO_ENTRY_VALUE = var8;
      if (h) {
         System.out.println("DEFAULT_DOUBLE_NO_ENTRY_VALUE: " + DEFAULT_DOUBLE_NO_ENTRY_VALUE);
      }

   }
}

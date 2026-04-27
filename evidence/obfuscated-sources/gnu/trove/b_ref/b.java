package gnu.trove.b_ref;

public final class b {
   // $FF: synthetic field
   private static boolean a = !b.class.desiredAssertionStatus();

   public static int a(double var0) {
      if (!a && Double.isNaN(var0)) {
         throw new AssertionError("Values of NaN are not supported.");
      } else {
         long var2;
         return (int)((var2 = Double.doubleToLongBits(var0)) ^ var2 >>> 32);
      }
   }

   public static int a(float var0) {
      if (!a && Float.isNaN(var0)) {
         throw new AssertionError("Values of NaN are not supported.");
      } else {
         return Float.floatToIntBits(var0 * 6.6360896E8F);
      }
   }

   public static int a(int var0) {
      return var0;
   }

   public static int a(long var0) {
      return (int)(var0 ^ var0 >>> 32);
   }

   public static int a(Object var0) {
      return var0 == null ? 0 : var0.hashCode();
   }

   public static int b(float var0) {
      int var1 = (int)var0;
      if (var0 - (float)var1 > 0.0F) {
         ++var1;
      }

      return var1;
   }
}

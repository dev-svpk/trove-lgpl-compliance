package solver;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public final class ScriptConfig {
   String[] a;
   List b;
   File c;
   SaveOptionsPanel d;
   int e = 0;
   int f = 1;
   int g = 30;
   int h = 15;
   int i = -1;
   boolean j = false;
   File k;
   private String l;

   public ScriptConfig(String var1, File var2, SaveOptionsPanel var3, String var4, String[] var5, List var6, int var7, int var8) throws Throwable {
      if (SaveDirectoryListener.b(var2) > 0) {
         this.a = var5;
      } else {
         this.a = new String[]{solver.HashUtil.a(new char[0])};
      }

      this.g = var7;
      this.h = var8;
      this.d = var3;
      this.b = var6;
      this.k = var2;
      this.l = var1;
      this.c = new File(var4);
      if (!this.c.exists()) {
         this.c = new File(FlopNE.c(), "script");

         for(int var10 = 2; this.c.exists(); ++var10) {
            this.c = new File(FlopNE.c(), "script" + var10);
         }
      }

      if (this.a.length == 1) {
         this.f = 0;
      }

   }

   public final boolean a() {
      return this.c() < this.b();
   }

   public static final String a(int[] var0) {
      if (var0 == null) {
         return solver.HashUtil.x(new char[0]);
      } else {
         for(int var1 = 1; var1 < var0.length; ++var1) {
            if (var0[var1] != var0[var1 - 1]) {
               return Arrays.toString(var0);
            }
         }

         return solver.HashUtil.I(new char[0]) + var0[0];
      }
   }

   public final int b() {
      return this.a.length * this.g();
   }

   public final int c() {
      return this.a.length == 1 ? this.f : (this.f - 1) * this.a.length + this.e;
   }

   public final String d() {
      return this.a[this.e - 1];
   }

   public final int[] e() {
      return this.b == null ? null : (int[])this.b.get(this.f - 1);
   }

   private int g() {
      return this.b == null ? 1 : this.b.size();
   }

   public final void f() {
      Thread var1 = FlopNE.a(new File(this.c, this.l + "" + this.d() + a(this.e()) + ".mkr"), this.d.a());

      try {
         var1.join();
      } catch (InterruptedException var2) {
         var2.printStackTrace();
      }
   }
}

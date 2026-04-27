package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.b_ref.b;
import gnu.trove.e_ref.ai;

final class THashMap$HashProcedure implements ai {
   private int h;
   // $FF: synthetic field
   final THashMap this$0;

   private THashMap$HashProcedure(THashMap var1) {
      this.this$0 = var1;
      this.h = 0;
   }

   public final int getHashCode() {
      return this.h;
   }

   public final boolean execute(Object var1, Object var2) {
      this.h += b.a(var1) ^ (var2 == null ? 0 : var2.hashCode());
      return true;
   }

   // $FF: synthetic method
   THashMap$HashProcedure(THashMap var1, THashMap$HashProcedure var2) {
      this(var1);
   }
}

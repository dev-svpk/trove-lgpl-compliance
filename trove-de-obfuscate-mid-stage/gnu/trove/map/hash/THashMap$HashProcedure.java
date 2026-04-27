package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.impl.HashFunctions;
import gnu.trove.procedure.TObjectObjectProcedure;

final class THashMap$HashProcedure implements TObjectObjectProcedure {
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
      this.h += HashFunctions.hash(var1) ^ (var2 == null ? 0 : var2.hashCode());
      return true;
   }

   // $FF: synthetic method
   THashMap$HashProcedure(THashMap var1, THashMap$HashProcedure var2) {
      this(var1);
   }
}

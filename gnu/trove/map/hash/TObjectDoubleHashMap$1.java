package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TObjectDoubleProcedure;

class TObjectDoubleHashMap$1 implements TObjectDoubleProcedure {
   // $FF: synthetic field
   final TObjectDoubleHashMap this$0;

   TObjectDoubleHashMap$1(TObjectDoubleHashMap var1) {
      this.this$0 = var1;
   }

   public boolean execute(Object var1, double var2) {
      this.this$0.put(var1, var2);
      return true;
   }
}

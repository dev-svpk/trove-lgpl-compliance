package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TObjectLongProcedure;

class TObjectLongHashMap$1 implements TObjectLongProcedure {
   // $FF: synthetic field
   final TObjectLongHashMap this$0;

   TObjectLongHashMap$1(TObjectLongHashMap var1) {
      this.this$0 = var1;
   }

   public boolean execute(Object var1, long var2) {
      this.this$0.put(var1, var2);
      return true;
   }
}

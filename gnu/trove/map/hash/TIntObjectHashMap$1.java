package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TIntObjectProcedure;

class TIntObjectHashMap$1 implements TIntObjectProcedure {
   // $FF: synthetic field
   final TIntObjectHashMap this$0;

   TIntObjectHashMap$1(TIntObjectHashMap var1) {
      this.this$0 = var1;
   }

   public boolean execute(int var1, Object var2) {
      this.this$0.put(var1, var2);
      return true;
   }
}

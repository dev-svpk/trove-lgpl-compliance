package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TDoubleObjectProcedure;

class TDoubleObjectHashMap$1 implements TDoubleObjectProcedure {
   // $FF: synthetic field
   final TDoubleObjectHashMap this$0;

   TDoubleObjectHashMap$1(TDoubleObjectHashMap var1) {
      this.this$0 = var1;
   }

   public boolean execute(double var1, Object var3) {
      this.this$0.put(var1, var3);
      return true;
   }
}

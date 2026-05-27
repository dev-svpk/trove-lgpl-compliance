package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TCharObjectProcedure;

class TCharObjectHashMap$1 implements TCharObjectProcedure {
   // $FF: synthetic field
   final TCharObjectHashMap this$0;

   TCharObjectHashMap$1(TCharObjectHashMap var1) {
      this.this$0 = var1;
   }

   public boolean execute(char var1, Object var2) {
      this.this$0.put(var1, var2);
      return true;
   }
}

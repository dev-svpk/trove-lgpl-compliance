package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TObjectByteProcedure;

class TObjectByteHashMap$1 implements TObjectByteProcedure {
   // $FF: synthetic field
   final TObjectByteHashMap this$0;

   TObjectByteHashMap$1(TObjectByteHashMap var1) {
      this.this$0 = var1;
   }

   public boolean execute(Object var1, byte var2) {
      this.this$0.put(var1, var2);
      return true;
   }
}

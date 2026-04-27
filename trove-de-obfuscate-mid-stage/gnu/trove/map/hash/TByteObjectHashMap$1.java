package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TByteObjectProcedure;

class TByteObjectHashMap$1 implements TByteObjectProcedure {
   // $FF: synthetic field
   final TByteObjectHashMap this$0;

   TByteObjectHashMap$1(TByteObjectHashMap var1) {
      this.this$0 = var1;
   }

   public boolean execute(byte var1, Object var2) {
      this.this$0.put(var1, var2);
      return true;
   }
}

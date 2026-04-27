package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TLongObjectProcedure;

class TLongObjectHashMap$1 implements TLongObjectProcedure {
   // $FF: synthetic field
   final TLongObjectHashMap this$0;

   TLongObjectHashMap$1(TLongObjectHashMap var1) {
      this.this$0 = var1;
   }

   public boolean execute(long var1, Object var3) {
      this.this$0.put(var1, var3);
      return true;
   }
}
